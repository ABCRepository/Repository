package org.abcd.sysmlevolver.evolution.updater.euml;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class EUMLUtil {
	private static EUMLUtil instance = null;
	// 1. Transition Evolution
	public EState targetStateOnCall = null;
	public EState sourceStateOnCall = null;
	public ETransition evolveTransitionOnCall = null;

	public String parentStateIdOnCall = null;
	public String stateMachineIdOnCall = null;
	public String classType = null;

	// 2. change event trigger state evolution

	public EState targetStateOnChange = null;
	public EState sourceStateOnChange = null;
	public ETransition evolveTransitionOnChange = null;
	public ETransition dummyTransitionOnchange = null;
	public String parentStateIdOnChange = null;
	public String stateMachineIdOnChange = null;
	// 2,1 new region inside evolved state
	public String regionIdOnChange = null;
	// 2.2 initial state and idle state evolution
	public EState initialStateOnChange = null;
	public EState idleStateOnChange = null;
	// 2.3 transition between initial and idle state
	public ETransition evolveTransitionOfInitAndIdleOnChange = null;

	private EModel model = null;

	public static synchronized EUMLUtil instance() {
		if (instance == null) {
			instance = new EUMLUtil();
		}
		return instance;
	}

	public Document getDocument(String file) {
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}

	public void xmlWriters(String file, Document document, OutputFormat format) {
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void generateTransitionAndStateOnCall(String file, String parentName, String currentStateName,
			String methodName, String guardConstraint, String targetStateName, String targetStateConstraint) {
		file += ".uml";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		String callEventId = null;
		String operationId = null;
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Model")) {
				List<Element> elements = element.elements();
				for (Element ele : elements) {
					if (ele.attributeValue("type").equals("uml:Class")) {
						List<Element> eleOPerations = ele.elements("ownedOperation");
						for (Element el : eleOPerations) {
							if (el.attributeValue("name").equals(methodName)) {
								operationId = el.attributeValue("id");
							}
						}
						// Behaviors
						List<Element> eleBehaviors = ele.elements("ownedBehavior");
						for (Element el : eleBehaviors) {
							// : state machine
							if (el.attributeValue("type").equals("uml:StateMachine")) {
								stateMachineIdOnCall = el.attributeValue("id");
								Element region = el.element("region");
								// top level
								List<Element> topLevelSubvertexs = region.elements("subvertex");
								// 1. read:
								for (Element subvertex : topLevelSubvertexs) {
									Element subregion = subvertex.element("region");
									// low level
									if (subregion != null) {
										if (subregion.attributeValue("name").equals(parentName)) {
											parentStateIdOnCall = subregion.attributeValue("id");
											// low level states
											List<Element> lowLevelSubvertexs = subregion.elements("subvertex");
											for (Element lowState : lowLevelSubvertexs) {
												if (lowState.attributeValue("name").equals(currentStateName)) {
													sourceStateOnCall = new EState();
													sourceStateOnCall.setName(lowState.attributeValue("name"));
													sourceStateOnCall.setPid(lowState.attributeValue("id"));
												} //
													// if
													// (lowState.attributeValue("name").equals(targetStateName))
													// {
													// tergatStateOnCall = new
													// EState();
													// tergatStateOnCall.setName(lowState.attributeValue("name"));
													// tergatStateOnCall.setPid(lowState.attributeValue("id"));
													// }
											}
											// new target State
											Element newState = subregion.addElement("subvertex");
											newState.addAttribute("xmi:type", "uml:State");
											String newTargetStateId = RandomKeyUtil.getPid();
											newState.addAttribute("xmi:id", newTargetStateId);
											newState.addAttribute("name", targetStateName);
											String stateInvariantId = RandomKeyUtil.getPid();
											newState.addAttribute("stateInvariant", stateInvariantId);
											Element ownedRule = newState.addElement("ownedRule");
											ownedRule.addAttribute("xmi:type", "uml:Constraint");
											ownedRule.addAttribute("xmi:id", stateInvariantId);
											Element specification = ownedRule.addElement("specification");
											specification.addAttribute("xmi:type", "uml:LiteralString");
											specification.addAttribute("xmi:id", RandomKeyUtil.getPid());
											specification.addAttribute("value", targetStateConstraint);

											targetStateOnCall = new EState();
											targetStateOnCall.setPid(newTargetStateId);
											targetStateOnCall.setName(targetStateName);

											// low level transition
											evolveTransitionOnCall = new ETransition();
											evolveTransitionOnCall.setPid(RandomKeyUtil.getPid());
											String guard = RandomKeyUtil.getPid();
											evolveTransitionOnCall.setGuard(guard);
											evolveTransitionOnCall.setSourceId(sourceStateOnCall.getPid());
											evolveTransitionOnCall.setTargetId(targetStateOnCall.getPid());
											EOwnedRule ownedRuleOfTrans = new EOwnedRule();
											ownedRuleOfTrans.setPid(guard);
											ESpecification specificationOfTrans = new ESpecification();
											specificationOfTrans.setPid(RandomKeyUtil.getPid());
											specificationOfTrans.setValue(guardConstraint);
											ownedRuleOfTrans.setSpecification(specificationOfTrans);
											evolveTransitionOnCall.setOwnedRule(ownedRuleOfTrans);
											ETrigger trigger = new ETrigger();
											trigger.setPid(RandomKeyUtil.getPid());
											// trigger.setEvent(callEventId);
											evolveTransitionOnCall.setTrigger(trigger);
										}
									}
								}
							} //
						}
					}
					if (ele.attributeValue("type").equals("uml:CallEvent")) {
						if (ele.attributeValue("operation") != null) {
							if (ele.attributeValue("operation").equals(operationId)) {
								callEventId = ele.attributeValue("id");
							}
						}
					}
				}
			}
		}
		generateTransitionOnCall(root, parentName, callEventId);
		xmlWriters(file, document, setOutputFormat(false));
	}

	public void updateTransitionOnCall(String file, String parentName, String currentStateName, String methodName,
			String constraint, String targetStateName) {
		file += ".uml";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		String callEventId = null;
		String operationId = null;
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Model")) {
				List<Element> elements = element.elements();
				for (Element ele : elements) {
					if (ele.attributeValue("type").equals("uml:Class")) {
						List<Element> eleOPerations = ele.elements("ownedOperation");
						for (Element el : eleOPerations) {
							if (el.attributeValue("name").equals(methodName)) {
								operationId = el.attributeValue("id");
							}
						}
						List<Element> eleBehaviors = ele.elements("ownedBehavior");
						for (Element el : eleBehaviors) {
							if (el.attributeValue("type").equals("uml:StateMachine")) {
								stateMachineIdOnCall = el.attributeValue("id");
								Element region = el.element("region");
								List<Element> topLevelSubvertexs = region.elements("subvertex");
								for (Element subvertex : topLevelSubvertexs) {
									Element subregion = subvertex.element("region");
									if (subregion != null) {
										if (subregion.attributeValue("name").equals(parentName)) {
											parentStateIdOnCall = subregion.attributeValue("id");
											List<Element> lowLevelSubvertexs = subregion.elements("subvertex");
											for (Element lowState : lowLevelSubvertexs) {
												if (lowState.attributeValue("name").equals(currentStateName)) {
													sourceStateOnCall = new EState();
													sourceStateOnCall.setName(lowState.attributeValue("name"));
													sourceStateOnCall.setPid(lowState.attributeValue("id"));
												} //
												if (lowState.attributeValue("name").equals(targetStateName)) {
													targetStateOnCall = new EState();
													targetStateOnCall.setName(lowState.attributeValue("name"));
													targetStateOnCall.setPid(lowState.attributeValue("id"));
												}
											}
											evolveTransitionOnCall = new ETransition();
											evolveTransitionOnCall.setPid(RandomKeyUtil.getPid());
											String guard = RandomKeyUtil.getPid();
											evolveTransitionOnCall.setGuard(guard);
											evolveTransitionOnCall.setSourceId(sourceStateOnCall.getPid());
											evolveTransitionOnCall.setTargetId(targetStateOnCall.getPid());
											EOwnedRule ownedRule = new EOwnedRule();
											ownedRule.setPid(guard);
											ESpecification specification = new ESpecification();
											specification.setPid(RandomKeyUtil.getPid());
											specification.setValue(constraint);
											ownedRule.setSpecification(specification);
											evolveTransitionOnCall.setOwnedRule(ownedRule);
											ETrigger trigger = new ETrigger();
											trigger.setPid(RandomKeyUtil.getPid());
											// trigger.setEvent(callEventId);
											evolveTransitionOnCall.setTrigger(trigger);
										}
									}
								}
							} //
						}
					}
					if (ele.attributeValue("type").equals("uml:CallEvent")) {
						if (ele.attributeValue("operation") != null) {
							if (ele.attributeValue("operation").equals(operationId)) {
								callEventId = ele.attributeValue("id");
							}
						}
					}
				}
			}
		}
		generateTransitionOnCall(root, parentName, callEventId);
		xmlWriters(file, document, setOutputFormat(false));
	}

	private void generateTransitionOnCall(Element root, String parentName, String callEventId) {
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Model")) {
				List<Element> elements = element.elements();
				for (Element ele : elements) {
					List<Element> eleBehaviors1 = ele.elements("ownedBehavior");
					for (Element el : eleBehaviors1) {
						if (el.attributeValue("type").equals("uml:StateMachine")) {
							stateMachineIdOnChange = el.attributeValue("id");
							Element region1 = el.element("region");
							List<Element> topLevelSubvertexs1 = region1.elements("subvertex");
							for (Element subvertex : topLevelSubvertexs1) {
								Element subregion = subvertex.element("region");
								if (subregion != null) {
									if (subregion.attributeValue("name").equals(parentName)) {
										boolean flag = true;
										List<Element> trans = subregion.elements("transition");
										for (Element tran : trans) {
											if (tran.attributeValue("id").equals(evolveTransitionOnCall.getPid())) {
												flag = false;
												break;
											}
										}
										if (flag == true) {
											Element newTransition = subregion.addElement("transition");
											newTransition.addAttribute("xmi:type", evolveTransitionOnCall.getType());
											newTransition.addAttribute("xmi:id", evolveTransitionOnCall.getPid());
											newTransition.addAttribute("guard", evolveTransitionOnCall.getGuard());
											newTransition.addAttribute("source", evolveTransitionOnCall.getSourceId());
											newTransition.addAttribute("target", evolveTransitionOnCall.getTargetId());
											Element ownedRule = newTransition.addElement("ownedRule");
											ownedRule.addAttribute("xmi:type",
													evolveTransitionOnCall.getOwnedRule().getType());
											ownedRule.addAttribute("xmi:id",
													evolveTransitionOnCall.getOwnedRule().getPid());
											Element specification = ownedRule.addElement("specification");
											specification.addAttribute("xmi:type",
													evolveTransitionOnCall.getOwnedRule().getSpecification().getType());
											specification.addAttribute("xmi:id",
													evolveTransitionOnCall.getOwnedRule().getSpecification().getPid());
											specification.addAttribute("value", evolveTransitionOnCall.getOwnedRule()
													.getSpecification().getValue());
											Element trigger = newTransition.addElement("trigger");
											trigger.addAttribute("xmi:type",
													evolveTransitionOnCall.getTrigger().getType());
											trigger.addAttribute("xmi:id",
													evolveTransitionOnCall.getTrigger().getPid());
											trigger.addAttribute("event", callEventId);
										}
									}
								}
							}
						}

					}
				}
			}
		}

	}

	public void generateTransitionAndStateOnChange(String file, String parentName, String currentStateName,
			String newStargetStateName, String constraint, String changeExpressionName, String initStateName,
			String idleStateName, String targetStateRegionName) {
		file += "model";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Model")) {
				List<Element> elements = element.elements();
				// EClass eClass = null;
				for (Element ele : elements) {
					// Behaviors
					List<Element> eleBehaviors1 = ele.elements("ownedBehavior");
					for (Element el : eleBehaviors1) {
						// : state machine
						if (el.attributeValue("type").equals("uml:StateMachine")) {
							stateMachineIdOnChange = el.attributeValue("id");
							Element region1 = el.element("region");
							if (region1.attributeValue("name").equals(parentName)) {
								// current State
								List<Element> highLevelSubvertexs = region1.elements("subvertex");
								for (Element highState : highLevelSubvertexs) {
									if (highState.attributeValue("name").equals(currentStateName)) {
										sourceStateOnChange = new EState();
										sourceStateOnChange.setName(highState.attributeValue("name"));
										sourceStateOnChange.setPid(highState.attributeValue("id"));
									} //
								}
								Element newState = region1.addElement("subvertex");
								newState.addAttribute("xmi:type", "uml:State");
								String newTargetStateId = RandomKeyUtil.getPid();
								newState.addAttribute("xmi:id", newTargetStateId);
								newState.addAttribute("name", newStargetStateName);
								String stateInvariantId = RandomKeyUtil.getPid();
								newState.addAttribute("stateInvariant", stateInvariantId);
								Element ownedRule = newState.addElement("ownedRule");
								ownedRule.addAttribute("xmi:type", "uml:Constraint");
								ownedRule.addAttribute("xmi:id", stateInvariantId);
								Element specification = ownedRule.addElement("specification");
								specification.addAttribute("xmi:type", "uml:LiteralString");
								specification.addAttribute("xmi:id", RandomKeyUtil.getPid());
								specification.addAttribute("value", constraint);

								targetStateOnChange = new EState();
								targetStateOnChange.setPid(newTargetStateId);
								targetStateOnChange.setName(newStargetStateName);

								Element targetStateRegion = newState.addElement("region");
								targetStateRegion.addAttribute("xmi:type", "uml:Region");
								String newtargetStateRegionId = RandomKeyUtil.getPid();
								regionIdOnChange = newtargetStateRegionId;
								targetStateRegion.addAttribute("xmi:id", newtargetStateRegionId);
								String newtargetStateRegionName = "Region" + newtargetStateRegionId;
								targetStateRegion.addAttribute("name", targetStateRegionName);

								Element initialStateInsideTargetState = targetStateRegion.addElement("subvertex");
								initialStateInsideTargetState.addAttribute("xmi:type", "uml:Pseudostate");
								String initialStateInsideTargetStateId = RandomKeyUtil.getPid();
								initialStateInsideTargetState.addAttribute("xmi:id", initialStateInsideTargetStateId);
								initialStateInsideTargetState.addAttribute("name", initStateName);

								initialStateOnChange = new EState();
								initialStateOnChange.setPid(initialStateInsideTargetStateId);
								initialStateOnChange.setName(initStateName);

								Element idleStateInsideTargetState = targetStateRegion.addElement("subvertex");
								idleStateInsideTargetState.addAttribute("xmi:type", "uml:State");
								String idleStateInsideTargetStateId = RandomKeyUtil.getPid();
								idleStateInsideTargetState.addAttribute("xmi:id", idleStateInsideTargetStateId);
								idleStateInsideTargetState.addAttribute("name", idleStateName);

								idleStateOnChange = new EState();
								idleStateOnChange.setPid(idleStateInsideTargetStateId);
								idleStateOnChange.setName(idleStateName);

								// generate a transition between initial and
								// idle state
								Element tranBetweenInitAndIdle = targetStateRegion.addElement("transition");
								tranBetweenInitAndIdle.addAttribute("xmi:type", "uml:Transition");
								String tranBetweenInitAndIdleId = RandomKeyUtil.getPid();
								tranBetweenInitAndIdle.addAttribute("xmi:id", tranBetweenInitAndIdleId);
								tranBetweenInitAndIdle.addAttribute("source", initialStateOnChange.getPid());
								tranBetweenInitAndIdle.addAttribute("target", idleStateOnChange.getPid());

								evolveTransitionOfInitAndIdleOnChange = new ETransition();
								evolveTransitionOfInitAndIdleOnChange.setPid(tranBetweenInitAndIdleId);
								evolveTransitionOfInitAndIdleOnChange.setSourceId(initialStateOnChange.getPid());
								evolveTransitionOfInitAndIdleOnChange.setTargetId(initialStateOnChange.getPid());

							}
						}
					}
				}
			}
		}
		generateTransitionOnChangeofGenerateTransitionAndStateOnChang(root, parentName, changeExpressionName);
		xmlWriters(file, document, setOutputFormat(false));
	}

	public void generateTransitionOnChangeofGenerateTransitionAndStateOnChang(Element root, String parentName,
			String changeExpressionName) {
		String changeEventId = null;
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Model")) {
				Element newChangeEvent = element.addElement("packagedElement");
				newChangeEvent.addAttribute("xmi:type", "uml:ChangeEvent");
				changeEventId = RandomKeyUtil.getPid();
				newChangeEvent.addAttribute("xmi:id", changeEventId);
				Element changeExpression = newChangeEvent.addElement("changeExpression");
				changeExpression.addAttribute("xmi:type", "uml:LiteralString");
				changeExpression.addAttribute("xmi:id", RandomKeyUtil.getPid());
				changeExpression.addAttribute("value", changeExpressionName);

				List<Element> elements = element.elements();
				for (Element ele : elements) {
					List<Element> eleBehaviors1 = ele.elements("ownedBehavior");
					for (Element el : eleBehaviors1) {
						if (el.attributeValue("type").equals("uml:StateMachine")) {
							Element region1 = el.element("region");
							if (region1.attributeValue("name").equals(parentName)) {
								Element newTransition = region1.addElement("transition");
								newTransition.addAttribute("xmi:type", "uml:Transition");
								String newTransitionId = RandomKeyUtil.getPid();
								newTransition.addAttribute("xmi:id", newTransitionId);
								newTransition.addAttribute("source", sourceStateOnChange.getPid());
								newTransition.addAttribute("target", targetStateOnChange.getPid());
								Element trigger1 = newTransition.addElement("trigger");
								trigger1.addAttribute("xmi:type", "uml:Trigger");
								trigger1.addAttribute("xmi:id", RandomKeyUtil.getPid());
								trigger1.addAttribute("event", changeEventId);

								evolveTransitionOnChange = new ETransition();
								evolveTransitionOnChange.setPid(newTransitionId);
								evolveTransitionOnChange.setSourceId(sourceStateOnChange.getPid());
								evolveTransitionOnChange.setTargetId(targetStateOnChange.getPid());

								Element dummyTransition = region1.addElement("transition");
								dummyTransition.addAttribute("xmi:type", "uml:Transition");
								String dummyTransitionId = RandomKeyUtil.getPid();
								dummyTransition.addAttribute("xmi:id", dummyTransitionId);
								dummyTransition.addAttribute("source", targetStateOnChange.getPid());
								dummyTransition.addAttribute("target", sourceStateOnChange.getPid());
								dummyTransitionOnchange = new ETransition();
								dummyTransitionOnchange.setPid(dummyTransitionId);
								dummyTransitionOnchange.setSourceId(targetStateOnChange.getPid());
								dummyTransitionOnchange.setTargetId(sourceStateOnChange.getPid());

							}
						}
					}
				}
			}
		}
	}

	public void generateTransitionOnChange(String file, String parentName, String currentStateName,
			String targetStateName, String changeExpressionName) {
		file += ".uml";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Model")) {
				List<Element> elements = element.elements();
				for (Element ele : elements) {
					List<Element> eleBehaviors1 = ele.elements("ownedBehavior");
					for (Element el : eleBehaviors1) {
						if (el.attributeValue("type").equals("uml:StateMachine")) {
							stateMachineIdOnChange = el.attributeValue("id");
							Element region1 = el.element("region");
							if (region1.attributeValue("name").equals(parentName)) {
								List<Element> highLevelSubvertexs = region1.elements("subvertex");
								for (Element highState : highLevelSubvertexs) {
									if (highState.attributeValue("name").equals(currentStateName)) {
										sourceStateOnChange = new EState();
										sourceStateOnChange.setName(highState.attributeValue("name"));
										sourceStateOnChange.setPid(highState.attributeValue("id"));
									} //
									if (highState.attributeValue("name").equals(targetStateName)) {
										targetStateOnChange = new EState();
										targetStateOnChange.setName(highState.attributeValue("name"));
										targetStateOnChange.setPid(highState.attributeValue("id"));
									} //
								}
							}
						}
					}
				}
			}
		}
		generateTransitionOnChangeofGenerateTransitionOnChange(root, parentName, changeExpressionName);
		xmlWriters(file, document, setOutputFormat(false));
	}

	public void generateTransitionOnChangeofGenerateTransitionOnChange(Element root, String parentName,
			String changeExpressionName) {
		String changeEventId = null;
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Model")) {
				Element newChangeEvent = element.addElement("packagedElement");
				newChangeEvent.addAttribute("xmi:type", "uml:ChangeEvent");
				changeEventId = RandomKeyUtil.getPid();
				newChangeEvent.addAttribute("xmi:id", changeEventId);
				Element changeExpression = newChangeEvent.addElement("changeExpression");
				changeExpression.addAttribute("xmi:type", "uml:LiteralString");
				changeExpression.addAttribute("xmi:id", RandomKeyUtil.getPid());
				changeExpression.addAttribute("value", changeExpressionName);

				List<Element> elements = element.elements();
				for (Element ele : elements) {
					List<Element> eleBehaviors1 = ele.elements("ownedBehavior");
					for (Element el : eleBehaviors1) {
						if (el.attributeValue("type").equals("uml:StateMachine")) {
							Element region1 = el.element("region");
							if (region1.attributeValue("name").equals(parentName)) {
								Element newTransition = region1.addElement("transition");
								newTransition.addAttribute("xmi:type", "uml:Transition");
								String newTransitionId = RandomKeyUtil.getPid();
								newTransition.addAttribute("xmi:id", newTransitionId);
								newTransition.addAttribute("source", sourceStateOnChange.getPid());
								newTransition.addAttribute("target", targetStateOnChange.getPid());
								Element trigger1 = newTransition.addElement("trigger");
								trigger1.addAttribute("xmi:type", "uml:Trigger");
								trigger1.addAttribute("xmi:id", RandomKeyUtil.getPid());
								trigger1.addAttribute("event", changeEventId);

								evolveTransitionOnChange = new ETransition();
								evolveTransitionOnChange.setPid(newTransitionId);
								evolveTransitionOnChange.setSourceId(sourceStateOnChange.getPid());
								evolveTransitionOnChange.setTargetId(targetStateOnChange.getPid());


							}
						}
					}
				}
			}
		}
	}

	public OutputFormat setOutputFormat(boolean flag) {
		OutputFormat format = null;
		if (flag == true) {// default format
			format = OutputFormat.createPrettyPrint();
			return format;
		} //
		format = new OutputFormat();
		format.setIndentSize(2);// line indent
		format.setNewlines(true);// a node is a row
		format.setTrimText(true);// deduplicate spaces
		format.setPadText(true);
		format.setNewLineAfterDeclaration(false);// put a blank line in the
													// second line of the xml
													// file
		format.setEncoding("UTF-8");
		return format;
	}

}

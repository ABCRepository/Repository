package org.abcd.sysmlevolver.evolution.updater.enotation;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import org.abcd.sysmlevolver.evolution.updater.euml.RandomKeyUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ENotationUtil {
	private static ENotationUtil instance = null;
	private String sourceStateIdOnCall = null;
	private String targetStateIdOnCall = null;

	private String sourceStateIdOnChange = null;
	private String targetStateIdOnChange = null;
	private String initialStateIdOnChange = null;
	private String idleStateIdOnChange = null;

	public static synchronized ENotationUtil instance() {
		if (instance == null) {
			instance = new ENotationUtil();
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

	public void updateTransitionNotationOnCall(String file, String transitionId, String parentStateId,
			String currentStateId, String targetStateId, String stateMachineId, String classType) {
		file += ".notation";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Diagram")) {
				if (element.attributeValue("type").equals("PapyrusUMLStateMachineDiagram")) {
					List<Element> childs1 = element.elements("children");
					for (Element child1 : childs1) {
						// System.out.println("id---child1--:" +
						// child1.attributeValue("id"));
						Element elementNode1 = child1.element("element");
						// to a specific State Machine
						if (elementNode1.attributeValue("href")
								.equals(RandomKeyUtil.getHref(classType, stateMachineId))) {
							List<Element> childs2 = child1.elements("children");
							for (Element child2 : childs2) {
								// System.out.println("id---child2--:" +
								// child2.attributeValue("id"));
								List<Element> childs3 = child2.elements("children");
								if (childs3 == null)
									continue;
								for (Element child3 : childs3) {
									// System.out.println("id---child3--:" +
									// child3.attributeValue("id"));
									Element elementNode3 = child3.element("element");
									if (elementNode3 != null) {
										List<Element> childs4 = child3.elements("children");
										if (childs4 == null)
											continue;
										for (Element child4 : childs4) {
											// System.out.println("id---child4--:"
											// + child4.attributeValue("id"));
											// sysMLEvolver state
											List<Element> childs5 = child4.elements("children");
											if (childs5 == null)
												continue;
											for (Element child5 : childs5) {
												// System.out.println("id---child5--:"
												// +
												// child5.attributeValue("id"));
												List<Element> childs6 = child5.elements("children");
												if (childs6 == null)
													continue;
												for (Element child6 : childs6) {
													// Region
													List<Element> childs7 = child6.elements("children");
													if (childs7 == null)
														continue;
													for (Element child7 : childs7) {
														Element elementNode7 = child7.element("element");
														if (elementNode7.attributeValue("href").equals(
																RandomKeyUtil.getHref(classType, parentStateId))) {
															List<Element> childs8 = child7.elements("children");
															if (childs8 == null)
																continue;
															for (Element child8 : childs8) {
																List<Element> childs9 = child8.elements("children");
																if (childs9 == null)
																	continue;
																for (Element child9 : childs9) {
																	Element elementNode9 = child9.element("element");
																	if (elementNode9.attributeValue("href")
																			.equals(RandomKeyUtil.getHref(classType,
																					currentStateId))) {
																		sourceStateIdOnCall = child9
																				.attributeValue("id");
																	} //
																	if (elementNode9.attributeValue("href")
																			.equals(RandomKeyUtil.getHref(classType,
																					targetStateId))) {
																		targetStateIdOnCall = child9
																				.attributeValue("id");
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
							}
						}
					}

					Element newEdges = element.addElement("edges");
					newEdges.addAttribute("xmi:type", "notation:Connector");
					newEdges.addAttribute("xmi:id", RandomKeyUtil.getPid());
					newEdges.addAttribute("type", "7000");
					newEdges.addAttribute("source", sourceStateIdOnCall);
					newEdges.addAttribute("target", targetStateIdOnCall);
					// 1
					Element children1 = newEdges.addElement("children");
					children1.addAttribute("xmi:type", "notation:DecorationNode");
					children1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children1.addAttribute("type", "7001");
					Element layoutConstraint1 = children1.addElement("layoutConstraint");
					layoutConstraint1.addAttribute("xmi:type", "notation:Location");
					layoutConstraint1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					// 2
					Element children2 = newEdges.addElement("children");
					children2.addAttribute("xmi:type", "notation:DecorationNode");
					children2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children2.addAttribute("type", "7002");
					Element layoutConstraint2 = children2.addElement("layoutConstraint");
					layoutConstraint2.addAttribute("xmi:type", "notation:Location");
					layoutConstraint2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint2.addAttribute("x", "3");
					layoutConstraint2.addAttribute("y", "23");
					// 3
					Element children3 = newEdges.addElement("children");
					children3.addAttribute("xmi:type", "notation:DecorationNode");
					children3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children3.addAttribute("type", "7003");
					Element layoutConstraint3 = children3.addElement("layoutConstraint");
					layoutConstraint3.addAttribute("xmi:type", "notation:Location");
					layoutConstraint3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint3.addAttribute("y", "60");

					Element styles = newEdges.addElement("styles");
					styles.addAttribute("xmi:type", "notation:FontStyle");
					styles.addAttribute("xmi:id", RandomKeyUtil.getPid());

					Element elementNode = newEdges.addElement("element");
					elementNode.addAttribute("xmi:type", "uml:Transition");
					elementNode.addAttribute("href", RandomKeyUtil.getHref(classType, transitionId));

					Element bendpoints = newEdges.addElement("bendpoints");
					bendpoints.addAttribute("xmi:type", "notation:RelativeBendpoints");
					bendpoints.addAttribute("xmi:id", RandomKeyUtil.getPid());
					bendpoints.addAttribute("points", "[]");

					Element sourceAnchor = newEdges.addElement("sourceAnchor");
					sourceAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					sourceAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					sourceAnchor.addAttribute("id", "(1.0," + Math.random() + ")");

					Element targetAnchor = newEdges.addElement("sourceAnchor");
					targetAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					targetAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					targetAnchor.addAttribute("id", "(0.0," + Math.random() + ")");
				}
			}
		}
		xmlWriters(file, document, setOutputFormat(false));
	}

	public OutputFormat setOutputFormat(boolean flag) {
		OutputFormat format = null;
		if (flag == true) {// default format
			format = OutputFormat.createPrettyPrint();
			return format;
		} //
		format = new OutputFormat();
		format.setIndentSize(3);// line indent
		format.setNewlines(true);// a node is a row
		format.setTrimText(true);// deduplicate spaces
		format.setPadText(true);
		format.setNewLineAfterDeclaration(false);// put a blank line in the
													// second line of the xml
													// file
		format.setEncoding("UTF-8");
		return format;
	}

	public String getSourceStateIdOnCall() {
		return sourceStateIdOnCall;
	}

	public void setSourceStateIdOnCall(String sourceStateIdOnCall) {
		this.sourceStateIdOnCall = sourceStateIdOnCall;
	}

	public String getTargetStateIdOnCall() {
		return targetStateIdOnCall;
	}

	public void setTargetStateIdOnCall(String targetStateIdOnCall) {
		this.targetStateIdOnCall = targetStateIdOnCall;
	}

	public String getSourceStateIdOnChange() {
		return sourceStateIdOnChange;
	}

	public void setSourceStateIdOnChange(String sourceStateIdOnChange) {
		this.sourceStateIdOnChange = sourceStateIdOnChange;
	}

	public String getTargetStateIdOnChange() {
		return targetStateIdOnChange;
	}

	public void setTargetStateIdOnChange(String targetStateIdOnChange) {
		this.targetStateIdOnChange = targetStateIdOnChange;
	}

	public String getInitialStateIdOnChange() {
		return initialStateIdOnChange;
	}

	public void setInitialStateIdOnChange(String initialStateIdOnChange) {
		this.initialStateIdOnChange = initialStateIdOnChange;
	}

	public String getIdleStateIdOnChange() {
		return idleStateIdOnChange;
	}

	public void setIdleStateIdOnChange(String idleStateIdOnChange) {
		this.idleStateIdOnChange = idleStateIdOnChange;
	}

	public void updateStateAndTransitionNotationOnCall(String file, String parentStateId, String transitionId,
			String currentStateId, String newtargetStateId, String stateMachineId, String classType) {
		file += ".notation";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Diagram")) {
				if (element.attributeValue("type").equals("PapyrusUMLStateMachineDiagram")) {
					List<Element> childs1 = element.elements("children");
					for (Element child1 : childs1) {
						Element elementNode1 = child1.element("element");
						// to a specific State Machine
						if (elementNode1.attributeValue("href")
								.equals(RandomKeyUtil.getHref(classType, stateMachineId))) {
							List<Element> childs2 = child1.elements("children");
							for (Element child2 : childs2) {
								List<Element> childs3 = child2.elements("children");
								if (childs3 == null)
									continue;
								for (Element child3 : childs3) {
									Element elementNode3 = child3.element("element");
									if (elementNode3 != null) {
										List<Element> childs4 = child3.elements("children");
										if (childs4 == null)
											continue;
										for (Element child4 : childs4) {
											List<Element> childs5 = child4.elements("children");
											if (childs5 == null)
												continue;
											for (Element child5 : childs5) {
												List<Element> childs6 = child5.elements("children");
												if (childs6 == null)
													continue;
												for (Element child6 : childs6) {
													List<Element> childs7 = child6.elements("children");
													if (childs7 == null)
														continue;
													for (Element child7 : childs7) {
														Element elementNode7 = child7.element("element");
														if (elementNode7.attributeValue("href").equals(
																RandomKeyUtil.getHref(classType, parentStateId))) {
															List<Element> childs8 = child7.elements("children");
															if (childs8 == null)
																continue;
															for (Element child8 : childs8) {
																List<Element> childs9 = child8.elements("children");
																if (childs9 == null)
																	continue;
																for (Element child9 : childs9) {
																	Element elementNode9 = child9.element("element");
																	if (elementNode9.attributeValue("href")
																			.equals(RandomKeyUtil.getHref(classType,
																					currentStateId))) {
																		sourceStateIdOnCall = child9
																				.attributeValue("id");
																	}
																}
															}

															// --------------------
															Element child8 = childs8.get(0);
															Element child = child8.addElement("children");

															child.addAttribute("xmi:type", "notation:Shape");
															targetStateIdOnCall = RandomKeyUtil.getPid();
															child.addAttribute("xmi:id", targetStateIdOnCall);
															child.addAttribute("type", "6000");

															// 1
															Element children1 = child.addElement("children");
															children1.addAttribute("xmi:type",
																	"notation:DecorationNode");
															children1.addAttribute("xmi:id", RandomKeyUtil.getPid());
															children1.addAttribute("type", "6001");
															Element layoutConstraint1 = children1
																	.addElement("layoutConstraint");
															layoutConstraint1.addAttribute("xmi:type",
																	"notation:Bounds");
															layoutConstraint1.addAttribute("xmi:id",
																	RandomKeyUtil.getPid());
															layoutConstraint1.addAttribute("width", "125");
															layoutConstraint1.addAttribute("height", "23");

															// 2
															Element children2 = child.addElement("children");
															children2.addAttribute("xmi:type",
																	"notation:DecorationNode");
															children2.addAttribute("xmi:id", RandomKeyUtil.getPid());
															children2.addAttribute("type", "19003");
															Element layoutConstraint2 = children2
																	.addElement("layoutConstraint");
															layoutConstraint2.addAttribute("xmi:type",
																	"notation:Location");
															layoutConstraint2.addAttribute("xmi:id",
																	RandomKeyUtil.getPid());
															layoutConstraint2.addAttribute("x", "40");

															// 3
															Element children3 = child.addElement("children");
															children3.addAttribute("xmi:type",
																	"notation:DecorationNode");
															children3.addAttribute("xmi:id", RandomKeyUtil.getPid());
															children3.addAttribute("type", "6002");
															Element layoutConstraint3 = children3
																	.addElement("layoutConstraint");
															layoutConstraint3.addAttribute("xmi:type",
																	"notation:Bounds");
															layoutConstraint3.addAttribute("xmi:id",
																	RandomKeyUtil.getPid());
															layoutConstraint2.addAttribute("y", "23");
															layoutConstraint3.addAttribute("width", "125");
															layoutConstraint3.addAttribute("height", "23");

															Element elementNode = child.addElement("element");
															elementNode.addAttribute("xmi:type", "uml:State");
															elementNode.addAttribute("href",
																	RandomKeyUtil.getHref(classType, newtargetStateId));

															Element layoutConstraintchild = child
																	.addElement("layoutConstraint");
															layoutConstraintchild.addAttribute("xmi:type",
																	"notation:Bounds");
															layoutConstraintchild.addAttribute("xmi:id",
																	RandomKeyUtil.getPid());
															layoutConstraintchild.addAttribute("x", "400");
															layoutConstraintchild.addAttribute("y", "360");
															layoutConstraintchild.addAttribute("width", "125");
															layoutConstraintchild.addAttribute("height", "50");
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

					Element newEdges = element.addElement("edges");
					newEdges.addAttribute("xmi:type", "notation:Connector");
					newEdges.addAttribute("xmi:id", RandomKeyUtil.getPid());
					newEdges.addAttribute("type", "7000");
					newEdges.addAttribute("source", sourceStateIdOnCall);
					newEdges.addAttribute("target", targetStateIdOnCall);
					// 1
					Element children1 = newEdges.addElement("children");
					children1.addAttribute("xmi:type", "notation:DecorationNode");
					children1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children1.addAttribute("type", "7001");
					Element layoutConstraint1 = children1.addElement("layoutConstraint");
					layoutConstraint1.addAttribute("xmi:type", "notation:Location");
					layoutConstraint1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					// 2
					Element children2 = newEdges.addElement("children");
					children2.addAttribute("xmi:type", "notation:DecorationNode");
					children2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children2.addAttribute("type", "7002");
					Element layoutConstraint2 = children2.addElement("layoutConstraint");
					layoutConstraint2.addAttribute("xmi:type", "notation:Location");
					layoutConstraint2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint2.addAttribute("x", "40");// 3
					layoutConstraint2.addAttribute("y", "23");
					// 3
					Element children3 = newEdges.addElement("children");
					children3.addAttribute("xmi:type", "notation:DecorationNode");
					children3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children3.addAttribute("type", "7003");
					Element layoutConstraint3 = children3.addElement("layoutConstraint");
					layoutConstraint3.addAttribute("xmi:type", "notation:Location");
					layoutConstraint3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint3.addAttribute("y", "50");// 60

					Element styles = newEdges.addElement("styles");
					styles.addAttribute("xmi:type", "notation:FontStyle");
					styles.addAttribute("xmi:id", RandomKeyUtil.getPid());

					Element elementNode = newEdges.addElement("element");
					elementNode.addAttribute("xmi:type", "uml:Transition");
					elementNode.addAttribute("href", RandomKeyUtil.getHref(classType, transitionId));

					Element bendpoints = newEdges.addElement("bendpoints");
					bendpoints.addAttribute("xmi:type", "notation:RelativeBendpoints");
					bendpoints.addAttribute("xmi:id", RandomKeyUtil.getPid());
					bendpoints.addAttribute("points", "[]");

					Element sourceAnchor = newEdges.addElement("sourceAnchor");
					sourceAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					sourceAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					sourceAnchor.addAttribute("id", "(1.0," + Math.random() + ")");

					Element targetAnchor = newEdges.addElement("sourceAnchor");
					targetAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					targetAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					targetAnchor.addAttribute("id", "(0.0," + Math.random() + ")");
				}
			}
		}
		xmlWriters(file, document, setOutputFormat(false));
	}

	/**
	 * update a new state and two transition in state machine, and update a
	 * initial state, a idle state and transition between them.
	 * 
	 * @param file
	 * @param parentStateId
	 * @param transitionId
	 * @param dummytransitionId
	 * @param currentStateId
	 * @param newtargetStateId
	 * @param stateMachineId
	 * @param classType
	 * @param newInitialStateId
	 * @param newIdleStateId
	 */
	public void updateStateAndTransitionNotationOnChange(String file, String parentStateId, String transitionId,
			String dummytransitionId, String currentStateId, String newtargetStateId, String stateMachineId,
			String classType, String newRegionId, String newInitialStateId, String newIdleStateId,
			String transitionBetweenInitAndIdleStateId) {
		file += ".notation";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Diagram")) {
				if (element.attributeValue("type").equals("PapyrusUMLStateMachineDiagram")) {
					List<Element> childs1 = element.elements("children");
					for (Element child1 : childs1) {
						Element elementNode1 = child1.element("element");
						// to a specific State Machine
						if (elementNode1.attributeValue("href")
								.equals(RandomKeyUtil.getHref(classType, stateMachineId))) {
							List<Element> childs2 = child1.elements("children");
							for (Element child2 : childs2) {
								List<Element> childs3 = child2.elements("children");
								if (childs3 == null)
									continue;
								for (Element child3 : childs3) {
									Element elementNode3 = child3.element("element");
									if (elementNode3 != null) {
										List<Element> childs4 = child3.elements("children");
										if (childs4 == null)
											continue;
										for (Element child4 : childs4) {
											List<Element> childs5 = child4.elements("children");
											if (childs5 == null)
												continue;
											for (Element child5 : childs5) {
												List<Element> childs6 = child5.elements("children");
												if (childs6 == null)
													continue;
												Element elementNode5 = child5.element("element");

												if (elementNode5.attributeValue("href")
														.equals(RandomKeyUtil.getHref(classType, currentStateId))) {
													sourceStateIdOnChange = child5.attributeValue("id");
												}

											}
										}
										// --------------------
										Element child4 = childs4.get(0);
										// target state
										Element child = child4.addElement("children");

										child.addAttribute("xmi:type", "notation:Shape");
										targetStateIdOnChange = RandomKeyUtil.getPid();
										child.addAttribute("xmi:id", targetStateIdOnChange);
										child.addAttribute("type", "6000");
										// System.err.println("targetStateIdOnChangeId
										// :" + targetStateIdOnChange);

										// 1
										Element children1 = child.addElement("children");
										children1.addAttribute("xmi:type", "notation:DecorationNode");
										children1.addAttribute("xmi:id", RandomKeyUtil.getPid());
										children1.addAttribute("type", "6001");
										Element layoutConstraint1 = children1.addElement("layoutConstraint");
										layoutConstraint1.addAttribute("xmi:type", "notation:Bounds");
										layoutConstraint1.addAttribute("xmi:id", RandomKeyUtil.getPid());
										layoutConstraint1.addAttribute("width", "125");
										layoutConstraint1.addAttribute("height", "23");

										// 2
										Element children2 = child.addElement("children");
										children2.addAttribute("xmi:type", "notation:DecorationNode");
										children2.addAttribute("xmi:id", RandomKeyUtil.getPid());
										children2.addAttribute("type", "19003");
										Element layoutConstraint2 = children2.addElement("layoutConstraint");
										layoutConstraint2.addAttribute("xmi:type", "notation:Location");
										layoutConstraint2.addAttribute("xmi:id", RandomKeyUtil.getPid());
										layoutConstraint2.addAttribute("x", "40");

										// 3
										Element children3 = child.addElement("children");
										children3.addAttribute("xmi:type", "notation:DecorationNode");
										children3.addAttribute("xmi:id", RandomKeyUtil.getPid());
										children3.addAttribute("type", "6002");
										Element layoutConstraint3 = children3.addElement("layoutConstraint");
										layoutConstraint3.addAttribute("xmi:type", "notation:Bounds");
										layoutConstraint3.addAttribute("xmi:id", RandomKeyUtil.getPid());
										layoutConstraint2.addAttribute("y", "23");
										layoutConstraint3.addAttribute("width", "125");
										layoutConstraint3.addAttribute("height", "23");

										Element elementNode = child.addElement("element");
										elementNode.addAttribute("xmi:type", "uml:State");
										elementNode.addAttribute("href",
												RandomKeyUtil.getHref(classType, newtargetStateId));

										Element layoutConstraintchild = child.addElement("layoutConstraint");
										layoutConstraintchild.addAttribute("xmi:type", "notation:Bounds");
										layoutConstraintchild.addAttribute("xmi:id", RandomKeyUtil.getPid());
										layoutConstraintchild.addAttribute("x", "1000");
										layoutConstraintchild.addAttribute("y", "500");
										layoutConstraintchild.addAttribute("width", "496");
										layoutConstraintchild.addAttribute("height", "220");

										// sub states inside target state
										// (Region)
										// child7
										Element Regionchild = children3.addElement("children");

										Regionchild.addAttribute("xmi:type", "notation:Shape");
										String children3childId = RandomKeyUtil.getPid();
										// System.err.println("Region
										// children3childId:" +
										// children3childId);
										Regionchild.addAttribute("xmi:id", children3childId);
										Regionchild.addAttribute("type", "3000");

										// 1 child8 (Region child)
										Element Regionchildchildren = Regionchild.addElement("children");
										Regionchildchildren.addAttribute("xmi:type", "notation:DecorationNode");
										Regionchildchildren.addAttribute("xmi:id", RandomKeyUtil.getPid());
										Regionchildchildren.addAttribute("type", "3002");

										// 2

										// initial state inside target state
										// child9
										Element initialState = Regionchildchildren.addElement("children");
										initialState.addAttribute("xmi:type", "notation:Shape");
										initialStateIdOnChange = RandomKeyUtil.getPid();
										// System.err.println("initialStateIdOnChangeId:"
										// + initialStateIdOnChange);
										initialState.addAttribute("xmi:id", initialStateIdOnChange);
										initialState.addAttribute("type", "8000");

										// 1
										Element initialStateChildern1 = initialState.addElement("children");
										initialStateChildern1.addAttribute("xmi:type", "notation:DecorationNode");
										initialStateChildern1.addAttribute("xmi:id", RandomKeyUtil.getPid());
										initialStateChildern1.addAttribute("type", "8001");
										Element initialStateChildernLayoutConstraint1 = initialStateChildern1
												.addElement("layoutConstraint");
										initialStateChildernLayoutConstraint1.addAttribute("xmi:type",
												"notation:Location");
										initialStateChildernLayoutConstraint1.addAttribute("xmi:id",
												RandomKeyUtil.getPid());
										initialStateChildernLayoutConstraint1.addAttribute("x", "25");
										initialStateChildernLayoutConstraint1.addAttribute("y", "3");

										// 2
										Element initialStateChildern = initialState.addElement("children");
										initialStateChildern.addAttribute("xmi:type", "notation:DecorationNode");
										initialStateChildern.addAttribute("xmi:id", RandomKeyUtil.getPid());
										initialStateChildern.addAttribute("type", "8002");
										Element initialStateChildernLayoutConstraint = initialStateChildern
												.addElement("layoutConstraint");
										initialStateChildernLayoutConstraint.addAttribute("xmi:type",
												"notation:Location");
										initialStateChildernLayoutConstraint.addAttribute("xmi:id",
												RandomKeyUtil.getPid());
										initialStateChildernLayoutConstraint.addAttribute("x", "25");
										initialStateChildernLayoutConstraint.addAttribute("y", "-10");

										Element initialStateElementNode = initialState.addElement("element");
										initialStateElementNode.addAttribute("xmi:type", "uml:Pseudostate");
										initialStateElementNode.addAttribute("href",
												RandomKeyUtil.getHref(classType, newInitialStateId));

										Element initialStatelayoutConstraintchild = initialState
												.addElement("layoutConstraint");
										initialStatelayoutConstraintchild.addAttribute("xmi:type", "notation:Bounds");
										initialStatelayoutConstraintchild.addAttribute("xmi:id",
												RandomKeyUtil.getPid());
										initialStatelayoutConstraintchild.addAttribute("x", "29");
										initialStatelayoutConstraintchild.addAttribute("y", "25");

										// Idle state inside target state
										Element idleState = Regionchildchildren.addElement("children");
										idleState.addAttribute("xmi:type", "notation:Shape");
										idleStateIdOnChange = RandomKeyUtil.getPid();
										idleState.addAttribute("xmi:id", idleStateIdOnChange);
										idleState.addAttribute("type", "6000");

										// 1
										Element idleStateChildern1 = idleState.addElement("children");
										idleStateChildern1.addAttribute("xmi:type", "notation:DecorationNode");
										idleStateChildern1.addAttribute("xmi:id", RandomKeyUtil.getPid());
										idleStateChildern1.addAttribute("type", "6001");
										Element idleStateChildern1LayoutConstraint = idleStateChildern1
												.addElement("layoutConstraint");
										idleStateChildern1LayoutConstraint.addAttribute("xmi:type", "notation:Bounds");
										idleStateChildern1LayoutConstraint.addAttribute("xmi:id",
												RandomKeyUtil.getPid());
										idleStateChildern1LayoutConstraint.addAttribute("x", "98");

										// 2
										Element idleStateChildern2 = idleState.addElement("children");
										idleStateChildern2.addAttribute("xmi:type", "notation:DecorationNode");
										idleStateChildern2.addAttribute("xmi:id", RandomKeyUtil.getPid());
										idleStateChildern2.addAttribute("type", "19003");
										Element idleStateChildern2LayoutConstraint = idleStateChildern2
												.addElement("layoutConstraint");
										idleStateChildern2LayoutConstraint.addAttribute("xmi:type",
												"notation:Location");
										idleStateChildern2LayoutConstraint.addAttribute("xmi:id",
												RandomKeyUtil.getPid());
										idleStateChildern2LayoutConstraint.addAttribute("x", "40");

										// 3
										Element idleStateChildern3 = idleState.addElement("children");
										idleStateChildern3.addAttribute("xmi:type", "notation:DecorationNode");
										idleStateChildern3.addAttribute("xmi:id", RandomKeyUtil.getPid());
										idleStateChildern3.addAttribute("type", "6002");
										Element idleStateChildern3LayoutConstraint = idleStateChildern3
												.addElement("layoutConstraint");
										idleStateChildern3LayoutConstraint.addAttribute("xmi:type", "notation:Bounds");
										idleStateChildern3LayoutConstraint.addAttribute("xmi:id",
												RandomKeyUtil.getPid());
										idleStateChildern3LayoutConstraint.addAttribute("y", "-1");
										idleStateChildern3LayoutConstraint.addAttribute("width", "98");

										Element idleStateElementNode = idleState.addElement("element");
										idleStateElementNode.addAttribute("xmi:type", "uml:State");
										idleStateElementNode.addAttribute("href",
												RandomKeyUtil.getHref(classType, newIdleStateId));

										Element idleStatelayoutConstraintchild = idleState
												.addElement("layoutConstraint");
										idleStatelayoutConstraintchild.addAttribute("xmi:type", "notation:Bounds");
										idleStatelayoutConstraintchild.addAttribute("xmi:id", RandomKeyUtil.getPid());
										idleStatelayoutConstraintchild.addAttribute("x", "125");
										idleStatelayoutConstraintchild.addAttribute("y", "25");
										idleStatelayoutConstraintchild.addAttribute("width", "98");
										idleStatelayoutConstraintchild.addAttribute("height", "40");

										// region child layoutConstraint
										Element RegionchildchildrenlayoutConstraint1 = Regionchildchildren
												.addElement("layoutConstraint");
										RegionchildchildrenlayoutConstraint1.addAttribute("xmi:type",
												"notation:Bounds");
										RegionchildchildrenlayoutConstraint1.addAttribute("xmi:id",
												RandomKeyUtil.getPid());

										// child7 element
										Element RegionchildElement1 = Regionchild.addElement("element");
										RegionchildElement1.addAttribute("xmi:type", "uml:Region");
										RegionchildElement1.addAttribute("href",
												RandomKeyUtil.getHref(classType, newRegionId));

										// System.err.println("herf :" +
										// RandomKeyUtil.getHref(classType,
										// newRegionId));

										Element children3childLayoutConstraint = Regionchild
												.addElement("layoutConstraint");
										children3childLayoutConstraint.addAttribute("xmi:type", "notation:Bounds");
										children3childLayoutConstraint.addAttribute("xmi:id", RandomKeyUtil.getPid());
										// x="778" y="378" width="496"
										// height="202"
										children3childLayoutConstraint.addAttribute("width", "496");
										children3childLayoutConstraint.addAttribute("height", "220");
									}
								}
							}
						}
					}

					Element newEdges = element.addElement("edges");
					newEdges.addAttribute("xmi:type", "notation:Connector");
					newEdges.addAttribute("xmi:id", RandomKeyUtil.getPid());
					newEdges.addAttribute("type", "7000");
					newEdges.addAttribute("source", sourceStateIdOnChange);
					newEdges.addAttribute("target", targetStateIdOnChange);
					// 1
					Element children1 = newEdges.addElement("children");
					children1.addAttribute("xmi:type", "notation:DecorationNode");
					children1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children1.addAttribute("type", "7001");
					Element layoutConstraint1 = children1.addElement("layoutConstraint");
					layoutConstraint1.addAttribute("xmi:type", "notation:Location");
					layoutConstraint1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					// 2
					Element children2 = newEdges.addElement("children");
					children2.addAttribute("xmi:type", "notation:DecorationNode");
					children2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children2.addAttribute("type", "7002");
					Element layoutConstraint2 = children2.addElement("layoutConstraint");
					layoutConstraint2.addAttribute("xmi:type", "notation:Location");
					layoutConstraint2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint2.addAttribute("x", "3");
					layoutConstraint2.addAttribute("y", "23");
					// 3
					Element children3 = newEdges.addElement("children");
					children3.addAttribute("xmi:type", "notation:DecorationNode");
					children3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children3.addAttribute("type", "7003");
					Element layoutConstraint3 = children3.addElement("layoutConstraint");
					layoutConstraint3.addAttribute("xmi:type", "notation:Location");
					layoutConstraint3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint3.addAttribute("y", "60");

					Element styles = newEdges.addElement("styles");
					styles.addAttribute("xmi:type", "notation:FontStyle");
					styles.addAttribute("xmi:id", RandomKeyUtil.getPid());

					Element elementNode = newEdges.addElement("element");
					elementNode.addAttribute("xmi:type", "uml:Transition");
					elementNode.addAttribute("href", RandomKeyUtil.getHref(classType, transitionId));

					Element bendpoints = newEdges.addElement("bendpoints");
					bendpoints.addAttribute("xmi:type", "notation:RelativeBendpoints");
					bendpoints.addAttribute("xmi:id", RandomKeyUtil.getPid());
					bendpoints.addAttribute("points", "[]");

					Element sourceAnchor = newEdges.addElement("sourceAnchor");
					sourceAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					sourceAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					sourceAnchor.addAttribute("id", "(1.0," + Math.random() + ")");

					Element targetAnchor = newEdges.addElement("targetAnchor");
					targetAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					targetAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					targetAnchor.addAttribute("id", "(0.0," + Math.random() + ")");

					// dummy transition
					Element dummynewEdges = element.addElement("edges");
					dummynewEdges.addAttribute("xmi:type", "notation:Connector");
					dummynewEdges.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummynewEdges.addAttribute("type", "7000");
					dummynewEdges.addAttribute("source", targetStateIdOnChange);
					dummynewEdges.addAttribute("target", sourceStateIdOnChange);
					// 1
					Element dummychildren1 = dummynewEdges.addElement("children");
					dummychildren1.addAttribute("xmi:type", "notation:DecorationNode");
					dummychildren1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummychildren1.addAttribute("type", "7001");
					Element dummylayoutConstraint1 = dummychildren1.addElement("layoutConstraint");
					dummylayoutConstraint1.addAttribute("xmi:type", "notation:Location");
					dummylayoutConstraint1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					// 2
					Element dummychildren2 = dummynewEdges.addElement("children");
					dummychildren2.addAttribute("xmi:type", "notation:DecorationNode");
					dummychildren2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummychildren2.addAttribute("type", "7002");
					Element dummylayoutConstraint2 = dummychildren2.addElement("layoutConstraint");
					dummylayoutConstraint2.addAttribute("xmi:type", "notation:Location");
					dummylayoutConstraint2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummylayoutConstraint2.addAttribute("x", "3");
					dummylayoutConstraint2.addAttribute("y", "23");
					// 3
					Element dummychildren3 = dummynewEdges.addElement("children");
					dummychildren3.addAttribute("xmi:type", "notation:DecorationNode");
					dummychildren3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummychildren3.addAttribute("type", "7003");
					Element dummylayoutConstraint3 = dummychildren3.addElement("layoutConstraint");
					dummylayoutConstraint3.addAttribute("xmi:type", "notation:Location");
					dummylayoutConstraint3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummylayoutConstraint3.addAttribute("y", "60");

					Element dummystyles = dummynewEdges.addElement("styles");
					dummystyles.addAttribute("xmi:type", "notation:FontStyle");
					dummystyles.addAttribute("xmi:id", RandomKeyUtil.getPid());

					Element dummyelementNode = dummynewEdges.addElement("element");
					dummyelementNode.addAttribute("xmi:type", "uml:Transition");
					dummyelementNode.addAttribute("href", RandomKeyUtil.getHref(classType, dummytransitionId));

					Element dummybendpoints = dummynewEdges.addElement("bendpoints");
					dummybendpoints.addAttribute("xmi:type", "notation:RelativeBendpoints");
					dummybendpoints.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummybendpoints.addAttribute("points", "[]");

					Element dummysourceAnchor = dummynewEdges.addElement("sourceAnchor");
					dummysourceAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					dummysourceAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummysourceAnchor.addAttribute("id", "(1.0," + Math.random() + ")");

					Element dummytargetAnchor = dummynewEdges.addElement("targetAnchor");
					dummytargetAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					dummytargetAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummytargetAnchor.addAttribute("id", "(0.0," + Math.random() + ")");

					// transition between initial state and idle state inside
					// target state

					Element initialAndIdleEdges = element.addElement("edges");
					initialAndIdleEdges.addAttribute("xmi:type", "notation:Connector");
					initialAndIdleEdges.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdleEdges.addAttribute("type", "7000");
					initialAndIdleEdges.addAttribute("source", initialStateIdOnChange);
					initialAndIdleEdges.addAttribute("target", idleStateIdOnChange);

					// System.err.println("initialStateIdOnChange :" +
					// initialStateIdOnChange);
					// System.err.println("idleStateIdOnChange :" +
					// idleStateIdOnChange);
					// 1
					Element initialAndIdlechildren1 = initialAndIdleEdges.addElement("children");
					initialAndIdlechildren1.addAttribute("xmi:type", "notation:DecorationNode");
					initialAndIdlechildren1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdlechildren1.addAttribute("type", "7001");
					Element initialAndIdlelayoutConstraint1 = initialAndIdlechildren1.addElement("layoutConstraint");
					initialAndIdlelayoutConstraint1.addAttribute("xmi:type", "notation:Location");
					initialAndIdlelayoutConstraint1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					// 2
					Element initialAndIdlechildren2 = initialAndIdleEdges.addElement("children");
					initialAndIdlechildren2.addAttribute("xmi:type", "notation:DecorationNode");
					initialAndIdlechildren2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdlechildren2.addAttribute("type", "7002");
					Element initialAndIdlelayoutConstraint2 = initialAndIdlechildren2.addElement("layoutConstraint");
					initialAndIdlelayoutConstraint2.addAttribute("xmi:type", "notation:Location");
					initialAndIdlelayoutConstraint2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdlelayoutConstraint2.addAttribute("x", "3");
					initialAndIdlelayoutConstraint2.addAttribute("y", "23");
					// 3
					Element initialAndIdlechildren3 = initialAndIdleEdges.addElement("children");
					initialAndIdlechildren3.addAttribute("xmi:type", "notation:DecorationNode");
					initialAndIdlechildren3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdlechildren3.addAttribute("type", "7003");
					Element initialAndIdlelayoutConstraint3 = initialAndIdlechildren3.addElement("layoutConstraint");
					initialAndIdlelayoutConstraint3.addAttribute("xmi:type", "notation:Location");
					initialAndIdlelayoutConstraint3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdlelayoutConstraint3.addAttribute("y", "60");

					Element initialAndIdlestyles = initialAndIdleEdges.addElement("styles");
					initialAndIdlestyles.addAttribute("xmi:type", "notation:FontStyle");
					initialAndIdlestyles.addAttribute("xmi:id", RandomKeyUtil.getPid());

					Element initialAndIdlelementNode = initialAndIdleEdges.addElement("element");
					initialAndIdlelementNode.addAttribute("xmi:type", "uml:Transition");
					initialAndIdlelementNode.addAttribute("href",
							RandomKeyUtil.getHref(classType, transitionBetweenInitAndIdleStateId));

					Element initialAndIdlebendpoints = initialAndIdleEdges.addElement("bendpoints");
					initialAndIdlebendpoints.addAttribute("xmi:type", "notation:RelativeBendpoints");
					initialAndIdlebendpoints.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdlebendpoints.addAttribute("points", "[]");

					Element initialAndIdlesourceAnchor = initialAndIdleEdges.addElement("sourceAnchor");
					initialAndIdlesourceAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					initialAndIdlesourceAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					dummysourceAnchor.addAttribute("id", "(1.0," + Math.random() + ")");

					Element initialAndIdletargetAnchor = initialAndIdleEdges.addElement("targetAnchor");
					initialAndIdletargetAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					initialAndIdletargetAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					initialAndIdletargetAnchor.addAttribute("id", "(0.0," + Math.random() + ")");

				}
			}
		}
		xmlWriters(file, document, setOutputFormat(false));
	}

	public void updateTransitionNotationOnChange(String file, String parentStateId, String transitionId,
			String currentStateId, String targetStateId, String stateMachineId, String classType) {
		file += ".notation";
		Document document = getDocument(file);
		Element root = document.getRootElement();
		for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
			Element element = it.next();
			if (element.getName().equals("Diagram")) {
				if (element.attributeValue("type").equals("PapyrusUMLStateMachineDiagram")) {
					List<Element> childs1 = element.elements("children");
					for (Element child1 : childs1) {
						Element elementNode1 = child1.element("element");
						// to a specific State Machine
						if (elementNode1.attributeValue("href")
								.equals(RandomKeyUtil.getHref(classType, stateMachineId))) {
							List<Element> childs2 = child1.elements("children");
							for (Element child2 : childs2) {
								List<Element> childs3 = child2.elements("children");
								if (childs3 == null)
									continue;
								for (Element child3 : childs3) {
									Element elementNode3 = child3.element("element");
									if (elementNode3 != null) {
										List<Element> childs4 = child3.elements("children");
										if (childs4 == null)
											continue;
										for (Element child4 : childs4) {
											List<Element> childs5 = child4.elements("children");
											if (childs5 == null)
												continue;
											for (Element child5 : childs5) {
												Element elementNode5 = child5.element("element");
												if (elementNode5.attributeValue("href")
														.equals(RandomKeyUtil.getHref(classType, currentStateId))) {
													sourceStateIdOnChange = child5.attributeValue("id");
												} //
												if (elementNode5.attributeValue("href")
														.equals(RandomKeyUtil.getHref(classType, targetStateId))) {
													targetStateIdOnChange = child5.attributeValue("id");
												}
											}
										}
									}
								}
							}
						}
					}

					Element newEdges = element.addElement("edges");
					newEdges.addAttribute("xmi:type", "notation:Connector");
					newEdges.addAttribute("xmi:id", RandomKeyUtil.getPid());
					newEdges.addAttribute("type", "7000");
					newEdges.addAttribute("source", sourceStateIdOnChange);
					newEdges.addAttribute("target", targetStateIdOnChange);
					// 1
					Element children1 = newEdges.addElement("children");
					children1.addAttribute("xmi:type", "notation:DecorationNode");
					children1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children1.addAttribute("type", "7001");
					Element layoutConstraint1 = children1.addElement("layoutConstraint");
					layoutConstraint1.addAttribute("xmi:type", "notation:Location");
					layoutConstraint1.addAttribute("xmi:id", RandomKeyUtil.getPid());
					// 2
					Element children2 = newEdges.addElement("children");
					children2.addAttribute("xmi:type", "notation:DecorationNode");
					children2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children2.addAttribute("type", "7002");
					Element layoutConstraint2 = children2.addElement("layoutConstraint");
					layoutConstraint2.addAttribute("xmi:type", "notation:Location");
					layoutConstraint2.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint2.addAttribute("x", "3");
					layoutConstraint2.addAttribute("y", "23");
					// 3
					Element children3 = newEdges.addElement("children");
					children3.addAttribute("xmi:type", "notation:DecorationNode");
					children3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					children3.addAttribute("type", "7003");
					Element layoutConstraint3 = children3.addElement("layoutConstraint");
					layoutConstraint3.addAttribute("xmi:type", "notation:Location");
					layoutConstraint3.addAttribute("xmi:id", RandomKeyUtil.getPid());
					layoutConstraint3.addAttribute("y", "60");

					Element styles = newEdges.addElement("styles");
					styles.addAttribute("xmi:type", "notation:FontStyle");
					styles.addAttribute("xmi:id", RandomKeyUtil.getPid());

					Element elementNode = newEdges.addElement("element");
					elementNode.addAttribute("xmi:type", "uml:Transition");
					elementNode.addAttribute("href", RandomKeyUtil.getHref(classType, transitionId));

					Element bendpoints = newEdges.addElement("bendpoints");
					bendpoints.addAttribute("xmi:type", "notation:RelativeBendpoints");
					bendpoints.addAttribute("xmi:id", RandomKeyUtil.getPid());
					bendpoints.addAttribute("points", "[]");

					Element sourceAnchor = newEdges.addElement("sourceAnchor");
					sourceAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					sourceAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					sourceAnchor.addAttribute("id", "(1.0," + Math.random() + ")");

					Element targetAnchor = newEdges.addElement("targetAnchor");
					targetAnchor.addAttribute("xmi:type", "notation:IdentityAnchor");
					targetAnchor.addAttribute("xmi:id", RandomKeyUtil.getPid());
					targetAnchor.addAttribute("id", "(0.0," + Math.random() + ")");
				}
			}
		}
		xmlWriters(file, document, setOutputFormat(false));
	}

}

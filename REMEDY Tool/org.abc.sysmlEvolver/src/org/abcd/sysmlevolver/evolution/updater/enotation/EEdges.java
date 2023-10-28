package org.abcd.sysmlevolver.evolution.updater.enotation;

import java.util.List;

public class EEdges {
	private String type = "notation:Connector";
	private String pid;
	private String type1;
	private String source;
	private String target;
	private List<EChildrenSon> childrens;
	private EStyles styles;
	private EElement element;
	private EBendPoints bendPoints;
	private EAnchor sourceAnchor;
	private EAnchor targetAnchor;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getType1() {
		return type1;
	}
	public void setType1(String type1) {
		this.type1 = type1;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public List<EChildrenSon> getChildrens() {
		return childrens;
	}
	public void setChildrens(List<EChildrenSon> childrens) {
		this.childrens = childrens;
	}
	public EStyles getStyles() {
		return styles;
	}
	public void setStyles(EStyles styles) {
		this.styles = styles;
	}
	public EElement getElement() {
		return element;
	}
	public void setElement(EElement element) {
		this.element = element;
	}
	public EBendPoints getBendPoints() {
		return bendPoints;
	}
	public void setBendPoints(EBendPoints bendPoints) {
		this.bendPoints = bendPoints;
	}
	public EAnchor getSourceAnchor() {
		return sourceAnchor;
	}
	public void setSourceAnchor(EAnchor sourceAnchor) {
		this.sourceAnchor = sourceAnchor;
	}
	public EAnchor getTargetAnchor() {
		return targetAnchor;
	}
	public void setTargetAnchor(EAnchor targetAnchor) {
		this.targetAnchor = targetAnchor;
	}
	
	
}

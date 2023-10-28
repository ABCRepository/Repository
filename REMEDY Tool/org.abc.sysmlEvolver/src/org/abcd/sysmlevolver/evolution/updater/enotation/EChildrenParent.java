package org.abcd.sysmlevolver.evolution.updater.enotation;

import java.util.List;

public class EChildrenParent {
	private String type = "notation:Shape";
	private String pid;
	private String type1;
	private List<EChildrenSon> childrens;
	private EElement element;
	private ELayoutConstraint layoutConstraint;
	
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
	public List<EChildrenSon> getChildrens() {
		return childrens;
	}
	public void setChildrens(List<EChildrenSon> childrens) {
		this.childrens = childrens;
	}
	public EElement getElement() {
		return element;
	}
	public void setElement(EElement element) {
		this.element = element;
	}
	public ELayoutConstraint getLayoutConstraint() {
		return layoutConstraint;
	}
	public void setLayoutConstraint(ELayoutConstraint layoutConstraint) {
		this.layoutConstraint = layoutConstraint;
	}
	
	
	
}

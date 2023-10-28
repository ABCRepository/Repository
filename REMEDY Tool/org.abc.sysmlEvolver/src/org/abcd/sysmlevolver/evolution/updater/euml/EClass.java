package org.abcd.sysmlevolver.evolution.updater.euml;

import java.util.List;

public class EClass {
	private String type = "uml:Class";
	private String pid = null;
	private String name = null;
	private String classifierBehavior =null;
	
	private List<EAttribute> attributes = null;
	
	private EBehavior behavior;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassifierBehavior() {
		return classifierBehavior;
	}

	public void setClassifierBehavior(String classifierBehavior) {
		this.classifierBehavior = classifierBehavior;
	}

	public List<EAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<EAttribute> attributes) {
		this.attributes = attributes;
	}

	public EBehavior getBehavior() {
		return behavior;
	}

	public void setBehavior(EBehavior behavior) {
		this.behavior = behavior;
	}
	
	
}

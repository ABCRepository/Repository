package org.abcd.sysmlevolver.evolution.updater.euml;

public class EOwnedRule {
	private String type = "uml:Constraint";
	private String pid;
	private ESpecification specification;
	
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
	public ESpecification getSpecification() {
		return specification;
	}
	public void setSpecification(ESpecification specification) {
		this.specification = specification;
	}
	
	
}

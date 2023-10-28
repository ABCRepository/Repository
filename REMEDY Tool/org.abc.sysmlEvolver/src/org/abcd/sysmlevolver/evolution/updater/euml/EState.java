package org.abcd.sysmlevolver.evolution.updater.euml;

public class EState {
	private String type ="uml:State";
	private String name;
	private String pid;
	private String stateInvariant;
	private EOwnedRule ownedRule;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
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
	public String getStateInvariant() {
		return stateInvariant;
	}
	public void setStateInvariant(String stateInvariant) {
		this.stateInvariant = stateInvariant;
	}
	public EOwnedRule getOwnedRule() {
		return ownedRule;
	}
	public void setOwnedRule(EOwnedRule ownedRule) {
		this.ownedRule = ownedRule;
	}
	
	
	
	
	

}

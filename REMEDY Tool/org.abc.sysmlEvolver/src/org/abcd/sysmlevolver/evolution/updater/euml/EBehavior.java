package org.abcd.sysmlevolver.evolution.updater.euml;

public class EBehavior {
	private String type ="uml:StateMachine";
	private String pid;
	private String name;
	private ERegion region;
	
	
	

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

	public ERegion getRegion() {
		return region;
	}

	public void setRegion(ERegion region) {
		this.region = region;
	}
	
	
}

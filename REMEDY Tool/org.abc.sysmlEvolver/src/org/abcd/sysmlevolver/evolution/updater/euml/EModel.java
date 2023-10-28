package org.abcd.sysmlevolver.evolution.updater.euml;


public class EModel {
	private String pid;
	private String name;
	private EClass eClass;
	
	public EModel() {
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

	public EClass geteClass() {
		return eClass;
	}

	public void seteClass(EClass eClass) {
		this.eClass = eClass;
	}

	
	
}

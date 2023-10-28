package org.abcd.sysmlevolver.evolution.updater.euml;

import java.util.ArrayList;
import java.util.List;

public class ERegion {
	private String type ="uml:Region";
	private String pid;
	private String name;
	
	private List<EState> states;
	private List<ETransition> transitions;
	
	
	public ERegion() {
		states = null;
		transitions = null;
		states = new ArrayList<EState>();
		transitions = new ArrayList<ETransition>();
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	

}

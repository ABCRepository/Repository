package org.abcd.sysmlevolver.evolution.updater.euml;

public class ETransition {
	private String type = "uml:Transition";
	private String pid;
	private String guard;
	private String sourceId;
	private String targetId;
	private EOwnedRule ownedRule;
	private ETrigger trigger;

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
	

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public EOwnedRule getOwnedRule() {
		return ownedRule;
	}

	public void setOwnedRule(EOwnedRule ownedRule) {
		this.ownedRule = ownedRule;
	}

	public ETrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(ETrigger trigger) {
		this.trigger = trigger;
	}
	
}

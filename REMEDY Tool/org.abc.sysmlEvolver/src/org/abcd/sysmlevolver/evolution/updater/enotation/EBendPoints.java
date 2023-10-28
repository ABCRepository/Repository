package org.abcd.sysmlevolver.evolution.updater.enotation;

import java.util.List;

public class EBendPoints {
	private String type = "notation:RelativeBendpoints";
	private String pid;
	private List<EPoint> points;
	
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
	public List<EPoint> getPoints() {
		return points;
	}
	public void setPoints(List<EPoint> points) {
		this.points = points;
	}
	
}

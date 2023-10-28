package org.abcd.sysmlevolver.evolution.updater.euml;

import java.util.UUID;

public class RandomKeyUtil {

	public static String getPid() {
		UUID id = UUID.randomUUID();
		String str = id.toString();
		str = "new111_"+str;
		return str;
	}
	
//	public static String getAnchorId() {
//		
//		UUID id = UUID.randomUUID();
//		String str = id.toString();
//		str = "new111_"+str;
//		return str;
//	}

	/**
	 *  
	 * @param type associate with type
	 * @param pid associate with reference id
	 * @return
	 */
	public static String getHref(String type,String pid) {
		type = type + ".uml#" + pid;
		return type;
	}

	public static void main(String[] args) {
		System.out.println(RandomKeyUtil.getPid());
		System.out.println(RandomKeyUtil.getHref("Testbed1",RandomKeyUtil.getPid()));
	}

}

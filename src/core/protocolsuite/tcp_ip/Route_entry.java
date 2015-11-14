/*
 * Route_entry.java
 *
 * Created on 7 Nov 2005, 17:52
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package core.protocolsuite.tcp_ip;

/**
 * 
 * @author key
 */
public class Route_entry {

	public static class RouterEntryType {
		public static int STATIC = 0;
		public static int RIP = 1;
		public static int OSPF = 2;
	}
	
	public String destIP;
	public String gateway;
	public String genMask;
	public String iFace;
	public int Type; // 0 - Static; 1 - RIP; 2 - OSPF
	public int metric;
	public long createdTime;

	/** Creates a new instance of Route_entry */
	public Route_entry() {
	}

	public Route_entry(String destIP, String gateway, String genMask, String iFace, int Type) {
		this.destIP = destIP;
		this.gateway = gateway;
		this.genMask = genMask;
		this.iFace = iFace;
		this.Type = Type;
		this.metric = 0;
		this.createdTime = 0;
	}

	public Route_entry(String destIP, String gateway, String genMask, String iFace, int Type, int metric, long ct) {
		this.destIP = destIP;
		this.gateway = gateway;
		this.genMask = genMask;
		this.iFace = iFace;
		this.Type = Type;
		this.metric = metric;
		this.createdTime = ct;
	}
}

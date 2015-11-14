package core;

import java.util.Enumeration;
import java.util.Hashtable;

import core.protocolsuite.tcp_ip.ICMP_packet;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.IP_packet;
import core.protocolsuite.tcp_ip.TCP_packet;
import core.protocolsuite.tcp_ip.UDP_packet;
 
/**
 *
 * @author key
 */
public class AccessListEngine {
	public class access_list{
		public final static int PERMIT = 1;
		public final static int DENY = 2;
		public final static int REMARK = 0;

		public final static int IP = 0;
		public final static int ICMP = 1;
		public final static int UDP = 2;
		public final static int TCP = 3;

		public int number = 0;
		public int line = 0;
		public short action = -1;
		public int protocol = -1;  
		public boolean log = false;

		public String IP1 = null;
		public String SubnetMask1 = null;
		public int Port1 = 0;

		public String IP2 = null;
		public String SubnetMask2 = null;
		public int Port2 = 0;
	}

	Node parentNode;

	Hashtable<Integer, Hashtable<Integer,access_list>> ACLs = new Hashtable<Integer, Hashtable<Integer,access_list>>();

	public AccessListEngine(Node inParentNode){
		parentNode = inParentNode;
	}

	public int addACL(int number, int line, short action, String IP1, String SubnetMask1, boolean log){
		return addACL(number, line, action, -1, IP1, SubnetMask1, null, null, 0, log);
	}

	public int addACL(int number, int line, short action, int protocol, String IP1, String SubnetMask1, String IP2, String SubnetMask2, int port, boolean log){
		int new_line = line;
		Hashtable<Integer,access_list> ACLl = ACLs.get(Integer.valueOf(number));
		boolean new_acl = false;

		if(ACLl == null){
			ACLl = new Hashtable<Integer,access_list>();            
			new_acl = true;
		}

		if(ACLl.get(Integer.valueOf(new_line)) != null) return -1;

		if(new_line < 0){            
			new_line = 0;
			Integer temp;
			Enumeration<Integer> e = ACLl.keys();
			while(e.hasMoreElements()){
				temp = e.nextElement();
				if(new_line < Integer.valueOf(temp)){
					new_line = Integer.valueOf(temp);
				}
			}
			new_line += 10;
		}

		access_list al = new access_list();
		al.number = number;
		al.line = new_line;
		al.action = action;
		al.protocol = protocol;
		al.IP1 = IP1;
		al.SubnetMask1 = SubnetMask1;
		al.IP2 = IP2;
		al.SubnetMask2 = SubnetMask2;
		al.Port2 = port;
		al.log = log;

		ACLl.put(Integer.valueOf(new_line), al);

		if(new_acl) ACLs.put(Integer.valueOf(number), ACLl);

		return new_line;
	}

	public boolean removeACL(int number, int line){
		Hashtable<Integer,access_list> ACLl = ACLs.get(Integer.valueOf(number));

		if(ACLl == null) return false;

		if(ACLl.get(Integer.valueOf(line)) == null) return false;

		ACLl.remove(Integer.valueOf(line));

		if(ACLl.size() < 1) ACLs.remove(Integer.valueOf(number));

		return true;
	}

	public boolean removeACL(int number, int action, String IP1, String SubnetMask1){
		Hashtable<Integer,access_list> ACLl = ACLs.get(Integer.valueOf(number));
		boolean found = false;

		if(ACLl == null) return false;

		access_list temp_acl = null;
		Enumeration<Integer> e = ACLl.keys();
		while(e.hasMoreElements()){
			temp_acl = ACLl.get(e.nextElement());
			if(temp_acl.action == action &&
					temp_acl.IP1.equals(IP1) &&
					temp_acl.SubnetMask1.equals(SubnetMask1)){
				found  = true;
				break;
			}            
		}

		if(found) ACLl.remove(Integer.valueOf(temp_acl.line));

		if(ACLl.size() < 1) ACLs.remove(Integer.valueOf(number));

		return found;
	}

	public boolean removeACL(int number, int action, int protocol, String IP1, String SubnetMask1, String IP2, String SubnetMask2, int port){
		Hashtable<Integer,access_list> ACLl = ACLs.get(Integer.valueOf(number));
		boolean found = false;

		if(ACLl == null) return false;

		access_list temp_acl = null;
		Enumeration<Integer> e = ACLl.keys();
		while(e.hasMoreElements()){
			temp_acl = ACLl.get(e.nextElement());
			if(temp_acl.action == action &&
					(protocol==-1 || temp_acl.protocol == protocol) &&
					temp_acl.IP1.equals(IP1) &&
					temp_acl.SubnetMask1.equals(SubnetMask1) &&
					(IP2==null || temp_acl.IP2.equals(IP2)) &&
					(SubnetMask2==null ||  SubnetMask2.equals(temp_acl.SubnetMask2)) &&
					(port==0 || temp_acl.Port2 == port)
			){
				found  = true;
				break;
			}            
		}

		if(found) ACLl.remove(Integer.valueOf(temp_acl.line));

		if(ACLl.size() < 1) ACLs.remove(Integer.valueOf(number));

		return found;
	}

	public boolean isACLExists(int number, int line){
		Hashtable<Integer,access_list> acllist = ACLs.get(new Integer(number));
		if(acllist==null) return false;
		return (acllist.containsKey(new Integer(line)));
	}

	public boolean passACL(int number, Packet packet){
		Hashtable<Integer,access_list> ACLl = ACLs.get(Integer.valueOf(number));

		if(ACLl == null) return true;

		try{
			int port1 = 0;
			int port2 = 0;
			int protocol = -1;
			String IP1 = null;
			String IP2 = null;
			if(packet instanceof IP_packet){
				IP_packet ippacket = (IP_packet) packet;
				IP1 = ippacket.getSourceIPAddress();
				IP2 = ippacket.getDestIPAddress();
				protocol = access_list.IP;
				if(ippacket instanceof TCP_packet){
					protocol = AccessListEngine.access_list.TCP;
					port1 = ((TCP_packet)ippacket).get_srcPort();
					port2 = ((TCP_packet)ippacket).get_destPort();
				}
				else if(ippacket instanceof UDP_packet){
					protocol = AccessListEngine.access_list.UDP;
					port1 = ((UDP_packet)ippacket).get_srcPort();
					port2 = ((UDP_packet)ippacket).get_destPort();
				}
				else if(ippacket instanceof ICMP_packet){
					protocol = AccessListEngine.access_list.ICMP;
				}
				String binIP1 = IPV4Address.toBinaryString(IP1);
				String binNetwork1 = "";
				String binMask1 = "";

				String binIP2 = null;
				if(IP2!=null){
					binIP2 = IPV4Address.toBinaryString(IP2);
				}
				String binNetwork2 = null;
				String binMask2 = null;

				access_list temp_acl = null;
				Enumeration<Integer> e = ACLl.keys();
				while(e.hasMoreElements()){
					temp_acl = ACLl.get(e.nextElement());

					if(temp_acl.action>0){
						binNetwork1 = IPV4Address.toBinaryString(temp_acl.IP1);
						binMask1 = IPV4Address.toBinaryString(temp_acl.SubnetMask1);
						binNetwork2 = null;
						binMask2 = null;
						if(temp_acl.IP2!=null && temp_acl.SubnetMask2!=null){
							binNetwork2 = IPV4Address.toBinaryString(temp_acl.IP2);
							binMask2 = IPV4Address.toBinaryString(temp_acl.SubnetMask2);
						}

						if((temp_acl.protocol==-1 || temp_acl.protocol==access_list.IP 
								|| temp_acl.protocol==protocol) &&
								IPV4Address.IPandMask(binNetwork1, binMask1).equals(IPV4Address.IPandMask(binIP1,binMask1)) &&
								(binNetwork2==null  
										|| (binIP2!=null && IPV4Address.IPandMask(binNetwork2, binMask2).equals(IPV4Address.IPandMask(binIP2,binMask2)))) &&
								(temp_acl.Port2 == 0 || temp_acl.Port2 == port2)
						){
							if(temp_acl.log){
								String src = IP1;
								String dst = IP2;
								if(port1>0 && port2>0){
									src += ":"+port1;
									dst += ":"+port2;
								}
								if(temp_acl.action == access_list.PERMIT){
									printLayerInfo("Packet from "+src+" to "+dst+" permited");
								}
								else{
									printLayerInfo("Packet from "+src+" to "+dst+" denied");
								}
							}
							if(temp_acl.action == access_list.PERMIT) return true;
							else return false;
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return false;
	}

	public void clear(){
		ACLs.clear();

	}

	protected void printLayerInfo(String s) {
		LayerInfo protInfo = new LayerInfo(getClass().getName());
		protInfo.setObjectName(parentNode.getName());
		protInfo.setDataType("ACL rule");
		protInfo.setLayer("ACL");
		protInfo.setDescription(s);
		Simulation.addLayerInfo(protInfo);
	}
}

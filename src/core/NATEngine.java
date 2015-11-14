package core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import core.protocolsuite.tcp_ip.ICMP_packet;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.IP_packet;
import core.protocolsuite.tcp_ip.TCP_packet;
import core.protocolsuite.tcp_ip.UDP_packet;

public class NATEngine {
	
	NetworkLayerDevice parentNode;
	
	public class NAT_session{
		public final static int UNKNOWN = 0;
		public final static int ICMP = 1;
        public final static int UDP = 2;
        public final static int TCP = 3;
        
		public int protocol = 0;
		
		public String in_ip = "";		
		public int in_port = 0;
		
		public String out_ip = "";
		public int out_port = 0;
		
		public long last_time = 0;
		
		public NAT_session(){
			last_time = System.currentTimeMillis();
		}
		
	}
	
	Vector<NAT_session> sessions;
	
	public void clearExpiredSessions(){
		ArrayList toRemove = new ArrayList();
		long time_now = System.currentTimeMillis();
		
		for(int i = 0; i<sessions.size(); i++){
			if((time_now - sessions.get(i).last_time) > 360 * 1000)
				toRemove.add(i);
		}
		
		if(toRemove.size() > 0)
			sessions.removeAll(toRemove);
	}
	
	public class NAT_rule{
		public boolean dynamic = false; // false -- static, true -- dymanic
		public boolean pool = false; // false -- int, true -- pool
		
		public int acl = 0; // acl number for dynamic
		
        public final static int IP = 0;
        public final static int ICMP = 1;
        public final static int UDP = 2;
        public final static int TCP = 3;
		public int protocol = NAT_rule.IP;
		
		public String in_ip = "";		
		public int in_port = 0;
		
		public String out_ip = "";
		public String out_int = ""; //or pool		
		public int out_port = 0;
		
	}
	
	Vector<NAT_rule> static_rules;
	Vector<NAT_rule> dynamic_rules;
	Hashtable<String, Pair> pools;
	java.security.SecureRandom rng;
	
	public NATEngine(NetworkLayerDevice node){
		parentNode = node;
		
		static_rules = new Vector<NAT_rule>(); 
		dynamic_rules = new Vector<NAT_rule>(); 
		sessions = new Vector<NAT_session>();
		pools = new Hashtable<String, Pair>();
		rng = new java.security.SecureRandom();
	}
	
	public int addRule(NAT_rule rule){
		return addRule(rule,-1);
	}
	
	public int addRule(NAT_rule rule, int pos){
		Vector<NAT_rule> rules = static_rules;
		if(rule.dynamic) rules = dynamic_rules;
		if(pos<0 || pos>=rules.size()){
			rules.add(rule);
			return rules.size()-1;
		}
		rules.add(pos, rule);
		return pos;
	}
	
	public boolean removeRule(int rnum, boolean dynamic){
		Vector<NAT_rule> rules = static_rules;
		if(dynamic) rules = dynamic_rules;
		if(rnum>=0 && rnum<rules.size()){
			rules.remove(rnum);
			return true;
		}
		return false;
	}
	
	public boolean isOverloadIP(String ip){
		for(int i=0; i<static_rules.size(); i++)
			if(static_rules.get(i).out_ip.equals(ip))
				return true;
		return false;
	}
	
	public Vector<Integer> findRules(boolean dynamic, Boolean pool, Integer acl, String in_ip, Integer in_port, String out_ip, Integer out_port, String out_int){
		Vector<NAT_rule> rules = static_rules;
		if(dynamic) rules = dynamic_rules;
		Vector<Integer> result = new Vector<Integer>();
		for(int i=0; i<rules.size(); i++){
			NAT_rule rule = rules.get(i);
			if((pool==null || rule.pool==pool.booleanValue())
				&& (acl==null || rule.acl==acl.intValue())
				&& (in_ip==null || rule.in_ip.equalsIgnoreCase(in_ip))
				&& (in_port==null || rule.in_port==in_port.intValue())
				&& (out_ip==null || rule.out_ip.equalsIgnoreCase(out_ip))
				&& (out_port==null || rule.out_port==out_port.intValue())
				&& (out_int==null || rule.out_int.equalsIgnoreCase(out_int))){
				result.add(new Integer(i));
			}
		}
		return result;
	}
	
	public IP_packet NAT_inside(IP_packet p){
		IP_packet rp = p;
		String NewIP = "";
		int NewPort = 0;
		
		clearExpiredSessions();

		for(int i=0; i<static_rules.size(); i++){
			NAT_rule rule = static_rules.get(i);

			
			if(rule.in_ip.equals(p.getSourceIPAddress()) && parentNode.getACL().passACL(rule.acl, p)){
				// rule found, let's see its type
				
				if(rule.protocol == NAT_rule.IP || rule.protocol == NAT_rule.ICMP){
					
					NewIP = rule.out_ip;
					
					if(rule.out_ip.length() < 5){
						NewIP = parentNode.getIPAddress(rule.out_int);
					}
					
					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
							"Inside NAT for packet applied: changed src IP from " + p.getSourceIPAddress() + " to " + NewIP + "."); 
					
					rp.setSourceIPAddress(NewIP);
					
					return rp;
				}else if(rule.protocol == NAT_rule.TCP && p instanceof TCP_packet){
					if(((TCP_packet)p).get_srcPort() == rule.in_port){
						NewIP = rule.out_ip;
						NewPort = rule.out_port;
						
						if(rule.out_ip.length() < 5){
							NewIP = parentNode.getIPAddress(rule.out_int);
						}
						
						Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
								"Inside PAT(tcp) for ip packet applied: changed src from " + p.getSourceIPAddress() + ":" + ((TCP_packet)p).get_srcPort() + " to " + NewIP + ":" + NewPort + "."); 
						
						((TCP_packet)rp).setSrcPort(NewPort);
						rp.setSourceIPAddress(NewIP);
						
						return rp;
					}					
				}else if(rule.protocol == NAT_rule.UDP && p instanceof UDP_packet){
					if(((UDP_packet)p).get_srcPort() == rule.in_port){
						NewIP = rule.out_ip;
						NewPort = rule.out_port;
						
						if(rule.out_ip.length() < 5){
							NewIP = parentNode.getIPAddress(rule.out_int);
						}
						
						Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
								"Inside PAT(udp) for ip packet applied: changed src from " + p.getSourceIPAddress() + ":" + ((UDP_packet)p).get_srcPort() + " to " + NewIP + ":" + NewPort + "."); 
						
						((UDP_packet)rp).setSrcPort(NewPort);
						rp.setSourceIPAddress(NewIP);
						
						return rp;
					}
				}
			}
		}
		
		String OldIP = "";
		int OldPort = 0;
		/*String NewIP = "";
		int NewPort = 0;*/
		int type = NAT_session.UNKNOWN;
		String proto = "unk";
		int s = -1;
		
		OldIP = p.getSourceIPAddress();
		
		if(p instanceof TCP_packet){
			type = NAT_session.TCP;
			OldPort = ((TCP_packet)p).get_srcPort();
			proto = "tcp";
		}else if(p instanceof UDP_packet){
			type = NAT_session.UDP;
			OldPort = ((UDP_packet)p).get_srcPort();
			proto = "udp";
		}else if(p instanceof ICMP_packet){
			type = NAT_session.ICMP;
			OldPort = ((ICMP_packet)p).UniqueIdentfier;
			proto = "icmp";
		}
		
		for(int i=0; i<dynamic_rules.size(); i++){
			NAT_rule rule = dynamic_rules.get(i);
						
			if(parentNode.getACL().passACL(rule.acl, p)){				
				s = findInsideSession(type, OldIP, OldPort);
				
				if(s>=0){
					sessions.get(s).last_time = System.currentTimeMillis();
					
					NewPort = sessions.get(s).out_port;
					NewIP = sessions.get(s).out_ip;
					
				}else{
					NewPort = rng.nextInt(40000) + 20000;
					if(rule.pool){
						NewIP = (String)pools.get(rule.out_int).getFirst();
					}else{
						NewIP = parentNode.getIPAddress(rule.out_int);
					}
					
					NAT_session new_s = new NAT_session();
					new_s.in_ip = OldIP; new_s.in_port = OldPort;
					new_s.protocol = type;
					new_s.out_ip = NewIP; new_s.out_port = NewPort;
					sessions.add(new_s);
				}
				
				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
						"Inside dynamic NAT(" + proto + ") for ip packet applied: changed src from " + OldIP + ":" + OldPort + " to " + NewIP + ":" + NewPort + ".");
				
				rp.setSourceIPAddress(NewIP);
				
				if(p instanceof TCP_packet){
					((TCP_packet)rp).setSrcPort(NewPort);
				}else if(p instanceof UDP_packet){
					((UDP_packet)rp).setSrcPort(NewPort);
				}
				
				return rp;
			}
		}
		
		return rp;
	}
	
	private int findInsideSession(int protocol, String ip, int port){
		for(int i = 0; i < sessions.size(); i++){
			NAT_session s = sessions.get(i);
			
			if(s.in_ip.equals(ip) && s.in_port == port && s.protocol == protocol)
				return i;
		}
		
		return -1;
	}
	
	private int findOutsideSession(int protocol, String ip, int port){
		for(int i = 0; i < sessions.size(); i++){
			NAT_session s = sessions.get(i);
			
			if(s.out_ip.equals(ip) && s.out_port == port && s.protocol == protocol)
				return i;
		}
		
		return -1;
	}
	
	public IP_packet NAT_outside(IP_packet p){
		IP_packet rp = p;
		String NewIP = "";
		int NewPort = 0;
		
		for(int i=0; i<static_rules.size(); i++){
			NAT_rule rule = static_rules.get(i);
			
			if(rule.out_ip.equals(p.getDestIPAddress()) && parentNode.getACL().passACL(rule.acl, p)){
				// rule found, let's see its type
				
				if(rule.protocol == NAT_rule.IP || rule.protocol == NAT_rule.ICMP){
					
					NewIP = rule.in_ip;
					
					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
							"Outside NAT for packet applied: changed dst IP from " + p.getDestIPAddress() + " to " + NewIP + ".");
					
					rp.setDestIPAddress(NewIP);
					
					return rp;
				}else if(rule.protocol == NAT_rule.TCP && p instanceof TCP_packet){
					if(((TCP_packet)p).get_destPort() == rule.out_port){
						NewIP = rule.in_ip;
						NewPort = rule.in_port;
						
						Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
								"Inside PAT(tcp) for packet applied: changed dst from " + p.getDestIPAddress() + ":" + ((TCP_packet)p).get_destPort() + " to " + NewIP + ":" + NewPort + "."); 
						
						((TCP_packet)rp).setDestPort(NewPort);
						rp.setDestIPAddress(NewIP);
						
						return rp;
					}
				}else if(rule.protocol == NAT_rule.UDP && p instanceof UDP_packet){
					if(((UDP_packet)p).get_destPort() == rule.out_port){
						NewIP = rule.in_ip;
						NewPort = rule.in_port;
						
						Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
								"Inside PAT(udp) for packet applied: changed dst from " + p.getDestIPAddress() + ":" + ((UDP_packet)p).get_destPort() + " to " + NewIP + ":" + NewPort + "."); 
						
						((UDP_packet)rp).setDestPort(NewPort);
						rp.setDestIPAddress(NewIP);
						
						return rp;
					}
				}
			}
		}
		
		String OldIP = "";
		int OldPort = 0;
		/*String NewIP = "";
		int NewPort = 0;*/
		int type = NAT_session.UNKNOWN;
		String proto = "unk";
		int s = -1;
		
		OldIP = p.getDestIPAddress();
		
		if(p instanceof TCP_packet){
			type = NAT_session.TCP;
			OldPort = ((TCP_packet)p).get_destPort();
			proto = "tcp";
		}else if(p instanceof UDP_packet){
			type = NAT_session.UDP;
			OldPort = ((UDP_packet)p).get_destPort();
			proto = "udp";
		}else if(p instanceof ICMP_packet){
			type = NAT_session.ICMP;
			OldPort = ((ICMP_packet)p).UniqueIdentfier;
			proto = "icmp";
		}
		
		for(int i=0; i<dynamic_rules.size(); i++){
			NAT_rule rule = dynamic_rules.get(i);
						
			if(parentNode.getACL().passACL(rule.acl, p)){				
				s = findOutsideSession(type, OldIP, OldPort);
				
				if(s>=0){
					sessions.get(s).last_time = System.currentTimeMillis();
					
					NewPort = sessions.get(s).in_port;
					NewIP = sessions.get(s).in_ip;
					
					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
							"Outside dynamic NAT(" + proto + ") for ip packet applied: changed dst from " + OldIP + ":" + OldPort + " to " + NewIP + ":" + NewPort + ".");
					
					rp.setDestIPAddress(NewIP);
					
					if(p instanceof TCP_packet){
						((TCP_packet)rp).setDestPort(NewPort);
					}else if(p instanceof UDP_packet){
						((UDP_packet)rp).setDestPort(NewPort);
					}
					
					return rp;
				}else{
					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "IP Packet", "Network", 
							"Outside dynamic NAT(" + proto + ") for ip packet failed: no session for " + OldIP + ":" + OldPort +  ".");
				}
			}
		}
		
		return rp;
	}
	
	public NAT_rule getRule(int i, boolean dynamic){
		Vector<NAT_rule> rules = static_rules;
		if(dynamic) rules = dynamic_rules;
		if(i>=0 && i<rules.size()){
			return rules.get(i);
		}
		return null;
	}
	
	public int countRules(boolean dynamic){
		Vector<NAT_rule> rules = static_rules;
		if(dynamic) rules = dynamic_rules;
		return rules.size();
	}
	
	public Pair getPool(String name){
		if(pools.containsKey(name)){
			return pools.get(name);
		}
		return null;
	}

	public void addPool(String name, Pair pool){
		pools.put(name, pool);
	}
	
	public Pair removePool(String name){
		if(pools.containsKey(name)){
			return pools.remove(name);
		}
		return null;
	}
	
	public boolean inPool(String name, String IP){
		if(pools.containsKey(name)){
			Pair ips = pools.get(name);
			
			return IPV4Address.IPEqLower(IP, (String)ips.getFirst())
				&& IPV4Address.IPEqLower((String)ips.getSecond(), IP);
		}
		return false;
	}
	
	public Enumeration<String> getPools(){
		return pools.keys();
	}
	
	public NAT_session getSession(int i){
		if(i>=0 && i<sessions.size()){
			return sessions.get(i);
		}
		return null;
	}
	
	public int countSessions(){
		return sessions.size();
	}
	
	public void clear(){
		static_rules.clear();
		dynamic_rules.clear();
		sessions.clear();
		pools.clear();
	}
}

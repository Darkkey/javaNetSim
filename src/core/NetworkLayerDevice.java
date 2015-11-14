/*
Java Firewall Simulator (jFirewallSim)

Copyright (c) 2004, jFirewallSim development team All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

	- Redistributions of source code must retain the above copyright notice, this list
	  of conditions and the following disclaimer.
	- Redistributions in binary form must reproduce the above copyright notice, this list
	  of conditions and the following disclaimer in the documentation and/or other
	  materials provided with the distribution.
	- Neither the name of the Canberra Institute of Technology nor the names of its
	  contributors may be used to endorse or promote products derived from this software
	  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package core;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import core.protocolsuite.tcp_ip.ARP_packet;
import core.protocolsuite.tcp_ip.ICMP_packet;
import core.protocolsuite.tcp_ip.IP_packet;
import core.protocolsuite.tcp_ip.InvalidIPAddressException;
import core.protocolsuite.tcp_ip.InvalidSubnetMaskException;
import core.protocolsuite.tcp_ip.Route_entry;

/**
* NetworkLayerDevice extends Node. This class adds network layer devices 
* sets IPAddress, defaultgateway, sends a ping and a few more options
* @author luke_hamilton
* @author angela_brown
* @author michael_reith
* @author robert_hulford
* @author bevan_calliess
* @since Oct 9, 2004
* @version v0.20
*/
public abstract class NetworkLayerDevice extends Node {
	
        protected DeviceConfig config = new DeviceConfig(this);
        private AccessListEngine acls = new AccessListEngine(this);
        private NATEngine nat = new  NATEngine(this);
    	Hashtable<Integer, String> Vlans;
    
        
	/**
	 * calls the super class (Node) and passes it inName and inProtocolStack
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @param inName - Node Name eg: PC1
	 * @param inProtocolStackLayers - ProtocolStack Layer
	 * @version v0.20
	 */
	public NetworkLayerDevice(String inName, int inProtocolStackLayers, boolean inOn) {
		super(inName, inProtocolStackLayers, inOn);
        //set default startup-config
        config.working_config = DeviceConfig.STARTUP_CONFIG;
        //config.add("ip telnet 21");
        //end set default startup-config
        config.working_config = DeviceConfig.RUNNING_CONFIG;
        config.load();

		Vlans = new Hashtable<Integer, String>();
	}
        
        @Override
		public void turnOn(){
            super.turnOn();
            initApplications();
            ifacesLinkUP();  // up all before loading config
            config.load();
        }
        
        @Override
		public void turnOff(){
        	acls.clear();
        	nat.clear();
            super.turnOff();
        }
        
        public void initApplications(){
            
        }
	
	/**
	 * This method will get the subnetMask of the Interface that was
	 * passed in
	 * @author bevan_callies
	 * @author robert_hulford
	 * @param inInteface - The Name of the Inteface eg: eth0 
	 * @return The subnetMask 
	 * @version v0.20
	 */
	//TODO This should maybe check the inInterface exists within the Interface hashtable first -luke hamilton
	public String getSubnetMask(String inInterface) {
            if(((NetworkInterface)(NetworkInterfacetable.get(inInterface))).isActive()){
            	return NodeProtocolStack.getSubnetMask(inInterface);
            }else{
                return "Not Applicable";
            }
	}
	
	/**
	 * This method will get the ipaddress of the Interface that was passed in
	 * 
	 * @author luke_hamilton
	 * @param inInteface - The Name of the Inteface eg: eth0 
	 * @return The subnetMask 
	 * @version v0.20

	 */
	//TODO This should maybe check the inInterface exists within the Interface hashtable first -luke hamilton
	public String getIPAddress(String inInterface){
            if(((NetworkInterface)(NetworkInterfacetable.get(inInterface))).isActive() ){
            		return NodeProtocolStack.getIPAddress(inInterface);
            }else{
                return "Not Applicable";
            }     
	}

/**
 * This method will pass in a packet and send it off to the
 * nodeProtocolStack
 * @author angela_brown
 * @author bevan_calliess
 * @param inPacket - A Packet
 * @version v0.20 
 */	
	@Override
	public void receivePacket(Packet inPacket, String inInterface) throws LowLinkException {
		
		if(inPacket instanceof IP_packet && !(inPacket instanceof ARP_packet)){
			try{
				if(this.getNetworkInterface(inInterface).getNAT() == NetworkInterface.INSIDE_NAT){
					// mark packet
					((IP_packet) inPacket).NatInsideMark = true;
					Simulation.addLayerInfo(getClass().getName(), getName(), "IP Packet", "Network", 
							"Marked packet as possible inside NAT candidate.");
				}else if(this.getNetworkInterface(inInterface).getNAT() == NetworkInterface.OUTSIDE_NAT){
					((IP_packet) inPacket).NatOutsideMark = true;
					inPacket = nat.NAT_outside((IP_packet) inPacket);
				}
			}catch(InvalidNetworkInterfaceNameException e){ 
			}
		}
		
		if(NodeProtocolStack != null) 
			NodeProtocolStack.receivePacket(inPacket, inInterface);
	}
	
	public Packet preparePacket(Packet inPacket){
		if(inPacket instanceof IP_packet && !(inPacket instanceof ARP_packet)){
			if(((IP_packet) inPacket).NatInsideMark){
				inPacket = nat.NAT_inside((IP_packet) inPacket);
			}
		}
		
		((IP_packet) inPacket).NatInsideMark = false;
		((IP_packet) inPacket).NatOutsideMark = false;
		
		return inPacket;
	}

	/**
	 * This method will send a ping to the specified Destination
	 * Address passing it up to the NodeProtocolStack
	 * @author angela_brown
	 * @author bevna_calliess
	 * @param inDestIPAddress - The Destination IP Addres eg: 192.168.0.2
	 * @throws CommunicationException
	 * @return ICMP packet
	 * @version v0.20
	 */
	public ICMP_packet sendPing(String inDestIPAddress) throws CommunicationException, LowLinkException{
		return NodeProtocolStack.sendPing(inDestIPAddress);
		
	}
	
	/**
	 * This method returns received ICMP packet with messageID. 
	 * @author QweR
	 * @param messageID - ICMP packet message ID
	 * @return received ICMP packet or null if packet was not received
	 * @version v0.21
	 */
	public ICMP_packet getReceivedICMPPacket(int messageID){
		return NodeProtocolStack.getReceivedICMPPacket(messageID);
	}
	
	/**
	 * This method remove and returns received ICMP packet with messageID. 
	 * @author QweR
	 * @param messageID - ICMP packet message ID
	 * @return received ICMP packet or null if packet was not received
	 * @version v0.21
	 */
	public ICMP_packet removeReceivedICMPPacket(int messageID){
		return NodeProtocolStack.removeReceivedICMPPacket(messageID);
	}

	
	/**
	 * This method will check to see if the Interface key is contained within the 
	 * NetworkInterfacetable (hash table) if so typecast it into a EthernetNetworkInterface Object
	 * and send the packet and Destination MAC address up to the EthernetNetworkInterface layer
	 * @author bevan_calliess
	 * @author angela_brown
	 * @param inDestMACAddress - Destination MAC address eg: AA:A1:BC:34:65
	 * @param inPacket - A Packet
	 * @param inInterfaceKey - The Inteface Key
	 * @throws InvalidNetworkInterfaceNameException
	 * @version v0.20
	 */
	public void sendPacket(String inDestMACAddress, Packet inPacket, String inInterfaceKey) throws InvalidNetworkInterfaceNameException, CommunicationException, LowLinkException{		
		
		if (NetworkInterfacetable.containsKey(inInterfaceKey))
		{
			inPacket = preparePacket(inPacket);
			
			if(NetworkInterfacetable.get(inInterfaceKey) instanceof EthernetNetworkInterface){				
				
				EthernetNetworkInterface temp = (EthernetNetworkInterface)NetworkInterfacetable.get(inInterfaceKey);	
				temp.sendPacket(inPacket,inDestMACAddress);		
			}else{
				sendPacket(inPacket, inInterfaceKey);
			}
		}
		else
		{
			throw new InvalidNetworkInterfaceNameException("Inteface does not exist.");
		}
	}
        
    public void sendPacket(Packet inPacket, String inInterfaceKey) throws InvalidNetworkInterfaceNameException, CommunicationException, LowLinkException{
		if (NetworkInterfacetable.containsKey(inInterfaceKey))
		{
			inPacket = preparePacket(inPacket);
			
			NetworkInterface temp = (NetworkInterface)NetworkInterfacetable.get(inInterfaceKey);	
			temp.sendPacket(inPacket);		
		}
		else
		{
			throw new InvalidNetworkInterfaceNameException("Inteface does not exist.");
		}
	}

	
	/**
	 * This method will get the defaultgateway when called from the NodeProtocolStack
	 * @author angela_brown
	 * @author michael_reith 
	 * @return Defaultgateway
	 * @version v0.20
	 */
	public String getDefaultGateway() {		
		return NodeProtocolStack.getDefaultGateway();
	}

	/**
	 * gets a string array from the arp protocol and returns it
	 * @author robert_hulford
	 * @author bevan_calliess
	 * @return Vector<Vector<String>> - The arp table entries
	 */
	public Vector<Vector<String>> getARPTable() {
		return NodeProtocolStack.getARPTable();
	}

	/**
	 * This method will pass inIterface, incustomeSubnetMask up to the NodeProtocolStack
	 * with the aim to set the CustomSubnetMask
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @param inInterface
	 * @param inCustomSubnetMask
	 * @throws InvalidNetworkInterfaceNameException
	 * @throws InvalidSubnetMaskException
	 * @version v0.20
	 */
	
	public void setCustomSubnetMask(String inInterface,	String inCustomSubnetMask)throws InvalidNetworkInterfaceNameException,InvalidSubnetMaskException	{
		if(NetworkInterfacetable.containsKey(inInterface)){
			NodeProtocolStack.setCustomSubnetMask(inInterface, inCustomSubnetMask);
		}else{
			throw new InvalidNetworkInterfaceNameException("Interface does not exsist");
		}		
	}

	/**
	 * This method will call the setDefaultgatway method within the NodeProtocolStack passing it 
	 * the inGatewayIPAddress
	 * @author angela_brown
	 * @author michael_reith
	 * @param inGatewayIPAddress
	 * @throws InvalidNodeNameException
	 * @throws InvalidDefaultGatewayException
	 * @version v0.20
	 */
	
	public void setDefaultGateway(String inGatewayIPAddress) throws InvalidNodeNameException, InvalidDefaultGatewayException{
		NodeProtocolStack.setDefaultGateway(inGatewayIPAddress);		
	}

	/**
	* This method will set the ip address for a network interface.
	* It will test if the inInterface exists and returns the MAC address of the netwokInterface set.
	* @param inInterface - The Interface name eg: eth0
	* @param inIPAddress - The IP address eg: 192.168.0.2
	* @return macAddress - The MAC address on the Interface card that the IP address was set for.
	* @throws InvalidNetworkInterfaceNameException
	* @throws InvalidIPAddressException
	*/

	public String setIPAddress(String inInterface, String inIPAddress) throws InvalidNetworkInterfaceNameException,InvalidIPAddressException {
		String macAddress = null;
		if (NetworkInterfacetable.containsKey(inInterface)) {			
				NodeProtocolStack.setIPAddress(inInterface, inIPAddress);
				if(NetworkInterfacetable.get(inInterface) instanceof EthernetNetworkInterface){
                                    EthernetNetworkInterface tempNic =
					(EthernetNetworkInterface) NetworkInterfacetable.get(
						inInterface);
                                    macAddress = tempNic.getMACAddress();
                                }
				
		} else {
			throw new InvalidNetworkInterfaceNameException("Interface does not exist");
		}
		return macAddress;
	}
	
        /**
	* This method will set the mac address for a network interface.
	* @param inInterface - The Interface name eg: eth0
	* @param macAddress - The MAC address on the Interface card was set.
	* @throws InvalidNetworkInterfaceNameException
	*/

	public void setMACAddress(String inInterface, String inMACAddress) throws InvalidNetworkInterfaceNameException {
		if (NetworkInterfacetable.containsKey(inInterface)) {			
				EthernetNetworkInterface tempNic =
					(EthernetNetworkInterface) NetworkInterfacetable.get(
						inInterface);
				tempNic.setMacAddress(inMACAddress);
				
		} else {
			throw new InvalidNetworkInterfaceNameException("Interface does not exist");
		}
	}
        
	/**
	* This method will call the protocol stacks' add to ARP method
	* passing the ip address and the mac address
	* @author bevan_calliess
	* @author robert_hulford
	* @param inMACAddress - The MAC Address eg: ab:ab:ab:ab:ab:ab
	* @param inIPAddress - The IPAddress eg: 192.168.0.2
	* @version v0.20
	*/
	public void addToARP(String inIPAddress, String inMACAddress){
		NodeProtocolStack.addToARP(inIPAddress, inMACAddress);
	}
        
	/**
	 * ARP functions...
         * Shall be documented as well.
	 * @author Key
	 * @version v0.21
	 */
        public void addToARPStatic(String inIPAddress, String inMACAddress){
		NodeProtocolStack.addToARPStatic(inIPAddress, inMACAddress);
	}
        
    public void removeARP(String inIPAddress){
		NodeProtocolStack.removeARP(inIPAddress);
	}
         
    public void setTCPIPSettings(String inInterface, String inIP, String inMask, String inGateway){
		getConfig().executeCommand("interface "+inInterface+" ip address "+ inIP + " " + inMask);
		
		if(inGateway != null)
			getConfig().executeCommand("ip route 0.0.0.0 0.0.0.0 " + inGateway + " " + inInterface);
		
        getConfig().executeCommand("write mem");
    }
    
    public void setTCPIPSettings(String inGateway){
		getConfig().executeCommand("ip route 0.0.0.0 0.0.0.0 " + inGateway + " eth0");
		
        getConfig().executeCommand("write mem");
    }
    
    public void setTCPIPSettings(String inInterface, String inIP, String inMask){
		getConfig().executeCommand("interface "+inInterface+" ip address "+ inIP + " " + inMask);
		
        getConfig().executeCommand("write mem");
    }
    
	@Override
	public void execCmd(String cmd){
		getConfig().executeCommand(cmd);
	}
        
        /**
	 * Routing tables functions... Use carefully!
         * Shall be documented as well.
	 * @author Key
	 * @version v0.21
	 */
        
        public void addRoute(Route_entry r){
                NodeProtocolStack.addRoute(r);
        }
        
        public void removeRoute(String destIP){
                NodeProtocolStack.removeRoute(destIP);
        }
           
        public String[] getRouteTableEntries(){
                return NodeProtocolStack.getRouteTableEntries();
        }
        
        public Route_entry getRouteTableEntry(String destIP){
                return NodeProtocolStack.getRouteTableEntry(destIP);
        }
        
        public String getFormattedRouteTable(){
        	String routes[] = getRouteTableEntries();
            String s = "";
            s+="Codes: C - connected, S - static, R - RIP, \nB - BGP, O - OSPF, * - candidate default\n\n";
            String type = "";
            String time = " ";
            
            for(int i=0; i<routes.length - 1; i++){
                   Route_entry r = getRouteTableEntry(routes[i]);
                   
                   if(r.Type == 0){
                	   type = "S";
                   }else if(r.Type == 1){
                	   type = "R";
                	   time = ", " + Long.valueOf((System.currentTimeMillis() - r.createdTime)/1000).toString() + " s., ";
                   }
                   
                   if(r.genMask == "0.0.0.0" || routes[i].contains("default") || routes[i] == "0.0.0.0")
                	   type+="*";
                   else
                	   type+=" ";
                                 
                   s+=type + "  " + routes[i]  + "/" + r.genMask + "[" + r.metric + "]" + time + " via " + r.gateway + " (" + r.iFace + ")\n";
            }
            
            try{
            	Object ifaces[] = getAllInterfaces();
            	
            	for(int i = 0; i<ifaces.length; i++){
            			String dcIP = getIPAddress((String)ifaces[i]);
            			String dcMask = getSubnetMask((String)ifaces[i]);            		
            			
            			if(dcIP!=null && dcIP.length() > 7 && dcMask.length() > 7 && !dcIP.contains("No"))
            				s+="C " + "  " + dcIP + "/" + dcMask + " is directly connected, " + (String)ifaces[i] + "\n";
            	}
            	
            }catch(Exception e){}
            
            return s;
        }
        
        public DeviceConfig getConfig(){
            return config;
        }
        
        public AccessListEngine getACL(){
            return acls;
        }

        public NATEngine getNAT(){
            return nat;
        }
        
        
        public String[] getFormattedARPTable() 
    	{
        	String[] arpEntries;
   			Vector<Vector<String>> ArpTable = getARPTable();
            arpEntries = new String[ArpTable.size()+1];
            
            if(ArpTable.size()>0){
                arpEntries[0] = "Internet Address\tPhysical Address\t\tType\n";
                for(int i=0;i<ArpTable.size();i++)
                {
                    arpEntries[i+1] = ArpTable.get(i).get(0) + "\t\t" + ArpTable.get(i).get(1) + "\t\t" + ArpTable.get(i).get(2) + "\n";
                }
            }else{
                arpEntries[0] = "No ARP Entries Found\n";
            }
    			
    		return arpEntries;
    	}
        
        public void addStaticARP(String inIPAddress, String inMACAddress)
    	{
    	    NetworkLayerDevice tempNet = this;
 
    	    tempNet.getConfig().executeCommand("arp "+inIPAddress+" "+inMACAddress);
            tempNet.getConfig().executeCommand("write mem");    			
    		
    	}
        
        public void deleteARPRecord(String inIPAddress)
    	{
    	    NetworkLayerDevice tempNet = this;
    		
    	    tempNet.getConfig().executeCommand("no arp "+inIPAddress);
            tempNet.getConfig().executeCommand("write mem");
    		
    	}

    	public Enumeration<Integer> getVlans(){
    		return Vlans.keys();
    	}
    	
    	public void addVlan(int v, String name){
    		Vlans.put(v, name);
    	}
    	
    	public void removeVlan(int v){
    		Vlans.remove(v);
    	}
    	
    	public void clearVlan(){
    		Vlans.clear();
    	}

    	public String getVlanName(int v){
    		if(Vlans.containsKey(v)){
    			return Vlans.get(v);
    		}
    		else if(v == 1){
    			return "default";
    		}
    		return null;
    	}
    	
    	public void setVlanName(int v, String name){
    		Vlans.remove(v);
    		Vlans.put(v, name);
    	}
    	
        @Override
		public String getAllData(){
        	String file = "";
        	file += "vlan.dat\n";
        	file += "1\n";
        	
        	Enumeration<Integer> it;
        	Integer v;
    		
    		it = Vlans.keys();

    		while (it.hasMoreElements()) {
    			v = it.nextElement();
    			
    			file = file + v + "," + Vlans.get(v) + "|";
    		}
    		
    		file += "\n";
        	
        	return file;
        }
        
        @Override
		public void loadDataFile(String name, int lines, String Data){
        	
        	if(name.equals("vlan.dat")){
        		Vlans.clear();
        		
        		String[] vlans = Data.split("\\|");
        		
        		for(int i = 0; i<vlans.length; i++){
        			if(vlans[i].length() > 1){
        				String[] vlan = vlans[i].split(",");
        				if(vlan.length>1)
        					Vlans.put(Integer.valueOf(vlan[0]), vlan[1]);
        				else
        					Vlans.put(Integer.valueOf(vlan[0]), "");
        			}
        		}
        	}
        	
        	return;
        }
}//EOF

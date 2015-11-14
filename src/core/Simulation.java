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
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class Simulation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3851012405328883446L;

	/**
	 * @link aggregation <{core.Link}>
	 * @directed directed
	 * @supplierCardinality 0..*
	 * @version v0.20
	 **/
	/*
	 * This is the container for all the links within the system.
	 * This is a much more efficient way to acess objects and less
	 * coding for core simulation developer.
	 */
	private java.util.Hashtable linkTable = null;

	/**
	 * @link aggregation <{core.Node}>
	 * @directed directed
	 * @supplierCardinality 0..*
	 * @version v0.20
	 **/

	private java.util.Hashtable nodeTable = null;
	private static LayerInfoHandler Info = new LayerInfoHandler();
	private static int PROTOCOL_TYPE;
	
	private java.util.Hashtable<java.lang.Long, WiFiPort> wifiMedium = null;

	public static long UIDGen;
	
	public static Simulation Sim = null;
	
        
	// A script is just a Vector containing Strings right now
	// Each String being a representation of a method to be run such as sendAppData()
	// private Vector script;
	// This object stores LayerInfo objects for the GUI to retrieve after the Simulation has run 
	//private LayerInfoHandler layerInfoHandler;
	// Indicates whether send methods should run sequentially or simultaneously
	//private boolean sequentialSends;

	/** Construct a new Simulation 
	 * @author bevan_calliess
	 * @author luke_hamilton
	 * @author angela_brown
	 * @author robert_hulford
	 * @author michale_reith
	 * @param inProtocoltype The Protocol Type eg: 7
	 * @version v0.20
	 **/
	public Simulation(int inProtocoltype) {
		//sequentialSends = false;
		//script = new Vector();
		//layerInfoHandler = new LayerInfoHandler();
		
		if(Simulation.Sim!=null){
			Simulation.Sim = null;
		}
		
		setProtocolType(inProtocoltype);
		linkTable = new Hashtable();
		nodeTable = new Hashtable();
		wifiMedium = new Hashtable<java.lang.Long, WiFiPort>();
		
		Simulation.UIDGen = 0;
		
		Simulation.Sim = this;
     }
	
	public void clear(){
		Simulation.Sim = null;
	}
	
	public void connectWireless(WiFiPort wp){
		wifiMedium.put(java.lang.Long.valueOf(wp.getID()), wp);		
	}
	
	public void disconnectWireless(WiFiPort wp){
		wifiMedium.remove(java.lang.Long.valueOf(wp.getID()));		
	}
	
	public void sendWirelessPacket(WiFiPort wp, Packet p, int channel) throws LowLinkException{
		if(!wp.isUP() || !wp.isOn() || !(channel > 0 && channel < 16)) return;
		
		Enumeration e = wifiMedium.elements();
		while(e.hasMoreElements())
		{
			WiFiPort wfp = (WiFiPort)e.nextElement();
			if(wfp.isUP() && wfp.isOn() && wfp.getChannel() == channel && wfp.getUID() != wp.getUID()) 
				wfp.receivePacket(p);
		}
	}
	
	/**
	 * This method is called from the TEXTUI or GUI. Checks to see if inNodeName is an instance of networkLayerDevice, 
	 * if so, move up to the NetworkLayerDevice and to get the subnet mask. Else it will throw exceptions.
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @param inNodeName - The Name of the Node eg: pc1
	 * @param inInterface - The Interface Name eg: eth0
	 * @return SubnetMask
	 * @throws InvalidNetworkLayerDeviceException
	 * @throws InvalidNodeNameException
	 * @version v0.20
	 **/
	//TODO I think this should also throw an invalidNetInterfaceNameException. -Luke hamilton
	/*public String getSubnetMask(String inNodeName, String inInterface)throws InvalidNetworkLayerDeviceException, InvalidNodeNameException {
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice){
				NetworkLayerDevice tempNode = (NetworkLayerDevice)nodeTable.get(inNodeName);
				return tempNode.getSubnetMask(inInterface);
			}
				throw new InvalidNetworkLayerDeviceException("This node is not a network layered device.");
		}
			throw new InvalidNodeNameException("Node does not exist.");
	}*/
	
	
   /*     public core.protocolsuite.tcp_ip.ProtocolStack getTCPProtocolStack(String inNodeName) {
            Node temp = (Node)nodeTable.get(inNodeName);
            return (core.protocolsuite.tcp_ip.ProtocolStack)temp.NodeProtocolStack;
        }*/
	/**
	 * This method is called from the GUI. Checks to see if inNodeName is an instance of networkLayerDevice, 
	 * if so, move up to the NetworkLayerDevice and then returns the ip address of the interface passed in. Else it will throw exceptions.
	 * @author luke_hamilton
	 * @param inNodeName - The Name of the Node eg: pc1
	 * @param inInterface - The Interface Name eg: eth0
	 * @return SubnetMask
	 * @throws InvalidNetworkLayerDeviceException
	 * @throws InvalidNodeNameException
	 * @version v0.20
	 **/	
	/*public String getIpAddressForInterface(String inNodeName, String inInterface) throws InvalidNetworkInterfaceNameException, InvalidNodeNameException, InvalidNetworkLayerDeviceException {
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice){
				NetworkLayerDevice tempNode = (NetworkLayerDevice)nodeTable.get(inNodeName);
				 return tempNode.getIPAddress(inInterface);
			}
			throw new InvalidNetworkLayerDeviceException("This node is not a network layered device.");
		}
			throw new InvalidNodeNameException("Node does not exist.");
	}*/

        /**
	 * This method is called from the GUI. Checks to see if inNodeName is an instance of networkLayerDevice, 
	 * if so, move up to the NetworkLayerDevice and then returns the mac address of the interface passed in. Else it will throw exceptions.
	 * @author key
	 * @param inNodeName - The Name of the Node eg: pc1
	 * @param inInterface - The Interface Name eg: eth0
	 * @return MAC ADDRESS
	 * @throws InvalidNetworkLayerDeviceException
	 * @throws InvalidNodeNameException
	 * @version v0.20
	 **/	
	/*public String getMacAddressForInterface(String inNodeName, String inInterface) throws InvalidNetworkInterfaceNameException, InvalidNodeNameException, InvalidNetworkLayerDeviceException {
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice){
				NetworkLayerDevice tempNode = (NetworkLayerDevice)nodeTable.get(inNodeName);
				 return tempNode.getMACAddress(inInterface);
			}
			throw new InvalidNetworkLayerDeviceException("This node is not a network layered device.");
		}
			throw new InvalidNodeNameException("Node does not exist.");
	}*/
        
	/**
	 * This method is called from TEXTUI or GUI. If inNodeName is an instance of node pass it up to the 
	 * NetworkLayerDevice to send ping. Else it will throw exceptions. 
	 * @author bevan_calliess
	 * @author angela_brown
	 * @param nodeName - The Name of the Node eg: pc2
	 * @param inDestIPAddress - The destination IP Address eg:192.168.0.1
	 * @throws CommunicationException
	 * @throws InvalidNetworkLayerDeviceException
	 * @throws InvalidNodeNameException
	 * @version v0.20
	 **/
	
	public void sendPing(String nodeName, String inDestIPAddress) throws CommunicationException, InvalidNetworkLayerDeviceException, InvalidNodeNameException, LowLinkException {
		if (nodeTable.containsKey(nodeName)){
			
			Node temp = (Node)nodeTable.get(nodeName);
			
			if (temp instanceof  NetworkLayerDevice){
				NetworkLayerDevice NLD = (NetworkLayerDevice)temp;
				NLD.sendPing(inDestIPAddress);				
			}else{
				throw new InvalidNetworkLayerDeviceException("");	
			}
		}else
			throw new InvalidNodeNameException("");	
	}

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! comment && doc this
        
        public Node getNode(String inNodeName){
            return (Node)nodeTable.get(inNodeName);
        }
        
	/**
	 * This method is called from the TEXTUI or GUI. If inNodeName is an instance of Node pass it up the 
	 * NetworkLayerDevice to get the defaultgateway of the node. Else it will throw exceptions.
	 * @author angela_brown
	 * @author michael_reith
	 * @param inNodeName - The Name of the Node eg: pc2
	 * @return getdefaultgateway
	 * @throws InvalidNodeNameException
	 * @throws InvalidNetworkLayerDeviceException
	 * @version v0.20
	 **/

	/*public String getDefaultGateway(String inNodeName)throws InvalidNodeNameException, InvalidNetworkLayerDeviceException {
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice){
				NetworkLayerDevice t = (NetworkLayerDevice)nodeTable.get(inNodeName);
				return t.getDefaultGateway();
			}
			throw new InvalidNetworkLayerDeviceException("This node is not a network layered device.");
		}
		throw new InvalidNodeNameException("Node does not exist.");		
	}*/

	
	/**
	 * This method is called from the TEXTUI or GUI. if inNodeName is contained within the hash table (nodeTable)
	 * type cast it to a node then check to see if it an instance of a networkLayerDevice. If so, pass it up to the 
	 * networkLayerDevice to set the CustomSubnetMask. 
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @param inNodeName - The Name of the Node eg: pc1
	 * @param inInterface - The Interface Name eg: eth0
	 * @param inCustomSubnetMask - The Custom Subnet Mask eg: 255.255.252.0
	 * @throws InvalidNodeNameException
	 * @throws InvalidNetworkInterfaceNameException
	 * @throws InvalidSubnetMaskException
	 * @version v0.20
	 **/
	/*public void setCustomSubnetMask(String inNodeName,String inInterface,String inCustomSubnetMask)throws InvalidNetworkLayerDeviceException, InvalidNodeNameException, InvalidNetworkInterfaceNameException,InvalidSubnetMaskException	{
		if (nodeTable.containsKey(inNodeName)){
//			try{
				Node temp = (Node)nodeTable.get(inNodeName);
				if (temp instanceof NetworkLayerDevice)//test if device is a network layer device
				{
				NetworkLayerDevice nld = (NetworkLayerDevice)temp;
				//nld.setCustomSubnetMask(inInterface, inCustomSubnetMask);
                
				}
				else
				{
					throw new InvalidNetworkLayerDeviceException("This device is not a network layered device");
				}
//			}catch (InvalidNetworkInterfaceNameException e){
//				throw e;
//			}			
		}else{
			throw new InvalidNodeNameException("Node does not exist");
		}		
	}*/
	
	/**
	 * This method passes in two strings (NodeName and IPAddress), it then
	 * checks the details of the input (inNodeName) to see if it is within a hash table.  
	 * If it is not an error message is produced to the screen. If there is no error 
	 * then the setdefaultgateway method is called in the NetworkLayerDevice.java 
	 * @author angela_brown
	 * @author michael_reith
	 * @param inNodeName - The Name of the Node eg: Pc1
	 * @param inGatewayIPAddress - The Gateway address eg: 192.0.1.2
	 * @throws InvalidNodeNameException
	 * @throws InvalidDefaultGatewayException
	 * @throws InvalidNetworkLayerDeviceException
	 * @version v0.20
	 **/
	/*public void setDefaultGateway(String inNodeName, String inGatewayIPAddress) throws InvalidNodeNameException, InvalidDefaultGatewayException,InvalidNetworkLayerDeviceException{		
				
		if(nodeTable.containsKey(inNodeName))
		{
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice)
			{	//test if device is a network layer device
				NetworkLayerDevice t = (NetworkLayerDevice)temp;
				//t.setDefaultGateway(inGatewayIPAddress);
                t.
                t.getConfig().executeCommand("write mem");
			}
			else
			{ 
				throw new InvalidNetworkLayerDeviceException("This device is not a network layered device");		
			}					
		}
		else
		{
			throw new InvalidNodeNameException("Node does not exist");
		}
	}*/


	
	
	/**
	 * Sets the Protocol Type to a global variable
	 * @author angela_brown
	 * @author bevan_calliess
	 * @param inProtocolType - The Protocol Type eg: 8
	 * @version v0.20
	 **/
	private void setProtocolType(int inProtocolType) {
		Simulation.PROTOCOL_TYPE = inProtocolType;
	}

	/**
	 * Get the Protocol type from the global variable
	 * @author angela_brown
	 * @author bevan_calliess
	 * @return PROTOCOL_TYPE
	 * @version v0.20
	 **/
	public static int getProtocolType() {
		return Simulation.PROTOCOL_TYPE;
	}

	/**
	 * This method will set the IP Address of a Node interface.
	 * This method needs to test if the inNodeName is valid.
	 * Once the Address is set the setIPAddress method of the Node will 
//	 * return the MAC address of the interface to be used to set Static ARP.
	 * Please note that static can still be implemented but we are currently using
	 * Dynamic ARP. 
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @param inNodeName - The Name of the Node eg: pc1
	 * @param inNodeInterface - The Interface name eg: eth0
	 * @param inIPAddress - The IP address eg: 192.168.0.2
	 * @throws InvalidNetworkLayerDeviceException
	 * @throws InvalidNodeNameException
	 * @version v0.20
	 **/
	/*public void setIPAddress(String inNodeName, String inNodeInterface, String inIPAddress) throws InvalidNodeNameException, InvalidNetworkInterfaceNameException, InvalidIPAddressException,InvalidNetworkLayerDeviceException{
		String macAddress;
		
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice){	//test if device is a network layer device
				NetworkLayerDevice t = (NetworkLayerDevice)temp;
	            //t.setIPAddress(inNodeInterface,inIPAddress);
	            t.getConfig().executeCommand("interface "+inNodeInterface+" ip address "+inIPAddress+" "+t.getSubnetMask(inNodeInterface));
	            t.getConfig().executeCommand("write mem");
	            macAddress = t.getMACAddress(inNodeInterface);
				updateARP(inIPAddress, macAddress, inNodeName);
                                //t.addToARP(inIPAddress, macAddress);
			}else{
				throw new InvalidNetworkLayerDeviceException("This device is not a network layered device");		
			}					
		}else{
			throw new InvalidNodeNameException("Node does not exist");
		}
	}*/
        
        /**
	 * This method will set the MAC Address of a Node interface.
	 * This method needs to test if the inNodeName is valid.
	 * Please note that static can still be implemented but we are currently using
	 * Dynamic ARP. 
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @param inNodeName - The Name of the Node eg: pc1
	 * @param inNodeInterface - The Interface name eg: eth0
	 * @param inMACAddress - The MAC address
	 * @throws InvalidNetworkLayerDeviceException
	 * @throws InvalidNodeNameException
	 * @version v0.20
	 **/
	/*public void setMACAddress(String inNodeName, String inNodeInterface, String inMACAddress) throws InvalidNodeNameException, InvalidNetworkInterfaceNameException, InvalidNetworkLayerDeviceException{
				
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice){	//test if device is a network layer device
				NetworkLayerDevice t = (NetworkLayerDevice)temp;
				t.setMACAddress(inNodeInterface,inMACAddress);
				//updateARP(inIPAddress, macAddress, inNodeName);
                                //t.addToARP(inIPAddress, macAddress);
			}else{
				throw new InvalidNetworkLayerDeviceException("This device is not a network layered device");		
			}					
		}else{
			throw new InvalidNodeNameException("Node does not exist");
		}
	}*/
        
	/**
	 * This method will loop through the node hash table, checking whether its
	 * an network layer device. if the test passes it calls the addToARP method.
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @param inIPAddress - The IP address eg:192.168.0.1
	 * @param inMACAddress - The MAC address eg: 52:6B:88:8A:48:B9
	 * @param inSkipName - The node name eg: pc1	 
	 * @version v0.20
	 **/
	/*public void updateARP(String inIPAddress, String inMACAddress, String inSkipName)
	{
	    Enumeration e = nodeTable.elements();
		while(e.hasMoreElements())
		{
			Node temp = (Node)e.nextElement();
			if (temp instanceof NetworkLayerDevice)
			{
				if(!temp.getName().equals(inSkipName))
				{
					NetworkLayerDevice tempNet = (NetworkLayerDevice)temp;
					tempNet.addToARP(inIPAddress, inMACAddress);
				}
				
			}
			
		} 
	}*/
        
   
        
     /*   */
        
	/**
	 * This method will delete a link from within the simulation if it is contained with the
	 * hash table (linkTable). 
	 * and reset any connect interface.
	 * @author luke_hamilton
	 * @param inLinkName - The name of the link eg: Link1
	 * @throws InvalidLinkNameException
	 * @version v0.20
	 **/
	public void deleteLink(String inLinkName) throws InvalidLinkNameException{
		if(linkTable.containsKey(inLinkName)){
			Link deletedLink = (Link)linkTable.get(inLinkName);
			deletedLink.disconnectLink();
			linkTable.remove(inLinkName);	
		}else{
			throw new InvalidLinkNameException("Link does not exist");
		}
				
	}

        /**
	 * Get link probability.
	 * @author Key
	 * @param inLinkName - The name of the link eg: Link1
	 * @throws InvalidLinkNameException
	 * @version v0.20
	 **/
	public double GetLinkProb(String inLinkName) throws InvalidLinkNameException{
		if(linkTable.containsKey(inLinkName)){
			Link lnk = (Link)linkTable.get(inLinkName);                   
			return lnk.getSC();
		}else{
			throw new InvalidLinkNameException("Link does not exist");
              	}
				
	}
        
        /**
	 * Get link probability.
	 * @author Key
	 * @param inLinkName - The name of the link eg: Link1
         * @param SC - sieve coefficient: *TODO* - description 
	 * @throws InvalidLinkNameException
	 * @version v0.20
	 **/
	public void SetLinkProb(String inLinkName, double SC) throws InvalidLinkNameException{
		if(linkTable.containsKey(inLinkName)){
			((Link)linkTable.get(inLinkName)).setSC(SC);
		}else{
			throw new InvalidLinkNameException("Link does not exist");
              	}
				
	}
        
        public void addConsoleLink(String inLinkName,String inFirstNodeName,String inFirstNodeInterface,String inSecondNodeName,String inSecondNodeInterface)
		throws InvalidLinkNameException, InvalidLinkConnectionException, InvalidNetworkInterfaceNameException, InvalidNodeNameException {

		if (!linkTable.containsKey(inLinkName)) {
					
			//test if node's exist within hashtable
			if(nodeTable.containsKey(inFirstNodeName) && nodeTable.containsKey(inSecondNodeName)){
				Node tempNode1 = (Node) nodeTable.get(inFirstNodeName);
				Node tempNode2 = (Node) nodeTable.get(inSecondNodeName);
				
				//Return NetworkInterface from Node objects		
			  	NetworkInterface interface1 = tempNode1.getNetworkInterface(inFirstNodeInterface);
				NetworkInterface interface2 = tempNode2.getNetworkInterface(inSecondNodeInterface);
			
				//Create link and add it to hashtable
				linkTable.put(inLinkName,new ConsoleLink(inLinkName, interface1, interface2));				
			}else{
				throw new InvalidNodeNameException("Invalid node name");
			}					
		} else
			throw new InvalidLinkNameException("Link already exists with same name.");
	}
        
         public void addSerialLink(String inLinkName,String inFirstNodeName,String inFirstNodeInterface,String inSecondNodeName,String inSecondNodeInterface)
		throws InvalidLinkNameException, InvalidLinkConnectionException, InvalidNetworkInterfaceNameException, InvalidNodeNameException {

		if (!linkTable.containsKey(inLinkName)) {
					
			//test if node's exist within hashtable
			if(nodeTable.containsKey(inFirstNodeName) && nodeTable.containsKey(inSecondNodeName)){
				Node tempNode1 = (Node) nodeTable.get(inFirstNodeName);
				Node tempNode2 = (Node) nodeTable.get(inSecondNodeName);
				
				//Return NetworkInterface from Node objects		
			  	NetworkInterface interface1 = tempNode1.getNetworkInterface(inFirstNodeInterface);
				NetworkInterface interface2 = tempNode2.getNetworkInterface(inSecondNodeInterface);
			
				//Create link and add it to hashtable
				linkTable.put(inLinkName,new SerialLink(inLinkName, interface1, interface2));				
			}else{
				throw new InvalidNodeNameException("Invalid node name");
			}					
		} else
			throw new InvalidLinkNameException("Link already exists with same name.");
	}
        
	/**
	 * This method checks to see if inLinkName is contained within the has table(linkTable).If so,
	 * check to see if inFirstNodeName is contained within hash table (nodeTable). If so, typecast inFirstNodeName
	 * and SecondNodeName to Nodes, inFirstNodeInterface and inSecondNodeInterface to Interfaces and put them into
	 * the hash table creating a ethernetLink and joining it's two interfaces. Else throw excpetions.
	 * @author luke_hamilton
 	 * @param inLinkName - The Name of the Link eg: link1
	 * @param inFirstNodeName - The Name of the first Node eg: pc1
	 * @param inFirstNodeInterface - Name of the first Node's interface eg: eth0
	 * @param inSecondNodeName - Name of the second Node eg: router1
	 * @param inSecondNodeInterface - Name of the second Node's interface eg: eth4
	 * @throws InvalidLinkNameException If simulation already contain a link with the same name
	 * @version v0.20
	 **/
	public void addEthernetLink(String inLinkName,String inFirstNodeName,String inFirstNodeInterface,String inSecondNodeName,String inSecondNodeInterface, String sieveCoeff )
		throws InvalidLinkNameException, InvalidLinkConnectionException, InvalidNetworkInterfaceNameException, InvalidNodeNameException {

		if (!linkTable.containsKey(inLinkName)) {
					
			//test if node's exist within hashtable
			if(nodeTable.containsKey(inFirstNodeName) && nodeTable.containsKey(inSecondNodeName)){
				Node tempNode1 = (Node) nodeTable.get(inFirstNodeName);
				Node tempNode2 = (Node) nodeTable.get(inSecondNodeName);
				
				//Return NetworkInterface from Node objects		
			  	NetworkInterface interface1 = tempNode1.getNetworkInterface(inFirstNodeInterface);
				NetworkInterface interface2 = tempNode2.getNetworkInterface(inSecondNodeInterface);
			
				//Create link and add it to hashtable
                                if(interface1 instanceof FiberEthernetNetworkInterface){
                                    linkTable.put(inLinkName,new FiberEthernetLink(inLinkName, interface1, interface2, Double.valueOf(sieveCoeff).doubleValue()));				
                                }else{
                                    linkTable.put(inLinkName,new EthernetLink(inLinkName, interface1, interface2, Double.valueOf(sieveCoeff).doubleValue()));				
                                }
			}else{
				throw new InvalidNodeNameException("Invalid node name");
			}					
		} else
			throw new InvalidLinkNameException("Link already exists with same name.");
	}

        public void addEthernetLink(String inLinkName,String inFirstNodeName,String inFirstNodeInterface,String inSecondNodeName,String inSecondNodeInterface)
		throws InvalidLinkNameException, InvalidLinkConnectionException, InvalidNetworkInterfaceNameException, InvalidNodeNameException {

		if (!linkTable.containsKey(inLinkName)) {
					
			//test if node's exist within hashtable
			if(nodeTable.containsKey(inFirstNodeName) && nodeTable.containsKey(inSecondNodeName)){
				Node tempNode1 = (Node) nodeTable.get(inFirstNodeName);
				Node tempNode2 = (Node) nodeTable.get(inSecondNodeName);
				
				//Return NetworkInterface from Node objects		
			  	NetworkInterface interface1 = tempNode1.getNetworkInterface(inFirstNodeInterface);
				NetworkInterface interface2 = tempNode2.getNetworkInterface(inSecondNodeInterface);
			
				//Create link and add it to hashtable
                                if(interface1 instanceof FiberEthernetNetworkInterface){
                                    linkTable.put(inLinkName,new FiberEthernetLink(inLinkName, interface1, interface2));				
                                }else{    
                                    linkTable.put(inLinkName,new EthernetLink(inLinkName, interface1, interface2));				
                                }
			}else{
				throw new InvalidNodeNameException("Invalid node name");
			}					
		} else
			throw new InvalidLinkNameException("Link already exists with same name.");
	}
        
	/**
	* This method checks to see if the hash Table (nodeTable) is empty. If not 
	* displays the nodes in the node list via System.out.println() calls.
	* <P>This method is useful for a CLI or for debugging.</P>
	* @author luke_hamilton	
	* @version v0.20
	**/
	public void displayNodes() {
		if(nodeTable.isEmpty()){
			System.out.println("There are currently no nodes in the simulation");	
		}else{		
			Node tempNode;
			String str;
			Enumeration keys = nodeTable.keys();
			System.out.println("Node within Current Simulation:");
			while (keys.hasMoreElements()) {
				str = (String) keys.nextElement();
				tempNode = (Node) nodeTable.get(str);
				System.out.println(tempNode.getName());
				tempNode.displayInterfaces();
				System.out.println();
			}
		}
	}
        
       	/**
	 * This method is for display information about a paritular node 
	 * within the system that are contained within the hash table (nodeTable)
	 * <P>This method is useful for a CLI or for debugging.</P>
	 * @author luke_hamilton
	 * @param inNodeName - The Node Name eg: pc1
	 * @version v0.20
	 **/
	public void displayNode(String inNodeName)throws InvalidNodeNameException {
		if(nodeTable.containsKey(inNodeName)){
			Node tempNode = (Node) nodeTable.get(inNodeName);
			System.out.println("Name: " + tempNode.getName());
			tempNode.displayInterfaces();	
		}else
			throw new InvalidNodeNameException("Node does not exist within the simulation");
	}

	/**
	 * This method will return true if inNodeName exist within
	 * the Simulation. This method will mainly be called from TextUI
	 * @author luke_hamilton
	 * @param inNodeName - The Name of the Node eg: pc1
	 * @return True if Node is contained within the hash Table (nodeTable), False 
	 * if the Node is not contained in the hash Table
	 * @version v0.20
	 **/
	public boolean containsNode(String inNodeName) {
		if (nodeTable.containsKey(inNodeName)) {
			return true;
		}
			return false;
	}
	
         /**
	 * Functions for control hubs.
         * Shall be documented as well.
	 * @author Key
	 * @version v0.21
	 */
        
    /*    public int getState(String inNodeName) {
		if (nodeTable.containsKey(inNodeName)) {
			      return ((Node)nodeTable.get(inNodeName)).getState();
		}
		return -1;
	}
        
        public String getCache(String inNodeName) {
		if (nodeTable.containsKey(inNodeName)) {
			      return ((Switch)((Node)nodeTable.get(inNodeName))).getCache();
		}
		return "";
	}
        
        public void Reset(String inNodeName) {
		if (nodeTable.containsKey(inNodeName)) {
			  ((Node)nodeTable.get(inNodeName)).Reset();
		}		
	}*/
        
        
        /**
	 * Routing tables functions... Use carefully!
         * Shall be documented as well.
	 * @author Key
	 * @version v0.21
	 */
        
       /* public void addRoute(String inNodeName,Route_entry r){
            if (nodeTable.containsKey(inNodeName)) {
			  ((NetworkLayerDevice)nodeTable.get(inNodeName)).addRoute(r);
            }
        }
        
        public void removeRoute(String inNodeName,String destIP){
            if (nodeTable.containsKey(inNodeName)) {
                      ((NetworkLayerDevice)nodeTable.get(inNodeName)).removeRoute(destIP);
            }
        }
               
        public String[] getRouteTableEntries(String inNodeName){
            if (nodeTable.containsKey(inNodeName)) {
                     return ((NetworkLayerDevice)nodeTable.get(inNodeName)).getRouteTableEntries();
            }
            return null;
        }
        
        public Route_entry getRouteEntry(String inNodeName,String destIP){
            if (nodeTable.containsKey(inNodeName)) {
                       return ((NetworkLayerDevice)nodeTable.get(inNodeName)).getRouteTableEntry(destIP);
            }                
            return null;
        }*/
              
	/**
	 * This method will return true if inLinkName exist within
	 * the simulation. This method will mainly be called from TextUI  
	 * @author luke_hamilton
	 * @param inLinkName - The Name of the Link eg: link1
	 * @return True if Node is contained in the hash table (linkTable), False if the 
	 * Node is not contained the has table 
	 * @version v0.20
	 **/
	public boolean containsLink(String inLinkName){
		if (linkTable.containsKey(inLinkName)) {
			return true;
		} 
			return false;
	}
	
	/**
	 * This method will return true if the simulation contains any object 
	 * E.g: if there are any node object within the nodeTable or any links within
	 * the linkTable
	 * @author luke_hamilton
	 * @return True if simulation contains any objects, False if the simulation
	 * does not contain any objects
	 * @version v0.20
	 **/
	public boolean containsObjects(){		
		if(nodeTable.isEmpty() && linkTable.isEmpty()){
			return false;
		}
			return true;		
	}
	
	/**
	 * This method will removed all Object (links/nodes) from the simulation
	 * @author luke_hamilton
	 * @version v0.20
	 **/
	public void removeAllObjects(){
                Enumeration e = nodeTable.elements();
		while(e.hasMoreElements())
		{
			Node temp = (Node)e.nextElement();
                        temp.stopTimers();
                }
                //
		nodeTable.clear();
		linkTable.clear();
	}

	/**
	 * This method is for deleting a node from within the simulation.
	 * It will
	 * @author luke_hamilton
	 * @param inNodeName - The Node Name eg: pc1
	 * @throws InvalidNodeNameException if the node name dosent exist
	 * @version v0.20
	 **/
	public void deleteNode(String inNodeName) throws InvalidNodeNameException {
		if (nodeTable.containsKey(inNodeName)) {	//test if node exist

			Node deletedNode = (Node) nodeTable.get(inNodeName);
                        deletedNode.stopTimers();
			deletedNode.removeAllLinks();
			nodeTable.remove(inNodeName); //remove node from hashtable

			//Remove any disconnected links from the simulation
			Enumeration keys = linkTable.keys();
			while (keys.hasMoreElements()) {
				String str = (String) keys.nextElement();
				Link tempLink = (Link) linkTable.get(str);
				if (tempLink.isNotConnected()) {
					linkTable.remove(str); //remove disconnected links from hashtable
				}
			}
		} else {
			throw new InvalidNodeNameException("Node does not exist within the simulation");
		}
	}

        /**
	 * This method is for deleting a link on specific interface from within the simulation.
	 * @author Key
	 * @param inNodeName - The Node Name eg: pc1
         * @param inInterfaceName - The Interface Name eg: eth0
	 * @throws InvalidNodeNameException if the node name dosent exist
         * @return String - link name for removing from interface...
	 * @version v0.20
	 **/
	public String disconnectLink(String inNodeName, String inInterfaceName) throws InvalidNetworkInterfaceNameException,InvalidNodeNameException {
		if (nodeTable.containsKey(inNodeName)) {	//test if node exist

			Node modifiedNode = (Node) nodeTable.get(inNodeName);
                        
			NetworkInterface modifiedInterface = modifiedNode.getNetworkInterface(inInterfaceName);
                        
                        modifiedInterface.removeConnectedLink();
                                          
			//Remove any disconnected links from the simulation
			Enumeration keys = linkTable.keys();
			while (keys.hasMoreElements()) {
				String str = (String) keys.nextElement();
				Link tempLink = (Link) linkTable.get(str);
				if (tempLink.isNotConnected()) {
					linkTable.remove(str); //remove disconnected links from hashtable
                                        return str;
				}
			}
		} else {
			throw new InvalidNodeNameException("Node does not exist within the simulation");
		}
                return "";
	}
        
                /**
	 * This method is for getting link on specific interface from within the simulation.
	 * @author Key
	 * @param inNodeName - The Node Name eg: pc1
         * @param inInterfaceName - The Interface Name eg: eth0
	 * @throws InvalidNodeNameException if the node name dosent exist
         * @return String - link name for removing from interface...
	 * @version v0.20
	 **/
	public String getLinkName(String inNodeName, String inInterfaceName) throws InvalidNetworkInterfaceNameException,InvalidNodeNameException {
		if (nodeTable.containsKey(inNodeName)) {	//test if node exist

			Node modifiedNode = (Node) nodeTable.get(inNodeName);
                        
			NetworkInterface modifiedInterface = modifiedNode.getNetworkInterface(inInterfaceName);
                        
                        return modifiedInterface.getConnectedLinkName();
                  
                } else {
			throw new InvalidNodeNameException("Node does not exist within the simulation");
		}
	}
        
	/**
	 * This method is for creating new node within the simulation
	 * @author key
	 * @throws InvalidNodeNameException
	 **/
       
    public void addNode(String ClassName, String inName, boolean inOn)throws InvalidNodeNameException {
    	try{
    		Class cNode = Class.forName( "core." + ClassName );
    		
    		//Class.forName( ClassName ).
    		
    		Constructor con = cNode.getConstructor(String.class, Boolean.TYPE);
    		    	
    		if (!nodeTable.containsKey(inName)) {
    			nodeTable.put(inName, con.newInstance(new Object[]{inName, inOn}));
    		} else {
    			throw new InvalidNodeNameException("Node already exists with same name");
    		}
    	}catch(InvalidNodeNameException ie){
    		throw ie;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return;
    }
        
	/**
	 * Compability methods for import old config files. Use addNode instead.
	 **/
    
    public void addPC(String inPCName, boolean inOn) throws InvalidNodeNameException {
		if (!nodeTable.containsKey(inPCName)) {
			nodeTable.put(inPCName, new PC(inPCName, inOn));
		} else {
			throw new InvalidNodeNameException("Node already exists with same name");
		}
		return;
	}
    
	public void addHub(String inNodeName, boolean inOn) throws InvalidNodeNameException {
		if (!nodeTable.containsKey(inNodeName)) {
			nodeTable.put(inNodeName, new Hub(inNodeName, inOn));
		} else {
			throw new InvalidNodeNameException("Node already exists with same name");
		}
	}
	
	public void addRouter(String inRouterName, boolean inOn)throws InvalidNodeNameException {
		if (!nodeTable.containsKey(inRouterName)) {
			nodeTable.put(inRouterName, new Router(inRouterName, inOn));
		} else {
			throw new InvalidNodeNameException("Node already exists with same name");
		}
		return;
	}
        
	public void addSwitch(String inNodeName, boolean inOn) throws InvalidNodeNameException {
		if(!nodeTable.containsKey(inNodeName)){
			nodeTable.put(inNodeName, new Switch(inNodeName, inOn));
		}else{
			throw new InvalidNodeNameException("Node already exists with same name");
		}
	}	

	/**
	 * Displays info about links within Simulation's link table via System.out.println()
	 * <P>Useful for a CLI or for testing purposes.</P>
	 * @author luke_hamilton
 	 * @version v0.20
	 **/
	public void displayLinks() {
		if(nodeTable.isEmpty()){
			System.out.println("There is currently no links within the simulation");	
		}else{		
			Enumeration keys = linkTable.keys();
			String str;
			Link tempLink;
			System.out.println("Links within the Simulation:");
			while (keys.hasMoreElements()) {
				str = (String) keys.nextElement();
				tempLink = (Link) linkTable.get(str);
				tempLink.displayDetails();
			}
		}
		System.out.println();
	}	
	
	/**
	 * This method adds InInfo to a LayerInfo Queue
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @param inInfo - Info
	 * @version v0.20
	 **/
	public static void addLayerInfo(LayerInfo inInfo){
		Simulation.Info.receiveInfo(inInfo);
	}
	
	public static void addLayerInfo(String infoClass, String name, String dataType, String layer, String desc){
		LayerInfo inInfo = new LayerInfo(infoClass);
		inInfo.setObjectName(name);
		inInfo.setDataType(dataType);
		inInfo.setLayer(layer);
		inInfo.setDescription(desc);
		Simulation.Info.receiveInfo(inInfo);
	}
	
	/**
	 * Clears the infoLayer
	 * @author bevna_calliess
	 * @author robert_reith
	 * @version v0.20
	 */
	public void clearLayerInfo(){
		Simulation.Info.clear();
	}
	
	/**
	 * Returns all record information when sending a ping or
	 * message across the system
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @return recordInformation
	 * @version v0.20 
	 */
	public Vector getRecordedInfo(){
		return Simulation.Info.getRecordedInfo();		
	}
	
	/**
	 * This method will look for the NodeName provided in the Node
	 * Hashtable and then call the getAvailable Interfaces method on
	 * that node.  This will return an array of Interface names that
	 * are avaialble to use when connecting a link.
	 * @return String[]	List of interface names that are not connected to a link. 
	 **/
	/*public String[] getAvailableInterfaces(String inNodeName)throws InvalidNetworkLayerDeviceException, InvalidNodeNameException {
	String strAvailInterfaces[];
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			strAvailInterfaces = temp.getAvailableInterfaces();			
		}else{
			throw new InvalidNodeNameException("Node does not exist.");
		}		
		return strAvailInterfaces;
	}*/
	
        /**
	 * @author luke_hamilton
	 * @param  inNodeName
	 * @return
	 * @throws InvalidNodeNameException
	 */
     /*   public Object[] getAllInterfaces(String inNodeName)throws InvalidNodeNameException {
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if (temp instanceof NetworkLayerDevice){
				NetworkLayerDevice tempNode = (NetworkLayerDevice)nodeTable.get(inNodeName);				
				return tempNode.getAllInterfaces();				
			}else{
                DataLinkLayerDevice tempNode = (DataLinkLayerDevice)nodeTable.get(inNodeName);				
				return tempNode.getAllInterfaces();				
            }
                        // * @throws InvalidNetworkLayerDeviceException
	                //InvalidNetworkLayerDeviceException    
			//throw new InvalidNetworkLayerDeviceException("This node is not a network layered device.");			
		}		
		throw new InvalidNodeNameException("Node does not exist.");						
	}*/
	
	/**
	 * The static method calls the valid IP Address method within the 
	 * IPV4Address class.
	 * @author luke_hamilton
	 * @param inDecIPAddress
	 * @return
	 */
	/*public static boolean validateDecIP(String inDecIPAddress){
		return IPV4Address.validateDecIP(inDecIPAddress);
	}*/
	
	/**
	 * This static method calls the static validateDecSubnetMask method within the IPV4Address class
	 * @author luke_hamilton
	 * @param inDecIPAddress
	 * @param inDecSubnetMask
	 * @return
	 */
	/*public static boolean validateDecSubnetMask(String inDecSubnetMask){
		return IPV4Address.validateDecSubnetMask(inDecSubnetMask);
	}*/
	
	/**
	 * This static method calls the getDefaultSubnetMask method within the IPV4Address
	 * class. it will the return the default subnet mask in a string
	 * @param inDecimalIp
	 * @return
	 */
	/*public static String getDefaultSubnetMask(String inDecimalIp){
		return IPV4Address.getDefaultSubnetMask(inDecimalIp);
	}*/
	
	public Vector getAllNodeInformation(String inNodeName) throws InvalidNodeNameException
	{
		Vector vecNodeInfo = new Vector();
		String strDefaultGateway;
		
		if(nodeTable.containsKey(inNodeName)){
			Node temp = (Node)nodeTable.get(inNodeName);
			if(temp instanceof NetworkLayerDevice)
			{
			NetworkLayerDevice tempNetwork = (NetworkLayerDevice)temp;
			
			strDefaultGateway = tempNetwork.getDefaultGateway();
			if(strDefaultGateway == null)
			{
				strDefaultGateway = "Gateway Not Set";
			}
			
			ArrayList aryInterfaceNamesTemp = tempNetwork.getAllInterfacesNames();
			Collections.sort(aryInterfaceNamesTemp);
			Iterator it = aryInterfaceNamesTemp.iterator();
			
			while(it.hasNext())
			{
				Vector vecInterfaceInfo = new Vector();
				vecInterfaceInfo.add(inNodeName);                                
				vecInterfaceInfo.add(strDefaultGateway);
				String strInterfaceName = (String)it.next();
				vecInterfaceInfo.add(strInterfaceName);
				try{    
                                        vecInterfaceInfo.add(tempNetwork.getIntSType(strInterfaceName));
					vecInterfaceInfo.add(tempNetwork.getMACAddress(strInterfaceName));                                        
					String tempIP = tempNetwork.getIPAddress(strInterfaceName);
					if(tempIP != null)
					{
                                            vecInterfaceInfo.add(tempNetwork.getIPAddress(strInterfaceName));
					}
					else
					{
                                            vecInterfaceInfo.add("IP Address not set");	
					}
					vecInterfaceInfo.add(tempNetwork.getSubnetMask(strInterfaceName));
					NetworkInterface tempNetworkCard = tempNetwork.getNetworkInterface(strInterfaceName);
					vecInterfaceInfo.add(tempNetworkCard.getConnectLinkName());
				}
				
				catch(InvalidNetworkInterfaceNameException e)
				{
					//This should never happen
				}
				
				vecNodeInfo.add(vecInterfaceInfo);
			}
			
			}
			else
			{
				DataLinkLayerDevice tempData = (DataLinkLayerDevice)temp;
				
				
				ArrayList aryInterfaceNamesTemp = tempData.getAllInterfacesNames();
				Collections.sort(aryInterfaceNamesTemp);
				Iterator it = aryInterfaceNamesTemp.iterator();
				
				while(it.hasNext())
				{                                    
					Vector vecInterfaceInfo = new Vector();
					vecInterfaceInfo.add(inNodeName);
                                        String strInterfaceName = (String)it.next();                                        
					try{                                            
                                            vecInterfaceInfo.add("Not Applicable");					
                                            vecInterfaceInfo.add(strInterfaceName);
                                            vecInterfaceInfo.add(tempData.getIntSType(strInterfaceName));
                                            vecInterfaceInfo.add("Not Applicable");
                                            vecInterfaceInfo.add("Not Applicable");
                                            vecInterfaceInfo.add("Not Applicable");
                                            NetworkInterface tempNetworkCard = tempData.getNetworkInterface(strInterfaceName);
                                            vecInterfaceInfo.add(tempNetworkCard.getConnectLinkName());
					}
					
					catch(InvalidNetworkInterfaceNameException e)
					{
						//This should never happen
					}
					vecNodeInfo.add(vecInterfaceInfo);
				}	
					
			}
			
		}
		else{
			throw new InvalidNodeNameException("Node does not exist.");
		}	
		return vecNodeInfo;
	}
	
}//EOF

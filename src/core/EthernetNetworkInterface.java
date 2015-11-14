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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.protocolsuite.tcp_ip.ARP_packet;
import core.protocolsuite.tcp_ip.IP_packet;

//import java.util.*;
/**
 * A NetworkInterface represents the physical interface between
 * a Node and a physical Link (network cable). Think of it as the NIC
 * (Network Interface Card) of a Node.  This particular Network Interface 
 * is modelled on an Ethernet Interface Card.
 *
 * <P>Different Nodes can contain different numbers of NetworkInterfaces.
 * A client PC usually only has one NetworkInterface, whereas a Router contains
 * at least two. A Hub or a Switch often has 8 or 16.</P>
 *
 * <P>In order for a Node to be able to send or receive network information (packets),
 * there must be a Link (network cable) between at least 2 NetworkInterfaces.</P>
 *
 * <P>NetworkInterfaces receive and send packets to and from DatalinkProtocols and
 * the Link object they are connected to.</P>
 *
 * @author Tristan Veness 
 * @author Bevan Calliess
 * @since 19 September 2004
 * @version v0.20
 */

public class EthernetNetworkInterface extends NetworkInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6899794496746248120L;
	/** 
	 * Stores The MAC address for each instance of this class 
	 * */
	protected String MACAddress;
	public String defaultMACAddress;

	/**
	 * Constructs a NetworkInterface object with the name inName and a 
	 * reference to it's parent Node.
	 * @author bevan_calliess
	 * @param inName - The name to give the NetworkInterface eg: eth0
	 * @param parent - The Node that the NetworkInterface is to be added to, it's parent.
	 * @version v0.20
	 */
	protected EthernetNetworkInterface(long UID, String inName, Node parent) {
		super(UID, inName,parent);
		if(parentNode instanceof Hub){	
			//test for Hub 
			MACAddress = null;	
			//set MAC address to null, as hub dont have MAC address.
		}else{
			MACAddress = setMacAddress();
			defaultMACAddress = MACAddress;
		}

	}

	@Override
	public void Timer(int temp){ }

	/**
	 * This method receives a packet from a connected link and then pass it
	 * off to the node's protocolstack.  It also has to test the packets 
	 * MAC address to see if it matchs the networkinterface or the
	 * broadcask MAC address of FF:FF:FF:FF:FF:FF. 
	 * @author bevan_calliess
	 * @author angela_brown
	 * @param inPacket
	 * @version v0.20
	 */
	@Override
	protected void receivePacket(Packet inPacket) throws LowLinkException {
		// cast the packet to an EthernetPacket
		Ethernet_packet tempPacket = (Ethernet_packet)inPacket;
		
		boolean drop = false;

		if(!parentNode.On || !up) return;
		

		if(!(MACAddress.equals(tempPacket.getDestinationMACAddress()) 
				|| tempPacket.getDestinationMACAddress().equals("FF:FF:FF:FF:FF:FF")))
			drop = true;
		
		
		//Test if this packet is for this interface or a broadcast
		if(!drop){
			//Packet is For this Interface or is broadcast so send it to the Parent Node

			//Create Layer info 
			LayerInfo pingInfo = new LayerInfo(getClass().getName());
			pingInfo.setObjectName(parentNode.getName());
			pingInfo.setDataType("Ethernet Packet");
			pingInfo.setLayer("Link");
			pingInfo.setDescription("Recieved and accepted packet at interface " + MACAddress);
			Simulation.addLayerInfo(pingInfo);
			Packet temp = tempPacket.getData();

			boolean allowReceive = true;

			NetworkLayerDevice device = (NetworkLayerDevice) parentNode;
			if(device!=null && getACLin()!=0 && temp instanceof IP_packet && !(temp instanceof ARP_packet)){
				allowReceive = device.getACL().passACL(getACLin(), temp);
			}

			if(allowReceive)
				parentNode.receivePacket(temp, name);
		}else{
			//Packet is not for the Interface Drop Packet and record something in 
			//the Layerinfo object	 
			LayerInfo pingInfo = new LayerInfo(getClass().getName());
			pingInfo.setObjectName(parentNode.getName());
			pingInfo.setDataType("Ethernet Packet");
			pingInfo.setLayer("Link");
			pingInfo.setDescription("Recieved and dropped packet at interface " + MACAddress);
			Simulation.addLayerInfo(pingInfo);                      
		}	    
	}

	@Override
	public int getType(){
		return NetworkInterface.Ethernet10T;
	}

	/**
	 * Typecasts the variables being passed in into a EthernetPacket packet and
	 * creates layerinfo and passes it to EthernetLink if temp is not null 
	 * @author bevan_calliess
	 * @author angela_brown
	 * @author rober_hulford
	 * @param inPacket - A Packet containing data 
	 * @param inMacAddress - A MAC address eg: A2:B3:45:64:EE
	 * @version v0.20
	 */
	protected void sendPacket(Packet inPacket, String inMacAddress) throws LowLinkException {

		if(!parentNode.On || !up) return;
		
		Ethernet_packet Packet = new Ethernet_packet(inPacket,inMacAddress, MACAddress);
		//if(Packet.getHopCount()>255) throw new CommunicationException("Hub buffer overflow (packet loop flood?).");

		EthernetLink temp = (EthernetLink)connectedLink;

		// Create Layer info 
		LayerInfo pingInfo = new LayerInfo(getClass().getName());
		pingInfo.setObjectName(parentNode.getName());
		pingInfo.setDataType("Ethernet Packet");
		pingInfo.setLayer("Link");
		pingInfo.setDescription("Sending packet from interface "+MACAddress );
		Simulation.addLayerInfo(pingInfo);

		if(temp!=null){	  
			try{
				temp.transportPacket(Packet,getSourceName());                   
			}catch(LowLinkException ex){
				LayerInfo frameErrInfo = new LayerInfo(getClass().getName());
				frameErrInfo.setObjectName(parentNode.getName());
				frameErrInfo.setDataType("Ethernet Packet");
				frameErrInfo.setLayer("Link");
				frameErrInfo.setDescription(ex.toString());
				Simulation.addLayerInfo(frameErrInfo);
				//throw new LowLinkException(ex.toString());
			}
		}		
	}

	/**
	 * Returns the NetworkInterface's MAC Address.
	 * @author bevan_calliess
	 * @return MACAddress  
	 * @version v0.20
	 */
	protected String getMACAddress() {
		return MACAddress;
	}     

	@Override
	public boolean isActive(){
		return true;
	}

	/**
	 * This method is called when instantiating a NetworkInterface object.  
	 * it Generates 6 random numbers and converts them to hexadecimal
	 * and combines them into a String simulating a MAC Address.
	 * NOTE: The individual segments of this string can be converted back to
	 * a long integer if requred using the Long.decode method. 
	 * @author bevan_calliess
	 * @return MACAddress (in hex)
	 * @version v0.20
	 */
	protected final String setMacAddress(){

		String macAddress = null;

		for(int i=0;i<6;i++){
			double rndNum = 0;

			//This is to ensure a two digit code numbers less than 17 
			//appear as a single digit.
			while(rndNum<17){	
				rndNum = Math.random();
				rndNum = (rndNum * 100)*2;
			}            

			// if this is not the first segment add the : divider         
			if(macAddress == null){
				macAddress = Long.toHexString((int)rndNum);
			}else{
				macAddress = macAddress + ":" + Long.toHexString((int)rndNum);
			}
		}
		return macAddress.toUpperCase();
	}

	protected void setMacAddress(String macAddress){
		if(macAddress==null){
			this.MACAddress = defaultMACAddress;
		}
		else{
			this.MACAddress = macAddress;
		}
	}

	public static boolean isMacAddress(String macAddress){
		Pattern p = Pattern.compile("^[0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5}$");
		Matcher m = p.matcher(macAddress);
		return m.matches();
	}



	/**
	 * This method displays details about the current interface card
	 * @author bevan_calliess
	 * @return String 
	 * @version v0.20
	 */ 
	@Override
	protected String getDetails(){

		return "Interface: "+name + "\t\tMAC: " + MACAddress +"\t\t"+ getConnectLinkDetails();
	}
	
	@Override
	public int getAcceptedPacketPercent() {
		return ((EthernetLink) connectedLink).getAcceptedPacketPercent();
	}
	
	@Override
	public int getInterfaceBandwidth() {
		return 10;
	}
}

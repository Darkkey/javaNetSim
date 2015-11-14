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

import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

import core.protocolsuite.tcp_ip.ExternalProxyApp;
import core.protocolsuite.tcp_ip.IP_packet;
import core.protocolsuite.tcp_ip.TCP_packet;

/**
* Represents a Router Node in a network. A Router is the backbone of the internet. Routers
* are responsible for Packets being able to reach their final destination.
* @author tristan_veness
* @since 13 June 2004 
* @version v0.10
**/

public class ExternalProxy extends NetworkLayerDevice {

    /**
	 * 
	 */
	private static final long serialVersionUID = -426558271140634982L;
	int externalPort;
    String externalIP;
    ExternalProxyApp NE;
    boolean busy;
   
    protected Hashtable Apps = null;
    
  
    public void addApp(Object app, int code){
                Apps.put(code, app);
    }
    
    public Object getApp(int code){
                return Apps.get(code);
    }
    
	/**
	* Constructs a new Router with the specified name and is given two 
	* Network Interfaces
	* @author tristan_veness
	* @param inName A name to give the new Router Node eg: Router1.
	* @version v0.10
	**/
     public ExternalProxy(String inName, boolean inOn) {
             super(inName,3, inOn);
             NodeProtocolStack.initNAT();
             externalIP = null;
             Apps = new Hashtable();             
             busy = false;
     }
     
     public void NATDisconnect(Object application){
         externalIP = null;        
     }
     
     @Override
	public void receivePacket(Packet inPacket) throws LowLinkException {
              if(inPacket instanceof IP_packet){
                    if(busy) return;
              
                    IP_packet IPP = (IP_packet) inPacket;
                    if(!NodeProtocolStack.isInternalIP(IPP.getDestIPAddress())){
                      System.out.println("Activating NAT when recieving...");
                      
                      if(IPP instanceof TCP_packet){
                          busy = true;
                          if(externalIP == null){
                              
                              externalIP = IPP.getDestIPAddress();
                              externalPort = ((TCP_packet) IPP).get_destPort();
                              
                              try {
                                Socket NAT = new Socket(externalIP, externalPort);
                                                         
                              
                                NE = null;
                                NE = new ExternalProxyApp(NodeProtocolStack, this, NAT, externalIP, externalPort, core.Simulation.UIDGen++);                              
                                System.out.println("NAT step 2");
                                //addApp(NE, 1);                             
                              
                                ((TCP_packet) IPP).setDestIPAddress(NodeProtocolStack.getIPAddress("eth0"));
                                NodeProtocolStack.recieveTCP_packet((TCP_packet) IPP);                              
                                busy = false;
                                
                              }catch (IOException e) {                                    
                                    e.printStackTrace();
                                    externalIP =  null;
                              }
                              
                          }else{
                            ((TCP_packet) IPP).setDestIPAddress(NodeProtocolStack.getIPAddress("eth0"));
                            NodeProtocolStack.recieveTCP_packet((TCP_packet) IPP);                              
                            busy = false;
                          }     
                          
                          
                      }
                        
                    }else{
                        NodeProtocolStack.receivePacket(inPacket, "unk");
                    }                
                }else{
                    NodeProtocolStack.receivePacket(inPacket, "unk");
                }
     }
     
     
     @Override
	public void sendPacket(String inDestMACAddress, Packet inPacket, String inInterfaceKey) throws InvalidNetworkInterfaceNameException, CommunicationException, LowLinkException{
		if (NetworkInterfacetable.containsKey(inInterfaceKey))
		{
			EthernetNetworkInterface temp = (EthernetNetworkInterface)NetworkInterfacetable.get(inInterfaceKey);	
                        
                        if(inPacket instanceof IP_packet){
                            IP_packet IPP = (IP_packet) inPacket;
                            System.out.println("Activating Proxy when sending...");
                            //if(!NodeProtocolStack.isInternalIP(IPP.getSourceIPAddress())){
                            //System.out.println("Activating NAT when sending...");
                                if(IPP instanceof TCP_packet){
                                    ((TCP_packet) IPP).setSourceIPAddress(externalIP);
                                }
                            //}
                        }
                        
			temp.sendPacket(inPacket,inDestMACAddress);		
		}
		else
		{
			throw new InvalidNetworkInterfaceNameException("Inteface does not exist.");
		}
	}
}//EOF
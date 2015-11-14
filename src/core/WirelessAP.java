/*
Java Network Simulator (javaNetSim)

Copyright (c) 2005, Ice Team;  All rights reserved.
Copyright (c) 2004, jFirewallSim development team;  All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

	- Redistributions of source code must retain the above copyright notice, this list
	  of conditions and the following disclaimer.
	- Redistributions in binary form must reproduce the above copyright notice, this list
	  of conditions and the following disclaimer in the documentation and/or other
	  materials provided with the distribution.
	- Neither the name of the Ice Team nor the names of its
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

/** 
 * This class extends of the DataLinkLayerDevice and allows the
 * creation of switches
 * @author bevan_calliess
 * @author luke_hamilton
 * @author Key
 * @since Nov 8, 2005
 * @version v0.22
 **/

public class WirelessAP extends NetworkLayerDevice{ //DataLinkLayerDevice {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7961369368235690525L;

	/**
	 * This creates a layer 2 switch with a default of 5 interface (ports)
	 * @author luke_hamilton
	 * @param inName The name of the Switch
	 * @param inProtocolStackLayers
	 * @version v0.20
	 */
    
        int sz = 0;
        
        int buffSize = 255;
        
        Hashtable IntCaches;
        
        public WirelessAP(String inName, boolean inOn) {
        		super(inName, 3, inOn);	//pass name and protocolstack layer 
                
                IntCaches = new Hashtable();   
                
        }

        @Override
		public void addNetworkInterface(String interfaceName, int type, boolean active, int code) {
            if(!active){                
                super.addNetworkInterface(interfaceName, type, false, code);
                IntCaches.put(interfaceName, new Hashtable());
            }
        }
        
        @Override
		public void Reset(){
            sz = 0;
            Enumeration it;                
            String nic = "";
                
            it = NetworkInterfacetable.elements();
               
            while(it.hasMoreElements()){
                NetworkInterface tempInterface = (NetworkInterface)it.nextElement();
                nic = tempInterface.getName();
                Hashtable outInt = (Hashtable) IntCaches.get(nic); 
                outInt.clear();
            }
        }
        
        @Override
		public int getState(){
            return sz;
        }
        

        @Override
		public void turnOn() {
            super.turnOn();
            
            //ifacesUP();
            
        }
        
        @Override
		public void turnOff() {
        	
        	ifacesDOWN();
        	
            super.turnOff();
            
        }
        public String getCache(){
              Enumeration it, it2;                
              String nic = "";
              String result = "";
                
              it = NetworkInterfacetable.elements();
               
              while(it.hasMoreElements()){
                        NetworkInterface tempInterface = (NetworkInterface)it.nextElement();
                        nic = tempInterface.getName();
                        Hashtable outInt = (Hashtable) IntCaches.get(nic);  
                        
                        result = result + nic + ": ";
                        
                        it2 = outInt.keys();
                        
                        while(it2.hasMoreElements()){
                            String mac = (String)it2.nextElement();
                            result = result + mac + "\t";
                        }
                        
                        result = result + "\n";
              }
              
              return result;
        }
        
	/**
	 * This method will recieve a packet from any of the connected links and the copy 
	 * the Packet and distribute a copy to each of the other connected links.
	 * @author bevan_calliess
	 * @param inPacket - The packet to be transported
	 * @param inLinkName - The name of the link that sent the packet eg: eth0
	 */
	
	@Override
	public void receivePacket(Packet inPacket,String inInterfaceName) throws LowLinkException{
        if(sz!=1){
                Ethernet_packet tempPacket = (Ethernet_packet)inPacket;
                Enumeration it;                
                boolean intFound = false;
                String nic = "";
                
                try{
                    Hashtable inInt = (Hashtable) IntCaches.get(inInterfaceName);
                    inInt.put(tempPacket.getSourceMACAddress(), "1");                    
                    Ethernet_packet copyPacket = new Ethernet_packet(tempPacket.getData(), tempPacket.getDestinationMACAddress(),tempPacket.getSourceMACAddress());
                    
                    it = NetworkInterfacetable.elements();
                    while(it.hasMoreElements()){
                        NetworkInterface tempInterface = (NetworkInterface)it.nextElement();
                        nic = tempInterface.getName();
                        Hashtable outInt = (Hashtable) IntCaches.get(nic);
                        if(outInt.get(tempPacket.getDestinationMACAddress()) != null){
                            intFound = true;
                            try{
                            	tempInterface.sendPacket(copyPacket);
                            }catch(NullPointerException e){
                                System.out.println("WirelessAP.java: " + e.toString());
                            }
                        }
                    }
                    
                    it = NetworkInterfacetable.elements();
                    while(it.hasMoreElements() && !intFound){
                        //Test to see if the current Interface is the Interface that sent in the packet
                    	// if it is skip that interface
                    	NetworkInterface tempInterface = (NetworkInterface)it.nextElement();
                    	if(!(tempInterface.getName().equals(inInterfaceName) && !inInterfaceName.contains("wrl"))){
                            
                            try{
                            	tempInterface.sendPacket(copyPacket);
                            }catch(NullPointerException e){
                                System.out.println("WirelessAP.java: " + e.toString());
                            }
                    	}						
                        
                    }
                  
               }catch(Throwable th)
               {
                   if(th.toString().contains("Packet lost due to physical link problems!")){
                    throw new LowLinkException(th.toString());   
                   }else{
                    sz=1;
                    System.out.println(th.toString());
                    throw new LowLinkException("WirelessAP buffer overflow (packet loop flood?).");
                   }
               }
            }
	}
	
    public boolean isActive(){
        return true;
    }
}

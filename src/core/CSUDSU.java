/*
Java Network Simulator (jNetSim)

Copyright (c) 2007, 2006, 2005, Ice Team;  All rights reserved.
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

public class CSUDSU extends DataLinkLayerDevice {
	
        /**
	 * 
	 */
	private static final long serialVersionUID = 7725383877392513016L;
		String lanPort;
        String wanPort;
        int sz = 0;
    
	public CSUDSU(String inName, boolean inOn) {
		super(inName, 1, inOn);	//pass name and protocolstack layer 
	}

        public void addSerialInterface(){
            lanPort = "ser0";
            addNetworkInterface(lanPort, NetworkInterface.Serial, false, 0);
        }
        
        public void addWANInterface(){
             wanPort = "wan0" + "_" + (int)(Math.random()*1000);
             addNetworkInterface(wanPort, NetworkInterface.WAN, false, 0);
        }
        
        public void setLAN(String lan){
            lanPort = lan;
        }
        
        public void setWAN(String wan){
            wanPort = wan;
        }
        
        @Override
		public void Reset(){
            super.Reset();
        }

        @Override
		public void turnOn() {
            super.turnOn();
        }
    
        @Override
		public void turnOff() {
            super.turnOff();
        }
        
        @Override
		public int getState(){
            return sz;
        }
        
               
	@Override
	protected void receivePacket(Packet inPacket,String inInterfaceName) throws LowLinkException{
            if(sz!=1){                
                if(inInterfaceName == lanPort){
                    NetworkInterface tempInterface = (NetworkInterface)this.NetworkInterfacetable.get(wanPort);                    
                    tempInterface.sendPacket(inPacket);
                }else{
                    SerialNetworkInterface tempInterface = (SerialNetworkInterface)this.NetworkInterfacetable.get(lanPort);                    
                    tempInterface.sendPacket(inPacket);
                }                   
            }
	}
}

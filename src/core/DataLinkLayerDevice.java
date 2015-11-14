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
/**
* @author  luke_hamilton
* @author  bevan_calliess
* @since	Oct 9, 2004
* @version v0.20
*/
public abstract class DataLinkLayerDevice extends Node {
	/**
	 * The method passes in two variables and then passes them 
	 * up to the super class
	 * @param inName - Node name eg: PC1
	 * @param inProtocolStackLayers 
	 * @version v0.20
	 */
    public DataLinkLayerDevice(String inName, int inProtocolStackLayers, boolean inOn) {
		super(inName, inProtocolStackLayers, inOn);
	}
	
	/**
	 * Passes in a Interface name and put it into the 
	 * NetworkInteface hash table
	 * @param InterfaceName - Nodes Interface name eg: eth0
	 * @version v0.20
	 */
	
	protected void addNetworkInterface(String interfaceName) {
            NetworkInterfacetable.put(interfaceName,new NetworkInterfacePort(core.Simulation.UIDGen++,interfaceName,this,true));            
    }
	
	 @Override
	public void turnOn() {
	     super.turnOn();
	        
	     ifacesLinkUP();	        
	 }
        

}

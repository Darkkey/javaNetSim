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

import core.protocolsuite.tcp_ip.DHCPC;
import core.protocolsuite.tcp_ip.DHCPD;
import core.protocolsuite.tcp_ip.DNS;
import core.protocolsuite.tcp_ip.RIP;
import core.protocolsuite.tcp_ip.SNMP;
import core.protocolsuite.tcp_ip.Telnet_server;

/**
* Represents a Router Node in a network. A Router is the backbone of the internet. Routers
* are responsible for Packets being able to reach their final destination.
* @author tristan_veness
* @since 13 June 2004 
* @version v0.10
**/

public class Router extends ApplicationLayerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6546396346550074138L;

	/**
	* Constructs a new Router with the specified name and is given two 
	* Network Interfaces
	* @author tristan_veness
	* @param inName A name to give the new Router Node eg: Router1.
	* @version v0.10
	**/
     public Router(String inName, boolean inOn) {
             super(inName,7, inOn);             
     }
     
     @Override
	public void initApplications(){
         super.initApplications();
         
         RIP ripServer=new RIP(NodeProtocolStack,core.Simulation.UIDGen++);
     
         SNMP snmpAgent = new SNMP(this, NodeProtocolStack, SNMP.DEFAULT_PORT, 0, core.Simulation.UIDGen++);
         
         Telnet_server telnetServer = new Telnet_server(this, NodeProtocolStack, 23, 1,  core.Simulation.UIDGen++);
         
         DHCPD dhcpd = new DHCPD(NodeProtocolStack, core.Simulation.UIDGen++);
         DHCPC dhcpc = new DHCPC(NodeProtocolStack, core.Simulation.UIDGen++);

         DNS dnsClient = new DNS(NodeProtocolStack, 91, 0, core.Simulation.UIDGen++);
         
         addApp(ripServer,ApplicationLayerDevice.RIP_SERVER_ID);
         
         addApp(telnetServer, ApplicationLayerDevice.TELNET_SERVER_ID);
        
         addApp(snmpAgent, ApplicationLayerDevice.SNMP_AGENT_ID);
         
         addApp(dhcpd, ApplicationLayerDevice.DHCP_SERVER_ID);
         addApp(dhcpc, ApplicationLayerDevice.DHCP_CLIENT_ID);
         
         addApp(dnsClient,ApplicationLayerDevice.DNS_CLIENT_ID);
    }
     
}//EOF

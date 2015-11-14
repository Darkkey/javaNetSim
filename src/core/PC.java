package core;

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



import core.protocolsuite.tcp_ip.DHCPC;
import core.protocolsuite.tcp_ip.DHCPD;
import core.protocolsuite.tcp_ip.DNS;
import core.protocolsuite.tcp_ip.Echo;
import core.protocolsuite.tcp_ip.Echo_tcp;
import core.protocolsuite.tcp_ip.PosixTelnetClient;
import core.protocolsuite.tcp_ip.SNMP;
import core.protocolsuite.tcp_ip.Telnet_client;
import core.protocolsuite.tcp_ip.Telnet_server;



/**

* A PC is considered to be a client PC in a network which only has one NetworkInterface. The addInterface

* method of the inherited Node class is overridden to only allow one NetworkInterface to be added.

* @author tristan_veness

* @since 13 June 2004

* @version v0.10

*/



class PC extends ApplicationLayerDevice {

     
     
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -5801793728971740333L;

	/**
	* Constructs a PC with the specified name.
	* @author tristan_veness
	* @param inName - A name to give the new PC Node. eg: PC1
	* @version v0.10
	*/     
     public PC(String inName, boolean inOn) {
             super(inName,7, inOn);                
     }
     
     @Override
	public void initApplications(){
             super.initApplications();
             
             //RIP ripServer=new RIP(NodeProtocolStack,core.Simulation.UIDGen++);
         
             Echo echoServer = new Echo(NodeProtocolStack, 7, 1,  core.Simulation.UIDGen++);
             Echo echoClient = new Echo(NodeProtocolStack, 0, 0,  core.Simulation.UIDGen++);
             Echo_tcp echotcpServer = new Echo_tcp(NodeProtocolStack, 17, 1,  core.Simulation.UIDGen++);
             Echo_tcp echotcpClient = new Echo_tcp(NodeProtocolStack, 0, 0,  core.Simulation.UIDGen++);
             
             SNMP snmpAgent = new SNMP(this, NodeProtocolStack, SNMP.DEFAULT_PORT, 0, core.Simulation.UIDGen++);
             SNMP snmpManager = new SNMP(this, NodeProtocolStack, 0, 1, core.Simulation.UIDGen++);

             Telnet_server telnetServer = new Telnet_server(this, NodeProtocolStack, 23, 1,  core.Simulation.UIDGen++);
             Telnet_client telnetClient = new Telnet_client(this, NodeProtocolStack, 0, 0,  core.Simulation.UIDGen++);
             
             PosixTelnetClient ptc = new PosixTelnetClient(NodeProtocolStack, core.Simulation.UIDGen++);
             
             DHCPD dhcpd = new DHCPD(NodeProtocolStack, core.Simulation.UIDGen++);
             DHCPC dhcpc = new DHCPC(NodeProtocolStack, core.Simulation.UIDGen++);

             DNS dnsServ = new DNS(NodeProtocolStack, 90, 1, core.Simulation.UIDGen++);
             DNS dnsClient = new DNS(NodeProtocolStack, 91, 0, core.Simulation.UIDGen++);
             
             //addApp(ripServer,RIP_SERVER_ID);
             
             addApp(echoServer, ApplicationLayerDevice.ECHO_SERVER_ID);
             addApp(echoClient, ApplicationLayerDevice.ECHO_CLIENT_ID);

             addApp(echotcpServer, ApplicationLayerDevice.ECHO_TCP_SERVER_ID);
             addApp(echotcpClient, ApplicationLayerDevice.ECHO_TCP_CLIENT_ID);

             addApp(telnetServer, ApplicationLayerDevice.TELNET_SERVER_ID);
             addApp(telnetClient, ApplicationLayerDevice.TELNET_CLIENT_ID);

             addApp(ptc, ApplicationLayerDevice.POSIX_TELNET_CLIENT_ID);
             
             addApp(snmpAgent, ApplicationLayerDevice.SNMP_AGENT_ID);
             addApp(snmpManager, ApplicationLayerDevice.SNMP_MANAGER_ID);
             
             addApp(dhcpd, ApplicationLayerDevice.DHCP_SERVER_ID);
             addApp(dhcpc, ApplicationLayerDevice.DHCP_CLIENT_ID);
             
             addApp(dnsServ,ApplicationLayerDevice.DNS_SERVER_ID);
             addApp(dnsClient,ApplicationLayerDevice.DNS_CLIENT_ID);
     }

}//EOF


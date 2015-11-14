package core;

import core.protocolsuite.tcp_ip.DHCPC;
import core.protocolsuite.tcp_ip.SNMP;




public class Printer extends ApplicationLayerDevice {

     /**
	 * 
	 */
	private static final long serialVersionUID = -8844316032033522655L;
	public final static int SNMP_AGENT_ID = 161;
     public final static int SNMP_MANAGER_ID = 30161;
     public final static int DHCP_SERVER_ID = 67;
     public final static int DHCP_CLIENT_ID = 68;
    
	/**
	* Constructs a PC with the specified name.
	* @author tristan_veness
	* @param inName - A name to give the new PC Node. eg: PC1
	* @version v0.10
	*/     
     public Printer(String inName, boolean inOn) {
             super(inName,7, inOn);                
     }
     
     @Override
	public void initApplications(){
             super.initApplications();
         
            
             SNMP snmpAgent = new SNMP(this, NodeProtocolStack, SNMP.DEFAULT_PORT, 0, core.Simulation.UIDGen++);
             DHCPC dhcpc = new DHCPC(NodeProtocolStack, core.Simulation.UIDGen++);

             addApp(snmpAgent, Printer.SNMP_AGENT_ID);
             addApp(dhcpc, Printer.DHCP_CLIENT_ID);
             
             getConfig().executeCommand("no interface eth0 shutdown");
             getConfig().executeCommand("interface eth0 ip dhcp client");
             getConfig().executeCommand("write memory");
     }

}//EOF


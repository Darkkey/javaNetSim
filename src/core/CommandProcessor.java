/*
 * CommandProcessor.java
 *
 * Created on 11 ������� 2007 �., 20:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.AccessListEngine.access_list;
import core.CommandInterface.Modes;
import core.NATEngine.NAT_rule;
import core.NATEngine.NAT_session;
import core.OSPF.OSPFConfigurateCommands;
import core.WiFiPort.APClient;
import core.protocolsuite.tcp_ip.DHCPC;
import core.protocolsuite.tcp_ip.DHCPD;
import core.protocolsuite.tcp_ip.DNS;
import core.protocolsuite.tcp_ip.DNS_Message;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.InvalidIPAddressException;
import core.protocolsuite.tcp_ip.InvalidSubnetMaskException;
import core.protocolsuite.tcp_ip.RIP;
import core.protocolsuite.tcp_ip.Route_entry;
import core.protocolsuite.tcp_ip.SNMP;
import core.protocolsuite.tcp_ip.TCP_session;
import core.protocolsuite.tcp_ip.Tcp;
import core.protocolsuite.tcp_ip.Telnet_server;
import core.protocolsuite.tcp_ip.UDP_session;
import core.protocolsuite.tcp_ip.jnSocket;

/**
 *
 * @author QweR
 */
public class CommandProcessor {

	private NetworkLayerDevice device;
	private CommandsTree commands = new CommandsTree();

	//private NoCommandClass noCommand = new NoCommandClass();
	private arp_CommandClass arp_Command = new arp_CommandClass();
	private clear_arp_CommandClass clear_arp_Command = new clear_arp_CommandClass();
	private clock_set_CommandClass clock_set_Command = new clock_set_CommandClass();
	private copy_running_startup_CommandClass copy_running_startup_Command = new copy_running_startup_CommandClass();
	private copy_startup_running_CommandClass copy_startup_running_Command = new copy_startup_running_CommandClass();
	private erase_startup_config_CommandClass erase_startup_config_Command = new erase_startup_config_CommandClass();
	private erase_vlan_CommandClass erase_vlan_Command = new erase_vlan_CommandClass();
	private help_CommandClass help_Command = new help_CommandClass();
	private hostname_CommandClass hostname_Command = new hostname_CommandClass();
	private location_CommandClass location_Command = new location_CommandClass();
	private reload_CommandClass reload_Command = new reload_CommandClass();
	private interface__authentication_CommandClass interface__authentication_Command = new interface__authentication_CommandClass();
	private interface__bssid_CommandClass interface__bssid_Command = new interface__bssid_CommandClass();
	private interface__channel_CommandClass interface__channel_Command = new interface__channel_CommandClass();
	private interface__clock_rate_CommandClass interface__clock_rate_Command = new interface__clock_rate_CommandClass();
	private interface__databits_CommandClass interface__databits_Command = new interface__databits_CommandClass();
	private interface__description_CommandClass interface__description_Command = new interface__description_CommandClass();
	private interface__encryption_key_CommandClass interface__encryption_key_Command = new interface__encryption_key_CommandClass();
	private interface__flowcontrol_CommandClass interface__flowcontrol_Command = new interface__flowcontrol_CommandClass();
	private interface__ip_address_CommandClass interface__ip_address_Command = new interface__ip_address_CommandClass();
	private interface__ip_access_group_CommandClass interface__ip_access_group_Command = new interface__ip_access_group_CommandClass();
	private interface__ip_nat_CommandClass interface__ip_nat_Command = new interface__ip_nat_CommandClass();
	private interface__ip_unreachables_CommandClass interface__ip_unreachables_Command = new interface__ip_unreachables_CommandClass();
	private interface__ip_redirects_CommandClass interface__ip_redirects_Command = new interface__ip_redirects_CommandClass();
	private interface__ip_mask_replay_CommandClass interface__ip_mask_replay_Command = new interface__ip_mask_replay_CommandClass();
	private interface__ip_information_replay_CommandClass interface__ip_information_replay_Command = new interface__ip_information_replay_CommandClass();
	private interface__ip_dhcp_client_CommandClass interface__ip_dhcp_client_Command = new interface__ip_dhcp_client_CommandClass();
	private interface__mac_address_CommandClass interface__mac_address_Command = new interface__mac_address_CommandClass();
	private interface__parity_CommandClass interface__parity_Command = new interface__parity_CommandClass();
	private interface__shutdown_CommandClass interface__shutdown_Command = new interface__shutdown_CommandClass();
	private interface__speed_CommandClass interface__speed_Command = new interface__speed_CommandClass();
	private interface__ssid_CommandClass interface__ssid_Command = new interface__ssid_CommandClass();
	private interface__station_role_CommandClass interface__station_role_Command = new interface__station_role_CommandClass();
	private interface__stopbits_CommandClass interface__stopbits_Command = new interface__stopbits_CommandClass();
	private interface__switchport_access_vlan_CommandClass interface__switchport_access_vlan_Command = new interface__switchport_access_vlan_CommandClass();
	private interface__switchport_description_CommandClass interface__switchport_description_Command = new interface__switchport_description_CommandClass();
	private interface__switchport_mode_access_CommandClass interface__switchport_mode_access_Command = new interface__switchport_mode_access_CommandClass();
	private interface__switchport_mode_trunk_CommandClass interface__switchport_mode_trunk_Command = new interface__switchport_mode_trunk_CommandClass();
	private ip_access_list_standart_CommandClass ip_access_list_standart_Command = new ip_access_list_standart_CommandClass();
	private ip_access_list_extended_CommandClass ip_access_list_extended_Command = new ip_access_list_extended_CommandClass();
	private ip_dhcp_pool__default_router_CommandClass ip_dhcp_pool__default_router_Command = new ip_dhcp_pool__default_router_CommandClass();
	private ip_dhcp_pool__hardware_address_CommandClass ip_dhcp_pool__hardware_address_Command = new ip_dhcp_pool__hardware_address_CommandClass();
	private ip_dhcp_pool__network_CommandClass ip_dhcp_pool__network_Command = new ip_dhcp_pool__network_CommandClass();
	private ip_dhcp_excluded_address_CommandClass ip_dhcp_excluded_address_Command = new ip_dhcp_excluded_address_CommandClass();
	private ip_dns_primary_CommandClass ip_dns_primary_Command = new ip_dns_primary_CommandClass();
	private ip_dns_server_CommandClass ip_dns_server_Command = new ip_dns_server_CommandClass();
	private ip_host_CommandClass ip_host_Command = new ip_host_CommandClass();
	private ip_host_hinfo_CommandClass ip_host_hinfo_Command = new ip_host_hinfo_CommandClass();
	private ip_name_server_CommandClass ip_name_server_Command = new ip_name_server_CommandClass();
	//private ip_nat_inside_destination_list_CommandClass ip_nat_inside_destination_list_Command = new ip_nat_inside_destination_list_CommandClass();
	private ip_nat_inside_source_list_CommandClass ip_nat_inside_source_list_Command = new ip_nat_inside_source_list_CommandClass();
	private ip_nat_inside_source_static_CommandClass ip_nat_inside_source_static_Command = new ip_nat_inside_source_static_CommandClass();
	private ip_nat_pool_CommandClass ip_nat_pool_Command = new ip_nat_pool_CommandClass();
	private ip_route_CommandClass ip_route_Command = new ip_route_CommandClass();
	private ip_tcp_window_size_CommandClass ip_tcp_window_size_Command = new ip_tcp_window_size_CommandClass();
	private router_rip_CommandClass router_rip_Command = new router_rip_CommandClass();
	private router_rip_network_CommandClass router_rip_network_Command = new router_rip_network_CommandClass();
	private show_access_lists_CommandClass show_access_lists_Command = new show_access_lists_CommandClass();
	private show_arp_CommandClass show_arp_Command = new show_arp_CommandClass();
	private show_clock_CommandClass show_clock_Command = new show_clock_CommandClass();
	private show_configuration_CommandClass show_configuration_Command = new show_configuration_CommandClass();
	private show_connection_CommandClass show_connection_Command = new show_connection_CommandClass();
	private show_dhcp_lease_CommandClass show_dhcp_lease_Command = new show_dhcp_lease_CommandClass();
	private show_dhcp_server_CommandClass show_dhcp_server_Command = new show_dhcp_server_CommandClass();
	private show_dot11_associations_CommandClass show_dot11_associations_Command = new show_dot11_associations_CommandClass();
	private show_hostname_CommandClass show_hostname_Command = new show_hostname_CommandClass();
	private show_interfaces_CommandClass show_interfaces_Command = new show_interfaces_CommandClass();
	private show_ip_CommandClass show_ip_Command = new show_ip_CommandClass();
	private show_ip_route_CommandClass show_ip_route_Command = new show_ip_route_CommandClass();
	private show_ip_nat_translations_CommandClass show_ip_nat_translations_Command = new show_ip_nat_translations_CommandClass();
	private show_kron_CommandClass show_kron_Command = new show_kron_CommandClass();
	private show_location_CommandClass show_location_Command = new show_location_CommandClass();
	private show_logging_CommandClass show_logging_Command = new show_logging_CommandClass();
	private show_ntp_CommandClass show_ntp_Command = new show_ntp_CommandClass();
	private show_running_config_CommandClass show_running_config_Command = new show_running_config_CommandClass();
	private show_sessions_CommandClass show_sessions_Command = new show_sessions_CommandClass();
	private show_snmp_community_CommandClass show_snmp_community_Command = new show_snmp_community_CommandClass();
	private show_snmp_mib_CommandClass show_snmp_mib_Command = new show_snmp_mib_CommandClass();
	private show_snmp_version_CommandClass show_snmp_version_Command = new show_snmp_version_CommandClass();
	private show_startup_config_CommandClass show_startup_config_Command = new show_startup_config_CommandClass();
	private show_tcp_sessions_CommandClass show_tcp_sessions_Command = new show_tcp_sessions_CommandClass();
	private show_tcp_statistics_CommandClass show_tcp_statistics_Command = new show_tcp_statistics_CommandClass();
	private show_tcp_window_size_CommandClass show_tcp_window_size_Command = new show_tcp_window_size_CommandClass();
	private show_udp_sessions_CommandClass show_udp_sessions_Command = new show_udp_sessions_CommandClass();
	private show_udp_statistics_CommandClass show_udp_statistics_Command = new show_udp_statistics_CommandClass();
	private show_version_CommandClass show_version_Command = new show_version_CommandClass();
	private show_vlan_CommandClass show_vlan_Command = new show_vlan_CommandClass();
	private snmp_server_community_CommandClass snmp_server_community_Command = new snmp_server_community_CommandClass();
	private snmp_server_port_CommandClass snmp_server_port_Command = new snmp_server_port_CommandClass();
	private telnet_CommandClass telnet_Command = new telnet_CommandClass();
	private telnet_server_CommandClass telnet_server_Command = new telnet_server_CommandClass();
	private username_CommandClass username_Command = new username_CommandClass();
	private vlan_CommandClass vlan_Command = new vlan_CommandClass();
	private write_memory_CommandClass write_memory_Command = new write_memory_CommandClass();
	private write_terminal_CommandClass write_terminal_Command = new write_terminal_CommandClass();

	/** Creates a new instance of CommandProcessor */
	public CommandProcessor(core.NetworkLayerDevice dev) {
		device = dev;
		initCommands();
	}

	private void initCommands(){
		commands.addDescription("no","Negate a command or set its defaults");
		commands.add("arp", arp_Command, "Add record to arp table");
		commands.addDescription("clear","Reset functions");
		commands.add("clear arp", clear_arp_Command, "Clear the entire ARP cache");
		commands.addDescription("clock","Manage the system clock");
		commands.add("clock set", clock_set_Command, "Set the time and date");
		commands.addDescription("copy","Copy confuguration files");
		commands.add("copy running-config startup-config", copy_running_startup_Command, "Copy from one file to another");
		commands.add("copy startup-config running-config", copy_startup_running_Command, "Copy from one file to another");
		commands.addDescription("erase","Erase a filesystem");
		commands.add("erase startup-config", erase_startup_config_Command, "Erase a startup-config");
		commands.add("erase vlan.dat", erase_vlan_Command, "Erase a vlan configuration file");
		commands.add("help", help_Command, "Description of the interactive help system");
		commands.add("hostname", hostname_Command, "Set system's name");
		commands.add("location", location_Command, "Set the system location");
		commands.add("reload", reload_Command, "Halt and perform a cold restart");

		commands.addDescription("interface","Select an interface to configure");
		commands.addDescription("interface *","Any available interface name");
		commands.add("interface * authentication", interface__authentication_Command, "Set authentication mode");
		commands.add("interface * bssid", interface__bssid_Command, "Set BSSID");
		commands.add("interface * channel", interface__channel_Command, "Specify channel");
		commands.add("interface * clock-rate", interface__clock_rate_Command, "Specify clock-rate");
		commands.add("interface * databits", interface__databits_Command, "Set number of data bits per character");
		commands.add("interface * description", interface__description_Command, "Interface specific description");
		commands.addDescription("interface * encryption","Configure encryption");
		commands.add("interface * encryption key", interface__encryption_key_Command, "Configure encryption key");
		commands.add("interface * flowcontrol", interface__flowcontrol_Command, "Set the flow control");
		commands.addDescription("interface * ip","Configure Internet Protocol");
		commands.add("interface * ip address", interface__ip_address_Command, "Set the IP address of an interface");
		commands.add("interface * ip access-group", interface__ip_access_group_Command, "Specify access control for packets");
		commands.add("interface * ip nat", interface__ip_nat_Command, "NAT interface commands");
		commands.add("interface * ip unreachables", interface__ip_unreachables_Command, "Enable sending ICMP Unreachable messages");
		commands.add("interface * ip redirects", interface__ip_redirects_Command, "Enable sending ICMP Redirect messages");
		commands.add("interface * ip mask-replay", interface__ip_mask_replay_Command, "Enable sending ICMP Mask Reply messages");
		commands.add("interface * ip information-replay", interface__ip_information_replay_Command, "Enable sending ICMP Information Reply messages");
		commands.addDescription("interface * dhcp","Configure DHCP parameters for this interface");
		commands.add("interface * ip dhcp client", interface__ip_dhcp_client_Command, "Enable DHCP client");
		commands.add("interface * mac-address", interface__mac_address_Command, "Manually set interface MAC address");
		commands.add("interface * parity", interface__parity_Command, "Set terminal parity");
		commands.add("interface * shutdown", interface__shutdown_Command, "Shutdown the selected interface");
		commands.add("interface * speed", interface__speed_Command, "Set the transmit and receive speeds");
		commands.add("interface * ssid", interface__ssid_Command, "Set SSID");
		commands.add("interface * station-role", interface__station_role_Command, "Specify station role");
		commands.add("interface * stopbits", interface__stopbits_Command, "Set async line stop bits");
		commands.addDescription("interface * switchport access","Configure a port access");
		commands.add("interface * switchport access vlan", interface__switchport_access_vlan_Command, "Configure a port as a static-access port");
		commands.add("interface * switchport description", interface__switchport_description_Command, "Port specific description");
		commands.addDescription("interface * switchport mode","Configure the VLAN membership mode of a port");
		commands.add("interface * switchport mode access", interface__switchport_mode_access_Command, "Set the port to access mode");
		commands.add("interface * switchport mode trunk", interface__switchport_mode_trunk_Command, "Set the port to trunk unconditionally");

		commands.addDescription("ip","Global IP configuration subcommands");
		commands.addDescription("ip access-list","Named access-list; Add an access list entry");
		commands.add("ip access-list standart", ip_access_list_standart_Command, "Standard Access List");
		commands.add("ip access-list extended", ip_access_list_extended_Command, "Extended Access List");
		commands.addDescription("ip dhcp","Configure DHCP server");
		commands.addDescription("ip dhcp pool","Configure DHCP address pools");
		commands.addDescription("ip dhcp pool *","Pool name");
		commands.add("ip dhcp pool * hardware-address", ip_dhcp_pool__hardware_address_Command, "Client hardware address");
		commands.add("ip dhcp pool * network", ip_dhcp_pool__network_Command, "Network number and mask");
		commands.add("ip dhcp pool * default-router", ip_dhcp_pool__default_router_Command, "Default routers");
		commands.add("ip dhcp excluded-address", ip_dhcp_excluded_address_Command, "Prevent DHCP from assigning certain addresses");
		commands.addDescription("ip dns","Configure DNS server for a zone");
		commands.add("ip dns primary", ip_dns_primary_Command, "Configure primary DNS server");
		commands.add("ip dns server", ip_dns_server_Command, "Enable DNS server");
		commands.add("ip host", ip_host_Command, "Add an entry to the ip hostname table");
		commands.add("ip host hinfo", ip_host_hinfo_Command, "Set system information record");
		commands.add("ip name-server", ip_name_server_Command, "Specify address of name server to use");
		commands.addDescription("ip nat","NAT configuration commands");
		commands.addDescription("ip nat inside","Inside address translation");
		//commands.addDescription("ip nat inside destination","Destination address translation");
		commands.addDescription("ip nat inside source","Source address translation");
		//commands.add("ip nat inside destination list", ip_nat_inside_destination_list_Command, "Specify access list describing global addresses");
		commands.add("ip nat inside source list", ip_nat_inside_source_list_Command, "Specify access list describing local addresses");
		commands.add("ip nat inside source static", ip_nat_inside_source_static_Command, "Specify static local->global mapping");
		commands.add("ip nat pool", ip_nat_pool_Command, "Define pool of addresses");
		commands.add("ip route", ip_route_Command, "Add route record");
		commands.addDescription("ip tcp","Global TCP parameters");
		commands.add("ip tcp window-size", ip_tcp_window_size_Command, "TCP window size");

		commands.addDescription("router","Enable a routing process");
		commands.add("router rip", router_rip_Command, "Routing Information Protocol (RIP)");
		commands.add("router rip network", router_rip_network_Command, "Add network/interface for RIP");

		new OSPFConfigurateCommands(commands, device).init();
		
		commands.addDescription("show","Show running system information");
		commands.add("show access-lists", show_access_lists_Command, "List access lists");
		commands.add("show arp", show_arp_Command, "ARP table");
		commands.add("show clock", show_clock_Command, "Display the system clock");
		commands.add("show configuration", show_configuration_Command, "Configuration details");
		commands.add("show connection", show_connection_Command, "Show Serial Connection");
		commands.addDescription("show dhcp","Dynamic Host Configuration Protocol status");
		commands.add("show dhcp lease", show_dhcp_lease_Command, "Show DHCP Addresses leased from a server");
		commands.add("show dhcp server", show_dhcp_server_Command, "Show DHCP Servers we know about");
		commands.addDescription("show dot11","Show 802.11 information");
		commands.add("show dot11 associations", show_dot11_associations_Command, "Show associated clients");
		commands.add("show hostname", show_hostname_Command, "Display name of host");
		commands.add("show interfaces", show_interfaces_Command, "Interface status and configuration");
		commands.add("show ip", show_ip_Command, "IP information");
		commands.add("show ip route", show_ip_route_Command, "Print route table");
		commands.addDescription("show ip nat","IP NAT information");
		commands.add("show ip nat translations", show_ip_nat_translations_Command, "Translation entries");
		commands.add("show kron", show_kron_Command, "Kron Subsystem");
		commands.add("show location", show_location_Command, "Display the system location");
		commands.add("show logging", show_logging_Command, "Show the contents of logging buffers");
		commands.add("show ntp", show_ntp_Command, "Network time protocol");
		commands.add("show running-config", show_running_config_Command, "Current operating configuration");
		commands.add("show sessions", show_sessions_Command, "Information about Telnet connections");
		commands.addDescription("show snmp","SNMP statistics");
		commands.add("show snmp community", show_snmp_community_Command, "show snmp community");
		commands.add("show snmp mib", show_snmp_mib_Command, "show mib objects");
		commands.add("show snmp version", show_snmp_version_Command, "show snmp version");
		commands.add("show startup-config", show_startup_config_Command, "Contents of startup configuration");
		commands.addDescription("show tcp","TCP information");
		commands.add("show tcp sessions", show_tcp_sessions_Command, "Print all TCP sessions");
		commands.add("show tcp statistics", show_tcp_statistics_Command, "TCP statistics");
		commands.add("show tcp window-size", show_tcp_window_size_Command, "TCP window-size");
		commands.addDescription("show udp","TCP information");
		commands.add("show udp sessions", show_udp_sessions_Command, "Print all UDP sessions");
		commands.add("show udp statistics", show_udp_statistics_Command, "UDP statistics");
		commands.add("show version", show_version_Command, "System hardware and software status");
		commands.add("show vlan", show_vlan_Command, "Display the parameters for all configured VLANs");

		commands.addDescription("snmp-server","Modify SNMP engine parameters");
		commands.add("snmp-server community", snmp_server_community_Command, "Enable SNMP; set community string");
		commands.add("snmp-server port", snmp_server_port_Command, "Specify server port");
		commands.add("telnet", telnet_Command, "Open a telnet connection");
		commands.add("telnet-server", telnet_server_Command, "Enable TELNET server; Specify server port");
		//commands.add("no telnet-server", telnet_server_Command, "Disable TELNET server");
		commands.add("username", username_Command, "Add User");
		commands.add("vlan", vlan_Command, "Create VLAN");
		commands.addDescription("write","Write running configuration to memory or terminal");
		commands.add("write memory", write_memory_Command, "Write to memory");
		commands.add("write terminal", write_terminal_Command, "Write to terminal");

	}

	public boolean add(String command, CommandInterface function, String description){
		return commands.add(command, function, description);
	}

	public void addDescription(String command, String description){
		commands.addDescription(command, description);
	}

	public String call(String command, Modes mode, String extend){
		String cmd = command;
		if(extend != null && !extend.equals("")){
			if(cmd.startsWith("no ")){
				cmd = "no " + extend + " " + cmd.substring(3);
			}
			else{
				cmd = extend + " " + cmd;
			}
		}
		return commands.call(cmd, mode);
	}

	public String complete(String command, Modes mode, String extend){
		String cmd = command;
		if(extend != null && !extend.equals("")){
			if(cmd.startsWith("no ")){
				cmd = "no " + extend + " " + cmd.substring(3);
			}
			else{
				cmd = extend + " " + cmd;
			}
		}
		cmd = commands.complete(cmd, mode);
		if(cmd!=null && extend != null && !extend.equals("")){
			if(cmd.startsWith("no ")){
				cmd = "no " + cmd.substring(3+extend.length()+1);
			}
			else{
				cmd = cmd.substring(extend.length()+1);
			}
		}
		return cmd;
	}

	public Vector<Pair> help(String command, Modes mode, String extend){
		String cmd = command;
		if(extend != null && !extend.equals("")){
			if(cmd.startsWith("no ")){
				cmd = "no " + extend + " " + cmd.substring(3);
			}
			else{
				cmd = extend + " " + cmd;
			}
		}
		return commands.help(cmd, mode);
	}

	/**
	 *
	 *  Commands implementation
	 *
	 */

	class NoCommandClass extends CommandInterface{
		public String call(Vector<String> params){
			return "Command not supported yet.\n";
		}
	};
	class arp_CommandClass extends CommandInterface{
		public arp_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<IP> <MAC>";
			no_call_params = "<IP>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				device.removeARP(params.get(0));
				device.addToARPStatic(params.get(0), params.get(1));
				out += "Created new static ARP entry: " + params.get(0) + " is " + params.get(1) + "\n";
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				device.removeARP(params.get(0));
				out += "Removed ARP entry for ip " + params.get(0) + "\n";
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class clear_arp_CommandClass extends CommandInterface{
		public clear_arp_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			Vector<Vector<String>> ArpTable = device.getARPTable();
			for(int i=1; i<ArpTable.size(); i++){
				device.removeARP(ArpTable.get(i).get(0));
			}
			out += "ARP table has been cleared\n";
			return out;
		}
	};
	class clock_set_CommandClass extends CommandInterface{
		public clock_set_CommandClass (){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<hh:mm:ss> <1-31> <1-12> <1970-2100>";
		}
		public String call(Vector<String> params){
			if(params.size()==4){
				device.setClock(params.get(0)+" "+params.get(1)+" "+params.get(2)+" "+params.get(3));
			}
			return "Command not supported yet.\n";
		}
	};
	class copy_running_startup_CommandClass extends CommandInterface{
		public copy_running_startup_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			boolean res = device.getConfig().writeMemory();
			String out = "Startup-config is not empty!\n";
			if(res){
				out = "Copying is complete\n";
			}
			return out;
		}
	};
	class copy_startup_running_CommandClass extends CommandInterface{
		public copy_startup_running_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			boolean res = device.getConfig().getConfig(DeviceConfig.RUNNING_CONFIG).isEmpty();
			String out = "Running-config is not empty!\n";
			if(res){
				device.Reset();
				out = "Copying is complete\n";
			}
			return out;
		}
	};
	class erase_startup_config_CommandClass extends CommandInterface{
		public erase_startup_config_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			device.getConfig().clearStartupConfig();
			return "Config was erased\n";
		}
	};
	class erase_vlan_CommandClass extends CommandInterface{
		public erase_vlan_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			NetworkLayerDevice ndev = device;
			if(params.size()==0){
				ndev.clearVlan();
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class help_CommandClass extends CommandInterface{
		public help_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return "Help may be requested at any point in a command by entering\n"+
			"a question mark '?'.  If nothing matches, the help list will\n"+
			"be empty and you must backup until entering a '?' shows the\n"+
			"available options.\n"+
			"Two styles of help are provided:\n"+
			"1. Full help is available when you are ready to enter a\n"+
			"   command argument (e.g. 'show ?') and describes each possible\n"+
			"   argument.\n"+
			"2. Partial help is provided when an abbreviated argument is entered\n"+
			"   and you want to know what arguments match the input\n"+
			"   (e.g. 'show ru?'.)\n";
		}
	};
	class hostname_CommandClass extends CommandInterface{
		public hostname_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<hostname>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				device.setName(params.get(0));
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class reload_CommandClass extends CommandInterface{
		public reload_CommandClass(){
			modes = new Modes(CommandInterface.STD_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			device.Reset();
			return "Device reloaded\n";
		}
	};
	class interface__authentication_CommandClass extends CommandInterface{
		public interface__authentication_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "(open|shared)";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						String mode = params.get(1);
						if(mode.equalsIgnoreCase("open")){
							wi.setSharedAuth(false);
							wi.refresh();
						}
						else if(mode.equalsIgnoreCase("shared")){
							wi.setSharedAuth(true);
							wi.refresh();
						}
						else{
							out += "error: invalid authentication mode\n";
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__bssid_CommandClass extends CommandInterface{
		public interface__bssid_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<BSSID>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						String bssid = params.get(1);
						if(device instanceof WirelessAP || device instanceof Router){
							if(EthernetNetworkInterface.isMacAddress(bssid)){
								wi.setBSSID(bssid);
								if(wi.isActive() && wi.isUP()){
									wi.DOWN();
									wi.UP();
								}
							}
							else{
								out += "error: incorrect BSSID\n";
							}
						}
						else{
							out += "error: command isn't support by this device\n";
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						String bssid = wi.getMACAddress();
						wi.setBSSID(bssid);
						if(wi.isActive() && wi.isUP()){
							wi.DOWN();
							wi.UP();
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__channel_CommandClass extends CommandInterface{
		public interface__channel_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<channel>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						try{
							int channel = Integer.parseInt(params.get(1));
							if(channel==0){
								wi.setChannel(0);
								if(wi.isUP()){
									wi.DOWN();
								}
							}
							else if(channel>=1 && channel<=14){
								wi.setChannel(channel);
								if(wi.isActive() && wi.isUP()){
									wi.DOWN();
									wi.UP();
								}
							}
							else{
								out += "error: channel must be a integer between 0 and 14\n";
							}
						}
						catch(NumberFormatException e){
							out += "error: channel must be a integer between 0 and 14\n";
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						wi.setChannel(0);
						if(wi.isUP()){
							wi.DOWN();
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__clock_rate_CommandClass extends CommandInterface{
		public interface__clock_rate_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<1-4000000>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					SerialNetworkInterface sni = (SerialNetworkInterface)device.getNetworkInterface(params.get(0));
					int cr = Integer.parseInt(params.get(1));
					if(cr>SerialNetworkInterface.MIN_CLOCKRATE || cr<=SerialNetworkInterface.MAX_CLOCKRATE){
						sni.setClockRate(cr);
					}
					else{
						out += "error: invalid clock rate";
					}
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (NumberFormatException ex) {
					out += "error: invalid clock rate";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					SerialNetworkInterface sni = (SerialNetworkInterface)device.getNetworkInterface(params.get(0));
					sni.setClockRate(SerialNetworkInterface.DEFAULT_CLOCKRATE);
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__description_CommandClass extends CommandInterface{
		public interface__description_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<description>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()>=2){
				try {
					String desc = params.get(1);
					for(int i=2; i<params.size(); i++){
						desc += " "+params.get(i);
					}
					device.getNetworkInterface(params.get(0)).setDescription(desc);
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).setDescription("");
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__databits_CommandClass extends CommandInterface{
		public interface__databits_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<5-8>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					int val = Integer.parseInt(params.get(1));
					if(val>=5 && val<=8){
						cni.databits = val;
					}
					else{
						out += "error: invalid databits number\n";
					}
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (NumberFormatException ex) {
					out += "error: invalid clock rate";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					cni.databits = ConsoleNetworkInterface.DATABITS_DEFAULT;
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__encryption_key_CommandClass extends CommandInterface{
		public interface__encryption_key_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "(1|2|3|4) size <40bit|128bit> <HEX key>";
			no_call_params = "(1|2|3|4)";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==5){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						try{
							int keynum = Integer.parseInt(params.get(1));
							String ssize = params.get(2);
							String sbits = params.get(3);
							int bits = 0;
							String key = params.get(4);
							if(keynum>=1 && keynum<=4){
								if(ssize.equalsIgnoreCase("size")){
									if(sbits.equalsIgnoreCase("40bit")){
										bits = 10;
									}
									else if(sbits.equalsIgnoreCase("128bit")){
										bits = 32;
									}
									if(bits>0){
										Pattern p = Pattern.compile("^[0-9A-Fa-f]{"+bits+"}$");
										Matcher m = p.matcher(key);
										if(m.matches()){
											wi.setWEPKeys(keynum, key);
											wi.refresh();
										}
										else{
											out += "error: incorrect key format\n";
										}
									}
									else{
										out += "error: unknown parameter '"+sbits+"'\n";
									}
								}
								else{
									out += "error: unknown parameter '"+ssize+"'\n";
								}
							}
							else{
								out += "error: channel must be a integer between 1 and 4\n";
							}
						}
						catch(NumberFormatException e){
							out += "error: channel must be a integer between 1 and 4\n";
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out; 
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						try{
							int keynum = Integer.parseInt(params.get(1));
							if(keynum>=1 && keynum<=4){
								wi.setWEPKeys(keynum, "");
								wi.refresh();
							}
							else{
								out += "error: channel must be a integer between 1 and 4\n";
							}
						}
						catch(NumberFormatException e){
							out += "error: channel must be a integer between 1 and 4\n";
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out; 
		}
	};
	class interface__flowcontrol_CommandClass extends CommandInterface{
		public interface__flowcontrol_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "(none|hardware|software)";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					String val = params.get(1);
					if(val.equalsIgnoreCase("none")){
						cni.flowcontrol = ConsoleNetworkInterface.FLOWCONTROL_NONE;
					}
					else if(val.equalsIgnoreCase("hardware")){
						cni.flowcontrol = ConsoleNetworkInterface.FLOWCONTROL_HARDWARE;
					}
					else if(val.equalsIgnoreCase("software")){
						cni.flowcontrol = ConsoleNetworkInterface.FLOWCONTROL_SOFTWARE;
					}
					else{
						out += "error: invalid flowcontrol value\n";
					}
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (NumberFormatException ex) {
					out += "error: invalid clock rate";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					cni.flowcontrol = ConsoleNetworkInterface.FLOWCONTROL_DEFAULT;
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__ip_access_group_CommandClass extends CommandInterface{
		public interface__ip_access_group_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<acl name> (in|out)";
			no_call_params = "<acl name> (in|out)";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==3){
				try {
					int iacl = Integer.parseInt(params.get(1));
					if(iacl>=1 && iacl<=2699){
						if(params.get(2).equalsIgnoreCase("in")){
							device.getNetworkInterface(params.get(0)).setACLin(iacl);
						}
						else if(params.get(2).equalsIgnoreCase("out")){
							device.getNetworkInterface(params.get(0)).setACLout(iacl);
						}
						else{
							out += "error: invalid ACL direction '"+params.get(2)+"'\n";
						}
					}
					else{
						out += "error: invalid ACL name '"+params.get(1)+"', ACL name must be number from 1 to 2699\n";
					}
				} catch (NumberFormatException ex) {
					out += "error: invalid ACL name '"+params.get(1)+"', ACL name must be number from 1 to 2699\n";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==3){
				try {
					int iacl = Integer.parseInt(params.get(1));
					if(iacl>=1 && iacl<=2699){
						if(params.get(2).equalsIgnoreCase("in")){
							device.getNetworkInterface(params.get(0)).setACLin(0);
						}
						else if(params.get(2).equalsIgnoreCase("out")){
							device.getNetworkInterface(params.get(0)).setACLout(0);
						}
						else{
							out += "error: invalid ACL direction '"+params.get(2)+"'\n";
						}
					}
					else{
						out += "error: invalid ACL name '"+params.get(1)+"', ACL name must be number from 1 to 2699\n";
					}
				} catch (NumberFormatException ex) {
					out += "error: invalid ACL name '"+params.get(1)+"', ACL name must be number from 1 to 2699\n";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			} 
			return out;
		}
	};
	class interface__ip_address_CommandClass extends CommandInterface{
		public interface__ip_address_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<ip address> <netmask>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==3){
				try {
					if(device.isActiveInterface(params.get(0))){
						device.setIPAddress(params.get(0), params.get(1));
						device.setCustomSubnetMask(params.get(0), params.get(2));
					}
					else{
						out += "error: interface "+params.get(0)+" is not active\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				} catch (InvalidIPAddressException ex) {
					out += "error: ip address is invalid\n";
				} catch (InvalidSubnetMaskException ex) {
					out += "error: subnet mast is invalid\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					if(device.isActiveInterface(params.get(0))){
						device.setIPAddress(params.get(0), "");
						device.setCustomSubnetMask(params.get(0), "");
					}
					else{
						out += "error: interface "+params.get(0)+" is not active\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				} catch (InvalidIPAddressException ex) {
					out += "error: ip address is invalid\n";
				} catch (InvalidSubnetMaskException ex) {
					out += "error: subnet mast is invalid\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__ip_nat_CommandClass extends CommandInterface{
		public interface__ip_nat_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "(inside|outside)";
			no_call_params = "(inside|outside)";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				if(params.size()==2){
					try {
						NetworkInterface ni = device.getNetworkInterface(params.get(0));
						if(device.isActiveInterface(params.get(0))){
							String natt = params.get(1);
							if(natt.equalsIgnoreCase("inside")){
								ni.setNAT(NetworkInterface.INSIDE_NAT);
							}
							else if(natt.equalsIgnoreCase("outside")){
								ni.setNAT(NetworkInterface.OUTSIDE_NAT);
							}
							else{
								out += "error: invalid parameter '"+natt+"'\n";
							}
						}
						else{
							out += "error: interface "+params.get(0)+" is not active\n";
						}
					} catch (InvalidNetworkInterfaceNameException ex) {
						out += "error: invalid inferface\n";
					}
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}  
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				if(params.size()==2 || params.size()==1){
					try {
						NetworkInterface ni = device.getNetworkInterface(params.get(0));
						if(device.isActiveInterface(params.get(0))){
							if(params.size()==1){
								ni.setNAT(NetworkInterface.NO_NAT);
							}
							else{
								String natt = params.get(1);
								if(natt.equalsIgnoreCase("inside") && ni.getNAT()==NetworkInterface.INSIDE_NAT){
									ni.setNAT(NetworkInterface.NO_NAT);
								}
								else if(natt.equalsIgnoreCase("outside") && ni.getNAT()==NetworkInterface.OUTSIDE_NAT){
									ni.setNAT(NetworkInterface.NO_NAT);
								}
								else{
									out += "error: invalid parameter '"+natt+"'\n";
								}
							}
						}
						else{
							out += "error: interface "+params.get(0)+" is not active\n";
						}
					} catch (InvalidNetworkInterfaceNameException ex) {
						out += "error: invalid inferface\n";
					}
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}  
			return out;
		}
	};
	class interface__ip_unreachables_CommandClass extends CommandInterface{
		public interface__ip_unreachables_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).unreachables = true;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).unreachables = false;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__ip_redirects_CommandClass extends CommandInterface{
		public interface__ip_redirects_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).redirects = true;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).redirects = false;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__ip_mask_replay_CommandClass extends CommandInterface{
		public interface__ip_mask_replay_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).maskReplay = true;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).maskReplay = false;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__ip_information_replay_CommandClass extends CommandInterface{
		public interface__ip_information_replay_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).informationReplay = true;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).informationReplay = false;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__ip_dhcp_client_CommandClass extends CommandInterface{
		public interface__ip_dhcp_client_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPC dhcpc = (DHCPC)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_CLIENT_ID);
				if(dhcpc!=null){
					if(params.size()==1){
						try {
							device.getNetworkInterface(params.get(0));
							dhcpc.StartDHCPC(params.get(0), "169.254.0.1");
						} catch (InvalidNetworkInterfaceNameException ex) {
							out += "Invalid interface name\n";
						} catch (CommunicationException ex) {
							out += "communication error\n";
							//ex.printStackTrace();
						} catch (LowLinkException ex) {
							out += "low link error\n";
							//ex.printStackTrace();
						} catch (TransportLayerException ex) {
							out += "transport error\n";
							//ex.printStackTrace();
						} catch (InvalidNetworkLayerDeviceException ex) {
							out += "network error\n";
							//ex.printStackTrace();
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}  
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPC dhcpc = (DHCPC)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_CLIENT_ID);
				if(dhcpc!=null){
					if(params.size()==1){
						try {
							if(device.getNetworkInterface(params.get(0))!=null){
								dhcpc.Close();
							}
						} catch (InvalidNetworkInterfaceNameException ex) {
							out += "Invalid interface name\n";
						} catch (TransportLayerException ex) {
							//ex.printStackTrace();
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}  
			return out;
		}
	};
	class interface__mac_address_CommandClass extends CommandInterface{
		public interface__mac_address_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<MAC>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					if(device.isActiveInterface(params.get(0))){
						if(EthernetNetworkInterface.isMacAddress(params.get(1))){
							device.setMACAddress(params.get(0), params.get(1));
						}
						else{
							out += "error: incorrect MAC address\n";
						}
					}
					else{
						out += "error: interface "+params.get(0)+" is not active\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					if(device.isActiveInterface(params.get(0))){
						device.setMACAddress(params.get(0), null);
					}
					else{
						out += "error: interface "+params.get(0)+" is not active\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__parity_CommandClass extends CommandInterface{
		public interface__parity_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "(none|even|odd|mark|space)";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					String val = params.get(1);
					if(val.equalsIgnoreCase("none")){
						cni.parity = ConsoleNetworkInterface.PARITY_NONE;
					}
					else if(val.equalsIgnoreCase("even")){
						cni.parity = ConsoleNetworkInterface.PARITY_EVEN;
					}
					else if(val.equalsIgnoreCase("odd")){
						cni.parity = ConsoleNetworkInterface.PARITY_ODD;
					}
					else if(val.equalsIgnoreCase("mark")){
						cni.parity = ConsoleNetworkInterface.PARITY_MARK;
					}
					else if(val.equalsIgnoreCase("space")){
						cni.parity = ConsoleNetworkInterface.PARITY_SPACE;
					}
					else{
						out += "error: invalid parity value\n";
					}
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (NumberFormatException ex) {
					out += "error: invalid clock rate";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					cni.parity = ConsoleNetworkInterface.PARITY_DEFAULT;
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__shutdown_CommandClass extends CommandInterface{
		public interface__shutdown_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).DOWN();
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					device.getNetworkInterface(params.get(0)).UP();
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__speed_CommandClass extends CommandInterface{
		public interface__speed_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<1-2147483647>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					int val = Integer.parseInt(params.get(1));
					if(val>0){
						cni.speed = val;
					}
					else{
						out += "error: invalid speed\n";
					}
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (NumberFormatException ex) {
					out += "error: invalid clock rate";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					cni.speed = ConsoleNetworkInterface.SPEED_DEFAULT;
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__ssid_CommandClass extends CommandInterface{
		public interface__ssid_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<SSID>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						String ssid = params.get(1);
						wi.setSSID(ssid);
						wi.refresh();
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						wi.setSSID("default");
						wi.refresh();
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__station_role_CommandClass extends CommandInterface{
		public interface__station_role_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "(client|repeater|root access-point)";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2 || params.size()==3){
				try {
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof WiFiPort){
						WiFiPort wi = (WiFiPort)ni;
						String role = params.get(1);
						if(role.equalsIgnoreCase("client")){
							wi.setMode(WiFiPort.MODE_STATION);
							if(wi.isActive() && wi.isUP()){
								wi.DOWN();
								wi.UP();
							}
						}
						else if(role.equalsIgnoreCase("repeater")){
							if(false && (device instanceof WirelessAP || device instanceof Router)){
								wi.setMode(WiFiPort.MODE_REPEATER);
								if(wi.isActive() && wi.isUP()){
									wi.DOWN();
									wi.UP();
								}
							}
							else{
								out += "error: unsupported station role 'repeater' for this device\n";
							}
						}
						else if(role.equalsIgnoreCase("root") && params.size()==3 && params.get(2).equalsIgnoreCase("access-point")){
							if(device instanceof WirelessAP || device instanceof Router){
								wi.setMode(WiFiPort.MODE_AP);
								if(wi.isActive() && wi.isUP()){
									wi.DOWN();
									wi.UP();
								}
							}
							else{
								out += "error: unsupported station role 'root access-point' for this device\n";
							}
						}
						else{
							out += "error: incorrect station role\n";
						}
					}
					else{
						out += "error: this command applicable only for WiFi interfaces\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__stopbits_CommandClass extends CommandInterface{
		public interface__stopbits_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "(1|1.5|2)";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==2){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					String val = params.get(1);
					if(val.equals("1")){
						cni.stopbits = ConsoleNetworkInterface.STOPBIT_1;
					}
					else if(val.equals("1.5")){
						cni.stopbits = ConsoleNetworkInterface.STOPBIT_15;
					}
					else if(val.equals("2")){
						cni.stopbits = ConsoleNetworkInterface.STOPBIT_2;
					}
					else{
						out += "error: invalid stopbits value\n";
					}
				} catch (ClassCastException ex){
					out += "unsupposed instruction for this interface\n";
				} catch (NumberFormatException ex) {
					out += "error: invalid clock rate";
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try {
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)device.getNetworkInterface(params.get(0));
					cni.stopbits = ConsoleNetworkInterface.STOPBIT_DEFAULT;
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__switchport_access_vlan_CommandClass extends CommandInterface{
		public interface__switchport_access_vlan_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<vlanid>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			if((params.size()==2 && add) || (params.size()==1 && !add)){
				try{
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof NetworkInterfacePort){
						NetworkInterfacePort eni = (NetworkInterfacePort) ni;
						if(add){
							try{
								int vlanid = Integer.parseInt(params.get(1));
								if(vlanid<=0) throw new NumberFormatException();
								eni.vlan = vlanid;
							}
							catch(NumberFormatException e){
								out += "error: invalid vlan number\n";
							}
						}
						else{
							eni.vlan = 1;
						}
					}
					else{
						out += "error: only ethernet interfaces is allowed\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__switchport_description_CommandClass extends CommandInterface{
		public interface__switchport_description_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<description>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			if((params.size()>1 && add) || (params.size()==1 && !add)){
				try{
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof NetworkInterfacePort){
						NetworkInterfacePort eni = (NetworkInterfacePort) ni;
						if(add){
							String desc = params.get(1);
							for(int i=2; i<params.size(); i++){
								desc += " "+params.get(i);
							}
							eni.description = desc;
						}
						else{
							eni.description = "";
						}
					}
					else{
						out += "error: only ethernet interfaces is allowed\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__switchport_mode_access_CommandClass extends CommandInterface{
		public interface__switchport_mode_access_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try{
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof NetworkInterfacePort){
						NetworkInterfacePort eni = (NetworkInterfacePort) ni;
						eni.mode = NetworkInterfacePort.MODE_ACCESS;
					}
					else{
						out += "error: only ethernet interfaces is allowed\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class interface__switchport_mode_trunk_CommandClass extends CommandInterface{
		public interface__switchport_mode_trunk_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				try{
					NetworkInterface ni = device.getNetworkInterface(params.get(0));
					if(ni instanceof NetworkInterfacePort){
						NetworkInterfacePort eni = (NetworkInterfacePort) ni;
						eni.mode = NetworkInterfacePort.MODE_TRUNK;
					}
					else{
						out += "error: only ethernet interfaces is allowed\n";
					}
				} catch (InvalidNetworkInterfaceNameException ex) {
					out += "error: invalid inferface\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class ip_access_list_standart_CommandClass extends CommandInterface{
		public ip_access_list_standart_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<1-99> [<line>] (permit|deny) (any|host <ip>|<ip> <mask>) [log]";
			no_call_params = "<1-99> (<line>|[<line>] (permit|deny) (any|host <ip>|<ip> <mask>) [log])";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			try{
				int iacl = Integer.parseInt(params.get(0));
				if(iacl>=1 && iacl<=2699){
					int iline = -1;
					int index = 1;
					try{
						iline = Integer.parseInt(params.get(index));
						index++;
					} catch(NumberFormatException e){ }

					if(iline>0 && !add){
						device.getACL().removeACL(iacl, iline);
					}
					else{
						String permit_deny = params.get(index);
						short action=-1;
						if(permit_deny.equalsIgnoreCase("permit")){
							action = AccessListEngine.access_list.PERMIT;
						}
						else if(permit_deny.equalsIgnoreCase("deny")){
							action = AccessListEngine.access_list.DENY;
						}

						if(action>-1){
							index+=1;
							String ip;
							String mask;
							String hosts = params.get(index);
							if(hosts.equalsIgnoreCase("any")){
								ip = "0.0.0.0";
								mask = "0.0.0.0";
								index+=1;
							}
							else if(hosts.equals("host")){
								ip = params.get(index+1);
								mask = "255.255.255.255";
								index+=2;
							}
							else{
								ip = hosts;
								mask = params.get(index+1);
								index+=2;
							}

							boolean log = false;
							if(params.size()>index){
								if(params.get(index).equalsIgnoreCase("log"))
									log = true;
							}
							//device.getConfig().add("ip access-list "+iacl+" "+permit_deny+" "+hosts);
							if((IPV4Address.isValidIp(ip) || ip.compareTo("0.0.0.0")==0) && IPV4Address.validateDecSubnetMask(mask)){
								if(add){
									device.getACL().addACL(iacl, iline, action, ip, mask, log);
								}
								else{
									if(iline==-1){
										device.getACL().removeACL(iacl, action, ip, mask);
									}
									else{
										device.getACL().removeACL(iacl, iline);
									}
								}
							}
							else{
								out += "error: invalid IP address or subnet mask\n";
							}
						}
						else{
							out += "error: invalid parameter: '"+permit_deny+"'\n";
						}
					}
				}
				else{
					out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
				}
			}
			catch(ArrayIndexOutOfBoundsException e){
				out += "error: invalid parameters\n";
			}
			catch(NumberFormatException e){
				out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
			}
			return out;
		}
	};
	class ip_access_list_extended_CommandClass extends CommandInterface{
		public ip_access_list_extended_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			//call_params = "<100-2699> [<line>] (permit|deny) (ip|tcp|udp|icmp) (any|host <ip>|<ip> <mask>) (any|host <ip>|<ip> <mask>) [(eq|lt|gt|neq) <port>]";
			call_params = "<100-2699> [<line>] (permit|deny) (ip|tcp|udp|icmp) (any|host <ip>|<ip> <mask>) (any|host <ip>|<ip> <mask>) [eq <port>] [log]";
			no_call_params = "<100-2699> (<line>|[<line>] (permit|deny) (ip|tcp|udp|icmp) (any|host <ip>|<ip> <mask>) (any|host <ip>|<ip> <mask>) [eq <port>] [log])";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			try{
				int iacl = Integer.parseInt(params.get(0));
				if(iacl>=1 && iacl<=2699){
					int iline = -1;
					int index = 1;
					try{
						iline = Integer.parseInt(params.get(index));
						index++;
					} catch(NumberFormatException e){ }

					if(iline>0 && !add){
						device.getACL().removeACL(iacl, iline);
					}
					else{
						String permit_deny = params.get(index);
						short action=-1;
						if(permit_deny.equalsIgnoreCase("permit")){
							action = AccessListEngine.access_list.PERMIT;
						}
						else if(permit_deny.equalsIgnoreCase("deny")){
							action = AccessListEngine.access_list.DENY;
						}

						if(action>-1){
							int protocol = -1;
							index++;
							String sprotocol = params.get(index);
							if(sprotocol.equalsIgnoreCase("ip")){
								protocol = AccessListEngine.access_list.IP;
							}
							else if(sprotocol.equalsIgnoreCase("tcp")){
								protocol = AccessListEngine.access_list.TCP;
							}
							else if(sprotocol.equalsIgnoreCase("udp")){
								protocol = AccessListEngine.access_list.UDP;
							}
							else if(sprotocol.equalsIgnoreCase("icmp")){
								protocol = AccessListEngine.access_list.ICMP;
							}
							if(protocol>-1){

								String ip1;
								String mask1;
								String ip2;
								String mask2;
								index++;
								String hosts = params.get(index);
								if(hosts.equalsIgnoreCase("any")){
									ip1 = "0.0.0.0";
									mask1 = "0.0.0.0";
									index++;
								}
								else if(hosts.equals("host")){
									ip1 = params.get(index+1);
									mask1 = "255.255.255.255";
									index+=2;
								}
								else{
									ip1 = hosts;
									mask1 = params.get(index+1);
									index+=2;
								}

								hosts = params.get(index);
								if(hosts.equalsIgnoreCase("any")){
									ip2 = "0.0.0.0";
									mask2 = "0.0.0.0";
									index++;
								}
								else if(hosts.equals("host")){
									ip2 = params.get(index+1);
									mask2 = "255.255.255.255";
									index+=2;
								}
								else{
									ip2 = hosts;
									mask2 = params.get(index+1);
									index+=2;
								}

								int port = 0;
								boolean log = false;
								if(params.size()>index){
									if(params.get(index).equalsIgnoreCase("eq")){
										try{
											port = Integer.parseInt(params.get(index+1));
											index += 2;
										}
										catch(NumberFormatException e){
											out += "error: invalid parameter port = '"+params.get(index+1)+"'\n";
											port = -1;
										}
									}
									else if(params.get(index).equalsIgnoreCase("log")){
										log = true;
										index += 1;
									}
									else{
										out += "error: invalid parameter: '"+params.get(index)+"'\n";
										port = -1;
									}
								}

								if(!log && params.size()>index){
									if(params.get(index).equalsIgnoreCase("log"))
										log = true;
								}

								if(port >= 0){                            
									if((IPV4Address.isValidIp(ip1) || ip1.compareTo("0.0.0.0")==0) && IPV4Address.validateDecSubnetMask(mask1) &&
											(IPV4Address.isValidIp(ip2) || ip2.compareTo("0.0.0.0")==0) && IPV4Address.validateDecSubnetMask(mask2)){
										if(port==0 || (protocol == AccessListEngine.access_list.TCP || protocol == AccessListEngine.access_list.UDP)){
											if(add){
												device.getACL().addACL(iacl, iline, action, protocol, ip1, mask1, ip2, mask2, port, log);
											}
											else{
												if(iline==-1){
													device.getACL().removeACL(iacl, action, protocol, ip1, mask1, ip2, mask2, port);
												}
												else{
													device.getACL().removeACL(iacl, iline);
												}
											}
										}
										else{
											out += "error: invalid parameter 'eq "+port+"'\n";
											port = -1;
										}
									}
									else{
										out += "error: invalid IP address or subnet mask\n";
									}
								}
							}
							else{
								out += "error: invalid protocol: '"+sprotocol+"'\n";
							}
						}
						else{
							out += "error: invalid action: '"+permit_deny+"'\n";
						}
					}
				}
				else{
					out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
				}
			}
			catch(ArrayIndexOutOfBoundsException e){
				out += "error: invalid parameters\n";
			}
			catch(NumberFormatException e){
				out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
			}
			return out;
		}
	};
	class ip_dhcp_pool__default_router_CommandClass extends CommandInterface{
		public ip_dhcp_pool__default_router_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<router ip>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==2){
						String poolname = params.get(0);
						String defrouter = params.get(1);
						if(IPV4Address.isValidIp(defrouter)){
							if(!dhcpd.pools.containsKey(poolname)){
								dhcpd.pools.put(poolname, dhcpd.new_pool());
							}
							DHCPD.pool pool = dhcpd.pools.get(poolname);
							pool.Gateway = defrouter;
						}
						else{
							out += "error: invalid gateway ip address\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==1){
						String poolname = params.get(0);
						if(dhcpd.pools.containsKey(poolname)){
							DHCPD.pool pool = dhcpd.pools.get(poolname);
							pool.Gateway=null;
						}
						else{
							out += "error: pool "+poolname+" not exists\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_dhcp_pool__hardware_address_CommandClass extends CommandInterface{
		public ip_dhcp_pool__hardware_address_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<MAC>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==2){
						String poolname = params.get(0);
						String mac = params.get(1);
						if(EthernetNetworkInterface.isMacAddress(mac)){
							if(!dhcpd.pools.containsKey(poolname)){
								dhcpd.pools.put(poolname, dhcpd.new_pool());
							}
							DHCPD.pool pool = dhcpd.pools.get(poolname);
							pool.MAC = mac;
						}
						else{
							out += "error: invalid hardware address\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==1){
						String poolname = params.get(0);
						if(dhcpd.pools.containsKey(poolname)){
							DHCPD.pool pool = dhcpd.pools.get(poolname);
							pool.MAC=null;
						}
						else{
							out += "error: pool "+poolname+" not exists\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_dhcp_pool__network_CommandClass extends CommandInterface{
		public ip_dhcp_pool__network_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<network ip> <netmask>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==3){
						String poolname = params.get(0);
						String ip = params.get(1);
						String mask = params.get(2);
						if(IPV4Address.validateDecSubnetMask(ip) && IPV4Address.validateDecSubnetMask(mask)){
							if(!dhcpd.pools.containsKey(poolname)){
								dhcpd.pools.put(poolname, dhcpd.new_pool());
							}
							DHCPD.pool pool = dhcpd.pools.get(poolname);
							pool.IP = ip;
							pool.Genmask = mask;
							if(dhcpd.running){
								try {
									dhcpd.Close();
									dhcpd.Listen();
								} catch (TransportLayerException ex) {
									out += "error: dhcp server has not started to listen";
								}
							}
							else{
								try {
									dhcpd.Listen();
								} catch (TransportLayerException ex) {
									out += "error: dhcp server has not started to listen";
								}
							}
						}
						else{
							out += "error: invalid ip address or net mask\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==1){
						String poolname = params.get(0);
						if(dhcpd.pools.containsKey(poolname)){
							dhcpd.pools.remove(poolname);
							if(dhcpd.running){
								try {
									dhcpd.Close();
									if(!dhcpd.pools.isEmpty()) dhcpd.Listen();
								} catch (TransportLayerException ex) {
									out += "error: dhcp server has not started to listen";
								}
							}
							else{
								try {
									if(!dhcpd.pools.isEmpty()) dhcpd.Listen();
								} catch (TransportLayerException ex) {
									out += "error: dhcp server has not started to listen";
								}
							}
						}
						else{
							out += "error: pool "+poolname+" not exists\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_dhcp_excluded_address_CommandClass extends CommandInterface{
		public ip_dhcp_excluded_address_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<low ip> <high ip>";
			no_call_params = "<low ip> <high ip>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==2){
						String low_ip = params.get(0);
						String high_ip = params.get(1);
						if(IPV4Address.isValidIp(low_ip) && IPV4Address.isValidIp(high_ip)){
							dhcpd.excludeAddress(low_ip, high_ip);
						}
						else{
							out += "error: invalid ip address\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==2){
						String low_ip = params.get(0);
						String high_ip = params.get(1);
						if(IPV4Address.isValidIp(low_ip) && IPV4Address.isValidIp(high_ip)){
							dhcpd.no_excludeAddress(low_ip, high_ip);
						}
						else{
							out += "error: invalid ip address\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_dns_primary_CommandClass extends CommandInterface{
		public ip_dns_primary_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<DNS domain name> soa <DNS primary name server>\n"+
			"    <DNS mailbox of responsible person> <Refresh time>\n"+
			"    <Refresh retry time> <Authority expire time> <Minimum TTL for zone info>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
				if(dns!=null){
					if(params.size()==8){
						String DomainName = params.get(0);
						String soa = params.get(1);
						String PrimaryNameServer = params.get(2);
						String MailboxResponsiblePerson = params.get(3);
						int RefreshTime = 0;
						int RefreshRetryTime = 0;
						int AuthorityExpireTime = 0;
						int MinimumTTLZoneInfo = 0;
						try{RefreshTime = Integer.parseInt(params.get(4));}catch(NumberFormatException e){}
						try{RefreshRetryTime = Integer.parseInt(params.get(5));}catch(NumberFormatException e){}
						try{AuthorityExpireTime = Integer.parseInt(params.get(6));}catch(NumberFormatException e){}
						try{MinimumTTLZoneInfo = Integer.parseInt(params.get(7));}catch(NumberFormatException e){}
						if(DNS.isValidName(DomainName)){
							if(soa.equalsIgnoreCase("soa")){
								if(DNS.isValidName(PrimaryNameServer)){
									if(DNS.isValidMail(MailboxResponsiblePerson)){
										if(DNS.isValidMail(MailboxResponsiblePerson)){
											if(RefreshTime>0){
												if(RefreshRetryTime>0){
													if(AuthorityExpireTime>0){
														if(MinimumTTLZoneInfo>0){
															dns.DomainName = DomainName;
															dns.PrimaryNameServer = PrimaryNameServer;
															dns.MailboxResponsiblePerson = MailboxResponsiblePerson;
															dns.RefreshTime = RefreshTime;
															dns.RefreshRetryTime = RefreshRetryTime;
															dns.AuthorityExpireTime = AuthorityExpireTime;
															dns.MinimumTTLZoneInfo = MinimumTTLZoneInfo;
														}
														else{
															out += "error: invalid Minimum TTL for zone info (must be between 1 and 2147483647)\n";
														}
													}
													else{
														out += "error: invalid Authority expire time (must be between 1 and 2147483647)\n";
													}
												}
												else{
													out += "error: invalid Refresh retry time (must be between 1 and 2147483647)\n";
												}
											}
											else{
												out += "error: invalid Refresh time (must be between 1 and 2147483647)\n";
											}
										}
										else{
											out += "error: invalid DNS mailbox of responsible person\n";
										}
									}
									else{
										out += "error: invalid DNS mailbox of responsible person\n";
									}
								}
								else{
									out += "error: invalid DNS primary name server\n";
								}
							}
							else{
								out += "error: keyword 'soa' expected\n";
							}
						}
						else{
							out += "error: invalid DNS domain name\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
				if(dns!=null){
					if(params.size()==0){
						dns.DomainName = "";
						dns.PrimaryNameServer = "";
						dns.MailboxResponsiblePerson = "";
						dns.RefreshTime = 86400;
						dns.RefreshRetryTime = 3600;
						dns.AuthorityExpireTime = 1209600;
						dns.MinimumTTLZoneInfo = 86400;
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_dns_server_CommandClass extends CommandInterface{
		public ip_dns_server_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "[<port>]";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
				if(dns!=null){
					if(params.size()==0 || params.size()==1){
						int port = 53;
						if(params.size()==1) port = Integer.parseInt(params.get(0));
						dns.setPort(port);
						try {
							dns.Listen();
						} catch (TransportLayerException e) {
							out += "error: DNS server couldn't stated\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
				if(dns!=null){
					if(params.size()==0){
						try {
							dns.Close();
						} catch (TransportLayerException e) {
							out += "error: DNS server couldn't stopped\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_host_CommandClass extends CommandInterface{
		public ip_host_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "(<name of host> <IP address>|<IP address> <name of host>|<name of domain> mx <priority> <name of host>|<name of host> cname <name of host>)";
			no_call_params = "(<name of host> <IP address>|<IP address> <name of host>|<name of domain> mx <priority> <name of host>|<name of host> cname <name of host>)";
		}
		public String call(Vector<String> params){
			return parse(params,true);
		}
		public String no_call(Vector<String> params){
			return parse(params,false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
				if(dns!=null){
					if(params.size()>=2 && params.size()<=4){
						switch(params.size()){
						case 2:{
							String first = params.get(0);
							String second = params.get(1);
							if(IPV4Address.isValidIp(first) && DNS.isValidName(second)){
								if(add)
									dns.addRecord(DNS.toInAddrArpa(first), second, DNS_Message.PTR_QUERY_TYPE);
								else
									dns.removeRecord(DNS.toInAddrArpa(first), second, DNS_Message.PTR_QUERY_TYPE);
							}
							else if(IPV4Address.isValidIp(second) && DNS.isValidName(first)){
								if(add)
									dns.addRecord(first, second, DNS_Message.A_QUERY_TYPE);
								else
									dns.removeRecord(first, second, DNS_Message.A_QUERY_TYPE);
							}
							else{
								out += "error: invalid A or PTR record\n";
							}
							break;
						}
						case 3:{
							String host1 = params.get(0);
							String cname = params.get(1);
							String host2 = params.get(2);
							if(cname.equalsIgnoreCase("cname")){
								if(DNS.isValidName(host1) && DNS.isValidName(host2)){
									if(add)
										dns.addRecord(host1, host2, DNS_Message.CNAME_QUERY_TYPE);
									else
										dns.removeRecord(host1, host2, DNS_Message.CNAME_QUERY_TYPE);
								}
								else{
									out += "error: invalid host name\n";
								}
							}
							else{
								out += "error: keyword 'cname' expected\n";
							}
							break;
						}
						case 4:{
							String domain = params.get(0);
							String mx = params.get(1);
							String priority = params.get(2);
							String host = params.get(3);
							if(mx.equalsIgnoreCase("mx")){
								if(DNS.isValidName(domain) && DNS.isValidName(host)){
									try{
										int pr = Integer.parseInt(priority);
										if(pr>0 && pr<65536)
											if(add)
												dns.addRecord(domain, host+":"+priority, DNS_Message.MX_QUERY_TYPE);
											else
												dns.removeRecord(domain, host, DNS_Message.MX_QUERY_TYPE);
										else
											out += "error: priority must be between 1 and 65535\n";
									}
									catch(NumberFormatException e){
										out += "error: priority must be between 1 and 65535\n";
									}
								}
								else{
									out += "error: invalid host name\n";
								}
							}
							else{
								out += "error: keyword 'mx' expected\n";
							}
							break;
						}
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_host_hinfo_CommandClass extends CommandInterface{
		public ip_host_hinfo_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<name of host> <description>";
			no_call_params = "<name of host>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
				if(dns!=null){
					if(params.size()>1){
						String dname = params.get(0);
						if(DNS.isValidName(dname)){
							String info = params.get(1);
							for(int i=2; i<params.size(); i++){
								info += " "+params.get(i);
							}
							dns.addRecord(dname, info, DNS_Message.HINFO_QUERY_TYPE);
						}
						else{
							out += "error: invalid domain name\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
				if(dns!=null){
					if(params.size()==1){
						String dname = params.get(0);
						if(DNS.isValidName(dname)){
							dns.removeRecord(dname, null, DNS_Message.HINFO_QUERY_TYPE);
						}
						else{
							out += "error: invalid domain name\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class ip_name_server_CommandClass extends CommandInterface{
		public ip_name_server_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<name-server ip-address>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				if(params.size()==1){
					String ns = params.get(0);
					if(IPV4Address.isValidIp(ns)){
						((ApplicationLayerDevice)device).setNameServer(ns);
					}
					else{
						out += "error: invalid IP address\n";
					}
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				if(params.size()==0){
					((ApplicationLayerDevice)device).setNameServer("");
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	/*class ip_nat_inside_destination_list_CommandClass extends CommandInterface{
		public ip_nat_inside_destination_list_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<1-2699> pool <pool> [overload]";
			no_call_params = "<1-2699> pool <pool>";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			if(params.size()==3 || params.size()==4){
				try{
					int iacl = Integer.parseInt(params.get(0));
					if(iacl>=1 && iacl<=2699){
						if(params.get(1).equalsIgnoreCase("pool")){
							String poolname = params.get(2);
							boolean overload = false;
							if(params.size()==4){
								overload = params.get(3).equalsIgnoreCase("overload");
							}
							if(add){
								//nat.inside.dest.list.add(iacl,poolname,overload);
							}
							else{
								//nat.inside.dest.list.remove(iacl,poolname);
							}
						}
						else{
							out += "error: invalid parameter '"+params.get(1)+"'\n";
						}
					}
					else{
						out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
					}
				}
				catch(NumberFormatException e){
					out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};*/
	class ip_nat_inside_source_list_CommandClass extends CommandInterface{
		public ip_nat_inside_source_list_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<1-2699> (pool <pool>|interface <interface>) overload";
			no_call_params = "<1-2699> (pool <pool>|interface <interface>)";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			if(params.size()==3 || params.size()==4){
				try{
					int iacl = Integer.parseInt(params.get(0));
					if(iacl>=1 && iacl<=2699){
						if(params.get(1).equalsIgnoreCase("pool")){
							String poolname = params.get(2);
							boolean overload = false;
							if(params.size()==4){
								overload = params.get(3).equalsIgnoreCase("overload");
							}
							if(add){
								if(overload){
									NAT_rule rule = device.getNAT().new NAT_rule();
									rule.dynamic = true;
									rule.pool = true;
									rule.acl = iacl;
									rule.out_int = poolname;
									device.getNAT().addRule(rule);
								}
								else{
									out += "error: parameter 'overload' expected\n";
								}
							}
							else{
								Vector<Integer> finds = device.getNAT().findRules(true, true, iacl, "", 0, "", 0, poolname);
								if(finds.size()>0){
									device.getNAT().removeRule(finds.get(0).intValue(), true);
								}
								else{
									out += "error: record not found\n";
								}
							}
						}
						else if(params.get(1).equalsIgnoreCase("interface")){
							String intname = params.get(2);
							try {
								device.getNetworkInterface(intname);
								boolean overload = false;
								if(params.size()==4){
									overload = params.get(3).equalsIgnoreCase("overload");
								}
								if(add){
									if(overload){
										NAT_rule rule = device.getNAT().new NAT_rule();
										rule.dynamic = true;
										rule.pool = false;
										rule.acl = iacl;
										rule.out_int = intname;
										device.getNAT().addRule(rule);
									}
									else{
										out += "error: parameter 'overload' expected\n";
									}
								}
								else{
									Vector<Integer> finds = device.getNAT().findRules(true, false, iacl, "", 0, "", 0, intname);
									if(finds.size()>0){
										device.getNAT().removeRule(finds.get(0).intValue(), true);
									}
									else{
										out += "error: record not found\n";
									}
								}
							} catch (InvalidNetworkInterfaceNameException e) {
								out += "error: invalid interface name\n";
							}
						}
						else{
							out += "error: invalid parameter '"+params.get(1)+"'\n";
						}
					}
					else{
						out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
					}
				}
				catch(NumberFormatException e){
					out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class ip_nat_inside_source_static_CommandClass extends CommandInterface{
		public ip_nat_inside_source_static_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "(ip|tcp|udp) <local ip> [<local port>] <global ip> [<global port>]";
			no_call_params = "(ip|tcp|udp) <local ip> [<local port>] <global ip> [<global port>]";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			int IP_protocol = NAT_rule.IP;
			int TCP_protocol = NAT_rule.TCP;
			int UDP_protocol = NAT_rule.UDP;

			String out = "";
			if(params.size()==3 || params.size()==5){
				try{
					boolean valid = true;
					String sprotocol = params.get(0);
					int protocol = -1;
					int local_port = 0;
					int global_port = 0;
					String local_ip = "";
					String global_ip = "";
					if(sprotocol.equalsIgnoreCase("ip")){
						protocol = IP_protocol;
					}
					else if(sprotocol.equalsIgnoreCase("tcp")){
						protocol = TCP_protocol;
					}
					else if(sprotocol.equalsIgnoreCase("udp")){
						protocol = UDP_protocol;
					}
					if(params.size()==3){
						local_ip = params.get(1);
						global_ip = params.get(2);
					}
					else if(params.size()==5 && (protocol==TCP_protocol || protocol==UDP_protocol)){
						local_ip = params.get(1);
						local_port = Integer.parseInt(params.get(2));
						global_ip = params.get(3);
						global_port = Integer.parseInt(params.get(4));
						if(local_port<=0 || global_port<=0 || local_port>65535 || global_port>65535){
							throw new NumberFormatException();
						}
					}
					else{
						valid = false;
					}
					if(valid && IPV4Address.isValidIp(local_ip) && IPV4Address.isValidIp(global_ip)){
						if(add){
							NAT_rule rule = device.getNAT().new NAT_rule();
							rule.in_ip = local_ip;
							rule.out_ip = global_ip;
							rule.in_port = local_port;
							rule.out_port = local_port;
							rule.protocol = protocol;
							rule.dynamic = false;
							rule.pool = false;
							device.getNAT().addRule(rule);
						}
						else{
							Vector<Integer> finds;
							if(params.size()==3)
								finds = device.getNAT().findRules(false, false, 0, local_ip, 0, global_ip, 0, "");
							else
								finds = device.getNAT().findRules(false, false, 0, local_ip, local_port, global_ip, global_port, "");
							if(finds.size()>0){
								device.getNAT().removeRule(finds.get(0).intValue(), false);
							}
							else{
								out += "error: nat rule not found\n";
							}
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				catch(NumberFormatException e){
					out += "error: invalid port number\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class ip_nat_pool_CommandClass extends CommandInterface{
		public ip_nat_pool_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<pool> <low ip> <high ip>";
			no_call_params = "<pool>";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			if(params.size()==3 && add){
				String poolname = params.get(0);
				String low_ip = params.get(1);
				String high_ip = params.get(2);
				if(IPV4Address.isValidIp(low_ip) && IPV4Address.isValidIp(high_ip)){
					device.getNAT().addPool(poolname, new Pair(low_ip,high_ip));
				}
				else{
					out += "error: invalid IP address\n";
				}
			}
			else if(params.size()==1 && !add){
				String poolname = params.get(0);
				device.getNAT().removePool(poolname);
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class ip_route_CommandClass extends CommandInterface{
		public ip_route_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "(<host ip>|<network ip>) <netmask> <gateway> [<target interface>]";
			no_call_params = "(<host ip>|<network ip>)";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==4 || params.size()==3){
				String dest = params.get(0);
				String mask = params.get(1);
				if((dest.compareToIgnoreCase("default")==0 || dest.equals("0.0.0.0")) && mask.equals("0.0.0.0")){
					try {
						device.setDefaultGateway(params.get(2));
						out+="Default gateway added.\n";
					} catch (InvalidNodeNameException ex) {
						out+="internal error: invalid node name!\n";
						ex.printStackTrace();
					} catch (InvalidDefaultGatewayException ex) {
						out+="error: invalid default gateway.\n";
					}
				}
				else{
					try {
						String iface = null;
						if(params.size()==4) iface = params.get(3);
						else{
							IPV4Address gw = new IPV4Address(params.get(2));
							ArrayList<?> ifaces = device.getAllInterfacesNames();
							boolean found = false;
							for(int i=0; i<ifaces.size() && !found; i++){
								iface = (String) ifaces.get(i);
								gw.setCustomSubnetMask(device.getSubnetMask(iface));
								found = gw.compareToSubnet(device.getIPAddress(iface));
							}
						}
						device.addRoute(new Route_entry(params.get(0), params.get(2), params.get(1), iface, 0));
						out+="Route added.\n";
					} catch (InvalidIPAddressException e) {
						out+="error: invalid gateway ip address.\n";
					} catch (InvalidSubnetMaskException e) {
						out+="internal error: invalid subnet mask!\n";
						e.printStackTrace();
					}
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				String dest = params.get(0);
				if(dest.compareToIgnoreCase("default")==0 || dest.equals("0.0.0.0")){
					try {
						device.setDefaultGateway(null);
						out+="Default gateway removed.\n";
					} catch (InvalidNodeNameException ex) {
						out+="internal error: invalid node name!\n";
						ex.printStackTrace();
					} catch (InvalidDefaultGatewayException ex) {
						out+="internal error: invalid default gateway.\n";
					}
				}
				else{
					device.removeRoute(dest);
					out+="Route to " + params.get(0) + " removed.\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class ip_tcp_window_size_CommandClass extends CommandInterface{
		public ip_tcp_window_size_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<window-size>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				if(params.size()==1){
					try{
						int ws = Integer.parseInt(params.get(0));
						device.NodeProtocolStack.TCP().setWindowSize(ws);
						out += "TCP window size is " + device.NodeProtocolStack.TCP().getWindowSize() + " segments";
					}
					catch(NumberFormatException e){
						out += "Invalid window size\n";
					}
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}            
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				if(params.size()==0){
					try{
						device.NodeProtocolStack.TCP().setWindowSize(Tcp.DEFAULT_WINDOW_SIZE);
						out += "TCP window size is " + device.NodeProtocolStack.TCP().getWindowSize() + " segments";
					}
					catch(NumberFormatException e){
						out += "Invalid window size\n";
					}
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}            
			return out;
		}
	};
	class location_CommandClass extends CommandInterface{
		public location_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.NO_CALL);
			call_params = "<location>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()>=1){
				String desc = params.get(1);
				for(int i=1; i<params.size(); i++){
					desc += " "+params.get(i);
				}
				device.location = desc;
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(params.size()==0){
				device.location = "";
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class router_rip_CommandClass extends CommandInterface{
		public router_rip_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				// do nothing :)
			}
			else{
				out += "This instruction not supported by device\n";
			}            
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				RIP rip = ((RIP)((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.RIP_SERVER_ID));
				try {
					rip.initializeRIP(new Vector<String>(0));
				} catch (TransportLayerException e) {
					out += "Internal error: rip: TransportLayerException\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}            
			return out;
		}
	};
	class router_rip_network_CommandClass extends CommandInterface{
		public router_rip_network_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "(<network ip> <netmask>|<interface>)";
			no_call_params = "(<network ip> <netmask>|<interface>)";
		}
		public String call(Vector<String> params){
			String out = "";
			try{
				if(device instanceof ApplicationLayerDevice){
					String iface=null;
					if(params.size()==1 || params.size()==2){
						if(params.size()==1){
							iface=params.get(0);
							ArrayList<?> ifaces = device.getAllInterfacesNames();
							boolean found = false;
							for(int i=0; i<ifaces.size() && !found; i++){
								found = (((String)ifaces.get(i)).compareToIgnoreCase(iface)==0);
							}
							if(!found){
								iface=null;
								out += "error: unknown interface name\n";
							}
						}
						else{
							IPV4Address ip = new IPV4Address(params.get(0));
							ArrayList<?> ifaces = device.getAllInterfacesNames();
							boolean found = false;
							for(int i=0; i<ifaces.size() && !found; i++){
								iface = (String) ifaces.get(i);
								ip.setCustomSubnetMask(device.getSubnetMask(iface));
								found = ip.compareToSubnet(device.getIPAddress(iface));
							}
							if(!found){
								iface=null;
								out += "error: interface for given network ip and netmask not found\n";
							}
						}
						if(iface!=null){
							RIP rip = ((RIP)((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.RIP_SERVER_ID));
							try {
								rip.addInterface(iface);
							} catch (TransportLayerException e) {
								out += "Internal error: rip: TransportLayerException\n";
							}
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			} catch (InvalidIPAddressException e) {
				out+="error: invalid gateway ip address.\n";
			} catch (InvalidSubnetMaskException e) {
				out+="internal error: invalid subnet mask!\n";
				e.printStackTrace();
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			try{
				if(device instanceof ApplicationLayerDevice){
					String iface=null;
					if(params.size()==1 || params.size()==2){
						if(params.size()==1){
							iface=params.get(0);
							ArrayList<?> ifaces = device.getAllInterfacesNames();
							boolean found = false;
							for(int i=0; i<ifaces.size() && !found; i++){
								found = (((String)ifaces.get(i)).compareToIgnoreCase(iface)==0);
							}
							if(!found){
								iface=null;
								out += "error: unknown interface name\n";
							}
						}
						else{
							IPV4Address ip = new IPV4Address(params.get(2));
							ArrayList<?> ifaces = device.getAllInterfacesNames();
							boolean found = false;
							for(int i=0; i<ifaces.size() && !found; i++){
								iface = (String) ifaces.get(i);
								ip.setCustomSubnetMask(device.getSubnetMask(iface));
								found = ip.compareToSubnet(device.getIPAddress(iface));
							}
							if(!found){
								iface=null;
								out += "error: interface for given network ip and netmask not found\n";
							}
						}
						if(iface!=null){
							RIP rip = ((RIP)((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.RIP_SERVER_ID));
							try {
								rip.removeInterface(iface);
							} catch (TransportLayerException e) {
								out += "Internal error: rip: TransportLayerException\n";
							}
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			} catch (InvalidIPAddressException e) {
				out+="error: invalid gateway ip address.\n";
			} catch (InvalidSubnetMaskException e) {
				out+="internal error: invalid subnet mask!\n";
				e.printStackTrace();
			}
			return out;
		}
	};
	class show_access_lists_CommandClass extends CommandInterface{
		final int ERROR = 0;
		final int STANDART = 1;
		final int EXTENDED = 2;
		public show_access_lists_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<1-2699>";
		}
		public String call(Vector<String> params){
			String out = "";
			Hashtable<Integer, Hashtable<Integer,access_list>> acls = device.getACL().ACLs;

			if(params.size()==1){
				try{
					Integer aclnum = new Integer(params.get(0));
					if(aclnum!=null && aclnum>=1 && aclnum<=2699){
						Hashtable<Integer,access_list> acl = acls.get(aclnum);
						if(acl!=null)
							out += printACL(acl);
						else{
							out += "ACL "+aclnum+" not exists\n";
						}
					}
					else{
						out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
					}
				}
				catch(NumberFormatException ex){
					out += "error: invalid ACL name '"+params.get(0)+"', ACL name must be number from 1 to 2699\n";
				}
			}
			else if(params.size()==0){
				Enumeration<Integer> aclkeys = acls.keys();
				while(aclkeys.hasMoreElements()){
					Hashtable<Integer,access_list> acl = acls.get(aclkeys.nextElement());
					out += printACL(acl);
				}
			}
			return out;
		}
		private String printACL(Hashtable<Integer,access_list> acl){
			String out = "";

			Enumeration<Integer> rulekeys = acl.keys();
			while(rulekeys.hasMoreElements()){
				access_list rule = acl.get(rulekeys.nextElement());

				String type = "unsupported_acl_type";
				int itype = 0;
				if(rule.number>=1 && rule.number<=99){
					type = "standart";
					itype = STANDART;
				}
				else if(rule.number>=100 && rule.number<=2699){
					type = "extended";
					itype = EXTENDED;
				}

				String permit_deny = "unsupported_action";
				if(rule.action==access_list.DENY){
					permit_deny = "deny  ";
				}
				else if(rule.action==access_list.PERMIT){
					permit_deny = "permit";
				}

				String hosts1 = "unsupported_hosts_1";
				if(rule.IP1.equals("0.0.0.0") && rule.SubnetMask1.equals("0.0.0.0")){
					hosts1 = "any";
				}
				else if(rule.SubnetMask1.equals("255.255.255.255")){
					hosts1 = "host "+rule.IP1;
				}
				else{
					hosts1 = rule.IP1+" "+rule.SubnetMask1;
				}

				String hosts2 = "unsupported_hosts_2";
				if(itype==EXTENDED){
					if(rule.IP2.equals("0.0.0.0") && rule.SubnetMask2.equals("0.0.0.0")){
						hosts2 = "any";
					}
					else if(rule.SubnetMask2.equals("255.255.255.255")){
						hosts2 = "host "+rule.IP2;
					}
					else{
						hosts2 = rule.IP2+" "+rule.SubnetMask2;
					}
				}

				String protocol = "unsupported_protocol";
				if(rule.protocol==access_list.IP){
					protocol = "ip  ";
				}
				else if(rule.protocol==access_list.TCP){
					protocol = "tcp ";
				}
				else if(rule.protocol==access_list.UDP){
					protocol = "udp ";
				}
				else if(rule.protocol==access_list.ICMP){
					protocol = "icmp";
				}

				String port = "unsupported_port";
				if(rule.Port2 == 0){
					port = "";
				}
				else{
					port = "eq "+rule.Port2;
				}

				String log = "";
				if(rule.log){
					log = "log";
				}

				switch(itype){
				case STANDART: out += "ip access-list "+type+" "+rule.number+" "+rule.line+" "+permit_deny+" "+hosts1+" "+log+"\n"; break;
				case EXTENDED: out += "ip access-list "+type+" "+rule.number+" "+rule.line+" "+permit_deny+" "+protocol+" "+hosts1+" "+hosts2+" "+port+" "+log+"\n"; break;
				default: out += "ip access-list "+type+" "+rule.number+" "+rule.line+" "+permit_deny+" "+protocol+" "+hosts1+" "+hosts2+" "+rule.Port2+" "+log+"\n";
				}
			}
			return out;
		}
	};
	class show_arp_CommandClass extends CommandInterface{
		public show_arp_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			try{
				Vector<Vector<String>> ArpTable = device.getARPTable();
				if(ArpTable.size()>0){
					out += "Internet Address\tPhysical Address\t\tType\n";
					for(int i=0;i<ArpTable.size();i++)
					{
						out += ArpTable.get(i).get(0) + "\t\t" + ArpTable.get(i).get(1) + "\t\t" + ArpTable.get(i).get(2) + "\n";
					}
				}
				else{
					out += "No ARP Entries Found\n";
				}
			}
			catch(Exception e)
			{
				out += "Exception: "+e.getClass().getName()+" "+e.getMessage()+"\n";
			}
			return out;
		}
	};
	class show_clock_CommandClass extends CommandInterface{
		public show_clock_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return "Command not supported yet.\n";
		}
	};
	class show_configuration_CommandClass extends CommandInterface{
		public show_configuration_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return device.getConfig().toString(DeviceConfig.RUNNING_CONFIG);
		}
	};
	class show_connection_CommandClass extends CommandInterface{
		public show_connection_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return "Command not supported yet.\n";
		}
	};
	class show_dhcp_lease_CommandClass extends CommandInterface{
		public show_dhcp_lease_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPD dhcpd = (DHCPD)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_SERVER_ID);
				if(dhcpd!=null){
					if(params.size()==0){
						out += "DHCP server is "+(dhcpd.running?"running\n":"stopped\n");
						Enumeration<String> leases = dhcpd.leases.keys();
						while(leases.hasMoreElements()){
							String name = leases.nextElement();
							DHCPD.lease lease = dhcpd.leases.get(name);
							out += "  Lease at '"+name+"' is "+(lease.completed?"complete":"not complete")+"\n" +
							"    Network: "+((lease.IP==null || lease.IP=="")?"(not set)":lease.IP)+"\n" +
							"    Mask: "+((lease.Genmask==null || lease.Genmask=="")?"(not set)":lease.Genmask)+"\n" +
							"    Gateway: "+((lease.Gateway==null || lease.Gateway=="")?"(not set)":lease.Gateway)+"\n" +
							"    MAC: "+((lease.MAC==null || lease.MAC=="")?"(not set)":lease.MAC)+"\n";
							if(lease.leased>0){
								Calendar lt = Calendar.getInstance(); 
								lt.setTimeInMillis(lease.leased);
								out += "    Leased: "+String.format("%1$tT", lt)+"\n";
							}
							else{
								out += "    Leased: (none)\n";
							}
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_dhcp_server_CommandClass extends CommandInterface{
		public show_dhcp_server_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				DHCPC dhcpc = (DHCPC)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DHCP_CLIENT_ID);
				if(dhcpc!=null){
					if(params.size()==0){
						if(dhcpc.running)
							out += "Server "+dhcpc.getDHCPServer()+" on interface "+dhcpc.getInterface()+" is using\n";
						else
							out += "No servers found\n";
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_dot11_associations_CommandClass extends CommandInterface{
		public show_dot11_associations_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof WirelessAP){
				ArrayList<String> inames = device.getAllInterfacesNames();
				for(int i=0; i<inames.size(); i++){
					try {
						NetworkInterface ni = device.getNetworkInterface(inames.get(i));
						if(ni instanceof WiFiPort){
							out += "Associations on "+inames.get(i)+" interface\n";
							boolean client_present = false;
							String out_cl = "  BSSID            \tState\n";
							client_present = true;
							WiFiPort wfi = (WiFiPort) ni;
							Enumeration<APClient> apc = wfi.getAPClients().elements();
							while(apc.hasMoreElements()){
								APClient client = apc.nextElement();
								out_cl += "  "+client.BSSID+"\t";
								switch(client.state){
								case WiFiPort.OFFLINE: out_cl += "offline\n"; break;
								case WiFiPort.PROBE: out_cl +="probe\n"; break;
								case WiFiPort.AUTH: out_cl += "auth\n"; break;
								case WiFiPort.ASSOC: out_cl += "assoc\n"; break;
								case WiFiPort.DATA: out_cl += "data\n"; break;
								case WiFiPort.DATA_ACK: out_cl += "data ack\n"; break;
								default: out_cl += "unknown\n";
								}
							}
							if(client_present){
								out += out_cl;
							}
							else{
								out += "  no associated clients found\n";
							}
						}
					} catch (InvalidNetworkInterfaceNameException e) {
					}
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}

			return out;
		}
	};
	class show_hostname_CommandClass extends CommandInterface{
		public show_hostname_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return ("System's name: " + device.getName() + "\n");
		}
	};
	class show_interfaces_CommandClass extends CommandInterface{
		public show_interfaces_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "[<interface name>]";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==1){
				out += sh_int(params.get(0));
			}
			else if(params.size()==0){
				Object[] ins = device.getAllInterfaces();
				for(int i=0; i<ins.length; i++){
					out += sh_int((String)ins[i]);
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
		private String sh_int(String iface){
			String out = "";
			try {
				NetworkInterface ni = device.getNetworkInterface(iface);
				boolean isLineUp = false;
				if(ni instanceof WiFiPort){
					isLineUp = ((WiFiPort) ni).associated;
				}
				else{
					isLineUp = ni.getConnectedLink() != null;
				}
				boolean isUp = ni.isUP();
				out += "Interface " + iface + " is " + (isUp?"up":"down") + ", line protocol is " + (isLineUp?"up":"down") + "\n";
				out += "  Description: \"" + ni.getDescription() + "\"\n";
				out += "  Hardware address " + device.getMACAddress(iface) + "\n";
				String ip = device.getIPAddress(iface);
				String mask = device.getSubnetMask(iface);
				out += "  Internet address " + (ip==null || ip.equals("")?"Not set":ip) + "/" + (mask==null || mask.equals("")?"Not set":mask) + "\n";
				if(ni instanceof NetworkInterfacePort){
					NetworkInterfacePort eni = (NetworkInterfacePort)ni;
					out += "  VLAN ID: "+eni.vlan+"\n";
					String vmode = "unknown";
					switch(eni.mode){
					case NetworkInterfacePort.MODE_ACCESS: vmode = "access"; break;
					case NetworkInterfacePort.MODE_TRUNK: vmode = "trunk"; break;
					}
					out += "  VLAN mode: "+vmode+"\n"; 
					if(eni.description!="")
					out += "  VLAN description: "+eni.description+"\n"; 
				}
				if(ni instanceof WiFiPort){
					WiFiPort wfi = (WiFiPort) ni;
					if(device instanceof WirelessAP || device instanceof Router){
						out += "  BSSID: " + wfi.getBSSID() + "\n";
					}
					else{
						out += "  Access point BSSID: " + wfi.getAPBSSID() + "\n";
					}
					out += "  SSID: " + wfi.getSSID() + "\n";
					out += "  Channel: " + wfi.getChannel() + "\n";
					if(wfi.getMode()==WiFiPort.MODE_AP)
						out += "  Station-role is access point\n";
					else if(wfi.getMode()==WiFiPort.MODE_REPEATER)
						out += "  Station-role is repeater\n";
					else if(wfi.getMode()==WiFiPort.MODE_STATION)
						out += "  Station-role is client\n";
					out += "  Authentication type: " + (wfi.isSharedAuth()?"shared":"open") + "\n";
				}
				if(ni instanceof SerialNetworkInterface){
					out += "  Clock-rate " + ((SerialNetworkInterface)ni).getClockRate() + "\n";
				}
				if(ni instanceof ConsoleNetworkInterface){
					ConsoleNetworkInterface cni = (ConsoleNetworkInterface)ni;
					out += "  Databits: " + cni.databits + "\n";
					String fc = "";
					switch(cni.flowcontrol){
						case ConsoleNetworkInterface.FLOWCONTROL_NONE: fc = "none"; break;
						case ConsoleNetworkInterface.FLOWCONTROL_HARDWARE: fc = "hardware"; break;
						case ConsoleNetworkInterface.FLOWCONTROL_SOFTWARE: fc = "software"; break;
					}
					out += "  Flowcontrol: " + fc + "\n";
					String pa = "";
					switch(cni.parity){
						case ConsoleNetworkInterface.PARITY_NONE: pa = "none"; break;
						case ConsoleNetworkInterface.PARITY_EVEN: pa = "even"; break;
						case ConsoleNetworkInterface.PARITY_ODD: pa = "odd"; break;
						case ConsoleNetworkInterface.PARITY_MARK: pa = "mark"; break;
						case ConsoleNetworkInterface.PARITY_SPACE: pa = "space"; break;
					}
					out += "  Parity: " + pa + "\n";
					String sb = "";
					switch(cni.stopbits){
						case ConsoleNetworkInterface.STOPBIT_1: sb = "1"; break;
						case ConsoleNetworkInterface.STOPBIT_15: sb = "1.5"; break;
						case ConsoleNetworkInterface.STOPBIT_2: sb = "2"; break;
					}
					out += "  Stopbits: " + sb + "\n";
					out += "  Speed: " + cni.speed + "\n";
				}
				out += "\n";
			} catch (InvalidNetworkInterfaceNameException ex) {
				out += "invalid network interface\n";
			}
			return out;
		}
	};
	class show_ip_CommandClass extends CommandInterface{
		public show_ip_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = " Recieved IP Packets: " + Integer.valueOf(device.NodeProtocolStack.getinputIPCount()).toString() +
			"\n Sent IP Packets: " + Integer.valueOf(device.NodeProtocolStack.getoutputIPCount()).toString() +
			"\n ARP Packets: " + Integer.valueOf(device.NodeProtocolStack.getARPCount()).toString(); 
			out += "\n";
			return out;
		}
	};
	class show_ip_route_CommandClass extends CommandInterface{
		public show_ip_route_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";

			if(params.size()==0){
				out += device.getFormattedRouteTable();
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class show_ip_nat_translations_CommandClass extends CommandInterface{
		public show_ip_nat_translations_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==0){
				NATEngine nat = device.getNAT();
				if(nat.countSessions()<1){
					out += "found no sessions\n";
				}
				else{
					out += "Protocol    Inside                 Outside\n";
					for(int i=0; i<nat.countSessions(); i++){
						NAT_session rule = nat.getSession(i); 
						String inside = rule.in_ip;
						if(rule.in_port>0) inside += ":"+rule.in_port;
						String outside = rule.out_ip;
						if(rule.out_port>0) outside += ":"+rule.out_port;
						String protocol = "";
						switch(rule.protocol){
						case NAT_session.UNKNOWN: protocol="---"; break;  
						case NAT_session.ICMP: protocol="icmp"; break;  
						case NAT_session.UDP: protocol="udp"; break;  
						case NAT_session.TCP: protocol="tcp"; break;   
						}
						out += String.format("%-12s%-23s%s\n", protocol, inside, outside);
					}
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class show_kron_CommandClass extends CommandInterface{
		public show_kron_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return "Command not supported yet.\n";
		}
	};
	class show_location_CommandClass extends CommandInterface{
		public show_location_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			out += "Location: \"" + device.location + "\"\n";
			return out;
		}
	};
	class show_logging_CommandClass extends CommandInterface{
		public show_logging_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return "Command not supported yet.\n";
		}
	};
	class show_ntp_CommandClass extends CommandInterface{
		public show_ntp_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(device instanceof ApplicationLayerDevice){

			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_running_config_CommandClass extends CommandInterface{
		public show_running_config_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return device.getConfig().toString(DeviceConfig.RUNNING_CONFIG);
		}
	};
	class show_sessions_CommandClass extends CommandInterface{
		public show_sessions_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				Telnet_server telser = (Telnet_server) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.TELNET_SERVER_ID);
				if(telser != null){
					if(params.size()==0){
						Enumeration<Integer> keys = telser.connections.keys();
						if(keys.hasMoreElements()){
							out += "From Host\tUser\n";
							while(keys.hasMoreElements()){
								Integer key = keys.nextElement();
								jnSocket jnsock = device.NodeProtocolStack.SL().get_socket(key.intValue());
								out += jnsock.dst_IP+":"+jnsock.dst_port+"\t"+(telser.connections.get(key)).user+"\n";
							}
						}
						else{
							out += "No telnet sessions found\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_snmp_community_CommandClass extends CommandInterface{
		public show_snmp_community_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.SNMP_AGENT_ID);
				if(snmpa != null){
					out += "SNMP Community: ";
					if(snmpa.getPassword().equals("")){
						out += "<not set>";
					}
					else{
						out += snmpa.getPassword();
					}
					out += "\n";
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_snmp_mib_CommandClass extends CommandInterface{
		public show_snmp_mib_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.SNMP_AGENT_ID);
				if(snmpa != null){
					out += "SNMP mibs:\n";
					for(int i=0; i<snmpa.SNMPgroups.size(); i++){
						SNMP.SNMPInstance gr = snmpa.SNMPgroups.get(i);
						String gr_name = gr.name;
						for(int j=0; j<gr.instance.size(); j++){
							out += gr_name + "." + gr.instance.get(j) + "\n";
						}
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_snmp_version_CommandClass extends CommandInterface{
		public show_snmp_version_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				out += "SNMP ver. 2.0\n";
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;

		}
	};
	class show_startup_config_CommandClass extends CommandInterface{
		public show_startup_config_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return device.getConfig().toString(DeviceConfig.STARTUP_CONFIG);
		}
	};
	class show_tcp_sessions_CommandClass extends CommandInterface{
		public show_tcp_sessions_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				out += "Source\tDestination\tState\n";
				Enumeration<String> keys = device.NodeProtocolStack.TCP().getSessionKeys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					TCP_session tcps = device.NodeProtocolStack.TCP().getSession(key);
					jnSocket jnsock = device.NodeProtocolStack.SL().get_socket(tcps.getSocket());
					out += jnsock.src_IP+":"+jnsock.src_port+"\t"+(jnsock.dst_IP==null?"0.0.0.0":jnsock.dst_IP==null)+":"+jnsock.dst_port+"\t"+tcps.getStateString()+"\n";
				}
				out += "";
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_tcp_statistics_CommandClass extends CommandInterface{
		public show_tcp_statistics_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				out += "\n Recieved TCP segments: " + Integer.valueOf(device.NodeProtocolStack.getTCPinputCount()).toString() + 
				"\n Sent TCP segments: " + Integer.valueOf(device.NodeProtocolStack.getTCPoutputCount()).toString() + 
				"\n Sent TCP ACK's: " + Integer.valueOf(device.NodeProtocolStack.getTCPACKCount()).toString() + 
				"\n Sent TCP Dublicates: " + Integer.valueOf(device.NodeProtocolStack.getTCPSDCount()).toString() + 
				"\n Recieved TCP Dublicates: " + Integer.valueOf(device.NodeProtocolStack.getTCPRDCount()).toString() + 
				"\n";
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_tcp_window_size_CommandClass extends CommandInterface{
		public show_tcp_window_size_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				out += "Windows size is " + device.NodeProtocolStack.TCP().getWindowSize() + " segments";
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_udp_sessions_CommandClass extends CommandInterface{
		public show_udp_sessions_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				out += "Source\tDestination\tState\n";
				Enumeration<String> keys = device.NodeProtocolStack.UDP().getSessionKeys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					UDP_session udps = device.NodeProtocolStack.UDP().getSession(key);
					jnSocket jnsock = device.NodeProtocolStack.SL().get_socket(udps.getSocket());
					out += jnsock.src_IP+":"+jnsock.src_port+"\t"+(jnsock.dst_IP==null?"0.0.0.0":jnsock.dst_IP==null)+":"+jnsock.dst_port+"\n";
				}
				out += "";
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_udp_statistics_CommandClass extends CommandInterface{
		public show_udp_statistics_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				out += "\n Recieved UDP segments: " + Integer.valueOf(device.NodeProtocolStack.getUDPinputCount()).toString() + 
				"\n Sent UDP segments: " + Integer.valueOf(device.NodeProtocolStack.getUDPoutputCount()).toString() +
				"\n";
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class show_version_CommandClass extends CommandInterface{
		public show_version_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "javaNetSim "+Version.CORE_VERSION+", "+Version.YEARS+"\n"+
			Version.TEAM_MEMBERS[0] + "\n" + Version.TEAM_MEMBERS[1] + "\n" + Version.TEAM_MEMBERS[2] + "\n";
			return out;
		}
	};
	class show_vlan_CommandClass extends CommandInterface{
		public show_vlan_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(params.size()==0){
				Hashtable<Integer, Vector<String>> vlanport = new Hashtable<Integer, Vector<String>>();
				TreeSet<Integer> vlans = new TreeSet<Integer>();
				vlans.add(1);
				Enumeration<Integer> keys = device.getVlans();
				while(keys.hasMoreElements()){
					vlans.add(keys.nextElement());
				}
				Object[] ints = device.getAllInterfaces();
				for(int i=0; i<ints.length; i++){
					try{
						NetworkInterface ni = device.getNetworkInterface((String)ints[i]);
						if(ni instanceof NetworkInterfacePort){
							NetworkInterfacePort eni = (NetworkInterfacePort) ni;
							int vlanid = eni.vlan;
							if(vlanport.containsKey(vlanid)){
								vlanport.get(vlanid).add((String)ints[i]);
							}
							else{
								Vector<String> ports = new Vector<String>(1);
								ports.add((String)ints[i]);
								vlanport.put(vlanid, ports);
								if(!vlans.contains(vlanid)){
									vlans.add(vlanid);
								}
							}
						}
					} catch (InvalidNetworkInterfaceNameException ex) {}
				}
				Iterator<Integer> vlansi = vlans.iterator();
				while(vlansi.hasNext()){
					int vlanid = vlansi.next();
					String vlanname = device.getVlanName(vlanid);
					if(vlanname==null) vlanname = "(vlan not exists)";
					String ports = "";
					if(vlanport.containsKey(vlanid)){
						Vector<String> ps = vlanport.get(vlanid);
						for(int i=0; i<ps.size(); i++){
							ports += (i==0?"":",")+(i!=0 && i%5==0?"\n                               ":"")+ps.get(i);
						}
					}
					else{
						ports = "no ports found";
					}
					out += String.format("%5d %20s", vlanid, vlanname)+"     "+ports+"\n";
				}
			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class snmp_server_community_CommandClass extends CommandInterface{
		public snmp_server_community_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<community>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.SNMP_AGENT_ID);
				if(snmpa != null){
					if(params.size()==1){
						snmpa.setPassword(params.get(0));
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.SNMP_AGENT_ID);
				if(snmpa != null){
					if(params.size() == 0){
						snmpa.setPassword(SNMP.DEFAULT_COMMUNITY);
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class snmp_server_port_CommandClass extends CommandInterface{
		public snmp_server_port_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<port>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.SNMP_AGENT_ID);
				if(snmpa != null){
					if(params.size()==1){
						if(snmpa.running){
							try{
								snmpa.Close();
							}catch(TransportLayerException e) { }
						}
						try{
							int port = Integer.parseInt(params.get(0));
							snmpa.setPort(port);
							try{
								snmpa.Listen();
							}
							catch(TransportLayerException e) {
								out+="SNMP agent: unable to listen\n";
							}
						}catch(NumberFormatException e){
							out += "error: invalid parameters\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.SNMP_AGENT_ID);
				if(snmpa != null){
					if(params.size()==0){
						try{
							snmpa.Close();
						}catch(TransportLayerException e) { }
						try{
							snmpa.setPort(SNMP.DEFAULT_PORT);
							if(snmpa.running){
								try{
									snmpa.Listen();
								}
								catch(TransportLayerException e) {
									out+="SNMP agent: unable to listen\n";
								}
							}
						}catch(NumberFormatException e){
							out += "error: invalid parameters\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class telnet_CommandClass extends CommandInterface{
		public telnet_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<IP>";
		}
		public String call(Vector<String> params){
			String out = "Command not supported yet.\n";
			if(device instanceof ApplicationLayerDevice){

			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class telnet_server_CommandClass extends CommandInterface{
		public telnet_server_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<port>";
			no_call_params = "<cr>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				Telnet_server telnets = (Telnet_server) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.TELNET_SERVER_ID);
				if(telnets != null){
					if(params.size()==1){
						try{
							telnets.Close();
						}catch(TransportLayerException e) { }
						try{
							int port = Integer.parseInt(params.get(0));
							telnets.setPort(port);
							try{
								telnets.Listen();
							}
							catch(TransportLayerException e) {
								out+="Telnet server: unable to listen\n";
							}
						}catch(NumberFormatException e){
							out += "error: invalid parameters\n";
						}
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				Telnet_server telnets = (Telnet_server) ((core.ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.TELNET_SERVER_ID);
				if(telnets != null){
					if(params.size()==0){
						try{
							telnets.Close();
						}catch(TransportLayerException e) { }
					}
					else{
						out += "error: invalid parameters\n";
					}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class username_CommandClass extends CommandInterface{
		public username_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<username> <password>";
			no_call_params = "<username>";
		}
		public String call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				core.ApplicationLayerDevice appdevice =  (core.ApplicationLayerDevice)device;
				if(params.size()==2){
					appdevice.addUser(params.get(0), params.get(1));
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
		public String no_call(Vector<String> params){
			String out = "";
			if(device instanceof ApplicationLayerDevice){
				core.ApplicationLayerDevice appdevice =  (core.ApplicationLayerDevice)device;
				if(params.size()==2){
					appdevice.delUser(params.get(0));
				}
				else{
					out += "error: invalid parameters\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
			return out;
		}
	};
	class vlan_CommandClass extends CommandInterface{
		public vlan_CommandClass(){
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.NO_CALL);
			call_params = "<vlanid> [<name>]";
			no_call_params = "<vlanid> [<name>]";
		}
		public String call(Vector<String> params){
			return parse(params, true);
		}
		public String no_call(Vector<String> params){
			return parse(params, false);
		}
		private String parse(Vector<String> params, boolean add){
			String out = "";
			if(params.size()==1 || params.size()==2){
				try{
					int vlanid = Integer.parseInt(params.get(0));
					if(vlanid<=0) throw new NumberFormatException();
					else if(vlanid==1){
						out += "error: VLAN 1 is reserved!\n";
					}
					else{
						String name = "";
						if(params.size()==2) name = params.get(1);
						if(add){
							device.addVlan(vlanid, name);
						}
						else{
							if(name.equals("")){
								device.removeVlan(vlanid);
							}
							else if(device.getVlanName(vlanid).equals(name)){
								device.removeVlan(vlanid);
							}
						}
					}
				}
				catch(NumberFormatException e){
					out += "error: invalid vlan number\n";
				}

			}
			else{
				out += "error: invalid parameters\n";
			}
			return out;
		}
	};
	class write_memory_CommandClass extends CommandInterface{
		public write_memory_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			device.getConfig().clearStartupConfig();
			boolean res = device.getConfig().writeMemory();
			String out = "Startup-config is not empty!\n";
			if(res){
				out = "Writing is complete\n";
			}
			return out;
		}
	};
	class write_terminal_CommandClass extends CommandInterface{
		public write_terminal_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<cr>";
		}
		public String call(Vector<String> params){
			return device.getConfig().toString(DeviceConfig.RUNNING_CONFIG);
		}
	};
}

/*
 * DeviceConfig.java
 *
 * Created on 11 ќкт€брь 2007 г., 23:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import core.AccessListEngine.access_list;
import core.CommandInterface.Modes;
import core.NATEngine.NAT_rule;
import core.OSPF.OSPF;
import core.OSPF.OSPFConfigurateCommands;
import core.protocolsuite.tcp_ip.DHCPC;
import core.protocolsuite.tcp_ip.DHCPD;
import core.protocolsuite.tcp_ip.DNS;
import core.protocolsuite.tcp_ip.RIP;
import core.protocolsuite.tcp_ip.Route_entry;
import core.protocolsuite.tcp_ip.SNMP;
import core.protocolsuite.tcp_ip.Tcp;
import core.protocolsuite.tcp_ip.Telnet_server;

/**
 * 
 * @author QweR
 */
public class DeviceConfig {

	public final static int RUNNING_CONFIG = 1;
	public final static int STARTUP_CONFIG = 2;
	private final static String EXEC = "#";

	public int working_config = DeviceConfig.RUNNING_CONFIG;

	private NetworkLayerDevice device;
	private CommandProcessor cmdproc;
	private LinkedList<String> running_config = new LinkedList<String>();
	private LinkedList<String> startup_config = new LinkedList<String>();
	Modes mode;

	/** Creates a new instance of DeviceConfig */
	public DeviceConfig(NetworkLayerDevice dev) {
		initRunningConfig();
		// initStartupConfig();
		cmdproc = new CommandProcessor(dev);
		device = dev;
		if (device instanceof ApplicationLayerDevice) {
			mode = new Modes(CommandInterface.ALL_MODES, CommandInterface.APPLICATION_LAYER, CommandInterface.CALLS);
		} else {
			mode = new Modes(CommandInterface.ALL_MODES, CommandInterface.NETWORK_LAYER, CommandInterface.CALLS);
		}
	}

	/**
	 * Load startup_config with replacing running_config
	 * 
	 */
	public void load() {
		Iterator<String> it = prepareConfig(startup_config, true).iterator();
		initRunningConfig();

		while (it.hasNext()) {
			String nextcmd = it.next();
			executeCommand(nextcmd);
			// running_config.add(nextcmd);
		}
		// clearStartupConfig();
		// writeMemory();
	}

	public LinkedList<String> getConfig(int config_name) {
		LinkedList<String> config = null;
		switch (config_name) {
		case RUNNING_CONFIG:
			config = prepareConfig(running_config, true);
			break;
		case STARTUP_CONFIG:
			config = prepareConfig(startup_config, true);
			break;
		default:
		}
		return config;
	}

	public String toString(int config_name) {
		String out = "";
		Iterator<String> cnf = device.getConfig().getConfig(config_name).iterator();
		while (cnf.hasNext()) {
			out += cnf.next() + "\n";
		}
		return out;
	}

	protected LinkedList<String> getRealConfig(int config_name) {
		LinkedList<String> config = null;
		switch (config_name) {
		case RUNNING_CONFIG:
			config = running_config;
			break;
		case STARTUP_CONFIG:
			config = startup_config;
			break;
		default:
		}
		return config;
	}

	public void clearStartupConfig() {
		while (!startup_config.isEmpty())
			startup_config.removeLast();
	}

	private void initRunningConfig() {
		while (!running_config.isEmpty())
			running_config.removeLast();
		running_config.add(DeviceConfig.EXEC + "COMMON");
		running_config.add(DeviceConfig.EXEC + "ACLS");
		running_config.add(DeviceConfig.EXEC + "INTERFACES");
		running_config.add(DeviceConfig.EXEC + "ROUTE");
		running_config.add(DeviceConfig.EXEC + "IP");
		running_config.add(DeviceConfig.EXEC + "ARPS");
		running_config.add(DeviceConfig.EXEC + "EXTENDED");
	}

	public boolean writeMemory() {
		boolean result = false;
		if (startup_config.isEmpty()) {
			Iterator<String> it = prepareConfig(running_config, true).iterator();
			while (it.hasNext()) {
				startup_config.add(it.next());
			}
			result = true;
		}
		return result;
	}

	/**
	 * Add full 'command'
	 * 
	 * @return false if 'command' already exists
	 */
	public boolean add(String command) {
		return add(command, working_config);
	}

	public boolean add(String command, int config_name) {
		return addBefore(command, config_name, null);
	}

	public boolean addBefore(String command, int config_name, String beforeCommand) {
		boolean result = false;
		LinkedList<String> config = getRealConfig(config_name);
		boolean found = false;
		int index = -1;
		Pattern p = null;
		try {
			if (beforeCommand != null) {
				p = Pattern.compile(beforeCommand);
			}
		} catch (PatternSyntaxException e) {
			System.out.println("addBefore: pattern " + beforeCommand + " have incorrent syntax\n");
			e.printStackTrace();
		}
		for (int i = 0; i < config.size() && !found; i++) {
			if (beforeCommand != null && p.matcher(config.get(i)).find()) {
				index = i;
			}
			if (command.compareToIgnoreCase(config.get(i)) == 0) {
				found = true;
			}
		}
		if (!found) {
			if (beforeCommand == null) {
				config.add(command);
			} else {
				config.add(index, command);
			}
			result = true;
		}
		return result;
	}

	/**
	 * Remove all elements match with 'command'
	 * 
	 */
	public boolean remove(String command) {
		return remove(command, working_config);
	}

	public boolean remove(String command, int config_name) {
		boolean result = false;

		try {
			Pattern p = Pattern.compile(command);
			LinkedList<String> config = getRealConfig(config_name);
			Iterator<String> it = config.iterator();
			while (it.hasNext()) {
				if (p.matcher(it.next()).find()) {
					it.remove();
					result = true;
				}
			}
		} catch (PatternSyntaxException e) {
			System.out.println("remove: pattern " + command + " have incorrent syntax\n");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Is exists 'command' in 'config_name' ?
	 * 
	 */
	public boolean isExists(String command) {
		return isExists(command, working_config);
	}

	public boolean isExists(String command, int config_name) {
		boolean result = false;

		try {
			Pattern p = Pattern.compile(command);
			LinkedList<String> config = getRealConfig(config_name);
			Iterator<String> it = config.iterator();
			while (it.hasNext() && !result) {
				if (p.matcher(it.next()).find()) {
					result = true;
				}
			}
		} catch (PatternSyntaxException e) {
			System.out.println("isExists: pattern " + command + " have incorrent syntax\n");
			e.printStackTrace();
		}
		return result;
	}

	public String executeCommand(String command) {
		return cmdproc.call(command, mode, "");
	}

	private LinkedList<String> prepareConfig(LinkedList<String> in, boolean fullConfig) {
		LinkedList<String> out = new LinkedList<String>();
		Iterator<String> it = in.iterator();
		while (it.hasNext()) {
			String nc = it.next();
			if (nc.startsWith(DeviceConfig.EXEC)) {
				nc = nc.substring(DeviceConfig.EXEC.length());
				if (nc.equalsIgnoreCase("ACLS")) {
					fillConfigACLS(out);
				} else if (nc.equalsIgnoreCase("INTERFACES")) {
					if (fullConfig)
						fillInterfaces(out);
					else
						out.add(DeviceConfig.EXEC + "INTERFACES");
				} else if (nc.equalsIgnoreCase("ARPS")) {
					fillARP(out);
				} else if (nc.equalsIgnoreCase("COMMON")) {
					if (fullConfig)
						fillCommon(out);
					else
						out.add(DeviceConfig.EXEC + "COMMON");
				} else if (nc.equalsIgnoreCase("IP")) {
					fillIP(out);
				} else if (nc.equalsIgnoreCase("ROUTE")) {
					if (fullConfig)
						fillRoute(out);
					else
						out.add(DeviceConfig.EXEC + "ROUTE");
				} else if (nc.equalsIgnoreCase("EXTENDED")) {
					fillExtended(out);
				} else {
					out.add(DeviceConfig.EXEC + "ERROR: unsupported keyword #" + nc);
				}
			} else {
				out.add(nc);
			}
		}
		return out;
	}

	protected void fillConfigACLS(LinkedList<String> conf) {
		// final int ERROR = 0;
		final int STANDART = 1;
		final int EXTENDED = 2;
		Hashtable<Integer, Hashtable<Integer, access_list>> acls = device.getACL().ACLs;

		Enumeration<Integer> aclkeys = acls.keys();
		while (aclkeys.hasMoreElements()) {
			Hashtable<Integer, access_list> acl = acls.get(aclkeys.nextElement());

			Enumeration<Integer> rulekeys = acl.keys();
			while (rulekeys.hasMoreElements()) {
				access_list rule = acl.get(rulekeys.nextElement());

				String type = "unsupported_acl_type";
				int itype = 0;
				if (rule.number >= 1 && rule.number <= 99) {
					type = "standart";
					itype = STANDART;
				} else if (rule.number >= 100 && rule.number <= 2699) {
					type = "extended";
					itype = EXTENDED;
				}

				String permit_deny = "unsupported_action";
				if (rule.action == access_list.DENY) {
					permit_deny = "deny  ";
				} else if (rule.action == access_list.PERMIT) {
					permit_deny = "permit";
				}

				String hosts1 = "unsupported_hosts_1";
				if (rule.IP1.equals("0.0.0.0") && rule.SubnetMask1.equals("0.0.0.0")) {
					hosts1 = "any";
				} else if (rule.SubnetMask1.equals("255.255.255.255")) {
					hosts1 = "host " + rule.IP1;
				} else {
					hosts1 = rule.IP1 + " " + rule.SubnetMask1;
				}

				String hosts2 = "unsupported_hosts_2";
				if (itype == EXTENDED) {
					if (rule.IP2.equals("0.0.0.0") && rule.SubnetMask2.equals("0.0.0.0")) {
						hosts2 = "any";
					} else if (rule.SubnetMask2.equals("255.255.255.255")) {
						hosts2 = "host " + rule.IP2;
					} else {
						hosts2 = rule.IP2 + " " + rule.SubnetMask2;
					}
				}

				String protocol = "unsupported_protocol";
				if (rule.protocol == access_list.IP) {
					protocol = "ip  ";
				} else if (rule.protocol == access_list.TCP) {
					protocol = "tcp ";
				} else if (rule.protocol == access_list.UDP) {
					protocol = "udp ";
				} else if (rule.protocol == access_list.ICMP) {
					protocol = "icmp";
				}

				String port = "unsupported_port";
				if (rule.Port2 == 0) {
					port = "";
				} else {
					port = "eq " + rule.Port2;
				}

				String log = "";
				if (rule.log) {
					log = "log";
				}

				switch (itype) {
				case STANDART:
					conf.add("ip access-list " + type + " " + rule.number + " " + permit_deny + " " + hosts1 + " "
							+ log);
					break;
				case EXTENDED:
					conf.add("ip access-list " + type + " " + rule.number + " " + permit_deny + " " + protocol + " "
							+ hosts1 + " " + hosts2 + " " + port + " " + log);
					break;
				default:
					conf.add("ip access-list " + type + " " + rule.number + " " + permit_deny + " " + protocol + " "
							+ hosts1 + " " + hosts2 + " " + rule.Port2 + " " + log);
				}
			}
		}
	}

	protected void fillInterfaces(LinkedList<String> conf) {
		Object[] ins = device.getAllInterfaces();

		for (int i = 0; i < ins.length; i++) {
			try {
				NetworkInterface ni = device.getNetworkInterface((String) ins[i]);
				EthernetNetworkInterface eni = null;
				SerialNetworkInterface sni = null;
				NetworkInterfacePort pni = null;
				ConsoleNetworkInterface cni = null;
				WiFiPort wfi = null;
				try {
					eni = (EthernetNetworkInterface) ni;
				} catch (ClassCastException e) {
				}
				;
				try {
					sni = (SerialNetworkInterface) ni;
				} catch (ClassCastException e) {
				}
				;
				try {
					pni = (NetworkInterfacePort) ni;
				} catch (ClassCastException e) {
				}
				;
				try {
					cni = (ConsoleNetworkInterface) ni;
				} catch (ClassCastException e) {
				}
				;
				try {
					wfi = (WiFiPort) ni;
				} catch (ClassCastException e) {
				}
				;

				String intName = (String) ins[i];
				if (ni.getDescription() != null && !ni.getDescription().equals(""))
					conf.add("interface " + intName + " description " + ni.getDescription());
				if (eni != null && !eni.getMACAddress().equalsIgnoreCase(eni.defaultMACAddress))
					conf.add("interface " + intName + " mac-address " + eni.getMACAddress());
				if ((eni != null || sni != null || wfi != null) && device.getIPAddress((String) ins[i]) != null
						&& !device.getIPAddress((String) ins[i]).equals("") && ni.isActive())
					conf.add("interface " + intName + " ip address " + device.getIPAddress((String) ins[i]) + " "
							+ device.getSubnetMask((String) ins[i]));
				if (sni != null && sni.getClockRate() != SerialNetworkInterface.DEFAULT_CLOCKRATE)
					conf.add("interface " + intName + " clock-rate " + sni.getClockRate());
				if (wfi != null) {
					if (wfi.getBSSID() != null && !wfi.getBSSID().equals(""))
						conf.add("interface " + intName + " bssid " + wfi.getBSSID());
					if (wfi.getSSID() != null && !wfi.getSSID().equals(""))
						conf.add("interface " + intName + " ssid " + wfi.getSSID());
					if (wfi.getChannel() == 0)
						conf.add("no interface " + intName + " channel");
					else
						conf.add("interface " + intName + " channel " + wfi.getChannel());
					if (wfi.getMode() == WiFiPort.MODE_AP)
						conf.add("interface " + intName + " station-role root access-point");
					else if (wfi.getMode() == WiFiPort.MODE_REPEATER)
						conf.add("interface " + intName + " station-role repeater");
					else if (wfi.getMode() == WiFiPort.MODE_STATION)
						conf.add("interface " + intName + " station-role client");
					conf.add("interface " + intName + " authentication " + (wfi.isSharedAuth() ? "shared" : "open"));
					for (int j = 1; j <= 4; j++) {
						if (wfi.getWEPKey(j) != null && !wfi.getWEPKey(j).equals(""))
							conf.add("interface " + intName + " encryption key " + j + " size "
									+ wfi.getWEPKey(j).length() * 4 + "bit " + wfi.getWEPKey(j));
					}
				}
				if (pni != null) {
					if (pni.vlan > 1) {
						conf.add("interface " + intName + " switchport access vlan " + pni.vlan);
					}
					switch (pni.mode) {
					case NetworkInterfacePort.MODE_ACCESS:
						break;
					case NetworkInterfacePort.MODE_TRUNK:
						conf.add("interface " + intName + " switchport mode trunk");
						break;
					}
					if (pni.description != "") {
						conf.add("interface " + intName + " switchport description " + pni.description);
					}
				}

				if (cni != null) {
					if (cni.databits != ConsoleNetworkInterface.DATABITS_DEFAULT)
						conf.add("interface " + intName + " databits " + cni.databits);
					if (cni.flowcontrol != ConsoleNetworkInterface.FLOWCONTROL_DEFAULT) {
						String fc = "";
						switch (cni.flowcontrol) {
						case ConsoleNetworkInterface.FLOWCONTROL_NONE:
							fc = "none";
							break;
						case ConsoleNetworkInterface.FLOWCONTROL_HARDWARE:
							fc = "hardware";
							break;
						case ConsoleNetworkInterface.FLOWCONTROL_SOFTWARE:
							fc = "software";
							break;
						}
						conf.add("interface " + intName + " flowcontrol " + fc);
					}
					if (cni.parity != ConsoleNetworkInterface.PARITY_DEFAULT) {
						String pa = "";
						switch (cni.parity) {
						case ConsoleNetworkInterface.PARITY_NONE:
							pa = "none";
							break;
						case ConsoleNetworkInterface.PARITY_EVEN:
							pa = "even";
							break;
						case ConsoleNetworkInterface.PARITY_ODD:
							pa = "odd";
							break;
						case ConsoleNetworkInterface.PARITY_MARK:
							pa = "mark";
							break;
						case ConsoleNetworkInterface.PARITY_SPACE:
							pa = "space";
							break;
						}
						conf.add("interface " + intName + " parity " + pa);
					}
					if (cni.stopbits != ConsoleNetworkInterface.STOPBIT_DEFAULT) {
						String sb = "";
						switch (cni.stopbits) {
						case ConsoleNetworkInterface.STOPBIT_1:
							sb = "1";
							break;
						case ConsoleNetworkInterface.STOPBIT_15:
							sb = "1.5";
							break;
						case ConsoleNetworkInterface.STOPBIT_2:
							sb = "2";
							break;
						}
						conf.add("interface " + intName + " stopbits " + sb);
					}
					if (cni.speed != ConsoleNetworkInterface.SPEED_DEFAULT)
						conf.add("interface " + intName + " speed " + cni.speed);
				}
				if (ni.getACLin() != 0)
					conf.add("interface " + intName + " ip access-group " + ni.getACLin() + " in");
				if (ni.getACLout() != 0)
					conf.add("interface " + intName + " ip access-group " + ni.getACLout() + " out");
				switch (ni.getNAT()) {
				case NetworkInterface.NO_NAT:
					break;
				case NetworkInterface.INSIDE_NAT:
					conf.add("interface " + intName + " ip nat inside");
					break;
				case NetworkInterface.OUTSIDE_NAT:
					conf.add("interface " + intName + " ip nat outside");
					break;
				}
				if (!ni.unreachables)
					conf.add("no interface " + intName + " ip unreachables");
				if (!ni.redirects)
					conf.add("no interface " + intName + " ip redirects");
				if (!ni.maskReplay)
					conf.add("no interface " + intName + " ip mask-replay");
				if (!ni.informationReplay)
					conf.add("no interface " + intName + " ip information-replay");

				if (ni.isUP()) {
					if (ni.isActive()) {
						conf.add("no interface " + intName + " shutdown");
					}
				} else {
					conf.add("interface " + intName + " shutdown");
				}

				if (device instanceof ApplicationLayerDevice) {
					DHCPC dhcpc = (DHCPC) ((ApplicationLayerDevice) device).getApp(PC.DHCP_CLIENT_ID);
					if (dhcpc != null && dhcpc.running && dhcpc.getInterface().equalsIgnoreCase(intName)) {
						conf.add("interface " + intName + " ip dhcp client");
					}
				}
			} catch (InvalidNetworkInterfaceNameException ex) {
				System.out.println("Internal error: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	protected void fillARP(LinkedList<String> conf) {
		Vector<Vector<String>> ArpTable = device.getARPTable();
		if (ArpTable.size() > 0) {
			for (int i = 0; i < ArpTable.size(); i++) {
				if (ArpTable.get(i).get(2).compareTo("Static") == 0)
					conf.add("arp " + ArpTable.get(i).get(0) + " " + ArpTable.get(i).get(1));
			}
		}
	}

	protected void fillCommon(LinkedList<String> conf) {
		conf.add("hostname " + device.getName());
		if (device.location != null && !device.location.equals(""))
			conf.add("location " + device.location);
	}

	protected void fillIP(LinkedList<String> conf) {
		NATEngine nat = device.getNAT();
		for (int dyn = 0; dyn < 2; dyn++) {
			for (int i = 0; i < nat.countRules(dyn == 1); i++) {
				String params;
				NAT_rule rule = nat.getRule(i, dyn == 1); // dyn==0 --
				// static_rules,
				// dyn==1 --
				// dynamic_rules
				if (rule.dynamic) {
					params = " list " + rule.acl;
					params += (rule.pool ? " pool " : " interface ") + rule.out_int + " overload";
				} else {
					params = " static";
					switch (rule.protocol) {
					case NAT_rule.IP:
						params += " ip";
						break;
					case NAT_rule.ICMP:
						params += " icmp";
						break;
					case NAT_rule.UDP:
						params += " udp";
						break;
					case NAT_rule.TCP:
						params += " tcp";
						break;
					}
					params += " " + rule.in_ip;
					if (rule.in_port > 0)
						params += " " + rule.in_port;
					params += " " + rule.out_ip;
					if (rule.out_port > 0)
						params += " " + rule.out_port;
				}
				conf.add("ip nat inside source" + params);
			}
		}
		Enumeration<String> natpools = nat.getPools();
		while (natpools.hasMoreElements()) {
			String name = natpools.nextElement();
			Pair pool = nat.getPool(name);
			conf.add("ip nat pool " + name + " " + (String) pool.getFirst() + " " + (String) pool.getSecond());
		}
		if (device instanceof ApplicationLayerDevice) {
			ApplicationLayerDevice adev = (ApplicationLayerDevice) device;
			DHCPD dhcpd = (DHCPD) adev.getApp(PC.DHCP_SERVER_ID);
			if (dhcpd != null) {
				Enumeration<String> dhcppools = dhcpd.pools.keys();
				while (dhcppools.hasMoreElements()) {
					String poolname = dhcppools.nextElement();
					DHCPD.pool pool = dhcpd.pools.get(poolname);
					if (pool.Gateway != "")
						conf.add("ip dhcp pool " + poolname + " default-router " + pool.Gateway);
					if (pool.Genmask != "")
						conf.add("ip dhcp pool " + poolname + " network " + pool.IP + " " + pool.Genmask);
					if (pool.MAC != "")
						conf.add("ip dhcp pool " + poolname + " hardware-address " + pool.MAC);
				}
				Vector<Pair> exclude = dhcpd.getExcludeAddresses();
				for (int i = 0; i < exclude.size(); i++) {
					Pair lha = exclude.get(i);
					conf.add("ip dhcp pool excluded-address " + lha.getFirst() + " " + lha.getSecond());
				}
			}
			if (device.NodeProtocolStack.TCP().getWindowSize() != Tcp.DEFAULT_WINDOW_SIZE)
				conf.add("ip tcp window-size " + device.NodeProtocolStack.TCP().getWindowSize());
			if (adev.getNameServer() != "") {
				conf.add("ip name-server " + adev.getNameServer());
			}
		}
	}

	protected void fillExtended(LinkedList<String> conf) {
		conf.add("clock set " + device.getClock());

		if (device instanceof ApplicationLayerDevice) {
			core.ApplicationLayerDevice appdevice = (core.ApplicationLayerDevice) device;
			// SNMP
			SNMP snmpa = (SNMP) appdevice.getApp(core.ApplicationLayerDevice.SNMP_AGENT_ID);
			if (snmpa != null && snmpa.running) {
				conf.add("snmp-server port " + snmpa.getPort());
				if (!snmpa.getPassword().equals(SNMP.DEFAULT_COMMUNITY)) {
					conf.add("snmp-server community " + snmpa.getPassword());
				}
			}

			// TELNET
			Telnet_server telnets = (Telnet_server) appdevice.getApp(core.ApplicationLayerDevice.TELNET_SERVER_ID);
			if (telnets != null && telnets.running) {
				conf.add("telnet-server " + telnets.getPort());
			}

			// username
			Enumeration<String> users = appdevice.getUserList();
			if (users != null) {
				String username;
				while (users.hasMoreElements()) {
					username = users.nextElement();
					conf.add("username " + username + " " + appdevice.getUserPassword(username));
				}
			}

			// router rip
			RIP rip = (RIP) appdevice.getApp(core.ApplicationLayerDevice.RIP_SERVER_ID);
			if (rip != null) {
				Enumeration<String> rip_ifaces = rip.getInterfaces().elements();
				if (rip_ifaces.hasMoreElements()) {
					conf.add("router rip");
					while (rip_ifaces.hasMoreElements()) {
						String riface = rip_ifaces.nextElement();
						conf.add("router rip network " + riface);
					}
				} else {
					// conf.add("no router rip");
				}
			}

			// router ospf
			OSPF ospf = appdevice.NodeProtocolStack.OSPF();
			if (ospf != null) {
				conf.add(OSPFConfigurateCommands.OSPF_AREA_COMMAND + ospf.getArea());
				if (ospf.isDeviceUseRedistribute()) {
					conf.add(OSPFConfigurateCommands.OSPF_REDISTRIBUTE_COMMAND);
				}
				for (String iface : ospf.interfaces()) {
					conf.add(OSPFConfigurateCommands.OSPF_NETWORK_COMMAND + iface);
				}
			}

			// DNS
			DNS dns = (DNS) appdevice.getApp(core.ApplicationLayerDevice.DNS_SERVER_ID);
			if (dns != null) {
				if (dns.listening) {
					conf.add("ip dns server " + dns.getPort());
				}
				if (DNS.isValidName(dns.DomainName) && DNS.isValidName(dns.PrimaryNameServer)
						&& DNS.isValidMail(dns.MailboxResponsiblePerson)) {
					conf.add("ip dns primary " + dns.DomainName + " soa " + dns.PrimaryNameServer + " "
							+ dns.MailboxResponsiblePerson + " " + dns.RefreshTime + " " + dns.RefreshRetryTime + " "
							+ dns.AuthorityExpireTime + " " + dns.MinimumTTLZoneInfo);
				}
				Hashtable<String, Vector<String>> rec = dns.records_a;
				Enumeration<String> domains = rec.keys();
				while (domains.hasMoreElements()) {
					String dname = domains.nextElement();
					Vector<String> vals = rec.get(dname);
					for (int i = 0; i < vals.size(); i++)
						conf.add("ip host " + dname + " " + vals.get(i));
				}
				rec = dns.records_ptr;
				domains = rec.keys();
				while (domains.hasMoreElements()) {
					String dname = domains.nextElement();
					Vector<String> vals = rec.get(dname);
					for (int i = 0; i < vals.size(); i++)
						conf.add("ip host " + DNS.fromInAddrArpa(dname) + " " + vals.get(i));
				}
				rec = dns.records_cname;
				domains = rec.keys();
				while (domains.hasMoreElements()) {
					String dname = domains.nextElement();
					Vector<String> vals = rec.get(dname);
					for (int i = 0; i < vals.size(); i++)
						conf.add("ip host " + dname + " cname " + vals.get(i));
				}
				Hashtable<String, Vector<Pair>> recm = dns.records_mx;
				domains = recm.keys();
				while (domains.hasMoreElements()) {
					String dname = domains.nextElement();
					Vector<Pair> vals = recm.get(dname);
					for (int i = 0; i < vals.size(); i++)
						conf.add("ip host " + dname + " mx " + vals.get(i).getSecond() + " " + vals.get(i).getFirst());
				}
				Hashtable<String, String> rech = dns.records_hinfo;
				domains = rech.keys();
				while (domains.hasMoreElements()) {
					String dname = domains.nextElement();
					conf.add("ip host hinfo " + dname + " " + rech.get(dname));
				}
			}
		}
	}

	protected void fillRoute(LinkedList<String> conf) {
		String routes[] = device.getRouteTableEntries();
		for (int i = 0; i < routes.length - 1; i++) {
			Route_entry r = device.getRouteTableEntry(routes[i]);
			conf.add("ip route " + routes[i] + " " + r.genMask + " " + r.gateway + " " + r.iFace);
		}
	}
}

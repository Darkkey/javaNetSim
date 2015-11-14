package core.OSPF;

import java.util.Vector;

import core.CommandInterface;
import core.CommandsTree;
import core.NetworkLayerDevice;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.InvalidIPAddressException;
import core.protocolsuite.tcp_ip.InvalidSubnetMaskException;

/**
 * @author Anatoly Chekh
 * 
 *         Class contains different OSPF commands
 */
public class OSPFConfigurateCommands {

	public static final String OSPF_AREA_COMMAND = "router ospf area";
	public static final String OSPF_REDISTRIBUTE_COMMAND = "router ospf redistribute";
	public static final String OSPF_NETWORK_COMMAND = "router ospf network";
	
	private final CommandsTree myCommands;
	private final NetworkLayerDevice myDevice;

	public OSPFConfigurateCommands(CommandsTree commands, NetworkLayerDevice device) {
		myCommands = commands;
		myDevice = device;
	}

	public void init() {
		myCommands.addDescription("router ospf","Open Shortest Path First (OSPF)");
		myCommands.add(OSPF_AREA_COMMAND, new RouterOSPFAreaCommand(), "Area of router OSPF");
		myCommands.add(OSPF_REDISTRIBUTE_COMMAND, new RouterOSPFRedistributeCommand(), "Router use redistribute");
		myCommands.add(OSPF_NETWORK_COMMAND, new RouterOSPFNetworkCommand(), "Add network/interface for OSPF");
	}

	private class RouterOSPFAreaCommand extends CommandInterface {

		public RouterOSPFAreaCommand() {
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<1-" + OSPFConstants.MAX_AREA_NUMBER + ">";
		}

		@Override
		public String call(Vector<String> params) {
			if (params.size() == 1) {
				try {
					int area = Integer.parseInt(params.get(0));
					if (area > OSPFConstants.MAX_AREA_NUMBER) {
						return "error: invalid ospf area number '" + params.get(0)
								+ "', ospf area number must be number from 1 to 65535\n";
					}
					myDevice.NodeProtocolStack.OSPF().setArea(area);
				} catch (NumberFormatException e) {
					return "error: invalid ospf area number '" + params.get(0)
							+ "', ospf area number must be number from 1 to 65535\n";
				}
			} else {
				return "error: invalid parameters\n";
			}
			return "";
		}
	}

	private class RouterOSPFRedistributeCommand extends CommandInterface {

		public RouterOSPFRedistributeCommand() {
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "<cr>";
			no_call_params = "<cr>";
		}

		@Override
		public String call(Vector<String> params) {
			if (params.isEmpty()) {
				myDevice.NodeProtocolStack.OSPF().setUseRedistribute(true);
				return "";	
			} else {
				return "error: invalid parameters\n";
			}
		}
		
		@Override
		public String no_call(Vector<String> params) {
			if (params.isEmpty()) {
				myDevice.NodeProtocolStack.OSPF().setUseRedistribute(false);
				return "";	
			} else {
				return "error: invalid parameters\n";
			}
		}
	}
	
	private class RouterOSPFNetworkCommand extends CommandInterface {

		public RouterOSPFNetworkCommand() {
			modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.NO_CALL);
			call_params = "(<network ip> <netmask>|<interface>)";
			no_call_params = "(<network ip> <netmask>|<interface>)";
		}

		@Override
		public String call(Vector<String> params) {
			return executeCommand(params, true);
		}

		public String no_call(Vector<String> params) {
			return executeCommand(params, false);
		}

		private String executeCommand(Vector<String> params, boolean callExecute) {
			String iface = null;
			if (params.size() == 1) {
				iface = params.get(0);
				if (!myDevice.getAllInterfacesNames().contains(iface)) {
					return "error: unknown interface name\n";
				}
			} else if (params.size() == 2) {
				try {
					IPV4Address ip = new IPV4Address(params.get(0));
					for (String candidate : myDevice.getAllInterfacesNames()) {
						ip.setCustomSubnetMask(myDevice.getSubnetMask(candidate));
						if (ip.compareToSubnet(myDevice.getIPAddress(candidate))) {
							iface = candidate;
							break;
						}
					}
				} catch (InvalidIPAddressException e) {
					e.printStackTrace();
					return "error: invalid gateway ip address.\n";
				} catch (InvalidSubnetMaskException e) {
					e.printStackTrace();
					return "internal error: invalid subnet mask!\n";
				}
				if (iface == null) {
					return "error: interface for given network ip and netmask not found\n";
				}
			} else {
				return "error: invalid parameters\n";
			}
			if (callExecute) {
				myDevice.NodeProtocolStack.OSPF().addInterface(iface);
			} else {
				myDevice.NodeProtocolStack.OSPF().removeInterface(iface);
			}
			return "";
		}

	}

}
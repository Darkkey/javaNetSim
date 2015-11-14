package core.OSPF;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import core.InvalidNetworkInterfaceNameException;
import core.NetworkInterface;
import core.NetworkLayerDevice;
import core.Simulation;
import core.TimerApp;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.InvalidIPAddressException;
import core.protocolsuite.tcp_ip.ProtocolStack;
import core.protocolsuite.tcp_ip.Route_entry;

/**
 * OSPF Protocol
 * 
 * @author Anatoly Chekh
 */
public class OSPF extends TimerApp {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4031443419981254419L;

	public static final int TIMEOUT = OSPFConstants.HELLO_INTERVAL * 1000;

	private final ProtocolStack myProtocolStack;

	private int myTimerCounter = 0;

	private boolean myDeviceIsBlock;

	private String myArea = "0.0.0.0";

	private boolean myDeviceUseRedistribute;

	/**
	 * neighbors list
	 */
	private List<Neighbor> myNeighbors = new ArrayList<Neighbor>();

	/**
	 * LSA data base Map from advertising router ID to LSA list
	 */
	private HashMap<String, List<LSA>> myLSAsDataBase = new HashMap<String, List<LSA>>();

	/**
	 * ospf-interfaces
	 */
	private List<String> myInterfaces = new ArrayList<String>();

	public OSPF(ProtocolStack parentStack, long UID) {
		super(UID);
		myProtocolStack = parentStack;
	}

	private String getRouterID() {
		String binMinIP = "";
		try {
			binMinIP = IPV4Address.toBinaryString("255.255.255.255");
		} catch (InvalidIPAddressException e) {
		}
		for (String iface : myProtocolStack.getParentNode().getAllInterfacesNames()) {
			try {
				String candidate = ((NetworkLayerDevice) myProtocolStack.getParentNode()).getIPAddress(iface);
				if (candidate != null && !"Not Applicable".equals(candidate)) {
					String binCandidate = IPV4Address.toBinaryString(candidate);
					if (binMinIP.compareTo(binCandidate) > 0) {
						binMinIP = binCandidate;
					}
				}
			} catch (InvalidIPAddressException e) {
			}
		}
		return IPV4Address.toDecimalString(binMinIP);
	}

	public void addInterface(String iface) {
		if (myProtocolStack.getIPAddress(iface) == null) {
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, "Error: cannot add interface " + iface
							+ ". IP address hadn't been set.");
			return;
		}
		if (myInterfaces.contains(iface)) {
			return;
		}
		myInterfaces.add(iface);
	}

	public boolean removeInterface(String iface) {
		return myInterfaces.remove(iface);
	}

	private String getHeaderMessage(int packetType) {
		return MessageFormat.format(OSPFConstants.OSPF_PACKET_HEADER, OSPFConstants.OSPF_VERSION, packetType, "0",
				getRouterID(), myArea, "0", "0", " ");
	}

	private String getLSAHeader(int lsaAge, int type, String linkStateID, String advertisingRouter) {
		return MessageFormat.format(OSPFConstants.LSA_HEADER, lsaAge, "0", type, linkStateID, advertisingRouter, "0",
				"0", "0");
	}
	
	private String convertRouterLSAs(String advertisingRouter, List<LSA> lsas, boolean isRemoveLSA) {
		StringBuilder lsaInfo = new StringBuilder();
		int count = 0;
		for (LSA lsa : lsas) {
			if (lsa.myType == OSPFConstants.ROUTER_LSA_TYPE) {
				count++;
				if (count > 0) {
					lsaInfo.append(OSPFConstants.PACKET_DELIMETER);
				}
				lsaInfo.append(MessageFormat.format(OSPFConstants.ROUTER_LSA_LINK_MESSAGE, lsa.myLinkID,
						lsa.myLinkData, lsa.myMetric));
			}
		}
		return count == 0 ? null : MessageFormat.format(OSPFConstants.ROUTER_LSA_MESSAGE, getLSAHeader(
				isRemoveLSA ? OSPFConstants.LSA_MAX_AGE : 0, OSPFConstants.ROUTER_LSA_TYPE, advertisingRouter,
				advertisingRouter), count, lsaInfo.toString());
	}

	private List<String> convertExternalLSAs(String advertisingRouter, List<LSA> lsas, boolean isRemoveLSA) {
		List<String> lsaInfo = new ArrayList<String>();
		for (LSA lsa : lsas) {
			if (lsa.myType == OSPFConstants.EXTERNAL_LSA_TYPE) {
				lsaInfo.add(MessageFormat.format(OSPFConstants.EXTERNAL_LSA_MESSAGE, getLSAHeader(
						isRemoveLSA ? OSPFConstants.LSA_MAX_AGE : 0, OSPFConstants.EXTERNAL_LSA_TYPE,
						lsa.myLinkStateID, advertisingRouter), lsa.myNetworkMask, lsa.myMetric));
			}
		}
		return lsaInfo;
	}

	private String convertNeighbors() {
		StringBuilder result = new StringBuilder();
		for (Neighbor neighbour : myNeighbors) {
			if (result.length() > 0) {
				result.append(OSPFConstants.PACKET_DELIMETER);
			}
			result.append(neighbour.myRouterID);
		}
		return result.toString();
	}

	private int convertDataBase(StringBuilder result) {
		int count = 0;
		for (Entry<String, List<LSA>> entry : myLSAsDataBase.entrySet()) {
			String routerLSAsInfo = convertRouterLSAs(entry.getKey(), entry.getValue(), false);
			if (routerLSAsInfo != null) {
				if (count > 0) {
					result.append(OSPFConstants.PACKET_DELIMETER);
				}
				count++;
				result.append(routerLSAsInfo);
			}
			for (String externalLSAinfo : convertExternalLSAs(entry.getKey(), entry.getValue(), false)) {
				if (count > 0) {
					result.append(OSPFConstants.PACKET_DELIMETER);
				}
				count++;
				result.append(externalLSAinfo);
			}
		}
		return count;
	}

	private void receiveHelloMessage(OSPF_Packet packet, String inInterface, StringTokenizer st, String routerID,
			long time) {
		st.nextToken(); // network mask
		st.nextToken(); // hello interval
		st.nextToken(); // options
		st.nextToken(); // router priority
		st.nextToken(); // router dead time
		st.nextToken(); // designated router
		st.nextToken(); // backup designated router

		Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
				OSPFConstants.HELLO_PACKET_TYPE_NAME, OSPFConstants.OSPF_LAYER, "Recieving OSPF hello message '"
						+ packet.getMessage() + "' from " + packet.getSourceIPAddress());

		Neighbor neighbor = findNeighbour(routerID);
		if (neighbor == null) {
			neighbor = new Neighbor(routerID, packet.getSourceIPAddress());
			myNeighbors.add(neighbor);
		}
		neighbor.myNeighborLastUpdate = time;
		String thisRouterId = getRouterID();
		if (containsThis(st)) {
			neighbor.myNeighborIsAdjacent = true;
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, "New state with " + routerID
							+ " is adjacent neightbours");
			if (addLSAEntry(routerID, inInterface, packet.getSourceIPAddress())) {
				LSA lsa = findLSA(thisRouterId, OSPFConstants.ROUTER_LSA_TYPE, thisRouterId, routerID, packet
						.getSourceIPAddress(), LSA.DEFAULT_MASK);
				sendLSAUpdateAndUpdateRouterTable(MessageFormat.format(OSPFConstants.LSA_UPDATE_MESSAGE,
						getHeaderMessage(OSPFConstants.LSA_UPDATE_PACKET_TYPE), 1, convertRouterLSAs(thisRouterId,
								Collections.singletonList(lsa), false)), inInterface);
				new SPFCreator(myLSAsDataBase, thisRouterId, myProtocolStack, getClass().getName());
			}
		} else {
			boolean needRemoveLSAs = neighbor.myNeighborIsAdjacent;
			neighbor.myNeighborIsAdjacent = false;
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, "New state with " + routerID
							+ " is neightbours");
			if (needRemoveLSAs) {
				LSA lsa = findLSA(thisRouterId, OSPFConstants.ROUTER_LSA_TYPE, thisRouterId, neighbor.myRouterID,
						neighbor.myRouterInterfaceIP, LSA.DEFAULT_MASK);
				if (lsa != null) {
					myLSAsDataBase.get(thisRouterId).remove(lsa);
					sendLSAUpdateAndUpdateRouterTable(MessageFormat.format(OSPFConstants.LSA_UPDATE_MESSAGE,
							getHeaderMessage(OSPFConstants.LSA_UPDATE_PACKET_TYPE), 1, convertRouterLSAs(thisRouterId,
									Collections.singletonList(lsa), true)), inInterface);
				}
			}
		}
	}

	private void receiveLSAUpdateMessage(OSPF_Packet packet, StringTokenizer st, String routerID, String inInterface) {
		if (checkNeighbor(routerID, packet.getDestIPAddress())) {
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.LSA_UPDATE_PACKET_TYPE_NAME, OSPFConstants.OSPF_LAYER,
					"Packet hasn't been recieved due to sender is not adjacent neighbor.");
			return;
		}

		Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
				OSPFConstants.LSA_UPDATE_PACKET_TYPE_NAME, OSPFConstants.OSPF_LAYER,
				"Recieving OSPF LSA-Update message '" + packet.getMessage() + "' from " + packet.getSourceIPAddress());

		boolean dataBaseChanged = false;
		int lsaCount = Integer.parseInt(st.nextToken());
		for (int i = 0; i < lsaCount; i++) {
			int age = Integer.parseInt(st.nextToken());
			st.nextToken(); // ls options
			int lsaType = Integer.parseInt(st.nextToken());
			String linkStateID = st.nextToken();
			String advertisingRouter = st.nextToken();
			st.nextToken(); // ls sequence number
			st.nextToken(); // ls checksum
			st.nextToken(); // lenght

			if (lsaType == OSPFConstants.ROUTER_LSA_TYPE) {
				if (receiveRouterLSA(age == OSPFConstants.LSA_MAX_AGE, st, linkStateID, advertisingRouter)) {
					dataBaseChanged = true;
				}
			} else if (lsaType == OSPFConstants.EXTERNAL_LSA_TYPE) {
				if (receiveExternalLSA(age == OSPFConstants.LSA_MAX_AGE, st, linkStateID, advertisingRouter)) {
					dataBaseChanged = true;
				}
			}
		}
		if (dataBaseChanged) {
			sendLSAUpdateAndUpdateRouterTable(packet.getMessage(), inInterface);
		}
	}

	private boolean checkNeighbor(String routerID, String destinationIP) {
		Neighbor neighbor = findNeighbour(routerID);
		if (neighbor == null) {
			return false;
		} else {
			if (OSPFConstants.All_SPF_ROUTERS_IP_ADRESS.equals(destinationIP)) {
				return neighbor.myNeighborIsAdjacent;
			} else if (OSPFConstants.All_DR_ROUTERS_IP_ADRESS.equals(destinationIP)) {
				// Receive only DR and BDR routers
				return false;
			} else {
				return neighbor.myRouterInterfaceIP.equals(destinationIP);
			}
		}
	}

	private boolean receiveRouterLSA(boolean removeLSAs, StringTokenizer st, String linkStateID,
			String advertisingRouter) {
		boolean dataBaseChanged = false;
		List<LSA> lsas = myLSAsDataBase.get(advertisingRouter);
		if (lsas == null) {
			lsas = new ArrayList<LSA>();
			myLSAsDataBase.put(advertisingRouter, lsas);
		}
		int linkCount = Integer.parseInt(st.nextToken());
		for (int i = 0; i < linkCount; i++) {
			String linkID = st.nextToken();
			String linkData = st.nextToken();
			int metric = Integer.parseInt(st.nextToken());
			LSA lsa = findLSA(advertisingRouter, OSPFConstants.ROUTER_LSA_TYPE, linkStateID, linkID, linkData,
					LSA.DEFAULT_MASK);
			if (removeLSAs) {
				if (lsa != null) {
					lsas.remove(lsa);
					Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
							OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, getRouterID() + " remove LSA: "
									+ lsa.toString());
					dataBaseChanged = true;
				}
			} else if (lsa == null) {
				lsa = new LSA(linkStateID, linkID, linkData, metric);
				lsas.add(lsa);
				Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
						OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, getRouterID() + " added LSA: "
								+ lsa.toString());
				dataBaseChanged = true;
			} else if (lsa.myMetric != metric) {
				lsa.myMetric = metric;
				Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
						OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, getRouterID()
								+ " update metric for LSA: " + lsa.toString());
				dataBaseChanged = true;
			}
		}
		return dataBaseChanged;
	}

	private boolean receiveExternalLSA(boolean removeLSAs, StringTokenizer st, String linkStateID,
			String advertisingRouter) {
		boolean dataBaseChanged = false;
		List<LSA> lsas = myLSAsDataBase.get(advertisingRouter);
		if (lsas == null) {
			lsas = new ArrayList<LSA>();
			myLSAsDataBase.put(advertisingRouter, lsas);
		}
		String networkMask = st.nextToken();
		int metric = Integer.parseInt(st.nextToken());
		LSA lsa = findLSA(advertisingRouter, OSPFConstants.EXTERNAL_LSA_TYPE, linkStateID, "", "", networkMask);
		if (removeLSAs) {
			if (lsa != null) {
				lsas.remove(lsa);
				Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
						OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, getRouterID()
								+ " remove external LSA: " + lsa.toString());
				dataBaseChanged = true;
			}
		} else if (lsa == null) {
			lsa = new LSA(linkStateID, networkMask, metric);
			lsas.add(lsa);
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, getRouterID() + " added external LSA: "
							+ lsa.toString());
			dataBaseChanged = true;
		} else if (lsa.myMetric != metric) {
			lsa.myMetric = metric;
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, getRouterID()
							+ " update metric for external LSA: " + lsa.toString());
			dataBaseChanged = true;
		}
		return dataBaseChanged;
	}

	private boolean containsThis(StringTokenizer st) {
		while (st.hasMoreTokens()) {
			if (st.nextToken().equals(getRouterID())) {
				return true;
			}
		}
		return false;
	}

	private Neighbor findNeighbour(String routerID) {
		for (Neighbor neighbour : myNeighbors) {
			if (routerID.equals(neighbour.myRouterID)) {
				return neighbour;
			}
		}
		return null;
	}

	private boolean addLSAEntry(String neighborRouterID, String inInterface, String interfaceDestinationIP) {
		String thisRouterID = getRouterID();
		List<LSA> lsas = myLSAsDataBase.get(thisRouterID);
		if (lsas == null) {
			lsas = new ArrayList<LSA>();
			myLSAsDataBase.put(thisRouterID, lsas);
		}
		try {
			int ifaceMetric = myProtocolStack.getParentNode().getNetworkInterface(inInterface).getMetric();
			LSA lsa = findLSA(thisRouterID, OSPFConstants.ROUTER_LSA_TYPE, thisRouterID, neighborRouterID,
					interfaceDestinationIP, LSA.DEFAULT_MASK);
			if (lsa == null) {
				lsa = new LSA(thisRouterID, neighborRouterID, interfaceDestinationIP, ifaceMetric);
				lsas.add(lsa);
				Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
						OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, thisRouterID + " added LSA entry: "
								+ lsa.toString());
				return true;
			} else if (lsa.myMetric != ifaceMetric) {
				lsa.myMetric = ifaceMetric;
				return true;
			}
		} catch (InvalidNetworkInterfaceNameException e) {
			e.printStackTrace();
		}
		return false;
	}

	private LSA findLSA(String routerID, int type, String linkStateID, String linkID, String linkData, String mask) {
		if (!myLSAsDataBase.containsKey(routerID)) {
			return null;
		}
		for (LSA candidate : myLSAsDataBase.get(routerID)) {
			if (candidate.myType == type && candidate.myLinkStateID.equals(linkStateID)
					&& candidate.myLinkID.equals(linkID) && candidate.myLinkData.equals(linkData)
					&& candidate.myNetworkMask.equals(mask)) {
				return candidate;
			}
		}
		return null;
	}

	@Override
	public void Timer(int code) {
		if (myDeviceIsBlock) {
			return;
		}
		myTimerCounter++;
		updateLSAs();
		if (myInterfaces.isEmpty()) {
			return;
		}
		sendHelloPacket();
		if (myTimerCounter >= OSPFConstants.LSA_UPDATE_INTERVAL / OSPFConstants.HELLO_INTERVAL) {
			myTimerCounter = 0;
			StringBuilder dataBase = new StringBuilder();
			int lsasCount = convertDataBase(dataBase);
			sendLSAUpdateAndUpdateRouterTable(MessageFormat.format(OSPFConstants.LSA_UPDATE_MESSAGE,
					getHeaderMessage(OSPFConstants.LSA_UPDATE_PACKET_TYPE), lsasCount, dataBase.toString()), null);
		}
	}

	private void updateLSAs() {
		String thisRouterID = getRouterID();
		List<LSA> lsasToRemove = updateNeighbors(Calendar.getInstance().getTimeInMillis());
		List<LSA> externalLSAs = updateExternalLSAs();
		if (myInterfaces.isEmpty()) {
			return;
		}
		if (!lsasToRemove.isEmpty() || !externalLSAs.isEmpty()) {
			int count = 0;
			StringBuilder lsas = new StringBuilder();
			if (!lsasToRemove.isEmpty()) {
				count++;
				lsas.append(convertRouterLSAs(thisRouterID, lsasToRemove, true));
			}
			if (!externalLSAs.isEmpty()) {
				for (String externalLSAInfo : convertExternalLSAs(thisRouterID, externalLSAs, false)) {
					if (count > 0) {
						lsas.append(OSPFConstants.PACKET_DELIMETER);
					}
					count++;
					lsas.append(externalLSAInfo);
				}
			}
			sendLSAUpdateAndUpdateRouterTable(MessageFormat.format(OSPFConstants.LSA_UPDATE_MESSAGE,
					getHeaderMessage(OSPFConstants.LSA_UPDATE_PACKET_TYPE), count, lsas.toString()), null);
		}
	}

	private List<LSA> updateNeighbors(long time) {
		String thisRouterID = getRouterID();
		List<LSA> lsasToRemove = new ArrayList<LSA>();
		for (Iterator<Neighbor> it = myNeighbors.iterator(); it.hasNext();) {
			Neighbor neighbor = it.next();
			if (time - neighbor.myNeighborLastUpdate >= OSPFConstants.ROUTER_DEAD_INTERVAL) {
				it.remove();
				Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
						OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER,
						"Hello-packets haven't been recieved from neightbour with id " + neighbor.myRouterID + " for "
								+ (OSPFConstants.ROUTER_DEAD_INTERVAL / 1000) + "s. The neighbour state lost.");

				if (neighbor.myNeighborIsAdjacent) {
					LSA lsa = findLSA(thisRouterID, OSPFConstants.ROUTER_LSA_TYPE, thisRouterID, neighbor.myRouterID,
							neighbor.myRouterInterfaceIP, LSA.DEFAULT_MASK);
					if (lsa != null) {
						lsasToRemove.add(lsa);
						myLSAsDataBase.get(thisRouterID).remove(lsa);
					}
				}
			}
		}
		return lsasToRemove;
	}

	private List<LSA> updateExternalLSAs() {
		if (!myDeviceUseRedistribute) {
			return new ArrayList<LSA>();
		}
		List<LSA> newExternalLSAs = new ArrayList<LSA>();
		String thisRouterID = getRouterID();
		newExternalLSAs.addAll(updateLSAFromRouterTable(thisRouterID));
		newExternalLSAs.addAll(updateLSAfromInterfaces(thisRouterID));
		return newExternalLSAs;
	}

	private List<LSA> updateLSAfromInterfaces(String thisRouterID) {
		List<LSA> lsas = new ArrayList<LSA>();
		for (String ifaceName : myProtocolStack.getParentNode().getAllInterfacesNames()) {
			try {
				NetworkInterface iface = myProtocolStack.getParentNode().getNetworkInterface(ifaceName);
				if (!iface.isActive()) {
					continue;
				}
				String mask = myProtocolStack.getSubnetMask(ifaceName);
				String ip = IPV4Address.networkAddressByIPandMask(myProtocolStack.getIPAddress(ifaceName), mask);
				LSA lsa = findLSA(thisRouterID, OSPFConstants.EXTERNAL_LSA_TYPE, ip, "", "", mask);
				if (lsa == null) {
					lsa = new LSA(ip, mask, iface.getMetric());
					Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
							OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, thisRouterID
									+ " added external LSA entry: " + lsa.toString());
					lsas.add(lsa);
				} else if (lsa.myMetric != iface.getMetric()) {
					lsa.myMetric = iface.getMetric();
					lsas.add(lsa);
				}
			} catch (InvalidNetworkInterfaceNameException e) {
				e.printStackTrace();
			}
		}
		return lsas;
	}

	private List<LSA> updateLSAFromRouterTable(String thisRouterID) {
		List<LSA> lsas = new ArrayList<LSA>();
		for (String destinationIP : myProtocolStack.getRouteTableEntries()) {
			if (destinationIP == null || "default".equals(destinationIP)) {
				continue;
			}
			Route_entry entry = myProtocolStack.getRouteTableEntry(destinationIP);
			if (entry.Type != Route_entry.RouterEntryType.OSPF && !myInterfaces.contains(entry.iFace)) {
				LSA lsa = findLSA(thisRouterID, OSPFConstants.EXTERNAL_LSA_TYPE, entry.destIP, "", "", entry.genMask);
				if (lsa == null) {
					lsa = new LSA(entry.destIP, entry.genMask, entry.metric);
					Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
							OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, thisRouterID
									+ " added external LSA entry: " + lsa.toString());
					lsas.add(lsa);
				} else if (lsa.myMetric != entry.metric) {
					lsa.myMetric = entry.metric;
					lsas.add(lsa);
				}
			}
		}
		return lsas;
	}

	private void sendLSAUpdateAndUpdateRouterTable(String message, String sourceInterface) {
		sendLSAUpdatePacket(message, sourceInterface);
		new SPFCreator(myLSAsDataBase, getRouterID(), myProtocolStack, getClass().getName()).updateRouterTable();
	}

	private void sendHelloPacket() {
		myDeviceIsBlock = true;
		String message = MessageFormat.format(OSPFConstants.HELLO_MESSAGE,
				getHeaderMessage(OSPFConstants.HELLO_PACKET_TYPE), "0.0.0.0", OSPFConstants.HELLO_INTERVAL, "0", "0",
				OSPFConstants.ROUTER_DEAD_INTERVAL, "0.0.0.0", "0.0.0.0", convertNeighbors());
		Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
				OSPFConstants.HELLO_PACKET_TYPE_NAME, OSPFConstants.OSPF_LAYER,
				"Sending OSPF broadcast request message '" + message + "'");
		for (String iface : myInterfaces) {
			sendPacket(message, OSPFConstants.All_SPF_ROUTERS_IP_ADRESS, iface);
		}
		myDeviceIsBlock = false;
	}

	private void sendLSAUpdatePacket(String message, String notSendInterface) {
		myDeviceIsBlock = true;
		for (String iface : myInterfaces) {
			if (notSendInterface != null && notSendInterface.equals(iface)) {
				// continue;
				// ignore for DR but not ignore not DR
			}
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.LSA_UPDATE_PACKET_TYPE_NAME, OSPFConstants.OSPF_LAYER,
					"Sending OSPF LSA-Update message '" + message + "' through interface " + iface);
			sendPacket(message, OSPFConstants.All_SPF_ROUTERS_IP_ADRESS, iface);
		}
		myDeviceIsBlock = false;
	}

	private void sendPacket(String message, String destinationIP, String iface) {
		try {
			if (OSPFConstants.All_SPF_ROUTERS_IP_ADRESS.equals(destinationIP)
					|| OSPFConstants.All_DR_ROUTERS_IP_ADRESS.equals(destinationIP)) {
				if (iface == null) {
					System.out.println("interface name cannot be null");
				}
				OSPF_Packet packet = new OSPF_Packet(destinationIP, message);
				packet.setSourceIPAddress(myProtocolStack.getIPAddress(iface));
				((NetworkLayerDevice) myProtocolStack.getParentNode()).sendPacket("FF:FF:FF:FF:FF:FF", packet, iface);
			} else {
				myProtocolStack.sendPacket(new OSPF_Packet(destinationIP, message));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void receivePacket(OSPF_Packet packet, String inInterface) {
		long time = Calendar.getInstance().getTimeInMillis();

		if (!myInterfaces.contains(inInterface)) {
			Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
					OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, "Packet hasn't been recieved: interface "
							+ inInterface + " is not ospf interface.");
			return;
		}

		Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
				OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER, "OSPF message recieved...");

		try {
			myDeviceIsBlock = true;
			StringTokenizer st = new StringTokenizer(packet.getMessage(), OSPFConstants.PACKET_DELIMETER);
			if (Integer.parseInt(st.nextToken()) != OSPFConstants.OSPF_VERSION) {
				Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
						OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER,
						"Packet hasn't been recieved due to wrong ospf version.");
				return;
			}
			int type = Integer.parseInt(st.nextToken());
			st.nextToken(); // length
			String routerID = st.nextToken();
			if (!st.nextToken().equals(myArea)) {
				Simulation.addLayerInfo(getClass().getName(), myProtocolStack.getParentNodeName(),
						OSPFConstants.OSPF_DATA_TYPE, OSPFConstants.OSPF_LAYER,
						"Packet hasn't been recieved due to different ospf areas.");
				return;
			}
			st.nextToken(); // check sum
			st.nextToken(); // authentication type
			st.nextToken(); // authentication
			switch (type) {
			case OSPFConstants.HELLO_PACKET_TYPE: {
				receiveHelloMessage(packet, inInterface, st, routerID, time);
				break;
			}
			case OSPFConstants.LSA_UPDATE_PACKET_TYPE: {
				receiveLSAUpdateMessage(packet, st, routerID, inInterface);
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			myDeviceIsBlock = false;
		}

	}

	public void setArea(int area) {
		myArea = "";
		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				myArea = "." + myArea;
			}
			myArea = area % 255 + myArea;
			area /= 255;
		}
	}

	public int getArea() {
		StringTokenizer st = new StringTokenizer(myArea, ".");
		int area = 0;
		int k = 256 * 256 * 256;
		while (st.hasMoreTokens()) {
			area += k * Integer.parseInt(st.nextToken());
			k /= 256;
		}
		return area;
	}

	public boolean isDeviceUseRedistribute() {
		return myDeviceUseRedistribute;
	}

	public void setUseRedistribute(boolean useRedistribute) {
		this.myDeviceUseRedistribute = useRedistribute;
	}

	public List<String> interfaces() {
		return Collections.unmodifiableList(myInterfaces);
	}

	/**
	 * Neighbor structure
	 * 
	 * @author Anatoly Chekh
	 */
	private class Neighbor {

		private boolean myNeighborIsAdjacent;

		private final String myRouterID;

		private final String myRouterInterfaceIP;

		private long myNeighborLastUpdate;

		private Neighbor(String routerID, String routerInterfaceIP) {
			myRouterID = routerID;
			myRouterInterfaceIP = routerInterfaceIP;
		}

	}

}

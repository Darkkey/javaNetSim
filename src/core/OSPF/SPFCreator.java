package core.OSPF;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import core.Simulation;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.ProtocolStack;
import core.protocolsuite.tcp_ip.Route_entry;

/**
 * Update router table. Creates graph from data base and runs Dijkstra algorithm
 * 
 * @author Anatoly Chekh
 */
class SPFCreator {

	private final HashMap<String, List<LSA>> myDataBate;
	private final String myRootRouterID;
	private final ProtocolStack myParentStack;
	private final String myInfoClass;

	SPFCreator(HashMap<String, List<LSA>> dataBate, String rootRouterID, ProtocolStack parentStack, String infoClass) {
		myDataBate = dataBate;
		myRootRouterID = rootRouterID;
		myParentStack = parentStack;
		myInfoClass = infoClass;
	}

	void updateRouterTable() {
		removeOldOSPFRouterEnties();
		HashMap<String, HashMap<String, LSA>> shortestGraph = getShortestGraph();
		Simulation.addLayerInfo(myInfoClass, myParentStack.getParentNodeName(), OSPFConstants.OSPF_DATA_TYPE,
				OSPFConstants.OSPF_LAYER, "Updating router table...");
		for (Entry<String, LSA> rootNeighbor : shortestGraph.get(myRootRouterID).entrySet()) {
			if (rootNeighbor.getValue().myType == OSPFConstants.ROUTER_LSA_TYPE) {
				String gateway = rootNeighbor.getValue().myLinkData;
				updateRecRouterTable(shortestGraph, rootNeighbor.getKey(), gateway, findInterface(gateway),
						rootNeighbor.getValue().myMetric);
			}
		}
	}

	private String findInterface(String ip) {
		for (String iface : myParentStack.getParentNode().getAllInterfacesNames()) {
			try {
				IPV4Address adress = new IPV4Address(ip);
				adress.setCustomSubnetMask(myParentStack.getSubnetMask(iface));
				if (adress.compareToSubnet(myParentStack.getIPAddress(iface))) {
					return iface;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void updateRecRouterTable(HashMap<String, HashMap<String, LSA>> shortestGraph, String vertexID,
			String gateway, String iface, int currentMetric) {
		for (Entry<String, LSA> neighbor : shortestGraph.get(vertexID).entrySet()) {
			String destinationIP = neighbor.getValue().getDestinationIP();
			Route_entry routerEntry = new Route_entry(destinationIP, gateway, neighbor.getValue().myNetworkMask, iface,
					Route_entry.RouterEntryType.OSPF, currentMetric + neighbor.getValue().myMetric, Calendar
							.getInstance().getTimeInMillis());
			myParentStack.addRoute(routerEntry);
			Simulation.addLayerInfo(myInfoClass, myParentStack.getParentNodeName(), OSPFConstants.OSPF_DATA_TYPE,
					OSPFConstants.OSPF_LAYER, myRootRouterID
							+ " added Router Entry: "
							+ MessageFormat.format("IP: {0}; gateway: {1}; mask: {2}; metric: {3}", routerEntry.destIP,
									routerEntry.gateway, routerEntry.genMask, routerEntry.metric));
			updateRecRouterTable(shortestGraph, neighbor.getKey(), gateway, iface, currentMetric
					+ neighbor.getValue().myMetric);
		}
	}

	private HashMap<String, HashMap<String, LSA>> getShortestGraph() {
		HashMap<String, HashMap<String, LSA>> result = new HashMap<String, HashMap<String, LSA>>();

		PriorityQueue<VertexCandidate> candidates = new PriorityQueue<VertexCandidate>();

		candidates.add(new VertexCandidate(myRootRouterID, null, null, 0));

		while (!candidates.isEmpty()) {
			VertexCandidate vertex = candidates.poll();
			result.put(vertex.myVertextID, new HashMap<String, LSA>());
			if (vertex.myPreviousVertex != null) {
				HashMap<String, LSA> edges = result.get(vertex.myPreviousVertex);
				edges.put(vertex.myVertextID, vertex.myEdge);
			}

			if (myDataBate.containsKey(vertex.myVertextID)) {
				for (LSA lsa : myDataBate.get(vertex.myVertextID)) {
					String vertexID = lsa.getDestinationID();
					if (result.containsKey(vertexID)) {
						continue;
					}
					VertexCandidate candidate = findCandidate(candidates, vertexID);
					if (candidate == null) {
						candidates.add(new VertexCandidate(vertexID, vertex.myVertextID, lsa, vertex.myMetric
								+ lsa.myMetric));
					} else if (candidate.myMetric > vertex.myMetric + lsa.myMetric) {
						candidates.remove(candidate);
						candidates.add(new VertexCandidate(vertexID, vertex.myVertextID, lsa, vertex.myMetric
								+ lsa.myMetric));
					}
				}
			}
		}

		return result;
	}

	private static VertexCandidate findCandidate(Collection<VertexCandidate> candidates, String vertexID) {
		for (VertexCandidate vertexCandidate : candidates) {
			if (vertexCandidate.myVertextID.equals(vertexID)) {
				return vertexCandidate;
			}
		}
		return null;
	}

	private void removeOldOSPFRouterEnties() {
		for (String routerEntry : myParentStack.getRouteTableEntries()) {
			if (routerEntry == null) {
				continue;
			}
			Route_entry entry = myParentStack.getRouteTableEntry(routerEntry);
			if (entry.Type == Route_entry.RouterEntryType.OSPF) {
				myParentStack.removeRoute(routerEntry);
			}
		}
	}

	private class VertexCandidate implements Comparable<VertexCandidate> {

		private final String myVertextID;

		private final String myPreviousVertex;

		private final LSA myEdge;

		private final int myMetric;

		public VertexCandidate(String vertextID, String previousVertex, LSA edge, int metric) {
			myVertextID = vertextID;
			myPreviousVertex = previousVertex;
			myEdge = edge;
			myMetric = metric;
		}

		//@Override
		public int compareTo(VertexCandidate vertexCandidate) {
			return myMetric - vertexCandidate.myMetric;
		}

	}

}

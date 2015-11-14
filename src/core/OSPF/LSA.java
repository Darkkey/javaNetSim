package core.OSPF;

import java.text.MessageFormat;

/**
 * LSA structure
 * 
 * @author Anatoly Chekh
 */
class LSA {

	static final String DEFAULT_MASK = "255.255.255.255";

	/**
	 * LSA-type: 1 - Router 5 - External
	 */
	final int myType;

	/**
	 * Area identifier if lsa type is Router-LSA then link state ID is Router ID
	 * if lsa type is External-LSA then link state ID is external network IP
	 */
	final String myLinkStateID;

	/**
	 * equals neighbor router ID
	 */
	final String myLinkID;

	/**
	 * equals neighbor router interface IP
	 */
	final String myLinkData;

	/**
	 * only for External-LSAs.
	 */
	final String myNetworkMask;

	int myMetric;

	/**
	 * Create Router-LSA
	 */
	LSA(String linkStateID, String linkID, String linkData, int metric) {
		this(OSPFConstants.ROUTER_LSA_TYPE, linkStateID, linkID, linkData, DEFAULT_MASK, metric);
	}

	/**
	 * Create External-LSA
	 */
	LSA(String linkStateID, String networkMask, int metric) {
		this(OSPFConstants.EXTERNAL_LSA_TYPE, linkStateID, "", "", networkMask, metric);
	}

	LSA(int type, String linkStateID, String linkID, String linkData, String networkMask, int metric) {
		myType = type;
		myLinkStateID = linkStateID;
		myLinkID = linkID;
		myLinkData = linkData;
		myNetworkMask = networkMask;
		myMetric = metric;
	}

	String getDestinationIP() {
		if (myType == OSPFConstants.ROUTER_LSA_TYPE) {
			return myLinkData;
		} else if (myType == OSPFConstants.EXTERNAL_LSA_TYPE) {
			return myLinkStateID;
		}
		return null;	
	}
	
	String getDestinationID() {
		if (myType == OSPFConstants.ROUTER_LSA_TYPE) {
			return myLinkID;
		} else if (myType == OSPFConstants.EXTERNAL_LSA_TYPE) {
			return myLinkStateID;
		}
		return null;	
	}

	@Override
	public String toString() {
		return MessageFormat.format("type: {0}; link state id: {1}; link id: {2}; link data: {3}; metric: {4}", myType,
				myLinkStateID, myLinkID, myLinkData, myMetric);
	}
}

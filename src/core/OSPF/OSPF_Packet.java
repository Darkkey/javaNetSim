package core.OSPF;

import core.protocolsuite.tcp_ip.IP_packet;

public class OSPF_Packet extends IP_packet {

	private final String myMessage;
	
	public OSPF_Packet(String inDestIPAddress, String message) {
		super(inDestIPAddress);
		myMessage = message;
	}
	
	public String getMessage() {
		return myMessage;
	}

}

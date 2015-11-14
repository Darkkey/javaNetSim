/*
Java Network Simulator (jNetSim)

Copyright (c) 2005 - 2008, Alexander K. Bolshev [Key];  All rights reserved.
Copyright (c) 2004, jFirewallSim development team;  All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

	- Redistributions of source code must retain the above copyright notice, this list
	  of conditions and the following disclaimer.
	- Redistributions in binary form must reproduce the above copyright notice, this list
	  of conditions and the following disclaimer in the documentation and/or other
	  materials provided with the distribution.
	- Neither the name of the Ice Team nor the names of its
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

package core.protocolsuite.tcp_ip;

import java.io.Serializable;
import java.util.Hashtable;

import core.CommunicationException;
import core.LayerInfo;
import core.LowLinkException;
import core.Simulation;

/**
 * 
 * @author angela_brown
 * 
 * @author bevan_calliess
 * 
 * @author luke_hamilton
 * 
 * @since Sep 17, 2004
 * 
 * @version v0.00
 * 
 */

public class ICMP implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4349938325413984166L;

	private ProtocolStack mParentStack;

	private Hashtable<Integer, ICMP_packet> receivedPackets; // Message ID and
														// received packets.

	private int lastMessageID = 0; // last packet message id was sent

	/**
	 * 
	 * Assigns the ParentStack
	 * 
	 * @author bevan_calliess
	 * 
	 * @author luke_hamilton
	 * 
	 * @param inParentStack -
	 *            Protocol Stack
	 * 
	 * @version v0.20
	 * 
	 */

	public ICMP(ProtocolStack inParentStack) {

		mParentStack = inParentStack;

		receivedPackets = new Hashtable<Integer, ICMP_packet>();

	}

	/**
	 * 
	 * Receives the ICMP packet and checks the message
	 * 
	 * code attached to it. If 8, it is an echo request and
	 * 
	 * pass it of. If 0, print out a message to the layer info.
	 * 
	 * @author bevan_calliess
	 * 
	 * @author angela_brown
	 * 
	 * @author robert_hulford
	 * 
	 * @param inPacket -
	 *            A PAcket
	 * 
	 * @version v0.20
	 * 
	 */

	public void receiveICMPPacket(ICMP_packet inPacket) throws LowLinkException {

		if (inPacket.getMessageCode() == 8) {

			echoRequest(inPacket);

		}else if (inPacket.getMessageCode() == 0) {

			receivedPackets.put(inPacket.getMessageID(), inPacket);

			// Create Layer info

			LayerInfo pingInfo = new LayerInfo(getClass().getName());

			pingInfo.setObjectName(mParentStack.getParentNodeName());

			pingInfo.setDataType("Echo Reply Packet");

			pingInfo.setLayer("Network");

			pingInfo.setDescription("Echo reply packet received from "
					+ inPacket.getSourceIPAddress());

			Simulation.addLayerInfo(pingInfo);

		}else if (inPacket.getMessageCode() == ICMP_packet.DESTINATION_UNREACHABLE){
			receivedPackets.put(inPacket.getMessageID(), inPacket);
			Simulation.addLayerInfo(getClass().getName(),
					mParentStack.getParentNodeName(), "ICMP Dst Unreach", "Network",
					"Recieved ICMP Destination Unreacheable from "
							+ inPacket.getSourceIPAddress());
		}else if (inPacket.getMessageCode() == ICMP_packet.TIME_EXCEEDED){
			receivedPackets.put(inPacket.getMessageID(), inPacket);
			Simulation.addLayerInfo(getClass().getName(),
					mParentStack.getParentNodeName(), "ICMP Time Exceeded", "Network",
					"Recieved ICMP Time Exceeded from "
							+ inPacket.getSourceIPAddress());
		}

	}

	/**
	 * 
	 * This method will send a reply to a ping request
	 * 
	 * @author angela_brown
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @author luke_hamilton
	 * 
	 * @param inPacket -
	 *            The original packet received by this Node
	 * 
	 * @exception CommunicationException
	 * 
	 * @version v0.20
	 * 
	 */

	private void echoRequest(ICMP_packet inPacket) throws LowLinkException {

		String destIPAddress = inPacket.getSourceIPAddress();

		ICMP_packet pingPacket = new ICMP_packet(destIPAddress);

		pingPacket.setMessageCode(ICMP_packet.ECHO_REPLY);
		pingPacket.setMessageID(inPacket.getMessageID());

		// Create Layer info

		LayerInfo pingInfo = new LayerInfo(getClass().getName());

		pingInfo.setObjectName(mParentStack.getParentNodeName());

		pingInfo.setDataType("Echo Reply Packet");

		pingInfo.setLayer("Network");

		pingInfo
				.setDescription("Created Echo Reply packet to " + destIPAddress);

		Simulation.addLayerInfo(pingInfo);

		try {

			mParentStack.sendPacket(pingPacket);

		} catch (CommunicationException e) {

			// This exception is caught but not acted upon as the

			// Echo request on any PC would not notify the user if it failed

			// TODO Once the recording of a sim process is implemented this
			// should record the error.

		}
		// catch(LowLinkException e){

		// This exception is caught but not acted upon as the

		// Echo request on any PC would not notify the user if it failed

		// TODO Once the recording of a sim process is implemented this should
		// record the error.

		// }

	}

	/**
	 * 
	 * This method will generate an ICMP echo request and send it
	 * 
	 * out via the Protocol stacks send method.
	 * 
	 * @author angela_brown
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @param inDestIPAddress -
	 *            Destination IP Address
	 * 
	 * @return ICMP_packet - Ping Packet
	 * 
	 */

	public ICMP_packet sendPing(String inDestIPAddress) {

		ICMP_packet pingPacket = new ICMP_packet(inDestIPAddress);

		pingPacket.setMessageCode(ICMP_packet.ECHO_REQUEST);

		pingPacket.setMessageID(++lastMessageID);

		// Create Layer info

		LayerInfo pingInfo = new LayerInfo(getClass().getName());

		pingInfo.setObjectName(mParentStack.getParentNodeName());

		pingInfo.setDataType("Echo Request Packet");

		pingInfo.setLayer("Network");

		pingInfo.setDescription("Created Echo Request packet to "
				+ inDestIPAddress);

		Simulation.addLayerInfo(pingInfo);

		return pingPacket;

	}

	public ICMP_packet getReceivedICMPPacket(int messageID) {
		if (receivedPackets.containsKey(messageID))
			return receivedPackets.get(messageID);
		return null;
	}

	public ICMP_packet removeReceivedICMPPacket(int messageID) {
		if (receivedPackets.containsKey(messageID))
			return receivedPackets.remove(messageID);
		return null;
	}

}// EOF


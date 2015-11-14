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

package core;

import java.util.LinkedList;

/**
 * EthernetLink extends Link. It sets two Interface links to a pc.
 * 
 * @author luke_hamilton
 * @author bevan_calliess
 * @since Sep 17, 2004
 * @version v0.20
 */

public class EthernetLink extends Link {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5723796766035188219L;

	private static final int MAX_COUNT = 100;

	java.security.SecureRandom rng;

	private LinkedList<Boolean> transportedPacket = new LinkedList<Boolean>();
	private int acceptedPacketCount = 0;

	/**
	 * Constructor to be used by the Simulation when connecting 2 PC's We have
	 * made a design decision to restrict a Ethernet link to only has 2 ends.
	 * 
	 * @author bevan_calliess
	 * @author luke_hamilton
	 * @param String
	 *            Name - Node name eg: PC1
	 * @param inInterface1
	 *            The first Interface to connect this link to eg: eth0
	 * @param inInterface2
	 *            The Seceond Interface to connect this link to eg: eth1
	 * @throws InvalidLinkConnectionException
	 */
	public EthernetLink(String inName, NetworkInterface inFirstNodeInterface, NetworkInterface inSecondNodeInterface)
			throws InvalidLinkConnectionException {
		super(inName);
		NetworkInterfaces.add(inFirstNodeInterface);
		NetworkInterfaces.add(inSecondNodeInterface);
		inFirstNodeInterface.setConnectedLink(this);
		inSecondNodeInterface.setConnectedLink(this);
		rng = new java.security.SecureRandom();
	}

	public EthernetLink(String inName, NetworkInterface inFirstNodeInterface, NetworkInterface inSecondNodeInterface,
			double sieveCoeff) throws InvalidLinkConnectionException {
		super(inName);
		NetworkInterfaces.add(inFirstNodeInterface);
		NetworkInterfaces.add(inSecondNodeInterface);
		inFirstNodeInterface.setConnectedLink(this);
		inSecondNodeInterface.setConnectedLink(this);
		rng = new java.security.SecureRandom();
		this.setSC(sieveCoeff);
		rng = new java.security.SecureRandom();
	}

	/**
	 * This method checks to see if the sourceName is within the hashtable that
	 * is stored within the NetworkInterface Layer. If it is send inPacket off
	 * to NetworkInterface.
	 * 
	 * @param inPacket
	 *            - Ethernet Packet
	 * @param inSourceName
	 *            - Source name of node
	 * @throws core.LowLinkException
	 */
	public void transportPacket(Ethernet_packet inPacket, String inSourceName) throws LowLinkException {

		for (NetworkInterface temp : NetworkInterfaces) {
			if (!temp.isOn() || (!temp.isUP()))
				return;

			if (!temp.getSourceName().equals(inSourceName)) {
				if ((sievingCoefficient) > rng.nextInt(100)) {
					updateInformation(true);
					temp.receivePacket(inPacket);
				} else {
					updateInformation(false);
					LayerInfo frameErrInfo = new LayerInfo(getClass().getName());
					frameErrInfo.setObjectName(inSourceName);
					frameErrInfo.setDataType("Ethernet Packet");
					frameErrInfo.setLayer("Link");
					frameErrInfo.setDescription("(***) Packet lost due to physical link problems!");
					Simulation.addLayerInfo(frameErrInfo);
				}

				/*
				 * int cat = FalseRandom.Kotochigov();
				 * 
				 * System.out.println(
				 * "Cat runs across the blackboard and generates... " + cat +
				 * "!");
				 * 
				 * if(cat==1) temp.receivePacket(inPacket); else throw new
				 * LowLinkException
				 * ("(***) Packet lost due to physical link problems!");
				 */
			}
		}
	}

	private void updateInformation(boolean isPacketRecieved) {
		transportedPacket.addLast(isPacketRecieved);
		if (isPacketRecieved) {
			acceptedPacketCount++;
		}
		if (transportedPacket.size() > MAX_COUNT) {
			if (transportedPacket.removeFirst()) {
				acceptedPacketCount--;
			}
		}
	}

	public int getAcceptedPacketPercent() {
		if (transportedPacket.size() == 0) {
			return 100;
		}
		return 100 * acceptedPacketCount / transportedPacket.size();
	}
	
}

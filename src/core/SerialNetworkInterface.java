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

public class SerialNetworkInterface extends NetworkInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4159063989094846370L;
	/**
	 * FrameRelay ClockRate
	 * */
	protected int ClockRate;
	public final static int DEFAULT_CLOCKRATE = 400000;
	public final static int MAX_CLOCKRATE = 400000;
	public final static int MIN_CLOCKRATE = 0;

	private static final int MAX_COUNT = 50;

	private LinkedList<Boolean> transportedPacket = new LinkedList<Boolean>();
	private int acceptedPacketCount = 0;

	protected SerialNetworkInterface(long UID, String inName, Node parent) {
		super(UID, inName, parent);
		ClockRate = SerialNetworkInterface.DEFAULT_CLOCKRATE;
	}

	@Override
	protected void receivePacket(Packet inPacket) throws LowLinkException {
		Serial_packet tempPacket = (Serial_packet) inPacket;

		if ((tempPacket.getClockRate() == 0 && ClockRate > 0) || (tempPacket.getClockRate() > 0 && ClockRate == 0)) {
			updateInformation(true);
			LayerInfo pingInfo = new LayerInfo(getClass().getName());
			pingInfo.setObjectName(parentNode.getName());
			pingInfo.setDataType("HDLC Packet");
			pingInfo.setLayer("Link");
			pingInfo.setDescription("Recieved and accepted packet at interface " + name);
			Simulation.addLayerInfo(pingInfo);
			Packet temp = tempPacket.getData();

			parentNode.receivePacket(temp, name);
		} else {
			updateInformation(false);
			LayerInfo pingInfo = new LayerInfo(getClass().getName());
			pingInfo.setObjectName(parentNode.getName());
			pingInfo.setDataType("HDLC Packet");
			pingInfo.setLayer("Link");
			pingInfo.setDescription("Recieved signal at interface " + name + " dropped due to invalid Clock rate.");
			Simulation.addLayerInfo(pingInfo);
		}
	}

	@Override
	public int getType() {
		return NetworkInterface.Serial;
	}

	@Override
	protected void sendPacket(Packet inPacket) throws LowLinkException {

		if (!parentNode.On || !up)
			return;

		Serial_packet Packet = new Serial_packet(inPacket, ClockRate);

		SerialLink temp = (SerialLink) connectedLink;

		// Create Layer info
		LayerInfo pingInfo = new LayerInfo(getClass().getName());
		pingInfo.setObjectName(parentNode.getName());
		pingInfo.setDataType("HDLC Packet");
		pingInfo.setLayer("Link");
		pingInfo.setDescription("Sending packet from interface " + name);
		Simulation.addLayerInfo(pingInfo);

		if (temp != null) {
			try {
				temp.transportPacket(Packet, getSourceName());
			} catch (LowLinkException ex) {
				LayerInfo frameErrInfo = new LayerInfo(getClass().getName());
				frameErrInfo.setObjectName(parentNode.getName());
				frameErrInfo.setDataType("HDLC Packet");
				frameErrInfo.setLayer("Link");
				frameErrInfo.setDescription(ex.toString());
				Simulation.addLayerInfo(frameErrInfo);
				// throw new LowLinkException(ex.toString());
			}
		}
	}

	/**
	 * Returns the NetworkInterface's MAC Address.
	 * 
	 * @author bevan_calliess
	 * @return MACAddress
	 * @version v0.20
	 */
	public int getClockRate() {
		return ClockRate;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	public final void setClockRate(int ClockRate) {
		this.ClockRate = ClockRate;
	}

	/**
	 * This method displays details about the current interface card
	 * 
	 * @author bevan_calliess
	 * @return String
	 * @version v0.20
	 */
	@Override
	protected String getDetails() {

		return "Interface: " + name + "\t\tClock Rate: " + ClockRate + "\t\t" + getConnectLinkDetails();
	}

	@Override
	public void Timer(int temp) {
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

	@Override
	public int getAcceptedPacketPercent() {
		if (transportedPacket.size() == 0) {
			return 100;
		}
		return 100 * acceptedPacketCount / transportedPacket.size();
	}
	
	@Override
	public int getInterfaceBandwidth() {
		return 1;
	}
	
}

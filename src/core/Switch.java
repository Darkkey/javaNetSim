/*
 Java Network Simulator (jNetSim)

 Copyright (c) 2005, Ice Team;  All rights reserved.
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
package core;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class extends of the DataLinkLayerDevice and allows the creation of
 * switches
 * 
 * @author bevan_calliess
 * @author luke_hamilton
 * @author Key
 * @since Nov 8, 2005
 * @version v0.22
 */

public class Switch extends DataLinkLayerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5320196830045694816L;

	/**
	 * This creates a layer 2 switch with a default of 5 interface (ports)
	 * 
	 * @author luke_hamilton
	 * @param inName
	 *            The name of the Switch
	 * @param inProtocolStackLayers
	 * @version v0.20
	 */

	int sz = 0;

	int buffSize = 255;

	Hashtable IntCaches;

	public Switch(String inName, boolean inOn) {
		super(inName, 1, inOn); // pass name and protocolstack layer

		IntCaches = new Hashtable();
	}

	@Override
	public void addNetworkInterface(String name, int type, boolean active) {
		if (!active) {
			super.addNetworkInterface(name, type, false, 0);
			IntCaches.put(name, new Hashtable());
		}
	}

	@Override
	public void Reset() {
		sz = 0;
		Enumeration it;
		String nic = "";

		it = NetworkInterfacetable.elements();

		while (it.hasMoreElements()) {
			NetworkInterface tempInterface = (NetworkInterface) it
					.nextElement();
			nic = tempInterface.getName();
			Hashtable outInt = (Hashtable) IntCaches.get(nic);
			outInt.clear();
		}
		super.Reset();
	}

	@Override
	public int getState() {
		return sz;
	}

	public String getCache() {
		Enumeration it, it2;
		String nic = "";
		String result = "";

		it = NetworkInterfacetable.elements();

		while (it.hasMoreElements()) {
			NetworkInterface tempInterface = (NetworkInterface) it
					.nextElement();
			nic = tempInterface.getName();
			Hashtable outInt = (Hashtable) IntCaches.get(nic);

			result = result + nic + ": ";

			it2 = outInt.keys();

			while (it2.hasMoreElements()) {
				String mac = (String) it2.nextElement();
				result = result + mac + "\t";
			}

			result = result + "\n";
		}

		return result;
	}

	/**
	 * This method will recieve a packet from any of the connected links and the
	 * copy the Packet and distribute a copy to each of the other connected
	 * links.
	 * 
	 * @author bevan_calliess
	 * @param inPacket -
	 *            The packet to be transported
	 * @param inLinkName -
	 *            The name of the link that sent the packet eg: eth0
	 */

	@Override
	protected void receivePacket(Packet inPacket, String inInterfaceName)
			throws LowLinkException {
		if (sz != 1) {
			Ethernet_packet tempPacket = (Ethernet_packet) inPacket;
			Enumeration it;
			boolean intFound = false;
			String nic = "";

			try {
				Hashtable inInt = (Hashtable) IntCaches.get(inInterfaceName);
				inInt.put(tempPacket.getSourceMACAddress(), "1");
				Ethernet_packet copyPacket = new Ethernet_packet(tempPacket
						.getData(), tempPacket.getDestinationMACAddress(),
						tempPacket.getSourceMACAddress());
				// tag it
				copyPacket.vlan_id = tempPacket.vlan_id;

				it = NetworkInterfacetable.elements();
				while (it.hasMoreElements()) {
					NetworkInterface tempInterface = (NetworkInterface) it
							.nextElement();
					nic = tempInterface.getName();
					Hashtable outInt = (Hashtable) IntCaches.get(nic);
					if (outInt.get(tempPacket.getDestinationMACAddress()) != null) {
						intFound = true;
						try {
							if(!tempInterface.getName().equals(inInterfaceName))
								tempInterface.sendPacket(copyPacket);
						} catch (NullPointerException e) {
							System.out.println("Switch.java: " + e.toString());
						}
					}
				}

				if (intFound)
					return;

				it = NetworkInterfacetable.elements();
				while (it.hasMoreElements()) {
					// Test to see if the current Interface is the Interface
					// that sent in the packet
					// if it is skip that interface
					NetworkInterface tempInterface = (NetworkInterface) it
							.nextElement();
					if (!tempInterface.getName().equals(inInterfaceName)) {

						try {
							tempInterface.sendPacket(copyPacket);
						} catch (NullPointerException e) {
							System.out.println("Switch.java: " + e.toString());
						}
					}

				}

			} catch (Throwable th) {
				if (th.toString().contains(
						"Packet lost due to physical link problems!")) {
					throw new LowLinkException(th.toString());
				} else {
					sz = 1;
					System.out.println(th.toString());
					throw new LowLinkException(
							"Switch buffer overflow (packet loop flood?).");
				}
			}
		}
	}
}

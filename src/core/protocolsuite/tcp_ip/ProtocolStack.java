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

package core.protocolsuite.tcp_ip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import core.ApplicationLayerDevice;
import core.CommunicationException;
import core.InvalidDefaultGatewayException;
import core.InvalidNetworkInterfaceNameException;
import core.LayerInfo;
import core.LowLinkException;
import core.NetworkInterface;
import core.NetworkLayerDevice;
import core.Node;
import core.Packet;
import core.Simulation;
import core.TransportLayerException;
import core.TransportLayerPortException;
import core.OSPF.OSPF;
import core.OSPF.OSPFConstants;
import core.OSPF.OSPF_Packet;

/**
 * 
 * @author luke_hamilton
 * 
 * @author angela_brown
 * 
 * @author michael_reith
 * 
 * @author robert_hulford
 * 
 * @author bevan_calliess
 * 
 * @since Sep 17, 2004
 * 
 * @version v0.20
 * 
 * 
 * 
 */

public class ProtocolStack extends core.ProtocolStack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5463115741095263969L;

	private RARP mRARPprotocol = null;

	private ICMP mICMPprotocol = null;

	private ARP mARPprotocol = null;

	private Tcp mTCPprotocol = null;

	private IpV4 mIPprotocol = null;

	private Udp mUDPprotocol = null;

	private Node mParentNode = null;

	private socketLayer mSL = null;

	private OSPF mOSPFProtocol = null;

	private int packetinputIPCounter; // counter for input IP Packets

	private int packetoutputIPCounter; // counter for output IP Packets

	private int packetARPCounter; // counter for ARP Packets

	/**
	 * 
	 * Constructs a ProtocolStack which contains a reference to the node.
	 * 
	 * @author luke_hamilton
	 * 
	 * @version v0.20
	 * 
	 */

	public ProtocolStack(Node node, int inProtocolStackLayers) {

		if (inProtocolStackLayers == 3) {// create a layer 3 protocol stack

			mRARPprotocol = new RARP();

			mARPprotocol = new ARP(this);

			mIPprotocol = new IpV4((NetworkLayerDevice) node);

			mICMPprotocol = new ICMP(this);

		} else if (inProtocolStackLayers == 7) {// create a layer 7 protocol
			// stack

			mRARPprotocol = new RARP();

			mARPprotocol = new ARP(this);

			mIPprotocol = new IpV4((NetworkLayerDevice) node); // Currnetly only
			// using IPv4
			// protocol

			mICMPprotocol = new ICMP(this);

			mSL = new socketLayer(this);

			mUDPprotocol = new Udp(this, mSL); // gift (sourceforge.net user)
			// 17 Nov 2005

			mTCPprotocol = new Tcp(this, mSL); // gift (sourceforge.net user)
			// 25 Nov 2005

		}
		mOSPFProtocol = new OSPF(this, Simulation.UIDGen++);
		mParentNode = node;
	}

	public String getHostName() {
		return mParentNode.getName();
	}

	public void initNAT() {
		mTCPprotocol = new Tcp(this, mSL); // gift (sourceforge.net user) 25
		// Nov 2005
		mSL = new socketLayer(this);
		mUDPprotocol = new Udp(this, mSL); // gift (sourceforge.net user) 17
		// Nov 2005
	}

	/**
	 * 
	 * This is the generic sendPacket() method used for any outgoing packet
	 * 
	 * for this network layer device. That a packet must already have a
	 * 
	 * destination IPAddress when it is passed to this method.
	 * 
	 * @author angela_brown
	 * 
	 * @author bevan_calliess
	 * 
	 * @author rober_hulford
	 * 
	 * @author luke_hamilton
	 * 
	 * @param inPacket
	 *            - IP Packet
	 * 
	 * @throws CommunicationException
	 * 
	 * @version v0.20
	 * 
	 */

	public String router(String inDestIPAddress) {
		try {
			String[] outIface = mIPprotocol.router(inDestIPAddress);

			return outIface[0];
		} catch (CommunicationException e) {
			return "";
		}
	}

	public void sendPacket(IP_packet inPacket) throws LowLinkException, CommunicationException {
		String destMAC = null;
		String GatewayAddress = null;
		String outInterface[] = new String[2];

		if (!IPV4Address.validateDecIP(inPacket.mDestIPAddress)) {
			throw new CommunicationException("Packet dropped host unreachable: " + inPacket.mDestIPAddress);
		}

		try {
			IPV4Address p_ip = new IPV4Address(inPacket.mDestIPAddress);
			if (p_ip.isBroadcast()) {
				NetworkLayerDevice temp = (NetworkLayerDevice) mParentNode;
				ArrayList nics = mParentNode.getAllInterfacesNames();

				for (int i = 0; i < nics.size(); i++) {
					if (temp.isActiveInterface((String) nics.get(i))) {
						LayerInfo routeInfo = new LayerInfo(getClass().getName());
						routeInfo.setObjectName(getParentNodeName());
						routeInfo.setDataType(trimClassName(inPacket.getClass().getName()));
						routeInfo.setLayer("Network");
						routeInfo
								.setDescription("Packet Received: Network Layer Device is sending broadcast packet through interface "
										+ (String) nics.get(i) + ".");
						Simulation.addLayerInfo(routeInfo);
						inPacket.mSourceIPAddress = mIPprotocol.getIPAddress((String) nics.get(i));
						temp.sendPacket("FF:FF:FF:FF:FF:FF", inPacket, (String) nics.get(i));
					}
				}

				return;
			}

			outInterface = mIPprotocol.router(inPacket.getDestIPAddress());
			try {
				if (outInterface[0] == null) {
					try {
						// Create layer info
						LayerInfo protocolInfo = new LayerInfo(getClass().getName());
						protocolInfo.setObjectName(getParentNodeName());
						if (!(inPacket instanceof ARP_packet) && !(inPacket instanceof ICMP_packet))
							protocolInfo.setDataType("IP_Packet");
						else
							protocolInfo.setDataType(trimClassName(inPacket.getClass().getName()));

						protocolInfo.setLayer("Network");
						protocolInfo
								.setDescription("No network interface subnet masks match default gateway provided. "
										+ " Unabled to send");
						Simulation.addLayerInfo(protocolInfo);
					} catch (NullPointerException e) {
						System.out.println("ProtocolStack.java: SendPacket 1 " + e.toString());
					}
				} else {
					try {
						String sourceIP = mIPprotocol.getIPAddress(outInterface[0]);

						if (!(inPacket instanceof ARP_packet)) //
							inPacket.setSourceIPAddress(sourceIP);
						// test if on local of remote network
						if (mParentNode.getIntType(outInterface[0]) != NetworkInterface.Serial) {
							if (outInterface[1] == null) {
								destMAC = mARPprotocol.getMACAddress(inPacket.getDestIPAddress(), outInterface[0]);
							} else {

								// String GatewayAddress =
								// mIPprotocol.getDefaultGateway();
								GatewayAddress = mIPprotocol.getGateway(inPacket.getDestIPAddress());
								String outIface = mIPprotocol.getInterface(inPacket.getDestIPAddress());
								if (outIface != null)
									outInterface[0] = outIface;
								if (GatewayAddress.contains("*"))
									GatewayAddress = inPacket.getDestIPAddress();
								destMAC = mARPprotocol.getMACAddress(GatewayAddress, outInterface[0]);
							}

							if (destMAC == null) { // if there is no matching
								// entry in arp throw an
								// error
								throw new CommunicationException("Unable to Resolve Destination MAC Address");
							}
						}
						// Create layer info
						LayerInfo protocolInfo = new LayerInfo(getClass().getName());
						protocolInfo.setObjectName(getParentNodeName());

						if (!(inPacket instanceof ARP_packet) && !(inPacket instanceof ICMP_packet))
							protocolInfo.setDataType("IP_Packet");
						else
							protocolInfo.setDataType(trimClassName(inPacket.getClass().getName()));
						protocolInfo.setLayer("Network");

						if (GatewayAddress == null)
							GatewayAddress = inPacket.getDestIPAddress();

						protocolInfo.setDescription("Sending packet from ProtocolStack (to " + GatewayAddress + ").");
						Simulation.addLayerInfo(protocolInfo);
					} catch (NullPointerException e) {
						System.out.println("ProtocolStack.java: SendPacket 1 " + e.toString());
						e.printStackTrace();
					}

					try {
						try {
							NetworkLayerDevice temp = (NetworkLayerDevice) mParentNode;

							if (mParentNode.getIntType(outInterface[0]) != NetworkInterface.Serial) {
								temp.sendPacket(destMAC, inPacket, outInterface[0]);
							} else {
								temp.sendPacket(inPacket, outInterface[0]);
							}

							if (inPacket instanceof ARP_packet) {
								packetARPCounter++;
							} else {
								packetoutputIPCounter++;
							}
						} catch (NullPointerException e) {
							System.out.println("ProtocolStack.java: SendPacket NLD " + e.toString());
						}

					} catch (InvalidNetworkInterfaceNameException ex) {

						throw new CommunicationException("The Interface " + outInterface
								+ "was unable to send the Packet.");

					} catch (NullPointerException e) {
						System.out.println("ProtocolStack.java: SendPacket 11! " + e.toString());
					}

				}

			} catch (NullPointerException e) {
				System.out.println("ProtocolStack.java: SendPacket 1!!! " + e.toString());
			}

		} catch (LowLinkException ex) {
			throw new LowLinkException(ex.toString());
		} catch (CommunicationException ex) {
			throw ex;
		} catch (InvalidNetworkInterfaceNameException e) { // UGLY!!! FIXME!!!
			System.out.println(e.toString());
		} catch (InvalidIPAddressException e) { // UGLY!!! FIXME!!!
			System.out.println(e.toString());
			// }catch(NullPointerException e){
			// System.out.println("::" + inPacket + "::");
			// System.out.println("ProtocolStack.java: SendPacket " +
			// e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Forwards the packet provided to the destination IPAddress
	 * 
	 * if it's known or the default gateway otherwise.
	 * 
	 * Is used by receivePacket method, if the network device is routable
	 * 
	 * @author angela_brown
	 * 
	 * @author robert_hulford
	 * 
	 * @author bevan_calliess
	 * 
	 * @param inPacket
	 *            - IP Packet
	 * 
	 * @throws CommunicationException
	 * 
	 * @version v0.20
	 * 
	 */

	public void forwardPacket(IP_packet inPacket) throws CommunicationException, LowLinkException {

		String destMAC = null;
		String outInterface[] = { null, null };

		if (!(inPacket instanceof ARP_packet)) {

			try {
				outInterface = mIPprotocol.router(inPacket.getDestIPAddress());
			} catch (CommunicationException e) {
				ICMP_packet pingPacket = new ICMP_packet(inPacket.getSourceIPAddress());
				pingPacket.setMessageCode(ICMP_packet.DESTINATION_UNREACHABLE);

				Simulation.addLayerInfo(getClass().getName(), getParentNodeName(), "ICMP Dst Unreach", "Network",
						"Sending ICMP Destination Unreacheable to " + inPacket.getSourceIPAddress());

				sendPacket(pingPacket);

				return;
			}

			String GatewayAddress = null;

			// test if on local of remote network
			try {

				if (mParentNode.getIntType(outInterface[0]) != NetworkInterface.Serial) {
					if (outInterface[1] == null) {

						destMAC = mARPprotocol.getMACAddress(inPacket.getDestIPAddress(), outInterface[0]);

					} else {
						try {
							GatewayAddress = mIPprotocol.getGateway(inPacket.getDestIPAddress());

							String outIface = mIPprotocol.getInterface(inPacket.getDestIPAddress());

							if (outIface != null)
								outInterface[0] = outIface;

							if (GatewayAddress.contains("*"))
								GatewayAddress = inPacket.getDestIPAddress();

						} catch (Exception e) {
						} // UGLY!!! FIXME!!!

						destMAC = mARPprotocol.getMACAddress(GatewayAddress, outInterface[0]);

						// String GatewayAddress =
						// mIPprotocol.getDefaultGateway();
						// destMAC =
						// mARPprotocol.getMACAddress(GatewayAddress,outInterface[0]);

					}

					if (destMAC == null) { // if there is no matching entry in
						// arp throw an error

						throw new CommunicationException("Unable to Resolve Destination MAC Address");

					}
				}

				// Create layer info

				LayerInfo protocolInfo = new LayerInfo(getClass().getName());

				protocolInfo.setObjectName(getParentNodeName());

				if (!(inPacket instanceof ARP_packet) && !(inPacket instanceof ICMP_packet))
					protocolInfo.setDataType("IP_Packet");
				else
					protocolInfo.setDataType(trimClassName(inPacket.getClass().getName()));

				protocolInfo.setLayer("Network");

				if (GatewayAddress == null)
					GatewayAddress = inPacket.getDestIPAddress();

				protocolInfo.setDescription("Forwarding packet from ProtocolStack(to " + GatewayAddress + ").");

				Simulation.addLayerInfo(protocolInfo);

				try {

					NetworkLayerDevice temp = (NetworkLayerDevice) mParentNode;

					if (mParentNode.getIntType(outInterface[0]) != NetworkInterface.Serial) {
						temp.sendPacket(destMAC, inPacket, outInterface[0]);
					} else {
						temp.sendPacket(inPacket, outInterface[0]);
					}

					if (inPacket instanceof ARP_packet) {
						packetARPCounter++;
					} else {
						packetoutputIPCounter++;
					}

				} catch (InvalidNetworkInterfaceNameException ex) {

					throw new CommunicationException("The Interface " + outInterface
							+ " was unable to send the Packet.");
				}
			} catch (InvalidNetworkInterfaceNameException e) {
			}

		}
	}

	/**
	 * This method forward the packet provided out the interface name provided
	 * using the broadcast macAddress so that all network layer devices on the
	 * same network will receive it. it is used during the ARP descovery
	 * proccess to determine the IPAddress of an unknown host.
	 */

	public void broadcastPacket(IP_packet inPacket, String interfaceName) throws CommunicationException,
			LowLinkException {

		String destMAC = "FF:FF:FF:FF:FF:FF";

		String sourceIP = mIPprotocol.getIPAddress(interfaceName);

		inPacket.setSourceIPAddress(sourceIP);

		// Create layer info

		LayerInfo protocolInfo = new LayerInfo(getClass().getName());

		protocolInfo.setObjectName(getParentNodeName());

		if (!(inPacket instanceof ARP_packet) && !(inPacket instanceof ICMP_packet))
			protocolInfo.setDataType("IP_Packet");
		else
			protocolInfo.setDataType(trimClassName(inPacket.getClass().getName()));

		protocolInfo.setLayer("Network");

		protocolInfo.setDescription("Sending broadcast packet from ProtocolStack.");

		Simulation.addLayerInfo(protocolInfo);

		try {

			NetworkLayerDevice temp = (NetworkLayerDevice) mParentNode;

			temp.sendPacket(destMAC, inPacket, interfaceName);

		} catch (InvalidNetworkInterfaceNameException ex) {

			throw new CommunicationException("The Interface " + interfaceName + " was unable to send the Packet.");

		}

	}

	/**
	 * 
	 * Sets the isRoutable flag for this protocol stack.
	 * 
	 * This is used during the receive packet proccess to determine if a packet
	 * should
	 * 
	 * be forwarded or dropped.
	 * 
	 * @author bevan_calliess
	 * 
	 * @author angela_brown
	 * 
	 * @param inRoutable
	 *            - boolean
	 * 
	 * @version v0.20
	 * 
	 */

	public void setRoutable(boolean inRoutable) {
		mIPprotocol.setIsRoutable(inRoutable);
	}

	/**
	 * 
	 * Gets the subnet mask of the interface provided
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @param inInterface
	 *            - Inteface of node eg: eth0
	 * 
	 * @return String - SubnetMask of Interface
	 * 
	 * @version v0.20
	 * 
	 */

	public String getSubnetMask(String inInterface) {
		return mIPprotocol.getSubnetMask(inInterface);
	}

	/**
	 * 
	 * Sends a ping packet to the IPAddress provided
	 * 
	 * @author robert_hulford
	 * 
	 * @author bevan_calliess
	 * 
	 * @author angela_brown
	 * 
	 * @param inDestIPAddress
	 *            - Destination IP address eg: 192.168.0.2
	 * 
	 * @throws CommunicationException
	 * 
	 * @return ICMP packet
	 * 
	 * @version v0.20
	 * 
	 */

	public ICMP_packet sendPing(String inDestIPAddress) throws CommunicationException, LowLinkException {
		ICMP_packet pingPacket = null;
		String dest = null;

		if (!IPV4Address.isValidIp(inDestIPAddress)) {
			if (mParentNode instanceof core.ApplicationLayerDevice) {
				Vector<String> addrs = ((ApplicationLayerDevice) mParentNode).resolve(inDestIPAddress);
				if (addrs.size() > 0) {
					dest = addrs.get(0);
				}
			}
		} else {
			dest = inDestIPAddress;
		}

		if (dest!=null && dest != "") {
			pingPacket = mICMPprotocol.sendPing(dest);
			sendPacket(pingPacket);
		} else
			throw new CommunicationException("Packet dropped host unreachable " + dest);
		return pingPacket;
	}

	/**
	 * This method returns received ICMP packet with messageID.
	 * 
	 * @author QweR
	 * @param messageID
	 *            - ICMP packet message ID
	 * @return received ICMP packet or null if packet was not received
	 * @version v0.21
	 */
	public ICMP_packet getReceivedICMPPacket(int messageID) {
		return mICMPprotocol.getReceivedICMPPacket(messageID);
	}

	/**
	 * This method remove and returns received ICMP packet with messageID.
	 * 
	 * @author QweR
	 * @param messageID
	 *            - ICMP packet message ID
	 * @return received ICMP packet or null if packet was not received
	 * @version v0.21
	 */
	public ICMP_packet removeReceivedICMPPacket(int messageID) {
		return mICMPprotocol.removeReceivedICMPPacket(messageID);
	}

	/**
	 * 
	 * gets a string array from the arp protocol and returns it
	 * 
	 * @author bevna_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @return Vector<Vector<String>> - The arp table entries
	 * 
	 * @version v0.20
	 * 
	 */

	public Vector<Vector<String>> getARPTable() {

		return mARPprotocol.getARPTable();

	}

	/**
	 * Sets the custom SubnetMask for the interface provided.
	 */

	public void setCustomSubnetMask(String inInterface, String inCustomSubnetMask) throws InvalidSubnetMaskException,
			InvalidNetworkInterfaceNameException

	{
		try {
			IpV4 IPProtocol = mIPprotocol;
			IPProtocol.setCustomSubnetMask(inInterface, inCustomSubnetMask);
		} catch (InvalidNetworkInterfaceNameException e) {
			throw e;
		}
	}

	/**
	 * Takes in an IPAddress and sets it to the default gateway
	 */

	public void setDefaultGateway(String inGatewayIPAddress) throws InvalidDefaultGatewayException {
		mIPprotocol.setDefaultGateway(inGatewayIPAddress);
	}

	/**
	 * This method will pass information to the IPv4 protocol, so it can track
	 * IP address and network interfaces.
	 */

	public void setIPAddress(String inInterfaceKey, String inIPAddress) throws InvalidIPAddressException {
		mIPprotocol.setIPAddress(inInterfaceKey, inIPAddress);
	}

	/**
	 * This method is to receive a packet from the node
	 */

	@Override
	public void receivePacket(Packet inPacket, String inInterface) throws LowLinkException {

		try {
			if (inPacket instanceof ARP_packet) {
				packetARPCounter++;
			} else {
				packetinputIPCounter++;
			}

			IP_packet ipPacket = (IP_packet) inPacket;

			String destIPAddress = ipPacket.getDestIPAddress();

			ipPacket.incrementHopCount();

			// Create layer info

			LayerInfo protocolInfo = new LayerInfo(getClass().getName());
			protocolInfo.setObjectName(getParentNodeName());
			if (!(inPacket instanceof ARP_packet) && !(inPacket instanceof ICMP_packet))
				protocolInfo.setDataType("IP_Packet");
			else
				protocolInfo.setDataType(trimClassName(inPacket.getClass().getName()));

			protocolInfo.setLayer("Network");
			protocolInfo.setDescription("ProtocolStack received packet from local Interface.");
			Simulation.addLayerInfo(protocolInfo);

			// check if the Dest IP Address is for this Network device
			// if it is then pass it to the appropriate protocol
			IPV4Address ip = new IPV4Address(destIPAddress);

			if (ipPacket instanceof OSPF_Packet) {
				if ((mIPprotocol.isInternalIP(destIPAddress) && !ipPacket.NatOutsideMark)
						|| OSPFConstants.All_SPF_ROUTERS_IP_ADRESS.equals(destIPAddress)
						|| OSPFConstants.All_DR_ROUTERS_IP_ADRESS.equals(destIPAddress)) {
					mOSPFProtocol.receivePacket((OSPF_Packet) ipPacket, inInterface);
				}
				return;
			}

			if ((mIPprotocol.isInternalIP(destIPAddress) && !ipPacket.NatOutsideMark) || ip.isBroadcast()) {
				// Create layer info
				LayerInfo protInfo = new LayerInfo(getClass().getName());
				protInfo.setObjectName(getParentNodeName());
				if (!(inPacket instanceof ARP_packet) && !(inPacket instanceof ICMP_packet))
					protInfo.setDataType("IP_Packet");
				else
					protInfo.setDataType(trimClassName(inPacket.getClass().getName()));
				protInfo.setLayer("Network");
				protInfo.setDescription("Confirmed Packet is for this Network Layer Device.");
				Simulation.addLayerInfo(protInfo);

				if (ipPacket instanceof ICMP_packet) {

					ICMP_packet temp = (ICMP_packet) inPacket;

					mICMPprotocol.receiveICMPPacket(temp);

				} else if (ipPacket instanceof ARP_packet) {

					ARP_packet temp = (ARP_packet) inPacket;

					mARPprotocol.receiveARPPacket(temp);

				} else if (ipPacket instanceof UDP_packet) {

					UDP_packet temp = (UDP_packet) inPacket;

					try {
						mUDPprotocol.receiveUDPPacket(temp);
					} catch (TransportLayerException te) {

					} catch (TransportLayerPortException tpe) {
						// here *TODO*: disconnect sender from port!!!
						LayerInfo UDP_Info = new LayerInfo(getClass().getName());
						UDP_Info.setObjectName(getParentNodeName());
						UDP_Info.setDataType("UDP Packet");
						UDP_Info.setLayer("Transport");
						UDP_Info.setDescription("UDP port packet receiving: \"" + tpe.toString() + "\".");
						Simulation.addLayerInfo(UDP_Info);
						// throw tpe;
					} catch (NullPointerException e) {
						LayerInfo UDP_Info = new LayerInfo(getClass().getName());
						UDP_Info.setObjectName(getParentNodeName());
						UDP_Info.setDataType("UDP Packet");
						UDP_Info.setLayer("Transport");
						UDP_Info.setDescription("Transport level doesn't support on this device.");
						Simulation.addLayerInfo(UDP_Info);
					}

				} else if (ipPacket instanceof TCP_packet) {

					TCP_packet temp = (TCP_packet) inPacket;
					try {
						mTCPprotocol.receiveTCPPacket(temp);
					} catch (TransportLayerException te) {
					} catch (NullPointerException e) {
						LayerInfo UDP_Info = new LayerInfo(getClass().getName());
						UDP_Info.setObjectName(getParentNodeName());
						UDP_Info.setDataType("TCP Packet");
						UDP_Info.setLayer("Transport");
						UDP_Info.setDescription("Transport level doesn't support on this device.");
						Simulation.addLayerInfo(UDP_Info);
					} catch (CommunicationException ce) {
					} catch (TransportLayerPortException tpe) {
						// here *TODO*: disconnect sender from port!!!
						LayerInfo TCP_Info = new LayerInfo(getClass().getName());
						TCP_Info.setObjectName(getParentNodeName());
						TCP_Info.setDataType("TCP Packet");
						TCP_Info.setLayer("Transport");
						TCP_Info.setDescription("TCP port packet receiving: \"" + tpe.toString() + "\".");
						Simulation.addLayerInfo(TCP_Info);
					}

				} else {
					// need to insert tests for all other packet types
					System.out.println("That packet type is not recognised");
				}

			} else if (mIPprotocol.getIsRoutable()) {
				// if I am routable then compare the Destination IP
				// against my routing table and forward the packet.
				// *UGLY* TODO FIXME -- use TTL instead of it
				if (ipPacket.getHopCount() == 255) {
					// Create layer info
					LayerInfo routeInfo = new LayerInfo(getClass().getName());
					routeInfo.setObjectName(getParentNodeName());
					routeInfo.setDataType(trimClassName(inPacket.getClass().getName()));
					routeInfo.setLayer("Network");
					routeInfo.setDescription("Packet Dropped: Hop count exceeded.\nHost " + ipPacket.getDestIPAddress()
							+ " Unreachable");
					Simulation.addLayerInfo(routeInfo);

					boolean sendUnreach = true;

					if (ipPacket instanceof ICMP_packet) {
						if (((ICMP_packet) ipPacket).getMessageCode() == ICMP_packet.TIME_EXCEEDED)
							sendUnreach = false;
					}

					if (sendUnreach) {
						ICMP_packet pingPacket = new ICMP_packet(ipPacket.getSourceIPAddress());
						pingPacket.setMessageCode(ICMP_packet.TIME_EXCEEDED);
						if (ipPacket instanceof ICMP_packet)
							pingPacket.setMessageID(((ICMP_packet) ipPacket).getMessageID());

						Simulation.addLayerInfo(getClass().getName(), getParentNodeName(), "ICMP Time Exceeded",
								"Network", "Sending ICMP Time Exceeded to " + ipPacket.getSourceIPAddress());

						try {
							sendPacket(pingPacket);
						} catch (CommunicationException e) {
						}
					}
				} else {
					try {
						if (!(ipPacket instanceof ARP_packet)) {

							// Create layer info

							LayerInfo routeInfo = new LayerInfo(getClass().getName());
							routeInfo.setObjectName(getParentNodeName());
							routeInfo.setDataType(trimClassName(inPacket.getClass().getName()));
							routeInfo.setLayer("Network");
							routeInfo
									.setDescription("Packet Received: Network Layer Device is Routable forwarding packet.");
							Simulation.addLayerInfo(routeInfo);

							forwardPacket(ipPacket);

						}

					} catch (CommunicationException ex) {
						String myName = mParentNode.getName();
						System.out.println(myName + " unable to forward packet due to the following error: "
								+ ex.toString());
					} catch (LowLinkException ex) {
						String myName = mParentNode.getName();
						System.out.println(myName + " unable to forward packet due to the following error: "
								+ ex.toString());
					}

				}

			} else {
				// isInternalIP
				// If we have a hub working all machines may record a drop of
				// the packet
				// Packet Dropped
			}

			if (ip.isBroadcast()) {

				ArrayList nics = mParentNode.getAllInterfacesNames();

				/*
				 * for(int i=0; i<nics.size(); i++){
				 * if(!inInterface.equals((String)nics.get(i)) &&
				 * temp.isActiveInterface((String)nics.get(i))){ LayerInfo
				 * routeInfo = new LayerInfo(getClass().getName());
				 * routeInfo.setObjectName(getParentNodeName());
				 * routeInfo.setDataType
				 * (trimClassName(inPacket.getClass().getName()));
				 * routeInfo.setLayer("Network");
				 * routeInfo.setDescription("Packet Received: Network Layer
				 * Device is Routable forwarding packet to interface " +
				 * (String)nics.get(i) + ".");
				 * Simulation.addLayerInfo(routeInfo);
				 * temp.sendPacket("FF:FF:FF:FF:FF:FF", inPacket,
				 * (String)nics.get(i) ); } }
				 */

			}

		} catch (InvalidIPAddressException e) {
			/*
			 * }catch(InvalidNetworkInterfaceNameException e){
			 * }catch(CommunicationException ex){
			 * 
			 * String myName = mParentNode.getName();
			 * 
			 * System.out.println(myName + " unable to forward packet due to the
			 * following error: " + ex.toString());
			 */
		} catch (LowLinkException ex) {

			String myName = mParentNode.getName();

			// System.out.println(myName + " unable to forward packet due to the
			// following error: " + ex.toString());

		}

	}

	public void recieveTCP_packet(TCP_packet temp) throws LowLinkException {
		try {
			mTCPprotocol.receiveTCPPacket(temp);
		} catch (TransportLayerException te) {
		} catch (NullPointerException e) {
			System.out.println("ProtocolStack.java: receiveTCP_packet " + e.toString());
		} catch (CommunicationException ce) {
		} catch (TransportLayerPortException tpe) {

			LayerInfo TCP_Info = new LayerInfo(getClass().getName());
			TCP_Info.setObjectName(getParentNodeName());
			TCP_Info.setDataType("TCP Packet");
			TCP_Info.setLayer("Transport");
			TCP_Info.setDescription("TCP port packet receiving: \"" + tpe.toString() + "\".");
			Simulation.addLayerInfo(TCP_Info);

		}
	}

	/**
	 * This method will return the ip address of the passed in Interface.
	 */

	public String getIPAddress(String inInterfaceKey) {
		if (mIPprotocol != null) {

			return mIPprotocol.getIPAddress(inInterfaceKey);

		}
		return null;

	}

	/**
	 * returns the default gateway currently set for the IP protocol
	 */

	public String getDefaultGateway() {
		return mIPprotocol.getDefaultGateway();
	}

	public void addToARP(String IPAddress, String MACAddress) {
		if (mIPprotocol.isLocalNetwork(IPAddress)) {
			// mARPprotocol.addToArpTable(IPAddress, MACAddress,"Static");
		}
	}

	/**
	 * 
	 * This method will add a static ARP entry to the ARP protocol
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @author Key
	 * 
	 * @param IPAddress
	 *            - IP address of node eg: 192.168.0.2
	 * 
	 * @param MACAddress
	 *            - MAC address of node eg: AB:AB:AB:AB:AB:AB
	 * 
	 * @version v0.22
	 * 
	 */

	public void addToARPStatic(String IPAddress, String MACAddress)

	{

		// if(mIPprotocol.isLocalNetwork(IPAddress)){
		mARPprotocol.addToArpTable(IPAddress, MACAddress, "Static");
		// }

	}

	public void removeARP(String IPAddress)

	{

		String iptoremove[] = new String[1];

		iptoremove[0] = IPAddress;

		mARPprotocol.removeFromArpTable(iptoremove);

	}

	/**
	 * 
	 * Takes the interface name as a parameter and will return the mac address
	 * 
	 * of that interface if it is present. otherwise it will return null.
	 * 
	 */

	public String getMACAddress(String inInterfaceName) {

		String srcMAC = null;

		try {

			srcMAC = mParentNode.getMACAddress(inInterfaceName);

		} catch (InvalidNetworkInterfaceNameException e) {

			return srcMAC;

		}

		return srcMAC;

	}

	/**
	 * 
	 * This method check the in ip address against all of its interfaces ip
	 * address'
	 * 
	 * and returns an array of interface names where the network portion of the
	 * ip address
	 * 
	 * matches.
	 * 
	 * eg - if the interface eth0 has an ip address of 192.168.0.1
	 * 
	 * and a subnet mask of 255.255.255.0
	 * 
	 * any ip address passed in that is on the same subnet eg - 192.168.0.*
	 * 
	 * would get a return of eth0.
	 * 
	 */

	public String getRouteInfo(String inIPAddress) throws CommunicationException {

		try {

			String outInterface[] = mIPprotocol.router(inIPAddress);

			return outInterface[0];

		} catch (CommunicationException ex) {

			throw ex;

		}

	}

	/**
	 * 
	 * This method will check if the IP address provided
	 * 
	 * is local to this machine. Return true if the address of one of
	 * 
	 * nodes IP addresses. Else, return false.
	 * 
	 * @author bevan_calliess
	 * 
	 * @param inTestIPAddress
	 *            - IP address of node eg: 192.168.0.2
	 * 
	 * @return boolean
	 * 
	 * @version v0.20
	 * 
	 */

	public boolean isInternalIP(String inTestIPAddress) {

		return mIPprotocol.isInternalIP(inTestIPAddress);

	}

	public boolean isBroadcastIP(String inTestIPAddress) {
		try {
			IPV4Address ip = new IPV4Address(inTestIPAddress);
			if (ip.isBroadcast()) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * 
	 * This method returns the name of the network layer device
	 * 
	 * that instantiated this protocol stack
	 * 
	 * @author luke_hamilton
	 * 
	 * @author bevan_calliess
	 * 
	 * @return string - Name of the parent network layer device
	 * 
	 * @version v0.20
	 * 
	 */

	public String getParentNodeName() {

		return mParentNode.getName();

	}

	public Node getParentNode() {

		return mParentNode;

	}

	/**
	 * 
	 * This method takes the full string of the className().getName() and
	 * 
	 * returns the last portion of the string after the last full stop.
	 * 
	 * eg. - takes this - core.protocolsuite.tcp_ip.ARP_packet and outputs -
	 * ARP_packet.
	 * 
	 */

	public String trimClassName(String inString)

	{

		int index = inString.lastIndexOf(".");

		String name = inString.substring(index + 1);

		return name;

	}

	/**
	 * 
	 * Routing tables functions... Use carefully!
	 * 
	 * Shall be documented as well.
	 * 
	 * @author Key
	 * 
	 * @version v0.21
	 * 
	 */

	public void addRoute(Route_entry r) {

		mIPprotocol.addRoute(r);

	}

	public void removeRoute(String destIP) {

		mIPprotocol.removeRoute(destIP);

	}

	public String[] getRouteTableEntries() {

		return mIPprotocol.getRouteTableEntries();

	}

	public Route_entry getRouteTableEntry(String destIP) {

		return mIPprotocol.getRouteTableEntry(destIP);

	}

	public int getinputIPCount() {
		return packetinputIPCounter;
	}

	public int getoutputIPCount() {
		return packetoutputIPCounter;
	}

	public int getARPCount() {
		return packetARPCounter;
	}

	public int getTCPinputCount() {
		return mTCPprotocol.GetReceivedSegmentsNumber();
	}

	public int getTCPoutputCount() {
		return mTCPprotocol.GetSentSegmentsNumber();
	}

	public int getTCPRDCount() {
		return mTCPprotocol.GetReceivedDuplicatesNumber();
	}

	public int getTCPSDCount() {
		return mTCPprotocol.GetSentDuplicatesNumber();
	}

	public int getTCPACKCount() {
		return mTCPprotocol.GetSentACKSegmentsNumber();
	}

	public int getUDPinputCount() {
		return mUDPprotocol.GetReceivedDatagrammsNumber();
	}

	public int getUDPoutputCount() {
		return mUDPprotocol.GetSentDatagrammsNumber();
	}

	public void resetCounters() {
		packetinputIPCounter = 0;
		packetoutputIPCounter = 0;
		packetARPCounter = 0;
		if (mUDPprotocol != null)
			mUDPprotocol.ResetCounters();
		if (mTCPprotocol != null)
			mTCPprotocol.ResetCounters();
	}

	public Udp UDP() {
		return mUDPprotocol;
	}

	public Tcp TCP() {
		return mTCPprotocol;
	}

	public socketLayer SL() {
		return mSL;
	}

	public OSPF OSPF() {
		return mOSPFProtocol;
	}

	public String getSrcIP() {
		Node temp = mParentNode;
		ArrayList nics = temp.getAllInterfacesNames();
		String IP = "";
		String iface = "";

		for (int i = 0; i < nics.size(); i++) {
			try {
				iface = (String) (nics.get(i));
				if (temp.isActiveInterface(iface)) {
					IP = getIPAddress((String) nics.get(i));
					if (IP != null) {
						return IP;
					}
				}
			} catch (InvalidNetworkInterfaceNameException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@Override
	public void intUP(String iface) {

	}

}// EOF


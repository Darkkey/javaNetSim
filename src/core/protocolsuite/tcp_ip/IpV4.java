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
import java.util.Enumeration;
import java.util.Hashtable;

import core.CommunicationException;
import core.InvalidDefaultGatewayException;
import core.InvalidNetworkInterfaceNameException;
import core.NetworkInterface;
import core.NetworkLayerDevice;

/**
 * 
 * This IpV4 class extends from the IP class
 * 
 * @author angela_brown
 * 
 * @author michael_reith
 * 
 * @author robert_hulford
 * 
 * @author bevan_calliess
 * 
 * @author luke_hamilton
 * 
 * @since Sep 17, 2004
 * 
 * @version v0.20
 * 
 */

public class IpV4 extends Ip implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3659576260038353324L;

	private String defaultGateway;

	private Hashtable ipAddress; // hashtable of networkinterface as the key
									// and ipv4address object as the data.

	private boolean isRoutable = false;

	private Hashtable routingTable; // route table

	private NetworkLayerDevice parentNode;

	/**
	 * 
	 * Creates a new IPaddress Hashtable
	 * 
	 * @author luke_hamilton
	 * 
	 * @version v0.20
	 * 
	 */

	public IpV4(NetworkLayerDevice inparentNode) {

		parentNode = inparentNode;

		ipAddress = new Hashtable();

		routingTable = new Hashtable();

	}

	/**
	 * 
	 * Routing tables functions... Use carefully!
	 * 
	 * @author Key
	 * 
	 * @version v0.20
	 * 
	 */

	public void addRoute(Route_entry r) {

		routingTable.put(r.destIP, r);

	}

	public void removeRoute(String destIP) {

		routingTable.remove(destIP);

	}

	public String[] getRouteTableEntries() {

		String output[] = new String[routingTable.size() + 1];

		Enumeration keys1 = routingTable.keys();

		int i = 0;

		while (keys1.hasMoreElements())

			output[i++] = (String) keys1.nextElement();

		return output;

	}

	public Route_entry getRouteTableEntry(String destIP) {

		return (Route_entry) routingTable.get(destIP);

	}

	/**
	 * 
	 * A boolean is passed in assigning it to the "isRoutable" variable.
	 * 
	 * If true, it is routable. If false, not routable.
	 * 
	 * @author angela_brown
	 * 
	 * @author bevan_calliess
	 * 
	 * @param inRoutable
	 * 
	 * @version v0.20
	 * 
	 */

	public void setIsRoutable(boolean inRoutable)

	{

		isRoutable = inRoutable;

	}

	/**
	 * 
	 * Gets the boolean that tells if it routable.
	 * 
	 * @author angela_brown
	 * 
	 * @author bevan_calliess
	 * 
	 * @return isRoutable - boolean
	 * 
	 * @version v0.20
	 * 
	 */

	public boolean getIsRoutable()

	{

		return isRoutable;

	}

	/**
	 * 
	 * Route passes in a Destination IP address. If Destination IP address
	 * 
	 * is found in the Hashtable return true, else return false
	 * 
	 * @author bevan_calliess
	 * 
	 * @author angela_brown
	 * 
	 * @param inDestIPAddress
	 * 
	 * @return boolean
	 * 
	 * @version v0.20
	 * 
	 */

	public boolean route(String inDestIPAddress) {

		Enumeration keys = ipAddress.keys();

		while (keys.hasMoreElements()) {

			String str = (String) keys.nextElement();

			IPV4Address temp = (IPV4Address) ipAddress.get(str);

			if (temp.compareToSubnet(inDestIPAddress)) {

				return true;

			}

			return false;

		}

		return false;

	}

	/**
	 * 
	 * Passes in a Destination IP address that is checked it see if it is the
	 * 
	 * Hashtable. If it is return Key of IPaddress within the Hashtable.
	 * 
	 * Else, find and return the default gateway.
	 * 
	 * @author angela_brown
	 * 
	 * @author bevan_calliess
	 * 
	 * @param inDestIPAddress
	 * 
	 * @return FLAG[] - string of null, key and/or defaultgateway
	 * 
	 * @throws CommunicationException
	 * 
	 * @version v0.20
	 * 
	 */

	public String[] router(String inDestIPAddress)
			throws CommunicationException {

		String FLAG[] = { null, null };

		try {

			IPV4Address address = new IPV4Address(inDestIPAddress);

			Enumeration keys = ipAddress.keys();

			while (keys.hasMoreElements()) {

				String str = (String) keys.nextElement();
				IPV4Address temp = (IPV4Address) ipAddress.get(str);

				if ((temp.compareToSubnet(inDestIPAddress) && parentNode
						.getNIC(str).isUP())
						|| address.isBroadcast())
					FLAG[0] = str;
			}

			if (FLAG[0] == null) {
				try {
					FLAG[0] = getInterface(inDestIPAddress);
					FLAG[1] = "GW";

				} catch (InvalidIPAddressException e) {

					System.out
							.println("Unrecoverable exception in IPV4.getInterface: "
									+ e.toString());
					e.printStackTrace();

				}

			}

			// else return GW

			if (FLAG[0] == null) {

				if (defaultGateway != null) {

					String strDW = IPV4Address.toDecimalString(defaultGateway);

					Enumeration keys1 = ipAddress.keys();

					while (keys1.hasMoreElements()) {

						String str = (String) keys1.nextElement();

						IPV4Address temp = (IPV4Address) ipAddress.get(str);

						if (temp.compareToSubnet(strDW)) {

							FLAG[0] = str;

							FLAG[1] = "GW";

						}

					}

				} else {

					throw new CommunicationException(
							"No Default gateway Set: Unable to resolve IP address.");

				}

			}

		} catch (NullPointerException e) {

			System.out.println("IPV4.java: route 1 " + e.toString());

		} catch (InvalidIPAddressException e) {

			System.out.println("IPV4.java: route 1 " + e.toString());

		} catch (InvalidNetworkInterfaceNameException e) {

			System.out.println("IPV4.java: route 1 " + e.toString());

		}

		return FLAG;

	}

	/**
	 * 
	 * Passes in IPAddress and returns false always.
	 * 
	 * @author bevan_calliess
	 * 
	 * @author angela_brown
	 * 
	 * @param inIPAddress -
	 *            IP address eg: 192.168.0.2
	 * 
	 * @return false
	 * 
	 * @version v0.20
	 * 
	 */

	public boolean checkIPAddress(String inIPAddress) {

		return false;

	}

	/**
	 * 
	 * Checks it see if the node is on a local network. If so,
	 * 
	 * return true, else return false.
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @param inIPAddress
	 * 
	 * @return boolean
	 * 
	 * @version v0.20
	 * 
	 */

	public boolean isLocalNetwork(String inIPAddress) {

		Enumeration keys = ipAddress.keys();

		while (keys.hasMoreElements()) {

			String str = (String) keys.nextElement();

			IPV4Address temp = (IPV4Address) ipAddress.get(str);

			if (temp.compareToSubnet(inIPAddress)) {

				return true;

			}

		}

		return false;

	}

	/**
	 * 
	 * Get the route Interface.
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @param inIPAddress -
	 *            IP address of node eg: 192.0.168.1
	 * 
	 * @return "eth0" - String Interface
	 * 
	 * @version v0.20
	 * 
	 */

	public String getRouteInteface(String inIPAddress) {

		return "eth0";

	}

	/**
	 * 
	 * Gets the subnetMask. If found return the SubnetMask.
	 * 
	 * If not found return string "0.0.0.0"
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @param inInterface
	 * 
	 * @return String - SubnetMask or "0.0.0.0"
	 * 
	 * @version v0.20
	 * 
	 */

	public String getSubnetMask(String inInterface) {

		try {

			IPV4Address temp = (IPV4Address) ipAddress.get(inInterface);

			return temp.getSubnetMask();

		} catch (NullPointerException e) {

			return "0.0.0.0";

		}

	}

	/**
	 * 
	 * Sets the CustomeSubnetMask. Checks to see if the Interface is contained
	 * within the IPaddress Hashtable
	 * 
	 * if so typecast it to a IPV4 call the setCustomSubnetMask method. Else,
	 * throw an excception.
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @param inInterface -
	 *            Interface eg: eth0
	 * 
	 * @param inCustomSubnetMask -
	 *            CustomeSubnetMask eg: 255.255.255.223
	 * 
	 * @throws InvalidSubnetMaskException
	 * 
	 * @throws InvalidNetworkInterfaceNameException
	 * 
	 * @version v0.20
	 * 
	 */

	public void setCustomSubnetMask(String inInterface,
			String inCustomSubnetMask) throws InvalidSubnetMaskException,
			InvalidNetworkInterfaceNameException {

		if (ipAddress.containsKey(inInterface))

		{

			IPV4Address temp = (IPV4Address) ipAddress.get(inInterface);

			temp.setCustomSubnetMask(inCustomSubnetMask);

		}

		else

		{

			throw new InvalidNetworkInterfaceNameException(
					"Invalid Interface Name.");

		}

	}

	/**
	 * 
	 * Sets the default gateway. Convert the GatewayIPAddress to Binary and
	 * assign it.
	 * 
	 * @author michael_reith
	 * 
	 * @author angela_brown
	 * 
	 * @throws InvalidDefaultGatewayException
	 * 
	 * @version 0.20
	 * 
	 */

	@Override
	public void setDefaultGateway(String inGatewayIPAddress)
			throws InvalidDefaultGatewayException {

		if (inGatewayIPAddress == null) {

			removeRoute("default");

		} else {

			try {

				defaultGateway = IPV4Address.toBinaryString(inGatewayIPAddress);

				Route_entry r = new Route_entry();

				r.destIP = "default";

				r.gateway = inGatewayIPAddress;

				r.genMask = "0.0.0.0";

				r.iFace = "eth0";

				r.Type = 0;

				addRoute(r);

			} catch (InvalidIPAddressException e) {

				throw new InvalidDefaultGatewayException(
						"Invaild Default Gateway Address.");

			}

		}

	}

	/**
	 * 
	 * Return the route gateway for this packet...
	 * 
	 * @param Destination
	 *            IP
	 * 
	 * @author Key
	 * 
	 * @return gateway IP
	 * 
	 * @throws InvalidDefaultGatewayException
	 * 
	 * @version 0.20
	 * 
	 */

	public String getGateway(String inIPAddress)
			throws InvalidIPAddressException {

		String binIP = IPV4Address.toBinaryString(inIPAddress);

		String binMask;

		String destNetwork;

		String curdestNetwork;

		Route_entry r;

		Enumeration keys1 = routingTable.keys();

		while (keys1.hasMoreElements()) {

			curdestNetwork = (String) keys1.nextElement();

			r = (Route_entry) routingTable.get(curdestNetwork);

			if (r.gateway.contains("*") && curdestNetwork.contains(inIPAddress))

				return "*";

			binMask = IPV4Address.toBinaryString(r.genMask);

			destNetwork = IPV4Address.toDecimalString(IPV4Address.IPandMask(
					binIP, binMask));

			if (destNetwork.contains(curdestNetwork))
				return r.gateway;

		}

		return getDefaultGateway();

	}

	/*
	 * 
	 * Return the interface to route through for this packet...
	 * 
	 * @param Destination IP
	 * 
	 * @author Key
	 * 
	 * @return gateway IP
	 * 
	 * @throws InvalidDefaultGatewayException
	 * 
	 * @version 0.20
	 * 
	 */

	public String getInterface(String inIPAddress)
			throws InvalidIPAddressException {

		String binIP = IPV4Address.toBinaryString(inIPAddress);

		String binMask;

		String destNetwork;

		String curdestNetwork;

		Route_entry r;

		// many vars :)

		Enumeration keys1 = routingTable.keys();

		while (keys1.hasMoreElements()) {

			curdestNetwork = (String) keys1.nextElement();

			r = (Route_entry) routingTable.get(curdestNetwork);

			if (r.gateway.contains("*") && curdestNetwork.contains(inIPAddress))

				return r.iFace;

			binMask = IPV4Address.toBinaryString(r.genMask);

			destNetwork = IPV4Address.toDecimalString(IPV4Address.IPandMask(
					binIP, binMask));

			if (destNetwork.contains(curdestNetwork))
				return r.iFace;

		}

		return null;

	}

	/**
	 * 
	 * Gets the defaultgateway.
	 * 
	 * Converts it to Decimal and returns it.
	 * 
	 * Else return "0.0.0.0"
	 * 
	 * @author angela_brown
	 * 
	 * @author michael_reith
	 * 
	 * @version v0.20
	 * 
	 */

	@Override
	public String getDefaultGateway() {

		/*
		 * 
		 * if(defaultGateway != null){
		 * 
		 * return IPV4Address.toDecimalString(defaultGateway);
		 *  }
		 * 
		 * return null;
		 * 
		 */

		try {

			Route_entry r = getRouteTableEntry("default");

			return r.gateway;

		} catch (Exception e) {

			return null;

		}

	}

	/**
	 * 
	 * This method will create an IPV4Address object and add it to
	 * 
	 * the hashtable of ipaddress, will the network interface address
	 * 
	 * as the key.
	 * 
	 * @author bevan_calliess
	 * 
	 * @param inInterfaceKey -
	 *            Interface eg: eth0
	 * 
	 * @param inIPAddress -
	 *            IP address of node eg: 192.168.0.2
	 * 
	 * @version v0.20
	 * 
	 */

	@Override
	public void setIPAddress(String inInterfaceKey, String inIPAddress)
			throws InvalidIPAddressException {

		ipAddress.put(inInterfaceKey, new IPV4Address(inIPAddress));

		if (ipAddress.size() > 1)

		{

			isRoutable = true;

		}

		else

		{

			isRoutable = false;

		}

	}

	/**
	 * 
	 * Checks to see if the IP address is internal.
	 * 
	 * If so, return true. Else, return false
	 * 
	 * @author bevan_calliess
	 * 
	 * @author robert_hulford
	 * 
	 * @version v0.20
	 * 
	 */

	@Override
	public boolean isInternalIP(String inIPAddress) {

		Enumeration en = ipAddress.elements();

		while (en.hasMoreElements()) {

			IPV4Address temp = (IPV4Address) en.nextElement();

			if (temp.getDecimalIp().equals(inIPAddress)) {

				return true;

			}

		}
		
		ArrayList ifaces = parentNode.getAllInterfacesNames();
		
		for(int i = 0; i < ifaces.size(); i++){
			try{
				if(parentNode.getNetworkInterface((String)ifaces.get(i)).getNAT() == NetworkInterface.OUTSIDE_NAT)
					if(parentNode.getNAT().isOverloadIP(inIPAddress))
						return true;
			}catch(core.InvalidNetworkInterfaceNameException e){ }
		}
		

		return false;

	}

	/**
	 * 
	 * This method will return the decimal IP address of the passed in
	 * Interface.
	 * 
	 * @author luke_hamilton
	 * 
	 * @param inInterfaceKey
	 * 
	 * @return String - IP Address eg: 192.0.2.1 or null
	 * 
	 * @version v0.20
	 * 
	 */

	@Override
	public String getIPAddress(String inInterfaceKey) {

		if (ipAddress.containsKey(inInterfaceKey)) {

			IPV4Address temp = (IPV4Address) ipAddress.get(inInterfaceKey);

			return temp.getDecimalIp();

		}

		return null;

	}

}// EOF


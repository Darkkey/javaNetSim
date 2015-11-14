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

/**
 * This class set and gets the Interface, IPaddress, MACAddress and SubnetMask
 * of a node by passing it in and assigning the variables accordingly
 * @author luke_hamilton
 * @author beven_Calliess
 * @version v0.20
 */
public class ipconfigTableEntry {
	
	private String InterfaceKey;
	private String IpAddress;
	private String MACAddress;
	private String SubnetMask;
	
	ipconfigTableEntry(String inInterface, String inIPAddress, String inMACAddress, String inSubnetMask){
		InterfaceKey = inInterface;
		IpAddress = inIPAddress;
		MACAddress = inMACAddress;
		SubnetMask = inSubnetMask;			 
	}
	
	/**
	 * Sets the Interface of the node
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @param inInterface - The Interface eg: eth0
	 * @version v0.20
	 */
	public void setInterface(String inInterface){
		InterfaceKey = inInterface;
	}
	
	/**
	 * Gets the Interface Key of the node
	 * @author bevan_calliess
	 * @author luke_hamilton
	 * @return InterfaceKey - The Interface eg: eth0
	 * @version v0.20
	 */
	public String getInterfaceKey(){
		return InterfaceKey;
	}
	/**
	 * Set the IP Address of the node
	 * @author bevan_calliess
	 * @param inIPAddress - The IP Address of the node eg: 192.168.0.2
	 * @version v0.20
	 */
	public void setIPAddress(String inIPAddress){
		IpAddress = inIPAddress;
	}
	
	/**
	 * Get the IP address of the node
	 * @author bevan_calliess
	 * @author luke_hamilton
	 * @return IPAddress - The IP Address of the node eg: 192.168.0.2
	 * @version v0.20
	 */
	public String getIPAddress(){
		return IpAddress;
	}
	/**
	 * Sets the MACAddress of the node
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @param inMAC - MAC Address of the node eg: AB:AB:AB:AB:AB
	 */
	public void setMACAddress(String inMAC){
		MACAddress = inMAC;
	}
	/**
	 * Get the MAC address of the node
	 * @author bevan_calliess
	 * @author luke_hamilton
	 * @return MACAddress - The nodes MAC address eg: AB:AB:AB:AB:AB
	 * @version v0.20
	 */
	public String getMACAddress(){
		return MACAddress;
	}
	/**
	 * Set the SubnetMask of the node
	 * @author bevan_calliess
	 * @author luke_hamilton
	 * @param inSubnetMask - The SubnetMask of the node
	 * @version v0.20
	 */
	public void setSubnetMask(String inSubnetMask){
		SubnetMask = inSubnetMask;
	}
	/**
	 * Gets the SubnetMask of the node
	 * @author bevan_calliess
	 * @author luke_hamilton
	 * @return SubnetMask
	 * @version v0.20
	 */
	public String getSubnetMask(){
		return SubnetMask;
	}
}//EOF

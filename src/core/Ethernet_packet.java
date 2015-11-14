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

/**
 * @author luke_hamilton
 * @since Sep 19, 2004
 * @version v0.20
 **/
public class Ethernet_packet extends Packet {
	private String sourceMACAddress;
	private String destMACAddress;	
	public int vlan_id = 1;

	/**
	 * This method is called by the networkinterface class, which then can test 
	 * to see if the MACAddress match or is a broadcast address
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @param inPacket - Packet 
	 * @param inDestMACAddress - Destination MAC address eg: A1:45:DE:78:98:A3
	 * @param inSourceMACAddress - Source MAC address eg: A2:A5:B2:D1:34:12
	 */
	public Ethernet_packet(Packet inPacket, String inDestMACAddress, String inSourceMACAddress)
	{
		destMACAddress = inDestMACAddress;
		Data = inPacket;
		sourceMACAddress = inSourceMACAddress;		
	}
	/**
	 * This method gets the Source MAC address 
	 * @author bevan_calliess
	 * @return sourceMACAddress
	 * @version v0.20
	 */
	public String getSourceMACAddress(){
		return sourceMACAddress;
	}
	/**
	 * This method will set the Destination MAC Adress
	 * @author bevan_calliess
	 * @param inDesMAC - Destination MAC address eg: A1:45:DE:78:98:A3
	 * @version v0.20
	 */
	public void setDestinationMACAddress(String inDesMAC){
		destMACAddress = inDesMAC;
	}
	
	/**
	 * This method will return the Destination MAC address
	 * @author bevan_calliess
	 * @return destMACAddress
	 * @version v0.20
	 */
	public String getDestinationMACAddress(){
		return destMACAddress;
	}
}

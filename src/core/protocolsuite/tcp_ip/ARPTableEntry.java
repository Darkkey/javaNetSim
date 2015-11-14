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
import java.util.GregorianCalendar;

/**
 * This object is intended to hold a single entry in the
 * ARP table of the ARP protocol
 * @author bevan_calliess
 * @author robert_hulford
 * @version v.20
 */
public class ARPTableEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4410133054157725843L;
	private String mIPAddress;
	private String mMACAddress;
	private String mEntryType;
	private GregorianCalendar mEntryTime;
	
	/**
	 * This assigns the IPaddress, MACaddress and EntryTime
	 * @param inIPAddress - IP Address  of node eg: 192.168.0.2
	 * @param inMACAddress - MAC Address of node eg: ab:ab:ab:ab:ab
 	 * @param inEntryType - The current date eg: 19/11/2004
 	 * @version v0.20
	 */
	
	public ARPTableEntry(String inIPAddress,String inMACAddress,String inEntryType){
		mIPAddress = inIPAddress;
		mMACAddress = inMACAddress;
		mEntryType = inEntryType;
		mEntryTime = new GregorianCalendar();
	}
	/**
	 * returns the IPAddress for this ARP Table Entry
	 * @author robert_hulford
	 * @author bevan_calliess
	 * @return IPAddress - Represents the IPAddress of this entry.
	 * @version v0.20
	 */	
	public String getIPAddress(){
		return mIPAddress;
	}
	/**
	* returns the MACAddress for this ARP Table Entry
	* @author robert_hulford
	* @author bevan_calliess
	* @return mMACAddress - Represents the MACAddress of this entry.
	* @version v0.20
	*/	
	public String getMACAddress(){
		return mMACAddress;
	}
	/**
	* returns the entry Type for this ARP Table Entry
	* @author robert_hulford
	* @author bevan_calliess
	* @return mEntryType - String eg: Static or Dynamic
	* @version v0.20
	*/	
	public String getEntryType(){
		return mEntryType;
	}

	/**
	* returns the entry Time for this ARP Table Entry
	* This is the Intial time the entry was added to the table
	* @author bevan_calliess
	* @author robert_hulford
	* @return mEntryTime - String eg: The Time the entry was added to the table
	* @version v0.20
	*/	
	public GregorianCalendar getEntryTime(){
		return mEntryTime;
	}
}//EOF

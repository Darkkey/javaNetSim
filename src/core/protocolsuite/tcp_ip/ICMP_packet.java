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
* Currently the only packets being used are echo_requests and echo_reply.
* The others are in their for future development. 
* @author luke_hamilton
* @author angela_brown
* @author robert_hulford
* @author bevan_calliess
* @version v0.20
*/
public class ICMP_packet extends IP_packet{
	private	int mMessageCode;
	private String ICMP_message;
	private int messageID;
	public static final int ECHO_REPLY = 0;
	public static final int DESTINATION_UNREACHABLE = 3;
	public static final int SOURCE_QUENCH = 4;
	public static final int REDIRECT = 5;
	public static final int ECHO_REQUEST = 8;
	public static final int ROUTER_ADVERTISEMENT = 9;
	public static final int ROUTER_SOLICITATION = 10;
	public static final int TIME_EXCEEDED = 11;
	public static final int PARAMETER_PROBLEM = 12;
	public static final int TIMESTAMP_REQUEST = 13;
	public static final int TIMESTAMP_REPLY = 14;
	public static final int INFORMATION_REQUEST = 15;
	public static final int INFORMATION_REPLY= 16;
	public static final int ADDRESSMASK_REQUEST = 17;
	public static final int ADDRESSMASK_REPLY = 18;	

/**
 * Assigns the Destination IPAddress
 * @author bevan_calliess
 * @author luke_hamilton
 * @param inDestIPAddress
 * @version v0.20
 **/	
	
public ICMP_packet(String inDestIPAddress)
{
	super (inDestIPAddress);	
}
/**
 * sets the ICMP_message
 * @author bevan_calliess
 * @param inICMP_message
 * @version v0.20
 */
public void setICMP_message(String inICMP_message)
{
	ICMP_message = inICMP_message;
}
/**
 * Return the ICMP_message
 * @author bevan_calliess
 * @return ICMP_message 
 * @version v0.20
 */

public String getICMP_message()
{
	return ICMP_message;
}
/**
 * This method will take an integer value representing the ICMP Packet
 * code that identifies the type of Packet.  EG an echo request packet is
 * type 8, Destination unreachable is 3.  You can refer to Page 133 of 
 * TCP/IP Principles, protocols & architectures VOl 1
 * and see the constants setup in this class
 * NOTE no validation of Codes is currently implemented
 * @author bevan_calliess
 * @author angela_brown
 * @param InCode - The code of the message eg: 8
 * @version v0.20
 */
public void setMessageCode(int inCode){
	mMessageCode = inCode;
}
/**
 * returns the message code for this packet
 * @author angela_brown
 * @author bevan_calliess
 * @return integer representing the message code
 * @version v0.20 
 **/
public int getMessageCode(){
	return mMessageCode;
}

@Override
public String toBytes(){
    return RawtoBytes() + IPtoBytes() + ICMPtoBytes();
}

@Override
public void fromBytes(String str){
    RawfromBytes(str);
    IPfromBytes(str);
    ICMPfromBytes(str);
}
        
public String ICMPtoBytes(){
    return "M|" + mMessageCode + "|" + ICMP_message + "|#";
}

public void ICMPfromBytes(String str){
    String icmp = str.replaceAll(".*#M\\|", "");

    System.out.println(icmp);

    String[] fields = icmp.split("\\|");

    mMessageCode = Integer.valueOf(fields[0]);
    ICMP_message = fields[1];
}

public int getMessageID(){
	return messageID;
}

public void setMessageID(int mid){
	messageID = mid;
}

}//EOF
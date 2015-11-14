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

 * This class is a representation of the Internets ARP packet. 

 * ARP packets are userd for IP address resolution. Echo resquests and echo reply

 * are currently the only ones implemented. Packets type will need to be 

 * implemented if you wish to futher this class. 

 * @author bevan_calliess

 * @author robert_hulford

 * @since Sep 17, 2004

 * @version v0.20 

 */



public class ARP_packet extends IP_packet{

	public static final int ARP_REQUEST = 1;

	public static final int ARP_REPLY = 2;

	public static final int RARP_REQUEST = 3;

	public static final int RARP_REPLY = 4;

	private String ARP_message;

	private String sourceMac;

	private int mMessageCode;

	

	/**

	 * This method passes the destination address into the super class

	 * (IP_packet)

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param inDestIPAddress

	 * @version v0.20

	 */

	public ARP_packet(String inDestIPAddress){

		super (inDestIPAddress);

	}

	

	/**

	 * This method passes sets the ARP_message

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param inARP_message

	 * @version v0.20

	 */

	public void setARP_message(String inARP_message)

	{

		ARP_message = inARP_message;

	}

	/**

	 * This method gets the ARP_message

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @version v0.20

	 */

	public String getARP_message()

	{

		return ARP_message;

	}

	/**

	 * This method sets the SourceMAC address 

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param inMac

	 * @version v0.20

	 */

	public void setSourceMAC(String inMac){

		sourceMac = inMac;

	}

	/**

	 * This method gets the SourceMAC address

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param inDestIPAddress

	 * @version v0.20

	 */

	public String getSourceMAC(){

		return sourceMac;

	}

	/**

	 * This method will take an integer value representing the ARP Packet

	 * code that identifies the type of Packet.  EG an ARP request packet is

	 * type 1, ARP reply is 2 see page 57 

	 * TCP/IP illustrated Vol1 the protocols  

	 * and see the constants setup in this class

	 * NOTE no validation of Codes is currently implemented

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param InCode - The code of the message	 

	 * @version v0.20

	 */

	public void setMessageCode(int inCode){

		mMessageCode = inCode;

	}

	/**

	 * returns the message code for this packet

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @return integer representing the message code

	 * @version v0.20

	 */

	public int getMessageCode(){

		return mMessageCode;

	}

	

}//EOF


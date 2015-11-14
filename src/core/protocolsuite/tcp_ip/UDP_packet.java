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

import core.TransportLayerException;

/**

 * This if for design and future implementation of UDP_packet

 * @author  luke_hamilton

 * @since   Sep 17, 2004
 
 * Design and implementation of UDP_packet
 * @author  gift (sourceforge.net user)
 * @since Nov 16, 2005
 * @version v0.20
 */

public class UDP_packet extends IP_packet
{
    
        /* DO NOT forget about pseudo UDP header fields
         *  - mSourceIPAddress (implemented)
         *  - mDestIPAddress (implemented)
         *  - PTCL (protocol type code) (implemented)
         *  - UDP length - not needed (not implemented)
         * that's why UDP_packet extends IP_packet */    
    
        private int UDP_MessageLength; 
        
        private String UDP_message;
        
        private int UDP_srcPort;
        
        private int UDP_destPort;
        
        private static final int PTCL = 17; //see RFC :) 
        
        private static final int CHECK_SUM = 1; //for future developing :)        
        
        private static final int HEAD_LENGTH = 8; //UDP header is 8 bytes
        
        private static final int MAX_LENGTH = 65535; //bytes is UDP datagramm maximum length


        public void setDestPort(int dp){
        	UDP_destPort = dp;
        }
        
        public void setSrcPort(int sp){
        	UDP_srcPort = sp;
        }


/**
 * This method passes the destination and source addresses into the super class
 * @author gift (sourceforge.net user)
 * @param inDestIPAddress destination IP address
 * @param inSourceIPAddress source IP address
 * @param indestPort destination port number
 * @param insrcPort sorce port number
 * @return Nothing.
 * @version v0.20
 */

public UDP_packet(String inDestIPAddress, String inSourceIPAddress, int indestPort, int insrcPort)
{
    super (inDestIPAddress);        
    this.setSourceIPAddress(inSourceIPAddress);
    UDP_destPort = indestPort;
    UDP_srcPort = insrcPort;
    UDP_MessageLength = UDP_packet.HEAD_LENGTH;
    UDP_message = "";
}

/**
 * This method sets the UDP_message and calculates UDP_message length
 * @author gift (sourceforge.net user)
 * @param inUDP_message a string to be set as a UDP message
 * @return Nothing.
 * @exception TransportLayerException If UDP message exceeds maximum size of UDP message.
 * @version v0.20
 * @see TransportLayerException
 */

public void setUDP_message(String inUDP_message) throws TransportLayerException
{
    int length;
    
    length = UDP_packet.HEAD_LENGTH + UDP_message.length();
    
    if (length <= UDP_packet.MAX_LENGTH)
    {
	UDP_message = inUDP_message;
        UDP_MessageLength = length;
        
     }else
     {
	throw new TransportLayerException("UDP Error: UDP message exceeds maximum size of " + UDP_packet.MAX_LENGTH + " bytes.");
     }
}

/**
 * This method returns the string describing UDP message
 * @author gift (sourceforge.net user)
 * @param Unused.
 * @return the UDP message
 * @version v0.20
 */

public String getUDP_message()
{
    return UDP_message;
}

/**
 * This method returns integer describing UDP message length
 * @author gift (sourceforge.net user)
 * @param Unused.
 * @return the length of UDP message.
 * @version v0.20
 */

public int getUDP_MessageLength()
{
    return UDP_MessageLength;
}


/**
 * This if for design and future implementation of UDP_packet
 * @author gift (sourceforge.net user)
 * @param Unused.
 * @return Nothing.
 * @version v0.20
 */
public void calculate_Check_Sum()
{

}

/**
 * This method gets Check_Sum
 * can be used for design and future implementation of UDP_packet
 * @author gift (sourceforge.net user)
 * @param Unused.
 * @return CHECK_SUM.
 * @version v0.20
 */
public int getCheck_Sum()
{
    return UDP_packet.CHECK_SUM;
}

/**
 * This method gets protocol code (17 for UDP)
 * @author gift (sourceforge.net user)
 * @param Unused.
 * @return protocol code PTCL.
 * @version v0.20
 */
public int getProtocolCode()
{
    return UDP_packet.PTCL;
}

/**
 * This method gets source port
 * @author gift (sourceforge.net user)
 * @param Unused.
 * @return source UDP port number.
 * @version v0.20
 */
public int get_srcPort()
{
    return UDP_srcPort;
}

/**
 * This method gets destination port
 * @author gift (sourceforge.net user)
 * @param Unused.
 * @return destination UDP port number.
 * @version v0.20
 */
public int get_destPort()
{
    return UDP_destPort;
}

@Override
public String toBytes(){
    return RawtoBytes() + IPtoBytes() + UDPtoBytes();
}

@Override
public void fromBytes(String str){
    RawfromBytes(str);
    IPfromBytes(str);
    UDPfromBytes(str);
}
        
public String UDPtoBytes(){
    return "U|" + UDP_MessageLength + "|" + UDP_message + "|" + UDP_srcPort + "|" + UDP_destPort + "|#";
}

public void UDPfromBytes(String str){
    String icmp = str.replaceAll(".*#U\\|", "");

    System.out.println(icmp);

    String[] fields = icmp.split("\\|");

    UDP_MessageLength = Integer.valueOf(fields[0]);
    UDP_message = fields[1];
    UDP_srcPort = Integer.valueOf(fields[2]);
    UDP_destPort = Integer.valueOf(fields[3]);
}


}//EOF


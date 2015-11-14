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

 * This is for design and future implementation of TCP_PACKET

 * @author luke_hamilton

 * @since Sep 17, 2004

 * Design and implementation of TCP_packet
 * @author  gift (sourceforge.net user)
 * @since Nov 25, 2005
 * @version v0.20
 */
public class TCP_packet extends IP_packet implements Comparable
{
        /* DO NOT forget about pseudo UDP header fields
         *  - mSourceIPAddress (implemented)
         *  - mDestIPAddress (implemented)
         *  - PTCL (protocol type code) (implemented)
         *  - TCP length - not needed (not implemented)
         * that's why TCP_packet as well as UDP_packet extends IP_packet */    
            
        private int TCP_srcPort;        
        private int TCP_destPort;        
        private int TCP_MessageLength;        
        private int sequence_number; //very very important field. It's value is used as an unique ID of each TCP_packet        
        private int  acknowledgment_number; //important field as well ;)        
        private boolean flags[] = {false,false,false,false,false,false}; /* URG, ACK, PSH, RST, SYN, FIN */        
        private int TCP_window;
        //private Object URG_pointer; //for future developing :)                
        private String TCP_message;        
        private static final int PTCL = 6; //see RFC :)         
        private static final int CHECK_SUM = 1; //for future developing :)        
        private static final int HEAD_LENGTH = 22; //TCP header is 22 bytes        
        private static final int MAX_LENGTH = 65535; //bytes will be maximum length


        public void setDestPort(int dp){
        	TCP_destPort = dp;
        }
        
        public void setSrcPort(int sp){
        	TCP_srcPort = sp;
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

    public TCP_packet(String inDestIPAddress, String inSourceIPAddress, int indestPort, int insrcPort){
        super (inDestIPAddress);        
        this.setSourceIPAddress(inSourceIPAddress);
        TCP_destPort = indestPort;
        TCP_srcPort = insrcPort;
        TCP_MessageLength = TCP_packet.HEAD_LENGTH;
        TCP_message = "";
    }
    
    public TCP_packet(TCP_packet pack){
        super(pack.getDestIPAddress());
        setSourceIPAddress(pack.getSourceIPAddress());
        TCP_srcPort = pack.TCP_srcPort;
        TCP_destPort = pack.TCP_destPort;
        TCP_MessageLength = pack.TCP_MessageLength;
        sequence_number = pack.sequence_number;
        acknowledgment_number = pack.acknowledgment_number;
        for(int i=0; i<flags.length; i++)
            flags[i] = pack.flags[i];
        TCP_window = pack.TCP_window;
        TCP_message = pack.TCP_message;
    }

    /**
     * This method sets the TCP_message and calculates TCP_message length
     * @author gift (sourceforge.net user)
     * @param inTCP_message a string to be set as a TCP message
     * @return Nothing.
     * @exception TransportLayerException If TCP message exceeds maximum size of TCP message.
     * @version v0.20
     * @see TransportLayerException
     */

    public void setTCP_message(String inTCP_message) throws TransportLayerException
    {
        int length;

        length = TCP_packet.HEAD_LENGTH + TCP_message.length();

        if (length <= TCP_packet.MAX_LENGTH)
        {
            TCP_message = inTCP_message;
            TCP_MessageLength = length;

         }else
         {
            throw new TransportLayerException("TCP Error: TCP message exceeds maximum size of " + TCP_packet.MAX_LENGTH + " bytes.");
         }
    }

    /**
     * This method returns the string describing TCP message
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return the TCP message
     * @version v0.20
     */

    public String getTCP_message()
    {
        return TCP_message;
    }

    /**
     * This method returns integer describing TCP message length
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return the length of TCP message.
     * @version v0.20
     */

    public int getTCP_MessageLength()
    {
        return TCP_MessageLength;
    }


    /**
     * This if for design and future implementation of TCP_packet
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
     * can be used for design and future implementation of TCP_packet
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return CHECK_SUM.
     * @version v0.20
     */
    public int getCheck_Sum()
    {
        return TCP_packet.CHECK_SUM;
    }

    /**
     * This method gets protocol code (6 for TCP)
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return protocol code PTCL.
     * @version v0.20
     */
    public int getProtocolCode()
    {
        return TCP_packet.PTCL;
    }

    /**
     * This method gets source port
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return source TCP port number.
     * @version v0.20
     */
    public int get_srcPort()
    {
        return TCP_srcPort;
    }

    /**
     * This method gets destination port
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return destination TCP port number.
     * @version v0.20
     */
    public int get_destPort()
    {
        return TCP_destPort;
    }

    /**
     * This method sets the sequence number
     * @author gift (sourceforge.net user)
     * @param inSNumb integer to be set as the sequence number
     * @return Nothing.
     * @version v0.20
     */

    public void set_sequence_number(int inSNumb)
    {
     sequence_number=inSNumb;
    }

    /**
     * This method sets the acknowledgment number
     * @author gift (sourceforge.net user)
     * @param inACK integer to be set as the acknowledgment number
     * @return Nothing.
     * @version v0.20
     */

    public void set_acknowledgment_number(int inACK)
    {
     acknowledgment_number=inACK;
    }

    /**
     * This method gets sequence number
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return TCP sequence number.
     * @version v0.20
     */
    public int get_sequence_number()
    {
        return sequence_number;
    }

    /**
     * This method gets acknowledgment number
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return TCP acknowledgment number.
     * @version v0.20
     */
    public int get_acknowledgment_number()
    {
        return acknowledgment_number;
    }

    /**
     * This method gets URG flag
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return URG boolean flag value.
     * @version v0.20
     */
    public boolean get_URG_flag()
    {
        return flags[0];
    }

    /**
     * This method gets ACK flag
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return ACK boolean flag value.
     * @version v0.20
     */
    public boolean get_ACK_flag()
    {
        return flags[1];
    }

    /**
     * This method gets PSH flag
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return PSH boolean flag value.
     * @version v0.20
     */
    public boolean get_PSH_flag()
    {
        return flags[2];
    }

    /**
     * This method gets RST flag
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return RST boolean flag value.
     * @version v0.20
     */
    public boolean get_RST_flag()
    {
        return flags[3];
    }

    /**
     * This method gets SYN flag
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return SYN boolean flag value.
     * @version v0.20
     */
    public boolean get_SYN_flag()
    {
        return flags[4];
    }

    /**
     * This method gets FIN flag
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return FIN boolean flag value.
     * @version v0.20
     */
    public boolean get_FIN_flag()
    {
        return flags[5];
    }

    /**
     * This method sets URG flag
     * @author gift (sourceforge.net user)
     * @param inflg_val boolean the value to be set for URG flag.
     * @return  Nothing.
     * @version v0.20
     */
    public void set_URG_flag(boolean inflg_val)
    {
        flags[0]=inflg_val;
    }

    /**
     * This method sets ACK flag
     * @author gift (sourceforge.net user)
     * @param inflg_val boolean the value to be set for ACK flag.
     * @return  Nothing.
     * @version v0.20
     */
    public void set_ACK_flag(boolean inflg_val)
    {
        flags[1]=inflg_val;
    }

    /**
     * This method sets PSH flag
     * @author gift (sourceforge.net user)
     * @param inflg_val boolean the value to be set for PSH flag.
     * @return  Nothing.
     * @version v0.20
     */
    public void set_PSH_flag(boolean inflg_val)
    {
        flags[2]=inflg_val;
    }

    /**
     * This method sets RST flag
     * @author gift (sourceforge.net user)
     * @param inflg_val boolean the value to be set for RST flag.
     * @return  Nothing.
     * @version v0.20
     */
    public void set_RST_flag(boolean inflg_val)
    {
        flags[3]=inflg_val;
    }

    /**
     * This method sets SYN flag
     * @author gift (sourceforge.net user)
     * @param inflg_val boolean the value to be set for SYN flag.
     * @return  Nothing.
     * @version v0.20
     */
    public void set_SYN_flag(boolean inflg_val)
    {
        flags[4]=inflg_val;
    }

    /**
     * This method sets FIN flag
     * @author gift (sourceforge.net user)
     * @param inflg_val boolean the value to be set for FIN flag.
     * @return  Nothing.
     * @version v0.20
     */
    public void set_FIN_flag(boolean inflg_val)
    {
        flags[5]=inflg_val;
    }
    
    @Override
	public Object clone(){
        return new TCP_packet(this);
    }

    public int compareTo(Object o) {
        return ( sequence_number - ((TCP_packet)o).sequence_number );
    }
    
    @Override
	public String toBytes(){
    return RawtoBytes() + IPtoBytes() + TCPtoBytes();
}

    @Override
	public void fromBytes(String str){
        RawfromBytes(str);
        IPfromBytes(str);
        TCPfromBytes(str);
    }

    public String TCPtoBytes(){
        return "T|" + TCP_MessageLength + "|" + TCP_message + "|" + TCP_srcPort + "|" + TCP_destPort + "|"
                + TCP_window + "|" + get_acknowledgment_number() + "|" + get_sequence_number() + "|" 
                + get_ACK_flag() + "|" + get_FIN_flag() + "|" + get_PSH_flag() + "|" + get_RST_flag() + "|"
                + get_SYN_flag() + "|" + get_URG_flag() + "|#";
    }

    public void TCPfromBytes(String str){
        String icmp = str.replaceAll(".*#T\\|", "");

        System.out.println(icmp);

        String[] fields = icmp.split("\\|");

        TCP_MessageLength = Integer.valueOf(fields[0]);
        TCP_message = fields[1];
        TCP_srcPort = Integer.valueOf(fields[2]);
        TCP_destPort = Integer.valueOf(fields[3]);
        TCP_window = Integer.valueOf(fields[4]);
        set_acknowledgment_number(Integer.valueOf(fields[5]));
        set_sequence_number(Integer.valueOf(fields[6]));
        set_ACK_flag(Boolean.valueOf(fields[7]));
        set_FIN_flag(Boolean.valueOf(fields[8]));
        set_PSH_flag(Boolean.valueOf(fields[9]));
        set_RST_flag(Boolean.valueOf(fields[10]));
        set_SYN_flag(Boolean.valueOf(fields[11]));
        set_URG_flag(Boolean.valueOf(fields[12]));
    }

}//EOF


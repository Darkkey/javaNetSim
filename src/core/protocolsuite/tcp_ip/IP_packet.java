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



import core.Packet;



/**

 * IP_Packet extends the packet class. It set the IP_Packet Source address

 * and the destination IP address

 * @author  luke_hamilton

 * @since	Sep 17, 2004

 * @version v0.20

 */



public class IP_packet extends Packet{

	

	String mSourceIPAddress;

	String mDestIPAddress;
	
	public boolean NatInsideMark = false;
	public boolean NatOutsideMark = false;

	

	/**

	 * Sets a IP packet

	 * @author angela_brown

	 * @author bevan_calliess

	 * @param inDestIPAddress - Destination IP address

	 * @version v0.20

	 */

	public IP_packet(String inDestIPAddress)
	{
		setDestIPAddress(inDestIPAddress);
		NatInsideMark = false;
		NatOutsideMark = false;
	}

	/**

	 * Sets the destination IP address

	 * @author angela_brown

	 * @author bevan_calliess

	 * @param inDestIPAddress - Destination IP address eg: 192.168.10.1

	 * @version v0.20

	 */

	public void setDestIPAddress(String inDestIPAddress)
	{
		mDestIPAddress = IPV4Address.sanitizeDecIP(inDestIPAddress);
	}

	/**

	 * Sets the source IP address

	 * @author angela_brown

	 * @author bevan_calliess

	 * @param inSourceIPAddress - Source IP address

	 * @version v0.20

	 */

	public void setSourceIPAddress(String inSourceIPAddress)

	{
		mSourceIPAddress = IPV4Address.sanitizeDecIP(inSourceIPAddress);
	}

	

	/**

	 * Sets the Data within the Packet

	 * @author bevan_calliess

	 * @author angela_brown

	 * @param inData

	 * @version v0.20

	 */

	public void setData(Packet inData)

	{

		Data = inData;

	}

	/**

	 * Gets the source IP Address

	 * @author angela_brown

	 * @author bevan_calliess

	 * @return mSourceIPAddress - Source IP address

	 * @version v0.20

	 */

	public String getSourceIPAddress()

	{

		return mSourceIPAddress;

	}

	/**

	 * Gets the destination IP address

	 * @author bevan_calliess

	 * @author angela_brown

	 * @return mDestIPAddress - Destination IP Address

	 * @version v0.20

	 */

	public String getDestIPAddress()

	{
                return mDestIPAddress;
	}

	

	/**

	 * Gets the Data packet

	 * @author bevan_calliess

	 * @author angela_brown

	 * @return Data - The data packet

	 * @version v0.20

	 **/        

	@Override
	public Packet getData()

	{

		return Data;

	}
        
        @Override
		public String toBytes(){
            return RawtoBytes() + IPtoBytes();
        }
        
        @Override
		public void fromBytes(String str){
            RawfromBytes(str);
            IPfromBytes(str);
        }
        
        public String IPtoBytes(){
            return "I|" + mSourceIPAddress + "|" + mDestIPAddress + "|#";
        }
        
        public void IPfromBytes(String str){
            String ip = str.replaceAll(".*#I\\|", "");
            
            System.out.println(ip);
            
            String[] fields = ip.split("\\|");
            
            mSourceIPAddress = fields[0];
            mDestIPAddress = fields[1];
        }

}//EOF


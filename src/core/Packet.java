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
 * This class is an abstract class that can get the data with the packet.
 * It also can get the hopcount to make sure that it doesn't end up in an
 * endless eg: between two routers and increments the hoop count.
 * @author  luke_hamilton 
 * @author angela_brown
 * @author bevan_calliess
 * @author robert_hulford
 * @since	Sep 17, 2004
 * @version v0.20
 */


public abstract class Packet {
	protected int UniqueIdentfier;
	protected Packet Data;
	protected int hopCount=0;
	
	public String toBytes(){
            return RawtoBytes();
        }
        
        public void fromBytes(String str){
            RawfromBytes(str);
        }
        
        public String RawtoBytes(){
            return "P|" + UniqueIdentfier + "|" + hopCount + "|#";
        }
        
        public void RawfromBytes(String str){
            String[] fields = str.split("\\|");
            
            UniqueIdentfier = Integer.valueOf(fields[1]);
            hopCount = Integer.valueOf(fields[2]);
            
            System.out.println(UniqueIdentfier + " " + hopCount);
        }
        
	/**
	 * Gets the data with the table
	 * @author angela_brown
	 * @author bevan_callies
	 * @return Data - The data within a packet
	 * @version v0.20
	 */
	public Packet getData()
	{
		return Data;
	}
	/**
	 * Gets the hop count
	 * @author bevan_calliess
	 * @author rovert_hulford 
	 * @return hopcount - The number of hops taken by a packet
	 * @version v0.20
	 */
	public int getHopCount()
	{
		return hopCount;
	}
	/**
	 * Increments the hop count by one each time it is called
	 * @author bevan_calliess
	 * @author robert_hulford
	 * @version v0.20
	 */
	public void incrementHopCount()
	{
		hopCount++;
	}

}//EOF

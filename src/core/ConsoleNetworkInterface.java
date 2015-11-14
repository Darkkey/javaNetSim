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

//import java.util.*;
/**
 * A NetworkInterface represents the physical interface between
 * a Node and a physical Link (network cable). Think of it as the NIC
 * (Network Interface Card) of a Node.  This particular Network Interface 
 * is modelled on an Ethernet Interface Card.
 *
 * <P>Different Nodes can contain different numbers of NetworkInterfaces.
 * A client PC usually only has one NetworkInterface, whereas a Router contains
 * at least two. A Hub or a Switch often has 8 or 16.</P>
 *
 * <P>In order for a Node to be able to send or receive network information (packets),
 * there must be a Link (network cable) between at least 2 NetworkInterfaces.</P>
 *
 * <P>NetworkInterfaces receive and send packets to and from DatalinkProtocols and
 * the Link object they are connected to.</P>
 *
 * @author Tristan Veness 
 * @author Bevan Calliess
 * @since 19 September 2004
 * @version v0.20
*/

public class ConsoleNetworkInterface extends NetworkInterface{
      
	public final static int STOPBIT_1 = 1;
	public final static int STOPBIT_15 = 2;
	public final static int STOPBIT_2 = 3;
	public final static int STOPBIT_DEFAULT = STOPBIT_1;

	public final static int PARITY_NONE = 1;
	public final static int PARITY_EVEN = 2;
	public final static int PARITY_ODD = 3;
	public final static int PARITY_MARK = 4;
	public final static int PARITY_SPACE = 5;
	public final static int PARITY_DEFAULT = PARITY_NONE;

	public final static int FLOWCONTROL_NONE = 1;
	public final static int FLOWCONTROL_HARDWARE = 2;
	public final static int FLOWCONTROL_SOFTWARE = 3;
	public final static int FLOWCONTROL_DEFAULT = FLOWCONTROL_NONE;
	
	public final static int SPEED_DEFAULT = 9600;
	public final static int DATABITS_DEFAULT = 8;
	
	public int speed = SPEED_DEFAULT;
	public int databits = DATABITS_DEFAULT;
	public int stopbits = STOPBIT_DEFAULT;
	public int parity = PARITY_DEFAULT;
	public int flowcontrol = FLOWCONTROL_DEFAULT;
    /**
	 * 
	 */
	private static final long serialVersionUID = 628884636032979769L;

	/**
    * Constructs a NetworkInterface object with the name inName and a 
    * reference to it's parent Node.
    * @author bevan_calliess
    * @param inName - The name to give the NetworkInterface eg: eth0
    * @param parent - The Node that the NetworkInterface is to be added to, it's parent.
    * @version v0.20
    */
    protected ConsoleNetworkInterface(long UID, String inName, Node parent) {
             super(UID, inName,parent);              
      }
    
    @Override
	public void Timer(int temp){ }

	@Override
	protected void receivePacket(Packet inPacket) throws LowLinkException {
		
	}
	
        @Override
		public int getType(){
            return NetworkInterface.Console;
        }
        

	@Override
	protected void sendPacket(Packet inPacket) throws LowLinkException {		
		
	}

        @Override
		public boolean isActive(){
            return false;
        }
   
	/**
	 * This method displays details about the current interface card
	 * @author bevan_calliess
	 * @return String 
	 * @version v0.20
	 */ 
	@Override
	protected String getDetails(){

		return "Interface: "+name + "\t\t" + "\t\t"+ getConnectLinkDetails();
	}			
}

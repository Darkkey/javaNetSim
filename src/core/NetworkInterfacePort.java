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

 * A NetworkInterfacePort represents the physical interface between

 * a DataLinkLayerDevice and a physical Link (network cable). Think of it as a port

 *

 * <P>Different DataLinkLayerDevices can contain different numbers of NetworkInterfacesPorts.

 * A Hub often has 4 or 8 and a switch has 8, 16 or 32.</P>

 *

 * <P>In order for a DataLinkLayerDevice to be able to send or receive network information (packets),

 * there must be a Link (network cable) between at least 2 NetworkInterfaces.</P>

 *

 * <P>NetworkInterfacePotrs receive and send packets to and from DatalinkProtocols and

 * the Link object they are connected to.</P>

 *

 * @author bevan_valliess

 * @since 13 Nov 2004 

 * @version v0.20

*/



public class NetworkInterfacePort extends NetworkInterface{

	  

  	/**
	 * 
	 */
	private static final long serialVersionUID = 2453643722253335811L;
	/**

  	* Constructs a NetworkInterfacePort object with the name inName

  	*  and a reference to it's parent Node.

  	* @author baven_calliess

  	* @param inName - The name to give the NetworkInterface eg: eth0

  	* @param inParent - The Node that the NetworkInterface is to be added to, it's parent.

  	* @version v0.20

  	*/
	

	public int vlan = 1;
	final static int MODE_ACCESS = 0;
	final static int MODE_TRUNK = 1;
	public int mode = NetworkInterfacePort.MODE_ACCESS;
	public String description = "";

  	protected NetworkInterfacePort(long UID, String inName, Node inParent) {

    	super(UID, inName,inParent);			 

  	}
  	
  	protected NetworkInterfacePort(long UID, String inName, Node inParent, boolean inUP) {

    	super(UID, inName,inParent);
    	
    	if(inUP)
    		setUP();

  	}


  	@Override
	public void Timer(int temp){ }
	

	/**

	 * This method recevie a packet from a connected link and then pass it

	 * to the node's protocolstack.  Depending on the network any

	 * testing of the packets headers may need to be inserted here.

	 * @author bevan_calliess

	 * @param inPacket - The packet being recived by this port

	 * @version v0.20

	 */

	@Override
	protected void receivePacket(Packet inPacket) throws LowLinkException {
        if(!parentNode.On) return;
        
        Ethernet_packet tempPacket = (Ethernet_packet)inPacket;

        boolean drop = false;
        
        if(mode == NetworkInterfacePort.MODE_ACCESS && tempPacket.vlan_id == 1 && parentNode instanceof NetworkLayerDevice)
        	tempPacket.vlan_id = vlan;
        
        if(mode == NetworkInterfacePort.MODE_ACCESS && tempPacket.vlan_id != vlan && parentNode instanceof NetworkLayerDevice)
			drop = true;
        
        if(!drop)
        	parentNode.receivePacket(tempPacket, name);		

        return;

	}

	

	/**

	 * This method sends a packet to a connected link Depending 

	 * it is used by the datalinkLayer devices to tansport

	 * packets without any validation or data checking.

	 * @author bevan_calliess

	 * @author angela_brown

	 * @param outPacket - A Packet

	 * @version v0.20

	 */

	@Override
	protected void sendPacket(Packet outPacket) throws LowLinkException{

        if(!parentNode.On) return;
        
        boolean drop = false;
		    
		Ethernet_packet tempPacket = (Ethernet_packet)outPacket;

		EthernetLink temp = (EthernetLink)connectedLink;

		if(mode == NetworkInterfacePort.MODE_ACCESS && (tempPacket.vlan_id != vlan))
			drop = true;
        
		if(temp!=null && !drop){	    			
			
			tempPacket.vlan_id = 1;

			temp.transportPacket(tempPacket,getSourceName());

		}		

	}

        @Override
		public int getType(){
            return NetworkInterface.Ethernet10T;
        }
		

	/**

	 * This method returns information about this InterfacePort

	 * @author bevan_calliess

	 * @return	String - The details of Interface name Mac and Links

	 * @version v0.20

	 */

	 @Override
	protected String getDetails(){

		return "Interface: "+name+"\t\tMAC: Not Applicable   \t\t"+ getConnectLinkDetails();

	}
	 
	 @Override
	public int getAcceptedPacketPercent() {
		return 100;
	}
	 
	 @Override
	public int getInterfaceBandwidth() {
		return 10;
	}

}//EOF


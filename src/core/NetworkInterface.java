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

 * A NetworkInterface represents the physical interface between

 * a Node and a physical Link (network cable). Think of it as the NIC

 * (Network Interface Card) of a Node.

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

 * @author tristan_veness (Original Author)

 * @author bevan_calliess

 * @author luke_hamilton

 * @since 19 September 2004

 * @version v0.20

 */



public abstract class NetworkInterface extends TimerApp{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5087638841189498070L;

	/** The NetworkInterface's name, eg "eth0" */

	protected String name;

	/** The Link that this NetworkInterface is connected to */

	protected Link connectedLink;

	protected Node parentNode;

	protected boolean up;

	protected boolean dhcp;

	protected int acl_in = 0;
	protected int acl_out = 0;

	public boolean unreachables = true;
	public boolean redirects = true;
	public boolean maskReplay = true;
	public boolean informationReplay = true;

	public boolean getDHCP(){
		return dhcp;
	}

	public void setDHCP(boolean inDHCP){
		dhcp = inDHCP;
	}


	protected String description = "";

	public void setUP(){
		up = true;
	}

	public void UP(){
		up = true;
		LayerInfo pingInfo = new LayerInfo(getClass().getName());
		pingInfo.setObjectName(parentNode.getName());
		pingInfo.setDataType("Interface");
		pingInfo.setLayer("Link");
		pingInfo.setDescription("Interface " + name + " state set to up!");
		Simulation.addLayerInfo(pingInfo);    
		if( parentNode.NodeProtocolStack != null){
			parentNode.NodeProtocolStack.intUP(name);
		}
	}

	public void DOWN(){
		up = false;
		LayerInfo pingInfo = new LayerInfo(getClass().getName());
		pingInfo.setObjectName(parentNode.getName());
		pingInfo.setDataType("Interface");
		pingInfo.setLayer("Link");
		pingInfo.setDescription("Interface " + name + " state set to down!");
		Simulation.addLayerInfo(pingInfo);
	}

	public boolean isUP(){
		return up;
	}

	public boolean isOn(){
		return parentNode.On;
	}

	public final static int Unknown = -1;
	public final static int Ethernet10T = 0;
	public final static int Console = 1;
	public final static int Wireless = 2;
	public final static int WAN = 3;
	public final static int Serial = 4;
	public final static int Ethernet100FX = 5;        

	public static String getIntName(int type){
		switch(type){
		case 0:
			return "eth";
		case 1:
			return "cua";
		case 2:
			return "wrl";
		case 3: 
			return "wan";                    
		case 4: 
			return "ser";             
		case 5: 
			return "fib";                     
		default:    
			return "unk";
		}
	}

	public final static int NO_NAT = 0;
	public final static int INSIDE_NAT = 1;
	public final static int OUTSIDE_NAT = 2;

	private int NAT_STATUS = 0;

	public void setNAT(int nat){
		NAT_STATUS = nat;
	}

	public int getNAT(){
		return NAT_STATUS;
	}

	public boolean isActive(){
		return false;
	}

	public int getType(){
		return -1;
	}

	/**

	 * Constructs a NetworkInterface object with the name inName and a reference to it's parent Node.

	 * @author tristan_veness

	 * @param inName - The name to give the NetworkInterface eg: eth0

	 * @param parent - The Node that the NetworkInterface is to be added to, it's parent.

	 * @version v0.10

	 */

	protected NetworkInterface(long UID, String inName, Node inParent) {
		super(UID);

		name = inName;

		parentNode = inParent;		

		up = false;

	}



	/**

	 * This method reset the connected link to null

	 * @author luke_hamilton

	 * @version v0.20	 

	 */

	protected void resetConnectedLink() {

		connectedLink = null;

	}



	/**

	 * This method will call the disconnectLink method on the 

	 * Connected link, and then set the connected link to null

	 * @author luke_hamilton

	 * @version v0.20

	 */

	protected void removeConnectedLink() {

		if(connectedLink != null){

			connectedLink.disconnectLink();

			connectedLink = null;

		}

	}



	/**

	 * This method recevie a packet from a connected link and then pass it

	 * to the node's protocolstack.  Depending on the network any

	 * testing of the packets headers may need to be inserted here.

	 * This method is overridded by the subclasses on interface to suite

	 * there requirements 

	 * @author baven_calliess

	 * @param inPacket - A packet

	 * @version v0.20

	 */

	protected void receivePacket(Packet inPacket) throws LowLinkException {}



	/**

	 * This method sends a packet to a connected link Depending on

	 * @author bevan_calliess

	 * @param inPacket

	 * @version v0.20

	 */

	protected void sendPacket(Packet outPacket) throws LowLinkException {}



	/**

	 * Returns the NetworkInterface's name.

	 * @author bevan_calliess

	 * @return Name - The NetworkInterface's name 

	 * @version v0.20

	 */



	public String getName() {

		return name;

	}



	/**

	 * This method is used by interfaces to uniquely identify themselves 

	 * in the simulation it will return a combination of the 

	 * Parent naodes name and the Interfaces name 

	 * eg. Node name PC1 interface eth0 will return "PC1eth0"

	 * @author bevan_calliess

	 * @return Name  - The Source name of this Node Interface 

	 * @version v0.20

	 */    

	protected String getSourceName() {

		return parentNode.getName()+":"+name;

	}



	/**

	 * Sets the connectedLink attribute of the NetworkInterface with Link l.

	 *

	 * <P>This method should only be called by the Link class when an Interface is added or removed from the Link.</P>

	 * @author tristan_veness

	 * @param l  - The Link connected with this NetworkInterface. 

	 * @version v0.10

	 */

	protected void setConnectedLink(Link l)throws InvalidLinkConnectionException {

		if(connectedLink == null){

			connectedLink = l;	

		}else

			throw new InvalidLinkConnectionException("Network Interface is already connected. Delete Connected link first");



	}



	/**

	 * Returns the Link that this NetworkInterface is connected to.

	 * @author tristan_veness

	 * @return The Link object that this NetworkInterface is connected to. null if it is not connected.

	 * @version v0.10

	 */

	public Link getConnectedLink() {

		return connectedLink;

	}



	public String getConnectedLinkName() {

		if(connectedLink!=null) return connectedLink.getName();

		else return null;

	}



	/**

	 * This method returns the name of the network interface

	 * @author tristan_veness

	 * @return Name 

	 * @version v0.10

	 */	 

	protected String getDetails(){

		return name;

	}



	/**

	 * This method returns a string of information about if the network interface is connected of not.

	 * <P>This method is useful for a CLI or for debugging.</P>

	 * @author tristan_veness

	 * @return Name - Connection: link1

	 * @version v0.10

	 */

	protected String getConnectLinkDetails(){

		if(connectedLink != null){

			return "Connection: "+ connectedLink.getName();

		}

		return "Connection: Not connected";	

	}



	public String getConnectLinkName()

	{

		if(connectedLink !=null)

		{

			return connectedLink.getName();

		}

		return "Not Connected";

	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public int getACLin(){
		return acl_in;
	}

	public void setACLin(int acl){
		if(acl<0 || acl>2699){
			acl_in = 0;
		}
		else{
			acl_in = acl;
		}
	}

	public int getACLout(){
		return acl_out;
	}

	public void setACLout(int acl){
		if(acl<0 || acl>2699){
			acl_out = 0;
		}
		else{
			acl_out = acl;
		}
	}

	public Node getParentNode() {
		return parentNode;
	}

	public int getAcceptedPacketPercent() {
		return 0;
	}
	
	public int getInterfaceBandwidth() {
		return 0; 
	}
	
	public int getMetric() {
		if (getAcceptedPacketPercent() == 0 || getInterfaceBandwidth() == 0) {
			return Integer.MAX_VALUE;
		}
		// return time needed for sending 100 Mb
		return (int) Math.round(100 * 100.0 / getAcceptedPacketPercent() * getInterfaceBandwidth());
	}

}//EOF


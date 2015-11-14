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

import java.io.Serializable;
import java.util.Vector;

/**
 * A Link represents the physical link between Nodes in a network. Think of it as
 * the network cable that connects (or links) Nodes together.
 *
 * <P>After adding Nodes to a network you need some way to connect them. The Link class
 * and the NetworkInterface class handle these connections between Nodes and the sending
 * of data between Nodes.</P>
 *
 * <P>Note: There are no distinctions between connections currently. There's no such thing as UTP cable or
 * Coax in our simulation but if this functionality were ever to be implemented you
 * will need to modify this class and the NetworkInterface class, perhaps simply by adding type attributes to
 * each with a check in the addInterface method.</P>
 *
 * @author tristan_veness (Original Author)
 * @author luke_hamilton
 * @since 14 June 2004
 * @version v0.20
 */

public class Link implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4675052404278691897L;

	/** The interfaceList holds all of the NetworkInterfaces that are connected to the Link */

	/**
	 * @link aggregation <{core.NetworkInterface}>
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	Vector<NetworkInterface> NetworkInterfaces = null; 

	/** Name of the link */ 
	protected String name;

	protected double sievingCoefficient;


	/**  *TODO*: javaDoc
	 *
	 */
	public double getSC(){
		return sievingCoefficient;
	}


	/**  *TODO*: javaDoc
	 *
	 */
	public void setSC(double SC){
		sievingCoefficient = SC;
	}

	/**
	 * Constructs a Link with the specified name. Conceptually this will simply create a cable
	 * not connected to anything. You need to call the addInterface() method to 'plug' the cable
	 * in to NetworkInterfaces.
	 * @author luke_hamilton
	 * @param inName  - The name to give the Link. eg: LINK1
	 * @version v0.20
	 **/
	protected Link(String inName) {                
		NetworkInterfaces = new Vector<NetworkInterface>();
		name = inName;
		sievingCoefficient = 100;
	}

	protected String getName()
	{
		return name;
	}

	/**
	 * This method returns true is the link is not connected to any
	 * network interface
	 * @author luke_hamilton
	 * @return boolean
	 * @version v0.20
	 */
	protected boolean isNotConnected() {
		if(NetworkInterfaces.isEmpty()){
			return true;
		}
		return false;
	}

	/**
	 * This method will disconnect the link from any connect networkinterfaces
	 * and then remove them from is Vector
	 * @author luke_hamilton
	 * @version v0.20 
	 */
	protected void disconnectLink() {

		for (int i = 0; i < NetworkInterfaces.size(); i++) {
			NetworkInterface temp = NetworkInterfaces.elementAt(i);
			temp.resetConnectedLink();
		}
		NetworkInterfaces.removeAllElements();		

	}

	/**
	 * Displays all the interfaces in the interface list through System.out.println() calls.
	 * @author tristan_veness
	 * @author luke_hamilton
	 * @version v0.20
	 */
	protected void displayDetails(){		
		System.out.println(name);
		for (int i = 0; i < NetworkInterfaces.size(); i++) {
			NetworkInterface NIC = NetworkInterfaces.elementAt(i);
			System.out.println("Connection " +i+ " "+NIC.getDetails());
		}
	}

	public String getLinkedNodeName(String node, String iface){
		for (int i = 0; i < NetworkInterfaces.size(); i++) {
			if(NetworkInterfaces.elementAt(i).getName().equalsIgnoreCase(iface)
					&& NetworkInterfaces.elementAt(i).getParentNode().getName().equalsIgnoreCase(node)){
				return NetworkInterfaces.elementAt(1-i).getParentNode().getName();
			}
		}
		return "";
	}
}//EOF

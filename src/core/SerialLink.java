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
import java.util.Iterator;
/**
 * SerialLink extends Link. It sets two Interface links to a pc.
 * @author  luke_hamilton
 * @author bevan_calliess
 * @since	Sep 17, 2004
 * @version v0.20
 */


public class SerialLink extends Link {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8760714768329454957L;


	public SerialLink(String inName, NetworkInterface inFirstNodeInterface, NetworkInterface inSecondNodeInterface)throws InvalidLinkConnectionException {
		super(inName);		
		NetworkInterfaces.add(inFirstNodeInterface);
		NetworkInterfaces.add(inSecondNodeInterface);
		inFirstNodeInterface.setConnectedLink(this);
		inSecondNodeInterface.setConnectedLink(this);					
	}
        
        public SerialLink(String inName, NetworkInterface inFirstNodeInterface, NetworkInterface inSecondNodeInterface, double sieveCoeff)throws InvalidLinkConnectionException {
		super(inName);		
		NetworkInterfaces.add(inFirstNodeInterface);
		NetworkInterfaces.add(inSecondNodeInterface);
		inFirstNodeInterface.setConnectedLink(this);
		inSecondNodeInterface.setConnectedLink(this);	
                this.setSC(sieveCoeff);
	}


	public void transportPacket(Serial_packet inPacket,String inSourceName) throws LowLinkException
	{
		Iterator it = NetworkInterfaces.iterator();
        while (it.hasNext()){
                    	
        	NetworkInterface temp = (NetworkInterface) it.next();
        	
        	if(!temp.isOn()  || !temp.isUP()) return;
		
        	if (!temp.getSourceName().equals(inSourceName))
        	{
        		    temp.receivePacket(inPacket);
        	}
        }
    }
}

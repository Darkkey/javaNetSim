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

/*
 * TransportLayerException.java
 *
 * Created on 16 November 2005, 22:41
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package core;

/**
 * @author gift (sourceforge.net user)
 * @param inExceptionMsg the Exception Message 
 * @version v0.10
 */
public class TransportLayerException extends SimulationException
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4448673018032365188L;

	/** Creates a new instance of TransportLayerException */
    public TransportLayerException(String inExceptionMsg) 
    {
        super(inExceptionMsg);
    }  

	/**
	 * @author gift (sourceforge.net user)
	 * @return exceptionMsg
	 * @version v0.10
	 */

	@Override
	public String toString()
        {

		return exceptionMsg;
	}
}

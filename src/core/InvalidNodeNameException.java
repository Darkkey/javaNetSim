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
 * InvalidNodeNameException Class that extends from the Simulation Exception
 * class
 * @author luke_hamilton
 * @version v0.20
 **/
public class InvalidNodeNameException extends SimulationException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 2230337408568877572L;

	/**
	 * This method throw inExceptionMsg up to the super class Simulation
	 * Excpetion
     * @author luke_hamilton
     * @param inExceptionMsg - The Exception Message
     * @version v0.20
     **/
    public InvalidNodeNameException(String inExceptionMsg){
		super(inExceptionMsg);
	}
}

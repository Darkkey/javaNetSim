package core;
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



/**
* SendThread class to be used when the Simulation sends something like ApplicationData. A SendThread is
* exactly the same as a normal Thread except it contains a reference to the Simulation's LayerInfoHandler.
* You could remove the need for this class by passing a reference to the LayerInfoHandler
* to every object's constructor that needs to send LayerInfo. And ofcourse storing that reference
* in each Simulation object (Node, NetworkInterface, Link, Protocol, etc.).
*
* @author tristan_veness
* @since 4 June 2004 
* @version v0.10
**/

class SendThread extends Thread {
          /** A reference to the Simulation's LayerInfoHandler */
		private LayerInfoHandler infoHandler;
		
		/** 
		* Constructs a new SendThread containing a reference to infoHandler.
		* @author tristan_veness
		* @param infoHandler - A reference to the Simulation's LayerInfoHandler.
		* @version v0.10
		**/
		protected SendThread(LayerInfoHandler infoHandler) {
			setDaemon(true);
			this.infoHandler = infoHandler;
	     }
		/**
		* Returns a reference to the LayerInfoHandler used by the Simulation and passed to this Thread.
		* @author bevan_calliess
		* @author robert_hulford
		* @return A reference to the LayerInfoHandler used by the Simulation.
		* @version v0.20
		**/
          public LayerInfoHandler getInfoHandler() {
               return infoHandler;
          }
}//EOF

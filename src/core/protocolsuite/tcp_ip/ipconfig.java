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

import java.util.Vector;



public class ipconfig {
	private Vector ipconfigTable;

	/**
	 * Assigns a new IP config vector
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @version v0.20
	 */
	public ipconfig(){
		ipconfigTable = new Vector();
	}
		
	/**
	 * Add a new entry to the IPconfigTable
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @param inEntry - IPconfigTableEntry
	 * @version v0.20
	 */
	public void addTableEntry(ipconfigTableEntry inEntry){
		ipconfigTable.add(inEntry);
	}
	
	/**
	 * Returns the size of the ipconfigTable
	 * @author luke_hamilton
	 * @author bevan_calliess
	 * @return size - The size of the ipconfigtable
	 */
	public int size(){
		return ipconfigTable.size();
	}
}//EOF

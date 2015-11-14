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

package guiUI;

import core.Node;

/**
 * 
 * <P>
 * The GuiHub class is used to instantiate new Hub's on the GUI
 * </P>
 * 
 * 
 * 
 * @author VC2 Team.
 * 
 * @since 15th November 2004
 * 
 * @version v0.20
 * 
 */

public class GuiHub extends DataLinkLayerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 787313107140597768L;

	/**
	 * 
	 * @param inName
	 *            The name of the Hub
	 * 
	 * @param inMainscreen
	 *            The JFrame that the router will be created on
	 * 
	 */

	public GuiHub(String inName, MainScreen inMainScreen) {

		super(inName, inMainScreen, "images/simulation/hub.png");

	}

	@Override
	public void addInterfaces(MainScreen parent, Node node) {

		String[] choices = { "5-ports simple hub.", "8-ports hub." };

		String choice = parent.getDeviceTypeDialog("Create new hub",
				"Please choose hub type", choices);

		int portCount = 5;

		if (choice.contains(choices[1]))
			portCount = 8;

		for (int i = 0; i < portCount; i++) {
			node.addNetworkInterface(core.NetworkInterface
					.getIntName(core.NetworkInterface.Ethernet10T)
					+ String.valueOf(i), core.NetworkInterface.Ethernet10T,
					false);
		}

	}

}

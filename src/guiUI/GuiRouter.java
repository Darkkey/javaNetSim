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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import core.NetworkInterface;
import core.Node;

/**
 * 
 * <P>
 * The GuiRouter class is used to instantiate new Routers on the GUI
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

public class GuiRouter extends ApplicationLayerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5079851235537869986L;

	/**
	 * 
	 * @param inName
	 *            The name of the router
	 * 
	 * @param inMainscreen
	 *            The JFrame that the router will be created on
	 * 
	 */

	private JMenuItem mnuTelnetListen = new JMenuItem(
			"Start telnet server to listen.");

	private JMenuItem mnuTelnetNoListen = new JMenuItem("Stop telnet server.");

	public GuiRouter(String inName, MainScreen inMainScreen) {

		super(inName, inMainScreen, "images/simulation/router.png"); // router_large.gif

		mnuTelnetListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.TelnetListen(lblNodeName.getText());
			}
		});
		mnuTelnetNoListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.TelnetNoListen(lblNodeName.getText());
			}
		});

		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuTelnetListen);
		mnuAppLayer.add(mnuTelnetNoListen);

	}

	@Override
	public void addInterfaces(MainScreen parent, Node node) {

		String[] choices = {
				"Simple router with 2 Ethernet-TX ports.",
				"Router with 3 Ethernet-TX ports",
				"Router with 2 Ethernet-TX ports and 1 Ethernet-FX port.",
				"Router with 3 Ethernet-FX ports.",
				"WAN router with 1 Ethernet-TX, 2 Serial and 1 Ethernet-FX ports.",
				"Wireless router with 1 Ethernet-TX and 1 WiFi ports.",
				"Multi-enviroment router with 3 Ethernet-TX, 2 Serial, 1 WiFi and 1 Ethernet-FX ports."/*,
				"Router with embbeded switch"*/
				};

		String choice = parent.getDeviceTypeDialog("Create new Router",
				"Please choose router type", choices);

		if (choice.contains(choices[0])) {
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "0", NetworkInterface.Ethernet10T, true, 0);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "1", NetworkInterface.Ethernet10T, true, 0);
		} else if (choice.contains(choices[1])) {
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "0", NetworkInterface.Ethernet10T, true, 0);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "1", NetworkInterface.Ethernet10T, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "2", NetworkInterface.Ethernet10T, true);
		} else if (choice.contains(choices[2])) {
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "0", NetworkInterface.Ethernet10T, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "1", NetworkInterface.Ethernet10T, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet100FX)
					+ "0", NetworkInterface.Ethernet100FX, true);
		} else if (choice.contains(choices[3])) {
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet100FX)
					+ "0", NetworkInterface.Ethernet100FX, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet100FX)
					+ "1", NetworkInterface.Ethernet100FX, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet100FX)
					+ "2", NetworkInterface.Ethernet100FX, true);
		} else if (choice.contains(choices[4])) {
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "0", NetworkInterface.Ethernet10T, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Serial)
					+ "0", NetworkInterface.Serial, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Serial)
					+ "1", NetworkInterface.Serial, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet100FX)
					+ "0", NetworkInterface.Ethernet100FX, true);
		} else if (choice.contains(choices[5])) {
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "0", NetworkInterface.Ethernet10T, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Wireless)
					+ "0", NetworkInterface.Wireless, true);
			
		} else if (choice.contains(choices[6])) {
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "0", NetworkInterface.Ethernet10T, true, 0);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "1", NetworkInterface.Ethernet10T, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet10T)
					+ "2", NetworkInterface.Ethernet10T, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Serial)
					+ "0", NetworkInterface.Serial, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Serial)
					+ "1", NetworkInterface.Serial, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Ethernet100FX)
					+ "0", NetworkInterface.Ethernet100FX, true);
			node.addNetworkInterface(NetworkInterface
					.getIntName(NetworkInterface.Wireless)
					+ "0", NetworkInterface.Wireless, true);
		}
		
		
		node.addNetworkInterface(NetworkInterface
				.getIntName(NetworkInterface.Console)
				+ "0", NetworkInterface.Console, false);
	}

}

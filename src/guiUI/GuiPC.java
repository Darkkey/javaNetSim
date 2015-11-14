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

import core.Node;

/**
 * 
 * <P>
 * The GuiPC class is used to instantiate new PC on the GUI
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

public class GuiPC extends ApplicationLayerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 816132267929695723L;

	/**
	 * 
	 * @param inName
	 *            The name of the PC
	 * 
	 * @param inMainscreen
	 *            The JFrame that the router will be created on
	 * 
	 */

	private JMenuItem mnuEchoListen = new JMenuItem(
			"Start udp echo server to listen.");

	private JMenuItem mnuEchoSend = new JMenuItem(
			"Send data via udp echo client.");

	private JMenuItem mnuEchotcpListen = new JMenuItem(
			"Start tcp echo server to listen.");

	private JMenuItem mnuEchotcpSend = new JMenuItem(
			"Send data via tcp echo client.");

	private JMenuItem mnuTelnetListen = new JMenuItem(
			"Start telnet server to listen.");

	private JMenuItem mnuTelnetNoListen = new JMenuItem("Stop telnet server.");

	private JMenuItem mnuTelnetConnect = new JMenuItem("Telnet client.");

	private JMenuItem mnuPosixTelnet = new JMenuItem("RFC(line) telnet client.");

	private JMenuItem mnuSNMPManager = new JMenuItem("Send SNMP message");

	private JMenuItem mnuDHCPD = new JMenuItem("Start DHCP Server");

	public GuiPC(String inName, MainScreen inMainScreen) {

		super(inName, inMainScreen, "images/simulation/mymac.png");

		mnuEchoListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.EchoServerListen(lblNodeName.getText());

			}
		});
		mnuEchoSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.EchoSend(lblNodeName.getText());
			}
		});

		mnuEchotcpListen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.EchotcpServerListen(lblNodeName.getText());

			}
		});
		mnuEchotcpSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.EchotcpSend(lblNodeName.getText());
			}
		});

		mnuSNMPManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.SNMPSendMessage(lblNodeName.getText());
			}
		});

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
		mnuTelnetConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.TelnetConnect(lblNodeName.getText());
			}
		});

		mnuPosixTelnet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.PosixTelnet(lblNodeName.getText());
			}
		});

		mnuDHCPD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.DHCPD(lblNodeName.getText());
			}
		});

		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuEchoListen);
		mnuAppLayer.add(mnuEchoSend);
		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuEchotcpListen);
		mnuAppLayer.add(mnuEchotcpSend);
		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuSNMPManager);
		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuTelnetListen);
		mnuAppLayer.add(mnuTelnetNoListen);
		mnuAppLayer.add(mnuTelnetConnect);
		mnuAppLayer.add(mnuPosixTelnet);
		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuDHCPD);
		
		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuTerminal);

	}

	@Override
	public void addInterfaces(MainScreen parent, Node node) {

		node.addNetworkInterface(core.NetworkInterface
				.getIntName(core.NetworkInterface.Ethernet10T)
				+ "0", core.NetworkInterface.Ethernet10T, true, 0);

		node.addNetworkInterface(core.NetworkInterface
				.getIntName(core.NetworkInterface.Console)
				+ "0", core.NetworkInterface.Console, false, 0);

		node.addNetworkInterface(core.NetworkInterface
				.getIntName(core.NetworkInterface.Wireless)
				+ "0", core.NetworkInterface.Wireless, true, 0);

	}

}

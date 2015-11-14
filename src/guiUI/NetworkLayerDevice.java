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

import javax.swing.JMenu;
import javax.swing.JMenuItem;



/**

 * <P>The NetworkLayerDevice class is an abstract class used for further classification of Network </P>

 * <P>layer devices such as PC's and Routers.

 * 

 * @author VC2 Team.

 * @since 15th November 2004

 * @version v0.20

 */



public abstract class NetworkLayerDevice extends GuiNode {

	

	private JMenuItem mnuSetTCPIPProperties = new JMenuItem("Set TCP/IP Properties ...");		

	private JMenuItem mnuSendPing = new JMenuItem("Send Ping ...");

	private JMenuItem mnuProperties = new JMenuItem("Properties ...");

        private JMenu mnuARPMenu  = new JMenu("ARP");

        private JMenu mnuCountersMenu  = new JMenu("Counters");

        

        private JMenuItem mnuShowCounters = new JMenuItem("Show Packet Counters");

        private JMenuItem mnuResetCounters = new JMenuItem("Reset Packet Counters");

        

        private JMenuItem mnuArpStaticAdd = new JMenuItem("Add static entry to ARP table...");

        private JMenuItem mnuArpRemove = new JMenuItem("Remove entry from ARP table...");

        

        private JMenuItem mnuRunCmd = new JMenuItem("Console");

        private JMenuItem mnuPR = new JMenuItem("Print route table");

	private JMenuItem mnuArp = new JMenuItem("Print ARP Table");


	/**

	 * @param inName  The name of the Network Layer Device

	 * @param inMainscreen	The JFrame that the router will be created on

	 * @param imageLocation	Location of the Image used on the GUI

	 */

	

	public NetworkLayerDevice(String inName, MainScreen inMainScreen, String imageLocation) {

		super(inName, inMainScreen, imageLocation);

		

		mnuSetTCPIPProperties.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.setTCPIPProperties(lblNodeName.getText());

				

				

			}

		});

		mnuSendPing.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

					controller.SendPing(lblNodeName.getText());

			}

		});

		mnuArp.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.printARP(lblNodeName.getText());

			}

		});

                

                mnuArpStaticAdd.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.addStaticARP(lblNodeName.getText());

			}

		});

                mnuArpRemove.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.removeARP(lblNodeName.getText());

			}

		});

                mnuRunCmd.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){
                           
                            controller.RunCmd(lblNodeName.getText());

			}

		});

		mnuProperties.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.showPropertiesDialog(lblNodeName.getText());

			}

		});

               

                mnuPR.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.PrintRouteTable(lblNodeName.getText());

			}

		});

                mnuShowCounters.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.showCounters(lblNodeName.getText());

			}

		});

                mnuResetCounters.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.resetCounters(lblNodeName.getText());

			}

		});

                

				

		GuiNodePopMenu.add(mnuSetTCPIPProperties);

		GuiNodePopMenu.add(mnuSendPing);

                GuiNodePopMenu.add(mnuARPMenu);

                mnuARPMenu.add(mnuArpStaticAdd);

                mnuARPMenu.add(mnuArpRemove);

		mnuARPMenu.add(mnuArp);

                GuiNodePopMenu.add(mnuCountersMenu);

                mnuCountersMenu.add(mnuShowCounters);

                mnuCountersMenu.add(mnuResetCounters);

                GuiNodePopMenu.add(mnuRunCmd);

                GuiNodePopMenu.add(mnuPR);

		GuiNodePopMenu.add(mnuProperties);

		

		

	}

	



	

	

}


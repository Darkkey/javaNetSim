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



/**

 * <P>The DataLinkLayerDevice class is an abstract class used for further classification of Data </P>

 * <P>link layer devices such as Hub's and Switches.

 * 

 * @author VC2 Team.

 * @since 15th November 2004

 * @version v0.20

 */



public abstract class DataLinkLayerDevice extends GuiNode {

	

	private JMenuItem mnuProperties = new JMenuItem("Properties ...");

        private JMenuItem mnuShowState = new JMenuItem("Show state");

        private JMenuItem mnuReset = new JMenuItem("Reset");


	/**

	 * @param inName  The name of the Data Link Layer Device

	 * @param inMainscreen	The JFrame that the router will be created on

	 * @param imageLocation	Location of the Image used on the GUI

	 */

	

	public DataLinkLayerDevice(String inName, MainScreen inMainScreen, String imageLocation) {

		super(inName, inMainScreen, imageLocation);



		mnuProperties.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.showPropertiesDialog(lblNodeName.getText());

			}

		});

                mnuReset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.Reset(lblNodeName.getText());

			}

		});

                

                mnuShowState.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				controller.ShowHubState(lblNodeName.getText());

			}

		});

                




                

		GuiNodePopMenu.add(mnuProperties);

                GuiNodePopMenu.add(mnuShowState);

                GuiNodePopMenu.add(mnuReset);         
	}

}


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



import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;



/**

 * <P>The SimulationToolBar class is the toolbar used for creating new Nodes</P>

 * 

 * @author VC2 Team.

 * @since 15th November 2004

 * @version v0.20

 */



public class SimulationToolBar extends ToolBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4864249995312706530L;



	private ClassLoader cl = this.getClass().getClassLoader();

	

	private JButton btnNewPC = new JButton(new ImageIcon(cl.getResource("images/simulation/PC_small.gif")));

	private JButton btnNewRouter = new JButton(new ImageIcon(cl.getResource("images/simulation/router_small.gif")));

	private JButton btnNewSwitch = new JButton(new ImageIcon(cl.getResource("images/simulation/switch_small.gif")));

	private JButton btnNewHub = new JButton(new ImageIcon(cl.getResource("images/simulation/hub_small.gif")));
        
        private JButton btnNewCSUDSU = new JButton(new ImageIcon(cl.getResource("images/simulation/csudsu_small.png")));
        
        private JButton btnNewPrinter = new JButton(new ImageIcon(cl.getResource("images/simulation/printer_small.png")));

	private JButton btnNewLink = new JButton(new ImageIcon(cl.getResource("images/simulation/link.gif")));

	

	Color activatedColor = Color.RED;

	Color deactivatedColor = btnNewLink.getBackground();

	

	/**

	 * Constructor to build a SimulationToolBar

	 * @param inMainScreen The JFrame that the SimulationToolBar will be placed on.

	 */

	public SimulationToolBar(MainScreen inMainScreen) {

		super(inMainScreen); 

		

		// Call method to create the toolbar

		

		buildToolbar();

	}

	

	/**

	 * This method is called from the constructor of SimulationToolBar

	 * It sets up the buttons, action listeners, tooltips and borders for the

	 * buttons on the Simulation toolbar. 	

	 * @author VC2 Team.

	 * @version v0.20

	 **/

	

	private void buildToolbar(){

		

		//Set the tooltips for these buttons

		

		btnNewPC.setToolTipText("Creates a new PC");

		btnNewRouter.setToolTipText("Creats a new Router");

		btnNewLink.setToolTipText("Creates a new Link between two nodes");

		btnNewSwitch.setToolTipText("Creates a new Switch");

		btnNewHub.setToolTipText("Creates a new Hub");
                
        btnNewCSUDSU.setToolTipText("Creates a new CSU/DSU device");
                
        btnNewPrinter.setToolTipText("Creates a new Printer");

				

		//Set the buttons to have no borders so that they don't make the toolbar look like crap.

		btnNewPC.setBorderPainted(false);

		btnNewRouter.setBorderPainted(false);

		btnNewLink.setBorderPainted(false);

		btnNewSwitch.setBorderPainted(false);

		btnNewHub.setBorderPainted(false);
                
                btnNewCSUDSU.setBorderPainted(false);
                
                btnNewPrinter.setBorderPainted(false);

		

		//Set action listener for PC toolbar item

		btnNewPC.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				setHighlightsOff();  //Clean out any buttons that may have been highlighted previously.

				highlightButton(btnNewPC, true);

				controller.addingNode("PC");

			}

		});


               btnNewPrinter.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				setHighlightsOff();  //Clean out any buttons that may have been highlighted previously.

				highlightButton(btnNewPrinter, true);

				controller.addingNode("Printer");

			}

		});


		//Set action listener for Hub toolbar item

		btnNewHub.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				setHighlightsOff(); //Clean out any buttons that may have been highlighted previously.

				highlightButton(btnNewHub, true);

				controller.addingNode("Hub");

			}

		});


		//Set action listener for Hub toolbar item

		btnNewCSUDSU.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				setHighlightsOff(); //Clean out any buttons that may have been highlighted previously.

				highlightButton(btnNewCSUDSU, true);

				controller.addingNode("CSUDSU");

			}

		});		

		//Set action listener for Router toolbar item

		btnNewRouter.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				setHighlightsOff(); //Clean out any buttons that may have been highlighted previously.

				highlightButton(btnNewRouter, true);

				controller.addingNode("Router");

			}

		});

	

		//Set action listener for Switch toolbar item

                

		btnNewSwitch.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

			    setHighlightsOff(); //Clean out any buttons that may have been highlighted previously.

                            highlightButton(btnNewSwitch, true);

                            controller.addingNode("Switch");

			}

		});

		

                

		btnNewLink.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e){

				setHighlightsOff(); //Clean out any buttons that may have been highlighted previously.

				setLinkHighlight(true);

				controller.creatingLink();	

				

				

			}

		});

		

		this.add(btnNewPC);
                
                this.add(btnNewPrinter);

		this.add(btnNewRouter);

		this.add(btnNewHub);

                this.add(btnNewSwitch);
                
                this.add(btnNewCSUDSU);

		this.add(btnNewLink);

	}

	

	//	 Will make a button highlight red to indicate that it is in use.  

	// Alternatively it will go back to grey when dormant.

	

	/**

	 * This method is called from the constructor of StandardToolBar

	 * It sets up the buttons, action listeners, tooltips and borders for the

	 * buttons on the standard toolbar. 

	 * 

	 * This method is called from the setLinkHighlight method

	 * @param inButton The button that's highlighted status will be changed

	 * @param blnStatus	True or False depending on whether the button should be highlighted or not.

	 * @author VC2 Team.

	 * @version v0.20

	 **/

	

	public void highlightButton(JButton inButton, boolean blnStatus){

		if(blnStatus)

		{

			inButton.setBackground(activatedColor);		

		}

		else

		{

			inButton.setBackground(deactivatedColor);

		}

	}

	

	/**

	 * This method calls the highlightButton method to set the Link button highlighted status on or off.

	 * @param inValue	True or False depending on whether the button should be highlighted or not.

	 * @author VC2 Team.

	 * @version v0.20

	 **/

	

	public void setLinkHighlight(boolean inValue){	

		highlightButton(btnNewLink, inValue);			

	}

	

	/**

	 * This method will set the background colour of all buttons to off.

	 * @author VC2 Team.

	 * @version v0.20

	 **/

	

	public void setHighlightsOff(){

		btnNewPC.setBackground(deactivatedColor);
                
                btnNewPrinter.setBackground(deactivatedColor);

		btnNewRouter.setBackground(deactivatedColor);

		btnNewSwitch.setBackground(deactivatedColor);

		btnNewHub.setBackground(deactivatedColor);

		btnNewLink.setBackground(deactivatedColor);

		btnNewCSUDSU.setBackground(deactivatedColor);
                
	}

}


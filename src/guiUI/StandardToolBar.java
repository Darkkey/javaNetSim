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

import javax.swing.ImageIcon;
import javax.swing.JButton;


/**
 * <P>The StandardToolBar class is the toolbar used for New Sims, saving Sims and loading Sims</P>
 * 
 * @author luke_hamilton
 * @since 15th November 2004
 * @version v0.20
 */

public class StandardToolBar extends ToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4244939315299762795L;

	private ClassLoader cl = this.getClass().getClassLoader();
	
	private JButton btnNew = new JButton(new ImageIcon(cl.getResource("images/standard/new_document16_h.gif")));
	private JButton btnOpen = new JButton(new ImageIcon(cl.getResource("images/standard/open_document16_h.gif")));
	private JButton btnSave = new JButton(new ImageIcon(cl.getResource("images/standard/save16_h.gif")));
	
	
	/**
	 * Constucts a standard toolbar
	 * @param inSim The Simulation that items from the toolbar will control.
	 * @param inMainScreen The JFrame that the toolbar will be attached to.
	 */
	public StandardToolBar(MainScreen inMainScreen) {
		super(inMainScreen);
		buildToolBar();
	}
	
	/**
	 * This method is called from the constructor of StandardToolBar
	 * It sets up the buttons, action listeners, tooltips and borders for the
	 * buttons on the standard toolbar. 	
	 * @author luke_hamilton
	 * @version v0.20
	 **/
	
	private void buildToolBar(){	
		
		// Setup tooltips for these buttons		
		btnNew.setToolTipText("Creates a New Simulation");
		btnOpen.setToolTipText("Open a previously saved Simulation");
		btnSave.setToolTipText("Saves the current Simulation");
		
		//Setup buttons on the toolbar to have no border.
		btnNew.setBorderPainted(false);
		btnOpen.setBorderPainted(false);
		btnSave.setBorderPainted(false);
		
			
		//Add Action listener for save
		btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			    //JOptionPane.showMessageDialog(null,"Sorry this is not yet implemented.","Save Dialog Message", 
				//		JOptionPane.INFORMATION_MESSAGE);
			    controller.Save();
			}
		});
		
		//Add Action listener for load
		btnOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			//    JOptionPane.showMessageDialog(null,"Sorry this is not yet implemented.","Load Dialog Message", 
			//			JOptionPane.INFORMATION_MESSAGE);
			    controller.Open();
			}
		});
		
		//Add Action listener for new
		btnNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				controller.clearSaveAs();					
				controller.refreshMainScreen();
			}
		});
		
		
		this.add(btnNew);
		this.add(btnOpen);
		this.add(btnSave);
	}

}

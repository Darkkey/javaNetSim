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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import core.InvalidLinkNameException;
import core.InvalidNetworkInterfaceNameException;
import core.InvalidNodeNameException;
import core.Simulation;

/**
 * 
 * @author Key
 *

 * This class is a dialog that enables to set link properties
 * 
 */
public class LinkProperties extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7313505773384166380L;
	private JPanel backpanel;
	private JLabel lblInterface;
	private JLabel lblNodeName;
	private JComboBox cmbNodeName;
	private JComboBox cmbInterface;
	private JLabel lblError; 
        private JLabel lblProp; 
	private JButton btnOk;
        private JTextField txtProp;
	
	private MainScreen controller;
	private Simulation Sim;
        private String NodeName="";
	private String Interface="";
	
	public LinkProperties(JFrame frame, Object nodeArray[], int selectedIndex, Simulation Sim, SandBox SBox) {
		super(frame);
		setResizable(false);
		controller = (MainScreen)frame;
		this.Sim = Sim;
                setTitle("Link Properties");
		initGUI(nodeArray,selectedIndex);

		final JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnOk = new JButton();
		btnOk.setEnabled(true);
		btnOk.setToolTipText("Set options!");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton();
			}
		});
		
		btnOk.setName("btnOK");
		panel.add(btnOk);
		btnOk.setText("OK");

		final JButton btnCancel = new JButton();
		btnCancel.setToolTipText("Cancel changes");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton();
			}
		});
		btnCancel.setName("btnCancel");
		panel.add(btnCancel);
		btnCancel.setText("Cancel");
		this.getRootPane().setDefaultButton(btnOk);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setVisible(true);
		
	}
	
	private void initGUI(Object nodeArray[], int selectedIndex) {
		try {
			setSize(350, 225);
			{
				backpanel = new JPanel();
				backpanel.setMinimumSize(new Dimension(200, 10));
				this.getContentPane().add(backpanel, BorderLayout.CENTER);
				GridBagLayout backpanelLayout = new GridBagLayout();
				backpanel.setPreferredSize(new java.awt.Dimension(264, 213));
				backpanelLayout.columnWeights = new double[] {};
				backpanelLayout.columnWidths = new int[] {};
				backpanelLayout.rowWeights = new double[] {0.0};
				backpanelLayout.rowHeights = new int[] {5,5,5,5};
				backpanel.setLayout(backpanelLayout);
				{
					lblNodeName = new JLabel();
					backpanel.add(lblNodeName, new GridBagConstraints(
						0,
						0,
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0),
						21,
						0));
					lblNodeName.setText("Node Name:");
				}
				{
					lblInterface = new JLabel();
					backpanel.add(lblInterface, new GridBagConstraints(
						0,
						1,
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 27),
						0,
						0));
					lblInterface.setText("Interface:");
				}
                                {
					lblProp = new JLabel();
					backpanel.add(lblProp, new GridBagConstraints(
						0,  
						2, 
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 16),
						0,
						0));
					lblProp.setText("Packet passthrough(%) (0 - 100):");
				}

				cmbNodeName = new JComboBox(nodeArray);
				cmbNodeName.setMinimumSize(new Dimension(100, 0));
				cmbNodeName.setSelectedIndex(selectedIndex);
				cmbNodeName.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectNode();
					}
				});

				final GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.gridx = 1;
				backpanel.add(cmbNodeName, gridBagConstraints);

				cmbInterface = new JComboBox();
				cmbInterface.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectInterface();
					}
				});
				cmbInterface.setEnabled(false);
				
                                final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
				gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_1.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_1.gridy = 1;
				gridBagConstraints_1.gridx = 1;
				backpanel.add(cmbInterface, gridBagConstraints_1);
                                
                                txtProp = new JTextField();
				txtProp.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						//ipAddressEntered();
					}
				});
                                
                                txtProp.setEnabled(true);
				final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
				gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_3.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_3.gridy = 2;
				gridBagConstraints_3.gridx = 1;
				backpanel.add(txtProp, gridBagConstraints_3);
				txtProp.setText("Enter %% 0-100");
                                
				lblError = new JLabel();
				lblError.setHorizontalTextPosition(SwingConstants.CENTER);
				lblError.setHorizontalAlignment(SwingConstants.CENTER);
				lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
				lblError.setMinimumSize(new Dimension(100, 20));
				lblError.setMaximumSize(new Dimension(100, 20));
				lblError.setPreferredSize(new Dimension(100, 20));
				lblError.setVisible(false);
				final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
				gridBagConstraints_5.anchor = GridBagConstraints.WEST;
				gridBagConstraints_5.insets = new Insets(0, 1, 0, 0);
				gridBagConstraints_5.fill = GridBagConstraints.BOTH;
				gridBagConstraints_5.gridwidth = 2;
				gridBagConstraints_5.gridy = 5;
				gridBagConstraints_5.gridx = 0;
				backpanel.add(lblError, gridBagConstraints_5);
				lblError.setText("Error Message!!!! Error");
                                   
                                
				if(selectedIndex == 0){
					selectNode();							
				}
				
                                
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is executed when the user hit's the enter button.
	 * It will delete the link on selected interface.
	 * 
	 * @author Key
	 *
	 */
	private void okButton(){
		try {
			
			if(NodeName != null && Interface != null){
				//String str = Sim.disconnectLink(NodeName, Interface);				
                                //SBox.removeLine(str);
                               
                                ///!!!!!!!!!!!!!!!
                                // !!!!!!!!!!!!!!!!!!
                                // *TODO*: security from FOOLS
                                String SC = txtProp.getText();
                                double scoeff = Double.valueOf(SC.trim()).doubleValue();
                                String lnk = Sim.getLinkName(NodeName, Interface);
                                if(lnk!=null){
                                    Sim.SetLinkProb(lnk, scoeff);
                                    controller.addToConsole("Setting sieving coefficient for " + NodeName +"'s link on interface "+Interface+" at " + SC + ".\n");
                                }
			}
					
						
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is executed when the user hits the cancel button
	 * @author luke_hamilton
         * @author Key
	 *
	 */
	private void cancelButton(){
		this.dispose();
	}

	/**
	 * This method will generate the data within the Interface combobox based
	 * on the node that is selected. It will also get the default gateway if there is one set
	 * for the node and add the text to the text field
	 * 
	 * @author luke_hamilton
         * @author Key
	 *
	 */	
	private void selectNode(){
		            
		//Remove all items before regenerating the combobox.
		//This is because if a users selects the node twice, it would add the interfaces twice.
		cmbInterface.removeAllItems();
		
		NodeName = (String)cmbNodeName.getSelectedItem();
				
		try {
			Object nics[] = Sim.getNode(NodeName).getAllInterfaces();	//Get object array of interface names
			
			//Sort the array
			Arrays.sort(nics);
			
			for (int i = 0; i < nics.length; i++) { //Add them to the combobox
                            if(Sim.getNode(NodeName).getIntType((String)nics[i]) == core.NetworkInterface.Ethernet10T ){
				cmbInterface.addItem(nics[i]);
                            }
			}			
			cmbInterface.setEnabled(true);
	
                        selectInterface();
                        
		} catch (Exception e) {	//This should never happen 
			e.printStackTrace();
		}
                
	}
	
	/**
	 * This method enabled's the IP Address text field once the interface has been selected.
	 * This will also check the selected interface to see if the ip address has already been set
	 * if so entering that ip address into the text field and then getting the set subnet mask for that 
	 * ip address.
	 * @author Key
	 */
	private void selectInterface(){	
            if(!Interface.equals("")){
                String lnk;
                try {
                    lnk = Sim.getLinkName(NodeName, Interface);
                    if(lnk!=null){
                        Sim.SetLinkProb(lnk, Double.parseDouble(txtProp.getText()));
                    }
                } 
                catch (NumberFormatException ex) {
//                    ex.printStackTrace();
                } 
                catch (InvalidLinkNameException ex) {
                    ex.printStackTrace();
                } 
                catch (InvalidNetworkInterfaceNameException ex) {
                    ex.printStackTrace();
                } 
                catch (InvalidNodeNameException ex) {
                    ex.printStackTrace();
                }
            }
            
            Interface = (String)cmbInterface.getSelectedItem();	
            try{
                String lnk = Sim.getLinkName(NodeName, Interface);
                if(lnk!=null) txtProp.setText(Double.valueOf(Sim.GetLinkProb(lnk)).toString());
            }catch(Exception e){ System.out.println(e.toString()); }
	}
	
}


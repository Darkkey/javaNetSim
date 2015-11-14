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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import core.NetworkLayerDevice;
import core.Node;
import core.Simulation;
import core.protocolsuite.tcp_ip.IPV4Address;


/**
 * 
 * @author luke_hamilton
 *
 * This class is a dialog that enables the user to be able to set the TCP/IP setting
 * on a selected node. eg: set IP address, subnet mask and default gateway address.
 * 
 */
public class SetTCPIPPropertiesDialog extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7663707499126963363L;
	private JPanel backpanel;
	private JLabel lblInterface;
	private JLabel lblIPAddress;
	private JLabel lblNodeName;
	private JComboBox cmbNodeName;
	private JComboBox cmbInterface;
	private JTextField txtIpAddress;
	private JTextField txtSubnetMask;
	private JLabel lblError; 
	private JTextField txtDefaultGW;
	private JButton btnOk;
	
	private MainScreen controller;
	private Simulation Sim;
	
	private String IPAddress="";
	private String SubnetMask="";
	private String NodeName="";
	private String Interface="";
	private String DefaultGWAddress="";

	private boolean ErrorFlag = true;
		
	public SetTCPIPPropertiesDialog(JFrame frame, Object nodeArray[], int selectedIndex, Simulation Sim) {
		super(frame);
		setResizable(false);
		controller = (MainScreen)frame;
		this.Sim = Sim;
		setTitle("Internet Protocol (TCP/IP) Properties");
		initGUI(nodeArray,selectedIndex);

		final JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnOk = new JButton();
		btnOk.setEnabled(true);
		btnOk.setToolTipText("Set TCP/IP Properties");
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
					lblIPAddress = new JLabel();
					backpanel.add(lblIPAddress, new GridBagConstraints(
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
					lblIPAddress.setText("IP Address:");
				}

				final JLabel lblSubnetMask = new JLabel();
				final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
				gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_2.weighty = 1.0;
				gridBagConstraints_2.anchor = GridBagConstraints.LINE_END;
				gridBagConstraints_2.gridy = 3;
				gridBagConstraints_2.gridx = 0;
				backpanel.add(lblSubnetMask, gridBagConstraints_2);
				lblSubnetMask.setText("Subnet Mask:");

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

				txtIpAddress = new JTextField();
				txtIpAddress.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						ipAddressEntered();
					}
				});
				txtIpAddress.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent e) {
//						checkForDefaultSubnet();
					}
					@Override
					public void keyPressed(KeyEvent e) {
//						checkForDefaultSubnet();
					}
				});
				txtIpAddress.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {						
						txtIpAddress.selectAll();
					}
				});
				txtIpAddress.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ipAddressEntered();
						
					}
				});
				txtIpAddress.setEnabled(false);
				final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
				gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_3.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_3.gridy = 2;
				gridBagConstraints_3.gridx = 1;
				backpanel.add(txtIpAddress, gridBagConstraints_3);
				txtIpAddress.setText("Enter IP Address");

				txtSubnetMask = new JTextField();
				txtSubnetMask.setPreferredSize(new Dimension(140, 20));
				txtSubnetMask.setMinimumSize(new Dimension(0, 0));

				txtSubnetMask.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						subnetmask();
					}
				});
				txtSubnetMask.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						subnetmask();
					}
				});
				txtSubnetMask.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						txtSubnetMask.selectAll();
					}
				});
				txtSubnetMask.setEnabled(false);
				final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
				gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_4.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_4.gridy = 3;
				gridBagConstraints_4.gridx = 1;
				backpanel.add(txtSubnetMask, gridBagConstraints_4);
				txtSubnetMask.setText("Enter Subnet Mask");

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

				txtDefaultGW = new JTextField();
				txtDefaultGW.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						setDefaultGW();
					}
				});
                                txtDefaultGW.addKeyListener(new KeyListener() {
                                        public void keyTyped(KeyEvent e) {}
                                        public void keyReleased(KeyEvent e) {}
                                        public void keyPressed(KeyEvent e) {
                                            int key = e.getKeyCode();
                                            if (key == KeyEvent.VK_ENTER) {
                                                setDefaultGW();                                                
                                            }
                                        }
				});
				txtDefaultGW.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						txtDefaultGW.selectAll();
					}
				});
				txtDefaultGW.setEnabled(false);
				final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
				gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_6.gridy = 4;
				gridBagConstraints_6.gridx = 1;
				backpanel.add(txtDefaultGW, gridBagConstraints_6);
				txtDefaultGW.setText("Enter Default Gateway");

				final JLabel lblDefaultGW = new JLabel();
				final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
				gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_7.gridy = 4;
				gridBagConstraints_7.gridx = 0;
				backpanel.add(lblDefaultGW, gridBagConstraints_7);
				lblDefaultGW.setText("Default Gateway:  ");
				
                                
                                
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
	 * It will then test if the IP address can be set, or if the subnetmask
	 * can be set and if the default gateway can be set.
	 * 
	 * @author luke_hamilton
	 *
	 */
	private void okButton(){
		try {
			
			if(NodeName != null && Interface != null && IPAddress != null && !ErrorFlag){
				Node temp = Sim.getNode(NodeName);
				NetworkLayerDevice nld = (NetworkLayerDevice)temp;
				
				nld.setTCPIPSettings(Interface, IPAddress, SubnetMask, DefaultGWAddress);
				
				controller.addToConsole(NodeName +"'s IP Address has been set to " + IPAddress + " on interface "+Interface+"\n");
				
				controller.addToConsole(NodeName +"'s Subnet Address has been set to " + SubnetMask + " on interface "+Interface+"\n");
				
				controller.addToConsole(NodeName +"'s Default Gateway Address has been set to " + DefaultGWAddress +"\n");
            
				this.dispose();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is executed when the user hits the cancel button
	 * @author luke_hamilton
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
                            if(Sim.getNode(NodeName).isActiveInterface((String)nics[i]) )
                                cmbInterface.addItem(nics[i]);
			}			
			cmbInterface.setEnabled(true);
			
			DefaultGWAddress = ((core.NetworkLayerDevice)Sim.getNode(NodeName)).getDefaultGateway();
			if(DefaultGWAddress != null){
				txtDefaultGW.setText(DefaultGWAddress);
			}
			txtDefaultGW.setEnabled(true);
			
		} catch (Exception e) {	//This should never happen 
			e.printStackTrace();
		}
	}
	
	/**
	 * This method enabled's the IP Address text field once the interface has been selected.
	 * This will also check the selected interface to see if the ip address has already been set
	 * if so entering that ip address into the text field and then getting the set subnet mask for that 
	 * ip address.
	 * @author luke_hamilton
	 */
	private void selectInterface(){
        //save IP and mask current interface
        if(!Interface.equals("")){
            if(IPAddress!=null && SubnetMask!=null) 
           	    ((NetworkLayerDevice)Sim.getNode(NodeName)).setTCPIPSettings(Interface, IPAddress, SubnetMask);
                
        }
                       
            //read IP and mask for selected interface
		txtIpAddress.setEnabled(true);
		Interface = (String)cmbInterface.getSelectedItem();
		
		if(Interface != null){
			try {				
				IPAddress = ((NetworkLayerDevice)Sim.getNode(NodeName)).getIPAddress(Interface);
				
				if(IPAddress != null){
					txtIpAddress.setText(IPAddress);
					SubnetMask =((core.NetworkLayerDevice)Sim.getNode(NodeName)).getSubnetMask(Interface);
                                        txtSubnetMask.setText(SubnetMask);
                                        txtSubnetMask.setEnabled(true);

				}else{
					txtIpAddress.setText("Enter IP Address");
					txtSubnetMask.setText("Enter Subnet Mask");
					txtSubnetMask.setEnabled(false);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}						
	}
	
	/**
	 * This method will validate the entered in IP Address
	 * and then display an error and shake the dialog if the enter ip 
	 * address is invalid.
	 * If the ip address is valid the subnet text field will the be validated.
	 * 
	 * @author luke_hamilton
	 *
	 */
	private void ipAddressEntered(){
		String ip = txtIpAddress.getText();
		
		if(!ip.equalsIgnoreCase("Enter IP Address")){
			if(!IPV4Address.validateDecIP(ip)){
				lblError.setText("Invalid IP Address");
				lblError.setForeground(Color.RED);
				lblError.setVisible(true);
				controller.shakeDiaLog(this);
				ErrorFlag = true;
			}else{
				lblError.setVisible(false);
				IPAddress = ip;
                                if(txtSubnetMask.getText().equals("Enter Subnet Mask") || txtSubnetMask.getText() == null || txtSubnetMask.getText().equals("0.0.0.0")){
                                    SubnetMask = IPV4Address.getDefaultSubnetMask(IPAddress);
                                    txtSubnetMask.setText(SubnetMask);
                                    txtSubnetMask.setEnabled(true);			
                                }
				ErrorFlag = false;			
			}		
		}		
	}
	
	/**
	 * This method validates the subnet mask agaest the ip address entered.
	 * If the entered in subnet mask is invalid an error message is displayed 
	 * to the dialog.
	 * @author luke_hamilton	 
	 */
	private void subnetmask(){		
		
		String subMask = txtSubnetMask.getText();

		lblError.setVisible(false);
		txtSubnetMask.setEnabled(true);
		SubnetMask = subMask;
		ErrorFlag = false;
	}
	
	/**
	 * This method check to see if the entered in Default Gateway 
	 * Address is a valid ip address. If not displaying an error message
	 * to the Dialog.
	 * @author luke_hamilton
	 *
	 */
	private void setDefaultGW(){
		String GW = txtDefaultGW.getText();
                if(!GW.equalsIgnoreCase("Enter Default Gateway")){
                        if(!IPV4Address.validateDecIP(GW) && !GW.equals("")){
				lblError.setText("Invalid Default Gateway Address");
				lblError.setForeground(Color.RED);
				lblError.setVisible(true);
				controller.shakeDiaLog(this);
				ErrorFlag = true;
			}else{
				lblError.setVisible(false);
                                GW = txtDefaultGW.getText();
                                if(GW.equals("")) GW = null;
				DefaultGWAddress = GW;
				ErrorFlag = false;
			}
		}				
	}
	
	/**
	 * 
	 * This method will check for a possible default subnet address as the user
	 * is typing in an ip address. Once one is found it will add it to the txtSubnetMak
	 * text field. 
	 * @author luke_hamilton
	 *
	 */
	private void checkForDefaultSubnet(){
		String tempip = txtIpAddress.getText();
		
		if(txtSubnetMask.getText().equals("Enter Subnet Mask") || txtSubnetMask.getText() == null || txtSubnetMask.getText().equals("0.0.0.0")){
			String tempSubnet = IPV4Address.getDefaultSubnetMask(tempip);
			txtSubnetMask.setText(tempSubnet);
                        
			txtSubnetMask.setEnabled(true);
        }
		
        btnOk.setEnabled(true);
		
	}

}


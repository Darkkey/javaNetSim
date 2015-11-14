/* 
Java Network Simulator (javaNetSim) 

Copyright (c) 2007, 2006, 2005, Ice Team;  All rights reserved.
Copyright (c) 2004, jFirewallSim development team;  All rights reserved. 
 
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

/*import core.InvalidLinkNameException;
import core.InvalidNetworkInterfaceNameException;
import core.InvalidNodeNameException;
import core.Node;
import core.NetworkLayerDevice;*/
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import core.Simulation;
import core.WANNetworkInterface;

/**
 * 
 * @author Key
 *

 * This class is a dialog that shows Serernet Port Props
 * 
 */
public class WANPortProperties extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7688514902767339887L;
	private JPanel backpanel;
	private JLabel lblInterface, cmbInterface;
	private JLabel lblNodeName, cmbNodeName;
        private JLabel lblError; 
        private JButton btnOk;
        
        private JLabel lblType; 
        private JComboBox cmbType; 
        
        private JLabel lblRole; 
        private JComboBox cmbRole; 
        
        private JLabel lblHost; 
        private JTextField txtHost;
        
        private JLabel lblPort; 
        private JTextField txtPort;
        
        private JLabel lblService;
        private JTextField txtService;
        
        private JLabel lblUP; 
        private JCheckBox chkUP;
	
	private Simulation Sim;
        private WANNetworkInterface wan;
	
	public WANPortProperties(JFrame frame, String inNodeName, String inNodeInt, Simulation Sim, SandBox SBox) {
		super(frame);
                
                try{
                    wan = (WANNetworkInterface)Sim.getNode(inNodeName).getNIC(inNodeInt);
                }catch(Exception e){
                    e.printStackTrace();
                }
                                
		setResizable(false);
		this.Sim = Sim;
                setTitle("WAN Port Properties");
		initGUI(inNodeName, inNodeInt);

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
                this.pack();
                
		this.getRootPane().setDefaultButton(btnOk);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setVisible(true);
		
                
	}
	
	private void initGUI(String NodeName, String NodeInt) {
		try {
			setSize(350, 325);
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
					lblType = new JLabel();
					backpanel.add(lblType, new GridBagConstraints(
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
					lblType.setText("Connection type:");
				}
                                {
					lblRole = new JLabel();
					backpanel.add(lblRole, new GridBagConstraints(
						0,  
						3, 
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 16),
						0,
						0));
					lblRole.setText("Connection role:");
				}
                                {
					lblHost = new JLabel();
					backpanel.add(lblHost, new GridBagConstraints(
						0,  
						4, 
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 16),
						0,
						0));
					lblHost.setText("Host/URL:");
				}
                                {
					lblPort = new JLabel();
					backpanel.add(lblPort, new GridBagConstraints(
						0,  
						5, 
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 16),
						0,
						0));
					lblPort.setText("Port:");
				}
                                {
					lblService = new JLabel();
					backpanel.add(lblService, new GridBagConstraints(
						0,  
						6, 
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 16),
						0,
						0));
					lblService.setText("Service:");
				}
                                {
					lblUP = new JLabel();
					backpanel.add(lblUP, new GridBagConstraints(
						0,  
						7, 
						1,
						1,
						0.0,
						1.0,
						GridBagConstraints.LINE_END,
						GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 16),
						0,
						0));
					lblUP.setText("Administratively UP:");
				}

                                {
                                    cmbNodeName = new JLabel();
                                    cmbNodeName.setMinimumSize(new Dimension(100, 0));
                                    cmbNodeName.setText(NodeName);
                                    final GridBagConstraints gridBagConstraints = new GridBagConstraints();
                                    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                                    gridBagConstraints.anchor = GridBagConstraints.LINE_START;
                                    gridBagConstraints.gridy = 0;
                                    gridBagConstraints.gridx = 1;
                                    backpanel.add(cmbNodeName, gridBagConstraints);
                                }
                                
                                {
                                    cmbInterface = new JLabel();
                                    cmbInterface.setText(NodeInt);				
                                    final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
                                    gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
                                    gridBagConstraints_1.anchor = GridBagConstraints.LINE_START;
                                    gridBagConstraints_1.gridy = 1;
                                    gridBagConstraints_1.gridx = 1;
                                    backpanel.add(cmbInterface, gridBagConstraints_1);
                                }
                                
                                cmbType = new JComboBox();
				                                
                                cmbType.setEnabled(true);
				final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
				gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_3.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_3.gridy = 2;
				gridBagConstraints_3.gridx = 1;
				backpanel.add(cmbType, gridBagConstraints_3);
                                cmbType.addItem("TCP Socket");
                                cmbType.addItem("UDP Socket");
                                //cmbType.addItem("RMI");
                                //cmbType.addItem("CORBA");
                                int sel = wan.getConnType() - 1;
                                if(sel<0) sel = 0;
                                cmbType.setSelectedIndex( sel );
                                
                                cmbRole = new JComboBox();
				                                
                                cmbRole.setEnabled(true);
				final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
				gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_4.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_4.gridy = 3;
				gridBagConstraints_4.gridx = 1;
				backpanel.add(cmbRole, gridBagConstraints_4);
                                cmbRole.addItem("Client");
                                cmbRole.addItem("Server");
                                if(wan.getServer()){
                                    cmbRole.setSelectedIndex(1);
                                }else{
                                    cmbRole.setSelectedIndex(0);
                                }
                                
                                txtHost = new JTextField();
                                txtHost.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {						
                                            lblError.setVisible(false);						
					}
				});
				txtHost.setEnabled(true);
				final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
				gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_5.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_5.gridy = 4;
				gridBagConstraints_5.gridx = 1;
                                backpanel.add(txtHost, gridBagConstraints_5);
				txtHost.setText(wan.getConnHost());
                                
                                txtPort = new JTextField();
                                txtPort.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {	                                            
                                            lblError.setVisible(false);						
					}
				});
				txtPort.setEnabled(true);
				final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
				gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_6.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_6.gridy = 5;
				gridBagConstraints_6.gridx = 1;
                                backpanel.add(txtPort, gridBagConstraints_6);
				txtPort.setText(String.valueOf(wan.getConnPort()));
                                
                                txtService = new JTextField();
                                txtService.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {						
                                            lblError.setVisible(false);						
					}
				});
				txtService.setEnabled(true);
				final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
				gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_7.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_7.gridy = 6;
				gridBagConstraints_7.gridx = 1;
                                backpanel.add(txtService, gridBagConstraints_7);
				txtService.setText(wan.getConnService());
                                       
                                chkUP = new JCheckBox();                                
                                chkUP.setEnabled(true);
				final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
				gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_8.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_8.gridy = 7;
				gridBagConstraints_8.gridx = 1;
                                backpanel.add(chkUP, gridBagConstraints_8);				
                                chkUP.setSelected(Sim.getNode(NodeName).getNIC(NodeInt).isUP());
                                
				lblError = new JLabel();
				lblError.setHorizontalTextPosition(SwingConstants.CENTER);
				lblError.setHorizontalAlignment(SwingConstants.CENTER);
				lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
				lblError.setMinimumSize(new Dimension(100, 20));
				lblError.setMaximumSize(new Dimension(100, 20));
				lblError.setPreferredSize(new Dimension(100, 20));
				lblError.setVisible(false);
                            
				final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
				gridBagConstraints_9.anchor = GridBagConstraints.WEST;
				gridBagConstraints_9.insets = new Insets(0, 1, 0, 0);
				gridBagConstraints_9.fill = GridBagConstraints.BOTH;
				gridBagConstraints_9.gridwidth = 2;
				gridBagConstraints_9.gridy = 5;
				gridBagConstraints_9.gridx = 0;
				backpanel.add(lblError, gridBagConstraints_9);
				lblError.setText("Invalid MAC!");
                                
                                cmbType.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectWANType();
                                                lblError.setVisible(false);
					}
				});
                                
                                cmbRole.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {						
                                            selectRole();
                                            lblError.setVisible(false);
					}
				});
                                                                
                                selectWANType();
                                
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
        private void selectRole(){
            switch(cmbType.getSelectedIndex() + 1){
                case WANNetworkInterface.RMI:
                    if(cmbRole.getSelectedIndex() == 1){
                        txtService.setText("");
                        txtService.setEnabled(false);
                        txtPort.setEnabled(false);   
                        txtPort.setText("1099");
                    }else{
                        txtService.setEnabled(true);
                        txtPort.setEnabled(true);                                        
                    }
                    break;
                case WANNetworkInterface.Corba:                    
                    if(cmbRole.getSelectedIndex() == 1){
                        txtService.setEnabled(false);
                        txtService.setText("");
                    }else
                        txtService.setEnabled(true);
                    break;    
            }
        }
        
        private void selectWANType(){
            switch(cmbType.getSelectedIndex() + 1){
                case WANNetworkInterface.SocketTCP:                    
                case WANNetworkInterface.SocketUDP:
                    cmbRole.setEnabled(true);
                    txtHost.setEnabled(true);
                    txtPort.setEnabled(true);
                    txtService.setEnabled(false);
                    txtService.setText("");
                    break;     
                    
                case WANNetworkInterface.RMI:
                    cmbRole.setEnabled(true);
                    if(cmbRole.getSelectedIndex() == 1){
                        txtService.setText("");
                        txtService.setEnabled(false);
                        txtPort.setEnabled(false);  
                        txtPort.setText("1099");
                    }else{
                        txtService.setEnabled(true);
                        txtPort.setEnabled(true);                                        
                    }
                    txtHost.setEnabled(true);                    
                    break;    
                case WANNetworkInterface.Corba:
                    cmbRole.setEnabled(true);
                    if(cmbRole.getSelectedIndex() == 1){
                        txtService.setText("");
                        txtService.setEnabled(false);
                    }else
                        txtService.setEnabled(true);
                    txtHost.setEnabled(true);
                    txtPort.setEnabled(true);                                        
                    break;    
            }
        }
        
	/**
	 * This is executed when the user hit's the enter button.	
	 * @author Key
	 *
	 */
	private void okButton(){
		try {		                    
                    if(!txtPort.getText().matches("[0-9]+")){
                        JOptionPane.showMessageDialog(this,"Invalid Port", "Error!", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    wan.setConnType(cmbType.getSelectedIndex() + 1);
                    wan.setConnHost(txtHost.getText());
                    wan.setConnPort(Integer.valueOf(txtPort.getText()));                            
                    wan.setServer( cmbRole.getSelectedIndex() == 1 );
                    wan.setConnType(cmbType.getSelectedIndex() + 1);
                    wan.setConnHost(txtHost.getText());
                    wan.setConnService(txtService.getText());                            
                     
                     if(wan.isUP()) wan.DOWN();
                  
                     if(chkUP.isSelected()){                        
                        wan.UP();
                     }
                     
                     this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is executed when the user hits the cancel button	 
         * @author Key
	 *
	 */
	private void cancelButton(){
		this.dispose();
	}	
}


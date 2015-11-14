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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import core.DeviceConfig;
import core.NetworkLayerDevice;
import core.Simulation;

/**
 * 
 * @author Key
 * 
 * 
 * This class is a dialog that shows Ethernet Port Props
 * 
 */
public class WiFiPortProperties extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8725131940909728774L;

	private JPanel backpanel;

	private JLabel lblInterface, cmbInterface;

	private JLabel lblNodeName, cmbNodeName;

	private JLabel lblError;

	private JButton btnOk;

	private JTextField txtMAC;

	private JLabel lblMAC;

	private JTextField txtSSID;

	private JLabel lblSSID;

	private JTextField txtWEPKey;

	private JLabel lblWEPKey;

	private JLabel lblAuthMode;

	private JComboBox cmbAuthMode;

	private JLabel lblChannel;

	private JComboBox cmbChannel;
	
	private JLabel lblMode;

	private JComboBox cmbMode;


	private JLabel lblUP;

	private JCheckBox chkUP;

	private MainScreen controller;

	private Simulation Sim;

	private String NodeName = "";

	private String NodeInt = "";

	public WiFiPortProperties(JFrame frame, String inNodeName,
			String inNodeInt, Simulation Sim) {
		super(frame);

		this.NodeName = inNodeName;
		this.NodeInt = inNodeInt;
		this.Sim = Sim;

		setResizable(false);
		controller = (MainScreen) frame;
		setTitle("WiFi Port Properties");
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
				backpanel.setPreferredSize(new java.awt.Dimension(264, 253));
				backpanelLayout.columnWeights = new double[] {};
				backpanelLayout.columnWidths = new int[] {};
				backpanelLayout.rowWeights = new double[] { 0.0 };
				backpanelLayout.rowHeights = new int[] { 5, 5, 5, 5 };
				backpanel.setLayout(backpanelLayout);
				{
					lblNodeName = new JLabel();
					backpanel.add(lblNodeName, new GridBagConstraints(0, 0, 1,
							1, 0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									0), 21, 0));
					lblNodeName.setText("Node Name:");
				}
				{
					lblInterface = new JLabel();
					backpanel.add(lblInterface, new GridBagConstraints(0, 1, 1,
							1, 0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblInterface.setText("Interface:");
				}

				{
					lblMAC = new JLabel();
					backpanel.add(lblMAC, new GridBagConstraints(0, 2, 1, 1,
							0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									16), 0, 0));
					lblMAC.setText("MAC Address:");
				}

				{
					lblChannel = new JLabel();
					backpanel.add(lblChannel, new GridBagConstraints(0, 3, 1,
							1, 0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblChannel.setText("Channel:");
				}
				{
					lblMode = new JLabel();
					backpanel.add(lblMode, new GridBagConstraints(0, 4, 1,
							1, 0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblMode.setText("Mode:");
				}
				
				{
					lblSSID = new JLabel();
					backpanel.add(lblSSID, new GridBagConstraints(0, 5, 1, 1,
							0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblSSID.setText("SSID:");
				}
				{
					lblAuthMode = new JLabel();
					backpanel.add(lblAuthMode, new GridBagConstraints(0, 6, 1,
							1, 0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblAuthMode.setText("Authentication:");
				}
				{
					lblWEPKey = new JLabel();
					backpanel.add(lblWEPKey, new GridBagConstraints(0, 7, 1, 1,
							0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblWEPKey.setText("WEP Key(1,40b):");
				}

				{
					lblUP = new JLabel();
					backpanel.add(lblUP, new GridBagConstraints(0, 8, 1, 1,
							0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									16), 0, 0));
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

				txtMAC = new JTextField();
				txtMAC.addFocusListener(new FocusAdapter() {
					public void actionPerformed(ActionEvent e) {
						lblError.setVisible(false);
					}
				});

				txtMAC.setEnabled(true);
				final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
				gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_3.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_3.gridy = 2;
				gridBagConstraints_3.gridx = 1;
				backpanel.add(txtMAC, gridBagConstraints_3);

				txtMAC.setText(Sim.getNode(NodeName).getMACAddress(NodeInt));
				
							
				cmbChannel = new JComboBox();
				cmbChannel.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
				gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_6.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_6.gridy = 3;
				gridBagConstraints_6.gridx = 1;
				backpanel.add(cmbChannel, gridBagConstraints_6);
				
				for(int i = 0; i<=15; i++)
					cmbChannel.addItem(i);
				
				cmbChannel.setSelectedIndex(((core.WiFiPort)Sim.getNode(NodeName).getNetworkInterface(NodeInt)).getChannel());
				
				cmbMode = new JComboBox();
				cmbMode.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
				gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_12.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_12.gridy = 4;
				gridBagConstraints_12.gridx = 1;
				backpanel.add(cmbMode, gridBagConstraints_12);
				
				if(!(Sim.getNode(NodeName) instanceof core.WirelessAP)) cmbMode.addItem("Station");
				if(Sim.getNode(NodeName) instanceof core.Router || Sim.getNode(NodeName) instanceof core.WirelessAP)
					cmbMode.addItem("AP");
				
				if(((core.WiFiPort)Sim.getNode(NodeName).getNetworkInterface(NodeInt)).getMode() == core.WiFiPort.MODE_AP){
					int newModeIndex=-1;
					for(int i=0; i<cmbMode.getComponentCount() && newModeIndex==-1; i++){
						if(cmbMode.getItemAt(i).equals("AP")) newModeIndex=i;
					}
					if(newModeIndex>=0) cmbMode.setSelectedIndex(newModeIndex);
				}
				
				txtSSID = new JTextField();
				/*txtSSID.addFocusListener(new FocusAdapter() {
					public void actionPerformed(ActionEvent e) {
						//lblError.setVisible(false);
					}
				});*/

				txtSSID.setEnabled(true);
				final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
				gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_9.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_9.gridy = 5;
				gridBagConstraints_9.gridx = 1;
				backpanel.add(txtSSID, gridBagConstraints_9);
				
				txtSSID.setText(((core.WiFiPort)Sim.getNode(NodeName).getNetworkInterface(NodeInt)).getSSID());
				
				cmbAuthMode = new JComboBox();
				cmbAuthMode.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
				gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_8.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_8.gridy = 6;
				gridBagConstraints_8.gridx = 1;
				backpanel.add(cmbAuthMode, gridBagConstraints_8);
				
				cmbAuthMode.addItem("Open");
				cmbAuthMode.addItem("Shared");
				
				if(((core.WiFiPort)Sim.getNode(NodeName).getNetworkInterface(NodeInt)).isSharedAuth())
					cmbAuthMode.setSelectedIndex(1);
				
				txtWEPKey = new JTextField();
				/*txtWEPKey.addFocusListener(new FocusAdapter() {
					public void actionPerformed(ActionEvent e) {
						//lblError.setVisible(false);
					}
				});*/

				txtWEPKey.setEnabled(true);
				final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
				gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_10.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_10.gridy = 7;
				gridBagConstraints_10.gridx = 1;
				backpanel.add(txtWEPKey, gridBagConstraints_10);
				
				txtWEPKey.setText(((core.WiFiPort)Sim.getNode(NodeName).getNetworkInterface(NodeInt)).getWEPKey(1));

				chkUP = new JCheckBox();
				chkUP.setEnabled(true);
				final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
				gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_4.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_4.gridy = 8;
				gridBagConstraints_4.gridx = 1;
				backpanel.add(chkUP, gridBagConstraints_4);
				chkUP.setSelected(Sim.getNode(NodeName).getNIC(NodeInt).isUP());

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
				gridBagConstraints_5.gridy = 9;
				gridBagConstraints_5.gridx = 0;
				backpanel.add(lblError, gridBagConstraints_5);
				lblError.setText("Invalid MAC!");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is executed when the user hit's the enter button. It will
	 * delete the link on selected interface.
	 * 
	 * @author Key
	 * 
	 */
	private void okButton() {
		try {
			String SC = txtMAC.getText();
			if (!SC.matches("[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]")) {
				Error("Invalid MAC Address!");
				return;
			}
			
			if(txtSSID.getText().length() < 2){
				Error("Invalid SSID!");
			}
							
			NetworkLayerDevice tmpNode = (NetworkLayerDevice) Sim.getNode(NodeName);
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " shutdown");
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " mac-address " + SC);
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " ssid " + txtSSID.getText());
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " channel " + cmbChannel.getSelectedIndex());
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " encryption key 1 size 40bit  " + txtWEPKey.getText());
			
			if(cmbAuthMode.getSelectedIndex() == 0)
				tmpNode.getConfig().executeCommand(
						"int " + NodeInt + " authentication open");
			else
				tmpNode.getConfig().executeCommand(
						"int " + NodeInt + " authentication shared");
			
			if(cmbMode.getSelectedItem().equals("Station")){
				tmpNode.getConfig().executeCommand(
						"int " + NodeInt + " station-role client");
			}
			else{
				tmpNode.getConfig().executeCommand(
						"int " + NodeInt + " station-role root access-point");
			}
			
			if (chkUP.isSelected()) {
				tmpNode.getConfig().executeCommand(
						"no int " + NodeInt + " shutdown");
			} else {
				tmpNode.getConfig().executeCommand(
						"int " + NodeInt + " shutdown");
			}
			tmpNode.getConfig().executeCommand("write mem");

				this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is executed when the user hits the cancel button
	 * 
	 * @author luke_hamilton
	 * @author Key
	 * 
	 */
	private void cancelButton() {
		this.dispose();
	}
	
	private void Error(String txt){
		lblError.setText(txt);
		lblError.setForeground(Color.RED);
		lblError.setVisible(true);
		controller.shakeDiaLog(this);
	}
}

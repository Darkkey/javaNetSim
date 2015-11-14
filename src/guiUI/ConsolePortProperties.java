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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

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
public class ConsolePortProperties extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8725131940909728774L;

	private JPanel backpanel;

	private JLabel lblInterface, cmbInterface;

	private JLabel lblNodeName, cmbNodeName;

	private JLabel lblError;

	private JButton btnOk;

	private JLabel lblUP;
	private JCheckBox chkUP;
	
	private JLabel lblBPS;
	private JComboBox cmbBPS;

	private JLabel lblDataBits;
	private JComboBox cmbDataBits;

	private JLabel lblParity;
	private JComboBox cmbParity;

	private JLabel lblStopBits;
	private JComboBox cmbStopBits;

	private JLabel lblFlowControl;
	private JComboBox cmbFlowControl;

	private MainScreen controller;

	private Simulation Sim;

	private String NodeName = "";

	private String NodeInt = "";

	public ConsolePortProperties(JFrame frame, String inNodeName,
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
					lblBPS = new JLabel();
					backpanel.add(lblBPS, new GridBagConstraints(0, 2, 1, 1,
							0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									16), 0, 0));
					lblBPS.setText("Bits per second:");
				}

				{
					lblDataBits = new JLabel();
					backpanel.add(lblDataBits, new GridBagConstraints(0, 3, 1,
							1, 0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblDataBits.setText("Data Bits:");
				}
				{
					lblParity = new JLabel();
					backpanel.add(lblParity, new GridBagConstraints(0, 4, 1,
							1, 0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblParity.setText("Parity:");
				}
				
				{
					lblStopBits = new JLabel();
					backpanel.add(lblStopBits, new GridBagConstraints(0, 5, 1, 1,
							0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblStopBits.setText("Stop Bits:");
				}
				
				{
					lblFlowControl = new JLabel();
					backpanel.add(lblFlowControl, new GridBagConstraints(0, 6, 1, 1,
							0.0, 1.0, GridBagConstraints.LINE_END,
							GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
									27), 0, 0));
					lblFlowControl.setText("Flow Control:");
				}
				
				{
					lblUP = new JLabel();
					backpanel.add(lblUP, new GridBagConstraints(0, 7, 1, 1,
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
				
				core.ConsoleNetworkInterface cint = 
					(core.ConsoleNetworkInterface)Sim.getNode(NodeName).getNetworkInterface(NodeInt);

				cmbBPS = new JComboBox();
				cmbBPS.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
				gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_6.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_6.gridy = 2;
				gridBagConstraints_6.gridx = 1;
				backpanel.add(cmbBPS, gridBagConstraints_6);
				
				cmbBPS.addItem(110); cmbBPS.addItem(300);
				cmbBPS.addItem(1200); cmbBPS.addItem(2400);
				cmbBPS.addItem(4800); cmbBPS.addItem(9600);
				cmbBPS.addItem(19200); cmbBPS.addItem(38400);
				cmbBPS.addItem(57600); cmbBPS.addItem(115200);
				cmbBPS.addItem(230400); cmbBPS.addItem(460800);
				cmbBPS.addItem(921600);
				
				for(int i=0; i<cmbBPS.getItemCount(); i++)
					if((Integer)(cmbBPS.getItemAt(i)) == cint.speed){
						cmbBPS.setSelectedIndex(i);
						break;
					}

				cmbDataBits = new JComboBox();
				cmbDataBits.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
				gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_12.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_12.gridy = 3;
				gridBagConstraints_12.gridx = 1;
				backpanel.add(cmbDataBits, gridBagConstraints_12);
				
				cmbDataBits.addItem(5); cmbDataBits.addItem(6);
				cmbDataBits.addItem(7); cmbDataBits.addItem(8);
				
				cmbDataBits.setSelectedIndex( cint.databits - 5 );
				
				//if(((core.WiFiPort)Sim.getNode(NodeName).getNetworkInterface(NodeInt)).getMode() == core.WiFiPort.MODE_AP)
				//	cmbMode.setSelectedIndex(1);
				
				cmbParity = new JComboBox();
				cmbParity.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
				gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_13.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_13.gridy = 4;
				gridBagConstraints_13.gridx = 1;
				backpanel.add(cmbParity, gridBagConstraints_13);
				
				cmbParity.addItem("none"); cmbParity.addItem("even");
				cmbParity.addItem("odd"); cmbParity.addItem("marker");
				cmbParity.addItem("space");
				
				cmbParity.setSelectedIndex(cint.parity - 1);
				
				//if(((core.WiFiPort)Sim.getNode(NodeName).getNetworkInterface(NodeInt)).getMode() == core.WiFiPort.MODE_AP)
				//	cmbMode.setSelectedIndex(1);
				
				cmbStopBits = new JComboBox();
				cmbStopBits.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
				gridBagConstraints_14.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_14.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_14.gridy = 5;
				gridBagConstraints_14.gridx = 1;
				backpanel.add(cmbStopBits, gridBagConstraints_14);
				
				cmbStopBits.addItem("1"); cmbStopBits.addItem("1.5");
				cmbStopBits.addItem("2"); 
				
				cmbParity.setSelectedIndex(cint.stopbits - 1);
				
				cmbFlowControl = new JComboBox();
				cmbFlowControl.setEnabled(true);
				
				final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
				gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_15.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_15.gridy = 6;
				gridBagConstraints_15.gridx = 1;
				backpanel.add(cmbFlowControl, gridBagConstraints_15);
				
				cmbFlowControl.addItem("none"); 
				cmbFlowControl.addItem("hardware"); cmbFlowControl.addItem("software");
				
				cmbParity.setSelectedIndex(cint.flowcontrol - 1);
				
				chkUP = new JCheckBox();
				chkUP.setEnabled(true);
				final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
				gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
				gridBagConstraints_4.anchor = GridBagConstraints.LINE_START;
				gridBagConstraints_4.gridy = 7;
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
				gridBagConstraints_5.gridy = 8;
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
			/*String SC = txtMAC.getText();
			if (!SC.matches("[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]")) {
				Error("Invalid MAC Address!");
				return;
			}
			
			if(txtSSID.getText().length() < 2){
				Error("Invalid SSID!");
			}*/
							
			NetworkLayerDevice tmpNode = (NetworkLayerDevice) Sim.getNode(NodeName);
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " shutdown");
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " speed " + cmbBPS.getSelectedItem());
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " databits " + cmbDataBits.getSelectedItem());

			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " parity " + cmbParity.getSelectedItem());
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " stopbits " + cmbStopBits.getSelectedItem());
			
			tmpNode.getConfig().executeCommand(
					"int " + NodeInt + " flowcontrol " + cmbFlowControl.getSelectedItem());
				
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

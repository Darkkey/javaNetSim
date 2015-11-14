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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Creates a PingDialog which extends JDialog.
 * @author Team VC2
 */

public class PingDialog extends JDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1088992961468735310L;
	private JLabel lblMessage = new JLabel("Send Ping");
	private JLabel lblSourceLabel = new JLabel("Source :");
	private JLabel lblDestinationLabel = new JLabel("Destination IP :");
	private JTextField txtDestinationIP = new JTextField(15);
	private JButton btnOK = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");
	private JLabel lblNodeName = new JLabel();

	private JPanel pnlMessage = new JPanel();
	private JPanel pnlSourceInformation = new JPanel();
	private JPanel pnlDestinationInformation = new JPanel();
	private JPanel pnlButtons = new JPanel();
	
	 private boolean wasOkPressed = false;

	public PingDialog(String inNodeName)
	{

		lblNodeName.setText(inNodeName);
		JPanel pnlSendPingScreen = (JPanel)this.getContentPane();
		pnlSendPingScreen.setLayout(new BoxLayout(pnlSendPingScreen, BoxLayout.Y_AXIS));
		pnlMessage.setLayout(new FlowLayout());
		pnlSourceInformation.setLayout(new FlowLayout());
		pnlDestinationInformation.setLayout(new FlowLayout());
		pnlButtons.setLayout(new FlowLayout());
				
		pnlMessage.setPreferredSize(new Dimension(50,50));
		
		pnlMessage.add(lblMessage);
		pnlSourceInformation.add(lblSourceLabel);
		pnlSourceInformation.add(lblNodeName);
		pnlDestinationInformation.add(lblDestinationLabel);
		pnlDestinationInformation.add(txtDestinationIP);
		pnlButtons.add(btnOK);
		pnlButtons.add(btnCancel);
		
		pnlSendPingScreen.add(pnlMessage);
		pnlSendPingScreen.add(pnlSourceInformation);
		pnlSendPingScreen.add(pnlDestinationInformation);
		pnlSendPingScreen.add(pnlButtons);
		
		
		// ActionListeners for our buttons.
		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);
		this.setSize(400,175);
		this.setLocationRelativeTo(null);
	    this.setModal(true);
	    this.setResizable(false);
	    lblDestinationLabel.setLabelFor(this.txtDestinationIP);
	    this.lblDestinationLabel.requestFocus();
	    this.txtDestinationIP.requestFocus();
	    this.txtDestinationIP.requestFocus(true);
	    this.getRootPane().setDefaultButton(btnOK);	   
	    
	  
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btnOK)
		{
			String strDestinationIP = txtDestinationIP.getText();
			strDestinationIP = strDestinationIP.trim();
			
			if(!strDestinationIP.equals(""))
			{			
				wasOkPressed = true;
				//this.hide();
                                this.setVisible(false);
			}
			else
			{
				// Throw warning dialog for empty Destination IP textfield
				JOptionPane.showInternalMessageDialog(this.getContentPane(), 
						 "Destination IP Address can not be empty", "Destination IP Address Warning", 
						 JOptionPane.WARNING_MESSAGE);
			}
		}
		if(e.getSource() == btnCancel)
		{
			this.dispose();
		}
	}
	
	/**
	 * This method returns the status of if the OK Button was pressed
	 * @return Boolean wasOKPressed True if the OK button was pressed.
	 */
	
	public boolean getWasOKPressed(){
		return wasOkPressed;
		
	}
	
	/**
	 * Returns the contents of the TextField for the DestinationIP address as a String
	 * @return String strDestinationIP The Destination IP Address
	 */
	
	public String getDestinationIPAddress()
	{
		return txtDestinationIP.getText();
	}
	
	

	
}

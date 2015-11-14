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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import core.Simulation;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.SNMP;

/**
 *
 * @author QweR
 *
 * This class is a dialog that enables the user to be able to set the TCP/IP setting
 * on a selected node. eg: set IP address, subnet mask and default gateway address.
 *
 */
public class SNMPSendDataDialog extends javax.swing.JDialog {
     /**
	 * 
	 */
	private static final long serialVersionUID = 2769275985951166051L;
	private JPanel backpanel;
     private JLabel lblIPAddress;
     private JLabel lblPort;
     private JLabel lblMessage;
     private JLabel lblVariables;
     private JLabel lblPassword;
     private JLabel lblError;
     private JTextField txtIPAddress;
     private JTextField txtPort;
     private JTextField txtMessage;
     private JTextField txtVariables;
     private JTextField txtPassword;
     private JButton btnOk;

     private MainScreen controller;
     private Simulation Sim;

     private String NodeName;

     public SNMPSendDataDialog(JFrame frame, Simulation s, String inNodeName) {
          super(frame);
          setResizable(false);
          controller = (MainScreen)frame;
          Sim = s;
          NodeName = inNodeName;
          setTitle("SNMP Send Data");
          initGUI();

          final JPanel panel = new JPanel();
          getContentPane().add(panel, BorderLayout.SOUTH);

          btnOk = new JButton();
          btnOk.setEnabled(true);
          btnOk.setToolTipText("Send SNMP message");
          btnOk.setName("btnOK");
          btnOk.setText("OK");
          btnOk.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                    okButton();
               }
          });
          panel.add(btnOk);

          final JButton btnCancel = new JButton();
          btnCancel.setToolTipText("Cancel changes");
          btnCancel.setName("btnCancel");
          btnCancel.setText("Cancel");
          btnCancel.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                    cancelButton();
               }
          });
          panel.add(btnCancel);
          
          this.getRootPane().setDefaultButton(btnOk);
          this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
          //this.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
          this.setLocationRelativeTo(null);
          this.setModal(true);
          this.setVisible(true);

     }

     private void initGUI() {
        try {
            setSize(500, 225);
            backpanel = new JPanel();
            //backpanel.setMinimumSize(new Dimension(400, 100));
            this.getContentPane().add(backpanel, BorderLayout.CENTER);
            GridBagLayout backpanelLayout = new GridBagLayout();
            //backpanel.setPreferredSize(new Dimension(800, 200));
            backpanelLayout.columnWeights = new double[] {};
            backpanelLayout.columnWidths = new int[] {100,100,250};
            backpanelLayout.rowWeights = new double[] {0.0};
            backpanelLayout.rowHeights = new int[] {5,5,5,5,5};
            backpanel.setLayout(backpanelLayout);
            
// ***** IP address *****            
            lblIPAddress = new JLabel();
            lblIPAddress.setText("IP Address:");
            backpanel.add(lblIPAddress, new GridBagConstraints(0,0,1,1,0.0,1.0,GridBagConstraints.LINE_END,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
            txtIPAddress = new JTextField();
            txtIPAddress.setEnabled(true);
            backpanel.add(txtIPAddress, new GridBagConstraints(1,0,1,1,0.0,1.0,GridBagConstraints.LINE_START,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
//            txtIPAddress.addFocusListener(new FocusAdapter() {
//                    public void focusLost(FocusEvent e) {
//                            lblError.setVisible(false);
//                    }
//            });
            
// ***** Port *****   
            lblPort = new JLabel();
            lblPort.setText("Destination port:");
            backpanel.add(lblPort, new GridBagConstraints(0,1,1,1,0.0,1.0,GridBagConstraints.LINE_END,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
            txtPort = new JTextField();
            txtPort.setEnabled(true);
            backpanel.add(txtPort, new GridBagConstraints(1,1,1,1,0.0,1.0,GridBagConstraints.LINE_START,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
            
// ***** Message *****   
            lblMessage = new JLabel();
            lblMessage.setText("SNMP message");
            backpanel.add(lblMessage, new GridBagConstraints(0,2,1,1,0.0,1.0,GridBagConstraints.LINE_END,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
            txtMessage = new JTextField();
            txtMessage.setEnabled(true);
            backpanel.add(txtMessage, new GridBagConstraints(1,2,1,1,0.0,1.0,GridBagConstraints.LINE_START,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
                                    
// ***** Variables *****   
            lblVariables = new JLabel();
            lblVariables.setText("Variables");
            backpanel.add(lblVariables, new GridBagConstraints(0,3,1,1,0.0,1.0,GridBagConstraints.LINE_END,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
            txtVariables = new JTextField();
            txtVariables.setEnabled(true);
            backpanel.add(txtVariables, new GridBagConstraints(1,3,2,1,0.0,1.0,GridBagConstraints.LINE_START,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));                                    

// ***** Password *****   
            lblPassword = new JLabel();
            lblPassword.setText("Community name");
            backpanel.add(lblPassword, new GridBagConstraints(0,4,1,1,0.0,1.0,GridBagConstraints.LINE_END,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
            txtPassword = new JTextField();
            txtPassword.setEnabled(true);
            backpanel.add(txtPassword, new GridBagConstraints(1,4,1,1,0.0,1.0,GridBagConstraints.LINE_START,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));

// ***** ERROR *****   
            lblError = new JLabel();
            lblError.setVisible(false);
            lblError.setForeground(Color.RED);
            lblError.setText("                                 ");
            backpanel.add(lblError, new GridBagConstraints(0,5,2,1,0.0,1.0,GridBagConstraints.LINE_END,
                            GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 16),0,0));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
     }

     /**
      * This method is executed when the user hit's the enter button.
      * It will then test if the IP address can be set, or if the subnetmask
      * can be set and if the default gateway can be set.
      *
      * @author QweR
      *
      */
     private void okButton(){
        try {
            if(txtIPAddress.getText()==null || txtIPAddress.getText().compareTo("")==0) {
                lblError.setText("Enter IP address!");
                lblError.setVisible(true);
            }
            else if(txtPort.getText()==null || txtPort.getText().compareTo("")==0) {
                lblError.setText("Enter port!");
                lblError.setVisible(true);
            }
            else if(txtMessage.getText()==null || txtMessage.getText().compareTo("")==0) {
                lblError.setText("Enter SNMP message!");
                lblError.setVisible(true);
            }
            else if(txtVariables.getText()==null || txtVariables.getText().compareTo("")==0) {
                lblError.setText("Enter varibles!");
                lblError.setVisible(true);
            }
            else if(txtPassword.getText()==null || txtPassword.getText().compareTo("")==0) {
                lblError.setText("Enter community name!");
                lblError.setVisible(true);
            }
            else {
                Vector<String> vars = new Vector<String>(0);
                Vector<String> vals = new Vector<String>(0);
                String ip = IPV4Address.sanitizeDecIPorName(txtIPAddress.getText());
                if(parseVariablesList(txtVariables.getText(),vars,vals)) {
                    String s = txtMessage.getText().toLowerCase();
                    if(s.compareTo("get")==0) {
                        controller.addToConsole("Trying to send get-Request with variables: {" + txtVariables.getText() + "} from " + NodeName + " to " + ip +  ":" + txtPort.getText() + "\n");
                        if(!((SNMP)((core.ApplicationLayerDevice)Sim.getNode(NodeName)).getApp(30161)).getRequest(ip, Integer.valueOf(txtPort.getText()).intValue(), vars, txtPassword.getText())) {
                            controller.addToConsole("Message '" + txtMessage.getText() + "' from " + NodeName + " to " + ip +  ":" + txtPort.getText() + " not sent\n");
                        }
                        this.dispose();
                    }
                    else if(s.compareTo("getnext")==0) {
                        controller.addToConsole("Trying to send getnext-Request with variables: {" + txtVariables.getText() + "} from " + NodeName + " to " + ip +  ":" + txtPort.getText() + "\n");
                        if(!((SNMP)((core.ApplicationLayerDevice)Sim.getNode(NodeName)).getApp(30161)).getNextRequest(ip, Integer.valueOf(txtPort.getText()).intValue(), vars, txtPassword.getText())) {
                            controller.addToConsole("Message '" + txtMessage.getText() + "' from " + NodeName + " to " + ip +  ":" + txtPort.getText() + " not sent\n");
                        }
                        this.dispose();
                    }
                    else if(s.compareTo("set")==0) {
                        controller.addToConsole("Trying to send set-Request with variables: {" + txtVariables.getText() + "} from " + NodeName + " to " + ip +  ":" + txtPort.getText() + "\n");
                        if(!((SNMP)((core.ApplicationLayerDevice)Sim.getNode(NodeName)).getApp(30161)).setRequest(ip, Integer.valueOf(txtPort.getText()).intValue(), vars, vals, txtPassword.getText())) {
                            controller.addToConsole("Message '" + txtMessage.getText() + "' from " + NodeName + " to " + ip +  ":" + txtPort.getText() + " not sent\n");
                        }
                        this.dispose();
                    }
                    else {
                        lblError.setText("unsupported SNMP message!");
                        lblError.setVisible(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     /**
      * This method is executed when the user hits the cancel button
      * @author QweR
      *
      */
     private void cancelButton(){
          this.dispose();
     }
     
     private boolean parseVariablesList(String s, Vector<String> vars, Vector<String> vals) {
         vars.clear();
         vals.clear();
         
         String[] onevar = s.split(";");
         for(int i=0;i<onevar.length;i++) {
             String[] var_value = onevar[i].split("=");
             if(var_value.length==1) {
                 String vari = deleteSpaces(var_value[0]);
                 if(var_value[0].compareTo("")!=0) {
                     vars.add(vari);
                 }
             }
             else if(var_value.length==2) {
                 String vari = deleteSpaces(var_value[0]);
                 String valuei = deleteSpaces(var_value[1]);
                 if(valuei.startsWith("\"") && valuei.endsWith("\"")) {
                     vars.add(vari);
                     vals.add(valuei.substring(1, valuei.length()-1));
                 }
                 else return false;
             }
             else return false;
         }                     
         return true;
     }
     
     private String deleteSpaces(String s) {
         while(s.startsWith(" ")) s = s.substring(1);
         while(s.endsWith(" ")) s = s.substring(0, s.length()-1);
         return s;
     }
}

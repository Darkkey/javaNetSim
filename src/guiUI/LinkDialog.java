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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *	Creates a dialog that allows the user to create a Link between 2 nodes
 *	Extends JDialog and implements ActionListener
 *	All JPanels and associated buttons are instantiated here.
 * @author Team VC2 
 */

class LinkDialog extends JDialog implements ActionListener 
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2863636695410784080L;
	private JButton btnCancel = new JButton("Cancel");
	private JButton btnOK = new JButton("OK");  
    JPanel pnlLinkInfo = new JPanel();
    JPanel pnlMessage = new JPanel();
    JPanel pnlInterfaceHolder = new JPanel();
    JPanel pnlFirstNode =  new JPanel();
    JPanel pnlSecondNode = new JPanel();
    JPanel pnlButtons = new JPanel();    
    JTextField txtLinkName = new JTextField(15);
    JComboBox cboFirstNode = new JComboBox();
    JComboBox cboSecondNode = new JComboBox();  
    String aryFirstNode[];
    private boolean wasOkPressed = false;
    
    
    /**
     *	Constructs a Link Dialog.
     *	@param String inPC1 The name of the first PC
     *	@param String inPC2 The name of the second PC
     *	@param String[] inFirstAvail String array of the available interfaces of PC1
     *	@param String[] inSecondAvail String array of the available interfaces of PC2
     * @author Team VC2 
     */
    
    public LinkDialog(String inPC1, String inPC2, String[] inFirstAvail, String[] inSecondAvail)
    {
		pnlFirstNode.setLayout(new BorderLayout());
	    pnlSecondNode.setLayout(new BorderLayout());
	    
	    pnlInterfaceHolder.setLayout(new BoxLayout(pnlInterfaceHolder, BoxLayout.X_AXIS));
	    pnlLinkInfo.setLayout(new FlowLayout());
	    JPanel pnlLinkScreen = (JPanel)this.getContentPane();
	    pnlLinkScreen.setLayout(new BoxLayout(pnlLinkScreen, BoxLayout.Y_AXIS));
	    pnlButtons.setLayout(new FlowLayout());	    		
	   
	    
	    btnCancel.addActionListener(this);
	    btnOK.addActionListener(this);
	    JLabel lblFirstNode = new JLabel();
	    JLabel lblSecondNode = new JLabel(inPC2);	       	 	 		
	    JLabel lblMessage = new JLabel("Please select the interface card for the specific nodes");
	    JLabel lblNameLink = new JLabel("Name of Link : ");
	    lblFirstNode.setHorizontalAlignment(SwingConstants.CENTER);
	    lblSecondNode.setHorizontalAlignment(SwingConstants.CENTER);
	    lblMessage.setHorizontalAlignment(SwingConstants.CENTER);    
	   
	    txtLinkName.setPreferredSize(new Dimension(80, 25));
	    
	    txtLinkName.setText(inPC1 + "-TO-" + inPC2);
            txtLinkName.setVisible(false);
	    lblFirstNode.setText(inPC1);
	    
	     pnlMessage.add(lblMessage);
	     pnlLinkScreen.add(pnlMessage);
	     //pnlLinkInfo.add(lblNameLink);
	     //pnlLinkInfo.add(txtLinkName);	     
	     pnlLinkScreen.add(pnlLinkInfo);
	     pnlFirstNode.add(lblFirstNode, BorderLayout.NORTH);
	    
	     Arrays.sort(inFirstAvail);
	     Arrays.sort(inSecondAvail);
	     cboFirstNode.setModel(new DefaultComboBoxModel(inFirstAvail));
	     pnlFirstNode.add(cboFirstNode, BorderLayout.SOUTH);			 
	     pnlInterfaceHolder.add(pnlFirstNode);
	     pnlSecondNode.add(lblSecondNode, BorderLayout.NORTH);
	     cboSecondNode.setModel(new DefaultComboBoxModel(inSecondAvail));
	     pnlSecondNode.add(cboSecondNode, BorderLayout.SOUTH);	     
	     pnlInterfaceHolder.add(pnlSecondNode);	     
	     pnlLinkScreen.add(pnlInterfaceHolder);
	     pnlButtons.add(btnOK);
	     pnlButtons.add(btnCancel);
	     pnlLinkScreen.add(pnlButtons);
	     
	     	       		       	 		       	 		       	 		       		
	     this.setSize(400,175);
	     this.setLocationRelativeTo(null);
	     this.setModal(true);
	     this.setResizable(false);
	     this.getRootPane().setDefaultButton(btnOK);	
	     btnOK.setToolTipText("Create the current link");
	     btnCancel.setToolTipText("Cancel the creation of the link");
	    			    			       	   		
}
    
    /**
     *	Returns the name of the LinkName from the TextField
     *	@return String LinkName The name of the Link
     * @author Team VC2 
     */
        
    public String getLinkName()
    {
    	return txtLinkName.getText();
    }

    /**
     *	Returns the name of the name of the selected interface
     *	@return String FirstInterface The name of the first interface
     * @author Team VC2 
     */
    
    public String getFirstSelectedInterface()
    {
    	return (String)cboFirstNode.getSelectedItem();
    }
    
    /**
     *	Returns the name of the second of the selected interface
     *	@return String FirstInterface The name of the second interface
     * @author Team VC2 
     */

    public String getSecondSelectedInterface()
	{
    	return (String)cboSecondNode.getSelectedItem();
	}

    /**
     *	Returns the status of if the OK button on the Link Dialog was pressed.
     *	@return Boolean OkPressed True if the OK button was pressed.
     * @author Team VC2 
     */
    
    public boolean getWasOKPressed()
    {
    	return wasOkPressed;	
    }

		
public void actionPerformed(ActionEvent e)
{
	if(e.getSource() == btnOK)
	{
		String strLinkName = txtLinkName.getText();
		strLinkName = strLinkName.trim();
		if(!strLinkName.equals(""))
		{			
			wasOkPressed = true;
			//this.hide();
                        this.setVisible(false);
		}
		else
		{
			// Pop up message box because the link name field is empty 
			JOptionPane.showInternalMessageDialog(this.getContentPane(), 
								 "Link Name can not be empty", "Link Name Error", 
								 JOptionPane.WARNING_MESSAGE);
		}								
	}
	
	if(e.getSource() == btnCancel)
	{
		this.dispose();
	}
	
}


}
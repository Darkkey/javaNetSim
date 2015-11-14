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
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;



/**

 * This class is used to display general information about a 

 * GUINode.  It will display the IP Address information including 

 * default gateway and any Interface specific information such as 

 * the Current IP address and subnetmask.

 * @author Michael

 */

public class NodePropertiesDialog extends JDialog implements ActionListener

{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8899776864912613856L;

	private JLabel lblNodeNameLabel = new JLabel("Node Name : ");

	private JLabel lblNodeName = new JLabel();

	private JLabel lblGatewayLabel = new JLabel("Default Gateway : ");

	private JLabel lblGatewayAddress = new JLabel();

	private JButton btnOK = new JButton("OK");

	

	JPanel pnlNodeNameDetails = new JPanel();

	JPanel pnlGatewayDetails = new JPanel();

	

	JPanel pnlOKButton = new JPanel();

	

	public NodePropertiesDialog(JFrame frame, Vector inVectorMasterList){

		super(frame);	

		ArrayList aryColumnNames = new ArrayList();

		aryColumnNames.add("Name");

		aryColumnNames.add("Gateway");
                
		aryColumnNames.add("Interface Name");
                
                aryColumnNames.add("Type");

		aryColumnNames.add("MAC Address");

		aryColumnNames.add("IP Address");

		aryColumnNames.add("Subnet Mask");

		aryColumnNames.add("Link Name");



		Vector col = new Vector(aryColumnNames);

		JTable tblInfo = new JTable(inVectorMasterList, col);

		TableModel model = tblInfo.getModel();

		

		// Remove the first 2 column names		

		lblNodeName.setText(tblInfo.getModel().getValueAt(0, 0).toString());		

		tblInfo.getColumnModel().removeColumn(tblInfo.getColumnModel().getColumn(0));

		lblGatewayAddress.setText(tblInfo.getModel().getValueAt(0, 1).toString());

		tblInfo.getColumnModel().removeColumn(tblInfo.getColumnModel().getColumn(0));

		

		//Makes uneditable

		tblInfo.setEnabled(false);

		tblInfo.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrPane = new JScrollPane(tblInfo);



		

		//ArrayList temp = (ArrayList)inArrayMasterList.get(0);

		//lblNodeName.setText((String)temp.get(0));

		//lblGatewayAddress.setText((String)temp.get(1));

		

		JPanel pnlPropertiesScreen = (JPanel)this.getContentPane();

		pnlPropertiesScreen.setLayout(new BoxLayout(pnlPropertiesScreen, BoxLayout.Y_AXIS));

		pnlNodeNameDetails.setLayout(new FlowLayout());

		pnlOKButton.setLayout(new FlowLayout());		

		

		pnlNodeNameDetails.setPreferredSize(new Dimension(50,50));

		pnlNodeNameDetails.add(lblNodeNameLabel);

		pnlNodeNameDetails.add(lblNodeName);	

		pnlGatewayDetails.add(lblGatewayLabel);

		pnlGatewayDetails.add(lblGatewayAddress);

		pnlPropertiesScreen.add(pnlNodeNameDetails);

		pnlPropertiesScreen.add(pnlGatewayDetails);

		pnlPropertiesScreen.add(scrPane);

		pnlOKButton.add(btnOK);

		pnlPropertiesScreen.add(pnlOKButton);

		

		this.setSize(700,200);

		this.setLocationRelativeTo(null);

	    this.setModal(true);

	    this.setResizable(false);

	    btnOK.setToolTipText("Close the screen");

	    

	    //Set default button to be btnOK

	    this.getRootPane().setDefaultButton(btnOK);	    

		btnOK.addActionListener(this);

	}

	

	

	public void actionPerformed(ActionEvent e)

	{

		if(e.getSource() == btnOK)

		{

			

				this.dispose();			

		}

	}

	

}


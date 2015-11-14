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

import java.awt.BorderLayout;import java.awt.Dimension;import java.awt.event.WindowAdapter;import java.awt.event.WindowEvent;import java.io.IOException;import javax.swing.JEditorPane;import javax.swing.JFrame;import javax.swing.JScrollPane;import javax.swing.event.HyperlinkEvent;import javax.swing.event.HyperlinkListener;


/**
 * This class is an help windows utilising a jEditorPane enclosed in a JFrame
 * 
 * @author Team VC2
 *  
 */

public class HelpWindow extends JFrame {
	
	/**	 * 	 */	private static final long serialVersionUID = 808190955118248967L;	/**
	 * Constructs a HelpWindow
	 * @author Team VC2 
	 */
	
	public HelpWindow()
	{
		ClassLoader cl = this.getClass().getClassLoader();
		this.setTitle("javaNetSim ReadMe/ChangeLog");
		initComponents();
		String file="README.txt";
		JScrollPane scrPane = new JScrollPane(jEditorPane1);
		this.setBounds(50,50, 880, 640);
		scrPane.setPreferredSize(new Dimension(450, 400));
		this.getContentPane().add(scrPane, BorderLayout.CENTER);
		jEditorPane1.setEditable(false);
		try
		{
			jEditorPane1.setPage(cl.getResource(file));

		} 
		catch (IOException ioe)
		{
			System.out.println(ioe);
		}
	}

	/**
	 * Creates the components used in the HelpWindow JFrame and adds
	 * hyperlinklisteners.  Adds the jEditorPane to the Content Pane of the JFrame
	 * @author Team VC2 
	 */
	
	private void initComponents() 
	{
		jEditorPane1 = new JEditorPane();
		addWindowListener(new WindowAdapter() {
			@Override			public void windowClosing(WindowEvent evt) {
				exitForm(evt);
			}
		});
		jEditorPane1.addHyperlinkListener(new HyperlinkListener() 
				{
			public void hyperlinkUpdate(HyperlinkEvent evt) 
			{
				jEditorPane1HyperlinkUpdate(evt);
			}
				});

 	  getContentPane().add(jEditorPane1, BorderLayout.CENTER);

 	  pack();
	}

	private void jEditorPane1HyperlinkUpdate(HyperlinkEvent evt) {

		if (evt.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
			try
			{
				jEditorPane1.setPage(evt.getURL());
			}
		catch(Exception e)
		{
			System.out.println("Couldn't open URL" + e);
		}
	}
	
	/**
	 * Sets the action for exiting the JFrame to dispose the JFrame.
	 * @author Team VC2 
	 */
	
	private void exitForm(WindowEvent evt) 
	{
		this.dispose();
	}

	private JEditorPane jEditorPane1;
	
}
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

import java.awt.BorderLayout;import java.awt.Dimension;import java.awt.Toolkit;import java.awt.event.MouseAdapter;import java.awt.event.MouseEvent;import javax.swing.ImageIcon;import javax.swing.JLabel;import javax.swing.JWindow;import javax.swing.SwingUtilities;

/**
 * This class create a Thread-safe splash screen. 
 * Users can quickly remove the splash screens by simply clicking anywhere on it.
 * The splash screen will also be removed as soon as the application loads
 * 
 * @author Luke Hamilton
 * @version 1.0 
 *  
 */
public class SplashWindow extends JWindow {

	/**	 * 	 */	private static final long serialVersionUID = -2508421826929754344L;	public SplashWindow(String filename, int waitTime) {
		ClassLoader cl = this.getClass().getClassLoader();
		JLabel l = new JLabel(new ImageIcon(cl.getResource(filename)));
                JLabel v = new JLabel(core.Version.CORE_VERSION + ", copyright(¿) " + core.Version.YEARS);
		getContentPane().add(l, BorderLayout.CENTER);
                getContentPane().add(v, BorderLayout.AFTER_LAST_LINE);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = l.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2),
		screenSize.height / 2 - (labelSize.height / 2));
		addMouseListener(new MouseAdapter() {
			@Override			public void mousePressed(MouseEvent e) {
				setVisible(false);
				dispose();
			}
		});

		final int pause = waitTime;		
		final Runnable closerRunner = new Runnable() {
			public void run() {
				setVisible(false);
				dispose();
			}
		};

		Runnable waitRunner = new Runnable() {
			public void run() {
				try {
                                        Thread.sleep(pause);
					SwingUtilities.invokeAndWait(closerRunner);
				} catch (Exception e) {
					e.printStackTrace();
					// can catch InvocationTargetException
					// can catch InterruptedException
				}
			}
		};
		
		setVisible(true);
		Thread splashThread = new Thread(waitRunner, "SplashThread");
		splashThread.start();
	}
}
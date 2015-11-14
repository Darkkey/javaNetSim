/*
 * ApplicationLayerDevice.java
 *
 * Created on 19 Nov 2005, 16:03
 *
 */

package guiUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * 
 * @author key
 */
public abstract class ApplicationLayerDevice extends NetworkLayerDevice {

	protected JMenu mnuAppLayer = new JMenu("Applications");
	
	protected JMenuItem mnuTerminal = new JMenuItem("Terminal");

	private JMenuItem mnuDHCPC = new JMenuItem("Run DHCP Client");

	private JMenuItem mnuSNMPStartAgent = new JMenuItem("Start SNMP agent");

	private JMenuItem mnuSNMPStopAgent = new JMenuItem("Stop SNMP agent");

	/** Creates a new instance of ApplicationLayerDevice */
	public ApplicationLayerDevice(String inName, MainScreen inMainScreen,
			String imageLocation) {
		super(inName, inMainScreen, imageLocation);

		GuiNodePopMenu.add(mnuAppLayer);

		mnuSNMPStartAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.SNMPStartAgent(lblNodeName.getText());
			}
		});
		mnuSNMPStopAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.SNMPStopAgent(lblNodeName.getText());
			}
		});

		mnuDHCPC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.DHCPC(lblNodeName.getText());
			}
		});
		
		mnuTerminal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.Terminal(lblNodeName.getText());
			}
		});

		mnuAppLayer.add(mnuSNMPStartAgent);
		mnuAppLayer.add(mnuSNMPStopAgent);
		mnuAppLayer.addSeparator();
		mnuAppLayer.add(mnuDHCPC);
	}
}

package guiUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import core.Version;

/**
 * <P>
 * The MenuBar class is the menubar that is used for the MainScreen class
 * </P>
 * 
 * @author VC2 Team
 * @since 15th November 2004
 * @version v0.20
 */

public class MenuBar extends JMenuBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7498213676250919283L;

	private MainScreen controller;

	// File menu
	private JMenu mnuFile = new JMenu("File");

	private JMenuItem mnuNew = new JMenuItem("New"); // Create new simulation

	private JMenuItem mnuOpen = new JMenuItem("Open ..."); // Open a old
															// simulation

	private JMenuItem mnuSave = new JMenuItem("Save ..."); // Save a simulation

	private JMenuItem mnuSaveAs = new JMenuItem("Save As..."); // Save a
																// simulation

	// HTML report generator menu

	private JMenuItem mnuImport = new JMenuItem("Import ..."); // Open a old
																// simulation

	private JMenuItem mnuGenRep = new JMenuItem("Export ...");

	private JMenuItem mnuExit = new JMenuItem("Exit"); // Exit appliction

	// private JMenuItem mnuClose = new JMenuItem("Close"); //Close

	// Simulation menu
	private JMenu mnuSimulation = new JMenu("Simulation"); // Simulation menu

	private JMenu mnuEnvironment = new JMenu("Enviroment");

	private JMenuItem mnuClearConsole = new JMenuItem("Clear Console");

	private JMenuItem mnuRefreshNodeInformation = new JMenuItem(
			"Refresh Node Information");

	private JMenu mnuFilters = new JMenu("Show simulation messages for:");

	private JMenu mnuFilters2 = new JMenu("Show headers:");

	// Filters menu

	private JCheckBoxMenuItem mnuLayers = new JCheckBoxMenuItem("for Layers",
			true);

	private JCheckBoxMenuItem mnuPackets = new JCheckBoxMenuItem(
			"for Packet Names", true);

	private JCheckBoxMenuItem mnuMsgLinkLayer = new JCheckBoxMenuItem(
			"Link and DataLink Layers", true);

	private JCheckBoxMenuItem mnuMsgARP = new JCheckBoxMenuItem("ARP", true);

	private JCheckBoxMenuItem mnuMsgNetwork = new JCheckBoxMenuItem(
			"Network Layer", true);

	private JCheckBoxMenuItem mnuMsgTransport = new JCheckBoxMenuItem(
			"Transport Layer", true);

	private JCheckBoxMenuItem mnuMsgApplication = new JCheckBoxMenuItem(
			"Application Layer", true);

	// Add submenu
	public JMenu mnuAdd = new JMenu("Add Device"); // For Adding objects to
													// simulation

	// help menu
	private JMenu mnuHelp = new JMenu("Help");

	private JMenuItem mnuAbout = new JMenuItem("About...");

	private JMenuItem mnuHelpPane = new JMenuItem("ReadMe/ChangeLog");

	/**
	 * Constucts the Menubar
	 * 
	 * @param inController
	 *            The JFrame that the Menu will be attached to.
	 */
	public MenuBar(MainScreen inController) {
		controller = inController;
		buildMenu();
	}

	/**
	 * Intilizes all the menu buttons to their respective menu structure,
	 * associates action listeners and sets mnemonics for all menu buttons.
	 * 
	 */

	private void buildMenu() {

		mnuNew.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
		mnuOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		mnuSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));

		mnuGenRep.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));

		mnuClearConsole.setAccelerator(KeyStroke.getKeyStroke("F3"));
		mnuRefreshNodeInformation.setAccelerator(KeyStroke.getKeyStroke("F4"));

		mnuMsgLinkLayer.setAccelerator(KeyStroke.getKeyStroke("F9"));
		mnuMsgNetwork.setAccelerator(KeyStroke.getKeyStroke("F10"));
		mnuMsgTransport.setAccelerator(KeyStroke.getKeyStroke("F11"));
		mnuMsgApplication.setAccelerator(KeyStroke.getKeyStroke("F12"));

		mnuHelpPane.setAccelerator(KeyStroke.getKeyStroke("F1"));
		mnuExit.setAccelerator(KeyStroke.getKeyStroke("ctrl + Q"));

		mnuLayers.setAccelerator(KeyStroke.getKeyStroke("F7"));
		mnuPackets.setAccelerator(KeyStroke.getKeyStroke("F8"));

		// Add items to file menu
		mnuFile.add(mnuNew);
		mnuFile.add(mnuOpen);

		mnuFile.add(mnuSave);
		mnuFile.add(mnuSaveAs);
		mnuFile.addSeparator();
		mnuFile.add(mnuImport);
		mnuFile.add(mnuGenRep);
		mnuFile.addSeparator();
		mnuFile.add(mnuExit);

		// Add items to simulation menu

		mnuSimulation.add(mnuAdd);

		mnuEnvironment.add(mnuClearConsole);
		mnuEnvironment.add(mnuRefreshNodeInformation);
		mnuEnvironment.add(mnuFilters);
		mnuEnvironment.add(mnuFilters2);

		mnuFilters2.add(mnuLayers);
		mnuFilters2.add(mnuPackets);

		mnuFilters.add(mnuMsgLinkLayer);
		// mnuFilters.add(mnuMsgARP);
		mnuFilters.add(mnuMsgNetwork);
		mnuFilters.add(mnuMsgTransport);
		mnuFilters.add(mnuMsgApplication);

		// Add items to help
		mnuHelp.add(mnuHelpPane);
		mnuHelp.add(mnuAbout);

		// Set up Mnemonics for menu bar.

		mnuFile.setMnemonic('F');
		mnuNew.setMnemonic('N');
		mnuOpen.setMnemonic('O');
		mnuSave.setMnemonic('S');
		mnuSaveAs.setMnemonic('a');
		mnuExit.setMnemonic('x');

		mnuSimulation.setMnemonic('S');
		mnuAdd.setMnemonic('A');

		mnuGenRep.setMnemonic('G');

		mnuEnvironment.setMnemonic('E');
		mnuClearConsole.setMnemonic('C');
		mnuRefreshNodeInformation.setMnemonic('N');
		mnuHelp.setMnemonic('H');
		mnuAbout.setMnemonic('A');

		// Add action listener to generate html report menuitem
		mnuGenRep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.Export();
			}
		});

		mnuMsgLinkLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setFilter(0, !controller.getFilter(0));
			}
		});

		mnuLayers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setFilter2(1, !controller.getFilter2(1));
			}
		});

		mnuPackets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setFilter2(0, !controller.getFilter2(0));
			}
		});

		mnuMsgARP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setFilter(1, !controller.getFilter(1));
			}
		});

		mnuMsgNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setFilter(2, !controller.getFilter(2));
			}
		});

		mnuMsgTransport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setFilter(3, !controller.getFilter(3));
			}
		});

		mnuMsgApplication.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setFilter(4, !controller.getFilter(4));
			}
		});

		// Add action listener to exit
		mnuExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				int result = JOptionPane
//						.showConfirmDialog(null,
//								"Are you sure you wish to quit?",
//								"Really Quit?", JOptionPane.YES_NO_OPTION,
//								JOptionPane.QUESTION_MESSAGE);
//
//				if (result == JOptionPane.YES_OPTION) {
//					// Quit the program if Yes is selected.
					controller.quit();
//				}
			}
		});

		// Add action listener to Save menu item
		mnuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(null,"Sorry this is not yet
				// implemented.","Save Dialog Message",
				// JOptionPane.INFORMATION_MESSAGE);
				controller.Save();
			}
		});

		// Add action listener to Save As menu item
		mnuSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(null,"Sorry this is not yet
				// implemented.","SaveAs Dialog Message",
				// JOptionPane.INFORMATION_MESSAGE);
				controller.SaveAs(null);
			}
		});

		// Add action listener to Save menu item
		mnuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(null,"Sorry this is not yet
				// implemented.","Open Dialog Message",
				// JOptionPane.INFORMATION_MESSAGE);
				controller.Open();
			}
		});

		// Add action listener to Save menu item
		mnuImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.Import();
			}
		});

		// Add action listener to New menu item
		mnuNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.clearSaveAs();
				controller.refreshMainScreen();
			}
		});

		mnuAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Display message box with info about the project.
				String strAboutProject;
				strAboutProject = "javaNetSim " + Version.CORE_VERSION + "\n\n";

				for (int i = 0; i < Version.TEAM_MEMBERS.length; i++) {
					strAboutProject = strAboutProject + Version.TEAM_MEMBERS[i]
							+ "\n";
				}
				JOptionPane.showMessageDialog(null, strAboutProject,
						"About javaNetSim", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		mnuClearConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.clearConsole();
			}
		});

		mnuRefreshNodeInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.refreshNodeInformationTab();
			}
		});

		mnuHelpPane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.showHelpPane();
			}
		});

		// Add menu's to menubar
		this.add(mnuFile);
		this.add(mnuSimulation);
		this.add(mnuEnvironment);
		this.add(mnuHelp);

	}

}

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
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.InvalidNodeNameException;
import core.LowLinkException;
import core.Node;
import core.Simulation;
import core.protocolsuite.tcp_ip.DHCPD;
import core.protocolsuite.tcp_ip.Echo;
import core.protocolsuite.tcp_ip.Echo_tcp;
import core.protocolsuite.tcp_ip.IPV4Address;
import core.protocolsuite.tcp_ip.Route_entry;
import core.protocolsuite.tcp_ip.SNMP;
import core.protocolsuite.tcp_ip.Telnet_client;
import core.protocolsuite.tcp_ip.Telnet_server;

/**
 * 
 * This class is the GUI's main screen.
 * 
 * @author Luke Hamilton
 * 
 * @author Angela_brown
 * 
 * @author Michael Reith
 * 
 * @author Rob Hulford
 * 
 * @author bevan_calliess
 * 
 */

public class MainScreen extends JFrame implements ComponentListener {

	// Prefix for file extension

	/**
	 * 
	 */
	private static final long serialVersionUID = 5753856262155988366L;

	private static final String OLD_PREFIX = "jfst";

	private static final String SIM_PREFIX = "jnst";

	private static final String HTM_PREFIX = "htm";
	
	private static final String PNG_PREFIX = "png";
	
	private static final String JPG_PREFIX = "png";

	// File used to save simulation

	private File simSaveFile;

	// Simulation object

	private Simulation Sim;

	// Sandbox

	private SandBox Sandbox;
	
	// Menu
	
	private MenuBar Menubar;
	
	private MainScreen controller;

	private JPanel pContentPane = (JPanel) getContentPane();

	// filters
	private boolean[] filters = new boolean[8];

	private boolean[] filters2 = new boolean[2];

	// Toolbar

	private JPanel toolbar = new JPanel();

	// Set if object are create and havent been saved

	private boolean isDirty = false;

	// Boolean for connect Link

	private boolean isLink = false;

	// Container class for GUI object

	private Hashtable GUInodeTable = new Hashtable();

	// Colors for console output
	private static final Color NETWORK_LAYER_COLOR = Color.CYAN;

	private static final Color LINK_LAYER_COLOR = Color.YELLOW;

	private static final Color TRANSPORT_LAYER_COLOR = Color.LIGHT_GRAY;

	private static final Color APPLICATION_LAYER_COLOR = Color.GREEN;

	private static final Color HARDWARE_LAYER_COLOR = Color.ORANGE;

	private static final Color SYSTEM_LAYER_COLOR = Color.RED;

	// The File chooser class

	private JScrollPane scroller = new JScrollPane();

	// This is the consol window used to display feedback from the sim

	// private JTextArea pnlConsole = new JTextArea();

	private JEditorPane pnlNodeInformation = new JEditorPane();

	ColorRenderer colorRenderer;

	DefaultTableModel mConsole = new DefaultTableModel();

	public boolean LIlocked;

	String columnNames[] = { "English", "Japanese", "Boolean", "Date",
			"ImageIcon" };

	JTable pnlConsole = new JTable(mConsole) {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7086361948529146764L;

		@Override
		public Class getColumnClass(int column) {
			return getValueAt(0, column).getClass();
		}

		@Override
		public Component prepareRenderer(TableCellRenderer renderer, int row,
				int column) {
			Component c = super.prepareRenderer(renderer, row, column);
			colorRenderer.setBackground(c, row, column);
			return c;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private JScrollPane scrConsole = new JScrollPane(pnlConsole);

	private JScrollPane scrNodeInformation = new JScrollPane(pnlNodeInformation);

	private JTabbedPane tabConsole = new JTabbedPane();

	private JSplitPane pSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	private JLabel statusBar;

	private Timer statusTimer = new Timer();

	// Cursors used during the muse drag and drop process

	Cursor csrTarget = new Cursor(Cursor.CROSSHAIR_CURSOR);

	Cursor csrDefault = new Cursor(Cursor.DEFAULT_CURSOR);

	// Toolbar that allows users to save load exit sim.

	SimulationToolBar SimToolBar;

	class ColorRenderer implements ActionListener {
		private JTable table;

		private AbstractTableModel model;

		private Map colors;

		private Point location;

		public ColorRenderer(JTable table) {
			this.table = table;
			model = (AbstractTableModel) table.getModel();
			colors = new HashMap();
			location = new Point();
		}

		public void setBackground(Component c, int row, int column) {
			if (table.isCellSelected(row, column))
				return;

			if (c instanceof DefaultTableCellRenderer) {
				c.setBackground(table.getBackground());
			}

			column = table.convertColumnIndexToModel(column);

			// cell color

			Object key = getKey(row, column);
			Object o = colors.get(key);

			if (o != null) {
				c.setBackground((Color) o);
				return;
			}

			// row color

			key = getKey(row, -1);
			o = colors.get(key);

			if (o != null) {
				c.setBackground((Color) o);
				return;
			}

			// column color

			key = getKey(-1, column);
			o = colors.get(key);

			if (o != null) {
				c.setBackground((Color) o);
				return;
			}

		}

		public void setCellColor(int row, int column, Color color) {
			Point key = new Point(row, column);
			colors.put(key, color);
		}

		public void setColumnColor(int column, Color color) {
			setCellColor(-1, column, color);
		}

		public void setRowColor(int row, Color color) {
			setCellColor(row, -1, color);
		}

		private Object getKey(int row, int column) {
			location.x = row;
			location.y = column;
			return location;
		}

		public void actionPerformed(ActionEvent e) {

			Iterator it = colors.keySet().iterator();

			while (it.hasNext()) {
				Point key = (Point) it.next();
				int row = key.x;
				int column = key.y;

				if (column == -1) {
					model.fireTableRowsUpdated(row, row);
				} else if (row == -1) {
					int rows = table.getRowCount();

					for (int i = 0; i < rows; i++) {
						model.fireTableCellUpdated(i, column);
					}
				} else {
					model.fireTableCellUpdated(row, column);
				}
			}
		}
	}

	/**
	 * 
	 * Constructs a Mainsreen, sets up a new Simulation and calls the buildGUI
	 * method.
	 * 
	 * 
	 * 
	 */

	public class LayerInfoTask extends TimerTask {
		private MainScreen ms;

		public LayerInfoTask(MainScreen ms) {
			this.ms = ms;

		}

		public void run() {
			ms.printLayerInfo(false);
		}
	}

	public MainScreen() {

		Sim = new Simulation(core.ProtocolStack.TCP_IP);

		for (int i = 0; i <= 7; i++)
			filters[i] = true;

		filters2[0] = true;
		filters2[1] = true;
		
		controller = this;

		buildGUI();
		
		fillTemplates();

		LIlocked = false;

		Timer timer = null;
		timer = new Timer();
		timer.schedule(new LayerInfoTask(this), 1000, 1000);
	}
	
	private ClassLoader cl = this.getClass().getClassLoader();
	
	public class nodeTemplate{
		private String className;
		private String screenName;
		private Cursor cursor;
		private int cnt;
		private JMenuItem menu;
		
		class mnuAddActListener implements ActionListener{
			MainScreen controller;
			String className;
			
			public mnuAddActListener(MainScreen inController, String inClassName){
				controller = inController;
				className = inClassName;
			}
			
			public void actionPerformed(ActionEvent e){
				controller.setAllHighlightsOff();
				controller.addingNode(className);
			}
		}
		
		public void Inc(){
			cnt++;
		}
		
		public int getCnt(){
			return cnt;
		}
		
		public String getClassName(){
			return className;
		}
	
		public String getScreenName(){
			return screenName;
		}
		
		public Cursor getCursor(){
			return cursor;
		}
		
		public nodeTemplate(String inclassName, String inscreenName, String incursorImage, String inmenu){
			className = inclassName;
			screenName = inscreenName;
			
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(cl.getResource(incursorImage)), new Point(0,0), inclassName + "Cursor");
			
			cnt = 0;
			
			if(inmenu!="" && inmenu!=null){
				menu = new JMenuItem(inmenu);
				
				Menubar.mnuAdd.add(menu);
				
				menu.addActionListener(new mnuAddActListener(controller, className));
			}
			
		}
	}

	public Hashtable<String, nodeTemplate> nodeTemplates = new Hashtable<String, nodeTemplate>();
	
	private void fillTemplates(){
		
		nodeTemplates.put("PC", new nodeTemplate("PC", "PC", "images/simulation/mymac.png", "PC"));
		
		nodeTemplates.put("Laptop", new nodeTemplate("Laptop", "Laptop", "images/simulation/laptop.png", "Laptop"));
		
		nodeTemplates.put("Router", new nodeTemplate("Router", "Router", "images/simulation/router.png", "Router"));
		
		nodeTemplates.put("Switch", new nodeTemplate("Switch", "Switch", "images/simulation/switch.png", "Switch"));
		
		nodeTemplates.put("MultilayerSwitch", new nodeTemplate("MultilayerSwitch", "Multilayer Switch", "images/simulation/mlswitch.png", "Multilayer Switch"));
		
		nodeTemplates.put("Hub", new nodeTemplate("Hub", "Hub", "images/simulation/hub.png", "Hub"));
		
		nodeTemplates.put("ExternalProxy", new nodeTemplate("ExternalProxy", "External Proxy", "images/simulation/network_local.png", "Socks Proxy"));
		
		nodeTemplates.put("CSUDSU", new nodeTemplate("CSUDSU", "CSU/DSU Unit", "images/simulation/csudsu2.png", "CSU/DSU unit"));
	
		nodeTemplates.put("Printer", new nodeTemplate("Printer", "Printer", "images/simulation/printer.png", "Printer"));   
		
		nodeTemplates.put("WirelessAP", new nodeTemplate("WirelessAP", "Wireless AP", "images/simulation/ap.png", "Wireless AP"));
	}
	
	/**
	 * 
	 * Constructs a StandardToolBar, SimulationToolBar and the 2 tab consoles.
	 * 
	 * Additionally a componentListener is associated with MainScreen to check
	 * for
	 * 
	 * window resizing. All created components are added to the content pane of
	 * 
	 * MainScreen.
	 * 
	 * 
	 * 
	 */

	private void buildGUI() {

		pContentPane.setLayout(new BorderLayout());

		// setup toolbars

		StandardToolBar StandardToolBar = new StandardToolBar(this);

		statusBar = new JLabel();

		SimToolBar = new SimulationToolBar(this);

		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

		toolbar.add(StandardToolBar);

		toolbar.add(SimToolBar);

		pContentPane.add(toolbar, BorderLayout.NORTH);

		addComponentListener(this);

		// setup menu bara

		Menubar = new MenuBar(this);
		
		this.setJMenuBar(Menubar);

		// setup sandbox
		
		scroller = new JScrollPane();
		
		// scroller.setVerticalScrollBar(new JScrollBar());
		// scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// scroller.setHorizontalScrollBar(new JScrollBar());
		// scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		Sandbox = new SandBox(this);

		scroller.getViewport().add(Sandbox);

		pSplit.setTopComponent(scroller);

		// setup console

		// Dimension cSize = new Dimension(this.getWidth(),80);

		// scrConsole.setPreferredSize(cSize);

		tabConsole.add("Console", scrConsole);

		tabConsole.add("Node Information", scrNodeInformation);

		// EDITABLE << FIXME!!!
		// pnlConsole.setEditable(false);

		pnlNodeInformation.setEditable(false);

		pSplit.setDividerSize(5);

		pSplit.setBottomComponent(tabConsole);

		pContentPane.add(pSplit);

		pContentPane.add(statusBar, BorderLayout.PAGE_END);

		statusBar.setText(" ");

		// setup frame

		this.setSize(800, 600);

		this.setTitle("javaNetSim");

		this.setLocationRelativeTo(null);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				quit();
			}
		});

		this.setVisible(true);

		pSplit.setDividerLocation(.8);

		// this.setExtendedState(MAXIMIZED_BOTH);

		colorRenderer = new ColorRenderer(pnlConsole);

		mConsole.setColumnIdentifiers(new Object[] { "Time", "Node", "Packet",
				"Layer", "Info" });
		
		pnlConsole.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		//pnlConsole.se

		//pnlConsole.getColumnModel().getColumn(0).setPreferredWidth(100);
		pnlConsole.getColumnModel().getColumn(0).setMinWidth(90);
		pnlConsole.getColumnModel().getColumn(0).setMaxWidth(200);
		//pnlConsole.getColumnModel().getColumn(1).setPreferredWidth(100);
		pnlConsole.getColumnModel().getColumn(1).setMinWidth(85);
		pnlConsole.getColumnModel().getColumn(1).setMaxWidth(200);
		//pnlConsole.getColumnModel().getColumn(2).setPreferredWidth(100);
		pnlConsole.getColumnModel().getColumn(2).setMinWidth(175);
		pnlConsole.getColumnModel().getColumn(2).setMaxWidth(200);
		//pnlConsole.getColumnModel().getColumn(3).setPreferredWidth(100);
		pnlConsole.getColumnModel().getColumn(3).setMinWidth(75);
		pnlConsole.getColumnModel().getColumn(3).setMaxWidth(200);
		
		pnlConsole.getColumnModel().getColumn(4).setMinWidth(400);
		
		
		//pnlConsole.setAutoResizeMode(;
		// pnlConsole.setEnabled(false);

		// pnlConsole.getColumnModel().getColumn(0).setMaxWidth(100);
		// pnlConsole.getColumnModel().getColumn(0).setMinWidth(100);
		// pnlConsole.getColumnModel().getColumn(1).setMaxWidth(100);
		// pnlConsole.getColumnModel().getColumn(1).setMinWidth(100);
		// pnlConsole.getColumnModel().getColumn(2).setMaxWidth(200);
		// pnlConsole.getColumnModel().getColumn(2).setMinWidth(200);
		// pnlConsole.getColumnModel().getColumn(3).setMaxWidth(100);
		// pnlConsole.getColumnModel().getColumn(3).setMinWidth(100);

		// pnlConsole.getColumnModel().getColumn(0).sizeWidthToFit();
		// pnlConsole.getColumnModel().getColumn(1).sizeWidthToFit();
		// pnlConsole.getColumnModel().getColumn(2).sizeWidthToFit();
		// pnlConsole.getColumnModel().getColumn(3).sizeWidthToFit();
		// pnlConsole.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

	}

	public class clearStatusTask extends TimerTask {
		private JLabel lbl;

		public clearStatusTask(JLabel lbl) {
			this.lbl = lbl;

		}

		public void run() {
			lbl.setText(" ");
		}
	}

	public void setStatus(String status) {
		Date d = new Date();
		statusBar.setText(d.toString() + ":   " + status);

		statusTimer.cancel();
		statusTimer = null;
		statusTimer = new Timer();
		statusTimer.schedule(new clearStatusTask(statusBar), 4000);
	}

	/**
	 * 
	 * Runs the Mainscreen, shows the splash screen.
	 * 
	 * @param String[]
	 *            args This is not used for the GUI but required by Java.
	 * 
	 */

	public static void main(String args[]) {
		

		// Set Default look and feel (For setExtendedState bug)

		// JFrame.setDefaultLookAndFeelDecorated(true);

		// JDialog.setDefaultLookAndFeelDecorated(true);

		core.Simulation.UIDGen = 0;

		// Math.random()

		new SplashWindow("images/firewall.gif", 2000);

		new MainScreen();

	}

	/*
	 * This method set value of filter. @author Key @param filter index @param
	 * filter value
	 * 
	 */
	public void setFilter(int i, boolean value) {
		filters[i] = value;
	}

	/*
	 * This method get value of filter. @author Key @param filter index @returns
	 * filter value
	 * 
	 */
	public boolean getFilter(int i) {
		return filters[i];
	}

	/*
	 * This method set value of filter. @author Key @param filter index @param
	 * filter value
	 * 
	 */
	public void setFilter2(int i, boolean value) {
		filters2[i] = value;
	}

	/*
	 * This method get value of filter. @author Key @param filter index @returns
	 * filter value
	 * 
	 */
	public boolean getFilter2(int i) {
		return filters2[i];
	}

	/**
	 * 
	 * Sets the cursor to the node type that has been selected.
	 * 
	 * @param int
	 *            inCursorType The type of cursor that the sandbox should
	 *            display.
	 * 
	 * 
	 * 
	 */

	public void addingNode(String nodeClass)

	{

		Sandbox.setCursorType(nodeClass);

	}

	public String getDeviceTypeDialog(String title, String msg, String[] choices) {
		String optbtn = (String) UIManager.get("OptionPane.cancelButtonText");
		UIManager.put("OptionPane.cancelButtonText", "Default");

		String choice = (String) JOptionPane.showInputDialog(this, msg, title,
				JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

		UIManager.put("OptionPane.cancelButtonText", optbtn);

		if (choice == null)
			choice = choices[0];

		return choice;
	}

	/**
	 * 
	 * This method will create a Node within the simulation and add it to the
	 * sandbox
	 * 
	 * @author key
	 * 
	 * 
	 * 
	 */

	public void addNode(String className, Point inPoint) {

		String result = JOptionPane.showInputDialog(this,
				"Please enter a name for new " + className + ":", className
						+ nodeTemplates.get(className).getCnt());

		if (result != null) {

			result = result.trim();

			if (!result.equalsIgnoreCase("")) {

				try {

					nodeTemplates.get(className).Inc();

					Sim.addNode(className, result, true);

					Class gNode = Class.forName("guiUI.Gui" + className);

					Constructor gCon = gNode.getConstructor(String.class,
							MainScreen.class);

					GuiNode tempNode = (GuiNode) gCon.newInstance(new Object[] {
							result, this });

					tempNode.addInterfaces(this, Sim.getNode(result));
					
					tempNode.updateInterfacesMenu();

					tempNode.setNodeLocation(inPoint);

					GUInodeTable.put(result, tempNode);

					this.Sandbox.add(tempNode);

					Sandbox.setLayer(tempNode, 3, 0);

					Sim.getNode(result).ifacesUP();

					if (Sim.getNode(result) instanceof core.NetworkLayerDevice)
						((core.NetworkLayerDevice) Sim.getNode(result))
								.getConfig().executeCommand("write mem");

					isDirty = true;

				} catch (InvalidNodeNameException e) {

					JOptionPane.showMessageDialog(this,
							"Invalid Switch name entered. Node with name: '"
									+ result
									+ "' all ready exist within Simulation",
							"Invalid Switch name", JOptionPane.ERROR_MESSAGE);
					

					this.setAllHighlightsOff();

					this.refreshNodeInformationTab();
					
					return;

				} catch (Exception e) {

					e.printStackTrace();

				}

			} else {

				JOptionPane.showMessageDialog(this, "Switch name not entered",
						"Switch name not entered", JOptionPane.WARNING_MESSAGE);

			}

		}

		this.setAllHighlightsOff();

		this.refreshNodeInformationTab();

	}

	/**
	 * 
	 * Calls the setIsLink method of MainScren with a value of true
	 * 
	 * 
	 * 
	 */

	public void creatingLink() {

		setIsLink(true);

	}

	/**
	 * 
	 * Calls the setHighlightsOff method of a SimulationToolBar that will
	 * 
	 * remove any highlighting from toolbar buttons.
	 * 
	 */

	public void setAllHighlightsOff()

	{

		SimToolBar.setHighlightsOff();

	}

	/**
	 * 
	 * This method is called when the user uses the drag and drop facility
	 * 
	 * to draw a link between two guiNodes. The method is provided with the
	 * 
	 * name of the two nodes and then obtains a list of all available interfaces
	 * 
	 * for each of them. Once the two lists have been obtained it displays a
	 * 
	 * dialog window to the user so that they can select which interface to
	 * 
	 * connect to and change the name of the link. If either node has no
	 * available
	 * 
	 * interfaces then an error message is displayed.
	 * 
	 * Once the User has made there selection the metod will create a link in
	 * the
	 * 
	 * Simulation and then add a line representing that link onto the
	 * LinkLayerPane.
	 * 
	 * 
	 * 
	 * @param inNode1
	 * 
	 * @param inNode2
	 * 
	 */

	public void addLink(String inNode1, String inNode2) {

		String[] strFirstNodeInter;

		String[] strSecondNodeInter;

		try

		{

			strFirstNodeInter = Sim.getNode(inNode1).getAvailableInterfaces();

			strSecondNodeInter = Sim.getNode(inNode2).getAvailableInterfaces();

			if (strFirstNodeInter.length == 0 || strSecondNodeInter.length == 0) {

				String Msg = "";

				if (strFirstNodeInter.length == 0) {

					Msg = Msg + inNode1 + " has no available interfaces \n ";

				}

				if (strSecondNodeInter.length == 0) {

					Msg = Msg + inNode2 + " has no available interfaces";

				}

				JOptionPane.showMessageDialog(this,

				Msg, "Available Interface Error",

				JOptionPane.WARNING_MESSAGE);

			} else {

				LinkDialog dlgLink = new LinkDialog(inNode1, inNode2,
						strFirstNodeInter, strSecondNodeInter);

				dlgLink.setTitle("Create Link between " + inNode1 + " and "
						+ inNode2 + "?");

				// dlgLink.show();
				dlgLink.setVisible(true);

				// if the user pressed th OK button create the link

				if (dlgLink.getWasOKPressed()) {

					String strFirstNodeInterface = dlgLink
							.getFirstSelectedInterface();

					String strSecondNodeInterface = dlgLink
							.getSecondSelectedInterface();

					// String strLinkName = inNode1 + "-" +
					// Sim.getNode(inNode1).getIntSType(strFirstNodeInterface) +
					// "-" + inNode2;

					String strLinkName = inNode1 + "(" + strFirstNodeInterface
							+ ") - " + inNode2 + "(" + strSecondNodeInterface
							+ ")";

					if (Sim.getNode(inNode1).getIntType(strFirstNodeInterface) == Sim
							.getNode(inNode2)
							.getIntType(strSecondNodeInterface)) {

						GuiNode tempFirstNode = (GuiNode) GUInodeTable
								.get(inNode1);

						Point FirstPoint = new Point((tempFirstNode.getX() +

						tempFirstNode.getWidth() / 2), (tempFirstNode.getY() +

						tempFirstNode.getHeight() / 2));

						GuiNode tempSecondNode = (GuiNode) GUInodeTable
								.get(inNode2);

						Point SecondPoint = new Point((tempSecondNode.getX() +

						tempSecondNode.getWidth() / 2), (tempSecondNode.getY() +

						tempSecondNode.getHeight() / 2));

						switch (Sim.getNode(inNode1).getIntType(
								strFirstNodeInterface)) {
						case core.NetworkInterface.Ethernet10T:
						case core.NetworkInterface.Ethernet100FX:
							Sim.addEthernetLink(strLinkName, inNode1,
									strFirstNodeInterface, inNode2,
									strSecondNodeInterface);
							break;
						case core.NetworkInterface.Console:
							Sim.addConsoleLink(strLinkName, inNode1,
									strFirstNodeInterface, inNode2,
									strSecondNodeInterface);
							break;
						case core.NetworkInterface.Serial:
							Sim.addSerialLink(strLinkName, inNode1,
									strFirstNodeInterface, inNode2,
									strSecondNodeInterface);
							break;
						}

						Sandbox.addLine(strLinkName, FirstPoint, SecondPoint,
								Sim.getNode(inNode1).getIntType(
										strFirstNodeInterface));

						tempFirstNode.setConnectedLinkName(strLinkName, 1,
								inNode2);

						tempSecondNode.setConnectedLinkName(strLinkName, 2,
								inNode1);

						// pnlConsole.append("Added Link between "+ inNode1 +"
						// and " + inNode2 + "\n");
					} else {
						JOptionPane
								.showMessageDialog(
										this,
										"Cannot connect interfaces with different types!",
										"Error!", JOptionPane.ERROR_MESSAGE);
					}
				}

				dlgLink.dispose();

				this.refreshNodeInformationTab();

			}

		}

		catch (Exception ufe)

		{

			System.out.println(ufe);

		}

	}

	/**
	 * 
	 * Creates a real line (not fake link line)
	 * 
	 * @param String
	 *            strLinkName The name of the Link
	 * 
	 * @param String
	 *            inFirstNodeName The name of the first Node
	 * 
	 * @param String
	 *            inSecondNodeName The name of the Second Node
	 * 
	 */

	public void createLink(String strLinkName, String inFirstNodeName,
			String inSecondNodeName, int LinkType) {

		try {

			GuiNode tempFirstNode = (GuiNode) GUInodeTable.get(inFirstNodeName);

			Point FirstPoint = new Point((tempFirstNode.getX() +

			tempFirstNode.getWidth() / 2), (tempFirstNode.getY() +

			tempFirstNode.getHeight() / 2));

			GuiNode tempSecondNode = (GuiNode) GUInodeTable
					.get(inSecondNodeName);

			Point SecondPoint = new Point((tempSecondNode.getX() +

			tempSecondNode.getWidth() / 2), (tempSecondNode.getY() +

			tempSecondNode.getHeight() / 2));

			Sandbox.addLine(strLinkName, FirstPoint, SecondPoint, LinkType); // FIXME!

			tempFirstNode
					.setConnectedLinkName(strLinkName, 1, inSecondNodeName);

			tempSecondNode
					.setConnectedLinkName(strLinkName, 2, inFirstNodeName);

		} catch (Exception ex) {

			System.out.println(ex.toString());

		}

	}

	/**
	 * 
	 * Creates a Ping Dialog for sending a Ping
	 * 
	 * @param String
	 *            inNodeName The name of the source Node
	 * 
	 */

	public void SendPing(String inNodeName)

	{

		PingDialog dlgSendPing = new PingDialog(inNodeName);

		dlgSendPing.setTitle("Send ping from " + inNodeName + " to ...");

		// dlgSendPing.show();
		dlgSendPing.setVisible(true);

		if (dlgSendPing.getWasOKPressed())

		{

			printNetworkStart();

			try {

				String strDestinationIP = dlgSendPing.getDestinationIPAddress();

				Sim.sendPing(inNodeName, strDestinationIP);

			}

			catch (LowLinkException e) {
				insertInConsole(inNodeName, "Link", "*SYSTEM*", e.toString());

			}

			catch (CommunicationException e) {
				insertInConsole(inNodeName, "Network", "*SYSTEM*", e.toString());

			}

			catch (InvalidNetworkLayerDeviceException e) {
				insertInConsole("(none)", "(none)", "*SYSTEM*", e.toString());
			}

			catch (InvalidNodeNameException e) {
				insertInConsole("(none)", "(none)", "*SYSTEM*", e.toString());
			}

			printLayerInfo(true);

			// Ok button was pressed.

		}

		dlgSendPing.dispose();

	}

	public void printNetworkStart() {

		setStatus("Starting network transfer...");

		// pnlConsole.append("****************************************************************************************************************
		// \n");

		// pnlConsole.append("Starting network transfer... \n");

		// pnlConsole.append("****************************************************************************************************************
		// \n");

		// pnlConsole.append("\n");

	}

	public static final String DATE_FORMAT_NOW = "HH:mm:ss-SSS";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(MainScreen.DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	public void insertInConsole(String Node, String Packet, String Layer,
			String Info) {
		mConsole.insertRow(mConsole.getRowCount(), new Object[] { MainScreen.now(), Node,
				Packet, Layer, Info });
		if (Layer.contains("Network")) {
			colorRenderer.setRowColor(mConsole.getRowCount() - 1,
					MainScreen.NETWORK_LAYER_COLOR);
		} else if (Layer.contains("Link")) {
			colorRenderer.setRowColor(mConsole.getRowCount() - 1,
					MainScreen.LINK_LAYER_COLOR);
		} else if (Layer.contains("Transport")) {
			colorRenderer.setRowColor(mConsole.getRowCount() - 1,
					MainScreen.TRANSPORT_LAYER_COLOR);
		} else if (Layer.contains("Application")) {
			colorRenderer.setRowColor(mConsole.getRowCount() - 1,
					MainScreen.APPLICATION_LAYER_COLOR);
		} else if (Layer.contains("Hardware")) {
			colorRenderer.setRowColor(mConsole.getRowCount() - 1,
					MainScreen.HARDWARE_LAYER_COLOR);
		} else if (Layer.contains("*SYSTEM*")) {
			colorRenderer.setRowColor(mConsole.getRowCount() - 1,
					MainScreen.SYSTEM_LAYER_COLOR);
		}

	}

	public void printLayerInfo(boolean header) {
		if (LIlocked)
			return;
		LIlocked = true;

		// if(header) pnlConsole.append("\n");

		Vector vecRecordedInfo = Sim.getRecordedInfo();

		if (vecRecordedInfo.size() == 0) {

			// pnlConsole.append("No entries to display. \n");

		} else {

			if (header) {
				// pnlConsole.append("****************************************************************************************************************
				// \n");

				// pnlConsole.append("The following is a record of the last
				// Transactions movements. \n");

				// pnlConsole.append("****************************************************************************************************************
				// \n");
			}

			Iterator it = vecRecordedInfo.iterator();

			while (it.hasNext()) {

				String recording[] = (String[]) it.next();
				String packet = "";
				String layer = "";
				// Very UGLY but works. ;)
				if (!(((String) recording[4]).contains("Network") && !filters[2])
						&& !(((String) recording[4]).contains("Link") && !filters[0])
						&& !(((String) recording[4]).contains("Transport") && !filters[3])
						&& !(((String) recording[4]).contains("Application") && !filters[4])) {
					// && !(((String)recording[4]).contains("ARP") &&
					// !filters[1])

					if (filters2[0])
						packet = pad(recording[2], 25, ' ');
					else
						packet = "";
					if (filters2[1])
						layer = pad(recording[4], 15, ' ');
					else
						layer = "";

					// pnlConsole.append(pad(recording[1],15,'
					// ')+packet+pad(recording[3],10,' ')+layer+recording[5]+
					// "\n");
					// System.out.println(pad(recording[1],15,'
					// ')+packet+pad(recording[3],10,' ')+layer+recording[5]+
					// "\n");
					insertInConsole(recording[1], packet, layer, recording[5]);
				}

			}

			if (header) {
				// pnlConsole.append("****************************************************************************************************************\n");
			}

			Sim.clearLayerInfo();

		}
		LIlocked = false;

	}

	public void addFooter() {
		// pnlConsole.append("****************************************************************************************************************\n");
	}

	public void addHeader() {
		// pnlConsole.append("****************************************************************************************************************
		// \n");

		// pnlConsole.append("The following is a record of the last Transactions
		// movements. \n");

		// pnlConsole.append("****************************************************************************************************************
		// \n");
	}

	/**
	 * 
	 * Creates a Properties Dialog on the specific Node
	 * 
	 * @param String
	 *            inNodeName The name node to show properties on
	 * 
	 */

	public void showPropertiesDialog(String inNodeName)

	{

		try {

			Vector VectorMasterList = Sim.getAllNodeInformation(inNodeName);

			NodePropertiesDialog dlgNodeProperties = new NodePropertiesDialog(
					this, VectorMasterList);

			dlgNodeProperties.setTitle("Properties for " + inNodeName);

			// dlgNodeProperties.show();
			dlgNodeProperties.setVisible(true);

		} catch (Exception ufe) {

			ufe.printStackTrace();

		}

	}

	/**
	 * 
	 * Creates a Link Properties Dialog on the specific Node
	 * 
	 * @param String
	 *            inNodeName The name node to show properties on
	 * 
	 */

	public void showLinkDialog(String inNodeName)

	{
		Object[] nodesArray = null;

		int selectedIndex = -1;

		// if null is passed from the menubar item

		// Get array of nodes from within the simulation

		if (inNodeName == null) {

			ArrayList Values = new ArrayList();

			Enumeration enum1 = GUInodeTable.keys();

			while (enum1.hasMoreElements()) {

				String key = (String) enum1.nextElement();

				GuiNode tempNode = (GuiNode) GUInodeTable.get(key);

				if (tempNode instanceof NetworkLayerDevice) {

					Values.add(key);

				}

			}

			nodesArray = Values.toArray();

			// Else pass the selected node name

		} else {

			nodesArray = new Object[1];

			nodesArray[0] = inNodeName;

			selectedIndex = 0;

		}

		// test that there are any node within the simulation

		if (nodesArray.length != 0) {

			new LinkProperties(this, nodesArray, selectedIndex, Sim, Sandbox);

			this.refreshNodeInformationTab();

		} else

			JOptionPane.showMessageDialog(this,
					"There are currently no node's within the simulation",
					"Warning!", JOptionPane.WARNING_MESSAGE);

	}

	/**
	 * 
	 * This method is used to pad a string to a required output. This is used
	 * 
	 * to pad string for both consoles on the GUI.
	 * 
	 * @param String
	 *            inString The string to be padded
	 * 
	 * @param int
	 *            length The total length that the string should be
	 * 
	 * @param char
	 *            pad character to add as the "pad"
	 * 
	 * @return strPaddedString the padded string
	 * 
	 */

	private String pad(String inString, int length, char pad) {

		if (inString != null) {

			StringBuffer buffer = new StringBuffer(inString);

			while (buffer.length() < length) {

				buffer.append(pad);

			}

			return buffer.toString();

		}

		StringBuffer empty = new StringBuffer(length);

		return empty.toString();

	}

	/**
	 * 
	 * Will get a String array of the available interfaces on a Node
	 * 
	 * @param String
	 *            inFirstNodeName The name of the node to check
	 * 
	 * @return Array of available interfaces
	 * 
	 */

	public boolean getAvailableInterfaces(String inFirstNodeName)

	{

		String[] strFirstNodeInter;

		try {

			strFirstNodeInter = Sim.getNode(inFirstNodeName).getAvailableInterfaces();

			if (strFirstNodeInter.length == 0)

			{

				return false;

			}

			return true;

		}

		catch (Exception ufe)

		{

			System.out
					.println("getAvailableInterfaces in MainScreen has caused a SNAFU");

		}

		return false;

	}

	/**
	 * 
	 * This method adds a Line to the LinkLayer ready to use during the
	 * 
	 * Drag and Drop process of adding a link. The line uses a
	 * 
	 * LinkLayerPanel constant for its name to ensure it is always unique.
	 * 
	 * @param inPosition
	 *            The starting position for the line (should be center of
	 *            object)
	 * 
	 */

	public void addFakeLine(Point inPosition) {

		Sandbox.addLine(LinkLayerPanel.FAKELINE, inPosition, inPosition, 0);

	}

	/**
	 * 
	 * This method is used to remove the fake line once
	 * 
	 * the Drag and Drop process of creating a link has finished
	 * 
	 */

	public void removeFakeLine() {

		Sandbox.removeLine(LinkLayerPanel.FAKELINE);

	}

	/**
	 * 
	 * Quits the MainScreen
	 * 
	 * 
	 * 
	 */

	// TODO Add code to test for a dirty file and then save simulation
	public void quit() {

		if(isDirty){
			int result = JOptionPane.showConfirmDialog(this, 
					"Save changes"+(simSaveFile!=null?" as "+simSaveFile.getName():"")+"?");
			switch(result){
			case JOptionPane.YES_OPTION:{
				if(Save()){
					System.exit(0);
				}
				break;
			}
			case JOptionPane.NO_OPTION: System.exit(0); break;
			case JOptionPane.CANCEL_OPTION: break;
			}
		}
		else{
			System.exit(0);
		}

	}

	/**
	 * 
	 * Deletes a line from sandbox...
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inLineName - the name of the line.
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public void deleteLine(String inLineName){
	 * 
	 * Sandbox.removeLine(inLineName);
	 *  }
	 */

	/**
	 * 
	 * Deletes a Node from both the Sim and the GUI Screen.
	 * 
	 * @author luke_hamilton
	 * 
	 * @param String
	 *            inNodeName The name of the node
	 * 
	 * @param GuiNode
	 *            inNode The guiNode to be deleted
	 * 
	 */

	public void deleteNode(String inNodeName, GuiNode inNode) {

		if (JOptionPane.showConfirmDialog(this, "Delete " + inNodeName + "?",
				"Confirm delete!", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

			try {

				Sim.deleteNode(inNodeName);

				GUInodeTable.remove(inNodeName);

				Vector vecConnectedLinks = inNode.getConnectedLinks();

				Iterator it = vecConnectedLinks.iterator();

				while (it.hasNext()) {

					String linkString = (String) it.next();

					String LinkDetails[] = linkString.split(":");

					Sandbox.removeLine(LinkDetails[0]);

					GuiNode temp = (GuiNode) GUInodeTable.get(LinkDetails[1]);

					temp.deleteConnectedLink(LinkDetails[0]);

				}

				// TODO does the remove method also dispose of the guiNode?

				this.Sandbox.remove(inNode);

				this.Sandbox.repaint();

				// pnlConsole.append(inNode.getName() +", and any connected
				// links have been deleted\n");

				this.refreshNodeInformationTab();

			} catch (InvalidNodeNameException e) {

				// TODO: handle exception

			}
		}

	}

	/**
	 * 
	 * This method will set the status of the Sandbox's Icon
	 * 
	 * @param Boolean
	 *            inIsLink is the link icon activated?
	 * 
	 */

	public void setIsLink(boolean inIsLink)

	{

		isLink = inIsLink;

		if (isLink)

		{

			this.Sandbox.setCursor(csrTarget);

		}

		else

		{

			this.Sandbox.setCursor(csrDefault);

			SimToolBar.setLinkHighlight(false);

		}

	}

	/**
	 * 
	 * Returns if the link button is currently active
	 * 
	 * @return boolean isLink true if Link button is active.
	 * 
	 */

	public boolean getIsLink()

	{

		return isLink;

	}

	public boolean Save() {
		if (simSaveFile != null)
			return SaveAs(simSaveFile.getPath());
		else
			return SaveAs(null);
	}

	public boolean SaveAs(String saveas) {
		
		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle("Save As ...");
		chooser.setAcceptAllFileFilterUsed(false);
		//chooser.set
		chooser.addChoosableFileFilter(new JNSTFilter());

		int returnVal;

		if (saveas == null) {
			returnVal = chooser.showSaveDialog(this);
		} else {
			returnVal = JFileChooser.APPROVE_OPTION;
		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// Get Selected file for saving GUI information
			File tempFile = chooser.getSelectedFile();

			if (saveas == null) {
				if (tempFile.getPath().contains("." + MainScreen.SIM_PREFIX))
					simSaveFile = new File(tempFile.getPath());
				else
					simSaveFile = new File(tempFile.getPath() + "."
							+ MainScreen.SIM_PREFIX);
			}
			
			if(simSaveFile.exists() && saveas == null){
				if(JOptionPane.showConfirmDialog(this, "File exists! Overwrite?",
				"Confirm overwriting!", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
					return false;
			}

			// Save Simulation.
			try {

				Enumeration enum1 = GUInodeTable.keys();
				String strSave = "";

				while (enum1.hasMoreElements()) {

					String key = (String) enum1.nextElement();
					GuiNode tempNode = (GuiNode) GUInodeTable.get(key);
					String lnk;

					try {

						Object nics[] = Sim.getNode(key).getAllInterfaces();
						Arrays.sort(nics);

						String strType = tempNode.getClass().toString();
						strType = strType.replaceFirst("class guiUI\\.", "");

						Point pnt1 = tempNode.getLocation();

						strSave = strSave + key + "\n"

						+ strType + "\n"

						+ pnt1.x + "," + pnt1.y + "\n"

						+ Sim.getNode(key).On + "\n"

						+ nics.length + "\n";

						for (int i = 0; i < nics.length; i++) {

							String linkName = Sim.getLinkName(key, nics[i]
									.toString());
							int type = Sim.getNode(key).getIntType(
									nics[i].toString());

							strSave = strSave + nics[i] + "|" + type + "|"
									+ linkName;

							String lP = "100.00";

							try {
								lnk = Sim.getLinkName(key, nics[i].toString());
								lP = Double.valueOf(Sim.GetLinkProb(lnk))
										.toString();
							} catch (Exception e) {
							}

							strSave += "|" + lP;

							switch (type) {
							case core.NetworkInterface.Ethernet10T:
							case core.NetworkInterface.Ethernet100FX:
							case core.NetworkInterface.Wireless:
								if (!Sim.getNode(key)
										.getNIC(nics[i].toString()).isActive()) {
									strSave += "|#";
								} else {
									strSave += "|"
											+ ((core.EthernetNetworkInterface) Sim
													.getNode(key).getNIC(
															nics[i].toString())).defaultMACAddress;
								}
								strSave += "|"
										+ Sim.getNode(key).getNIC(
												nics[i].toString()).isActive();
								break;
							case core.NetworkInterface.Console:
								break;
							case core.NetworkInterface.WAN:
								strSave += "|"
										+ ((core.WANNetworkInterface) Sim
												.getNode(key).getNIC(
														nics[i].toString()))
												.getConnType()
										+ "|"
										+ ((core.WANNetworkInterface) Sim
												.getNode(key).getNIC(
														nics[i].toString()))
												.getServer()
										+ "|"
										+ ((core.WANNetworkInterface) Sim
												.getNode(key).getNIC(
														nics[i].toString()))
												.getConnHost()
										+ "|"
										+ ((core.WANNetworkInterface) Sim
												.getNode(key).getNIC(
														nics[i].toString()))
												.getConnPort()
										+ "|"
										+ ((core.WANNetworkInterface) Sim
												.getNode(key).getNIC(
														nics[i].toString()))
												.getConnService();
								break;
							case core.NetworkInterface.Serial:
								strSave += "|"
										+ ((core.SerialNetworkInterface) Sim
												.getNode(key).getNIC(
														nics[i].toString()))
												.getClockRate();
								break;
							}

							strSave += "\n";
						}

						strSave += "#config\n";

						if (Sim.getNode(key) instanceof core.NetworkLayerDevice) {
							strSave += ((core.NetworkLayerDevice) Sim
									.getNode(key)).getConfig().toString(
									core.DeviceConfig.STARTUP_CONFIG);
						}

						strSave += "#endconfig\n";

						strSave += "#data\n";
						
						if (Sim.getNode(key) instanceof core.NetworkLayerDevice) {
							strSave += ((core.NetworkLayerDevice) Sim
								.getNode(key)).getAllData();
						}

						strSave += "#enddata\n";

					} catch (Exception e) { // This should never happen
						e.printStackTrace();
					}

				}

				Writer output = null;

				try {

					// use buffering

					output = new BufferedWriter(new FileWriter(simSaveFile));

					output.write(strSave);

				}

				finally {

					// flush and close both "output" and its underlying
					// FileWriter

					if (output != null)
						output.close();

				}

				System.out.println("Writing Simulation file");

				System.out.println("Simulation was saved succesfully.");

				System.out
						.println("File " + simSaveFile.getName()
								+ " has been saved to "
								+ simSaveFile.getAbsolutePath());

				System.out.println();

				isDirty = false;

				setStatus("Simulation was saved succesfully.");
				
				return true;

			} catch (IOException e) {

				System.out
						.println("There was an error writing the simulation to file.\n"
								+ e.toString());

			}

		}
		
		return false;

	}
	
	public void Terminal(String nodeName){
		
		core.Node node = Sim.getNode(nodeName);
		
		try{
		
			if(!(node instanceof core.NetworkLayerDevice)){
				JOptionPane.showMessageDialog(this,
					"Invalid device type!",	"Terminal", JOptionPane.ERROR_MESSAGE);
			
				return;
			}
		
			core.ConsoleNetworkInterface cint1 = (core.ConsoleNetworkInterface)node.getNIC("cua0");
			
			core.ConsoleLink lnk = (core.ConsoleLink)(cint1).getConnectedLink();
		
			if(lnk == null){
				JOptionPane.showMessageDialog(this,
						"Console interface has no connection!",	"Terminal", JOptionPane.ERROR_MESSAGE);
			}
		
			core.NetworkLayerDevice device = (core.NetworkLayerDevice)lnk.getParent(nodeName);
			
			core.ConsoleNetworkInterface cint2 = (core.ConsoleNetworkInterface)device.getNIC("cua0");
			
			if(!device.On || !cint2.isUP() || !cint1.isUP()){
				JOptionPane.showMessageDialog(this,
						"Console interface has no connection or is down!",	"Terminal", JOptionPane.ERROR_MESSAGE);
			}
			
			if(cint1.databits == cint2.databits && 
			   cint1.flowcontrol == cint2.flowcontrol &&
			   cint1.parity == cint2.parity &&
			   cint1.speed == cint2.speed &&
			   cint1.stopbits == cint2.stopbits &&
			   cint1.speed == 9600 &&
			   cint1.flowcontrol == core.ConsoleNetworkInterface.FLOWCONTROL_NONE){
					
				RunCmd(device.getName());
			}else{
				JOptionPane.showMessageDialog(this,
						"Incorrect port params!",	"Terminal", JOptionPane.ERROR_MESSAGE);
			}
			
		}catch(Exception e){
			JOptionPane.showMessageDialog(this,
					"Unrecoverable error!",	"Terminal", JOptionPane.ERROR_MESSAGE);
			
		}
			
	}
	
	public void Export(){
		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle("Export to...");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new HTMLFilter());
		chooser.addChoosableFileFilter(new JPGFilter());
		chooser.addChoosableFileFilter(new PNGFilter());
		

		int returnVal = chooser.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			// Get Selected file for saving GUI information
			File tempFile = chooser.getSelectedFile();
			
			String file = tempFile.getPath();
			
			if(tempFile.exists()){
				if(JOptionPane.showConfirmDialog(this, "File exists! Overwrite?",
				"Confirm overwriting!", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
					return;
			}
			
			if (chooser.getFileFilter() instanceof HTMLFilter)
				save_html_rep(file);
			else if (chooser.getFileFilter() instanceof PNGFilter)
				save_image(file, MainScreen.PNG_PREFIX);
			else if (chooser.getFileFilter() instanceof JPGFilter)
				save_image(file, MainScreen.JPG_PREFIX);
			
		}
	}
		
	/**
	 * Generate and save png
	 * 
	 * @author key
	 */
	private void save_image(String saveas, String prefix) {
		// Get image to write to a file
		BufferedImage bufferedImage = getDesktop();
		File file;
		
		if (saveas.contains("." + prefix))
			file = new File(saveas);
		else
			file = new File(saveas + "."
					+ prefix);

		// Save as PNG
		try{ 
			ImageIO.write(bufferedImage, prefix, file);
		}catch(IOException e){ e.printStackTrace(); }
		
		setStatus(prefix + " file was saved succesfully.");
	}

	/**
	 * Generate and save html report file
	 * 
	 * @author gift (sourceforge.net user)
	 */
	private void save_html_rep(String saveas) {

		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle("Save HTML report...");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new HTMLFilter());

		int returnVal;

		if (saveas == null) {
			returnVal = chooser.showSaveDialog(this);
		} else {
			returnVal = JFileChooser.APPROVE_OPTION;
		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			// Get Selected file for saving GUI information
			File tempFile = chooser.getSelectedFile();

			// TODO Test if prefix isnt already added!!!!!
			// Set the report save file
			if (saveas == null) {
				if (tempFile.getPath().contains("." + MainScreen.HTM_PREFIX))
					simSaveFile = new File(tempFile.getPath());
				else
					simSaveFile = new File(tempFile.getPath() + "."
							+ MainScreen.HTM_PREFIX);
			}

			// Generate report and save Simulation.
			generate_html_report(simSaveFile.getAbsolutePath(), tempFile
					.getAbsolutePath()
					+ ".jpg");

			System.out.println("Generating HTML report file");
			System.out.println("Simulation was saved succesfully.");
			System.out.println("File " + simSaveFile.getName()
					+ " has been saved to " + simSaveFile.getAbsolutePath());
			System.out.println();
			setStatus("HTML report was saved succesfully.");
		}

	}

	void BuildSandbox()

	{

	}

	/**
	 * 
	 * Opens a saved Simulation.
	 * 
	 * 
	 * 
	 */

	public void Open() {

		printLayerInfo(false);

		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle("Open File ...");
		chooser.setMultiSelectionEnabled(false);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new JNSTFilter());

		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try {
				File inFile = chooser.getSelectedFile();
				File simFile;

				if (inFile.getPath().contains("." + MainScreen.SIM_PREFIX))
					simFile = new File(inFile.getPath());
				else
					simFile = new File(inFile.getPath() + "." + MainScreen.SIM_PREFIX);

				this.refreshMainScreen();

				BufferedReader input = null;

				try {

					input = new BufferedReader(new FileReader(simFile));
					String line = null; // not declared within while loop

					Vector DevicesTurnedOn = new Vector();
					Hashtable links = new Hashtable();
					Vector connections = new Vector();

					while ((line = input.readLine()) != null) {

						String strNodeName;
						String strClassName;
						String coords;
						int intCount;
						int x;
						int y;
						int j;
						// int deviceType = 0;
						boolean on = false;

						strNodeName = line;
						strClassName = input.readLine().trim();
						coords = input.readLine();
						on = Boolean.valueOf(input.readLine().trim());
						intCount = Integer.valueOf(input.readLine()).intValue();

						if (on)
							DevicesTurnedOn.add(strNodeName);

						String xy[] = coords.split(",");

						x = Integer.valueOf(xy[0]).intValue();
						y = Integer.valueOf(xy[1]).intValue();

						Point pnt = new Point(x, y);

						Sim.addNode(strClassName.substring(3).trim(),
								strNodeName, false);
						/*
						 * if(strClassName.contains("GuiHub")){ deviceType = 1;
						 * Sim.addHub(strNodeName, false); }else
						 * if(strClassName.contains("GuiSwitch")){ deviceType =
						 * 1; Sim.addSwitch(strNodeName, false); }else
						 * if(strClassName.contains("GuiWirelessAP")){
						 * deviceType = 1; Sim.addAP(strNodeName, false); }else
						 * if(strClassName.contains("GuiCSUDSU")){ deviceType =
						 * 1; Sim.addCSUDSU(strNodeName, false); }else
						 * if(strClassName.contains("GuiPC")){ deviceType = 2;
						 * Sim.addPC(strNodeName, false); }else
						 * if(strClassName.contains("GuiPrinter")){ deviceType =
						 * 2; Sim.addPrinter(strNodeName, false); }else
						 * if(strClassName.contains("GuiRouter")){ deviceType =
						 * 2; Sim.addRouter(strNodeName, false); }else
						 * if(strClassName.contains("GuiExternalProxy")){
						 * deviceType = 2; Sim.addExternalNAT(strNodeName,
						 * false); }
						 */

						String sieve = "100.00";

						for (j = 1; j <= intCount; j++) {

							line = input.readLine();

							String iface[] = line.split("\\|");

							int LinkType = Integer.valueOf(iface[1]);

							String strLinkName = iface[2];

							sieve = iface[3];

							// sim nodes
							switch (LinkType) {
							case core.NetworkInterface.Ethernet100FX:
								if (!iface[4].contains("#")) {
									Sim
											.getNode(strNodeName)
											.addNetworkInterface(
													iface[0],
													core.NetworkInterface.Ethernet100FX,
													true);
									try {
										((core.EthernetNetworkInterface) Sim
												.getNode(strNodeName).getNIC(
														iface[0])).defaultMACAddress = iface[4];
									} catch (Exception e) {
									}
								} else {
									Sim
											.getNode(strNodeName)
											.addNetworkInterface(
													iface[0],
													core.NetworkInterface.Ethernet100FX,
													false);
								}
								break;
							case core.NetworkInterface.Ethernet10T:
								if (!iface[4].contains("#")) {
									Sim
											.getNode(strNodeName)
											.addNetworkInterface(
													iface[0],
													core.NetworkInterface.Ethernet10T,
													true);
									try {
										// FIXME!!!!!!!
										((core.EthernetNetworkInterface) Sim
												.getNode(strNodeName).getNIC(
														iface[0])).defaultMACAddress = iface[4];
									} catch (Exception e) {
									}
								} else {
									Sim
											.getNode(strNodeName)
											.addNetworkInterface(
													iface[0],
													core.NetworkInterface.Ethernet10T,
													false);
								}
								break;
							case core.NetworkInterface.Wireless:
								Sim.getNode(strNodeName).addNetworkInterface(
										iface[0],
										core.NetworkInterface.Wireless,
										Boolean.valueOf(iface[5]));
								if (!Boolean.valueOf(iface[5])) {
									// ugly hack!
									Sim.getNode(strNodeName)
											.getNetworkInterface(iface[0]).UP();
								}
								try {
									// FIXME!!!!!!!
									((core.EthernetNetworkInterface) Sim
											.getNode(strNodeName).getNIC(
													iface[0])).defaultMACAddress = iface[4];
								} catch (Exception e) {
								}
								break;
							case core.NetworkInterface.Serial:
								if (Sim.getNode(strNodeName) instanceof core.CSUDSU) {
									Sim
											.getNode(strNodeName)
											.addNetworkInterface(
													iface[0],
													core.NetworkInterface.Serial,
													false);
									((core.CSUDSU) Sim.getNode(strNodeName))
											.setLAN(iface[0]);
								} else {
									Sim
											.getNode(strNodeName)
											.addNetworkInterface(
													iface[0],
													core.NetworkInterface.Serial,
													true);
								}
								((core.SerialNetworkInterface) Sim.getNode(
										strNodeName).getNIC(iface[0]))
										.setClockRate(Integer.valueOf(iface[4]));
								break;

							case core.NetworkInterface.Console:
								Sim.getNode(strNodeName).addNetworkInterface(
										iface[0],
										core.NetworkInterface.Console, false);
								break;

							case core.NetworkInterface.WAN:
								Sim.getNode(strNodeName).addNetworkInterface(
										iface[0], core.NetworkInterface.WAN,
										false);
								((core.CSUDSU) Sim.getNode(strNodeName))
										.setWAN(iface[0]);
								((core.WANNetworkInterface) Sim.getNode(
										strNodeName).getNIC(iface[0]))
										.setConnType(Integer.valueOf(iface[4]));
								((core.WANNetworkInterface) Sim.getNode(
										strNodeName).getNIC(iface[0]))
										.setServer(Boolean.valueOf(iface[5]));
								((core.WANNetworkInterface) Sim.getNode(
										strNodeName).getNIC(iface[0]))
										.setConnHost(iface[6]);
								((core.WANNetworkInterface) Sim.getNode(
										strNodeName).getNIC(iface[0]))
										.setConnPort(Integer.valueOf(iface[7]));
								if (iface.length > 8)
									((core.WANNetworkInterface) Sim.getNode(
											strNodeName).getNIC(iface[0]))
											.setConnService(iface[8]);
								break;
							}

							// interfaces
							if (!strLinkName.contains("null")) {

								if (((String) links.get(strLinkName)) == null) {

									links.put(strLinkName, strNodeName + "|"
											+ iface[0] + "|" + sieve);

								} else {

									String ln[] = ((String) links
											.get(strLinkName)).split("\\|");

									switch (LinkType) {
									case core.NetworkInterface.Ethernet10T:
										Sim.addEthernetLink(strLinkName, ln[0],
												ln[1], strNodeName, iface[0],
												ln[2]);
										break;
									case core.NetworkInterface.Ethernet100FX:
										Sim.addEthernetLink(strLinkName, ln[0],
												ln[1], strNodeName, iface[0],
												ln[2]);
										break;
									case core.NetworkInterface.Console:
										Sim.addConsoleLink(strLinkName, ln[0],
												ln[1], strNodeName, iface[0]);
										break;
									case core.NetworkInterface.Serial:
										Sim.addSerialLink(strLinkName, ln[0],
												ln[1], strNodeName, iface[0]);
										break;
									}

									connections.add(strLinkName + "|" + ln[0]
											+ "|" + strNodeName + "|"
											+ LinkType);
								}

							}

						}

						try {
							Class gNode = Class
									.forName("guiUI." + strClassName);

							Constructor gCon = gNode.getConstructor(
									String.class, MainScreen.class);

							GuiNode tempNode = (GuiNode) gCon
									.newInstance(new Object[] { strNodeName,
											this });

							tempNode.setNodeLocation(pnt);
							tempNode.setEnabled(false);
							tempNode.updateInterfacesMenu();
							Sandbox.add(tempNode);
							Sandbox.setLayer((Component) tempNode, 3, 0);
							GUInodeTable.put(strNodeName, tempNode);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// add gui nodes
						/*
						 * if(strClassName.contains("GuiHub")){ GuiHub tempHub =
						 * new GuiHub(strNodeName,this);
						 * 
						 * }else if(strClassName.contains("GuiSwitch")){
						 * GuiSwitch tempSwitch = new
						 * GuiSwitch(strNodeName,this);
						 * tempSwitch.setNodeLocation(pnt);
						 * tempSwitch.setEnabled(false);
						 * Sandbox.add(tempSwitch);
						 * Sandbox.setLayer(tempSwitch,3,0);
						 * GUInodeTable.put(strNodeName,tempSwitch); }else
						 * if(strClassName.contains("GuiCSUDSU")){ GuiCSUDSU
						 * tempCSUDSU = new GuiCSUDSU(strNodeName,this);
						 * tempCSUDSU.setNodeLocation(pnt);
						 * tempCSUDSU.setEnabled(false);
						 * Sandbox.add(tempCSUDSU);
						 * Sandbox.setLayer(tempCSUDSU,3,0);
						 * GUInodeTable.put(strNodeName,tempCSUDSU); }else
						 * if(strClassName.contains("GuiPC")){ GuiPC tempPC =
						 * new GuiPC(strNodeName,this);
						 * tempPC.setNodeLocation(pnt);
						 * tempPC.setEnabled(false); Sandbox.add(tempPC);
						 * Sandbox.setLayer(tempPC,3,0);
						 * GUInodeTable.put(strNodeName,tempPC); }else
						 * if(strClassName.contains("GuiPrinter")){ GuiPrinter
						 * tempPC = new GuiPrinter(strNodeName,this);
						 * tempPC.setNodeLocation(pnt);
						 * tempPC.setEnabled(false); Sandbox.add(tempPC);
						 * Sandbox.setLayer(tempPC,3,0);
						 * GUInodeTable.put(strNodeName,tempPC); }else
						 * if(strClassName.contains("GuiRouter")){ GuiRouter
						 * tempRouter = new GuiRouter(strNodeName,this);
						 * tempRouter.setNodeLocation(pnt);
						 * tempRouter.setEnabled(false);
						 * Sandbox.add(tempRouter);
						 * Sandbox.setLayer(tempRouter,3,0);
						 * GUInodeTable.put(strNodeName,tempRouter); }else
						 * if(strClassName.contains("GuiExternalProxy")){
						 * GuiExternalProxy tempRouter = new
						 * GuiExternalProxy(strNodeName,this);
						 * tempRouter.setNodeLocation(pnt);
						 * tempRouter.setEnabled(false);
						 * Sandbox.add(tempRouter);
						 * Sandbox.setLayer(tempRouter,3,0);
						 * GUInodeTable.put(strNodeName,tempRouter); }else
						 * if(strClassName.contains("GuiWirelessAP")){
						 * GuiWirelessAP tempRouter = new
						 * GuiWirelessAP(strNodeName,this);
						 * tempRouter.setNodeLocation(pnt);
						 * tempRouter.setEnabled(false);
						 * Sandbox.add(tempRouter);
						 * Sandbox.setLayer(tempRouter,3,0);
						 * GUInodeTable.put(strNodeName,tempRouter); }
						 */

						// config
						line = input.readLine();
						if (line.contains("#config")) {
							line = input.readLine();
							while (!line.contains("#endconfig")) {
								((core.NetworkLayerDevice) Sim
										.getNode(strNodeName)).getConfig().add(
										line, core.DeviceConfig.STARTUP_CONFIG);
								line = input.readLine();
							}
						}

						// data
						line = input.readLine();
						if (line.contains("#data")) {
							
							boolean more_data = true;
							
							while(more_data){
								String file;
								int lines;
								String data = "";
							
								line = input.readLine();
								if(line.contains("#enddata")){
									more_data = false;
									break;
								}
								
								file = line;
								
								lines = Integer.valueOf(input.readLine());
								
								for(int i = 0; i<lines; i++){
									line = input.readLine();
									if (line == null || line.contains("#enddata"))
										break;
									data += line;
								}
											
								((core.NetworkLayerDevice) Sim
										.getNode(strNodeName)).loadDataFile(file, lines, data);
							}
						}

						// /

					}// while

					// add graphical connections
					for (int i = 0; i < connections.size(); i++) {
						String[] gc = ((String) connections.get(i))
								.split("\\|");
						this.createLink(gc[0], gc[1], gc[2], Integer
								.valueOf(gc[3]));
					}

					// turn on devices
					for (int i = 0; i < DevicesTurnedOn.size(); i++) {
						String nodeName = (String) DevicesTurnedOn.get(i);
						Sim.getNode(nodeName).turnOn();
						((GuiNode) GUInodeTable.get(nodeName)).setEnabled(true);
					}

					this.refreshNodeInformationTab();
					simSaveFile = simFile;

				} finally {
					try {
						if (input != null) {
							// flush and close both "input" and its underlying
							// FileReader
							input.close();
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void Import() {

		printLayerInfo(false);

		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle("Import simulation from previous versions ...");

		chooser.setMultiSelectionEnabled(false);

		chooser.setAcceptAllFileFilterUsed(false);

		chooser.addChoosableFileFilter(new JFSTFilter());

		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try {

				File inFile = chooser.getSelectedFile();
				Vector DevicesTurnedOn = new Vector();

				// System.out.println(inFile.getPath());

				File simFile;

				if (inFile.getPath().contains("." + MainScreen.OLD_PREFIX))
					simFile = new File(inFile.getPath());

				else
					simFile = new File(inFile.getPath() + "." + MainScreen.OLD_PREFIX);

				this.refreshMainScreen();

				BufferedReader input = null;

				try {

					// use buffering

					// this implementation reads one line at a time

					input = new BufferedReader(new FileReader(simFile));

					String line = null; // not declared within while loop

					Hashtable links = new Hashtable();

					while ((line = input.readLine()) != null) {

						String strNodeName;

						String strClassName;

						String coords;

						int intCount;

						int x;

						int y;

						int j;

						int deviceType = 0;

						strNodeName = line;

						strClassName = input.readLine().trim();

						coords = input.readLine();

						intCount = Integer.valueOf(input.readLine()).intValue();

						String xy[] = coords.split(",");

						x = Integer.valueOf(xy[0]).intValue();

						y = Integer.valueOf(xy[1]).intValue();

						// System.out.println(intCount + " " + x + " " + y);

						Point pnt = new Point(x, y);

						if (strClassName.contains("GuiHub")) {

							deviceType = 1;

							Sim.addHub(strNodeName, true);
							for (int i = 0; i < 5; i++) {
								Sim
										.getNode(strNodeName)
										.addNetworkInterface(
												core.NetworkInterface
														.getIntName(core.NetworkInterface.Ethernet10T)
														+ String.valueOf(i),
												core.NetworkInterface.Ethernet10T,
												false);
							}

							GuiHub tempHub = new GuiHub(strNodeName, this);

							tempHub.setNodeLocation(pnt);

							tempHub.setEnabled(true);

							Sandbox.add(tempHub);

							Sandbox.setLayer((Component) tempHub, 3, 0);

							GUInodeTable.put(strNodeName, tempHub);

						} else if (strClassName.contains("GuiSwitch")) {

							deviceType = 1;

							Sim.addSwitch(strNodeName, true);
							for (int i = 0; i < 8; i++) {
								Sim
										.getNode(strNodeName)
										.addNetworkInterface(
												core.NetworkInterface
														.getIntName(core.NetworkInterface.Ethernet10T)
														+ String.valueOf(i),
												core.NetworkInterface.Ethernet10T,
												false);
							}

							GuiSwitch tempSwitch = new GuiSwitch(strNodeName,
									this);

							tempSwitch.setNodeLocation(pnt);

							tempSwitch.setEnabled(true);
							
							tempSwitch.updateInterfacesMenu();

							Sandbox.add(tempSwitch);

							Sandbox.setLayer(tempSwitch, 3, 0);

							GUInodeTable.put(strNodeName, tempSwitch);

						} else if (strClassName.contains("GuiPC")) {

							deviceType = 2;

							Sim.addPC(strNodeName, true);
							Sim
									.getNode(strNodeName)
									.addNetworkInterface(
											core.NetworkInterface
													.getIntName(core.NetworkInterface.Ethernet10T)
													+ "0",
											core.NetworkInterface.Ethernet10T,
											true);
							Sim
									.getNode(strNodeName)
									.addNetworkInterface(
											core.NetworkInterface
													.getIntName(core.NetworkInterface.Console)
													+ "0",
											core.NetworkInterface.Console, true);

							GuiPC tempPC = new GuiPC(strNodeName, this);

							tempPC.setEnabled(true);

							tempPC.setNodeLocation(pnt);
							
							tempPC.updateInterfacesMenu();

							Sandbox.add(tempPC);

							Sandbox.setLayer(tempPC, 3, 0);

							GUInodeTable.put(strNodeName, tempPC);

						} else if (strClassName.contains("GuiRouter")) {

							deviceType = 2;

							Sim.addRouter(strNodeName, true);
							Sim
									.getNode(strNodeName)
									.addNetworkInterface(
											core.NetworkInterface
													.getIntName(core.NetworkInterface.Ethernet10T)
													+ "0",
											core.NetworkInterface.Ethernet10T,
											true);
							Sim
									.getNode(strNodeName)
									.addNetworkInterface(
											core.NetworkInterface
													.getIntName(core.NetworkInterface.Ethernet10T)
													+ "1",
											core.NetworkInterface.Ethernet10T,
											true);
							Sim
									.getNode(strNodeName)
									.addNetworkInterface(
											core.NetworkInterface
													.getIntName(core.NetworkInterface.Serial)
													+ "0",
											core.NetworkInterface.Serial, true);
							Sim
									.getNode(strNodeName)
									.addNetworkInterface(
											core.NetworkInterface
													.getIntName(core.NetworkInterface.Console)
													+ "0",
											core.NetworkInterface.Console, true);

							GuiRouter tempRouter = new GuiRouter(strNodeName,
									this);

							tempRouter.setNodeLocation(pnt);

							tempRouter.setEnabled(true);
							
							tempRouter.updateInterfacesMenu();

							Sandbox.add(tempRouter);

							Sandbox.setLayer(tempRouter, 3, 0);

							GUInodeTable.put(strNodeName, tempRouter);

						}

						String sieve = "100.00";

						for (j = 1; j <= intCount; j++) {

							line = input.readLine();

							String iface[] = line.split("\\|");

							String strLinkName = iface[1];

							if (deviceType == 2
									&& !iface[2].contains("Not Applicable")) {

								if (!iface[2].contains("null")) {

									
									((core.NetworkLayerDevice)Sim.getNode(strNodeName))
										.setTCPIPSettings(iface[0], iface[2], iface[3]);
									
								}

								try {
									((core.EthernetNetworkInterface) Sim
											.getNode(strNodeName).getNIC(
													iface[0])).defaultMACAddress = iface[5];
									// Sim.setMACAddress(strNodeName,iface[0],
									// iface[5]);

									sieve = iface[6];

								} catch (Exception e) {
								}

								if (!iface[4].contains("null"))
									((core.NetworkLayerDevice)Sim.getNode(strNodeName))
									.setTCPIPSettings(iface[4]);

							} else if (deviceType == 1) {

							}

							if (!strLinkName.contains("null")) {

								if (((String) links.get(strLinkName)) == null) {

									links.put(strLinkName, strNodeName + "|"
											+ iface[0] + "|" + sieve);

								} else {

									String ln[] = ((String) links
											.get(strLinkName)).split("\\|");

									if (iface[0].contains("eth")) {
										Sim.addEthernetLink(strLinkName, ln[0],
												ln[1], strNodeName, iface[0],
												ln[2]);
										this
												.createLink(
														strLinkName,
														ln[0],
														strNodeName,
														core.NetworkInterface.Ethernet10T);
									} else if (iface[0].contains("cua")) {
										Sim.addConsoleLink(strLinkName, ln[0],
												ln[1], strNodeName, iface[0]);
										this.createLink(strLinkName, ln[0],
												strNodeName,
												core.NetworkInterface.Console);
									} else if (iface[0].contains("ser")) {
										Sim.addSerialLink(strLinkName, ln[0],
												ln[1], strNodeName, iface[0]);
										this.createLink(strLinkName, ln[0],
												strNodeName,
												core.NetworkInterface.Serial);
									}
								}

							}

						}

						try {
							
							if(Sim.getNode(strNodeName) instanceof core.DataLinkLayerDevice)
								Sim.getNode(strNodeName).ifacesLinkUP();
							else
								Sim.getNode(strNodeName).ifacesUP();
							
							Route_entry r;

							line = input.readLine();

							for (int i = 0; i < Integer.valueOf(line)
									.intValue(); i++) {
								String rs = input.readLine();
								String rts[] = rs.split("\\|");

								if(rts[0].equals("default"))
									rts[2] = "0.0.0.0";
								
								if(rts[2].equals("null"))
									rts[2] = "255.255.0.0";
								
								/*r = new Route_entry(rts[0], rts[1], rts[2],
										rts[4], Integer.valueOf(rts[3])
												.intValue());*/
								//((core.NetworkLayerDevice)Sim.getNode(strNodeName)).addRoute(r);
								((core.NetworkLayerDevice)Sim.getNode(strNodeName)).getConfig()
									.executeCommand("ip route " + rts[0] + " " + rts[2] + " " + rts[1] + " " + rts[4]);
							}
						
							Sim.getNode(strNodeName).execCmd("write mem");
						} catch (Exception e) {

						}
						
						

					}

				} finally {

					try {

						if (input != null) {

							// flush and close both "input" and its underlying
							// FileReader

							input.close();

						}

					}

					catch (IOException ex) {

						ex.printStackTrace();

					}

				}

				this.refreshNodeInformationTab();

				// FileInputStream in = new FileInputStream(simFile);

				// ObjectInputStream s = new ObjectInputStream(in);

				// Sim = (Simulation) s.readObject();

				// Sim.displayNodes();

				// BuildSandbox();

				// save the file for later use with the saveSim method

				// simSaveFile = simFile;

			} catch (FileNotFoundException e) {

				// TODO: handle exception

				e.printStackTrace();

			} catch (IOException e) {

				// TODO: handle exception

				e.printStackTrace();

			} catch (Exception e) {

				// TODO: handle exception

				e.printStackTrace();

			}

		}

	}

	/**
	 * 
	 * This method is called from the menu bar ($menu bar item name )and the
	 * right clicking on a node.
	 * 
	 * If the menu bar item is selected it will get a array of the node names
	 * from within the
	 * 
	 * simulation. If there are currently no node's within the simulation a
	 * message will be displayed to
	 * 
	 * the user. If the user select the ($rightclick item name ) only that node
	 * name is passed to the setipaddressdialog class.
	 * 
	 * @author luke_hamilton
	 * 
	 * @param InNodeName
	 * 
	 */

	public void setTCPIPProperties(String InNodeName) {

		Object[] nodesArray = null;

		int selectedIndex = -1;

		// if null is passed from the menubar item

		// Get array of nodes from within the simulation

		if (InNodeName == null) {

			ArrayList Values = new ArrayList();

			Enumeration enum1 = GUInodeTable.keys();

			while (enum1.hasMoreElements()) {

				String key = (String) enum1.nextElement();

				GuiNode tempNode = (GuiNode) GUInodeTable.get(key);

				if (tempNode instanceof NetworkLayerDevice) {

					Values.add(key);

				}

			}

			nodesArray = Values.toArray();

			// Else pass the selected node name

		} else {

			nodesArray = new Object[1];

			nodesArray[0] = InNodeName;

			selectedIndex = 0;

		}

		// test that there are any node within the simulation

		if (nodesArray.length != 0) {

			new SetTCPIPPropertiesDialog(this, nodesArray, selectedIndex, Sim);

			this.refreshNodeInformationTab();

		} else

			JOptionPane.showMessageDialog(this,
					"There are currently no node's within the simulation",
					"Warning!", JOptionPane.WARNING_MESSAGE);

	}

	public void ShowHubState(String InNodeName) {

		int state = Sim.getNode(InNodeName).getState();

		try {
			if (Sim.getNode(InNodeName) instanceof core.Switch) {

				String cache = ((core.Switch)Sim.getNode(InNodeName)).getCache();

				if (state == -1) {

					JOptionPane
							.showMessageDialog(
									this,
									"There are currently no node's within the simulation",
									"Warning!", JOptionPane.WARNING_MESSAGE);

				} else if (state == 0) {

					JOptionPane.showMessageDialog(this,
							"Device state: normal\n\nSwitch cache:\n-----------\n"
									+ cache, InNodeName + ": device state",
							JOptionPane.INFORMATION_MESSAGE);

				} else if (state == 1) {

					JOptionPane.showMessageDialog(this,
							"Device state: freezed", "Warning! " + InNodeName
									+ ": device state",
							JOptionPane.WARNING_MESSAGE);

				}

			} else {

				if (state == -1) {

					JOptionPane
							.showMessageDialog(
									this,
									"There are currently no node's within the simulation",
									"Warning!", JOptionPane.WARNING_MESSAGE);

				} else if (state == 0) {

					JOptionPane.showMessageDialog(this, "Device state: normal",
							InNodeName + ": device state",
							JOptionPane.INFORMATION_MESSAGE);

				} else if (state == 1) {

					JOptionPane.showMessageDialog(this,
							"Device state: freezed", "Warning! " + InNodeName
									+ ": device state",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		} catch (Exception e) {

			JOptionPane.showMessageDialog(this,
					"There are currently no node's within the simulation",
					"Warning!", JOptionPane.WARNING_MESSAGE);

		}
	}

	public void Reset(String InNodeName) {

		insertInConsole(InNodeName, "(none)", "Hardware", "Device reseted.");
		
		Sim.getNode(InNodeName).Reset();
		
	}

	/**
	 * 
	 * This Method is called from the individual guiNodes as they are moved
	 * 
	 * around the screen. It is passed the name of the Link, and Integer
	 * 
	 * representing the end of the link that needs to be moved either 1 or 2
	 * 
	 * and a point object that gives the current location of the mouse.
	 * 
	 * @param inLinkName
	 *            The name of the link to be moved.
	 * 
	 * @param inEnd
	 *            Which end of the link to move (1 or 2).
	 * 
	 * @param inPoint
	 *            The Location to move the particular end to.
	 * 
	 */

	public void moveLink(String inLinkName, int inEnd, Point inPoint) {

		if (inLinkName != null) {

			Sandbox.moveLine(inLinkName, inEnd, inPoint);

		}

	}

	/**
	 * 
	 * This method will shake the a container.
	 * 
	 * @author luke_hamilton
	 * 
	 * @param Container
	 *            c The container to be shaked!
	 * 
	 */

	public void shakeDiaLog(Container c) {

		int w = c.getWidth();

		int h = c.getHeight();

		int x = c.getX();

		int y = c.getY();

		int speed = 15;

		for (int i = 0; i < 5; i++) {

			try {

				c.setBounds(x - 10, y - 10, w, h);

				Thread.sleep(speed);

				c.setBounds(x + 10, y + 10, w, h);

				Thread.sleep(speed);

				c.setBounds(x - 10, y + 10, w, h);

				Thread.sleep(speed);

				c.setBounds(x + 10, y - 10, w, h);

				Thread.sleep(speed);

			} catch (InterruptedException e) {
			}

		}

		c.setBounds(x, y, w, h);

	}

	/**
	 * Clears the Console.
	 * 
	 */
	public void clearConsole() {
		mConsole.setRowCount(0);
	}

	/**
	 * Generates HTML code from text in console. Is not used.
	 * generate_html_console_out_rapid is used instead.
	 * 
	 * @author gift (sourceforge.net user) P.S. Generation is independent of
	 *         mConsole structure => slower that
	 *         generate_html_console_out_rapid. Has some bugs with coloring that
	 *         are not fixed due to my lazy mood.
	 */
	public StringBuffer generate_html_console_out() {

		int i, j;
		int clr = 0xFFFFFF;
		int mask = 0xFFFFFF;
		/*
		 * When I have done generate_html_console_out_rapid I decided to comment
		 * out StringBuffer(655360). There is a chance that java will allocate
		 * memory ;) If you plan to use generate_html_console_out instead of
		 * generate_html_console_out_rapid you'd better use
		 * StringBuffer(655360).
		 */

		// UNCOMMENT NEXT LINE
		// StringBuffer out = new StringBuffer(655360);
		// COMMENT NEXT LINE
		StringBuffer out = new StringBuffer(1024);

		String cur;
		boolean color_selected = false;

		if (mConsole.getColumnCount() > 0 && mConsole.getRowCount() > 0) {
			out
					.append("<!-- Console output part -->\r\n<TABLE align=\"center\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\" style='border:solid windowtext 1.0pt;'>\r\n<TR bgColor=#FFFFFF>");
			// Form table header
			for (i = 0; i < mConsole.getColumnCount(); i++) {
				out.append("<TD>" + mConsole.getColumnName(i) + "</TD>");
			}
			out.append("</TR>\r\n<!-- main console output body -->\r\n\r\n");

			// Form table body
			for (j = 0; j < mConsole.getRowCount(); j++) {
				color_selected = false;
				cur = "";

				// add table row
				for (i = 0; i < mConsole.getColumnCount(); i++) {
					cur += "<TD vAlign=\"top\">"
							+ (String) mConsole.getValueAt(j, i)
							+ "&nbsp;</TD>";
					// Select TR bgColor.
					if (!color_selected) {
						if (cur.contains("Network")) {
							clr = MainScreen.NETWORK_LAYER_COLOR.getRGB();
							color_selected = true;
						} else if (cur.contains("Link")) {
							clr = MainScreen.LINK_LAYER_COLOR.getRGB();
							color_selected = true;
						} else if (cur.contains("Transport")) {
							clr = MainScreen.TRANSPORT_LAYER_COLOR.getRGB();
							color_selected = true;
						} else if (cur.contains("Application")) {
							clr = MainScreen.APPLICATION_LAYER_COLOR.getRGB();
							color_selected = true;
						} else if (cur.contains("Hardware")) {
							clr = MainScreen.HARDWARE_LAYER_COLOR.getRGB();
							color_selected = true;
						} else if (cur.contains("*SYSTEM*")) {
							clr = MainScreen.SYSTEM_LAYER_COLOR.getRGB();
							color_selected = true;
						}
					}
				}
				// Integer.toHexString(clr).toUpperCase()
				out.append("<TR bgColor=#" + String.format("%06X", clr & mask)
						+ ">\r\n" + cur + "\r\n</TR>\r\n");
			}
			out
					.append("</TABLE>\r\n<!-- main console output body end -->\r\n\r\n");
		} else {
			out
					.append("<BR />Nothing to show in simulation transactions.<BR />");
		}

		return out;
	}

	/**
	 * Generates HTML code from text in console.
	 * 
	 * @author gift (sourceforge.net user) P.S. Depends on mConsole structure =>
	 *         faster that generate_html_console_out and has less bugs with
	 *         coloring :)
	 */
	public StringBuffer generate_html_console_out_rapid() {

		int i, j;
		int clr = 0xFFFFFF;
		int r_c = mConsole.getRowCount();
		StringBuffer out = new StringBuffer(655360); // 640Kb for a log
														// stripe
		String cur;

		if (mConsole.getColumnCount() > 0 && r_c > 0) {
			out
					.append("<!-- Console output part -->\r\n<TABLE align=\"center\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\" style='border:solid windowtext 1.0pt;'>\r\n<TR bgColor=#FFFFFF>");
			// Form table header
			for (i = 0; i < mConsole.getColumnCount(); i++) {
				out.append("<TD>" + mConsole.getColumnName(i) + "</TD>");
			}
			out.append("</TR>\r\n<!-- main console output body -->\r\n\r\n");

			// Form table body
			for (j = 0; j < r_c; j++) {
				// Select color for TR background
				cur = (String) mConsole.getValueAt(j, 3);
				if (cur.contains("Network")) {
					clr = MainScreen.NETWORK_LAYER_COLOR.getRGB();
				} else if (cur.contains("Link")) {
					clr = MainScreen.LINK_LAYER_COLOR.getRGB();
				} else if (cur.contains("Transport")) {
					clr = MainScreen.TRANSPORT_LAYER_COLOR.getRGB();
				} else if (cur.contains("Application")) {
					clr = MainScreen.APPLICATION_LAYER_COLOR.getRGB();
				} else if (cur.contains("Hardware")) {
					clr = MainScreen.HARDWARE_LAYER_COLOR.getRGB();
				} else if (cur.contains("*SYSTEM*")) {
					clr = MainScreen.SYSTEM_LAYER_COLOR.getRGB();
				}

				// add table row
				out.append("<TR bgColor=#"
						+ String.format("%06X", clr & 0xFFFFFF) + ">\r\n");
				for (i = 0; i < 5; i++) {
					out.append("<TD vAlign=\"top\">"
							+ (String) mConsole.getValueAt(j, i)
							+ "&nbsp;</TD>");
				}
				out.append("\r\n</TR>\r\n");
			}
			out
					.append("</TABLE>\r\n<!-- main console output body end -->\r\n\r\n");
		} else {
			out
					.append("<BR />Nothing to show in simulation transactions.<BR />");
		}

		return out;
	}

	/**
	 * Generates HTML code from text in node information.
	 * 
	 * @author gift (sourceforge.net user)
	 */
	public StringBuffer generate_html_node_info() {

		int i = 0;
		StringBuffer out = new StringBuffer(10240); // approx 10 units in
													// simulation
		String cur_col;

		if (!GUInodeTable.isEmpty()) {
			Enumeration enu = GUInodeTable.keys();
			String strCurrentNodeName = "";

			out
					.append("<!-- Node information part -->\r\n<TABLE align=\"center\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\" style='border:solid windowtext 1.0pt;'>\r\n\r\n");

			while (enu.hasMoreElements()) {
				try {
					Vector NodeData = Sim.getAllNodeInformation((String) enu
							.nextElement());
					Iterator it = NodeData.iterator();

					while (it.hasNext()) {
						Vector LineData = (Vector) it.next();
						String strNodeName = (String) LineData.elementAt(0);
						String strDefGate = (String) LineData.elementAt(1);
						String strInterfaceName = (String) LineData
								.elementAt(2);
						String strInterfaceType = (String) LineData
								.elementAt(3);
						String strMAC = (String) LineData.elementAt(4);
						String strIP = (String) LineData.elementAt(5);
						String strSubnet = (String) LineData.elementAt(6);
						String strLinkName = (String) LineData.elementAt(7);

						if (!strCurrentNodeName.equals(strNodeName)) {
							strCurrentNodeName = strNodeName;
							cur_col = ((i ^= 1) == 0) ? "<TR bgColor=#9999FF>"
									: "<TR bgColor=#FF9999>";

							out
									.append("<!-- Simulation device -->\r\n"
											+ cur_col
											+ "\r\n"
											+ "<TD>Name: "
											+ strNodeName
											+ "</TD>\r\n"
											+ "<TD>Default gateway: "
											+ strDefGate
											+ "</TD>\r\n"
											+ "<TD>&nbsp;</TD><TD>&nbsp;</TD><TD>&nbsp;</TD><TD>&nbsp;</TD>\r\n</TR>\r\n\t<!-- device interfaces-->\r\n");
						}
						out.append("\t<TR bgColor=#CCCCCC>\r\n");
						out.append("\t<TD>Interface: " + strInterfaceName
								+ "</TD>\r\n" + "\t<TD>Type: "
								+ strInterfaceType + "</TD>\r\n"
								+ "\t<TD>MAC address: " + strMAC + "</TD>\r\n"
								+ "\t<TD>IP address: " + strIP + "</TD>\r\n"
								+ "\t<TD>Subnet mask: " + strSubnet
								+ "</TD>\r\n" + "\t<TD>Link name: "
								+ strLinkName + "</TD>\r\n");
						out.append("\t</TR>\r\n");
					}
				} catch (Exception e) {
					addToConsole(e.toString());
				}
			}
			out
					.append("</TABLE>\r\n<!-- Node information output body -->\r\n\r\n");
		} else {
			out.append("<BR />Nothing to show in node information.<BR />");
		}

		return out;
	}

	class JFSTFilter extends FileFilter {
		public String getDescription() {
			return "Old Simulation File (*." + MainScreen.OLD_PREFIX + ")";
		}

		public boolean accept(File f) {
			if (f.isDirectory() || f.getName().endsWith(MainScreen.OLD_PREFIX)) {
				return true;
			}

			return false;
		}
	}

	class JNSTFilter extends FileFilter {
		public String getDescription() {
			return "Simulation File (*." + MainScreen.SIM_PREFIX + ")";
		}

		public boolean accept(File f) {
			if (f.isDirectory() || f.getName().endsWith(MainScreen.SIM_PREFIX)) {
				return true;
			}
			return false;
		}

	}

	/**
	 * @author gift (sourceforge.net user)
	 */
	class HTMLFilter extends FileFilter {
		/**
		 * This method returns the Description that is in the File choosers
		 * Dialog
		 */
		public String getDescription() {
			return "Hypertext markup language file (*." + MainScreen.HTM_PREFIX + ")";
		}

		/**
		 * This method returns true or false if a file has the prefix
		 */
		public boolean accept(File f) {
			if (f.isDirectory() || f.getName().endsWith(MainScreen.HTM_PREFIX)) {
				return true;
			}
			return false;
		}
	}
	
	class PNGFilter extends FileFilter {
		/**
		 * This method returns the Description that is in the File choosers
		 * Dialog
		 */
		public String getDescription() {
			return "Portable Network Graphics image (*." + MainScreen.PNG_PREFIX + ")";
		}

		/**
		 * This method returns true or false if a file has the prefix
		 */
		public boolean accept(File f) {
			if (f.isDirectory() || f.getName().endsWith(MainScreen.PNG_PREFIX)) {
				return true;
			}
			return false;
		}
	}
	
	class JPGFilter extends FileFilter {
		/**
		 * This method returns the Description that is in the File choosers
		 * Dialog
		 */
		public String getDescription() {
			return "JPEG image (*." + MainScreen.JPG_PREFIX + ")";
		}

		/**
		 * This method returns true or false if a file has the prefix
		 */
		public boolean accept(File f) {
			if (f.isDirectory() || f.getName().endsWith(MainScreen.JPG_PREFIX)) {
				return true;
			}
			return false;
		}
	}


	/**
	 * 
	 * This method will return the current hight of the
	 * 
	 * combined Menu and toolbars. It is used when drawing
	 * 
	 * the line from one Node to Another.
	 * 
	 * @return int Combined hight of MenuBar ToolBar and TitleBar
	 * 
	 */

	public int getMenuToolbarHeight() {

		JMenuBar tempBar = this.getJMenuBar();

		int mh = tempBar.getHeight() * 2;

		int ml = tempBar.getY();

		int th = SimToolBar.getHeight();

		return mh + ml + th;

	}

	/**
	 * 
	 * Dynamically resized the DividerLocation of the split pane
	 * 
	 * @param ComponentEvent
	 *            arg0 Should never pass something in explicitly
	 * 
	 */

	public void componentResized(ComponentEvent arg0) {

		pSplit.setDividerLocation(.8);

	}

	// This code is required to implement ComponentListener.

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentShown(ComponentEvent arg0) {
	}

	/**
	 * 
	 * Clears the node information console tab.
	 * 
	 * 
	 * 
	 */

	public void clearNodeInformation()

	{

		pnlNodeInformation.setText("");

	}

	/**
	 * 
	 * Everytime something is performed in the Sim, this method is called which
	 * 
	 * will gain all the information about nodes, links etc and display it to
	 * the
	 * 
	 * Node Information Tab.
	 * 
	 * 
	 * 
	 */

	public void refreshNodeInformationTab()

	{

		clearNodeInformation();

		StringBuffer temp = generate_html_node_info();

		pnlNodeInformation.setContentType("text/html");
		pnlNodeInformation.setText(temp.toString());
		pnlNodeInformation.setEditable(false);

	}

	// Invokes the help menu.

	/**
	 * 
	 * Creates a help windows from HelpWindow class
	 * 
	 */

	public void showHelpPane()

	{

		this.refreshNodeInformationTab();

		HelpWindow help = new HelpWindow();

		help.setLocationRelativeTo(null);

		// help.show();
		help.setVisible(true);

	}

	/**
	 * Generates HTML report of the simulation
	 * 
	 * @author gift (sourceforge.net user)
	 */
	public void generate_html_report(String path, String gr_path) {
		FileWriter f_out; // html file
		File g_out; // graphics file
		String gr_path2 = ""; // to insert in HTML file

		// ///////////////////////////////////////////////
		// PART 1. Generate HTML file.
		// ///////////////////////////////////////////////

		// Process gr_path: remove everything up to last / or \
		if (gr_path.contains("\\")) {
			gr_path2 = gr_path.substring(gr_path.lastIndexOf('\\') + 1, gr_path
					.length());
		} else if (gr_path.contains("/")) {
			gr_path2 = gr_path.substring(gr_path.lastIndexOf('/') + 1, gr_path
					.length());
		} else {
			gr_path2 = gr_path;
		}
		try {
			f_out = new FileWriter(path);

			f_out
					.write("<HTML>\r\n"
							+ "<HEAD>\r\n"
							+ "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=windows-1251\">\r\n"
							+ "<META HTTP-EQUIV=\"Content-Style-Type\" CONTENT=\"text/css\">\r\n"
							+ "<META HTTP-EQUIV=\"Author\" CONTENT=\"javaNetSim, http://sf.net/projects/javanetsim\">\r\n"
							+ "<META HTTP-EQUIV=\"Keywords\" CONTENT=\"javaNetSim, network simulator, eth, ethernet, nets, ip, tcp, udp, switch, hub, dod\">\r\n"
							+ "<!-- <LINK REL=stylesheet type=text/css href=\"style.css\"> -->\r\n"
							+ "<TITLE>javaNetSim HTML report</TITLE>\r\n"
							+ "</HEAD>\r\n<BODY>\r\n");
			f_out
					.write("<P>Welcome to javaNetSim report page!</P>\r\n"
							+ "<P>Simulation printscreen: <BR/>"
							+ "<IMG SRC=\""
							+ gr_path2
							+ "\"></P>\r\n"
							+ "<P>Here is the summary of the devices you used in your simulation."
							+ "</P>\r\n<P>");
			f_out.write(generate_html_node_info().toString());
			f_out
					.write("</P><BR/><BR/><P>"
							+ "Here is the transaction log of your simulation (taken from console)."
							+ "</P><P>");
			f_out.write(generate_html_console_out_rapid().toString());
			f_out
					.write("</P>\r\n"
							+ "<!-- Appendix -->\r\n"
							+ "Project page: <A HREF = \"http://sf.net/projects/javanetsim\">http://sf.net/projects/javanetsim</A>, if you found a bug, please, post it to <A HREF = \"http://sf.net/tracker/?atid=784685&group_id=152576\">http://sf.net/tracker/?atid=784685&group_id=152576</A>"
							+ "<!-- close html file -->\r\n</BODY>\r\n</HTML>");
			f_out.close();

			// ///////////////////////////////////////////////
			// PART 2. Generate image file.
			// ///////////////////////////////////////////////

			// Get image to write to a file
			BufferedImage bufferedImage = getDesktop();

			// Save as PNG
			// File file = new File(gr_path);
			// ImageIO.write((RenderedImage) bufferedImage, "png", file);

			// Save as JPEG
			g_out = new File(gr_path);
			ImageIO.write(bufferedImage, "jpg", g_out);

		} catch (IOException ex) {
			addToConsole(ex.toString());
		}
	}
	
	public BufferedImage getDesktop(){
		int width = this.getWidth();
		int height = this.getHeight();
		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		// Create a graphics contents on the buffered image
		Graphics2D g2d = bufferedImage.createGraphics();

		// Draw graphics var1: printscreen
		// g2d.setColor(new Color(0xEEEEEE));
		// g2d.setColor(new Color(0xFFFFFF));
		// g2d.fillRect(0,0, width, height);
		// this.paint(g2d);
		// g2d.dispose();

		// Draw graphics var2: cropped to work area
		this.paint(g2d);

		// Graphics context no longer needed so dispose of it
		g2d.dispose();

		int w = scroller.getWidth();
		int h = scroller.getHeight();
		int x = (int) (scroller.getLocationOnScreen().getX() - this
				.getLocationOnScreen().getX());
		int y = (int) (scroller.getLocationOnScreen().getY() - this
				.getLocationOnScreen().getY());

		BufferedImage bufferedImage2 = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2_2d = bufferedImage2.createGraphics();

		// key's shamanism
		g2_2d.drawImage(bufferedImage, 0, 0, w, h, x + 5, y + 5, x + w
				- scroller.getVerticalScrollBar().getWidth() - 5, y + h
				- scroller.getHorizontalScrollBar().getHeight() - 5, this);

		g2_2d.dispose();
		
		return bufferedImage2;
	}

	public void clearSaveAs()

	{

		simSaveFile = null;

	}

	// This method will delete everything and start a new simulation.

	/**
	 * 
	 * Deletes all nodes from the GUI and the Simulation.
	 * 
	 */

	public boolean refreshMainScreen()

	{

		int result;

		if (isDirty)

		{

			result = JOptionPane.showConfirmDialog(null,
					"Really create a new Simulation?  " +

					"All unsaved information will be lost",

					"Start new Simulation", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);

		}

		else

		{

			result = JOptionPane.YES_OPTION;

		}

		if (result == JOptionPane.YES_OPTION)

		{

			this.setAllHighlightsOff();

			Sandbox.removeAll();

			GUInodeTable.clear();

			Sim.removeAllObjects();

			Sim.clear();

			Sim = null;

			Sim = new Simulation(core.ProtocolStack.TCP_IP);

			Sandbox = null;
			scroller = null;

			Sandbox = new SandBox(this);
			scroller = new JScrollPane();

			scroller.getViewport().add(Sandbox);

			pSplit.setTopComponent(scroller);

			pSplit.setDividerLocation(.8);

			repaint();

			this.refreshNodeInformationTab();

			this.clearConsole();

			isDirty = false;

			return true;

		}

		return false;

	}

	/**
	 * 
	 * This method will add the passed in message to the console.
	 * 
	 * The main point of this method is so out side class can
	 * 
	 * add text easily to the console.
	 * 
	 * @author luke_hamilton
	 * 
	 * @param msg
	 * 
	 */

	public void addToConsole(String msg) {

		// pnlConsole.append(msg);
		System.out.println("CONSOLE:!!!!" + msg);
	}

	/**
	 * 
	 * Prints the arp table of a selected Node.
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inNodeName Name of node to print ARP table for.
	 * 
	 */

	public void printARP(String inNodeName) {
		try {
			String s = "";
			s += "ARP entries for :" + inNodeName + "\n";
			String ArpTable[] = ((core.NetworkLayerDevice)Sim.getNode(inNodeName)).getFormattedARPTable();
			for (int i = 0; i < ArpTable.length; i++) {
				s += ArpTable[i] + "\n";
			}
			InfoBox ib = new InfoBox(this, "ARP table", s);
		} catch (Exception e) {
			// Should never get here.
		}
	}

	/**
	 * 
	 * Adds static ARP entry to the ARP cache on the target node.
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inNodeName Name of node to add static ARP Entry
	 * 
	 */

	public void addStaticARP(String inNodeName) {

		String MAC = JOptionPane.showInputDialog(this, "MAC address:",
				"Add static ARP entry", JOptionPane.QUESTION_MESSAGE);

		if (MAC != null) {

			String IP = JOptionPane.showInputDialog(this, "IP address:",
					"Add static ARP entry", JOptionPane.QUESTION_MESSAGE);

			if (IP != null) {

				((core.NetworkLayerDevice)Sim.getNode(inNodeName)).addStaticARP(IP, MAC);

				this.addToConsole("Created new static ARP entry: " + inNodeName
						+ " " + IP + " is " + MAC + "\n");

			}

		}

	}

	/**
	 * 
	 * Removes static(or dynamic) ARP entry from the ARP cache on the target
	 * node.
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inNodeName Name of node to add static ARP Entry
	 * 
	 */

	public void removeARP(String inNodeName) {

		String IP = JOptionPane.showInputDialog(this, "IP address:",
				"Remove ARP entry", JOptionPane.QUESTION_MESSAGE);

		if (IP != null) {

			((core.NetworkLayerDevice)Sim.getNode(inNodeName)).deleteARPRecord(IP);

			this.addToConsole("Removed ARP entry: on " + inNodeName
					+ " for ip " + IP + "\n");

		}

	}

	/**
	 * 
	 * Set Echo Server Status on node to listening
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inNodeName Name of node to server listen
	 * 
	 */

	public void EchoServerListen(String inNodeName) {

		String port = JOptionPane.showInputDialog(this, "Port:",
				"Set Echo Server listening on port.",
				JOptionPane.QUESTION_MESSAGE);

		if (port != null) {

			try {

				Echo echo1 = ((Echo) ((core.ApplicationLayerDevice) Sim
						.getNode(inNodeName)).getApp(7));

				echo1.setPort(Integer.valueOf(port).intValue());

				echo1.Listen();

				printLayerInfo(true);

			} catch (Exception e) {

				addToConsole(e.toString());

			}

		}

	}

	public void EchotcpServerListen(String inNodeName) {

		String port = JOptionPane.showInputDialog(this, "Port:",
				"Set Echo Server listening on port.",
				JOptionPane.QUESTION_MESSAGE);

		if (port != null) {

			try {

				Echo_tcp echo1 = ((Echo_tcp) ((core.ApplicationLayerDevice) Sim
						.getNode(inNodeName)).getApp(17));

				echo1.setPort(Integer.valueOf(port).intValue());

				echo1.Listen();

				printLayerInfo(true);

			} catch (Exception e) {

				addToConsole(e.toString());

			}

		}

	}

	public void TelnetListen(String inNodeName) {
		String port = JOptionPane.showInputDialog(this, "Port:",
				"Set Telnet Server listening on port...",
				JOptionPane.QUESTION_MESSAGE);
		if (port != null) {
			String pass = JOptionPane
					.showInputDialog(this, "Password:",
							"Set Telnet Server listening on port " + port
									+ " with password...",
							JOptionPane.QUESTION_MESSAGE);
			if (pass != null) {
				try {
					Telnet_server telnet1 = ((Telnet_server) ((core.ApplicationLayerDevice) Sim
							.getNode(inNodeName)).getApp(23));
					telnet1.setPort(Integer.valueOf(port).intValue());
					telnet1.setPassword(pass);
					telnet1.Listen();
					printLayerInfo(true);
				} catch (Exception e) {
					addToConsole(e.toString());
				}
			}
		}
	}

	public void TelnetNoListen(String inNodeName) {
		try {
			Telnet_server telnet1 = ((Telnet_server) ((core.ApplicationLayerDevice) Sim
					.getNode(inNodeName)).getApp(23));
			telnet1.Close();
		} catch (Exception e) {
			addToConsole(e.toString());
		}
		printLayerInfo(true);
	}

	public boolean isOn(String inNodeName) {
		try {
			return Sim.getNode(inNodeName).On;
		} catch (Exception e) {
			return false;
		}
	}

	public void TurnDevice(GuiNode node, String inNodeName) {
		try {
			if (Sim.getNode(inNodeName).On == true) {
				Sim.getNode(inNodeName).turnOff();
				node.setEnabled(false);
			} else {
				Sim.getNode(inNodeName).turnOn();
				node.setEnabled(true);
			}
		} catch (Exception e) {
		}
	}

	public String DeviceState(String inNodeName) {
		try {
			if (Sim.getNode(inNodeName).On == true) {
				return "Turn Off";
			} else {
				return "Turn On";
			}
		} catch (Exception e) {
			return "??????";
		}
	}

	public void DHCPD(String inNodeName) {
		try {
			DHCPD dhcpd = ((DHCPD) ((core.ApplicationLayerDevice) Sim
					.getNode(inNodeName)).getApp(67));

			DHCPD.pool p = dhcpd.new_pool();
			p.Gateway = "172.168.0.1";
			p.IP = "172.168.0.10";
			p.Genmask = "255.255.255.0";
			p.MAC = "";

			dhcpd.pools.put(p.IP, p);

			dhcpd.Listen();
		} catch (Exception e) {
		}
	}

	public void DHCPC(String inNodeName) {
		try {
			String choice = (String) JOptionPane.showInputDialog(this, "Select DHCP interface to run on:", "DHCP interface",
					JOptionPane.PLAIN_MESSAGE, null, Sim.getNode(inNodeName).getActiveInterfaces() , (Sim.getNode(inNodeName).getActiveInterfaces())[0]); 
			
			if(choice != null){
				/*((DHCPC) ((core.ApplicationLayerDevice) Sim.getNode(inNodeName))
					.getApp(68)).StartDHCPC(choice, "");*/
				
				((core.NetworkLayerDevice)Sim.getNode(inNodeName)).getConfig().executeCommand("interface " + choice + " ip dhcp client");
				((core.NetworkLayerDevice)Sim.getNode(inNodeName)).getConfig().executeCommand("write mem");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Send echo message to server
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inNodeName Name of node to server listen
	 * 
	 */

	public void EchoSend(String inNodeName) {

		String ip = IPV4Address.sanitizeDecIPorName(JOptionPane.showInputDialog(this, "Server IP:",
				"Send msg over Echo.", JOptionPane.QUESTION_MESSAGE));

		if (ip == null)
			return;

		String port = JOptionPane.showInputDialog(this, "Server Port:",
				"Send msg over Echo.", JOptionPane.QUESTION_MESSAGE);

		if (port == null)
			return;

		String msg = JOptionPane.showInputDialog(this, "Message:", "Message.",
				JOptionPane.QUESTION_MESSAGE);

		if (msg == null)
			return;

		String cnt = JOptionPane.showInputDialog(this, "Quantity:",
				"How many times send.", JOptionPane.QUESTION_MESSAGE);

		if (port != null && ip != null && msg != null && cnt != null) {

			try {

				printNetworkStart();
				// =<150
				this.addToConsole("Trying to send echo message '" + msg
						+ "' from " + inNodeName + " to " + ip + ":" + port
						+ "\n");
				((Echo) ((core.ApplicationLayerDevice) Sim.getNode(inNodeName))
						.getApp(30007)).SendEcho(msg, ip, Integer.valueOf(port)
						.intValue(), Integer.valueOf(cnt).intValue());

			} catch (Exception e) {

				addToConsole(e.toString());

			}

			printLayerInfo(true);

		}

	}

	public void SNMPStartAgent(String inNodeName) {

		String port = JOptionPane.showInputDialog(this, "Port:",
				"Set SNMP agent listening on port.",
				JOptionPane.QUESTION_MESSAGE);
		if (port != null) {
			String password = JOptionPane.showInputDialog(this,
					"Community name:", "Set SNMP community name for agent",
					JOptionPane.QUESTION_MESSAGE);
			if (password != null) {
				try {
					SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice) Sim
							.getNode(inNodeName)).getApp(161);
					snmpa.setPassword(password);
					snmpa.setPort(Integer.valueOf(port).intValue());
					snmpa.Listen();
					printLayerInfo(true);
				} catch (Exception e) {
					addToConsole(e.toString());
				}
				printLayerInfo(true);
			}
		}
	}

	public void SNMPStopAgent(String inNodeName) {
		try {
			SNMP snmpa = (SNMP) ((core.ApplicationLayerDevice) Sim
					.getNode(inNodeName)).getApp(161);
			snmpa.Close();
		} catch (Exception e) {
			addToConsole(e.toString());
		}
		printLayerInfo(true);
	}

	public void SNMPSendMessage(String inNodeName) {
		// printNetworkStart();
		new SNMPSendDataDialog(this, Sim, inNodeName);
		printLayerInfo(true);
	}

	/*
	 * public class TTask extends TimerTask { private MainScreen ms; private int
	 * counts;
	 * 
	 * public TTask (MainScreen ms, int counts) { this.counts=counts;
	 * this.ms=ms;
	 *  }
	 * 
	 * 
	 * public void run() { ms.printLayerInfo(false); if(counts--<=0){
	 * ms.addFooter(); this.cancel(); } } }
	 */

	/**
	 * 
	 * Send echo message to server
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inNodeName Name of node to server listen
	 * 
	 */

	public void EchotcpSend(String inNodeName) {

		String ip = IPV4Address.sanitizeDecIPorName(JOptionPane.showInputDialog(this, "Server IP:",
				"Send message over Echo TCP. Host.", JOptionPane.QUESTION_MESSAGE));

		if (ip == null)
			return;

		String port = JOptionPane.showInputDialog(this, "Server Port:",
				"Send message over Echo TCP. Port.", JOptionPane.QUESTION_MESSAGE);

		if (port == null)
			return;

		String msg = JOptionPane.showInputDialog(this, "Message:", "Send message over Echo TCP. Message.",
				JOptionPane.QUESTION_MESSAGE);

		if (msg == null)
			return;

		String cnt = JOptionPane.showInputDialog(this, "Quantity:",
				"Send message over Echo TCP. How many times send?", JOptionPane.QUESTION_MESSAGE);

		if (port != null && ip != null && msg != null && cnt != null) {

			try {

				printNetworkStart();

				/*
				 * Timer timer = null; timer=new Timer(); timer.schedule(new
				 * TTask(this, 1),4000,4000); //=<150
				 */
				this.addToConsole("Trying to send echo message '" + msg
						+ "' from " + inNodeName + " to " + ip + ":" + port
						+ "\n");
				((Echo_tcp) ((core.ApplicationLayerDevice) Sim
						.getNode(inNodeName)).getApp(30017)).SendEcho(msg, ip,
						Integer.valueOf(port).intValue(), Integer.valueOf(cnt)
								.intValue());

			} catch (Exception e) {

				addToConsole(e.toString());

			}
			addHeader();
			printLayerInfo(false);

		}

	}

	public void TelnetConnect(String inNodeName) {
		String ip = IPV4Address.sanitizeDecIPorName(JOptionPane.showInputDialog(this, "Server IP:",
				"Telnet connect to...", JOptionPane.QUESTION_MESSAGE));
		if (ip != null) {
			String port = JOptionPane.showInputDialog(this, "Server Port:",
					"Telnet connect to...", JOptionPane.QUESTION_MESSAGE);
			if (port != null) {
				try {
					printNetworkStart();
					TelnetEmulator t = new TelnetEmulator(this,
							((Telnet_client) ((core.ApplicationLayerDevice) Sim
									.getNode(inNodeName)).getApp(30023)), ip,
							Integer.valueOf(port).intValue());
					t.pack();
					t.setVisible(true);
					t.start();
				} catch (Exception e) {
					addToConsole(e.toString());
				}
				addHeader();
				printLayerInfo(false);
			}
		}
	}

	public void PosixTelnet(String inNodeName) {
		String ip = IPV4Address.sanitizeDecIPorName(JOptionPane.showInputDialog(this, "Server IP:",
				"Telnet connect to...", JOptionPane.QUESTION_MESSAGE));
		if (ip != null) {
			String port = JOptionPane.showInputDialog(this, "Server Port:",
					"Telnet connect to...", JOptionPane.QUESTION_MESSAGE);
			if (port != null) {
				try {
					printNetworkStart();
					PosixTelnetClientGUI t = new PosixTelnetClientGUI(this,
							((core.ApplicationLayerDevice) Sim
									.getNode(inNodeName)), ip, Integer.valueOf(
									port).intValue());
					t.pack();
					t.setVisible(true);
					t.start();
				} catch (Exception e) {
					addToConsole(e.toString());
				}
				addHeader();
				printLayerInfo(false);
			}
		}
	}

	/**
	 * Print routes table on throws node
	 * 
	 * @author Key
	 * @param String
	 *            inNodeName Name of node to run command on
	 */

	public void PrintRouteTable(String inNodeName) {
		new InfoBox(this, "Route table", ((core.NetworkLayerDevice) Sim
			.getNode(inNodeName)).getFormattedRouteTable());
	}

	/**
	 * 
	 * @author Key
	 * @param String
	 *            inNodeName Name of node
	 */

	public void showCounters(String inNodeName) {
		core.protocolsuite.tcp_ip.ProtocolStack PS = Sim.getNode(inNodeName).getProtocolStack();
		int ARPCount = PS.getARPCount();
		int inIPCount = PS.getinputIPCount();
		int outIPCount = PS.getoutputIPCount();
		String msg = "";

		try {
			if (Sim.getNode(inNodeName) instanceof core.NetworkLayerDevice
					&& !(Sim.getNode(inNodeName) instanceof core.ApplicationLayerDevice)) {
				msg = "Counters: \n\n Recieved IP Packets: "
						+ Integer.valueOf(inIPCount).toString()
						+ "\n Sent IP Packets: "
						+ Integer.valueOf(outIPCount).toString()
						+ "\n ARP Packets: "
						+ Integer.valueOf(ARPCount).toString();
			} else {
				msg = "Counters: \n\n Recieved IP Packets: "
						+ Integer.valueOf(inIPCount).toString()
						+ "\n Sent IP Packets: "
						+ Integer.valueOf(outIPCount).toString()
						+ "\n ARP Packets: "
						+ Integer.valueOf(ARPCount).toString()
						+ "\n Recieved TCP segments: "
						+ Integer.valueOf(PS.getTCPinputCount()).toString()
						+ "\n Sent TCP segments: "
						+ Integer.valueOf(PS.getTCPoutputCount()).toString()
						+ "\n Sent TCP ACK's: "
						+ Integer.valueOf(PS.getTCPACKCount()).toString()
						+ "\n Sent TCP Dublicates: "
						+ Integer.valueOf(PS.getTCPSDCount()).toString()
						+ "\n Recieved TCP Dublicates: "
						+ Integer.valueOf(PS.getTCPRDCount()).toString()
						+ "\n Recieved UDP segments: "
						+ Integer.valueOf(PS.getUDPinputCount()).toString()
						+ "\n Sent UDP segments: "
						+ Integer.valueOf(PS.getUDPoutputCount()).toString();
			}
			JOptionPane.showMessageDialog(this, msg, inNodeName
					+ ": packet counters.", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
		}
	}

	public void resetCounters(String inNodeName) {
		core.protocolsuite.tcp_ip.ProtocolStack PS = Sim.getNode(inNodeName)
				.getProtocolStack();
		PS.resetCounters();
		showCounters(inNodeName);
	}

	public String getNodeTooltipText(String inNodeName) {
		// core.protocolsuite.tcp_ip.ProtocolStack PS =
		// Sim.getTCPProtocolStack(inNodeName);

		String htmlText = "<html>";

		try {
			// DataLinkLayerDevice dev = Sim.getNode(inNodeName);
			Vector VectorMasterList = Sim.getAllNodeInformation(inNodeName);
			/*
			 * ArrayList aryColumnNames = new ArrayList();
			 * aryColumnNames.add("Name"); aryColumnNames.add("Gateway");
			 * aryColumnNames.add("Interface Name"); aryColumnNames.add("MAC
			 * Address"); aryColumnNames.add("IP Address");
			 * aryColumnNames.add("Subnet Mask"); aryColumnNames.add("Link
			 * Name");
			 */

			Vector vec1 = (Vector) VectorMasterList.get(0);

			htmlText = htmlText + "Name: " + vec1.get(0) + "<br>";
			htmlText = htmlText + "Default Gateway: " + vec1.get(1) + "<br>";

			htmlText = htmlText
					+ "<table border='0' cellpadding='1' cellspacing='5'>";

			htmlText = htmlText
					+ "<tr><td>Interface</td><td>Type</td><td>MAC</td><td>IP</td><td>Netmask</td><td>Link To</td></tr>";

			for (int i = 0; i < VectorMasterList.size(); i++) {
				htmlText = htmlText + "<tr>";
				Vector vecInterfaceInfo = (Vector) VectorMasterList.get(i);
				htmlText = htmlText + "<td>" + vecInterfaceInfo.get(2)
						+ "</td>";
				htmlText = htmlText + "<td>" + vecInterfaceInfo.get(3)
						+ "</td>";
				htmlText = htmlText + "<td>" + vecInterfaceInfo.get(4)
						+ "</td>";
				htmlText = htmlText + "<td>" + vecInterfaceInfo.get(5)
						+ "</td>";
				htmlText = htmlText + "<td>" + vecInterfaceInfo.get(6)
						+ "</td>";
				String lnk = (String) vecInterfaceInfo.get(7);
				lnk = lnk.replaceAll("-TO-" + inNodeName, "");
				lnk = lnk.replaceAll(inNodeName + "-TO-", "");
				htmlText = htmlText + "<td>" + lnk + "</td>";
				htmlText = htmlText + "</tr>";
			}

			htmlText = htmlText + "</table>";

		} catch (Exception e) {
		}

		// System.out.println(htmlText);

		return htmlText + "</html>";
	}

	/**
	 * 
	 * Runs network configure command on the target host. (route, et cetera)
	 * 
	 * Need for:
	 * 
	 * 1. Testing purposes.
	 * 
	 * 2. Future realisation of Telnet protocol (QweR?)
	 * 
	 * @author Key
	 * 
	 * @param String
	 *            inNodeName Name of node to run command on
	 * 
	 */

	public void RunCmd(String inNodeName) {
		if (!(Sim.getNode(inNodeName) instanceof core.Printer)) {
			Terminal r = new Terminal(this, (core.NetworkLayerDevice) Sim
					.getNode(inNodeName));
			r.pack();
			r.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this,
					"Feature isn't supported by this device type!",
					"Error", JOptionPane.ERROR_MESSAGE);
		} 
	}

	/**
	 * 
	 * This method will return the current size of the Sandbox
	 * 
	 * @return Dimension SandboxSize Size of the Sandbox
	 * 
	 */
	
	public Dimension getSandboxSize()

	{

		return Sandbox.getSize();

	}

	/**
	 * 
	 * This method calls each of the nodes in the Node table and sets thier
	 * Sandbox size
	 * 
	 * varibles to the current size of the sandbox.
	 * 
	 * @param Dimension
	 *            inDimension the current size of the sandbox
	 * 
	 */

	public void updateNodesKnowledge(Dimension inDimension)

	{

		Enumeration enum1 = GUInodeTable.keys();

		while (enum1.hasMoreElements())

		{

			String key = (String) enum1.nextElement();

			GuiNode tempNode = (GuiNode) GUInodeTable.get(key);

			tempNode.setCurrentSandboxDim(inDimension);

		}

	}

	/**
	 * 
	 * Returns the current cursor on the Sandbox
	 * 
	 * @return Cursor SandboxCursor The current cursor on the Sandbox
	 * 
	 */

	public Object[] getInterfaces(String NodeName)
			throws InvalidNodeNameException {
		Object nics[] = Sim.getNode(NodeName).getAllInterfaces(); // Get object array of
														// interface names
		Arrays.sort(nics);
		return nics;
	}
	
	public void showIntProps(String NodeName, String IntName) {
		try {
			Node temp = Sim.getNode(NodeName);
			int itype = temp.getIntType(IntName);

			switch (itype) {
			case core.NetworkInterface.Ethernet10T:
			case core.NetworkInterface.Ethernet100FX:
				if (temp.getNIC(IntName).isActive()) {
					new EthPortProperties(this, NodeName, IntName, Simulation.Sim);
				}
				break;
			case core.NetworkInterface.Wireless:
				new WiFiPortProperties(this, NodeName, IntName, Sim);
				break;				
			case core.NetworkInterface.Serial:
				new SerPortProperties(this, NodeName, IntName, Sim, Sandbox);
				break;
			case core.NetworkInterface.Console:
				new ConsolePortProperties(this, NodeName, IntName, Sim);
				break;				
			case core.NetworkInterface.WAN:
				/*
				 * String service = JOptionPane.showInputDialog(this, "Sevice
				 * name", "Service name.", JOptionPane.QUESTION_MESSAGE);
				 * if(service == null || service == "" || service.length() < 3){
				 * ((core.WANNetworkInterface)(temp.getNIC(IntName))).setServer(true);
				 * }else{
				 * ((core.WANNetworkInterface)(temp.getNIC(IntName))).setServer(false); }
				 * 
				 * ((core.WANNetworkInterface)(temp.getNIC(IntName))).UP();
				 */
				new WANPortProperties(this, NodeName, IntName, Sim, Sandbox);
				break;
			default:
				break;
			}

			this.refreshNodeInformationTab();
		} catch (Exception e) {
		}
	}

	public void breakLink(String NodeName, String IntName) {
		if (JOptionPane.showConfirmDialog(this, "Disconnect link from port "
				+ IntName + " on " + NodeName + "?", "Confirm disconnect!",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			try {
				String str = Sim.disconnectLink(NodeName, IntName);
				Sandbox.removeLine(str);
				this.refreshNodeInformationTab();
				// controller.addToConsole(NodeName +"'s link on interface
				// "+Interface+" has been disconnected!\n");
				// ^^^FIXME!
			} catch (Exception e) {
			}
		}
	}

	public Cursor getCurrentSandboxCursor()

	{

		return Sandbox.getCursor();

	}

}

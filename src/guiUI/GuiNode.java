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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import core.NetworkInterface;
import core.Node;
import core.Simulation;

/**
 * 
 * @author luke_hamilton
 * 
 * @author michael_reith
 * 
 * @author bevan_calliess
 * 
 * @author VC2 Team.
 * 
 */

public abstract class GuiNode extends JPanel implements MouseListener,
MouseMotionListener, Serializable, DragGestureListener,
DragSourceListener, DropTargetListener {

	// The node's name within a label

	/**
	 * 
	 */
	private static final long serialVersionUID = 3501858129186840165L;

	protected JLabel lblNodeName = new JLabel();

	protected GuiNode selfLink = this;

	// The node's icon

	protected ImageIcon nodeIcon;

	// The label for displaying the Icon

	protected JLabel lblIcon = new JLabel();

	protected String tmpName = "";

	// A Reference to the MainScreen

	protected MainScreen controller;

	Color validDropLocation = Color.GREEN;

	Color invalidDropLocation = Color.RED;

	// Right click menuitems //TODO this needs to be reviewed

	protected JPopupMenu GuiNodePopMenu = new JPopupMenu("Node Popup Menu");

	private JMenuItem mnuDelete = new JMenuItem("Delete");

	private JMenuItem mnuTurn = new JMenuItem("Turn Off");

	private JMenu mnuBreakLink = new JMenu("Disconnect link");

	private JMenuItem mnuLink = new JMenuItem("Links properties");

	private JMenu mnuInt = new JMenu("Interface properties:");

	private String strNodeName;

	private Vector<String> vecConnectedLinks = new Vector<String>();

	boolean acceptDrop = true;

	boolean selfDrop = true;

	Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	private int SandBoxWidth;

	private int SandBoxHeight;

	private int pressedX = 0;
	private int pressedY = 0;

	// These statics are only for sandbox class. These shouldn't be used
	// anywhere else.

	public final static int NodeHeight = 64;

	public final static int NodeWidth = 64;

	/**
	 * 
	 * This is the constructor for a GUI Node it takes the name of the node
	 * 
	 * that will be displayed on the screen. This name must be unique as it is
	 * used
	 * 
	 * as a key in many areas of the sim. It also takes a reference to the
	 * MainScreen
	 * 
	 * as it needs to call various methods within the Mainscreen. The
	 * constructor also
	 * 
	 * provides a reference to the Image location so that the correct image is
	 * displayed
	 * 
	 * for each node eg. routers display the router image.
	 * 
	 * @param inName
	 *            Name of this node
	 * 
	 * @param inMainScreen
	 *            The controller of this object
	 * 
	 * @param imageLocation
	 *            The location of the file to be displayed.
	 * 
	 */

	public GuiNode(String inName, MainScreen inMainScreen, String imageLocation) {

		// Set the refence to the mainscreen

		controller = inMainScreen;

		strNodeName = inName;

		// Set the name within the label

		lblNodeName.setText(inName);

		lblNodeName.setHorizontalAlignment(SwingConstants.CENTER);

		SandBoxWidth = (int) controller.getSandboxSize().getWidth();

		SandBoxHeight = (int) controller.getSandboxSize().getHeight();

		// Set the icon

		ClassLoader cl = this.getClass().getClassLoader();

		nodeIcon = new ImageIcon(cl.getResource(imageLocation));

		// nodeIcon = new ImageIcon(imageLocation);

		lblIcon.setIcon(nodeIcon);

		lblIcon.setHorizontalAlignment((SwingConstants.CENTER));

		// Set the layout of this jPanel

		this.setLayout(new BorderLayout());

		// Add label to panel

		this.add(lblNodeName, BorderLayout.NORTH);

		// Add Icon label to panel

		this.add(lblIcon, BorderLayout.CENTER);

		// Dimension dim = this.getPreferredSize();

		// this.setBounds(0,0,75,55);
		this.setBounds(0, 0, GuiNode.NodeWidth + 10, GuiNode.NodeHeight + 10);

		this.addMouseListener(this);

		this.addMouseMotionListener(this);

		// Set this to be translucent so that lines are drawn through the text

		// Looks alot better this way

		this.setOpaque(false);

		this.setBackground(validDropLocation);

		DragSource dragSource = DragSource.getDefaultDragSource();

		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_REFERENCE, this);

		new DropTarget(this, DnDConstants.ACTION_REFERENCE, this);

		// Setup right click menu for node

		mnuDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				delete();

			}

		});



		mnuLink.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				controller.showLinkDialog(lblNodeName.getText());

			}

		});

		GuiNodePopMenu.add(mnuDelete);
		GuiNodePopMenu.add(mnuTurn);
		GuiNodePopMenu.add(mnuBreakLink);
		GuiNodePopMenu.add(mnuLink);
		GuiNodePopMenu.add(mnuInt);

		mnuTurn.setText(controller.DeviceState(lblNodeName.getText()));
		mnuTurn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// controller.showLinkDialog(lblNodeName.getText());
				controller.TurnDevice(selfLink, lblNodeName.getText());
				mnuTurn.setText(controller.DeviceState(lblNodeName.getText()));
			}
		});
		mnuBreakLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateConnectedLinkMenu();
			}
		});

	}

	@Override
	public void setEnabled(boolean e) {
		lblIcon.setEnabled(e);
		mnuTurn.setText(controller.DeviceState(lblNodeName.getText()));
	}

	public void updateInterfacesMenu(){
		try {
			Object ints[] = controller.getInterfaces(lblNodeName.getText());

			for (int i = 0; i < ints.length; i++) {		
				int int_type = Simulation.Sim.getNode(this.strNodeName).getIntType((String)ints[i]); 

				if(int_type != core.NetworkInterface.Unknown){
					JMenuItem mnuI = new JMenuItem((String) ints[i]);
					mnuI.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {

							controller.showIntProps(lblNodeName.getText(),
									((JMenuItem) e.getSource()).getText());

						}

					});
					mnuInt.add(mnuI);
				}

				if(int_type != core.NetworkInterface.Wireless){

					JMenuItem mnuDI = new JMenuItem((String) ints[i]);
					mnuDI.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {

							controller.breakLink(lblNodeName.getText(),
									((JMenuItem) e.getSource()).getText());

						}

					});
					mnuBreakLink.add(mnuDI);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateConnectedLinkMenu(){
		try {
			mnuBreakLink.removeAll();
			Object ints[] = controller.getInterfaces(lblNodeName.getText());
			for (int i = 0; i < ints.length; i++) {		
				int int_type = Simulation.Sim.getNode(this.strNodeName).getIntType((String)ints[i]); 
				NetworkInterface ni = Simulation.Sim.getNode(this.strNodeName).getNetworkInterface((String)ints[i]);
				boolean lineIsConnected = ni.getConnectedLink()!=null;

				if(int_type != core.NetworkInterface.Wireless && lineIsConnected){
					
					JMenuItem mnuDI = new JMenuItem((String)ints[i]+" ("+ni.getConnectedLink().getLinkedNodeName(getName(), (String)ints[i])+")");
					mnuDI.addActionListener(new LinkMenuActionListener((String)ints[i]));
					mnuBreakLink.add(mnuDI);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void addInterfaces(MainScreen parent, Node node);


	/**
	 * 
	 * This method controls the dropping of the drag and drop methods
	 * 
	 * used to transfer the name of the source PC for links to the
	 * 
	 * destination PC, This information is vital when the Link is created
	 * 
	 * in the sim and also for the drawing of the Link line on the GUI.
	 * 
	 * The Source PC name is stored in a Transferable object and extracted here
	 * 
	 * at the destination.
	 * 
	 */

	public void drop(DropTargetDropEvent e) {

		String temp;

		DataFlavor stringFlavor = DataFlavor.stringFlavor;

		try {

			Transferable tr = e.getTransferable();

			temp = (String) tr.getTransferData(stringFlavor);

			if (!temp.equals(this.getName())) {

				controller.addLink(temp, this.getName());

			}

		} catch (Exception ufe) {

			System.out.println(ufe);

		}

		e.acceptDrop(DnDConstants.ACTION_REFERENCE);

		e.dropComplete(true);

		controller.setIsLink(false);

		this.setBorder(null);

	}

	public void dragEnter(DropTargetDragEvent e) {
	}

	/**
	 * 
	 * This method is related to the drag and drop methods used to
	 * 
	 * transfer the name of the source PC to the destination.
	 * 
	 * It sets the highlighted border around the node to null when
	 * 
	 * a Drag and drop process exits the node
	 * 
	 */

	public void dragExit(DropTargetEvent e) {

		this.setBorder(null);

	}

	/**
	 * 
	 * This method relates to the Drag and Drop process used to transfer the
	 * 
	 * name of the source PC to the Destination. It checks if this node has any
	 * 
	 * available interfaces and If it is also the source. If there are no
	 * interfaces
	 * 
	 * available it will not accept the drop. and the highlight around the node
	 * 
	 * will be set to red.
	 * 
	 */

	public void dragOver(DropTargetDragEvent e) {

		// boolean testInterfaces = false;

		acceptDrop = controller.getAvailableInterfaces(this.getName());

		if (acceptDrop && selfDrop) {

			e.acceptDrag(e.getDropAction());

			this.setBackground(validDropLocation);

			this.setBorder(BorderFactory.createEtchedBorder());

		}

		else if (selfDrop)

		{

			this.setBackground(invalidDropLocation);

			this.setBorder(BorderFactory.createEtchedBorder());

		}

	}

	public void dropActionChanged(DropTargetDragEvent e) {
	}

	/**
	 * 
	 * This method starts the Drag and Drop process it creates
	 * 
	 * the Transferable object that contains the name of the source
	 * 
	 * PC and sets the selfDrop flag so that it will not allow you to
	 * 
	 * drop onto this node.
	 * 
	 */

	public void dragGestureRecognized(DragGestureEvent e)

	{

		if (controller.getIsLink())

		{

			Cursor csrTarget = new Cursor(Cursor.CROSSHAIR_CURSOR);

			if (controller.getAvailableInterfaces(this.getName()))

			{

				e.startDrag(csrTarget, new StringSelection(this.getName()),
						this);

				Point tempPoint = new Point(
						(this.getX() + this.getWidth() / 2), (this.getY() +

								this.getHeight() / 2));

				controller.addFakeLine(tempPoint);

				selfDrop = false;

			}

		}

	}

	/**
	 * 
	 * This method ends the Drag and Drop process. It will call the
	 * 
	 * remove fake line method on the controller. This removes the line that
	 * 
	 * follows the mouse during the drag and drop process.
	 * 
	 */

	public void dragDropEnd(DragSourceDropEvent e) {

		controller.removeFakeLine();

		controller.setIsLink(false);

		selfDrop = true;

	}

	public void dragEnter(DragSourceDragEvent e) {
	}

	/**
	 * 
	 * This method is part of the Drag and Drop process. It is used to move the
	 * 
	 * line that follows the cursor during the drag and drop.
	 * 
	 * Since the drag and drop event only provides the location of the mouse
	 * 
	 * relative to the Whole window and not the current application we need
	 * 
	 * to calculate the current location of the Jfirewall sim JFrame on the
	 * 
	 * screen and then use this as an offset for the Lines end location.
	 * 
	 */

	public void dragExit(DragSourceEvent e) {

		// Get the current location of the TopLeft corner of the mainScreen

		Point conLoc = controller.getLocation();

		// Get the combined Height of the Toolbar and menuBar

		int tmHeight = controller.getMenuToolbarHeight();

		// now Subtract its location from the current Mouse Location

		// This is close but we still need to allow for the hight of the menu

		// and title bars at the top of the frame.

		Point tempPoint = new Point((e.getX() - conLoc.x) - 5,
				(e.getY() - conLoc.y) - tmHeight);

		controller.moveLink(LinkLayerPanel.FAKELINE, 2, tempPoint);

	}

	/**
	 * 
	 * This method is part of the Drag and Drop process and is used
	 * 
	 * to move the end of the line while it is over a GUI node.
	 * 
	 * This is so the line appears to move seamlessly behind a node.
	 * 
	 */

	public void dragOver(DragSourceDragEvent e) {

		// Get the current location of the TopLeft courner of the mainScreen

		Point conLoc = controller.getLocation();

		// Get the combined Height of the Toolbar and menuBar

		int tmHeight = controller.getMenuToolbarHeight();

		// now Subtract its location from the current Mouse Location

		// This is close but we still need to allow for the hight of the menu

		// and title bars at the top of the frame.

		Point tempPoint = new Point((e.getX() - conLoc.x) - 5,
				(e.getY() - conLoc.y) - tmHeight);

		controller.moveLink(LinkLayerPanel.FAKELINE, 2, tempPoint);

	}

	// These are currnetly unused, but have to be here

	// because of the abstract class MouseListener and MouseMotionListener needs
	// them

	public void dropActionChanged(DragSourceDragEvent e) {
	}

	public void mouseMoved(MouseEvent e) {

		this.setToolTipText(controller.getNodeTooltipText(strNodeName));

	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * 
	 * This method pops up the popup menu for this node
	 * 
	 * if the mouse is right clicked.
	 * 
	 * 
	 * 
	 */

	public void mousePressed(MouseEvent e) {

		// Set popup menu for right click here.

		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {

			boolean ena = controller.isOn(lblNodeName.getText());
			for (int i = 2; i < GuiNodePopMenu.getSubElements().length; i++) {
				((JMenuItem) GuiNodePopMenu.getSubElements()[i])
				.setEnabled(ena);
			}
			updateConnectedLinkMenu();
			GuiNodePopMenu.show(e.getComponent(), e.getX(), e.getY());
		}
		else if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
			pressedX = e.getX();
			pressedY = e.getY();
		}

	}

	/**
	 * 
	 * This method controls the movment of this GuiNode and any connected links.
	 * 
	 * As the guiNode is dragged across the LayeredPaned it calls the MoveLink
	 * 
	 * method on the MainScreen(Controller) for each of its connected Links.
	 * 
	 * 
	 * 
	 */

	public void mouseDragged(MouseEvent e) {

		Cursor currentCursor = controller.getCurrentSandboxCursor();

		if (currentCursor.getName().equals(defaultCursor.getName()))

		{

			if (e.getModifiers() == InputEvent.BUTTON1_MASK)

			{

				if (controller.getIsLink())

				{

					// Ignore me

					// System.out.println(e.getX() + ", " + e.getY());

				}

				else {

					int controlX = this.getX() + e.getX();// + this.getWidth() / 2;
					int controlY = this.getY() + e.getY();// + this.getHeight() / 2;

					if (controlX > SandBoxWidth) controlX = SandBoxWidth;
					else if (controlX < 0) controlX = 0;
					if (controlY > SandBoxHeight) controlY = SandBoxHeight;
					else if (controlY < 0) controlY = 0;
					this.setLocation(controlX-pressedX, controlY-pressedY);

					Point position = new Point(this.getX()+this.getWidth()/2, this.getY()+this.getHeight()/2);

					Iterator<String> it = vecConnectedLinks.iterator();

					while (it.hasNext()) {

						String linkString = it.next();

						String LinkDetails[] = linkString.split(":");

						controller.moveLink(LinkDetails[0], Integer
								.parseInt(LinkDetails[1]), position);

					}

				}

			}

		}

	}

	/**
	 * 
	 * Removes this gui node from the layeder pane.
	 * 
	 */

	public void delete() {

		controller.deleteNode(lblNodeName.getText(), this);

	}

	/**
	 * 
	 * This method returns the name of this GUINode
	 * 
	 * @return String the name of this node.
	 * 
	 */

	@Override
	public String getName()

	{

		return strNodeName;

	}

	/**
	 * 
	 * This method is used by the AddLink process in the main Screen.
	 * 
	 * It takes two parameters the first is the name of a link that this guinode
	 * is
	 * 
	 * connected to. This can be as many links as there are interfaces so for
	 * routers
	 * 
	 * it would currently be two and PC's one. The second is the end of the link
	 * 
	 * that this guiNode is connected to. This will either be 1 or 2 based on
	 * which way the
	 * 
	 * mouse was dragged. These parameters are used in the mouse dragged
	 * 
	 * event to call the MoveLink method of the main screen so that the
	 * connected links move
	 * 
	 * with the guiNode on teh screen.
	 * 
	 * So that the two items never get out of sync I have converted the integer
	 * into a string
	 * 
	 * and added it onto the end of the string devided by a :
	 * 
	 * During the Mouse dragged event the : is used to split the string back
	 * into the two parts.
	 * 
	 * 
	 * 
	 * @param inName
	 *            Name of the Link this PC is connected to
	 * 
	 * @param inWhichEnd
	 *            Integer representing the end that it is connected to eg. 1 or
	 *            2
	 * 
	 */

	public void setConnectedLinkName(String inName, int inWhichEnd,
			String inOtherNodeName) {

		vecConnectedLinks.addElement(inName + ":"
				+ Integer.toString(inWhichEnd) + ":" + inOtherNodeName);

	}

	/**
	 * 
	 * Returns a vector of all of the connected link names.
	 * 
	 * @return Vector List of Connected Links.
	 * 
	 */

	public Vector<String> getConnectedLinks() {

		Vector<String> linkList = new Vector<String>();

		Iterator<String> it = vecConnectedLinks.iterator();

		while (it.hasNext()) {

			String linkString = it.next();

			String LinkDetails[] = linkString.split(":");

			linkList.add(LinkDetails[0] + ":" + LinkDetails[2]);

		}

		return linkList;

	}

	/**
	 * 
	 * This method will remove any connected links from the
	 * 
	 * vector of links.
	 * 
	 * @param inLink
	 *            The name of the link to be removed.
	 * 
	 */

	public void deleteConnectedLink(String inLink)

	{

		Iterator<String> it = vecConnectedLinks.iterator();

		while (it.hasNext()) {

			String linkString = it.next();

			String LinkDetails[] = linkString.split(":");

			if (inLink.equals(LinkDetails[0]))

			{

				it.remove();

			}

		}

	}

	/**
	 * 
	 * This method sets the location of this GUINode
	 * 
	 * @param inPoint
	 *            a Point object with the new location for this node.
	 * 
	 */

	public void setNodeLocation(Point inPoint)

	{

		this.setBounds((int) inPoint.getX(), (int) inPoint.getY(), this
				.getWidth(), this.getHeight());

	}

	/**
	 * 
	 * This method is part of the Drag and Drop process. It tests if the
	 * 
	 * dataflavour of the Drop process is suppoted by this NOde.
	 * 
	 * Currently the only flavour is the string flavour.
	 * 
	 * @param e
	 *            The dataflavour to test.
	 * 
	 * @return Boolean Tru is the flavour passed in is supported
	 * 
	 */

	public boolean isDragOK(DropTargetDragEvent e)

	{

		if (e.isDataFlavorSupported(DataFlavor.stringFlavor))

		{

			return true;

		}

		return false;

	}

	/**
	 * 
	 * This method creates a local variable within the node so that it knows the
	 * 
	 * current dimensions of the sandbox. This is used during the moving of a
	 * 
	 * node to contain them within the sandbox.
	 * 
	 * @param inDimension
	 *            The current dimension of the sandBox.
	 * 
	 */

	public void setCurrentSandboxDim(Dimension inDimension)

	{

		SandBoxHeight = (int) inDimension.getHeight();

		SandBoxWidth = (int) inDimension.getWidth();

	}
	
	class LinkMenuActionListener implements ActionListener {
		String ifName;
		public LinkMenuActionListener(String in){
			super();
			ifName=in;
		}
		public void actionPerformed(ActionEvent e) {
			controller.breakLink(lblNodeName.getText(),ifName);
		}
	}

}

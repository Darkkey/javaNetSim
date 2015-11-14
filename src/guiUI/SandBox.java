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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLayeredPane;

/**
 * This Class is the display area for a simulation.  This is were the various
 * GUInodes and links will be drawn.
 * All components are instantiated within the class prior to construction.
 * Extends JLayeredPane and implements MouseListener, MouseMotionListener and ComponenetListener
 * All GUI Nodes are added to the 3rd layer of the Sandbox and a panel called LinkLayerPanel is added to the first
 * layer of the Sandbox which displays all the link and line drawn for this Sim.
 * 
 * @author Team VC2
 */


public class SandBox extends JLayeredPane implements MouseListener, MouseMotionListener, ComponentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7676829472573372306L;
	public static final int PC_CURSOR = 0;
	public static final int ROUTER_CURSOR = 1;
	public static final int SWITCH_CURSOR = 2;
	public static final int HUB_CURSOR = 3;
    public static final int EXTERNALNAT_CURSOR = 4;
    public static final int CSUDSU_CURSOR = 5;
    public static final int PRINTER_CURSOR = 6;
    public static final int AP_CURSOR = 7;
	
	private Cursor csrDefault = new Cursor(Cursor.DEFAULT_CURSOR);
	private MainScreen controller;
	private Point cursorLocation = new Point(0,0);
	private LinkLayerPanel pnlLinkLayer;
	private boolean blnPlaceableLocation = false;
	private int controlX;
    private int controlY;
    private String cursorClass = "";
	
	// Create the various cursors for this panel
    private ClassLoader cl = this.getClass().getClassLoader();
    
	Image pcImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/mymac.png"));
	Cursor customPCCursor = Toolkit.getDefaultToolkit().createCustomCursor(pcImage, cursorLocation, "pcCursor");
	
	Image routerImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/router.png"));
	Cursor customRouterCursor = Toolkit.getDefaultToolkit().createCustomCursor(routerImage, cursorLocation, "routerCursor");
	
	Image switchImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/switch.png"));
	Cursor customSwitchCursor = Toolkit.getDefaultToolkit().createCustomCursor(switchImage, cursorLocation, "switchCursor");
	
	Image hubImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/hub.png"));
	Cursor customHubCursor = Toolkit.getDefaultToolkit().createCustomCursor(hubImage, cursorLocation, "hubCursor");
        
    Image externalnatImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/network_local.png"));
	Cursor customExternalNATCursor = Toolkit.getDefaultToolkit().createCustomCursor(externalnatImage, cursorLocation, "externalnatCursor");
        
    Image csudsuImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/csudsu2.png"));
	Cursor customCSUDSUCursor = Toolkit.getDefaultToolkit().createCustomCursor(csudsuImage, cursorLocation, "csudsuCursor");
        
    Image printerImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/printer.png"));
	Cursor customPrinterCursor = Toolkit.getDefaultToolkit().createCustomCursor(printerImage, cursorLocation, "printerCursor");
	
	Image APImage = Toolkit.getDefaultToolkit().getImage(cl.getResource("images/simulation/ap.png"));
	Cursor customAPCursor = Toolkit.getDefaultToolkit().createCustomCursor(APImage, cursorLocation, "APCursor");
	
	public SandBox(MainScreen inMainScreen){
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setLayout(null);
                //this.
		addComponentListener(this);
		pnlLinkLayer = new LinkLayerPanel();

        this.setLayer(pnlLinkLayer,1,0);
		this.add(pnlLinkLayer);
		controller = inMainScreen;	
                
        this.setPreferredSize(new Dimension(7000,7000));
	}
	
	/**
	 * Adds a new line to the LinkLayerPanel
	 * @param String inName Name to be used as a reference for this line
	 * @param Point inStart Point of where to start line
	 * @param Point inEnd Point of where to finish line
	 */
	
	public void addLine(String inName,Point inStart, Point inEnd, int type){
		pnlLinkLayer.addLine(inName, inStart,inEnd, type);
	}
	
	/**
	 * Removes the specified line from the LinkLayerPanel
	 * @param String inName Name of line to be removed.
	 */
	
	public void removeLine(String inName){
		pnlLinkLayer.removeLine(inName);	
	}
	
	/**
	 * When a node is moved on the Sandbox, this method will move any lines that are connected to this node.
	 * @param String inName Name of the line to be moved
	 * @param int inWhichEnd The end of the line to be moved
	 * @param Point inPosition The new position for the specified end of the line.
	 */
	
	public void moveLine(String inName,int inWhichEnd,Point inPosition){
		pnlLinkLayer.moveLine(inName,inWhichEnd,inPosition);
	}	
	
	
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseDragged(MouseEvent e){}
	
	/**
	 * Standard mouseMoved event however code has been added to ensure that nodes can't be built on the outer
	 * regions of the sandbox.
	 */
	
	public void mouseMoved(MouseEvent e)
	{
	
				
	     controlX = e.getX() + GuiNode.NodeWidth;
	     controlY = e.getY() + GuiNode.NodeHeight;
	     
	       if(controlX < controller.getSandboxSize().getWidth() && controlX > 35)
	       {
	       		if(controlY < controller.getSandboxSize().getHeight() && controlY > 50)
	       		{
	       			blnPlaceableLocation = true;
	       		}
	       		else
	       		{
	       			blnPlaceableLocation = false;	
	       		}
	       }
	 
	       else
	       {
	       	blnPlaceableLocation = false;
	       }
		 
	}
	
	/**
	 * This method will cancel any selection made with the mouse button and reset the cursor
	 * to the default cursor.  Additionally it makes sure that if restricts the user from 
	 * creating nodes outside the Sandbox.
	 */
	
	public void mouseClicked(MouseEvent e)
	{		
		if(e.getModifiers() == InputEvent.BUTTON3_MASK)	
		{
			controller.setIsLink(false);
			controller.setAllHighlightsOff();
		}
		if(e.getModifiers() == InputEvent.BUTTON1_MASK)
		{
		     
			if(blnPlaceableLocation && cursorClass.length()>1)
			{
				
				this.setCursor(csrDefault);
				controller.addNode(cursorClass, new Point(e.getX()-GuiNode.NodeWidth/2, e.getY()-GuiNode.NodeHeight/2));
				cursorClass = "";
			}
			
		}
	}
	
	/**
	 * Sets the cursor to the specified value
	 * @param int cursorType The cursor to be changed to
	 */
	
	public void setCursorType(String nodeType){
		
		cursorClass = nodeType;
		
		this.setCursor(controller.nodeTemplates.get(nodeType).getCursor());
	}
	
	/**
	 * This method ensures that the LinkLayerPanel is always resized to the same size as the
	 * sandbox.
	 */
	
	public void componentResized(ComponentEvent arg0){
               	pnlLinkLayer.setSize(this.getSize());
                controller.updateNodesKnowledge(this.getSize());
	}	
	public void componentHidden(ComponentEvent arg0){}
	public void componentMoved(ComponentEvent arg0){}	
	public void componentShown(ComponentEvent arg0) {
	}              
        
}

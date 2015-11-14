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



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JPanel;



/**

 * This object is an extension of the Java Swing JPanel.

 * It has been extended so that we can overide the paint method 

 * and use it to draw the various links.  It will be inserted into the

 * bottom layer of a layered pane so that it always sits behind the

 * various Nodes being displayed on teh screen. 

 * 

 * @author bevan_calliess

 *  

 */



public class LinkLayerPanel extends JPanel {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6226572644195993822L;

	final static String FAKELINE = new String("FakeLine");

	final static BasicStroke stroke = new BasicStroke(3.0f);

	final static Color linkCol = Color.BLUE;

        class LinkLine{
            public Line2D.Double Line;
            public BasicStroke stroke;
            public Color linkCol;
            
            public LinkLine(Line2D.Double inLine, BasicStroke instroke, Color inlinkCol){
                Line = inLine;
                stroke = instroke;
                linkCol = inlinkCol;
            }
        }
        
	private Hashtable LineTable = new Hashtable();

		

	

	/**

	 * This method will take the name of the Link and the starting

	 * and ending points that it is drawn between. 

	 * A 2DLine object is created using these co-ordinates and then

	 * it is added to a hashTable using the name as a key

	 * so that it can be located and moved and/or removed as required.

	 * @param inName	Used as the key for the hashTable

	 * @param inStart	Used for the first set of co-ordinates p1x and p1y

	 * @param inEnd		Used for the second set of co-ordinates p2x and p2y

	 */

	public void addLine(String inName,Point inStart, Point inEnd, int type){		

                switch(type){
                    case core.NetworkInterface.Ethernet10T:
                	LineTable.put(inName,new LinkLine(new Line2D.Double(inStart.x,inStart.y,inEnd.x,inEnd.y), new BasicStroke(2.0f), Color.BLACK));
                        break;
                    case core.NetworkInterface.Console:                        
                	LineTable.put(inName,new LinkLine(new Line2D.Double(inStart.x,inStart.y,inEnd.x,inEnd.y), new BasicStroke(3.0f), Color.CYAN ));
                        break;                        
                    case core.NetworkInterface.Serial:                        
                	LineTable.put(inName,new LinkLine(new Line2D.Double(inStart.x,inStart.y,inEnd.x,inEnd.y), new BasicStroke(3.0f), Color.RED));
                        break; 
                    case core.NetworkInterface.Ethernet100FX:                        
                	LineTable.put(inName,new LinkLine(new Line2D.Double(inStart.x,inStart.y,inEnd.x,inEnd.y), new BasicStroke(3.0f), Color.ORANGE));
                        break;                         
                }

		repaint();

	}

	/**

	 * This method will remove a line from the hashtable so that

	 * it will no longer be drawn. 

	 * @param inName

	 */

	public void removeLine(String inName){

		LineTable.remove(inName);		

		repaint();

	}

	

	/**

	 * This method is Used to move the co-ordinates of a particular 

	 * line in the hashTable.  

	 * 

	 * @param inName		Key to the HashTable.(linkName)

	 * @param inWhichEnd	Which set of co_ordinates to move p1 or p2

	 * @param inPosition	The position to move those cordinates to.

	 */	

	public void moveLine(String inName,int inWhichEnd,Point inPosition){

		if(LineTable.containsKey(inName))

		{

			LinkLine myLine = (LinkLine)LineTable.get(inName);

			if(inWhichEnd == 1){

				Point2D p1 = inPosition;

				Point2D p2 = myLine.Line.getP2();

				myLine.Line.setLine(p1,p2);

			}else{

				Point2D p1 = myLine.Line.getP1();

				Point2D p2 = inPosition;

				myLine.Line.setLine(p1,p2);				

			}

		repaint();

		}

		

	}

	

	/**

	 * Overides the standard paint method so that it

	 * draws the various lines on the screen.

	 */

	@Override
	public void paint(Graphics g)

	{

		super.paint(g);

		Graphics2D g2D = (Graphics2D)g;

		Enumeration it = LineTable.elements();

                while(it.hasMoreElements()){

			LinkLine myLine = (LinkLine)it.nextElement();
                        
                        g2D.setStroke(myLine.stroke);

                        g2D.setPaint(myLine.linkCol);
                        
			g2D.draw(myLine.Line);

		}		

	}

}


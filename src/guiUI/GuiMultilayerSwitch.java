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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import core.NetworkInterface;
import core.Node;



/**

 * <P>The GuiSwitch class is used to instantiate new Switches on the GUI</P>

 * 

 * @author luke_hamilton

 * @since 15th November 2004

 * @version v0.20

 */



public class GuiMultilayerSwitch extends NetworkLayerDevice {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7713023657795807625L;
	/**

	 * @param inName  The name of the switch

	 * @param inMainscreen	The JFrame that the Switch will be created on

	 */

    private JMenuItem mnuReset = new JMenuItem("Reset");

	public GuiMultilayerSwitch(String inName, MainScreen inMainScreen) {

		super(inName, inMainScreen,"images/simulation/mlswitch.png");	
		mnuReset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				controller.Reset(lblNodeName.getText());
			}
		});
		GuiNodePopMenu.add(mnuReset);   

	}
	
	@Override
	public void addInterfaces(MainScreen parent, Node node){
		
		 String[] choices = {"24 Ethernet-TX ports switch.",
                 "12 Ethernet-TX ports with 2 Ethernet-FX switch.",
                 "24 Ethernet-TX ports with 2 Ethernet-FX switch.",
                 "48 Ethernet-TX ports with 2 Ethernet-FX switch.",
                 "12 Ethernet-FX ports with 2 Ethernet-TX switch."
                };
		 
		 String choice = parent.getDeviceTypeDialog("Create new switch", "Please choose switch type", choices);
		 
		 int tx=0, fx=0;
         
         if(choice.contains(choices[0])){
             tx = 24; fx = 0;
         }else if(choice.contains(choices[1])){
             tx = 12; fx = 2;
         }else if(choice.contains(choices[2])){
             tx = 24; fx = 2;
         }else if(choice.contains(choices[3])){
             tx = 48; fx = 2;
         }else if(choice.contains(choices[4])){
             tx = 2; fx = 12;
         }
         
         for(int i = 0; i < tx; i++){
             node.addNetworkInterface(core.NetworkInterface.getIntName(core.NetworkInterface.Ethernet10T) + String.valueOf(i), core.NetworkInterface.Ethernet10T, false);       
         }
         
         for(int i = 0; i < fx; i++){
             node.addNetworkInterface(core.NetworkInterface.getIntName(core.NetworkInterface.Ethernet100FX) + String.valueOf(i), core.NetworkInterface.Ethernet100FX, false);       
         }
         
 		node.addNetworkInterface(NetworkInterface
				.getIntName(NetworkInterface.Console)
				+ "0", NetworkInterface.Console, false);

	}

}


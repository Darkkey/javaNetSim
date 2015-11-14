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

package textUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;

import org.omg.CORBA.DynAnyPackage.Invalid;

import core.CommunicationException;
import core.InvalidLinkConnectionException;
import core.InvalidLinkNameException;
import core.InvalidNetworkInterfaceNameException;
import core.InvalidNetworkLayerDeviceException;
import core.InvalidNodeNameException;
import core.LowLinkException;
import core.NetworkLayerDevice;
import core.Simulation;
import core.Version;
import core.protocolsuite.tcp_ip.InvalidIPAddressException;



/**

 * Class TextUI

 * Provides a Command Line Interface for creating and manipulating a network simulation.

 * @version v0.20

 **/

class TextUI {

	

	private static final String PREFIX = "jfst";

	

    private Simulation sim;    

	private boolean isDirty = false;	//used to identify if the simulation has changed

	private File simSaveFile; 				//File used to save simulation

	private InputStreamReader stdin = new InputStreamReader(System.in);

	private BufferedReader userInterface = new BufferedReader(stdin);

	private String response; //This is for the catching the users response from the keyboard



	/**

	 * Creates a new simulation 

	 * @author tristan_veness

	 * @author luke_hamilton

	 * @version v0.20

	 **/

	

	private TextUI() {	    	    

	    sim = new Simulation(core.ProtocolStack.TCP_IP);		//Create new Simulation with the type of protocolsuites to be used

	    displayWelcomeMessage();

	    System.out.println("New Simulation created.\n");

		run();

	}

	

	/**

	 * Create new TextUI object

	 * @author tristan_veness

	 * @param args

	 * @version v0.20

	 **/

	public static void main(String args[]) {

		new TextUI();

	}



	/**	

	 * This is the method allow the CLI to run in a shell of it own.

	 * @author angela_brown

	 * @author bevan_calliess

	 * @author luke_hamilton

	 * @author michael_reith

	 * @author robert_hulford

	 * @exception IOEException

	 * @version v0.20

	 **/



	private void run() {

		String args[];

		while (true) {

		         	

	     	try {

	     		System.out.print(">");

	     		response = userInterface.readLine();	//read user input				

		    }

			catch (IOException e) {

				System.out.println("IO Exception encountered.");

			}

			

			//This catchs a rare Nullpointer exception if the user was to

			//Control-C the CLI

			if(response != null){

				args = response.split(" ");	//Split into array

			}else{				

				args = null;

			}

							

			if (args == null);	//This also stop a nullpointer exception if a user was to Control-C the CLI

			else if(args[0].equalsIgnoreCase("")); //This catchs if a user just hits enter

			// QUIT / EXIT command

			else if (args[0].equalsIgnoreCase("QUIT") || args[0].equalsIgnoreCase("EXIT")) {

			    exitapp(args);

			}



			// HELP command

			else if (args[0].equalsIgnoreCase("HELP") || args[0].equalsIgnoreCase("H") ||args[0].equalsIgnoreCase("?")) {

				if (args.length == 1) { displayHelp(); }	// display general help

				else { displayHelp(args[1]); }		// or display help on a specific command

			}



            // ABOUT command

			else if (args[0].equalsIgnoreCase("ABOUT") || args[0].equalsIgnoreCase("VERSION")) {

				displayAboutInfo();

			}

			//add pc

			else if (args[0].equalsIgnoreCase("ADDPC") || args[0].equalsIgnoreCase("ADDP")) {

				addPc(args);

			}

			//add hub

			else if (args[0].equalsIgnoreCase("ADDHUB") || args[0].equalsIgnoreCase("ADDH")) {

				addHub(args);

			}

			//Add switch

			else if(args[0].equalsIgnoreCase("ADDSWITCH") || args[0].equalsIgnoreCase("ADDS")){

				addSwitch(args);

			}

			//add router

			else if (args[0].equalsIgnoreCase("ADDROUTER") || args[0].equalsIgnoreCase("ADDR")) {

				addRouter(args);	

			}

			// DISPLAYNODES / DN command

			else if (args[0].equalsIgnoreCase("DISPLAYNODES") || args[0].equalsIgnoreCase("DN")) { 

				displayNodes(args);	

			}

			// DISPLAYLINKS / DL command

			else if (args[0].equalsIgnoreCase("DISPLAYLINKS") || args[0].equalsIgnoreCase("DLS")) {

				sim.displayLinks();

			}

			//add link

			else if(args[0].equalsIgnoreCase("ADDLINK") || args[0].equalsIgnoreCase("ADDL")){

				addLink(args);

			}

			//delete pc

			else if(args[0].equalsIgnoreCase("DELETEPC") || args[0].equalsIgnoreCase("DELP")){

				deleteNode(args);

			}

			//delete router

			else if(args[0].equalsIgnoreCase("DELETEROUTER") || args[0].equalsIgnoreCase("DELR")){

				deleteNode(args);

			}

			//delete hub

			else if(args[0].equalsIgnoreCase("DELETEHUB") || args[0].equalsIgnoreCase("DELH")){

				deleteNode(args);

			}

			//delete node

			else if(args[0].equalsIgnoreCase("DELETENODE") || args[0].equalsIgnoreCase("DELN")){

				deleteNode(args);

			}

			//delete link

			else if(args[0].equalsIgnoreCase("DELETELINK") || args[0].equalsIgnoreCase("DELL")){

				deleteLink(args);

			}

			//save

			else if(args[0].equalsIgnoreCase("SAVE") || args[0].equalsIgnoreCase("SV")){

				saveSim(args);				

			}

			//save as

			else if(args[0].equalsIgnoreCase("SAVEAS") || args[0].equalsIgnoreCase("SVA")){

				saveSimAs(args);				

			}

			//load

			else if(args[0].equalsIgnoreCase("LOAD") || args[0].equalsIgnoreCase("LD")){

				loadSim(args);				

			}

			else if(args[0].equalsIgnoreCase("GETDEFAULTGATEWAY") || args[0].equalsIgnoreCase("GETGW")){

				getDefaultGateway(args);

			}

			else if (args[0].equalsIgnoreCase("GETSUBNETMASK") || args[0].equalsIgnoreCase("GETSM")){

				getSubnetMask(args);

			}

			//set ip address

			else if(args[0].equalsIgnoreCase("SETIPADDRESS") || args[0].equalsIgnoreCase("SETIP")){

				setIpAddress(args);				

			}

			else if(args[0].equalsIgnoreCase("SETDEFAULTGATEWAY") || args[0].equalsIgnoreCase("SETGW")){

				setDefaultGateway(args);				

			}

			else if(args[0].equalsIgnoreCase("ARP")){

				getArpTable(args);				

			}

			else if (args[0].equalsIgnoreCase("SETSUBNETMASK") || args[0].equalsIgnoreCase("SETSM")){

				setCustomSubnetMask(args);

			}

			else if (args[0].equalsIgnoreCase("PING")){

                            try{

                                sendPing(args);

                        }catch(Exception e){}

			}							

			else if (args[0].equalsIgnoreCase("TEST1") || args[0].equalsIgnoreCase("T1")){

				buildScenario1();

			}

			else if (args[0].equalsIgnoreCase("TEST2") || args[0].equalsIgnoreCase("T2")){

				buildScenario2();

			}

			else if (args[0].equalsIgnoreCase("TEST3") || args[0].equalsIgnoreCase("T3")){

				buildScenario3();

			}

			else if (args[0].equalsIgnoreCase("TEST4") || args[0].equalsIgnoreCase("T4")){

				buildScenario4();

			}

			else if (args[0].equalsIgnoreCase("TEST5") || args[0].equalsIgnoreCase("T5"))

			{

			    buildScenario5();

			}

			else if (args[0].equalsIgnoreCase("DISPLAYLAYERINFO") || args[0].equalsIgnoreCase("DLI")){

				displayRecordedInfo();

			}

			else if (args[0].equalsIgnoreCase("NEWSIM")){

				newsim();

			}

			else { System.out.println("No such command '" + args[0] + "'. Type 'help' for a list of commands."); }

		}

	}





	/**

	 * The displayHelp has been modified in alpha and a short description of the what the command is. 

	 * The shortcut is also displayed next to the command to help users.	 

	 * @author luke_hamilton

	 * @author bevan_calliess

	 * @author michael_reith

	 * @author robert_hulford

	 * @author angela_brown

	 * @version v0.20

	 **/

	private void displayHelp() {

		System.out.println();

		System.out.println("Available commands");

		System.out.println("------------------");

		System.out.println("Please note the shortcut of the command are within the brackets.\n");

		System.out.println("ABOUT (VERSION) - Displays information about program.");

		

		System.out.println("ADDLINK (ADDL) - Adds a link.");

	    System.out.println("ADDPC (ADDP) - Adds a PC.");

	    System.out.println("ADDROUTER (ADDR) - Adds a Router.");

		System.out.println("ADDHUB (ADDH) - Adds a Hub.\n");

	    System.out.println("ARP - Displays the ARP table for the Node");

	   

	    System.out.println("DELETELINK (DELL) - Deletes a link.");

	    System.out.println("DELETEPC (DELP)- Deletes a PC.");

	    System.out.println("DELETEROUTER (DELR)- Deletes a Router");

		System.out.println("DELETEHUB (DELH)- Deletes a Hub");

	

	    System.out.println("DISPLAYLINKS (DLS) - Displays a list of links.");

	    System.out.println("DISPLAYNODES (DN) - Displays a list of nodes.\n");

	    System.out.println("DISPLAYLAYERINFO (DLI) - Displays information relating to the transport of the last Packet.\n");

	  

	    System.out.println("GETDEFAULTGATEWAY (GETGW) - Gets default gateway for current node");

	    System.out.println("GETSUBNETMASK (GETSM) - Gets SubnetMask for current node");

	    

	    System.out.println("LOAD (LD) - Loads saved simulation.");

	

	    System.out.println("PING - Ping another Pc.");

	

	    System.out.println("QUIT (EXIT) - Quits the program.");

	   

	    System.out.println("SAVE (SV) - Saves the simulation in current file.");

	    System.out.println("SAVEAS (SVA) - Saves the current simulation for later use.");

	    System.out.println("SETIPADDRESS (SETIP) - Sets the address of the specified Network Interface.");

	    System.out.println("SETDEFAULTGATEWAY (SETGW) - Sets the default gateway of a specified Node.");

	    System.out.println("SETSUBNETMASK (SETSM) - Sets the subnet mask of a specified Network Interface."); 

	  

	    System.out.println("TEST1 (T1) - Loads test 1 to the current simulation.");

	    System.out.println("TEST2 (T2) - Loads test 2 to the current simulation.");

	    System.out.println("TEST3 (T3) - Loads test 3 to the current simulation.");

	    System.out.println("TEST4 (T4) - Loads test 4 to the current simulation."); 

	    System.out.println("TEST5 (T5) - Loads test 5 to the current simulation.");

	        

	    System.out.println("\nFor more information on a specific command, type HELP command-name.\n");

	    

	    /*

	     * Please note the code below has been taken out of the help command as they have not been

	     * implemented, they have been left commented out for future use as optional additions if wanted.

	     */

	    

	    //System.out.println("ADDINTERFACETOLINK (ADDI) - Adds an interface of a node to a Link.");

	    //System.out.println("ADDINTERFACE (ADDINT) - Adds a network interface.");

	    //System.out.println();

	    //System.out.println("CLEARSCRIPT (CS) - Clears script commands.");

	    //System.out.println("CONFIGUREROUTERS (CR) - Configures all Rrouters.");

	    //System.out.println();

	    //System.out.println("DELETECOMMAND (DC) - Deletes specified line commands.");

	    //System.out.println("DISPLAYALLINFO (DAI)- Displays all layer information.");

	    //System.out.println("DISPLAYINFO (DI)- Displays specified layer information.");

	    //System.out.println("DISPLAYROUTINGTABLES (DRT) - Displays all routing tables.");

	    //System.out.println("DISPLAYSCRIPT (DS) - Displays script contents.");

	    //System.out.println();

	    //System.out.println();

		//System.out.println("RUNSEQUENTIALLY (SEQ) - Runs script sequentially.");

	    //System.out.println("RUNSCRIPT (RS) - Runs script.");

	    //System.out.println("RUNSIMULTANEOUSLY (SIM) - Runs script simultaneously.");

	    //System.out.println();

	    //System.out.println("SETTIMER (SETT) - Sets the allocated time for each layer.");   

	    //System.out.println("STOPINFO (SI) -  Stops the timer.");

	    //System.out.println("SENDPACKET (SEND) - Sends a Packet to a destination.");

		

	}

	

	/**

	 * The displayHelp(string) has been modified in alpha and a more detailed description of the 

	 * what the command is, an example is also given.

	 * @author tristan_veness

	 * @author angela_brown

	 * @author bevan_calliess

	 * @author michael_reith

	 * @author luke_hamilton

	 * @author robert_hulford

	 * @param command - A command from the Help display eg: About or ADDINT

	 * @version v0.20

	 **/

	private void displayHelp(String command)

	{

		

		if (command.equalsIgnoreCase("ABOUT") || command.equalsIgnoreCase("VERSION"))  

		{

			System.out.println();

			System.out.println("*** ABOUT");

			System.out.println("Displays information about this program.");

			System.out.println();

		}	

		else if (command.equalsIgnoreCase("ADDLINK") || command.equalsIgnoreCase("ADDL"))  

		{

			System.out.println();

			System.out.println("*** ADDLINK (ADDL)");

			System.out.println("ADDLINK <linkname> <firstnodename> <firstnodeinterface> <secondnodename> <secondnodeinterface>");

			System.out.println("Adds a Link to the Simulation with the given linkname ");

			System.out.println("between first Nodes interface card and second Nodes interface card.");

			System.out.println("Example:");

			System.out.println("        ADDLINK Link1 PC1 eth0 PC2 eth0");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("ADDPC") || command.equalsIgnoreCase("ADDP"))  

		{

			System.out.println();

			System.out.println("*** ADDPC (ADDP)");

			System.out.println("ADDPC <nodename>");

			System.out.println("Adds a PC (Node) to the Simulation with the given nodename");

			System.out.println("Example:");

			System.out.println("        ADDPC PC1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("ADDHUB") || command.equalsIgnoreCase("ADDH"))  

		{

			System.out.println();

			System.out.println("*** ADDHUB (ADDH)");

			System.out.println("ADDHUB <nodename>");

			System.out.println("Adds a Hub (Node) to the Simulation with the given nodename");

			System.out.println("Example:");

			System.out.println("        ADDHUB HUB1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("ADDROUTER") || command.equalsIgnoreCase("ADDR"))  

		{

			System.out.println();

			System.out.println("*** ADDROUTER (ADDR)");

			System.out.println("ADDROUTER <nodename>");

			System.out.println("Adds a Router (Node) to the Simulation with the given nodename");

			System.out.println("Example:");

			System.out.println("        ADDROUTER Router1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("ARP"))  

		{

			System.out.println();

			System.out.println("*** ARP ");

			System.out.println("ARP <nodename>");

			System.out.println("Displays the ARP table for a given nodename");

			System.out.println("Example:");

			System.out.println("        ARP pc1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("DELETEHUB") || command.equalsIgnoreCase("DELH"))  

		{

			System.out.println();

			System.out.println("*** DELETEHUB (DELH)");

			System.out.println("DELETEHUB <hunname>");

			System.out.println("Deletes a Hub in the Simulation with the given nodename.");

			System.out.println("Example:");

			System.out.println("        DELETEHUB HUB1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("DELETELINK") || command.equalsIgnoreCase("DELL"))  

		{

			System.out.println();

			System.out.println("*** DELETELINK (DELL)");

			System.out.println("DELETELINK <linkname>");

			System.out.println("Deletes a Link in the Simulation with the given linkname.");

			System.out.println("Example:");

			System.out.println("        DELETELINK Link1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("DELETEPC") || command.equalsIgnoreCase("DELP"))  

		{

			System.out.println();

			System.out.println("*** DELETEPC (DELP)");

			System.out.println("DELETEPC <nodename>");

			System.out.println("Deletes a PC (Node) in the Simulation with the given nodename");

			System.out.println("Example:");

			System.out.println("        DELETEPC PC1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("DELETEROUTER") || command.equalsIgnoreCase("DELR"))  

		{

			System.out.println();

			System.out.println("*** DELETEROUTER (DELR)");

			System.out.println("DELETEROUTER <nodename>");

			System.out.println("Deletes a Router (Node) in the Simulation with the given nodename");

			System.out.println("Example:");

			System.out.println("        DELETEROUTER Router1");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("DISPLAYLINKS") || command.equalsIgnoreCase("DLS"))  

		{

			System.out.println();

			System.out.println("*** DISPLAYLINKS (DLS)");

			System.out.println("Displays the list of Links contained in the Network Simulation.");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("DISPLAYNODES") || command.equalsIgnoreCase("DN"))  

		{

			System.out.println();

			System.out.println("*** DISPLAYNODES (DN)");

			System.out.println("Displays the list of Nodes contained in the Network Simulation.");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("GETDEFAULTGATEWAY") || command.equalsIgnoreCase("GETGW"))

		{

			System.out.println();

			System.out.println("*** GETDEFAULTGATEWAY (GETGW)");

			System.out.println("GETDEFAULTGATEWAY <nodename>");

			System.out.println("Gets the default gateway address of the specified Network Interface of the Node with the specified nodename.");

			System.out.println("The name must be a valid node name.");

			System.out.println("Examples:");

			System.out.println("        GETDEFAULTGATEWAY pc1 ");

			System.out.println("        GETDEFAULTGATEWAY router1 ");			

			System.out.println();

		}	

		else if (command.equalsIgnoreCase("GETSUBNETMASK") || command.equalsIgnoreCase("GETSM"))

		{

			System.out.println();

			System.out.println("*** GETSUBNETMASK (GETSM)");

			System.out.println("GETSUBNETMASK <nodename><Interface>");

			System.out.println("Gets the subnet mask address of the specified Network Interface of the Node with the specified nodename.");

			System.out.println("The address is a valid subnet mask address.");

			System.out.println("Example:");

			System.out.println("        GETSUBNETMASK pc1");

			System.out.println();

		}

		

		else if (command.equalsIgnoreCase("LOAD") || command.equalsIgnoreCase("LD"))

		{

			System.out.println();

			System.out.println("*** LOAD (LD)");

			System.out.println("Load a simulation object from a saved file. ");

			System.out.println("The path to the file must be included. ");

			System.out.println("Example:");

			System.out.println("		load drive:directory/filename."+TextUI.PREFIX);

			System.out.println();

		}

		else if (command.equalsIgnoreCase("PING"))

		{

			System.out.println();

			System.out.println("*** PING ");

			System.out.println("Pings another Pc. ");

			System.out.println("Example:");

			System.out.println("		PING <SourceNodeName> <DestIPAddress>");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("EXIT") || command.equalsIgnoreCase("QUIT"))  

		{

			System.out.println();

			System.out.println("*** EXIT (QUIT)");

			System.out.println("Exits from the program.");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("SAVE") || command.equalsIgnoreCase("SV"))

		{

			System.out.println();

			System.out.println("*** SAVE (SV)");

			System.out.println("Saves the current simulation object into the current simulation file.");

			System.out.println("Please note that when saving for the first time you must use the command <SAVEAS>.");

			System.out.println("Example:");

			System.out.println("		SAVE");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("SAVEAS") || command.equalsIgnoreCase("SVA"))

		{

			System.out.println();

			System.out.println("*** SAVEAS (SVA)");

			System.out.println("Saves the current simulation object into a file for use at a later date.");

			System.out.println("If a file name has not been entered the simulation will saved under the default " +

					"file name Sim."+TextUI.PREFIX);

			System.out.println("Example:");

			System.out.println("		SAVEAS filename");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("SETIPADDRESS") || command.equalsIgnoreCase("SETIP"))  

		{

			System.out.println();

			System.out.println("*** SETIPADDRESS (SETIP)");

			System.out.println("SETIPADDRESS <nodename> <interfacename> <address>");

			System.out.println("Sets the IP address of the specified Network Interface of the Node with the specified nodename.");

			System.out.println("The address must be a valid IP address.");

			System.out.println("Examples:");

			System.out.println("        SETIPADDRESS pc1 eth0 192.168.0.245");

			System.out.println("        SETIPADDRESS router1 eth1 10.0.5.56");			

			System.out.println();

		}

		else if (command.equalsIgnoreCase("SETSUBNETMASK") || command.equalsIgnoreCase("SETSM"))

		{

			System.out.println();

			System.out.println("*** SETSUBNETMASK (SETSM)");

			System.out.println("SETSUBNETMASK <nodename> <interface> <address>");

			System.out.println("Sets the subnet mask address of the specified Network Interface of the Node with the specified nodename.");

			System.out.println("The address must be a valid subnet mask address.");

			System.out.println("Example:");

			System.out.println("        SETSUBNETMASK pc1 255.255.252.0");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("SETDEFAULTGATEWAY") || command.equalsIgnoreCase("SETGW"))

		{

			System.out.println();

			System.out.println("*** SETDEFAULTGATEWAY (SETGW)");

			System.out.println("SETDEFAULTGATEWAY <nodename> <address>");

			System.out.println("Sets the default gateway address of the specified Network Interface of the Node with the specified nodename.");

			System.out.println("The address must be a valid gateway address.");

			System.out.println("Examples:");

			System.out.println("        SETDEFAULTGATEWAY pc1 192.168.0.245");

			System.out.println("        SETDEFAULTGATEWAY router1 10.0.5.56");			

			System.out.println();

		}

		else if (command.equalsIgnoreCase("DISPLAYLAYERINFO") || command.equalsIgnoreCase("DLI")){

			System.out.println();

			System.out.println("*** DISPLAYLAYERINFO (DLI)");

			System.out.println("Displays information relating to the transport of the last packet.");

			System.out.println();

		}

		else if (command.equalsIgnoreCase("TEST1") || command.equalsIgnoreCase("T1")){

			System.out.println();

			System.out.println("*** TEST1 (T1)");

			System.out.println("Creates a test simulation connecting a PC to another PC.");

			System.out.println("The IP addresses, defaultgateways and subnetmask have been set.");

			System.out.println("Example:");

			System.out.println("		TEST1");

			System.out.println("		T1");	

			System.out.println();

		}else if (command.equalsIgnoreCase("TEST2") || command.equalsIgnoreCase("T2")){

			System.out.println();

			System.out.println("*** TEST2 (T2)");

			System.out.println("Creates a test simulation connecting a PC to another PC via a router.");

			System.out.println("The IP addresses, defaultgateways and subnetmask have been set.");

			System.out.println("Example:");

			System.out.println("		TEST2");

			System.out.println("		T2");	

			System.out.println();

		}else if (command.equalsIgnoreCase("TEST3") || command.equalsIgnoreCase("T3")){

			System.out.println();

			System.out.println("*** TEST3 (T3)");

			System.out.println("Creates a test simulation connecting a PC to another PC via a 2 routers.");

			System.out.println("The IP addresses, defaultgateways and subnetmask have been set.");

			System.out.println("Example:");

			System.out.println("		TEST3");

			System.out.println("		T3");	

			System.out.println();

		}

		else if (command.equalsIgnoreCase("TEST4") || command.equalsIgnoreCase("T4")){

			System.out.println();

			System.out.println("*** TEST4 (T4)");

			System.out.println("Creates a test simulation connecting four PCs to a hub.");

			System.out.println("The IP addresses, defaultgateways and subnetmask have been set.");

			System.out.println("Example:");

			System.out.println("		TEST4");

			System.out.println("		T4");	

			System.out.println();

		}

		else if (command.equalsIgnoreCase("TEST5") || command.equalsIgnoreCase("T5")){

			System.out.println();

			System.out.println("*** TEST5 (T5)");

			System.out.println("Creates a test simulation conntecting a router to two hubs on different networks.");

			System.out.println("Each hub is connected to four pcs.");			

			System.out.println("The IP addresses, defaultgateways and subnetmask have been set.");

			System.out.println("Example:");

			System.out.println("		TEST4");

			System.out.println("		T4");	

			System.out.println();

		}

	}

	

	/**

	 * This method displays a message to CLI when first loaded.

	 * @author angela_brown

	 * @author bevan_calliess

	 * @author luke_hamilton

	 * @version v0.20

	 **/

	private void displayWelcomeMessage() {

	     System.out.println("Welcome to the Java Firewall Simulator Command Line Interface");

	     System.out.println("You may type a command at any time.");

	     System.out.println("Type 'help' for a list of commands.\n");	   

      	 System.out.println();

	}

  		

	/**

	 * This method is for displaying the about information of the program

	 * @author luke_hamilton

	 * @version v0.20

	 **/

	private void displayAboutInfo() {

		System.out.println("\nJava Firewall Simulator Command Line Interface");

		System.out.println("----------------------------------------------");

		System.out.println("Developed by: Canberra Institute of Technology, Bruce, ACT, Australia");

		System.out.println("Version: "+Version.CORE_VERSION);						

		

		System.out.println("\nDeveloper\t\t\t\tRoll\t\t\t\t\t\t");

		System.out.println("----------------------------------------------------------------------------------------");

		for (int i = 0,c = 1 ; i < Version.TEAM_MEMBERS.length; i++, c++) {			

			

			System.out.print(Version.TEAM_MEMBERS[i]);

			

			//System.out.print(i);

			if( i == 0 || i == 12 || i == 5 || i == 9){				

				System.out.println();

			}

									

			if(c == 4){

				System.out.println();

				c = 0;

			}

			

		}

		System.out.println();

	}

	

	/**

	 * This method is for creating a standard PC within the Simulation

	 * A PC, is created with a default of one network interface.

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: addpc pc1

	 * @exception InvalidNodeNameException

	 * @version v0.20

	 **/

	private void addPc(String args[]){

		//If the number of arguments (including the command name) is less than the command requires display an error		

		if (args.length != 2) {

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <pcname>");

		}else{			

			try {

				sim.addPC(args[1], true);	

				System.out.println("Node '"+ args[1] + "' created.");

				System.out.println();

				isDirty=true;

			} catch (InvalidNodeNameException e) {

				System.out.println("Node with the name: '"+ args[1] + "' already exist within Simulation.");

			}	

		}	

	}

	

	/**

 	 * This method is for creating a router within the simulation

	 * A router is created with a default of two network interfaces

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: addrouter router1

	 * @exception InvalidNodeNameException

	 * @version v0.20

	 **/

	private void addRouter(String args[]){

		// If the number of arguments (including the command name) is less than the command requires display an error

		if (args.length != 2) {

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <routername>");

		}else{			

			try {

				sim.addRouter(args[1], true);

				System.out.println("Node '"+ args[1] + "' created.");

				System.out.println();

				isDirty=true;

			} catch (InvalidNodeNameException e) {

				System.out.println("Node with the name: '"+ args[1] + "' already exist within Simulation.");

			}	

		}		

	}

	/**

	 * This method send a Pings from one Pc to another 

	 * it catches insufficient parameters and checks for the 

	 * existance of nodes

	 * @author angela_brown

	 * @author bevan_calliess

	 * @param args[] - String of arguments from the command line eg: sendPing pc1 192.168.0.2

	 * @exception InvalidNodeNameException

	 * @exception InvalidNetworkLayerDeviceException

	 * @version v0.20

	 **/

	private void sendPing(String args[]) throws LowLinkException

	{

		if (args.length!=3){

			System.out.println("Insufficient Parameters");

			System.out.println("Use: " + args[0] + "<SourceNodeName> <DestIPAddress>");

		}

		else{

		try{

			// clear the recoding of the last packet movement

			sim.clearLayerInfo();			

			sim.sendPing(args[1],args[2]);

			System.out.println("Ping Packet sent: use DLI command to display information.");

			System.out.println();

		}catch(CommunicationException e)

		{

			System.out.println(e.toString());

			System.out.println();

		}catch(InvalidNodeNameException e)

		{

			System.out.println("Node: " + args[1] + " does not exist");

		}catch(InvalidNetworkLayerDeviceException e)

		{

			System.out.println("Node: " + args[1] + " is not a Network Layer Device");

		}

		}

		System.out.println();

	}

	

	

	/**

	 * This method creates a link between two nodes within the simulation

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: addlink Link1

	 * @exception InvalidLinkNameException

	 * @exception InvalidLinkConnectionException

	 * @exception InvalidNetworkInterfaceNameException

	 * @exception InvalidNodeNameExcpetion

	 * @version v0.20

	 **/	

	private void addLink(String args[]){

		if(args.length != 6){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <linkname> <firstnodename> <firstnodeinterface> <secondnodename> <secondnodeinterface>");

			System.out.println("Eg:  " + args[0] + " link1 pc1 eth0 router1 eth4");

		}else{

			try {				

				sim.addEthernetLink(args[1],args[2],args[3],args[4],args[5]);

				System.out.println("Link '"+ args[1] + "' created.\n");				

				isDirty=true;

			} catch (InvalidLinkNameException e) {

				System.out.println("Link with the name: '"+ args[1] + "' already exist within Simulation.");

			}catch(InvalidLinkConnectionException e){

				System.out.println("Unable to create link. A connection to one of the interface specified may already exist.\n");

			}catch (InvalidNetworkInterfaceNameException e) {				

				System.out.println(e);

			}catch (InvalidNodeNameException e) {

				System.out.println("Unable to create link. One of the node names passed dosen't exist within the simulation\n");

			}			

		}

	}

	

	/**

	 * This method will close the current simulation down.

	 * In doing this it will ask the user for if they would like

	 * to save it. If so asks for file name. If not exits without saving.

	 * @author bevan_calliess

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: exit

	 * @exception IOException

	 * @version v0.20

	 **/

	private void exitapp(String args[]){		

		if(isDirty){

		    

		    //get the user input from the command line

		    try{

		        System.out.print("Would you like to save the current simulation (y/N)? ");

		        response = userInterface.readLine();

		        if(response.equalsIgnoreCase("Y")){

		            if(simSaveFile != null){

		            	System.out.print("Save simulation to '"+ simSaveFile.getName() + "' (y/N)?");

		            	response = userInterface.readLine();

		            	if(response.equalsIgnoreCase("Y")){

		            		saveSim(args);

		            	}else

		            		saveSimAs(args);		            	

		            }else

			            saveSimAs(args);		            

		        }			     	     		

		    }catch (IOException e) {

					System.out.println("IO Exception encountered.");					

			}

		}				

		System.out.println("Program exited normally.");

		System.exit(0); 		

	}

	

	

	/**

	 * This method deletes a link that connected two nodes together within the simulation

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: deletelink link1

	 * @exception InvalidLinkNameException

	 * @exception IOException

	 * @version v0.20

	 **/

	private void deleteLink(String args[]) {		

		

		if(args.length != 2){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <linkname>");

		}else{

			if(sim.containsLink(args[1])){

				try {

					System.out.print("Are you sure you want to delete '"+args[1]+"'?\n" +

						"This will also reset all connected NetworkInterfaces (y/n)?");

					response = userInterface.readLine();	

					if(response.equalsIgnoreCase("Y")){

						try {				

							sim.deleteLink(args[1]);	

							System.out.println("\nDeleted: '"+args[1]+"'\n");

							isDirty = true;

						} catch (InvalidLinkNameException e) {

							System.out.println("Link with the name: '"+ args[1] + "' dosent exist within Simulation.");

						}						

					}

				} catch (IOException e) {

					System.out.println("IO Exception encountered.");					

				}	

			}else{

				System.out.println("'"+args[1]+"' does not exist within Simulation.\n");

			}

		}	

	}



	/**

	 * This method deletes a Node from within the simulation.

	 * Please note that when a node is deleted all link connected to the node will also

	 * be deleted. And Any network interface connect to that link will

	 * be reset.

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: deleteNode pc1

	 * @exception IOException

	 * @exception InvalidNodeNameException

	 * @version v0.20

	 **/

	private void deleteNode(String args[]) {		

		

		if(args.length != 2){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <nodename>");

		}else{

			//test if node exist

			if(sim.containsNode(args[1])){

				try {

					System.out.print("Are you sure you want to delete '"+args[1]+"'?\n" +

						"This will also delete all connected links, \nand reset any NetworkInterfaces connected to those links (y/n)?");

					response = userInterface.readLine();	

					if(response.equalsIgnoreCase("Y")){

						try {				

							sim.deleteNode(args[1]);	

							System.out.println("\nDeleted: '"+args[1]+"'\n");

							isDirty = true;

						} catch (InvalidNodeNameException e) {

							System.out.println("Node with the name: '"+ args[1] + "' dosent exist within Simulation.");

						}						

					}

				} catch (IOException e) {

					System.out.println("IO Exception encountered.");					

				}					

			}else{

				System.out.println("'"+args[1]+"' does not exist within Simulation.\n");

			}

		}	

	}

	

	/**

	 * This method will save the current simulation object out for later use

	 * @author bevan_calliess

     * @param args[] - String of arguments from the command line eg: save temp

	 * @exception IOException

	 * @version v0.20

	 **/

	private void saveSimAs(String args[]){

		//test to see if a file name was provided and create a file

	 	// with that name, if no name is provided use the default filename 'sim'

	    String fileName; 

	 	if(args.length < 2){	//check to see if the filename argument exists

	 		try {

	 			System.out.print("Please enter a file name: ");

	 			fileName = userInterface.readLine();

		        

	 			if(fileName.equalsIgnoreCase("")){  //if the user just pressed return use default

	 				fileName = "sim";

		            System.out.println("No file name provided using default");

		        }	 			if(!fileName.endsWith(TextUI.PREFIX)){

	 				fileName = fileName + "."+ TextUI.PREFIX;

	 			}

	 		 	//overwrite the simSaveFile with the new file

	 		 	simSaveFile = new File(fileName);

	 		 	saveSim(args);

			} catch (IOException e) {

				System.out.println("Error is writing to file");

			}	 		

	 	}else if(args.length > 2){

	 		System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <savename>\n");

	 	}

	 	else{		//if it does exist check it is not empty	 							 	

	 	   	if(!args[1].endsWith(TextUI.PREFIX)){

	 	   		fileName = args[1]+"."+TextUI.PREFIX;

	 	   	}else{

	 	   		fileName = args[1];

	 	   	}

	 		

 		 	//overwrite the simSaveFile with the new file

 		 	simSaveFile = new File(fileName);

 		 	saveSim(args);

	 	}

	 }

	 

	/**

	 * This method will save the current simulation object into the current

	 * saveSimFile.  If the file is still null then no action is taken

	 * and the user is informed of the error.

	 * @author bevan_calliess

	 * @param args[] - String of arguments from the command line eg: save temp

	 * @exception IOException

	 * @exception InterruptedException

	 * @version v0.20

	 **/

	private void saveSim(String args[]){

		//test to see if the saveSimFile is null

		if(simSaveFile !=null && args.length <=2){

			try{		 	// open a file output stream to write to using the new File object

				FileOutputStream out = new FileOutputStream(simSaveFile);

		 	    ObjectOutputStream objStream = new ObjectOutputStream(out);

		 	    objStream.writeObject(sim);

		 	    objStream.flush();

		 	    

		 	    //This prints a text based progress of the file being saved

		 	    //Its really just for show.

		 	    System.out.print("Saving");

		 	    for(int i = 0; i < 10; i++){

		 	    	System.out.print(".");

		 	    	try {

		 	    		Thread.sleep(50);

					} catch (InterruptedException e) {}

		 	    }

		 	    System.out.println("Simulation was saved succesfully.");

		 	    System.out.println("File "+ simSaveFile.getName() + " has been saved to " + simSaveFile.getAbsolutePath());

		 	    System.out.println();

		 	    isDirty=false;

			}catch(IOException e){

		 		System.out.println("There was an error writing the simulation to file.\n");

		 	}

		 }else if (simSaveFile == null && args.length <=2){

		 	System.out.println("No save file present using the Save as function.");

		 	saveSimAs(args);

		 }else{

		 	System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <savename>");

		 }

	}

	 

	 

	/**

	 * This method will load a simulation object from a file

	 * Please note args[1] contains the name of the file to load simulation from

	 * @author angela_brown

	 * @author  bevan_calliess

	 * @param args[] - String of arguments from the command line eg: load temp

	 * @exception IOException

	 * @exception InterruptedException

	 * @exception ClassNotFoundException

	 * @version v0.20

	 **/

	private void load(String args[]) {



		//set the sim variable to the new serialized version

		try {

			if(!args[1].endsWith(TextUI.PREFIX)){ args[1] = args[1] + "."+TextUI.PREFIX;}		//Add PREFIX if not already added

			

			File inFile = new File(args[1]);

			FileInputStream in = new FileInputStream(inFile);

			ObjectInputStream s = new ObjectInputStream(in);

			sim = (Simulation) s.readObject();



			//save the file for later use with the saveSim method

			simSaveFile = inFile;



			//This prints a text based progress of the file beening loaded

	 	    //Its realy just for show.

	 	    System.out.print("Loading File");

	 	    for(int i = 0; i < 10; i++){

	 	    	System.out.print(".");

	 	    	Thread.sleep(50);

	 	    }			

			System.out.println(inFile+" Loaded.\n");

			isDirty = false;

		}catch (IOException e) {

			System.out.println(e.getMessage()+ "There was an error reading from the file.\n");

		}catch (InterruptedException e){

			System.out.println("Interrupted Thread\n"); 

		}catch (ClassNotFoundException e){

			System.out.println(e.getMessage()+ "There was an error reading from the file.\n");

		}

	}

	

	/**

	 * This method will load a simulation object from file 

	 * args[1] contains the name of the file to load simulation from A message has been added to

	 * ask the user if they wish to load over another file if they try such as

	 * silly thing. :)

	 * @author bevan_calliess - modified by angela_brown 

	 * @param args[] - String of arguments from the command line eg: load temp

	 * @exception Exception 

	 * @version v0.20

	 **/

	private void loadSim(String args[]) {



		if (args.length != 2) { //check to see if the filename argument exists

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0]+ " <filename> including path eg c:/savedSimFile."+TextUI.PREFIX+"\n");

		} else { // create a new file object based on the args[1] parameter				



			if (isDirty) {

				System.out.print("You are about to load over another simulation, do you wish to continue (y/N)? ");

				try {

					//check to see what the user has entered.	

					response = userInterface.readLine();



					if (response.equalsIgnoreCase("Y")) {

						//calls load method	

						load(args);

					} else {

						System.out.print("You have choosen not to load a file, process has been aborted. \n");						

					}

				} catch (Exception e) {

					//you should never get this message!!

					System.out.println(e.getMessage() + "Command line error.\n");

				}

			} else {

				//calls load method	

				load(args);

			}

		}

	}

	

	/**

	 * This method will set the ip address of a node interface

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: setaddress pc1 eth0 192.168.0.1

	 * @exception InvalidNodeNameException

	 * @exception InvalidNetworkInterfaceNameException

	 * @exception InvalidIPAddressException

	 * @exception InvalidNetworkLayerDeviceExcpetion

	 * @version v0.20

	 **/	

	private void setIpAddress(String args[]){

		if (args.length != 4){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0]+ " <nodename> <interface> <ipaddress>");

			System.out.println("Eg :  " + args[0] + " pc1 eth0 192.168.0.2\n");

		}else{

			/*try {

				sim.setIPAddress(args[1],args[2],args[3]);	

				System.out.println("IP Address set.\n");

			} 

			catch (InvalidNodeNameException e) {

				System.out.println("'"+args[1]+"' does not exist within Simulation.\n");

			}

			catch (InvalidNetworkInterfaceNameException e){

				System.out.println("'"+args[2]+"' does not exist within the Node.\n");

			}

			catch (InvalidIPAddressException e){

				System.out.println("'"+args[3]+"' Invalid IP Address entered.\n");

			}

			catch (InvalidNetworkLayerDeviceException e){

				System.out.println("IP Address's cant be set on '"+args[1]+"'. This isn't a network layer device.\n");

			}	*/					

		}

	}

	

	/**

	 * This method creates a hub.

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: addhub hub1 

	 * @exception InvalidNodeNameException

	 * @version v0.20

	 **/

	private void addHub(String args[]){

		//If the number of arguments (including the command name) is less than the command requires display an error		

		if (args.length != 2) {

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <hubname>");

		}else{					

			try {

				sim.addHub(args[1], true);	

				System.out.println("Node '"+ args[1] + "' created.\n");				

				isDirty=true;

			}catch (InvalidNodeNameException e) {

				System.out.println("Node with the name: '"+ args[1] + "' already exist within Simulation.");

			}	

		}		

	}

	/**	

	 * This method passes in a string of args and checks the details of the input.

	 * If there is an error in the string an error message is produced to the screen. 	 	 

	 * If there is no error then the setdefaultgateway method is called in the simulation.java 

	 * @author angela_brown

	 * @author michael_reith

	 * @param args[] - String of arguments from the command line eg: setgw pc1 192.168.0.245 

	 * @exception InvalidNodeNameException

	 * @exception Invalid NetworkLayerDeviceException

	 * @exception Invalid DefaultGatewayException

	 * @version v0.20

	 **/

	private void setDefaultGateway(String args[])

	{

		if (args.length !=3){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <nodename> <IPAddress>");

		}else{

			/*try{

				sim.setDefaultGateway(args[1],args[2]);

				System.out.println("Node '" + args[1] + "' has been set with a default gateway.\n");

				isDirty=true;

			}catch (InvalidNodeNameException e){

				System.out.println("Node with the name: '" + args[1] + "' does not exsist.");

			}catch (InvalidNetworkLayerDeviceException e){

				System.out.println("Default Gateway cannot be set on '"+args[1]+"'. This isn't a network layer device.\n");

			}catch (InvalidDefaultGatewayException e){

				System.out.println("Invalid Default Gateway");

			}*/

		}

	}

	

	/**

	 * This method will create a switch with a default of 8 ports.

	 * This method is for future implementation, once switches have been designed for the system. 

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: addswitch  switch1 

	 * @exception InvalidNodeNameException

	 * @version v0.20

	 **/

	private void addSwitch(String args[]){

		//If the number of arguments (including the command name) is less than the command requires display an error		

		if (args.length != 2) {

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <hubname>");

		}else{					

			try {

				sim.addSwitch(args[1], true);	

				System.out.println("Node '"+ args[1] + "' created.\n");				

				isDirty=true;

			}catch (InvalidNodeNameException e) {

				System.out.println("Node with the name: '"+ args[1] + "' already exist within Simulation.");

			}	

		}

		System.out.println();

	}

	

	/**

	 * This method display information about all node within the current simulation,

	 * or a selected node within the simulation.

	 * @author luke_hamilton

	 * @param args[] - String of arguments from the command line eg: dn 

	 * @exception InvalidNodeNameException

	 * @version v0.20

	 **/

	private void displayNodes(String args[]){

		if(args.length == 1){

			sim.displayNodes();

		}else if (args.length == 2){

			try {

				sim.displayNode(args[1]);	

			} catch (InvalidNodeNameException e) {

				System.out.println("'"+args[1]+"' does not exist within Simulation.\n");

			}

		}

	}

	/**

	 * This method gets the string args and outputs it to the screen

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param args[] - String of arguments from the command line eg: ARP 

	 * @exception InvalidNodeNameException

	 * @exception InvalidNetworkLayerDeviceException

	 * @version v0.20

	 **/

	private void getArpTable(String args[])

	{

		if (args.length != 2){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <hubname>");

		}

		else

		{

			try	{

				String[] arpTableEntries = ((core.NetworkLayerDevice)sim.getNode(args[1])).getFormattedARPTable();

				for(int i = 0;i<arpTableEntries.length;i++)
					System.out.println(arpTableEntries[i]);
				
			}catch(ClassCastException e){

				System.out.println("'"+args[1]+"' is not a network device.\n");

			}

		}

	}	

	

	/**

	 * This method passes a string in if ok assigns it to the default gateway	 

	 * @author angela_brown

	 * @author michael_reith

	 * @param args[] - String of arguments from the command line eg: getgw pc1

	 * @exception InvalidNodeNameException

	 * @exception InvalidNetworkLayerDeviceException

	 * @version v0.20

	 **/

	private void getDefaultGateway(String args[]){

		if (args.length != 2){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <nodename>");

			

		}else{

			try {

				System.out.println("GW: "+ ((core.NetworkLayerDevice)sim.getNode(args[1])).getDefaultGateway());	

			}catch (ClassCastException e){

				System.out.println("Default Gateway cannot be set on '"+ args[1]+ "'. This isn't a network layer device.\n");

			}

	    }

	}

	

	/**

	 * This method is used to set a custom subnet mask for a node

	 * @author bevan_calliess 

	 * @author robert_hulford

	 * @param args[] - String of arguments from the command line eg:  setsubnetmask pc1 255.255.252.0

	 * @exception InvalidNodeNameException

	 * @exception InvalidNetworkInterfaceNameException

	 * @exception InvalidSubnetMaskExcpetion

	 * @version v0.20

	 **/

	private void setCustomSubnetMask(String args[]){

		if (args.length != 4){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <nodename> <Interface Name> <Subnet Mask>");			

		}else{

			/*try {

				//sim.setCustomSubnetMask(args[1],args[2],args[3]);

				System.out.println("Node '" + args[1] + "' has been set with a subnet mask.\n");

				isDirty=true;

			}catch(InvalidNetworkLayerDeviceException e){

					System.out.println("Node '"+ args[1]+ "'. is not a network layer device.\n");

			}catch (InvalidNodeNameException e){

				System.out.println("Node with the name: '" + args[1] + "' does not exsist.");

			}catch (InvalidNetworkInterfaceNameException e){

				System.out.println("NetworkInterface '"+ args[2]+ "' is not a valid Interface for this Node.\n");

			}catch (InvalidSubnetMaskException e){

				System.out.println("SubnetMask '" + args[3] + "' is not a valid subnet mask for this node");
			
			}*/	

	    }

	}

	/**

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param args[] - String of arguments from the command line eg:  getsubnetmask pc1 eth0

	 * @exception InvalidNodeNameException

	 * @exception InvalidNetworkLayerDeviceException

	 * This method returns the subnet mask of the Node and interface passed in

	 * @version v0.20

	 **/

	private void getSubnetMask(String args[]){

		if (args.length != 3){

			System.out.println("Insufficient Parameters.");

			System.out.println("Use: " + args[0] + " <nodename> <interface>");			

		}else{

			try {

				System.out.println("SubnetMask: "+ ((NetworkLayerDevice)sim.getNode(args[1])).getSubnetMask(args[2]));				

			}catch (ClassCastException e){

				System.out.println("Node '"+ args[1]+ "'. is not a network layer device.\n");

			}		

	    }

	}

	/**

	 * This method displays a message the user informing them that they will

	 * override the current simulation.

	 * @author luke_hamilton

	 * @exception IOException 

	 * @return boolean 

	 * @version v0.20

	 **/

	private boolean buildScenarioWARNING(){

		if(sim.containsObjects()){

			try {

				System.out.println("WARNING. This will delete all object within the current simulation");

				System.out.print("Are you sure you want to continue(y/N)?");

				response = userInterface.readLine();					

			} catch (IOException e) {

				System.out.println("IO Exception encountered.");

			}			

			if(response.equalsIgnoreCase("Y")){

				sim.removeAllObjects();

				return true;		

			}

				return false;

		}

		System.out.println();

		return true;	

	}

	

	/**

	 * This will build the test Scenario 1 which is just two Pc's and a link 	

	 * @author luke_hamilton

	 * @exception Exception 

	 * @version v0.20

	 **/

	private void buildScenario1(){

		if(buildScenarioWARNING()){

			try {

				sim.addPC("pc1", true);

				sim.addPC("pc2", true);							

				sim.addEthernetLink("link1", "pc1", "eth0", "pc2", "eth0");

				//sim.setIPAddress("pc1","eth0","192.168.0.1");

				//sim.setIPAddress("pc2","eth0","192.168.0.2");

				//as a temp fix for the bug in ARP if you reset the 

				//ip address for all nodes the machine can then check that it

				// is local and set the ARP entry				

				//sim.setIPAddress("pc1","eth0","192.168.0.1");

				//sim.setIPAddress("pc2","eth0","192.168.0.2");

				

				isDirty = true;

				System.out.println("Test Scenario 1 created\n");	

			} catch (Exception e) {

				System.out.println("This should never happen, as these values are hardcoded!!!");

				e.printStackTrace();

			}								 

		}

	}

				

	

	/**	 

	 * This will build the test Scenario 2 which is two Pc's and a route between them

	 * @author luke_hamilton

	 * @exception Exception

	 * @version v0.20

	 **/

	private void buildScenario2(){

		if(buildScenarioWARNING()){

			try {

				/*sim.addPC("pc1", true);

				sim.addRouter("router1", true);

				sim.addPC("pc2", true);

				

				

				sim.setIPAddress("pc1","eth0","192.168.0.2");

				sim.setDefaultGateway("pc1","192.168.0.1");

				sim.setIPAddress("pc2","eth0","10.0.0.2");

				sim.setDefaultGateway("pc2","10.0.0.1");

				sim.setIPAddress("router1","eth0","192.168.0.1");

				sim.setIPAddress("router1","eth1","10.0.0.1");

											

				sim.addEthernetLink("link1", "pc1", "eth0", "router1", "eth0");

				sim.addEthernetLink("link2", "pc2" , "eth0", "router1", "eth1");

				*/

				isDirty = true;

				System.out.println("Test Scenario 2 created\n");	

			} catch (Exception e) {

				System.out.println("This should never happed");

				e.printStackTrace();

			}								 

		}

	}

	

	/**

	 * This will build the test Scenario 3. Two pc's connected to via 2 routers.

	 * @author angela_brown

	 * @author bevan_calliess

	 * @exception Exception

	 * @version v0.20

	 **/

	private void buildScenario3(){

		if(buildScenarioWARNING()){

			try {

				/*sim.addPC("pc1", true);

				sim.addRouter("router1", true);

				sim.addRouter("router2", true);

				sim.addPC("pc2", true);

				

				sim.setIPAddress("pc1","eth0","192.168.0.2");

				sim.setDefaultGateway("pc1","192.168.0.1");

				sim.setIPAddress("pc2","eth0","10.0.0.2");

				sim.setDefaultGateway("pc2","10.0.0.1");

				sim.setIPAddress("router1","eth0","192.168.0.1");

				sim.setIPAddress("router1","eth1","127.10.0.1");

				sim.setDefaultGateway("router1","127.10.0.2");

				sim.setIPAddress("router2","eth1","127.10.0.2");

				sim.setIPAddress("router2","eth0","10.0.0.1");

				sim.setDefaultGateway("router2","127.10.0.1");

															

				sim.addEthernetLink("link1", "pc1", "eth0", "router1", "eth0");

				sim.addEthernetLink("link2", "pc2" , "eth0", "router2", "eth0");

				sim.addEthernetLink("link3", "router1" , "eth1", "router2", "eth1");

				*/

				isDirty = true;

				System.out.println("Test Scenario 3 created\n");	

			} catch (Exception e) {

				System.out.println("This should never happed");

				e.printStackTrace();

			}								 

		}

		 

	}

	

	/**

	 * This will build the test Scenario 4. Four pc's connected on the same network 

	 * to a hub

	 * @author bevan_calliess

	 * @exception Exception

	 * @version v0.20

	 */

	private void buildScenario4(){

		if(buildScenarioWARNING()){

			try {

				sim.addPC("pc1", true);

				sim.addPC("pc2", true);

				sim.addPC("pc3", true);

				sim.addPC("pc4", true);

				sim.addHub("hub1", true);				

				

				/*sim.setIPAddress("pc1","eth0","192.168.0.1");

				sim.setIPAddress("pc2","eth0","192.168.0.2");

				sim.setIPAddress("pc3","eth0","192.168.0.3");

				sim.setIPAddress("pc4","eth0","192.168.0.4");
				*/
				

															

				sim.addEthernetLink("link1", "pc1", "eth0", "hub1", "eth0");

				sim.addEthernetLink("link2", "pc2" , "eth0", "hub1", "eth1");

				sim.addEthernetLink("link3", "pc3" , "eth0", "hub1", "eth2");

				sim.addEthernetLink("link4", "pc4", "eth0", "hub1", "eth3");

				

				isDirty = true;

				System.out.println("Test Scenario 4 created\n");	

			} catch (Exception e) {

				System.out.println("This should never happed");

				e.printStackTrace();

			}								 

		}

		 

	}

	/**

	 * This scenario connects 3 pc's to a hub1, hub1 is connected to router1.

	 * router1 is connected to hub2 and hub2 is connected to another 3 pc's.

	 * @author angela_brown

	 * @exception Exception

	 * @version v0.20

	 */

	private void buildScenario5()

	{

	//build scenario 5

	//3 pcs - hub - router - hub - 3pcs

		if(buildScenarioWARNING()){

	    try {

			/*sim.addPC("pc1", true);

			sim.addPC("pc2", true);

			sim.addPC("pc3", true);

			sim.addHub("hub1", true);

			sim.addRouter("router1", true);

			sim.addHub("hub2", true);

			sim.addPC("pc4", true);

			sim.addPC("pc5", true);

			sim.addPC("pc6", true);

			

			sim.setIPAddress("pc1","eth0","192.168.0.2");

			sim.setDefaultGateway("pc1","192.168.0.1");

			sim.setIPAddress("pc2","eth0","192.168.0.3");

			sim.setDefaultGateway("pc2","192.168.0.1");

			sim.setIPAddress("pc3","eth0","192.168.0.4");

			sim.setDefaultGateway("pc3", "192.168.0.1");

			sim.setIPAddress("pc4","eth0","10.0.0.2");

			sim.setDefaultGateway("pc4", "10.0.0.1");

			sim.setIPAddress("pc5","eth0","10.0.0.3");

			sim.setDefaultGateway("pc5","10.0.0.1");

			sim.setIPAddress("pc6","eth0","10.0.0.4");

			sim.setDefaultGateway("pc6", "10.0.0.1");

			sim.setIPAddress("router1","eth0", "192.168.0.1");

			sim.setIPAddress("router1","eth1", "10.0.0.1");

			sim.setDefaultGateway("router1", "192.168.0.1");

														

			sim.addEthernetLink("link1", "pc1", "eth0", "hub1", "eth0");

			sim.addEthernetLink("link2", "pc2" , "eth0", "hub1", "eth1");

			sim.addEthernetLink("link3", "pc3" , "eth0", "hub1", "eth2");

			sim.addEthernetLink("link4", "hub1", "eth3", "router1", "eth0");

			sim.addEthernetLink("link5", "router1", "eth1", "hub2", "eth0");

			sim.addEthernetLink("link6", "hub2", "eth1", "pc4", "eth0");

			sim.addEthernetLink("link7", "hub2", "eth2", "pc5", "eth0");

			sim.addEthernetLink("link8", "hub2", "eth3", "pc6", "eth0");

			*/

			isDirty = true;

			System.out.println("Test Scenario 5 created\n");	

		} catch (Exception e) {

			System.out.println("This should never happed");

			e.printStackTrace();

		}

		}

	}

	

	private void displayRecordedInfo(){

		Vector info = sim.getRecordedInfo();

		if(info.size()==0){

			System.out.println("No entries to display.");			

		}else{		

			System.out.println("****************************************************************************************************************");

			System.out.println("The following is a record of the last Transactions movements.");

			System.out.println("****************************************************************************************************************");

			Iterator it = info.iterator();

			while(it.hasNext()){

				String recording[] = (String[])it.next();

				System.out.println(pad(recording[1],15,' ')+pad(recording[2],25,' ')+pad(recording[3],15,' ')+pad(recording[4],10,' ')+recording[5]);

			}

			System.out.println("****************************************************************************************************************");

			sim.clearLayerInfo();

		}

	}

	

	/**

	 * This method is used to create a fixed length string

	 * If the string is already greater than the length no action is taken

	 * and the original string is returned.

	 * @param inString The string that you want padded to the fixed length.

	 * @param length	The length you would like to string to be.

	 * @param pad		The character you would like to use to pad the String. 

	 *

	 */

	private String pad(String inString, int length, char pad) {

		if(inString !=null){

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

	 * This method will clean out the current simulation object, after asking the user

	 * if they are sure of what they are doing. In a effect creating a new simulation workspace.

	 * @author luke_hamilton 

	 *

	 */

	private void newsim(){

		if(isDirty){

			try {

				System.out.println("WARNING. This will delete all object within the current simulation");

				System.out.print("Are you sure you want to continue(y/N)?");

				response = userInterface.readLine();					

			} catch (IOException e) {

				System.out.println("IO Exception encountered.");

			}			

			

			if(response.equalsIgnoreCase("Y")){

				sim.removeAllObjects();

				System.out.println("New Simulation created\n");	

				isDirty = false;

			}						

		}else{

			System.out.println();

		}

	}

	

		

}//EOF

	

	

	
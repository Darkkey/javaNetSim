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



package core;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import core.OSPF.OSPF;
import core.protocolsuite.tcp_ip.ProtocolStack;



/**

 * A Node represents a device or a machine on a network. For example a PC, Hub, Switch or Router.

 *

 * <P>Note: This class may well need to be majorly modified. In order to implement Layer-2 Nodes such as

 * hubs and switches. Or alternatively a separate class be created for hub and switch to inherit from.

 * Currently the PC and Router nodes both inherit directly from this class. There is no DatalinkNode or

 * NetworkNode like in our class diagrams. The ideal way would probably be to shift all layer-2 associated

 * methods and attributes to a DatalinkNode class and all methods and attributes above layer-2 to be moved

 * to a NetworkNode class. Or something similar.</P>

 *

 * @author tristan_veness (Original Author) 

 * @author luke_hamilton 

 * @author angela_brown

 * @author bevan_calliess

 * @author michael_reith

 * @author robert_hulford

 * @since 13 June 2004

 * @version v0.20

 */



public abstract class Node implements Serializable{

	/**

	 * @link aggregation <{core.NetworkInterface}>

	 * @directed directed

	 * @supplierCardinality 0..*

	 */
	
		protected java.util.Hashtable NetworkInterfacetable = null;		//Hashtable of network interfaces

	public ProtocolStack NodeProtocolStack;
	
	public ProtocolStack getProtocolStack(){
		return NodeProtocolStack;
	}

        protected String name;
        
        public boolean On;
        
        public String location = "";
        
        protected int ProtocolStackLayers;

    
        class app_timer_task{
            core.TimerApp app;
            int interval;
            long lastcall;
            Node n;
            
            public app_timer_task(Node inN, core.TimerApp inApp, int inInterval){
            	lastcall = System.currentTimeMillis();
                interval = inInterval;
                app = inApp;
                n = inN;
            }
            
            public void call(){
                try{
                    if(n.On){
                        if(System.currentTimeMillis() >= (lastcall+interval) ){
                            lastcall = System.currentTimeMillis();
                            app.Timer(0);                        
                        }                    
                    }
                }catch(Exception e){
                    
                }
            }
            
        }
        
        public class AppTasks extends TimerTask                
        {
                private java.util.Hashtable timerTable;
                                
                public AppTasks(java.util.Hashtable inTimerTable)
                {
                    this.timerTable=inTimerTable;
                    
                }
                                    
              
                @Override
				public void run() 
                {
                    java.util.Enumeration e = timerTable.keys();
                    
                    for(;e.hasMoreElements();){
                        Long l  = (Long)e.nextElement();
                        ((app_timer_task)timerTable.get(l)).call();
                    }
                }
       }
        
        private java.util.Hashtable timerTable = new Hashtable();
        Timer appTimer = null;
        
        public void startTimerTask(core.TimerApp app, int interval){
            timerTable.put(Long.valueOf(app.getUID()), new app_timer_task(this, app, interval));
        }
        
        public void cancelTimerTask(core.TimerApp app){
            if(timerTable.get(app.getUID())!=null){
                timerTable.remove(app.getUID());
            }
        }
        
        public void cancelTimerTask(long UID){
            if(timerTable.get(UID)!=null){
                timerTable.remove(UID);
            }
        }

        public void stopTimers(){
            if(appTimer!=null){
                appTimer.cancel();
            }
            appTimer = null;
        }

    /**

     * Constructs an object of type Node. This can only be called by the Node's

     * respective sub classes like PC and Router. This constructor is only to be called

     * by the Node's sub classes using <b>super</b>.

     * @author luke_hamilton

     * @author

     * @param inName The name to give the Node.

     * @version v0.20  

     */

    protected Node(String inName, int inProtocolStackLayers, boolean inOn) {

            NetworkInterfacetable = new Hashtable();        	        	

            name=inName;

            ProtocolStackLayers = inProtocolStackLayers;
        
            init(inProtocolStackLayers);
            
            this.On = inOn;
            
            appTimer=new Timer();
            appTimer.schedule(new AppTasks(timerTable),500,500);
	}    

        protected void init(int inProtocolStackLayers){
            On = true;
            timerTable.clear();
            if(inProtocolStackLayers != 1){
                //This test the current simulation object for the type of protocol that is being used and then
                //Creates a protocolstack of that type.
                if(Simulation.getProtocolType() == core.ProtocolStack.TCP_IP){
                    NodeProtocolStack = new core.protocolsuite.tcp_ip.ProtocolStack(this,inProtocolStackLayers);
                    startTimerTask(NodeProtocolStack.OSPF(), OSPF.TIMEOUT);
                }else if(Simulation.getProtocolType() == core.ProtocolStack.APPLETALK){
                //NodeProtocolStack = new core.protocolsuite.appletalk.ProtocolStack(this);		//Not implemented
                }else if(Simulation.getProtocolType() == core.ProtocolStack.IBM_SNA){
                //NodeProtocolStack = new core.protocolsuite.ibm_sna.ProtocolStack(this);		//Not implemented
                }else if(Simulation.getProtocolType() == core.ProtocolStack.IPX_SPX){
                //NodeProtocolStack = new core.protocolsuite.ipx_spx.ProtocolStack(this);		//Not implemented
            }	
            }else{
                NodeProtocolStack = null;	//Set layer 1 devices protocolstack to null
            }
        }
        
    public String getAllData(){
    	return "";
    }
    
    public void loadDataFile(String name, int lines, String Data){
    	return;
    }

   	/**

	 * This method loops throught the NetworkInface hashtable and 

	 * then removes all connected links

	 * @author luke_hamilton

	 * @version v0.20

	 */

	protected void removeAllLinks() {

		Enumeration keys = NetworkInterfacetable.keys();

		while(keys.hasMoreElements()){

			String str = (String)keys.nextElement();

			NetworkInterface temp = (NetworkInterface)NetworkInterfacetable.get(str);

			temp.removeConnectedLink();

		}		

	}

	/**

	 * This method is called by a netinterface card connect to this node. 

	 * The packet will then in passed to the protocolstack

	 * @author bevan_calliess

	 * @author angela_brown

	 * @param inPacket - A Packet

	 * @version v0.20

	 */

	protected void receivePacket(Packet inPacket) throws LowLinkException
        {
		return;

	}

	

	/**

	 * This method is called by a network interface card connect to this node.

	 * It is an alternative signature to be used when the Interface 

	 * receiving the packet needs to be known.  eg hubs and switches.

	 * @author bevan_calliess

	 * @author angela_brown

	 * @param inPacket - A Packet

	 * @version v0.20

	 */

	protected void receivePacket(Packet inPacket,String inInterfaceName) throws LowLinkException {}



    /**

    * Creates a new network interface and adds it to the Node's NetworkInterfacetable(hashtable)

	* @author bevan_calliess

	* @param interfaceName - The name to give the new NetworkInterface. eg: eth0 

	* @version v0.20

	* */    
	
	public void addNetworkInterface(String interfaceName, int type, boolean active) {
		addNetworkInterface(interfaceName, type, active, 0);
	}

    public void addNetworkInterface(String interfaceName, int type, boolean active, int code) {

        switch(type){
            case NetworkInterface.Ethernet10T:
                if(active){
                    NetworkInterfacetable.put(interfaceName,new EthernetNetworkInterface(core.Simulation.UIDGen++,interfaceName,this));
                }else{
                    NetworkInterfacetable.put(interfaceName,new NetworkInterfacePort(core.Simulation.UIDGen++, interfaceName,this));
                }
                break;    
            case NetworkInterface.Ethernet100FX:
                if(active){
                    NetworkInterfacetable.put(interfaceName,new FiberEthernetNetworkInterface(core.Simulation.UIDGen++,interfaceName,this));
                }else{
                    NetworkInterfacetable.put(interfaceName,new FiberNetworkInterfacePort(core.Simulation.UIDGen++,interfaceName,this));
                }
                break;     
            case NetworkInterface.Wireless:
            	if(active){
            		NetworkInterfacetable.put(interfaceName,new WiFiInterface(core.Simulation.UIDGen++,interfaceName,this));
            	}else{
            		NetworkInterfacetable.put(interfaceName,new WiFiPort(core.Simulation.UIDGen++,interfaceName,this));
            	}
                break;
            case NetworkInterface.Console:
                NetworkInterfacetable.put(interfaceName,new ConsoleNetworkInterface(core.Simulation.UIDGen++,interfaceName,this));
                break;
            case NetworkInterface.WAN:
                NetworkInterfacetable.put(interfaceName,new WANNetworkInterface(core.Simulation.UIDGen++,interfaceName,this));
                break;    
            case NetworkInterface.Serial:
                NetworkInterfacetable.put(interfaceName,new SerialNetworkInterface(core.Simulation.UIDGen++,interfaceName,this));
                break;                   
        }
    }



    /**

    * Deletes a network interface from the interface list.

    * @author bevan_calliess

    * @param inNetworkInterfaceName  - The name of the NetworkInterface to remove from the Node. eg: eth0

    * @version v0.20

    */

    protected void removeNetworkInterface(String interfaceName) {

		NetworkInterfacetable.remove(interfaceName);

    }

    

    /**

     * Gets the name of the Node.

     * @author luke_hamilton

     * @return The name of the Node.

     * @version v0.20

     */

    public String getName() {

        return name;

    }

    

    public int getState() { return 0; }

    public void Reset() {
        turnOff();
        turnOn();
    }
    
    public void turnOn() {
        init(ProtocolStackLayers);
        
        //ifacesUP();
        
    }
    
    public void ifacesUP(){
    	
		Enumeration keys = NetworkInterfacetable.keys();

		while(keys.hasMoreElements()){

			String str = (String)keys.nextElement();

			NetworkInterface temp = (NetworkInterface)NetworkInterfacetable.get(str);

			if(temp.isActive() && temp.isUP() == false)
				temp.UP();
			else
				temp.setUP();
		}
    }
    
    public void ifacesLinkUP(){
    	
		Enumeration keys = NetworkInterfacetable.keys();

		while(keys.hasMoreElements()){

			String str = (String)keys.nextElement();

			NetworkInterface temp = (NetworkInterface)NetworkInterfacetable.get(str);

			if(!temp.isActive())
				temp.setUP();
			
		}
    }
    
    protected void ifacesDOWN(){
    	Enumeration keys = NetworkInterfacetable.keys();

		while(keys.hasMoreElements()){

			String str = (String)keys.nextElement();

			NetworkInterface temp = (NetworkInterface)NetworkInterfacetable.get(str);

			if(temp.isActive()) temp.DOWN();
		}
    }
    
    public void turnOff() {
    	ifacesDOWN();

    	On = false;
    	timerTable.clear();
        NodeProtocolStack = null;
    }

    

    /**

     * Displays all the interfaces in the network interfaces hashtable

     * @author luke_hamilton

     * @version v0.20

     */

    protected void displayInterfaces() {

    	NetworkInterface NIC;

    	String str;

    	

		Enumeration keys = NetworkInterfacetable.keys();

    	while(keys.hasMoreElements()){

    		str = (String) keys.nextElement();

    		NIC = (NetworkInterface)NetworkInterfacetable.get(str);

    		System.out.print(NIC.getDetails());    

    		if(this.getClass() != Hub.class){

    			String strIPAddress = NodeProtocolStack.getIPAddress(str);

    			if(strIPAddress == null)

    			{

    				strIPAddress = "Not set";

    			}

				System.out.println("\tIP Address: "+ strIPAddress);	

    		}else 

    			System.out.println();  		    	

    	}

     }
    
    public Object[] getAllInterfaces(){
		
		ArrayList<String> interfaceArray = new ArrayList<String>();
		Enumeration keys = NetworkInterfacetable.keys();
		while(keys.hasMoreElements()){
			String str = (String) keys.nextElement();
			NetworkInterface x = (NetworkInterface)NetworkInterfacetable.get(str);			
			interfaceArray.add(x.getName());
		}
		
		return interfaceArray.toArray();	
	}
    
    public Object[] getActiveInterfaces(){
		
		ArrayList<String> interfaceArray = new ArrayList<String>();
		Enumeration keys = NetworkInterfacetable.keys();
		while(keys.hasMoreElements()){
			String str = (String) keys.nextElement();
			NetworkInterface x = (NetworkInterface)NetworkInterfacetable.get(str);
			if(x.isActive())
				interfaceArray.add(x.getName());
		}
		
		return interfaceArray.toArray();	
	}
    
	public void execCmd(String cmd){ 
		System.out.println("WARNING: Cannot execute " + cmd + " for " + getName() + ". This device not support configuration management.");
	}

          

     /**

      * This method returns the network interface object. 

      * This is called when a link is created. It test to see if the passed interfaceName

      * is valid, if not it will throw a exception 

      * @author luke_hamilton

      * @param interfaceName

      * @throws InvalidNetworkInterfaceNameException

      * @return object refences

      * @version v0.20

      */

     public NetworkInterface getNetworkInterface(String interfaceName) throws InvalidNetworkInterfaceNameException{

     	if(NetworkInterfacetable.containsKey(interfaceName)){

			return (NetworkInterface)NetworkInterfacetable.get(interfaceName);	

     	}

     		throw new InvalidNetworkInterfaceNameException("'"+interfaceName+ "' on node '"+ name +"' is an invalid Network Interface name");	     	

     }

     

     /**

      * This method returns the MAC address of the the interface 

      * The method uses a TCP/IP method from the Ethernet interface 

      * so if you are replacing the Protocol stack with another type

      * you will need to change this method.

      * @author luke_hamilton 

      * @author bevan_calliess

      * @param inInterfaceName

      * @return	String The MAC address of the Interface.

      * @throws InvalidNetworkInterfaceNameException

      * @version v0.20

      */

     public String getMACAddress(String inInterfaceName)throws InvalidNetworkInterfaceNameException{

     	if(NetworkInterfacetable.containsKey(inInterfaceName)){

                //"Not Applicable"
                if(NetworkInterfacetable.get(inInterfaceName) instanceof EthernetNetworkInterface){
                	EthernetNetworkInterface temp =(EthernetNetworkInterface)NetworkInterfacetable.get(inInterfaceName);	
                	return temp.getMACAddress();                	
                }else{
                    return "Not Applicable";
                }

     	}

     		throw new InvalidNetworkInterfaceNameException("'"+ inInterfaceName + "' on node '"+ name +"' is an invalid Network Interface name");	     	

     }     
     
     public int getIntType(String inInterfaceName)throws InvalidNetworkInterfaceNameException{

     	if(NetworkInterfacetable.containsKey(inInterfaceName)){

     		NetworkInterface temp =(NetworkInterface)NetworkInterfacetable.get(inInterfaceName);	

     		return temp.getType();

     	}

     		throw new InvalidNetworkInterfaceNameException("'"+ inInterfaceName + "' on node '"+ name +"' is an invalid Network Interface name");	     	

     }       
     
    public boolean isActiveInterface(String inInterfaceName)throws InvalidNetworkInterfaceNameException{

     	if(NetworkInterfacetable.containsKey(inInterfaceName)){

     		NetworkInterface temp =(NetworkInterface)NetworkInterfacetable.get(inInterfaceName);	

     		return temp.isActive();

     	}

     		throw new InvalidNetworkInterfaceNameException("'"+ inInterfaceName + "' on node '"+ name +"' is an invalid Network Interface name");	     	

     }       


     
     public String getIntSType(String inInterfaceName)throws InvalidNetworkInterfaceNameException{

     	if(NetworkInterfacetable.containsKey(inInterfaceName)){

     		NetworkInterface temp =(NetworkInterface)NetworkInterfacetable.get(inInterfaceName);	

     		switch(temp.getType()){
                    case NetworkInterface.Ethernet10T:
                        return "Copper Ethernet";
                    case NetworkInterface.Console:
                        return "Console";    
                    case NetworkInterface.Wireless:
                        return "Wireless";
                    case NetworkInterface.Ethernet100FX:
                        return "Fiber Ethernet";                        
                    case NetworkInterface.Serial:
                        return "Serial";                                                
                    default:
                        return "Unknown";
                }

     	}

     	throw new InvalidNetworkInterfaceNameException("'"+ inInterfaceName + "' on node '"+ name +"' is an invalid Network Interface name");	     	

     }  
     
     public NetworkInterface getNIC(String inInterfaceName)throws InvalidNetworkInterfaceNameException{
        if(NetworkInterfacetable.containsKey(inInterfaceName)){
            return (NetworkInterface)NetworkInterfacetable.get(inInterfaceName);
        }else{
            throw new InvalidNetworkInterfaceNameException("'"+ inInterfaceName + "' on node '"+ name +"' is an invalid Network Interface name");	     	
        }
     }
             
     
    /* public void setMACAddress(String inInterfaceName, String MAC)throws InvalidNetworkInterfaceNameException, SimulationException{

     	if(NetworkInterfacetable.containsKey(inInterfaceName)){

     		EthernetNetworkInterface temp =(EthernetNetworkInterface)NetworkInterfacetable.get(inInterfaceName);	

                if(MAC.matches("[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]:[0-9A-F][0-9A-F]")){                    
                    temp.setMacAddress(MAC);
                }else{
                    throw new SimulationException("Invalid MAC address!");	     	
                }

     	}else{
            throw new InvalidNetworkInterfaceNameException("'"+ inInterfaceName + "' on node '"+ name +"' is an invalid Network Interface name");	     	
        }
     }    */
     

     /**

      * This method will iterate through all of this nodes interfaces

      * and add the name of any that do not have a link connected

      * to a String Array.  This list is used in the gui to display a 

      * list of available interfaces for the user to select from when creating

      * a link.

      * @return	String[]  List of Interface names without a link.

      */

     public String[] getAvailableInterfaces()

 	{

 		int counter = 0;

 		Enumeration it = NetworkInterfacetable.elements();

 		Enumeration it2 = NetworkInterfacetable.elements();

 		while(it.hasMoreElements())

 		{

 			NetworkInterface temp = (NetworkInterface)it.nextElement();			

 			if(temp.getConnectedLink() == null){

 				counter++;

 			}

      	} 

 		String strAvailInterfaces[] = new String[counter];

 		int iterator = 0;

 		while(it2.hasMoreElements())

 		{

 			NetworkInterface temp = (NetworkInterface)it2.nextElement();			

 			if(temp.getConnectedLink() == null){

 				strAvailInterfaces[iterator] = temp.getName();

 				iterator++;

 			}

      	} 		

 		return strAvailInterfaces;

 	}

     

     public ArrayList<String> getAllInterfacesNames(){

		

		ArrayList<String> interfaceArray = new ArrayList<String>();

		Enumeration<String> keys = NetworkInterfacetable.keys();

		while(keys.hasMoreElements()){

			String strInterfaceName = keys.nextElement();			

			interfaceArray.add(strInterfaceName);

		}

		

		return interfaceArray;		

	} 

    /**
    * Returns 1st interface name in the network interfaces hashtable
    * @author gift (sourceforge.net user)
    * @return 
    * @version v0.20
    */

         public String getFirstInterfaceName()
         {			
		Enumeration keys = NetworkInterfacetable.keys();

		while(keys.hasMoreElements())
                {
			String strInterfaceName = (String) keys.nextElement();			
			
                        if(((NetworkInterface)NetworkInterfacetable.get(strInterfaceName)).isActive()){
                            return strInterfaceName;
                        }
                }
                
                return null;
	} 
         
    /**
     * Gets the name of the Node.
     * @author luke_hamilton
     * @return The name of the Node.
     * @version v0.20
     */
    public void setName(String inName) {
        name=inName;
    }
    
    public boolean setClock(String date){
        //set new date
        return true;
    }
    
    public String getClock(){
        return "00:00:00 1 1 1970";
    }
   
}//EOF


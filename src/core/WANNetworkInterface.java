/* 
Java Network Simulator (javaNetSim) 

Copyright (c) 2007, 2006, 2005, Ice Team;  All rights reserved.
Copyright (c) 2004, jFirewallSim development team;  All rights reserved. 
 
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
import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import core.protocolsuite.tcp_ip.ICMP_packet;
import core.protocolsuite.tcp_ip.IP_packet;
import core.protocolsuite.tcp_ip.TCP_packet;
import core.protocolsuite.tcp_ip.UDP_packet;

public class WANNetworkInterface extends NetworkInterface{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -391650798313777242L;
	public final static int NotSet = 0;
    public final static int SocketTCP = 1;
    public final static int SocketUDP = 2;
    public final static int RMI = 3;
    public final static int Corba = 4;
    
    protected int type;
   
    protected boolean server; // for socket connections
    protected String Host;
    protected int port; 
    protected String Service;
    
    protected boolean connected;
    
    protected WANRMIServer RMIServer;
    protected WANRMI RMIClient;
    protected WANRMICallback RMICallback;
    protected WANRMIClient RMIClientCallback;
    
    protected WANCorbaServer CORBAServer;
    protected WANCorbaClient CORBAClient;
    
    protected WANSocket s;
    protected WANDatagram d;
    
    /* Interface properties setup functions */
    
    public void setServer(boolean inServer){
        server = inServer;
    }
   
    public boolean getServer(){
        return server;
    }
    
    public int getConnType(){
        return type;
    }
    
    public void setConnType(int inType){
        type = inType;
    }    
    
    public int getConnPort(){
        return port;
    }
    
    public void setConnPort(int inPort){
        port = inPort;
    }
        
    public String getConnHost(){
        return Host;
    }
    
    public void setConnHost(String inHost){
        Host = inHost;
    }
    
    public String getConnService(){
        return Service;
    }
    
    public void setConnService(String inService){
        Service = inService;
    }
    
    public void setRMICallback(WANRMICallback callback){
        this.RMICallback = callback;
    }
    
    /* /Interface properties setup functions */
    
    /* Standart javaNetSim interface functions */
    
    @Override
	public void UP(){        
        up = listen();
        if(!server){
            up &= connect();
        }
        super.UP();
    }
        
    @Override
	public void DOWN(){
        super.DOWN();
        close();        
    }
    
    @Override
	public int getType(){
        return NetworkInterface.WAN;
    }
    
    @Override
	public boolean isActive(){
        return true;
    }
	 	 
	
    @Override
	protected String getDetails(){
        return "Interface: "+name;
    }	
    
    /* /Standart javaNetSim interface functions */
    
    public WANNetworkInterface(long UID, String inName, Node parent) {                
        super(UID, inName, parent);
        type = 0;
        connected = false;
        Service = "";
        Host = "";
        RMICallback = null;
        if (System.getSecurityManager() == null)
                    System.setSecurityManager ( new RMISecurityManager() );
        
        //temp debug values
        up = false;
    }
    
    // forwards packet to device stack
    @Override
	public void receivePacket(Packet inPacket) throws LowLinkException {
        switch(type){
            case RMI:      
                {
                    if(!connected){
                        connect();
                    }                    
                }    
                break;
            case Corba:      
                {
                    if(!connected){
                        connect();
                    }                    
                }    
                break;                
            case SocketTCP:
            case SocketUDP:
                //Nothing to do...
                break;
        }
        
        addLayerInfo("WAN Packet","Recieved and accepted packet at interface " + name);       
        
        parentNode.receivePacket(inPacket, name);
    }
    
    // decode recieved packet to Packet class
     public void receivePacket(String inPacket) throws LowLinkException{
        System.out.println(inPacket);
        
        String[] packets = inPacket.split("#");
        
        char ptype = packets[packets.length - 1].charAt(0);
        
        System.out.println(ptype);
        
        try{
            switch(ptype){
                case 'M':
                    ICMP_packet icmp = new ICMP_packet("");
                    icmp.fromBytes(inPacket);
                    receivePacket(icmp);
                    break;
                case 'I':
                    IP_packet ip = new IP_packet("");
                    ip.fromBytes(inPacket);
                    receivePacket(ip);
                    break;
                case 'T':
                    TCP_packet tcp = new TCP_packet("","",0,0);
                    tcp.fromBytes(inPacket);
                    receivePacket(tcp);
                    break;
                case 'U':
                    UDP_packet udp = new UDP_packet("","",0,0);
                    udp.fromBytes(inPacket);
                    receivePacket(udp);
                    break;
            }
        }catch(Exception e){
            //nothing to do....
        }
    }   
     
    // sending packet through WAN interface
    @Override
	protected void sendPacket(Packet inPacket) throws LowLinkException {
          if(!connected){
               connect();
          }
          addLayerInfo("WAN Packet", "Sending packet from interface " + name);
          
          if(connected){          
                try{
                     switch(type){
                         case RMI:    
                            if(server)
                                RMICallback.recievePacket(inPacket.toBytes());
                            else
                                RMIClient.recievePacket(inPacket.toBytes());
                            break;
                         case SocketUDP:
                            d.sendPacket(inPacket.toBytes());
                            break;
                         case SocketTCP:
                            s.sendPacket(inPacket.toBytes());
                            break; 
                         case Corba:                            
                            CORBAClient.sendPacket(inPacket.toBytes());
                            break;
                    }
                }catch(Exception e){
                    addLayerInfo("WAN Connection", "Error: "  + e.toString());
                    e.printStackTrace();
                }
          }
    }

    // this is the same as upper function, MAC is ignored here
    protected void sendPacket(Packet inPacket, String inMacAddress) throws CommunicationException, LowLinkException {
        sendPacket(inPacket);        
    }

    // adding layerInfo    
    public void addLayerInfo(String DataType, String Msg){
         LayerInfo pingInfo = new LayerInfo(getClass().getName());
         pingInfo.setObjectName(parentNode.getName());
         pingInfo.setDataType(DataType);
         pingInfo.setLayer("Link");
         pingInfo.setDescription(Msg);
         Simulation.addLayerInfo(pingInfo);
    }
    

    // starts listening on transport
    protected boolean listen(){        
        try{
            switch(type){
                case RMI:      
                {
                    RMIServer = new WANRMIServer(this);                      
                    Naming.bind (name, RMIServer);
                    return true;                    
                }   
                
                case Corba:
                {
                    CORBAServer = new WANCorbaServer(this, Host, port, name);
                    return true;
                }
                
                case SocketUDP:
                    if(server){
                        d = new WANDatagram(this, Host, port, server);
                        return d.listen();                    
                    }else{
                        return false;
                    }
                case SocketTCP:
                    if(server){
                        s = new WANSocket(this, Host, port, server);
                        return s.listen();                    
                    }else{
                        return false;
                    }
            }
        }catch(Exception e){
            addLayerInfo("Wan interface", "Unknown error during connect: " + e.toString());
            e.printStackTrace();
        }
        return false;
    }
    
      
    // starts connecting through transport
    public boolean connect(){
        try{
            switch(type){
                case RMI:
                {
                    if(Host == "" || Service == ""){
                        //Error
                        return false;
                    }
                    RMIClient = null;
                    RMIClient = (WANRMI) Naming.lookup
			("rmi://" + Host + ":" + port + "/" + Service);
                    RMIClientCallback = null;
                    RMIClientCallback = new WANRMIClient(this);                                      
                    RMIClient.setServiceName( RMIClientCallback );
                    
                    connected = true;
                    
                    return true;
                }
                case Corba:
                {
                    if(Service == ""){
                        //Error
                        return false;
                    }
                    CORBAClient = new WANCorbaClient(this);
                    CORBAClient.Connect(Host, port, Service);
                    CORBAClient.setServiceName(name);
                    
                    connected = true;
                    
                    return true;
                }
                case SocketTCP:
                    if(!server){
                        s = new WANSocket(this, Host, port, server);
                        connected = s.connect();                    
                        return connected;
                    }else{
                        return false;
                    }
                case SocketUDP:
                    if(!server){
                        d = new WANDatagram(this, Host, port, server);
                        connected = d.connect();                    
                        return connected;
                    }else{
                        return false;
                    }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    
    // closing transport
    protected void close(){
        try{
            connected = false;
            switch(type){
                case RMI:               
                    RMIClientCallback = null;
                    RMICallback = null;
                    RMIClient = null;
                    Naming.unbind(name);            
                    RMIServer = null;                      
                    break;
                case Corba:
                    if(CORBAServer != null){
                        CORBAServer.close();
                        CORBAServer = null;
                    }
                    if(CORBAClient != null){
                        CORBAClient.close();
                        CORBAClient = null;
                    }                    
                    break;
                case SocketUDP:
                    if(d!= null) d.close();
                    d = null;
                    break;                    
                case SocketTCP:
                    if(s!= null) s.close();
                    s = null;
                    break;
            }          
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
	public void Timer(int temp){ }
}

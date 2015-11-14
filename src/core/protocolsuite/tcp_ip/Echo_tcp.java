/*
 * Echo.java
 *
 * Created on 19 Nov 2005 Ç., 14:09
 *
 */

package core.protocolsuite.tcp_ip;
import java.util.Vector;

import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LowLinkException;
import core.TransportLayerException;

/**
 *
 * @author key
 * @author gift (sourceforge.net user)
 */
public class Echo_tcp extends Application{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2881575973178293668L;
	//private byte ConnectionAttempts=1;
    private int counts = 1;
    private String data = "";
    //private int crecv;
    private long utc1;
    public int recieved;
    
    private Vector<Integer> connections = new Vector<Integer>(0);
    
    /** Creates a new instance of Echo */
    public Echo_tcp(ProtocolStack inParentStack, int listenPort, int appType, long UID) {
        super(inParentStack, listenPort, appType, UID);
        counts = 0;
        appSock = mParentStack.SL().socket(jnSocket.TCP_socket, this);
    }
        
    @Override
	public void Timer(int code){ }
    
    /**
    * This method start to listen on application port
    * @author key
    * @version v0.01
    */
    @Override
	public void Listen() throws TransportLayerException{    
        recieved = 0;
        try{
            if(appType == 1){
                mParentStack.SL().bind(appSock, mParentStack.getSrcIP(), listenPort);
                mParentStack.SL().listen(appSock);
                printLayerInfo("Echo TCP server starts listening in port " + listenPort + ".");
            }
        }catch (TransportLayerException e){
        	printLayerInfo("Error: cannot bind port " + listenPort + ".");
        	throw new TransportLayerException("Cannot bind port " + listenPort + "."); 
        }
    }
    
    @Override
	public void Accept(int listenSock, int sessionSock){
        connections.add(new Integer(sessionSock));
        //System.out.println("Accepted socket="+sessionSock+" for listen socket="+listenSock);
    }
    
    /**
    * This method stop listening on application port
    * @author key
    * @param Protocol Stack 
    * @return Nothing.
    * @version v0.01
    */
    @Override
	public void Close() throws TransportLayerException
    {              
        if(appType == 0){
            printLayerInfo("Echo application", "Echo TCP server closed socket."); 
        }else{
            printLayerInfo("Echo application", "Echo TCP server closed socket.");
        }
        mParentStack.SL().close(appSock);
        for(int i=0; i<connections.size(); i++){
            mParentStack.SL().close(connections.get(i).intValue());
        }
        recieved = 0;
    }
    
    @Override
	public void Free() throws TransportLayerException{
        if(appType == 0){
            printLayerInfo("Echo application", "Echo TCP application freed socket.");
        }else{
            printLayerInfo("Echo application", "Echo TCP server freed socket.");
        }
        mParentStack.SL().free(appSock);
        for(int i=0; i<connections.size(); i++){
            mParentStack.SL().free(connections.get(i).intValue());
        }
    }
    
    /**
    * This method connects to server on the other side (imaginations from the other side.... :+)
    * @author key
    * @author gift (sourceforge.net user)
    * @param Host - hostname or ip of server.
    * @param port - server's port
    * @version v0.03
    */
        
    @Override
	public boolean Connect(String Host, int port) throws TransportLayerException, InvalidNetworkLayerDeviceException, CommunicationException, LowLinkException 
    {
        printLayerInfo("Connecting to host " + Host + ":"+ port +". Please wait...");
            
        boolean isconnected  = mParentStack.SL().connect(appSock, Host, port);
            
        return isconnected;
    }   

    
    /**
    * This method should be called from UDP when client connects to server
    * @author key
    * @version v0.01
    */
    @Override
	public void OnConnect(int sock){
        System.out.println("ECHO_TCP: Connected "+sock+" !");
        printLayerInfo("Connected "+sock+" !");
        try{
            if(appType == 0){
                printLayerInfo("Start sending echo message '" + data + "' to " + mParentStack.SL().get_socket(sock).dst_IP + ":" + mParentStack.SL().get_socket(sock).dst_port);
                this.counts--;
                SendData(data);
                //mParentStack.TCP().disconnect(sock);
            }
        }catch(LowLinkException e){
            System.out.println(e.toString());
        }catch(CommunicationException e){
            System.out.println(e.toString());
        }catch(TransportLayerException e){
            System.out.println(e.toString());
        }
    }

    /**
    * This method is called when client disconnected from server.
    * @author key
    * @version v0.01
    */

    @Override
	public void OnDisconnect(int sock) { 
        System.out.println("ECHO_TCP: Disconnected "+sock+" !");
        printLayerInfo("Disconnected");
        try{
            if(appType == 1){
                connections.remove(new Integer(sock));
                //Listen();
            }
        }catch(Exception e){
            printLayerInfo("Cannot listen on port: " + e.toString());
        }
    }
    
    @Override
	public void OnError(int sock, int error){
        switch(error){
            case -1: {
                printLayerInfo("Error: can not connect to " + mParentStack.SL().get_socket(sock).dst_IP + ":" + mParentStack.SL().get_socket(sock).dst_port);
                break;
            }
            case -2 :{
                OnDisconnect(sock);
                break;
            }
        }
    }
    
    /**
    * This method sends data to the other side.
    * @param data to send
    * @author key
    * @version v0.01
    */

    @Override
	public void SendData(int sock, String Data) throws LowLinkException, TransportLayerException, CommunicationException
    {
        mParentStack.SL().write(sock, Data);
        //processing the protocol doings.
    }
    
    
    
    /**
    * This method recieves data from the other side.
    * @param data to recv
    * @author key
    * @author gift (sourceforge.net user)
    * @version v0.02
    */
    @Override
	public void RecvData(int sock, String Data) throws LowLinkException, TransportLayerException {
        //processing the protocol doings.
        recieved++;
        
        if(appType == 0){
            printLayerInfo("Recieving echo message '" + Data + "' from server.");
            
            /*
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("Echo Protocol Data");
            protInfo.setLayer("Application ");
            protInfo.setDescription("Recieving echo message '" + Data + "' from server.");
            Simulation.addLayerInfo(protInfo);*/

            if(this.counts==0){
                printLayerInfo("Connection time: " + (System.currentTimeMillis () - utc1) + " ms. Total recieved messages: " + recieved + ".");
                try{
                    mParentStack.SL().disconnect(sock);
                }catch(CommunicationException e){
                    System.out.println(e.toString());
                } 
            }else{
                this.counts--;
                try {
                    printLayerInfo("Sending echo message '" + Data + "' to server...");
                    SendData(Data);                
                }catch(LowLinkException e){
                    System.out.println(e.toString());
                }catch(TransportLayerException e){
                    System.out.println(e.toString());
                }catch(CommunicationException e){
                    System.out.println(e.toString());
                }    
            }
            
        }else{
            //server processing recieve
            
            printLayerInfo("Recieving echo message '" + Data + "' from client. Total recieved messages: " + recieved + ".");
            
            printLayerInfo("Sending echo message '" + Data + "' to client.");
          
            try { 
                SendData(sock, Data);
            }catch(CommunicationException e){
                System.out.println(e.toString());
            }
                                                 
        }
    }
    
    /**
    * This method processes a echo sending by client
    * @param data to send
    * @param server host
    * @param server port
    * @author key
    * @author gift (sourceforge.net user)
    * @version v0.02
    */
    public void SendEcho(String Data, String Host, int port, int counts) throws CommunicationException, LowLinkException, InvalidNetworkLayerDeviceException, TransportLayerException{
        recieved = 0;
        utc1 = System.currentTimeMillis ();
        this.counts = counts;
        this.data = Data;
        
        if (Connect(Host, port)){
        }
        else{
            printLayerInfo("Error: can not connect to " + Host + ":" + port + "!");
        }
    }

    protected void printLayerInfo(String s) {
        super.printLayerInfo("Echo Protocol Data", s);
    }
    

}

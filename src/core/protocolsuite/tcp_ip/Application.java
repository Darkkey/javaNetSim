/*
 * ApplicationProtocol.java
 *
 * Created on 19 Nov 2005 Ç., 13:53
 */

package core.protocolsuite.tcp_ip;
import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LayerInfo;
import core.LowLinkException;
import core.Simulation;
import core.TransportLayerException;

/**
 *
 * @author key
 */
public abstract class Application extends core.TimerApp{
    
    protected String protocolName;
    protected int listenPort;
    protected int clientPort;
    protected ProtocolStack mParentStack;
 
    protected int appType;
    protected int appSock;    
    
    /** Creates a new instance of ApplicationProtocol */
    
    public Application(ProtocolStack inParentStack, int listenPort, int appType, long UID){       
        super(UID);
        this.listenPort = listenPort;
        this.mParentStack = inParentStack;
        this.appType = appType;             
    }
    
    /**
    * This method start to listen on application port
    * @author key
    * @version v0.01
    */
    public abstract void Listen() throws TransportLayerException;
    
    public abstract void Accept(int listenSock, int sessionSock);
    
    /**
    * This method stop listening on application port
    * @author key
    * @param Protocol Stack 
    * @return Nothing.
    * @version v0.01
    */
    public abstract void Close() throws TransportLayerException;

    public abstract void Free() throws TransportLayerException;
    
    /**
    * This method connects to server on the other side (imaginations from the other side.... :+)
    * @author key
    * @param Host - hostname or ip of server.
    * @param port - server's port
    * @version v0.01
    */
    
    public abstract boolean Connect(String Host, int port) throws TransportLayerException, InvalidNetworkLayerDeviceException, CommunicationException, LowLinkException;

    /**
    * This method is called when client disconnected from server.
    * @author key
    * @version v0.01
    */

    public abstract void OnDisconnect(int sock);
    
    public abstract void OnConnect(int sock);
    
    public abstract void OnError(int sock, int error);
    
    /**
    * This method sends data to the other side.
    * @param data to send
    * @author key
    * @version v0.01
    */

    public abstract void SendData(int sock, String Data) throws LowLinkException, TransportLayerException, CommunicationException;  
    
    public void SendData(int sock, String host, int port, String Data) throws LowLinkException, TransportLayerException, CommunicationException { 
       try{
        if(Connect(host, port)){
            SendData(sock, Data);
        }
       }catch(InvalidNetworkLayerDeviceException e){ throw new CommunicationException(e.toString()); }
    }
    
    public void SendData(String Data) throws LowLinkException, TransportLayerException, CommunicationException
    {
        if(Data.length() > 0)
            SendData(appSock, Data);
    }
    
    /**
    * This method recieves data from the other side.
    * @param data to recv
    * @author key
    * @version v0.01
    */
    public abstract void RecvData(int sock, String Data) throws LowLinkException, TransportLayerException;
    

    public void setPort(int port){
        this.listenPort = port;
    }
    
    public int getPort(){
        return this.listenPort;
    }
    
    protected void printLayerInfo(String DataType, String s) {
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType(DataType);
            protInfo.setLayer("Application ");
            protInfo.setDescription(s);
            Simulation.addLayerInfo(protInfo);            
    }
    
}

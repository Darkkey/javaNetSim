/*
 * Echo.java
 *
 * Created on 19 Nov 2005 �., 14:09
 *
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
public class Echo extends Application{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -8739785460456648633L;
	long utc1;
    public int recieved;
    
    /** Creates a new instance of Echo */
    public Echo(ProtocolStack inParentStack, int listenPort, int appType, long UID) {
        super(inParentStack, listenPort, appType, UID);
        appSock = mParentStack.SL().socket(jnSocket.UDP_socket, this);
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
        //      
        try{            
            mParentStack.SL().bind(appSock, mParentStack.getSrcIP(), listenPort);
            mParentStack.SL().listen(appSock);
            printLayerInfo("Echo app", "Echo server starts listening in port " + listenPort + ".");
        } catch (Exception e)
        {
            printLayerInfo("Echo app", "Error: cannot bind port " + listenPort + ".");
            throw new TransportLayerException("Cannot bind port " + listenPort + "."); 
        }
    }
    
    
    @Override
	public void Accept(int listenSock, int sessionSock){
        
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
            printLayerInfo("Echo application", "Echo application closed socket."); 
        }else{
            printLayerInfo("Echo application", "Echo server closed socket.");
        }
        mParentStack.SL().close(appSock);
    }
    
    @Override
	public void Free() throws TransportLayerException
    {
        if(appType == 0){
            printLayerInfo("Echo application", "Echo application freed socket.");
        }else{
            printLayerInfo("Echo application", "Echo server freed socket.");
        }
        mParentStack.SL().free(appSock);
    }
    
    /**
    * This method connects to server on the other side (imaginations from the other side.... :+)
    * This is "fake" method cos of UDP
    * @author key
    * @param Host - hostname or ip of server.
    * @param port - server's port
    * @version v0.01
    */
        
    @Override
	public boolean Connect(String Host, int port) throws TransportLayerException, InvalidNetworkLayerDeviceException, CommunicationException, LowLinkException {      
        //clientPort = mParentStack.SL().reserveUDPPort(this, sdHost, sdPort);
        //if (clientPort>0) return true; else return false;        
        return mParentStack.SL().connect(appSock, Host, port);
    }   

    
    /**
    * This method should be called from UDP when client connects to server
    * @author key
    * @version v0.01
    */
    @Override
	public void OnConnect(int sock){
    }
    
    /**
    * This method is called when client disconnected from server.
    * @author key
    * @version v0.01
    */

    @Override
	public void OnDisconnect(int sock){
        
    }
    
    @Override
	public void OnError(int sock, int error){
        
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
        String sdHost = mParentStack.SL().get_socket(sock).dst_IP;
        int sdPort = mParentStack.SL().get_socket(sock).dst_port;
        mParentStack.SL().writeTo(sock, Data, sdHost, sdPort);
        
        //processing the protocol doings.
    }
    
   
    /**
    * This method recieves data from the other side.
    * @param data to recv
    * @author key
    * @version v0.01
    */
    @Override
	public void RecvData(int sock, String Data) throws LowLinkException, TransportLayerException {
        recieved++;
        //processing the protocol doings.
        if(appType == 0){
            //client processing recieve
            // printing some ...
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("Echo Protocol Data");
            protInfo.setLayer("Application ");
            protInfo.setDescription("Recieving echo message '" + Data + "' from server.");
            Simulation.addLayerInfo(protInfo);

            //mParentStack.UDP().closePort(sock);

        }else{
            //server processing recieve
          try{
            String sdHost = mParentStack.SL().get_socket(sock).dst_IP;
            int sdPort = mParentStack.SL().get_socket(sock).dst_port;
            
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("Echo Protocol Data");
            protInfo.setLayer("Application ");
            protInfo.setDescription("Recieving echo message '" + Data + "' from client " + sdHost + ":" + sdPort + ". Total recieved messages by server: " + recieved);
            Simulation.addLayerInfo(protInfo);
            
            LayerInfo protInfo2 = new LayerInfo(getClass().getName());
            protInfo2.setObjectName(mParentStack.getParentNodeName());
            protInfo2.setDataType("Echo Protocol Data");
            protInfo2.setLayer("Application ");
            protInfo2.setDescription("Sending echo message '" + Data + "' to client.");
            Simulation.addLayerInfo(protInfo2);
            
            SendData(Data);
            
            /*LayerInfo protInfo3 = new LayerInfo(getClass().getName());
            protInfo3.setObjectName(mParentStack.getParentNodeName());
            protInfo3.setDataType("Echo Protocol Data");
            protInfo3.setLayer("Application ");
            protInfo3.setDescription("Server closing connection. Now listening on " + listenPort + ".");
            Simulation.addLayerInfo(protInfo3);*/
            
            //Close();
            //Listen();
          }catch(Exception e){
            System.out.println(e.toString());
            ///*TODO*: here to catch
          }
        }
    }
    
    /**
    * This method processes a echo sending by client
    * @param data to send
    * @param server host
    * @param server port
    * @author key
    * @version v0.01
    */
    public void SendEcho(String Data, String Host, int port, int counts) throws CommunicationException, LowLinkException, InvalidNetworkLayerDeviceException, TransportLayerException{
        //mParentStack.FreeUDPApplication(this);

        utc1 = System.currentTimeMillis();
        recieved = 0;
        
        for(int c=0; c<counts; c++){
            if (Connect(Host, port))
            {
                LayerInfo protInfo = new LayerInfo(getClass().getName());
                protInfo.setObjectName(mParentStack.getParentNodeName());
                protInfo.setDataType("Echo Protocol Data");
                protInfo.setLayer("Application ");
                protInfo.setDescription("Start sending echo message '" + Data + "' to " + Host + ":" + port);
                Simulation.addLayerInfo(protInfo);

                SendData(Data);    
                //try{
                //Thread.sleep(5);
                //}catch(Exception e){}
            } else {
                LayerInfo protInfo = new LayerInfo(getClass().getName());
                protInfo.setObjectName(mParentStack.getParentNodeName());
                protInfo.setDataType("Echo Protocol Data");
                protInfo.setLayer("Application ");
                protInfo.setDescription("Error: can not connect to " + Host + ":" + port + "!");
                Simulation.addLayerInfo(protInfo);
                c=counts;
            }   
        }
        LayerInfo protInfo2 = new LayerInfo(getClass().getName());
        protInfo2.setObjectName(mParentStack.getParentNodeName());
        protInfo2.setDataType("Echo Protocol Data");
        protInfo2.setLayer("Application ");
        protInfo2.setDescription("Connection time: " + (System.currentTimeMillis () - utc1) + " ms. Sent messages: " + counts + " Recieved messages: " + recieved);
        Simulation.addLayerInfo(protInfo2);
    }
}

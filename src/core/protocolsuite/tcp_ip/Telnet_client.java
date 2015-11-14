/*
 * Telnet_client.java
 *
 * Created on 19 Nov 2005 Ç., 14:09
 *
 */

package core.protocolsuite.tcp_ip;
import guiUI.TelnetEmulator;
import core.ApplicationLayerDevice;
import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LowLinkException;
import core.TransportLayerException;

/**
 *
 * @author key
 * @author gift (sourceforge.net user)
 */
public class Telnet_client extends Application{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5670018907094576728L;
	private TelnetEmulator terminal;
    /** Creates a new instance of Telnet Server */
    public Telnet_client(ApplicationLayerDevice dev, ProtocolStack inParentStack, int listenPort, int appType, long UID) {
        super(inParentStack, listenPort, appType, UID);
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
        throw new TransportLayerException("Cannot bind port " + listenPort + ".");               
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
        mParentStack.SL().close(appSock);
    }
    
    @Override
	public void Free() throws TransportLayerException
    {
        mParentStack.SL().free(appSock);
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
    
    public boolean Disconnect() throws LowLinkException, CommunicationException, TransportLayerException
    {
        return mParentStack.SL().disconnect(appSock);
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
        terminal.recvData("Connection refused");
        terminal.close();
        printLayerInfo("Client: disconnected");
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
            printLayerInfo("Client: Recieving message '" + Data + "' from server.");
            terminal.recvData(Data);
//        }
    }
    
    /**
    * This method processes a telnet sending by client
    * @param data to send
    * @param server host
    * @param server port
    * @author key
    * @author gift (sourceforge.net user)
    * @version v0.02
    */
    public void TelnetConnect(TelnetEmulator emul, String Host, int port) throws CommunicationException, LowLinkException, InvalidNetworkLayerDeviceException, TransportLayerException{

        this.terminal = emul;
        
        if (Connect(Host, port))
        {
            //SendData("\r\n");
            
        } else
        {
            printLayerInfo("Client: error: can not connect to " + Host + ":" + port + "!");
            terminal.recvData("Client: error: cannot connect to " + Host + ":" + port + "!");
        }
    }
    
    public void printLayerInfo(String s) {
        super.printLayerInfo("Telnet Protocol Data", s);
    }

}

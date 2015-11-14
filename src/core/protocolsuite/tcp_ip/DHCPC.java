/*
 * DHCPC.java
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
public class DHCPC extends Application{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6247754123840765620L;
	public boolean running;
    public boolean completed;
        
    String iface;

	String DHCPServer;
    int xid;
    int leaseTime;
    
    String resend;
    int resendings;
    
    
    /** Creates a new instance of DHCPC */
    public DHCPC(ProtocolStack inParentStack, long UID) {
        super(inParentStack, 0,0, UID);
        appSock = mParentStack.SL().socket(jnSocket.UDP_socket, this);
        running = false;
        completed = false;
        resend = "";
        resendings = 0;
    }
    
    @Override
	public void Timer(int code){ 
        if(running){
            if(!completed){
                if(resend != "" && resendings++ < 5){
                    LayerInfo protInfo = new LayerInfo(getClass().getName());
                    protInfo.setObjectName(mParentStack.getParentNodeName());
                    protInfo.setDataType("DHCP client");
                    protInfo.setLayer("Application ");
                    protInfo.setDescription("Resening DHCP packet to 255.255.255.255 due to timer.");
                    Simulation.addLayerInfo(protInfo);
                    try{
                        SendData(appSock, "255.255.255.255", 67, resend);
                    }catch(Exception e){}
                }
            }else{
                //re-lease
            }
        }
    }
        
    /**
    * This method start to listen on application port
    * @author key
    * @version v0.01
    */
    @Override
	public void Listen() throws TransportLayerException{
        //      
        try{            
            mParentStack.SL().bind(appSock, mParentStack.getSrcIP(), 68);
            mParentStack.SL().listen(appSock);
            printLayerInfo("DHCP Client", "DHCP Client binded port " + 68 + ".");
        } catch (Exception e)
        {
            printLayerInfo("DHCP Client", "Error: cannot bind port " + 68 + ".");
            throw new TransportLayerException("Cannot bind port " + 68 + "."); 
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
        running = false;
        printLayerInfo("DHCP Client", "DHCP Client application closed socket.");
        mParentStack.SL().close(appSock);
    }
    
    @Override
	public void Free() throws TransportLayerException
    {              
        mParentStack.getParentNode().cancelTimerTask(this);
        printLayerInfo("DHCP Client", "DHCP Client application freed socket.");
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
    
    @Override
	public void SendData(int sock, String host, int port, String Data) throws LowLinkException, TransportLayerException, CommunicationException { 
        mParentStack.SL().writeTo(sock, Data, host, port);                
    }
    
    /**
    * This method recieves data from the other side.
    * @param data to recv
    * @author key
    * @version v0.01
    */
    @Override
	public void RecvData(int sock, String Data) throws LowLinkException, TransportLayerException {
        DHCPPacket p = new DHCPPacket();
        
        if(!running) return;
        
        p.fromBytes(Data);

        if(p.op == 2 && p.xid == xid){
            switch(p.msgType){
                case 2: //OFFER
                    if(!completed){
                        
                        LayerInfo protInfo = new LayerInfo(getClass().getName());
                        protInfo.setObjectName(mParentStack.getParentNodeName());
                        protInfo.setDataType("DHCP client");
                        protInfo.setLayer("Application ");
                        protInfo.setDescription("Recieved DHCPOFFER(xid=" + p.xid + ") from " + p.DHCPServer + " with op=2 yiaddr='" + p.yiaddr +
                                "' lease time=" + p.leaseTime + ".");
                        Simulation.addLayerInfo(protInfo);
                        
                        DHCPPacket r = new DHCPPacket();
                        r.op = 1;
                        r.xid = xid;
                        r.msgType = 3;
                        DHCPServer = r.DHCPServer = p.DHCPServer;
                        r.WantedIP = p.yiaddr;
                        r.chaddr = mParentStack.getMACAddress(iface);
                        
                        protInfo = new LayerInfo(getClass().getName());
                        protInfo.setObjectName(mParentStack.getParentNodeName());
                        protInfo.setDataType("DHCP client");
                        protInfo.setLayer("Application ");
                        protInfo.setDescription("Sending DHCPREQUEST(xid=" + p.xid + ") packet to 255.255.255.255 with op=1 chaddr='" +
                            r.chaddr + "' DHCP Server='" + r.DHCPServer + "' preferred IP='" + r.WantedIP + "'.");
                        Simulation.addLayerInfo(protInfo);
                        
                        resend = r.toBytes();
                        
                        try{
                            SendData(appSock, "255.255.255.255", 67, resend);
                        }catch(CommunicationException e){
                            throw new TransportLayerException(e.toString());
                        }
                    }
                    break;
                case 4: // DECLINE
                    // sleeping
                    break;
                case 5: // ACK
                    if(!completed){
                        // here set up interfaces...
                        LayerInfo protInfo = new LayerInfo(getClass().getName());
                        protInfo.setObjectName(mParentStack.getParentNodeName());
                        protInfo.setDataType("DHCP client");
                        protInfo.setLayer("Application ");
                        protInfo.setDescription("Recieved DHCPACK(xid=" + p.xid + ") from " + p.DHCPServer + " with op=2 yiaddr='" + p.yiaddr +
                                "' lease time=" + p.leaseTime + " subnet mask='" + p.SubnetMask + "' default gateway='" + p.Gateway + "'.");
                        Simulation.addLayerInfo(protInfo);
                                    
                        protInfo = new LayerInfo(getClass().getName());
                        protInfo.setObjectName(mParentStack.getParentNodeName());
                        protInfo.setDataType("DHCP client");
                        protInfo.setLayer("Application ");
                        protInfo.setDescription("Updating " + iface + " IP configuration.");
                        Simulation.addLayerInfo(protInfo);
                        
                        resend = "";
                        resendings = 0;
                        
                        try{
                            mParentStack.setIPAddress(iface, p.yiaddr );
                            mParentStack.setCustomSubnetMask(iface,p.SubnetMask );
                            mParentStack.setDefaultGateway(p.Gateway);
                        }catch(Exception e){}

                        leaseTime = p.leaseTime;
                        DHCPServer = p.DHCPServer;
                        
                        completed = true;
                    }
                    break;
            }
        }else{
            //Invalid or not our packet; simply drop
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("DHCP client");
            protInfo.setLayer("Application ");
            protInfo.setDescription("Recieved DHCP packet with invalid xid=" + p.xid + " and/or data.");
            Simulation.addLayerInfo(protInfo);
        }
    }
    
    /**
    * This method processes a DHCPC sending by client
    * @param data to send
    * @param server host
    * @param server port
    * @author key
    * @version v0.01
    */
    public void StartDHCPC(String inIface, String WantedIP) throws CommunicationException, LowLinkException, InvalidNetworkLayerDeviceException, TransportLayerException{
        if(running){
            Close();
        }
        
        running = true;
        completed = false;
        iface = inIface;
        
        Listen();
        
        DHCPPacket d = new DHCPPacket();
        d.op = 1;
        xid = d.xid;
        d.WantedIP = WantedIP;
        d.ciaddr = mParentStack.getIPAddress(iface);
        d.chaddr = mParentStack.getMACAddress(iface);
        d.msgType = 1;
        
        if(d.ciaddr == null){
            d.ciaddr = "";
        }
        
        String Data =  d.toBytes();
        
        resend = Data;
        resendings = 0;
        
        LayerInfo protInfo = new LayerInfo(getClass().getName());
        protInfo.setObjectName(mParentStack.getParentNodeName());
        protInfo.setDataType("DHCP client");
        protInfo.setLayer("Application ");
        protInfo.setDescription("Starting DHCP discover process on interface " + iface + "... Transaction ID is " + xid + ".");
        Simulation.addLayerInfo(protInfo);
               
        protInfo = new LayerInfo(getClass().getName());
        protInfo.setObjectName(mParentStack.getParentNodeName());
        protInfo.setDataType("DHCP client");
        protInfo.setLayer("Application ");
        protInfo.setDescription("Sending DHCPDISCOVER packet to 255.255.255.255 with op=1 chaddr='" +
                d.chaddr + "' ciaddr='" + d.ciaddr + "' preferred IP='" + WantedIP + "'.");
        Simulation.addLayerInfo(protInfo);
        
        mParentStack.getParentNode().startTimerTask(this, 5000);
        
        SendData(appSock, "255.255.255.255", 67, Data);
    }

    public String getInterface() {
		return iface;
	}
    
    public String getDHCPServer() {
		return DHCPServer;
	}
}

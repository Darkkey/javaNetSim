/*
 * DHCPD.java
 */

package core.protocolsuite.tcp_ip;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LayerInfo;
import core.LowLinkException;
import core.Pair;
import core.Simulation;
import core.TransportLayerException;

/**
 *
 * @author key
 */
public class DHCPD extends Application{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3558382935460040966L;

	public class pool{
        public String MAC="";
        
        public String IP="";        
        public String Genmask="";
        
        public String Gateway="";
    }
    
    public pool new_pool(){
        return new pool();
    }
    
    public class lease{
        public String IP;
        public String Genmask;
        public String Gateway;
        
        public String MAC;
                
        public int xid;
                
        public long negotiation_started; 
                
        public boolean completed;
        
        public int leaseTime;
        public long leased;    
        
        public lease(){
            negotiation_started = 0;
            xid = 0;
            completed = false;            
        }
    }
    
    public boolean running = false;
    public Hashtable<String, pool> pools = new Hashtable<String, pool>();
    public Hashtable<String, lease> leases = new Hashtable<String, lease>();
    private Vector<Pair> exclude = new Vector<Pair>(0);
    
    /** Creates a new instance of DHCPD */
    public DHCPD(ProtocolStack inParentStack, long UID) {
        super(inParentStack, 0, 0, UID);
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
            mParentStack.SL().bind(appSock, mParentStack.getSrcIP(), 67);
            mParentStack.SL().listen(appSock);
            running = true;
            printLayerInfo("DHCP server", "DHCP server starts listening in port " + 67 + ".");
        } catch (Exception e)
        {
            printLayerInfo("DHCP server", "Error: cannot bind port " + 67 + ".");
            throw new TransportLayerException("Cannot bind port " + 67 + "."); 
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
        printLayerInfo("DHCP server", "DHCPD application closed socket.");
        mParentStack.SL().close(appSock);
        running = false;
    }
    
    @Override
	public void Free() throws TransportLayerException
    {              
        printLayerInfo("DHCP server", "DHCPD application freed socket.");
        mParentStack.SL().free(appSock);
        running = false;
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
        return false;
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
    }
    
    @Override
	public void SendData(int sock, String host, int port, String Data) throws LowLinkException, TransportLayerException, CommunicationException { 
        mParentStack.SL().writeTo(sock, Data, host, port);        
    }
    
    String findLease(String MAC){
        String IP;
                
        for(Enumeration<String> e = leases.keys(); e.hasMoreElements();){
            IP = e.nextElement();
            if((leases.get(IP)).MAC.equals(MAC)){
                return IP;
            }
        }
        
        return "";
    }
    
    lease advertLease(String MAC){
        lease l = new lease();
        
        l.IP = "";
               
        String IP;
        String Network, PoolNetwork;
        String PoolName;
        
        for(Enumeration<String> e = pools.keys(); e.hasMoreElements();){
            PoolName =  e.nextElement();
            pool p = pools.get(PoolName);
            
            try{  
                byte b[] = java.net.InetAddress.getByName(p.IP).getAddress();
                
                boolean done = false;
                
                while(!done){
                    if(b[3] < 254){
                        b[3]++;
                    }else if(b[2] < 254){
                        b[2]++;
                    }else if(b[1] < 254){
                        b[1]++;
                    }else if(b[0] < 254){
                        b[0]++;
                    }else{
                        break;
                    }
                    
                    IP = java.net.InetAddress.getByAddress(b).getHostAddress();
                    Network = IPV4Address.toDecimalString(IPV4Address.IPandMask(IPV4Address.toBinaryString(IP), IPV4Address.toBinaryString(p.Genmask)));
                    PoolNetwork = IPV4Address.toDecimalString(IPV4Address.IPandMask(IPV4Address.toBinaryString(p.IP), IPV4Address.toBinaryString(p.Genmask)));
                    
                    if(Network.equals(PoolNetwork)){
                        if(!leases.containsKey(IP) && !isExcludes(IP)){
                            l.IP = IP;
                            l.Genmask = p.Genmask;
                            l.Gateway = p.Gateway;
                            l.MAC = MAC;
                            return l;
                        }
                    }else{
                        break;
                    }
                }
                
            }catch(Exception e2){}
        }
        
        return l;
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
        String IP;
        
        p.fromBytes(Data);
        
        if(p.op == 1){
            switch(p.msgType){
                case 1:
                {
                    LayerInfo protInfo = new LayerInfo(getClass().getName());
                    protInfo.setObjectName(mParentStack.getParentNodeName());
                    protInfo.setDataType("DHCP Server");
                    protInfo.setLayer("Application ");
                    protInfo.setDescription("Recieved DHCPDISCOVER(xid=" + p.xid + ") from " + p.chaddr + " with op=1.");
                    Simulation.addLayerInfo(protInfo);
                    
                    IP = findLease(p.chaddr);
                    
                    if(IP!=""){
                    	leases.remove(p.chaddr);
                    }
                    
                    lease l = advertLease(p.chaddr);
                        
                        if(l.IP!=""){
                            l.xid = p.xid;
                            l.negotiation_started = (System.currentTimeMillis()/1000);

                            leases.put(l.IP, l);

                            DHCPPacket o = new DHCPPacket();

                            o.chaddr = p.chaddr;
                            o.msgType = 2;
                            o.DHCPServer = mParentStack.SL().get_socket(appSock).src_IP;
                            o.leaseTime = 3600;
                            o.yiaddr = l.IP;
                            o.op = 2;
                            l.xid = o.xid = p.xid;
                            o.SubnetMask = l.Genmask;
                            o.Gateway = l.Gateway;
                            
                            LayerInfo protInfo1 = new LayerInfo(getClass().getName());                            
                            protInfo1.setObjectName(mParentStack.getParentNodeName());
                            protInfo1.setDataType("DHCP Server");
                            protInfo1.setLayer("Application ");
                            protInfo1.setDescription("Sending DHCPOFFER(xid=" + o.xid + ") packet to 255.255.255.255 with op=1 chaddr='" +
                            o.chaddr + "' DHCP Server='" + o.DHCPServer + "' yiaddr='" + o.yiaddr + "'.");
                            Simulation.addLayerInfo(protInfo);

                            try{
                                SendData(appSock, "255.255.255.255", 68, o.toBytes());
                            }catch(CommunicationException e){
                                throw new TransportLayerException(e.toString());
                            }
                        }

                    
                     /*else if(check if leased yet ){
                     
                     }else{
                        
                     }
                    */
                }
                    break;
                case 3:
                    IP = findLease(p.chaddr);
                    
                    LayerInfo protInfo = new LayerInfo(getClass().getName());
                    protInfo.setObjectName(mParentStack.getParentNodeName());
                    protInfo.setDataType("DHCP Server");
                    protInfo.setLayer("Application ");
                    protInfo.setDescription("Recieved DHCPREQUEST(xid=" + p.xid + ") from " + p.chaddr + " with op=1 and preferred IP='" + p.WantedIP + "'.");
                    Simulation.addLayerInfo(protInfo);
                    
                    if(IP!=""){
                        lease l = leases.get(IP);
                        
                        if(l.xid == p.xid){
                            l.leased = (System.currentTimeMillis()/1000);
                            l.negotiation_started = 0;
                            l.completed = true;
                            
                            DHCPPacket o = new DHCPPacket();

                            o.chaddr = p.chaddr;
                            o.msgType = 5;
                            o.DHCPServer = mParentStack.SL().get_socket(appSock).src_IP;
                            o.leaseTime = 3600;
                            o.yiaddr = l.IP;
                            o.xid = l.xid;
                            o.op = 2;
                            o.SubnetMask = l.Genmask;
                            o.Gateway = l.Gateway;
                            o.WantedIP = "";                            
                            
                            protInfo = new LayerInfo(getClass().getName());
                            protInfo.setObjectName(mParentStack.getParentNodeName());
                            protInfo.setDataType("DHCP Server");
                            protInfo.setLayer("Application ");
                            protInfo.setDescription("Sending DHCPACK(xid=" + o.xid + ") packet to 255.255.255.255 with op=1 chaddr='" +
                            o.chaddr + "' DHCP Server='" + o.DHCPServer + "' yiaddr='" + o.yiaddr + "'...");
                            
                            try{
                                SendData(appSock, "255.255.255.255", 68, o.toBytes());
                            }catch(CommunicationException e){
                                throw new TransportLayerException(e.toString());
                            }
                        }else{
                            protInfo = new LayerInfo(getClass().getName());
                            protInfo.setObjectName(mParentStack.getParentNodeName());
                            protInfo.setDataType("DHCP Server");
                            protInfo.setLayer("Application ");
                            protInfo.setDescription("Recieved DHCP packet with invalid xid=" + p.xid + ".");
                            Simulation.addLayerInfo(protInfo);
                        }
                    }
                    /*if(!findLease()){
                     
                     }else{
                     }*/
                    break;
            }
        }else{
            // drop it;
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("DHCP Server");
            protInfo.setLayer("Application ");
            protInfo.setDescription("Recieved DHCP packet with invalid data.");
            Simulation.addLayerInfo(protInfo);
        }
    }
    
    public void excludeAddress(String low_ip, String high_ip){
        boolean found = false;
        for(int i=0; i<exclude.size() && !found; i++){
            Pair el = exclude.get(i);
            if(el.getFirst().equals(low_ip) && el.getSecond().equals(high_ip)){
                found = true;
            }
        }
        if(!found){
            exclude.add(new Pair(low_ip, high_ip));
        }
    }
    
    public void no_excludeAddress(String low_ip, String high_ip){
        for(int i=0; i<exclude.size(); i++){
            Pair el = exclude.get(i);
            if(el.getFirst().equals(low_ip) && el.getSecond().equals(high_ip)){
                exclude.remove(i);
                break;
            }
        }
    }
    
    public Vector<Pair> getExcludeAddresses(){
        return exclude;
    }
    
    private boolean isExcludes(String ip){
    	boolean result = false;
    	for(int i=0; i<exclude.size() && !result; i++){
    		Pair ips = exclude.get(i);
    		result = IPV4Address.isBetween(ip, (String)ips.getFirst(), (String)ips.getSecond());
    	}
    	return result;
    }
    
}

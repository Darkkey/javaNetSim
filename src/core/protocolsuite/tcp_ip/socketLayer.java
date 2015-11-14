package core.protocolsuite.tcp_ip;

import java.util.Hashtable;
import java.util.Vector;

import core.ApplicationLayerDevice;
import core.CommunicationException;
import core.LowLinkException;
import core.TransportLayerException;
/**
 *
 * @author key
 */
public class socketLayer {
    ProtocolStack mParentStack;
    private Hashtable<Integer,jnSocket> sockTable = new Hashtable<Integer,jnSocket>();
    int lastSock;
    
    /** Creates a new instance of socketLayer */
    public socketLayer(ProtocolStack inParentStack) {
        mParentStack= inParentStack;
        sockTable.clear();
        lastSock = 0;
    }
    
    public int socket(short type, Application app){
        sockTable.put(lastSock,new jnSocket(lastSock, app, type));
        lastSock++;
        return (lastSock - 1);
    }    
    
    public jnSocket get_socket(int sock){
        return sockTable.get(sock);
    }
    
    public void bind(int sock, String srcIP, int srcPort) throws TransportLayerException{
        jnSocket jnsock = get_socket(sock);
        jnsock.src_IP = srcIP;
        //((jnSocket)sockTable.get(sock)).src_port = srcPort;  <-- WRONG!
        //jnsock.open_state = true; //call listen(sock) to open socket
        if(jnsock.type == jnSocket.UDP_socket){
            mParentStack.UDP().bindPort(sock, srcPort);
        }
        else if(jnsock.type == jnSocket.TCP_socket){
            mParentStack.TCP().bindPort(sock, srcPort);
        }
        //((jnSocket)sockTable.get(sock)).type
        // bind tcp or udp...
    }
    
    public void listen(int sock) throws TransportLayerException{
        jnSocket jnsock = get_socket(sock);
        //((jnSocket)sockTable.get(sock)).src_port = srcPort;  <-- WRONG!
        if(jnsock.type == jnSocket.UDP_socket){
            mParentStack.UDP().listen(sock);
        }
        else if(jnsock.type == jnSocket.TCP_socket){
            mParentStack.TCP().listen(sock);
        }
        jnsock.open_state = true; //bind or connect or listen
    }
    
    public boolean connect(int sock, String ipaddr, int port) throws LowLinkException, CommunicationException, TransportLayerException{
        boolean result = false;
        String dest = null;
        
		if(!IPV4Address.isValidIp(ipaddr)){
			Vector<String> addrs = ((ApplicationLayerDevice)mParentStack.getParentNode()).resolve(ipaddr);
			if(addrs.size()>0){
				dest = addrs.get(0);
			}
		}
		else{
			dest = ipaddr;
		}
        
		if(dest != null){
	        jnSocket jnsock = get_socket(sock);
	        if(jnsock.type == jnSocket.TCP_socket){
	            jnsock.open_state = true;
	            jnsock.dst_IP = dest;
	            jnsock.dst_port = port;
	            result = mParentStack.TCP().connect(sock, dest, port);
	        }
	        else if(jnsock.type == jnSocket.UDP_socket){
	            jnsock.dst_IP = dest;
	            jnsock.dst_port = port;
	            result = true;
	        }
		}
        return result;
    }
    
    public boolean disconnect(int sock) throws LowLinkException, CommunicationException, TransportLayerException{
        boolean result = false;
        jnSocket jnsock = get_socket(sock);
        if(jnsock.type == jnSocket.TCP_socket){
            result = mParentStack.TCP().disconnect(sock);
        }
        else if(jnsock.type == jnSocket.UDP_socket){
            close(sock);
            result = true;
        }
        return result;
    }
    
    public int accept(int sock, String dstIP, int dstPort){
        jnSocket jnsock = get_socket(sock);
        int new_sock = socket(jnsock.type, jnsock.app);
        jnSocket new_jnsock = get_socket(new_sock);
        new_jnsock.src_IP = jnsock.src_IP;
        new_jnsock.src_port = jnsock.src_port;
        new_jnsock.dst_IP = dstIP;
        new_jnsock.dst_port = dstPort;
        new_jnsock.open_state = true;
        new_jnsock.app.Accept(sock, new_sock);
        return new_sock;
    }
    
    public void write(int sock, String msg) throws TransportLayerException, LowLinkException, CommunicationException{
        mParentStack.TCP().sendTCPData(sock, msg);
    }
    
    public void writeTo(int sock, String data, String IP, int port) throws LowLinkException, TransportLayerException{
    	
        String dest = null;
        
		if(!IPV4Address.isValidIp(IP)){
			Vector<String> addrs = ((ApplicationLayerDevice)mParentStack.getParentNode()).resolve(IP);
			if(addrs.size()>0){
				dest = addrs.get(0);
			}
		}
		else{
			dest = IP;
		}
        
		if(dest != null){
	        (sockTable.get(sock)).dst_IP = dest;
	        (sockTable.get(sock)).dst_port = port;
	        (sockTable.get(sock)).open_state = true;
	        mParentStack.UDP().sendUDPPacket(sock, dest, port, data);
		}
    }
    
    public void recv(int sock, String data) throws LowLinkException, TransportLayerException{
        if(sock < lastSock){
            if((sockTable.get(sock)).open_state == true){
                (sockTable.get(sock)).app.RecvData(sock, data);
            }
        }
    }
    
    public void recvFrom(int sock, String IP, int port, String data) throws LowLinkException, TransportLayerException{
        if(sock < lastSock){
            if((sockTable.get(sock)).open_state == true){                
                //DEPRECATED IN SOON
                if((sockTable.get(sock)).type == jnSocket.UDP_socket){
                    (sockTable.get(sock)).dst_IP = IP;
                    (sockTable.get(sock)).dst_port = port;                    
                }
                //^^^^^^^^^^^^^^^^
                
                //DEPRECATED IN SOON
                //((jnSocket)sockTable.get(sock)).app.RecvIP(IP);
                //((jnSocket)sockTable.get(sock)).app.RecvPrt(port);
                //^^^^^^^^^^^^^^^^
                
                //((jnSocket)sockTable.get(sock)).app.OnConnect(sock);
                (sockTable.get(sock)).app.RecvData(sock, data);
            }
        }
    }
    
    public void close(int sock) throws TransportLayerException{
        jnSocket jnsock = sockTable.get(sock);
        //((jnSocket)sockTable.get(sock)).app = null;
        jnsock.open_state = false; 
        if(jnsock.type == jnSocket.UDP_socket){
            mParentStack.UDP().closePort(sock);
        }
        else if(jnsock.type == jnSocket.TCP_socket){
            mParentStack.TCP().closePort(sock);
        }
        //jnsock.src_port = 0;
        //jnsock.src_IP = "";
    }  
    public void free(int sock) throws TransportLayerException{
        jnSocket jnsock = sockTable.get(sock);
        jnsock.app = null;
        if(jnsock.open_state){
            if(jnsock.type == jnSocket.UDP_socket){
                mParentStack.UDP().closePort(sock);
            }
            else if(jnsock.type == jnSocket.TCP_socket){
                mParentStack.TCP().closePort(sock);
            }
            jnsock.open_state = false;
        }        
        jnsock.src_port = 0;
        jnsock.src_IP = "";
        sockTable.remove(sock);
    } 
}

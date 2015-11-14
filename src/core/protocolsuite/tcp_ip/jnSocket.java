/*
 * jnSocket.java
 *
 * Created on 14 Сентябрь 2007 г., 22:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core.protocolsuite.tcp_ip;

/**
 *
 * @author key
 */
public class jnSocket{
    public static final short RAW_socket = 0;
    public static final short UDP_socket = 1;
    public static final short TCP_socket = 2;
    
    public int number;
    public short type; // 0 -- raw, 1 -- udp, 2 -- tcp
    public boolean open_state; 
    
    public Application app;
    
    public String src_IP;
    public int src_port;
    
    public String dst_IP;
    public int dst_port;
    
    public jnSocket(int _number, Application _app, short _type){
        open_state = false;
        type = _type;
        app = _app;
        number = _number;
    }
    
    public String genKey(){
        String key;
        switch(type){
            case RAW_socket: key=src_IP+String.valueOf(src_port)+"_"+dst_IP+"_"+String.valueOf(dst_port); break;
            case UDP_socket: key=jnSocket.genUDPkey(src_port); break;
            case TCP_socket: key=jnSocket.genTCPkey(src_port, dst_IP, dst_port); break;
            default: key="";
        }
        return key;
    }
    
    static public String genTCPkey(int srcport, String dstIP, int dstport){
        return String.valueOf(srcport)+"_"+dstIP+"_"+String.valueOf(dstport);
    }
    
    static public String genUDPkey(int dstport){
        return String.valueOf(dstport);
    }
}


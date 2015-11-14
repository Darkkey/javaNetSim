/*
 * WANSocketServer.java
 *
 * Created on 14 ќкт€брь 2007 г., 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author key
 */
public class WANDatagram {
    WANNetworkInterface parentInterface;
    String Hostname;
    int port;
    DatagramSocket s;
    IncomeConnectionHandler ih;
    
    String clientHost;
    int clientPort;
    
    boolean server;
       
    void setClientAddress(String inClientHost, int inClientPort){
        clientHost = inClientHost;
        clientPort = inClientPort;
    }
            
    /** Creates a new instance of WANSocketServer */
    public WANDatagram(WANNetworkInterface inParentInterface, String inHostname, int inPort, boolean type) {
        parentInterface = inParentInterface;
        Hostname = inHostname;
        port = inPort;        
        server = type;
        ih = null;
    }
    
    public boolean listen(){
        try{
                if(server){
                    parentInterface.addLayerInfo("Wan interface", "Starting listening for peer...");
                    s =  new DatagramSocket(port); 
                }else{
                    s =  new DatagramSocket(); 
                    sendPacket("P!#");
                }
            
                ih = new IncomeConnectionHandler(s, this, parentInterface);
                ih.start();
                return true;            
        }catch(Exception e) {
            parentInterface.addLayerInfo("Wan interface", "Listen error: " + e.toString());
        }
        return false;
    }
    
    public boolean connect(){
        return listen();
    }
    
    public void close(){
        try{
            if(s!=null){
                s.close();
                s = null;
            }
            if(ih!=null){
                ih.interrupt();
                ih = null;
            }
        }catch(Exception e){}
        System.gc();
    }
    
    public void recievePacket(String inPacket) throws LowLinkException{
        parentInterface.receivePacket(inPacket);
    }   
    
    public void sendPacket(String inPacket){
        InetAddress address;
        DatagramPacket packet;
        String dstHost = "";
        int dstPort = 0;
        
        if(server){
             dstHost = clientHost;
             dstPort = clientPort;        
        }else{
             dstHost = Hostname;
             dstPort = port;        
        }
        
        try{
            address = InetAddress.getByName(dstHost);
            packet = new DatagramPacket(inPacket.getBytes(), inPacket.getBytes().length , address, dstPort);
        
            s.send(packet);
        }catch(java.net.UnknownHostException h){
            parentInterface.addLayerInfo("Wan interface", "Error during communication: " + h.toString());
        }catch(java.io.IOException i){
            parentInterface.addLayerInfo("Wan interface", "Error during communication: " + i.toString());
        }
    }   
    
    class IncomeConnectionHandler extends Thread {
        DatagramSocket s;
       // Node node;
        WANDatagram w;
        WANNetworkInterface parentInterface;
        boolean occupied;
        
        IncomeConnectionHandler(DatagramSocket s, WANDatagram w, WANNetworkInterface parentInterface) {
            this.s = s;
            //this.node = node;            
            this.w = w;
            this.parentInterface = parentInterface;
            occupied = false;
        }

        @Override
		public void run() { 
            try{
                parentInterface.addLayerInfo("Wan interface", "Starting listening for datagrams from peers.");
                parentInterface.connected = true;              
                
                while (true) {
                    byte[] buf = new byte[256];

                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    s.receive(packet);                    
                    
                    if((new String(packet.getData(),"ascii")).contains("P!#") && !occupied){
                        setClientAddress(packet.getAddress().getHostAddress(), packet.getPort());
                        occupied = true;
                    }
                    
                    w.recievePacket((new String(packet.getData(),"ascii")).substring(0, packet.getLength()));
                    
                }                                
            }catch(Exception e){
                parentInterface.addLayerInfo("Wan interface", "Error during communication: " + e.toString());
                e.printStackTrace();
            }
         }
     }
    
}

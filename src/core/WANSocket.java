/*
 * WANSocketServer.java
 *
 * Created on 14 ќкт€брь 2007 г., 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author key
 */
public class WANSocket {
    WANNetworkInterface parentInterface;
    String Hostname;
    int port;
    ServerSocket s;
    ClientConnectionHandler ch;
    IncomeConnectionHandler ih;
    
    Socket c;
    PrintWriter out;
    
    boolean server;
    
    void setClientSocket(Socket c, PrintWriter out){
        this.c = c;
        this.out = out;        
    }
            
    /** Creates a new instance of WANSocketServer */
    public WANSocket(WANNetworkInterface inParentInterface, String inHostname, int inPort, boolean type) {
        parentInterface = inParentInterface;
        Hostname = inHostname;
        port = inPort;
        server = type;
        ch = null;
        ih = null;
    }
    
    public boolean listen(){
        try{
            if(server){
                parentInterface.addLayerInfo("Wan interface", "Starting listening for peer...");
                s = new ServerSocket(port);
                ih = new IncomeConnectionHandler(s, this, parentInterface);
                ih.start();
                return true;
            }
        }catch(Exception e) {
            parentInterface.addLayerInfo("Wan interface", "Listen error: " + e.toString());
        }
        return false;
    }
    
    public boolean connect(){
        try{
            if(!server){         
                parentInterface.addLayerInfo("Wan interface", "Starting connecting to peer...");
                c = new Socket(Hostname, port);
                ch = new ClientConnectionHandler(c, this, parentInterface);
                ch.start();
                return true;
            }
        }catch(Exception e){
            parentInterface.addLayerInfo("Wan interface", "Connection error: " + e.toString());
        }  
        return false;
    }
    
    public void close(){
        try{
            if(s!=null){
                s.close();
            }
            if(c!=null){
                c.close();
            }
            if(ch!=null){
                ch.interrupt();
                ch = null;
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
        if(c != null){
            out.println(inPacket);
            out.flush();
        }
    }   
    
    class IncomeConnectionHandler extends Thread {
        ServerSocket s;
        //Node node;
        WANSocket w;
        WANNetworkInterface parentInterface;
        
        IncomeConnectionHandler(ServerSocket s, WANSocket w, WANNetworkInterface parentInterface) {
            this.s = s;
            //this.node = node;            
            this.w = w;
            this.parentInterface = parentInterface;
        }

        @Override
		public void run() { 
            try{
              while(s != null){
                Socket incoming = s.accept(); 
                parentInterface.addLayerInfo("Wan interface", "Accepted connection from peer.");
                parentInterface.connected = true;
                w.setClientSocket(incoming, new PrintWriter(new OutputStreamWriter(incoming.getOutputStream())));
                BufferedReader in = new BufferedReader( new InputStreamReader(incoming.getInputStream())); 
                
                while (true) {
                    String str = in.readLine(); 
                    if (str == null) {
                        break;
                    } else {
                        w.recievePacket(str);
                    }
                }
                incoming.close();
                incoming = null;
                w.setClientSocket(null, null);                
              }
            }catch(Exception e){
                parentInterface.addLayerInfo("Wan interface", "Error during communication: " + e.toString());
                e.printStackTrace();
            }
         }
     }
    
    class ClientConnectionHandler extends Thread {
        Socket s;
        //Node node;
        WANSocket w;
        
        WANNetworkInterface parentInterface;
    

        ClientConnectionHandler(Socket s, WANSocket w, WANNetworkInterface parentInterface) {
            this.s = s;
            //this.node = node;            
            this.w = w;
            this.parentInterface = parentInterface;
        }

        @Override
		public void run() { 
            try{
                parentInterface.addLayerInfo("Wan interface", "Connected to peer.");
                w.setClientSocket(s, new PrintWriter(new OutputStreamWriter(s.getOutputStream())));
                BufferedReader in = new BufferedReader( new InputStreamReader(s.getInputStream())); 
                
                while (true) {
                    String str = in.readLine(); 
                    if (str == null) {
                        break;
                    } else {
                        w.recievePacket(str);
                    }
                }
                s.close();
                s = null;
                w.setClientSocket(null, null);
            }catch(Exception e){
                parentInterface.addLayerInfo("Wan interface", "Error during communication: " + e.toString());
                e.printStackTrace();
            }
         }
     }
}

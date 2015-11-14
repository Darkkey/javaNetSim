/*
 * Echo.java
 *
 * Created on 19 Nov 2005 Ç., 14:09
 *
 */

package core.protocolsuite.tcp_ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import core.CommunicationException;
import core.Error;
import core.ExternalProxy;
import core.LayerInfo;
import core.LowLinkException;
import core.Simulation;
import core.TransportLayerException;

/**
 *
 * @author key
 * @author gift (sourceforge.net user)
 */
public class ExternalProxyApp extends Application{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -907806368539057280L;
	public int recieved;
    private ExternalProxy EN;
    private Socket NAT;
    private PrintStream out;
    
    /** Creates a new instance of Echo */
    public ExternalProxyApp(ProtocolStack inParentStack, ExternalProxy EN, Socket NAT, String externalIP, int listenPort, long UID) {
        super(inParentStack, listenPort, 1, UID);
        this.EN = EN;
        this.NAT = NAT;
        try {
            out =  new PrintStream(NAT.getOutputStream());
        } catch(IOException e) {
	    e.printStackTrace();
	} 
               
        System.out.println("NAT step 1");
        
        // Ugly, pls rewrite...
        try{
             Listen();
        }catch(Exception e){}
        
        new SocketHandler(NAT, this).start();
        
        System.out.println("NAT step 1.5");
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
        //throw new TransportLayerException("Cannot bind port " + listenPort + ".");       
        recieved = 0;
        //try{
           //if(appType != 0) mParentStack.ListenTCP(this, listenPort);  
        //} catch (TransportLayerException e)
        //{
        //    e.printStackTrace();
        //}
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
        //mParentStack.FreeTCPApplication(this);
        //mParentStack.CloseTCP(this);
        recieved = 0;
    }
    
    @Override
	public void Free() throws TransportLayerException{
        //mParentStack.FreeTCPApplication(this);
        recieved = 0;
    }
    
   
    @Override
	public boolean Connect(String s, int i){ return false; }
    
    /**
    * This method disconnects from server.
    * @author key
    * @version v0.01
    */

    public void Disconnect() throws TransportLayerException, LowLinkException{
        try {
        //mParentStack.FinalizeTCP(this); //will close client connection
        }catch(Exception e){
            ///*TODO*: here to catch
          }        
        
        
        LayerInfo protInfo3 = new LayerInfo(getClass().getName());
        protInfo3.setObjectName(mParentStack.getParentNodeName());
        protInfo3.setDataType("Echo Protocol Data");
        protInfo3.setLayer("Network");
        protInfo3.setDescription("NAT closing transversal connection.");
        Simulation.addLayerInfo(protInfo3);
            
        //mParentStack.CloseTCP(this);
        Close();        
    }  

    /**
    * This method is called when client disconnected from server.
    * @author key
    * @version v0.01
    */

    @Override
	public void OnDisconnect(int sock) { 
        
        LayerInfo protInfo3 = new LayerInfo(getClass().getName());
        protInfo3.setObjectName(mParentStack.getParentNodeName());
        protInfo3.setDataType("Echo Protocol Data");
        protInfo3.setLayer("Network");
        protInfo3.setDescription("NAT closing transversal connection.");
        Simulation.addLayerInfo(protInfo3);
            
        try{           
            //mParentStack.CloseTCP(this);
            Close();
            EN.NATDisconnect(this);
            out = null;
            NAT.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
	public void OnError(int sock, int error){
        
    }
    
    @Override
	public void OnConnect(int sock) { 
        
    }
    
    class SocketHandler extends Thread {
        Socket NAT;
        ExternalProxyApp NATE;
        private InputStream in;
        private BufferedReader reader;
    

        SocketHandler(Socket NAT, ExternalProxyApp NATE) {
            this.NAT = NAT;
            this.NATE = NATE;            
        }

        @Override
		public void run() { 
            try {
               in = NAT.getInputStream();
            } catch(IOException e) {
                e.printStackTrace();
            }

            reader = new BufferedReader(new InputStreamReader(in));
	    boolean done = false;
            try{
                while ( ! done) {
                String str = reader.readLine();
                    if (str == null){
                        done = true;
                        NATE.Disconnect();
                    }else{
                        NATE.SendData(str);
                    }	
                }
	    }catch(Exception e){
                try{
                    NATE.Disconnect();
                }catch(Exception e1){}
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
        
           // mParentStack.SendTCP(this, Data,-1);
        
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
       
          
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("Echo Protocol Data");
            protInfo.setLayer("Application ");
            protInfo.setDescription("Recieving echo message '" + Data + "' from client. Total recieved messages: " + recieved + ".");
            Simulation.addLayerInfo(protInfo);
            
            /*LayerInfo protInfo2 = new LayerInfo(getClass().getName());
            protInfo2.setObjectName(mParentStack.getParentNodeName());
            protInfo2.setDataType("Echo Protocol Data");
            protInfo2.setLayer("Application ");
            protInfo2.setDescription("Sending echo message '" + Data + "' to client.");
            Simulation.addLayerInfo(protInfo2);*/
          
            try {                 
                out.print(Data);                
               }catch(Exception e){
                Error.Report(e);
            ///*TODO*: here to catch
            }                                              
        
    }
    
   
    
   /**
    * This method recieves source IP of a packet
    * @param data to receive
    * @author gift (sourceforge.net user)
    * @version v0.01
    */
    public  void RecvIP(String IP) throws LowLinkException, TransportLayerException
    {
        
    }
    
    /**
    * This method recieves source port number of a packet
    * @param data to receive
    * @author gift (sourceforge.net user)
    * @version v0.01
    */
    public  void RecvPrt(int port_num) throws LowLinkException, TransportLayerException
    {      
        
    }
    

}

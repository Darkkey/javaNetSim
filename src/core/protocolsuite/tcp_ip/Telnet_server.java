/*
 * Telnet_server.java
 *
 * Created on 19 Nov 2005, 14:09
 *
 */

package core.protocolsuite.tcp_ip;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.ApplicationLayerDevice;
import core.CommunicationException;
import core.Error;
import core.LowLinkException;
import core.TransportLayerException;

/**
 *
 * @author key
 * @author gift (sourceforge.net user)
 */
public class Telnet_server extends Application{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4916242999290315818L;
	private String login = "root";
    private String def_pass = "javanetsim";
	
    public class TSession{
        public String user;
    }
    
    public boolean running = false;
    
    private ApplicationLayerDevice mDevice;
    private String cmdline = "";
    private boolean islogin=false;
    private boolean ispass=false;
//    private boolean isprompt=true;
    private boolean isnewpass=false;
    private String temp = "";
    public Hashtable<Integer,TSession> connections = new Hashtable<Integer,TSession>();
    
    /** Creates a new instance of Telnet Server */
    public Telnet_server(ApplicationLayerDevice dev, ProtocolStack inParentStack, int listenPort, int appType, long UID) {
        super(inParentStack, listenPort, appType, UID);
        mDevice = dev;
        islogin=false;
        ispass=false;
        appSock = mParentStack.SL().socket(jnSocket.TCP_socket, this);
        mDevice.addUser(login,def_pass);
    }
    
    public void Timer(int code){ }
        
    /**
    * This method start to listen on application port
    * @author key
    * @version v0.01
    */
    public void Listen() throws TransportLayerException{
        //throw new TransportLayerException("Cannot bind port " + listenPort + ".");       
        try{
            mParentStack.SL().bind(appSock, mParentStack.getSrcIP(), listenPort);
            mParentStack.SL().listen(appSock);
            printLayerInfo("Telnet server starts listening in port " + listenPort + ".");
            running = true;
        }catch (TransportLayerException e){
        	printLayerInfo("Error: cannot bind port " + listenPort + ".");
            System.out.println(e.toString());;
        	throw new TransportLayerException("Cannot bind port " + listenPort + "."); 
        }
        islogin=false;
        ispass=false;
    }
    
    public void Accept(int listenSock, int sessionSock){
        connections.put(new Integer(sessionSock), new TSession());
    }
    
    /**
    * This method stop listening on application port
    * @author key
    * @param Protocol Stack 
    * @return Nothing.
    * @version v0.01
    */
    public void Close() throws TransportLayerException
    {              
        running = false;
        mParentStack.SL().close(appSock);
        Enumeration<Integer> keys = connections.keys();
        while(keys.hasMoreElements()){
            Integer key = keys.nextElement();
            mParentStack.SL().close(key.intValue());
            connections.remove(key);
        }
        islogin=false;
        ispass=false;
    }
    
    public void Free() throws TransportLayerException
    {              
        mParentStack.SL().free(appSock);
        Enumeration<Integer> keys = connections.keys();
        while(keys.hasMoreElements()){
            Integer key = keys.nextElement();
            mParentStack.SL().free(key.intValue());
            connections.remove(key);
        }
        islogin=false;
        ispass=false;
    }
    
    public boolean Connect(String temp1, int temp2){
        return false;
    }
    
    
    /**
    * This method should be called from TCP when client connects to server
    * @author key
    * @version v0.01
    */
    public void OnConnect(int sock){
        islogin = true;
        temp = "";
        cmdline = "";
        try{
            SendData(sock, "\r\nlogin: ");
        }catch(LowLinkException e){
            printLayerInfo(e.toString());
        }catch(TransportLayerException e){
            printLayerInfo(e.toString());
        }catch(CommunicationException e){
            printLayerInfo(e.toString());
        }
    }
    
    /**
    * This method is called when client disconnected from server.
    * @author key
    * @version v0.01
    */

    public void OnDisconnect(int sock){
        printLayerInfo("Server: client closed connection");
        islogin=false;
        ispass=false;
        temp = "";
        cmdline = "";
        connections.remove(new Integer(sock));
    }
    
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

    public void SendData(int sock, String Data) throws LowLinkException, TransportLayerException, CommunicationException
    {
            mParentStack.SL().write(sock,Data);
    }
    
    
    
    /**
    * This method recieves data from the other side.
    * @param data to recv
    * @author key
    * @author gift (sourceforge.net user)
    * @version v0.02
    */
    public void RecvData(int sock, String Data) throws LowLinkException, TransportLayerException {
    	String new_data = Data;
        printLayerInfo("Server: recieving message '" + new_data + "' from client.");
        String outData="";

        while(new_data.length()>0) {
            int ch = new_data.charAt(0);
            switch(ch) {
                case 0x08:  {
                    if(cmdline.length() > 0) {
                        outData += "\b";
                        cmdline = cmdline.substring(0,cmdline.length()-1);
                    }
                    break;
                }  
                case 0xA:
                case 0xD:   {
                    if(islogin) {
                        islogin = false;
                        temp = cmdline;
                        ispass = true;
                        outData += "\r\npassword: ";
                    }
                    else if(ispass) {
                        ispass = false;
                        if(isAuth(temp, cmdline)) {
                            connections.get(new Integer(sock)).user = temp;
                            outData += "\r\nWelcome to " + mParentStack.getParentNodeName() + "\r\n" + runcmd("");
                        }
                        else {
                            try {
                                SendData(sock,"\r\nLogin or password is incorrect");
                                outData = "";
                            }
                            catch(Exception e) {
                                Error.Report(e);
                            }
                        }
                    }
                    else if(isnewpass) {
                        if(temp.compareTo("")==0) {
                            temp = cmdline;
                            outData += "\r\nRetype new password: ";
                        }
                        else {
                            if(temp.compareTo(cmdline)==0) {
                                mDevice.addUser(login,temp);
                                outData += "\r\n" + runcmd("");
                            }
                            else {
                                outData += "\r\n Password is not identical\r\n" + runcmd("");
                            }
                            isnewpass = false;
                            temp="";
                        }
                    }
                    else {
                        outData += "\r\n" + runcmd(removeSpaces(cmdline));
                    }
                    cmdline = "";
                    if(ch==0xD && new_data.charAt(1)==0xA) 
                        new_data = new_data.substring(1);
                    break;
                }              
//                    case 0x1B:  outData="ESC"; break;
                default:
                            if(ch>=0x20 && ch<0x80)
                            {
                                if(ispass || isnewpass) {
                                    outData += "*";
                                }
                                else {
                                    outData += String.valueOf(new_data.charAt(0));
                                }
                                cmdline+=String.valueOf(new_data.charAt(0));
                            }
                            else {
//                                    outData += "<" + ( ch < 16 ? "0" : "" ) + Integer.toHexString(ch) + ">";
                            }
            }
            new_data = new_data.substring(1);
        }
        try{
            if(outData.compareTo("")==0){
                mParentStack.SL().disconnect(sock);
            }
            else{
                printLayerInfo("Server: sending message '" + outData + "' to client.");
                SendData(sock, outData);
            }
        }
        catch(CommunicationException e){
            printLayerInfo("Server: can not send data packet: communication error");
        }
    }
    
    private String runcmd(String cmd) {
        String out="";
        if(cmd.compareTo("")==0) {
            //nothing
        }
        else if(cmd.compareTo("?")==0 || cmd.compareToIgnoreCase("help")==0) {
            out += " route   \t show/edit route table\r\n";
            out += " arp     \t show/edit arp table\r\n";
            out += " snmp    \t on/off snmp agent\r\n";
            out += " counters\t show network counters\r\n";
            out += " passwd  \t change password\r\n";
            out += " quit    \t close terminal session\r\n";
            out += " ? or help\tshow this screen\r\n";                   
        }
        else if(cmd.compareToIgnoreCase("quit")==0) {
            return "";
        }
        else {
            String tokens[]=cmd.split(" ");
            Matcher m;
            if(tokens[0].compareTo("route")==0){
                if((m=Pattern.compile(" +print$").matcher(cmd)).find()) {

                    String routes[] = mParentStack.getRouteTableEntries();
                    out += "IP routing table:\r\n" + "Destination" + "\t" + "Gateway" + "\t" + "Genmask"  + "\t" + "Type" + "\t" + "Iface\r\n";
                    for(int i=0; i<routes.length - 1; i++){
                        Route_entry r = mParentStack.getRouteTableEntry(routes[i]);
                        out += routes[i] + "\t" + r.gateway  + "\t" + r.genMask + "\t" + r.Type + "\t" + r.iFace + "\r\n";
                    }
                }
                else if((m=Pattern.compile(" +add +([^ ]+) +([^ ]+) +([^ ]+)( +([^ ]+|\\*))?$").matcher(cmd)).find()) {
                    if(m.group(5)!=null) {                         
                        mParentStack.addRoute(new Route_entry(m.group(1), m.group(5), m.group(3), m.group(2), 0));
                    }
                    else {
                        mParentStack.addRoute(new Route_entry(m.group(1), "", m.group(3), m.group(2), 0));
                    }
                    out+="Route added.\r\n";
                }
                else if((m=Pattern.compile(" +del +([^ ]+)$").matcher(cmd)).find()) {
                    mParentStack.removeRoute(m.group(1));
                    out+="Route to " + tokens[2] + "removed.\r\n";
                }
                else{
                    out+="Unknown route command. Usage:\r\n";
                    out+=" route add (<host ip>|<network ip>) <target interface> <netmask> [<gateway>|*]\r\n" + 
                         "                                                          add new route record\r\n";
                    out+=" route del (<host ip>|<network ip>)                       delete route record\r\n";
                    out+=" route print                                              print route table\r\n";
                }
            }
            else if(tokens[0].compareTo("snmp")==0){
                if((m=Pattern.compile(" +(on|\\d+)( +([^ ]+))?$").matcher(cmd)).find()) {
                    int l_port=SNMP.DEFAULT_PORT;
                    String l_pass="public";
                    if(m.group(1).compareTo("on")!=0) l_port = Integer.parseInt(m.group(1));
                    if(m.group(3)!=null) {
                        if(m.group(3).compareTo("")!=0) l_pass = m.group(3);
                    }
                    SNMP snmpa = (SNMP) mDevice.getApp(161);
                    try{
                        snmpa.Close();
                        //snmpa.Disconnect();
                        out+="SNMP agent stoped\r\n";
                        snmpa.setPassword(l_pass);
                        snmpa.setPort(l_port);
                        try{
                            snmpa.Listen();
                            out+="Now SNMP agent listen on port " + String.valueOf(l_port) + "\r\n";
                        }
                        catch(TransportLayerException e) {
                            out+="Unable to open connection on SNMP agent\r\n";
                        }
                    }
                    catch(TransportLayerException e) {
                        out+="Unable to close connection on SNMP agent\r\n";
                    }
                }    
                else if((m=Pattern.compile(" +off$").matcher(cmd)).find()) {
                    SNMP snmpa = (SNMP) mDevice.getApp(161);
                    try{
                        snmpa.Close();
                        out+="SNMP agent stoped\r\n";
                    }
                    catch(TransportLayerException e) {
                        out+="Unable to close connection on SNMP agent\r\n";
                    }
                }
                else {
                    out+="Unknown snmp command. Usage:\r\n";
                    out+=" snmp (on|<port number>) [community name]    Start SNMP agent\r\n";
                    out+=" snmp off                                    Stop SNMP agent\r\n";
                }
            }
            else if(tokens[0].compareTo("passwd")==0){
                temp="";
                isnewpass=true;
                out="Type new password: ";
                return out;
            }
            else if(tokens[0].compareTo("counters")==0){
                out += " Recieved IP Packets: " + Integer.valueOf(mParentStack.getinputIPCount()).toString() +
                    "\r\n Sent IP Packets: " + Integer.valueOf(mParentStack.getoutputIPCount()).toString() +
                    "\r\n ARP Packets: " + Integer.valueOf(mParentStack.getARPCount()).toString() + 
                    "\r\n Recieved TCP segments: " + Integer.valueOf(mParentStack.getTCPinputCount()).toString() + 
                    "\r\n Sent TCP segments: " + Integer.valueOf(mParentStack.getTCPoutputCount()).toString() + 
                    "\r\n Sent TCP ACK's: " + Integer.valueOf(mParentStack.getTCPACKCount()).toString() + 
                    "\r\n Sent TCP Dublicates: " + Integer.valueOf(mParentStack.getTCPSDCount()).toString() + 
                    "\r\n Recieved TCP Dublicates: " + Integer.valueOf(mParentStack.getTCPRDCount()).toString() + 
                    "\r\n Recieved UDP segments: " + Integer.valueOf(mParentStack.getUDPinputCount()).toString() + 
                    "\r\n Sent UDP segments: " + Integer.valueOf(mParentStack.getUDPoutputCount()).toString() +
                    "\r\n";
            }
            else if(tokens[0].compareTo("arp")==0){
                if((m=Pattern.compile(" +-a$").matcher(cmd)).find()) {
                    try{
                        Vector<Vector<String>> ArpTable = mParentStack.getARPTable();
                        if(ArpTable.size()>0){
                            out += "Internet Address\tPhysical Address\t\tType\r\n";
                            for(int i=0;i<ArpTable.size();i++)
                            {
                                out += ArpTable.get(i).get(0) + "\t\t" + ArpTable.get(i).get(1) + "\t\t" + ArpTable.get(i).get(2) + "\r\n";
                            }
                        }
                        else{
                            out += "No ARP Entries Found\r\n";
                        }
                    }
                    catch(Exception e)
                    {
                            //Should never get here.
                    }
                }
                else if((m=Pattern.compile(" +-d +([^ ]+)$").matcher(cmd)).find()) {
                    mParentStack.removeARP(m.group(1));
                    out += "Removed ARP entry for ip " + m.group(1) + "\r\n";
                }
                else if((m=Pattern.compile(" +-s +([^ ]+) +([^ ]+)$").matcher(cmd)).find()) {
                    mParentStack.addToARPStatic(m.group(1), m.group(2));
                    out += "Created new static ARP entry: " + m.group(1) + " is " + m.group(2) + "\r\n";
                }
                else {
                    out+="Unknown arp command. Usage:\r\n";
                    out+=" arp -a                               print ARP table\r\n";
                    out+=" arp -d <ip address>                  delete record from ARP table\r\n";
                    out+=" arp -s <ip address> <MAC address>    add new ARP record\r\n";
                }
            }
            else out = tokens[0] + ": command not found\r\n";
        }
        out+=mParentStack.getParentNodeName() + " # ";
        return out;
    }
    
    private String removeSpaces(String s) {
    	String r = s;
        while(r.startsWith(" ")) r = r.substring(1);
        while(r.endsWith(" ")) r = r.substring(0, r.length()-1);
        return r;
    }
    
    protected void printLayerInfo(String s) {
        super.printLayerInfo("Telnet Protocol Data", s);
    }
    
    public void setPassword(String s) {
        mDevice.addUser(login, s);
    }
    
    public String getPassword() {
        return mDevice.getUserPassword(login);
    }
    
    private boolean isAuth(String user, String pass){
        boolean auth = false;
        if(pass.equals(mDevice.getUserPassword(user))){
            auth = true;
        }
        return auth;
    }
}

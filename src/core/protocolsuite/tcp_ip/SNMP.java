/*
Java Firewall Simulator (jFirewallSim)

Copyright (c) 2004, jFirewallSim development team All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

	- Redistributions of source code must retain the above copyright notice, this list
	  of conditions and the following disclaimer.
	- Redistributions in binary form must reproduce the above copyright notice, this list
	  of conditions and the following disclaimer in the documentation and/or other
	  materials provided with the distribution.
	- Neither the name of the Canberra Institute of Technology nor the names of its
	  contributors may be used to endorse or promote products derived from this software
	  without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


package core.protocolsuite.tcp_ip;

import java.util.ArrayList;
import java.util.Vector;

import core.ApplicationLayerDevice;
import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LowLinkException;
import core.NetworkLayerDevice;
import core.Node;
import core.TransportLayerException;

/**
 * @author QweR
 * @since Sep 17, 2004
 * @version v0.01
 */


public class SNMP extends Application{

    /**
	 * 
	 */
	private static final long serialVersionUID = -7962147089665700097L;
	public final static int SNMP_ERROR_noError = 0;
    public final static int SNMP_ERROR_tooBig = 1;
    public final static int SNMP_ERROR_noSuchName = 2;
    public final static int SNMP_ERROR_badValue = 3;
    public final static int SNMP_ERROR_readOnly = 4;
    public final static int SNMP_ERROR_genErr = 5;
    public final static int SNMP_ERROR_noAccess = 6;
    public final static int SNMP_ERROR_wrongType = 7;
    public final static int SNMP_ERROR_wrongLength = 8;
    public final static int SNMP_ERROR_wrongEncoding = 9;
    public final static int SNMP_ERROR_wrongValue = 10;         //unused
    public final static int SNMP_GET = 1;
    public final static int SNMP_GETNEXT = 2;
    public final static int SNMP_SET = 3;
    public final static int SNMP_TRAP = 4;
    public final static int SNMP_RESPONSE = 5;
    public final static int DEFAULT_PORT = 161;
    public final static String DEFAULT_COMMUNITY = "public";
    private static int SNMPID = 0;
    public boolean running = false;
    public Vector<SNMPInstance> SNMPgroups = new Vector<SNMPInstance>(0);
    private String current_password = DEFAULT_COMMUNITY;
    private ApplicationLayerDevice current_device;
    //following variables be actual temporality
    private int m_len=0;
    private int m_majorver=0;
    private int m_minorver=0;
    private int m_release=0;
    private int m_id=0;
    private int m_type=-1;
    private int m_errorstatus=0;
    private int m_errorindex=0;
    private int m_generictrap=0;
    private int m_specifictrap=0;
    private String m_password="";
    private Vector<String> m_vars = new Vector<String>(0);
    private Vector<String> m_values = new Vector<String>(0);
    private String m_last="";  //last part of packet after call function (parseSNMPHeader, parseSNMPPassword, parseSNMPError, parseSNMPTrap)
    private String retIP="";
    private int retPort=0;
    private int m_GroupID=-1;
    private int m_InstanceID=-1;
    private String lastPacket=""; //last recieved packet
    private Vector<String> tVectorString = new Vector<String>(0);
    
    public SNMP(ApplicationLayerDevice device,ProtocolStack inParentStack, int listenPort, int appType, long UID) {
        super(inParentStack, listenPort, appType, UID);
        current_device = device;
        //groups and instance required placed in lexicographical order :(MIB)
        int i;
        SNMPgroups.add(new SNMPInstance("Counter"));         //generate by SNMP manager (updateValues)
        i=SNMPgroups.size()-1;
            SNMPgroups.get(i).add("InputIP","");                //ro
            SNMPgroups.get(i).add("OutputIP","");               //ro
            SNMPgroups.get(i).add("ARP","");                    //ro
            SNMPgroups.get(i).add("InputTCP","");               //ro
            SNMPgroups.get(i).add("OutputTCP","");              //ro
            SNMPgroups.get(i).add("ReceiveDuplicatedTCP","");   //ro
            SNMPgroups.get(i).add("SendDuplicatedTCP","");      //ro
            SNMPgroups.get(i).add("SendAckTCP","");             //ro
            SNMPgroups.get(i).add("InputUDP","");               //ro
            SNMPgroups.get(i).add("OutputUDP","");              //ro
        SNMPgroups.add(new SNMPInstance("Device"));         //generate by SNMP manager (updateValues)
        i=SNMPgroups.size()-1;
            SNMPgroups.get(i).add("AllInterfaces","");          //ro
            SNMPgroups.get(i).add("AvailableInterfaces","");    //ro
            SNMPgroups.get(i).add("Hostname","");               //ro!
            SNMPgroups.get(i).add("MACaddress_Eth0","");        //ro
        SNMPgroups.add(new SNMPInstance("IP"));         //generate by SNMP manager (updateValues)
        i=SNMPgroups.size()-1;
            SNMPgroups.get(i).add("AllInterfaces","");          //ro
            SNMPgroups.get(i).add("ARPTable","");               //ro
            SNMPgroups.get(i).add("DefaultGateway","");         //rw
            SNMPgroups.get(i).add("Address_Eth0","");           //rw
            SNMPgroups.get(i).add("SubnetMask_Eth0","");        //rw
        SNMPgroups.add(new SNMPInstance("SNMP"));           //setup by developer
        i=SNMPgroups.size()-1;
            SNMPgroups.get(i).add("CommunityName","");          //rw
            SNMPgroups.get(i).add("revision","0");              //ro
            SNMPgroups.get(i).add("version","2");               //ro
        appSock = mParentStack.SL().socket(jnSocket.UDP_socket, this);
    }
    
    public void Timer(int code){ }
    
    /**
    * This method start to listen on application port
    * @author QweR
    * @version v0.01
    */
    public void Listen() throws TransportLayerException {
        try{
            //mParentStack.ListenUDP(this, listenPort);            
            mParentStack.SL().bind(appSock, mParentStack.getSrcIP(), listenPort);
            mParentStack.SL().listen(appSock);
            running = true;
            printInfo("SNMP agent starts listening on port " + listenPort); //<<-- FIXME!!!
        }
        catch (Exception e) {            
            printInfo("Error: cannot bind port " + listenPort + ".");
            throw new TransportLayerException("Cannot bind port " + listenPort + "."); 
        }
    }
    
    public void Accept(int listenSock, int sessionSock){
        //do nothing, UDP application
    }
    
    /**
    * This method stop listening on application port
    * @author QweR
    * @version v0.01
    */
    public void Close() throws TransportLayerException {
        //mParentStack.CloseUDP(this);
        if(appType==0){
            printInfo("SNMP agent closed socket.");
        }
        else if(appType==1){
            printInfo("SNMP manager closed socket.");
        }
        running = false;
        mParentStack.SL().close(appSock);         
    }
    
    public void Free() throws TransportLayerException{
        if(appType==0){
            printInfo("SNMP agent freed socket.");
        }
        else if(appType==1){
            printInfo("SNMP manager freed socket.");
        }
        mParentStack.SL().free(appSock);         
    }
    
    /**
    * This method connects to server on the other side (imaginations from the other side.... :+)
    * @author QweR
    * @param Host - hostname or ip of server.
    * @param port - server's port
    * @version v0.01
    */
    public boolean Connect(String Host, int port) throws TransportLayerException, InvalidNetworkLayerDeviceException, CommunicationException, LowLinkException {      
        //clientPort = mParentStack.reserveUDPPort(this, sdHost, sdPort);
        //if (clientPort>0) return true;
        //else return false;
        return mParentStack.SL().connect(appSock, Host, port);
    }

    /**
    * This method is called when client disconnected from server.
    * @author key
    * @version v0.01
    */

    public void OnDisconnect(int sock){
    
    }
    
    
    public void OnConnect(int sock){
    
    }
    
    
    public void OnError(int sock, int error){
        
    }

    /**
    * This method sends data to the other side.
    * @param Data to send
    * @author QweR
    * @version v0.01
    */
    public void SendData(int sock, String Data) throws LowLinkException, TransportLayerException, CommunicationException {
         //mParentStack.sendUDP(this, Data);
        String sdHost = mParentStack.SL().get_socket(sock).dst_IP;
        int sdPort = mParentStack.SL().get_socket(sock).dst_port;
        mParentStack.SL().writeTo(sock, Data, sdHost, sdPort);
    }
    
    /**
    * This method recieves data from the other side.
    * @param Data to recv
    * @author QweR
    * @version v0.01
    */
    public void RecvData(int sock, String Data) throws LowLinkException, TransportLayerException {
        //appType = 0 - agent
        //appType = 1 - network management systems
        /** initialization temporary variables **/
        m_len=0;
        m_majorver=0;
        m_minorver=0;
        m_release=0;
        m_id=0;
        m_type=-1;
        m_errorstatus=0;
        m_errorindex=0;
        m_generictrap=0;
        m_specifictrap=0;
        m_password="";
        m_vars.clear();
        m_values.clear();
        m_last="";
        retIP="";
        retPort=0;
        m_GroupID=-1;
        m_InstanceID=-1;
        lastPacket = Data;
        try {
            if(parse(Data) == 0) {
                updateMIBValues();
                switch(m_type) {
                    case SNMP_GET: {
                        if(appType==0) {
                            printInfo("Received getRequest: `" + getPacketData(lastPacket)+"`");
                            if(current_password.compareTo(m_password)==0) {
                                if(getValues())
                                    getResponse(retIP, retPort, m_vars, m_values, m_errorstatus, m_errorindex); // m_errorstatus == 0 && m_errorindex ==0
                                else
                                    getResponse(retIP, retPort, tVectorString, tVectorString, m_errorstatus, m_errorindex);
                            }
                            else {
                                //printInfo("Passwords incomming packet and SNMP agent not equal");
                                getResponse(retIP, retPort, tVectorString, tVectorString, SNMP.SNMP_ERROR_noAccess, 0);
                            }
                        }
                        break;
                    }
                    case SNMP_GETNEXT: {
                        if(appType==0) {
                            printInfo("Received getNextRequest: `" + getPacketData(lastPacket)+"`");
                            if(current_password.compareTo(m_password)==0) {
                                if(getnextValues())
                                    getResponse(retIP, retPort, m_vars, m_values, m_errorstatus, m_errorindex); // m_errorstatus == 0 && m_errorindex ==0
                                else
                                    getResponse(retIP, retPort, tVectorString, tVectorString, m_errorstatus, m_errorindex);
                            }
                            else {
                                //printInfo("Passwords incomming packet and SNMP agent not equal");
                                getResponse(retIP, retPort, tVectorString, tVectorString, SNMP.SNMP_ERROR_noAccess, 0);
                            }
                        }
                        break;
                    }
                    case SNMP_SET: {
                        if(appType==0) {
                            printInfo("Received setRequest: `" + getPacketData(lastPacket)+"`");
                            if(current_password.compareTo(m_password)==0) {
                                if(setValues())
                                    getResponse(retIP, retPort, m_vars, m_values, m_errorstatus, m_errorindex); // m_errorstatus == 0 && m_errorindex ==0
                                else
                                    getResponse(retIP, retPort, tVectorString, tVectorString, m_errorstatus, m_errorindex);
                            }
                            else {
                                //printInfo("Passwords incomming packet and SNMP agent not equal");
                                getResponse(retIP, retPort, tVectorString, tVectorString, SNMP.SNMP_ERROR_noAccess, 0);
                            }
                        }
                        break;
                    }
                    case SNMP_TRAP: {
                        if(appType==1) {
                            //unsupported
                        }
                        break;
                    }
                    case SNMP_RESPONSE: {
                        if(appType==1) {
                            if(m_vars.size()==m_values.size()) {
                                String s = "";
                                for(int i=0;i<m_vars.size();i++) {
                                    if(i!=0) s+=" , ";
                                    s+="'"+m_vars.get(i)+"="+m_values.get(i)+"'";
                                }
                                switch(m_errorstatus) {
                                    case SNMP_ERROR_noError: {
                                        //s+="  without error";
                                        break;
                                    }
                                    case SNMP_ERROR_noSuchName: {
                                        s+="  no such name #"+Integer.valueOf(m_errorindex).toString();                                        
                                        break;
                                    }
                                    case SNMP_ERROR_badValue: {
                                        s+="  bad value #"+Integer.valueOf(m_errorindex).toString();  
                                        break;
                                    }
                                    case SNMP_ERROR_readOnly: {
                                        s+="  read only #"+Integer.valueOf(m_errorindex).toString();  
                                        break;
                                    }
                                    case SNMP_ERROR_genErr: {
                                        s+="  gereral error";
                                        break;
                                    }
                                    case SNMP_ERROR_noAccess: {
                                        s+="  no access (incorrect community name)";
                                        break;
                                    }
                                    case SNMP_ERROR_wrongType: {
                                        s+="  wrong type #"+Integer.valueOf(m_errorindex).toString();  
                                        break;
                                    }
                                    case SNMP_ERROR_wrongLength: {
                                        s+="  wrong length";
                                        break;
                                    }
                                    case SNMP_ERROR_wrongEncoding: {
                                        s+="  wrong encoding";
                                        break;
                                    }
                                    case SNMP_ERROR_wrongValue: {
                                        s+="  wrong value #"+Integer.valueOf(m_errorindex).toString();  
                                        break;
                                    }
                                }
                                printInfo("Received getResponse: " + s);
                            }
                            else {
                                printInfo("Received getResponse: invalid packet.");
                            }
                        }
                        break;
                    }
                    default: break;
                }
            }
            else {
                switch(appType) {
                    case 0: {
                        printInfo("Agent received corrupted packet!");
                        getResponse(retIP, retPort, tVectorString, tVectorString, m_errorstatus, m_errorindex);
                        break;
                    }
                    case 1: {
                        printInfo("Manager received corrupted packet!");
                        break;
                    }
                    default: {
                        printInfo("UNKNOWN program received corrupted packet!");
                        break;
                    }
                }
                
            }
            if(appType==0) {
                // Reallly needn't to do anything in version...
                //Close();
                //Listen();
            }
            else if(appType==1) {
                Close();
            }
            retIP="unknown";
            retPort=0;
        }
        catch (CommunicationException e) {
            //print anything...?
        }
        catch(InvalidNetworkLayerDeviceException e) {
            //print another anything...
        }
    }
    
    /**
    * This method realize Get-request function of SNMPv2.
    * @param r vector of String. String as 'group.instance'
    * @return false if one or more variables not exist
    * @author QweR
    * @version v0.01
    */
    public boolean getRequest(String Host, int port, Vector<String> r,String password) throws LowLinkException, TransportLayerException, CommunicationException, InvalidNetworkLayerDeviceException {
        if (Connect(Host, port)) {
            String pack = createSNMPHeader(getNextID(),SNMP.SNMP_GET);
            pack += createSNMPPassword(password);
            String tmp = createSNMPVars(r);
            if(tmp==null) {
                return false;
            }
            pack=finalizePacket(pack+tmp);
            printInfo("Sending getRequest message `" + getPacketData(pack) + "` to " + Host + ":" + port);
            SendData(pack);       
        } 
        else {
            printInfo("Error: can not connect to " + Host + ":" + port + "!");
            return false;
        }
        return true;
    }
    
    /**
    * This method realize Get-next-request function of SNMPv2.
    * @param r vector of String. String as 'group.instance'
    * @return false if one or more variables not exist
    * @author QweR
    * @version v0.01
    */
    public boolean getNextRequest(String Host, int port, Vector<String> r,String password) throws LowLinkException, TransportLayerException, CommunicationException, InvalidNetworkLayerDeviceException {
        if (Connect(Host, port)) {
            String pack = createSNMPHeader(getNextID(),SNMP.SNMP_GETNEXT);
            pack += createSNMPPassword(password);
            String tmp = createSNMPVars(r);
            if(tmp==null) {
                return false;
            }
            pack=finalizePacket(pack+tmp);
            printInfo("Sending getNextRequest message `" + getPacketData(pack) + "` to " + Host + ":" + port);
            SendData(pack);   
        } 
        else {
            printInfo("Error: can not connect to " + Host + ":" + port + "!");
            return false;
        }
        return true;
    }
    
    /**
    * This method realize Set-request function of SNMPv2.
    * @param r vector of String. String as 'group.instance'
    * @param v vector of String. String as 'value'
    * @return false if one or more variables not exist
    * @author QweR
    * @version v0.01
    */
    public boolean setRequest(String Host, int port, Vector<String> r,Vector<String> v,String password) throws LowLinkException, TransportLayerException, CommunicationException, InvalidNetworkLayerDeviceException {
        if (Connect(Host, port)) {
            String pack = createSNMPHeader(getNextID(),SNMP.SNMP_SET);
            pack += createSNMPPassword(password);
            String tmp = createSNMPVarsValues(r,v);
            if(tmp==null) {
                return false;
            }
            pack=finalizePacket(pack+tmp);
            printInfo("Sending setRequest message `" + getPacketData(pack) + "` to " + Host + ":" + port);
            SendData(pack);   
        } 
        else {
            printInfo("Error: can not connect to " + Host + ":" + port + "!");
            return false;
        }
        return true;
    }
    
    /**
    * This method realize Get-response function of SNMPv2.
    * @param r vector of String. String as 'group.instance'
    * @param v vector of String. String as 'value'
    * @return false if one or more variables not exist
    * @author QweR
    * @version v0.01
    */
    public boolean getResponse(String Host, int port, Vector<String> r,Vector<String> v,int es,int ei) throws LowLinkException, TransportLayerException, CommunicationException, InvalidNetworkLayerDeviceException {
        String pack = createSNMPHeader(m_id,SNMP.SNMP_RESPONSE);
        pack += createSNMPError(es,ei);
        String tmp = createSNMPVarsValues(r,v);
        if(tmp==null) {
            return false;
        }
        pack=finalizePacket(pack+tmp);
//        printInfo("Sending message `" + getPacketData(pack) + "` to " + Host + ":" + port);
        printInfo("Sending getResponse message `" + getPacketData(pack) + "` to manager");
        SendData(pack);  
        return true;
    }
    
    /**
    * This method realize TRAP function of SNMPv2.
    * @param r vector of String. String as 'group.instance'
    * @param v vector of String. String as 'value'
    * @return false if one or more variables not exist
    * @author QweR
    * @version v0.01
    */
//    public boolean trap(String Host, int port, Vector<String> r,Vector<String> v,int gtc,int stc) throws LowLinkException, TransportLayerException, CommunicationException, InvalidNetworkLayerDeviceException {
//        mParentStack.FreeUDPApplication(this);
//        if (ClientConnect(Host, port)) {
//                  
//            String pack = createSNMPHeader(getNextID(),SNMP_TRAP);
//            pack += createSNMPTrap(gtc,stc);
//            String tmp = createSNMPVarsValues(r,v);
//            if(tmp==null) {
//                return false;
//            }
//            printInfo("Start sending message " + getPacketData(pack+tmp) + " to " + Host + ":" + port);
//            SendData(finalizePacket(pack+tmp)); 
//        } 
//        else {
//            printInfo("Error: can not connect to " + Host + ":" + port + "!");
//            return false;
//        }
//        return true;
//    }

    /**
    * This method set password for SNMP agent.
    * @param p new password.
    * @return false if new password equal null or ""
    * @author QweR
    * @version v0.01
    */
    public boolean setPassword(String p) {
        if(p==null || p.compareTo("")==0) return false;
        current_password = p;
        return true;
    }
    
    /**
    * This method set password for SNMP agent.
    * @return current password for SNMP agnet.
    * @author QweR
    * @version v0.01
    */
    public String getPassword() {
        return current_password;
    }
       
    /**
    * This method parse incoming packet.
    * @param in incoming packet
    * @return result of parsing: 
     * if 0 then successful pasre
     * if 1 then error in main header
     * if 2 then error in password block
     * if 3 then error in error block
     * if 4 then error in trap block
     * if 5 then error in variables block
     * if 6 then error in values block
    * @author QweR
    * @version v0.01
    */
    protected int parse(String in) {
        
        if(parseSNMPHeader(in)) {
            switch(m_type) {
                case SNMP_GET: {
                    if(parseSNMPPassword(m_last)) {
                        if(parseSNMPVars(m_last)) {
                            //printInfo("recieved GET");
                        }
                        else return 5;
                    }
                    else return 2;
                    break;
                }
                case SNMP_GETNEXT: {
                    if(parseSNMPPassword(m_last)) {
                        if(parseSNMPVars(m_last)) {
                            //printInfo("recieved GETNEXT");
                        }
                        else return 5;
                    }
                    else return 2;
                    break;
                }
                case SNMP_SET: {
                    if(parseSNMPPassword(m_last)) {
                        if(parseSNMPVarsValues(m_last)) {
                            //printInfo("recieved SET");
                        }
                        else return 6;
                    }
                    else return 2;
                    break;
                }
                case SNMP_TRAP: {
                    if(parseSNMPTrap(m_last)) {
                        if(parseSNMPVars(m_last)) {
                            //printInfo("recieved GET.");
                        }
                        else return 5;
                    }
                    else return 4;
                    break;
                }
                case SNMP_RESPONSE: {
                    if(parseSNMPError(m_last)) {
                        if(parseSNMPVarsValues(m_last)) {
                            //printInfo("recieved RESPONSE");
                        }
                        else return 6;
                    }
                    else return 3;
                    break;
                }
                default: return 1;
            }
        }
        else return 1;
        return 0;
    }
    
    /**
    * This method gets values from vector of 'group.instance'.
    * return true if function successful complete. If function false then m_errorstatus & m_errorindex contain error code
    * @author QweR
    * @version v0.01
    */
    protected boolean getValues() {
        String[] a;
        int i;
        
        m_values.clear();
        for(i=0;i<m_vars.size();i++) {
            a = m_vars.get(i).split("\\.");
            if(a.length>=2) {
                if(getIDbyName(a[0],a[1])) {
                    m_values.add(SNMPgroups.get(m_GroupID).value.get(m_InstanceID));
                }
                else {
                    m_errorstatus = SNMP.SNMP_ERROR_noSuchName;
                    m_errorindex = i+1;
                    return false;
                }
            }
            else {
                m_errorstatus = SNMP.SNMP_ERROR_noSuchName;
                m_errorindex = i+1;
                return false;
            }
        }
        return true;
    }
    
    /**
    * This method get next values from vector of 'group.instance'.
    * return true if function successful complete. If function false then m_errorstatus & m_errorindex contain error code
    * @author QweR
    * @version v0.01
    */
    protected boolean getnextValues() {
        String[] a;
        int i;
        
        m_values.clear();
        for(i=0;i<m_vars.size();i++) {
            a = m_vars.get(i).split("\\.");
            if(a.length>=2) {
                if(getIDbyName(a[0],a[1])) {
                    if(m_InstanceID+1<SNMPgroups.get(m_GroupID).value.size()) m_InstanceID++;
                    else if(m_GroupID+1<SNMPgroups.size()){
                        m_GroupID++;
                        m_InstanceID=0;
                    }
                    m_values.add(SNMPgroups.get(m_GroupID).value.get(m_InstanceID));
                    m_vars.set(i,SNMPgroups.get(m_GroupID).name+"."+SNMPgroups.get(m_GroupID).instance.get(m_InstanceID));
                }
                else {
                    m_errorstatus = SNMP.SNMP_ERROR_noSuchName;
                    m_errorindex = i+1;
                    return false;
                }
            }
            else {
                m_errorstatus = SNMP.SNMP_ERROR_noSuchName;
                m_errorindex = i+1;
                return false;
            }
        }
        return true;
    }
    
    /**
    * This method sets values from vector of 'group.instance' and 'value'.
    * return true if function successful complete. If function false then m_errorstatus & m_errorindex contain error code
    * @author QweR
    * @version v0.01
    */
    protected boolean setValues() {
        String[] a;
        String[] sa = new String[m_vars.size()];
        int i;
        int er;
        
        for(i=0;i<m_vars.size();i++) {
            a = m_vars.get(i).split("\\.");
            if(a.length>=2) {
                if(getIDbyName(a[0],a[1])) {
                    er = checkMIBValue(a[0]+"."+a[1], m_values.get(i));
                    if(er==0) {
                        SNMPgroups.get(m_GroupID).value.set(m_InstanceID,m_values.get(i));
                        sa[i] = a[0]+"."+a[1];
                    }
                    else {
                        m_errorstatus = er;
                        m_errorindex = i+1;
                        return false;
                    }
                }
                else {
                    m_errorstatus = SNMP.SNMP_ERROR_noSuchName;
                    m_errorindex = i+1;
                    return false;
                }
            }
            else {
                m_errorstatus = SNMP.SNMP_ERROR_noSuchName;
                m_errorindex = i+1;
                return false;
            }
        }
        for(i=0;i<m_vars.size();i++) setMIBValue(sa[i], m_values.get(i));       //set values only if all variables correct
        return true;
    }
    
    /**
    * This method create SNMP header for all type packets.
    * @param id identifier of message
    * @param type type of message
    * @return string SNMP header
    * @author QweR
    * @version v0.01
    */
    protected String createSNMPHeader(int id,int type){
        char[] c = new char[8];
        
        c[0]=c[1]=0;
        c[2]=2;
        c[3]=2;
        c[4]=0;
        c[5]=(char)(id/256);
        c[6]=(char)(id%256);
        c[7]=(char)type;
        return (String.copyValueOf(c));
    }
    
    /**
    * This method create SNMP password header.
    * @param pass password
    * @return string SNMP password header
    * @author QweR
    * @version v0.01
    */
    protected String createSNMPPassword(String pass) {
        char[] c = new char[2];
        
        c[0]=(char)(pass.length()/256);
        c[1]=(char)(pass.length()%256);
        return (String.copyValueOf(c)+pass);
    }
    
    /**
    * This method create SNMP array of group+instance.
    * @param r vector of variables
    * @return string SNMP variables
    * @author QweR
    * @version v0.01
    */
    protected String createSNMPVars(Vector<String> r) {
        int i;
        String[] gi;
        String out = "";
        char[] c = {0,0,0,0};
        //char[] c = new char[4];
        
        for(i=0;i<r.size();i++) {
            gi = r.get(i).split("\\.");
            if(gi.length==2) {
                if(getIDbyName(gi[0], gi[1])) {
                    c[0]=(char)(m_GroupID+1);
                    //c[1]=0;
                    c[2]=(char)(m_InstanceID+1);
                    //c[3]=0;
                }
                else c[0]=c[2]=0;
            }
            else c[0]=c[2]=0;
            out+=String.copyValueOf(c);
        }
        return out;
    }
    
    /**
    * This method create SNMP array of group+instance+value.
    * @param r vector of variables
    * @param v vector of values conform variables
    * @return string SNMP variables with values
    * @author QweR
    * @version v0.01
    */
    protected String createSNMPVarsValues(Vector<String> r,Vector<String> v) {
        int i;
        String[] gi;
        String out = "";
        char[] c = {0,0,0,0};
        char[] cv = new char[3];
        
        if(r.size()!=v.size()) return "";
        for(i=0;i<r.size();i++) {
            gi = r.get(i).split("\\.");
            if(gi.length==2) {
                if(getIDbyName(gi[0],gi[1])) {
                    c[0]=(char)(m_GroupID+1);
                    //c[1]=0;
                    c[2]=(char)(m_InstanceID+1);
                    //c[3]=0;
                }
                else c[0]=c[2]=0;
            }
            else c[0]=c[2]=0;
            cv[0]=2;
            cv[1]=(char)(v.get(i).length()/256);
            cv[2]=(char)(v.get(i).length()%256);
            out+=String.copyValueOf(c)+String.copyValueOf(cv)+v.get(i);
        }
        return out;
    }
    
    /**
    * This method create SNMP error body for packet.
    * @param es error status
    * @param ei error index
    * @return string SNMP error body
    * @author QweR
    * @version v0.01
    */
    protected String createSNMPError(int es,int ei) {
        char[] c = new char[7];
        
        c[0]=(char) es;
        c[1]=(char)(ei/16777216);
        c[2]=(char)((ei%16777216)/65536);
        c[3]=(char)((ei%65536)/256);
        c[4]=(char)(ei%256);
        c[5]=0;
        c[6]=0;
        return (String.copyValueOf(c));
    }
    
    /**
    * This method create SNMP trap body for packet.
    * @param g generic trap code
    * @param s specific trap code
    * @return string SNMP trap body
    * @author QweR
    * @version v0.01
    */
    protected String createSNMPTrap(int g,int s) {
        char[] c = new char[6];
        
        c[0]=(char)(g/16777216);
        c[1]=(char)((g%16777216)/65536);
        c[2]=(char)((g%65536)/256);
        c[3]=(char)(g%256);
        c[4]=(char)(s/256);
        c[5]=(char)(s%256);
        return (String.copyValueOf(c));
    }
    
    /**
    * This method finalized (set correct length) SNMP packet.
    * @param g generic trap code
    * @param s specific trap code
    * @return string SNMP trap body
    * @author QweR
    * @version v0.01
    */
    protected String finalizePacket(String p) {
        char[] c = new char[2];
        
        c[0]=(char)((p.length()-2)/256);
        c[1]=(char)((p.length()-2)%256);
        return (String.copyValueOf(c)+p.substring(2));
    }
    
    /**
    * This method parse a SNMP header to normal data.
    * @param in packet
    * @param last last piece of packet
    * @return result of parsing: if false then error
    * @author QweR
    * @version v0.01
    */
    protected boolean parseSNMPHeader(String in) {
        if(in.length()>=8) {
            m_len=in.charAt(0)*256+in.charAt(1);
            m_majorver=in.charAt(2);
            m_minorver=in.charAt(3);
            m_release=in.charAt(4);
            m_id=in.charAt(5)*256+in.charAt(6);
            m_type=in.charAt(7);
            m_last=in.substring(8);
            if(m_len!=in.length()-2) {
                m_errorstatus = SNMP.SNMP_ERROR_wrongLength;
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
    * This method parse SNMP password header to Password.
    * @param in packet, incipient from password
    * @param last last piece of packet
    * @return result of parsing: if false then error
    * @author QweR
    * @version v0.01
    */
    protected boolean parseSNMPPassword(String in) {
        if(in.length()>=2) {
            int l=in.charAt(0)*256+in.charAt(1);
            if(in.length()<l+2) {
                m_errorstatus = SNMP.SNMP_ERROR_wrongLength;
                return false;
            }
            m_password=in.substring(2, l+2);
            m_last=in.substring(l+2);
            return true;
        }
        return false;
    }
    
    /**
    * This method parse SNMP body to array of group+instance.
    * @param r packet body
    * @return result of parsing: if false then error
    * @author QweR
    * @version v0.01
    */
    protected boolean parseSNMPVars(String r) {
        char[] c = r.toCharArray();
        int i;
        
        m_vars.clear();
        for(i=0;i<c.length;i+=4) {
            if(c[i+1]==0 && c[i+3]==0) {
                if(c[i]>0 && c[i+2]>0 && c[i]<=SNMPgroups.size() && c[i+2]<=SNMPgroups.get(c[i]-1).instance.size()) {
                    m_vars.add(SNMPgroups.get(c[i]-1).name+"."+SNMPgroups.get(c[i]-1).instance.get(c[i+2]-1));
                }
                else {
                    m_errorstatus=SNMP.SNMP_ERROR_noSuchName;
                    m_errorindex=m_vars.size()+1;
                    return false;
                }
            }
            else {
                m_errorstatus=SNMP.SNMP_ERROR_genErr;
                return false;
            }
        }
        if(c.length!=i) return false;
        return true;
    }
    
    /**
    * This method parse SNMP body to array of group+instance+value.
    * @param in packet body
    * @param v (output data) vector of values conform returned variables
    * @return result of parsing: if false then error
    * @author QweR
    * @version v0.01
    */
    protected boolean parseSNMPVarsValues(String in) {
        char[] c = in.toCharArray();
        int i,len;

        m_vars.clear();
        m_values.clear();
        for(i=0;i<c.length;i+=7) {
            if(i+6<c.length) {
                if(c[i+1]==0 && c[i+3]==0) {
                    if(c[i+4]==2) {
                        len = c[i+5]*256+c[i+6];
                        if(i+6+len<c.length) {
                            if(c[i]>0 && c[i+2]>0 && c[i]<=SNMPgroups.size() && c[i+2]<=SNMPgroups.get(c[i]-1).instance.size()) {
                                m_vars.add(SNMPgroups.get(c[i]-1).name+"."+SNMPgroups.get(c[i]-1).instance.get(c[i+2]-1));
                                m_values.add(String.copyValueOf(c, i+7, len));
                                i+=len;
                            }
                            else {
                                m_errorstatus = SNMP.SNMP_ERROR_noSuchName;
                                m_errorindex=m_vars.size()+1;
                                return false;
                            }
                        }
                        else {
                            m_errorstatus = SNMP.SNMP_ERROR_wrongLength;
                            return false;
                        }
                    }
                    else {
                        m_errorstatus = SNMP.SNMP_ERROR_wrongType;
                        return false;
                    }
                }
                else {
                    m_errorstatus = SNMP.SNMP_ERROR_genErr;
                    return false;
                }
            }
            else {
                m_errorstatus = SNMP.SNMP_ERROR_wrongLength;
                return false;
            }
        }
        if(c.length!=i) return false;
        return true;
    }
    
    /**
    * This method parse SNMP error body of packet to normal data.
    * @param in error header
    * @param last last piece of packet
    * @return result of parsing: if false then error
    * @author QweR
    * @version v0.01
    */
    protected boolean parseSNMPError(String in) {
        if(in.length()<7) {
            m_errorstatus = SNMP.SNMP_ERROR_wrongLength;
            return false;
        }
        m_errorstatus=in.charAt(0);
        m_errorindex=((in.charAt(1)*256+in.charAt(2))*256+in.charAt(3))*256+in.charAt(4);
        m_last = in.substring(7);
        return true;
    }
    
    /**
    * This method parse SNMP trap body of packet to normal data.
    * @param in trap header
    * @param last last piece of packet
    * @return result of parsing: if false then error
    * @author QweR
    * @version v0.01
    */
    protected boolean parseSNMPTrap(String in) {
        if(in.length()>6) {
            m_errorstatus = SNMP.SNMP_ERROR_wrongLength;
            return false;
        }
        m_generictrap=((in.charAt(0)*256+in.charAt(1))*256+in.charAt(2))*256+in.charAt(3);
        m_specifictrap=in.charAt(4)+in.charAt(5);
        m_last = in.substring(6);
        return true;
    }
    
    /**
    * This method gets next value of SNMPID modulo 2^16.
    * @author QweR
    * @version v0.01
    */
    protected int getNextID() {
        if(SNMP.SNMPID >= 65535) SNMP.SNMPID=0;
        else SNMP.SNMPID++;
        return SNMP.SNMPID;
    }
    
    /**
    * This method update SNMP values.
    * @author QweR
    * @version v0.01
    */
    protected void updateMIBValues() {
        String str="";
        String[] stra;
        ArrayList alist;
        Object[] oba;
        try {
        //"Counter.InputIP"
            getIDbyName("Counter","InputIP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getinputIPCount()).toString());
        //"Counter.OutputIP"
            getIDbyName("Counter","OutputIP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getoutputIPCount()).toString());
        //"Counter.ARP"
            getIDbyName("Counter","ARP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getARPCount()).toString());
        //"Counter.InputTCP"
            getIDbyName("Counter","InputTCP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getTCPinputCount()).toString());
        //"Counter.OutputTCP"
            getIDbyName("Counter","OutputTCP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getTCPoutputCount()).toString());
        //"Counter.ReceiveDuplicatedTCP"
            getIDbyName("Counter","ReceiveDuplicatedTCP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getTCPRDCount()).toString());
        //"Counter.SendDuplicatedTCP"
            getIDbyName("Counter","SendDuplicatedTCP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getTCPSDCount()).toString());
        //"Counter.SendAckTCP"
            getIDbyName("Counter","SendAckTCP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getTCPACKCount()).toString());
        //"Counter.InputUDP"
            getIDbyName("Counter","InputUDP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getUDPinputCount()).toString());
        //"Counter.OutputUDP"
            getIDbyName("Counter","OutputUDP");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, Integer.valueOf(mParentStack.getUDPoutputCount()).toString());
        //"Device.AllInterfaces"
            str="";
            alist=((Node)current_device).getAllInterfacesNames();
            for(int i=0;i<alist.size();i++) {
                if(i!=0) str+=", ";
                str+=alist.get(i).toString();
            }
            getIDbyName("Device", "AllInterfaces");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, str);
        //"Device.AvailableInterfaces"
            str="";
            stra=((Node)current_device).getAvailableInterfaces();
            for(int i=0;i<stra.length;i++) {
                if(i!=0) str+=", ";
                str+=stra[i];
            }
            getIDbyName("Device", "AvailableInterfaces");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, str);
        //"Device.Hostname"
            getIDbyName("Device", "Hostname");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, ((Node)current_device).getName());
        //"Device.MACaddress_Eth0"
            getIDbyName("Device", "MACaddress_Eth0");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, ((Node)current_device).getMACAddress("eth0"));
        //"IP.AllInterfaces"
            str="";
            oba=((NetworkLayerDevice)current_device).getAllInterfaces();
            for(int i=0;i<oba.length;i++) {
                if(i!=0) str+=", ";
                str+=oba[i].toString();
            }
            getIDbyName("IP", "AllInterfaces");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, str);
        //"IP.ARPTable"
            str="";
            Vector<Vector<String>> ArpTable = ((NetworkLayerDevice)current_device).getARPTable();
            for(int i=0;i<ArpTable.size();i++) {
                if(i!=0) str+=", ";
                str += ArpTable.get(i).get(0) + "\t\t" + ArpTable.get(i).get(1) + "\t\t" + ArpTable.get(i).get(2);
            }
            getIDbyName("IP", "ARPTable");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, str);
        //"IP.DefaultGateway"
            getIDbyName("IP", "DefaultGateway");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, ((NetworkLayerDevice)current_device).getDefaultGateway());
        //"IP.IPaddress_Eth0"
            getIDbyName("IP", "Address_Eth0");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, ((NetworkLayerDevice)current_device).getIPAddress("eth0"));
        //"IP.subnetMask_Eth0"
            getIDbyName("IP", "SubnetMask_Eth0");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, ((NetworkLayerDevice)current_device).getSubnetMask("eth0"));
        //"SNMP.CommunityName"
            getIDbyName("SNMP", "CommunityName");
            SNMPgroups.get(m_GroupID).value.set(m_InstanceID, current_password);
        }
        catch(Exception e) {
            System.out.print(e.toString());
        }
    }
    
    /**
     * This method check MIB values on correctness
     * @author QweR
     * @version v0.01
     */
    private int checkMIBValue(String var,String val) {
        if(var.compareToIgnoreCase("test.test_1")==0) {
            if(val.compareTo("B")==0 || val.compareTo("C")==0) return 0;
            else return SNMP.SNMP_ERROR_badValue;
        }
        else if(var.compareToIgnoreCase("test.test_2")==0) {
            if(Integer.parseInt(val,10)>0 && Integer.parseInt(val,10)<99) return 0;
            else return SNMP.SNMP_ERROR_badValue;
        }
        else if(var.compareToIgnoreCase("IP.DefaultGateway")==0) {
            if(val!="" || var!=null) return 0;
        }     
        else if(var.compareToIgnoreCase("IP.Address_Eth0")==0) {
            if(val!="" || var!=null) return 0;
        } 
        else if(var.compareToIgnoreCase("IP.SubnetMask_Eth0")==0) {
            if(val!="" || var!=null) return 0;
        }  
        else if(var.compareToIgnoreCase("SNMP.CommunityName")==0) {
            if(val!="" || var!=null) return 0;
        }       
        return SNMP.SNMP_ERROR_readOnly;
    }

    /**
     * This method set MIB value
     * @author QweR
     * @version v0.01
     */
    private int setMIBValue(String var,String val) {
        try {
            int er=0;        
            if((er=checkMIBValue(var, val))!=0) return er;
            if(var.compareToIgnoreCase("IP.DefaultGateway")==0) {
                ((NetworkLayerDevice)current_device).setDefaultGateway(val);
            }
            else if(var.compareToIgnoreCase("IP.Address_Eth0")==0) {
                String a = ((NetworkLayerDevice)current_device).setIPAddress("eth0", val);
//                printInfo(">>>>>>>>>> " + a);
            }
            else if(var.compareToIgnoreCase("IP.subnetMask_Eth0")==0) {
                ((NetworkLayerDevice)current_device).setCustomSubnetMask("eth0", val);
            }
            else if(var.compareToIgnoreCase("SNMP.CommunityName")==0) {
                current_password=val;
            }
        }
        catch(Exception e) {
            //System.out.print(e.toString());
            printInfo("bad value: " + var + " = \"" + val);
            return SNMP.SNMP_ERROR_badValue;
        }
        return 0;
    }
    
    /**
    * This method return index by string name of variable.
    * @param n name of variable
    * @author QweR
    * @version v0.01
    */
    public int getGroupID(String n) {
        int i;
        
        for(i=0;i<SNMPgroups.size();i++) {
            if(SNMPgroups.get(i).name.compareToIgnoreCase(n)==0) break;
        }
        if(i>=SNMPgroups.size()) return (-1);
        return i;
    }
    
    /**
    * This method return index by string name of variable.
    * @param n name of variable
    * @author QweR
    * @version v0.01
    */
    public boolean getIDbyName(String gr,String ins) {
        if((m_GroupID=getGroupID(gr))!=-1) {
            if((m_InstanceID=SNMPgroups.get(m_GroupID).getInstanceID(ins))!=-1) {
                return true;
            }
        }
        return false;
    }
    
    protected void printInfo(String s) {
           printLayerInfo("SNMP Protocol Data", s);            
    }
    
    protected String getPacketData(String pack) {
        String out="",ob="";
        for(int i=0;i<pack.length();i++) {
            if(i!=0) out += " ";
            ob = Integer.toString((int) pack.charAt(i), 16).toUpperCase();
            if(ob.length()<=1) out += "0";
            out += ob;
        }
        return out;
    }
    
    public class SNMPInstance {
        public String name;
        public Vector<String> instance = new Vector<String>(0);
        public Vector<String> value = new Vector<String>(0);
        
        public SNMPInstance(String n) {
            name=n;
        }
        
        /**
        * This method return index by string name of variable.
        * @param n name of variable
        * @author QweR
        * @version v0.01
        */
        public int getInstanceID(String n) {
            int i;
            
            for(i=0;i<instance.size();i++) {
                if(instance.get(i).compareToIgnoreCase(n)==0) break;
            }
            if(i>=instance.size()) return (-1);
            return i;
        }
        
        /**
        * This method add variable and variable value to my MIB.
        * @param i instance of variables
        * @param v value of varialbes
        * @author QweR
        * @version v0.01
        */
        public void add(String i,String v) {
            instance.add(i);
            value.add(v);
        }
    }
}

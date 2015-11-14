/* 
Java Network Simulator (javaNetSim) 

Copyright (c) 2007, 2006, 2005, Ice Team;  All rights reserved.
Copyright (c) 2004, jFirewallSim development team;  All rights reserved. 
 
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
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

//import sun.swing.PrintingStatus;

import core.CommunicationException;
import core.LayerInfo;
import core.LowLinkException;
import core.Simulation;
import core.TransportLayerException;
import core.TransportLayerPortException;

/**
 *
 * This is for design and future implementation of TCP
 *
 * @author luke_hamilton
 *
 * @since Sep 17, 2004
 *
 * @version v0.20
 *
 * @author gift (sourceforge.net user)
 * @since 25 Nov 2005
 * @version v0.9 released 05 Dec 2005
 *
 */





public class Tcp implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1545651097126589467L;

	private class TCPTask extends TimerTask {
        public TCPTask() {
        }
        
        /**
         * This methos implements TCP timer algorithm
         * @author gift (sourceforge.net user)
         * @version v0.10
         */
        public void run() {
            try{
                LinkedList<Resender> q_rs = new LinkedList<Resender>();
                LinkedList<Integer> q_nt = new LinkedList<Integer>();
                synchronized(tcp_timers_lock){
                    while(!tcp_timers.isEmpty() && current_time > ((Resender)tcp_timers.peek()).getResendTime()){
                        Resender rs = (Resender)tcp_timers.poll();
                        int nt = rs.nextTime(current_time);
                        if(nt>0){
                            tcp_timers.add(rs);
                        }
                        q_rs.add(rs);
                        q_nt.add(new Integer(nt));
                        //printLayerInfo(">>> Timer "+mParentStack.getHostName()+":"+rs.timerID+" was reinstalled");
                    }
                    //System.out.println("Host: "+mParentStack.getHostName()+", time: " + current_time);
                }
                while(!q_rs.isEmpty()){
                    Resender rs = (Resender) q_rs.poll();
                    int nt = ((Integer)q_nt.poll()).intValue();
                    rs.resend(nt);
                }
                if(!q_nt.isEmpty()) printLayerInfo(">>> TCP timer: synchronization ERROR!!!!");
            }catch(Exception e){
                e.printStackTrace();
            }
            current_time += Tcp.TCP_TIMER_DELAY;
        }
    }
    
    private class Resender implements Comparable{
        private ConcurrentLinkedQueue<Integer> resend_time = new ConcurrentLinkedQueue<Integer>();
        private TCP_packet packet;
        private int sock;
        public int nearest_resend;
        public int resend_delay;
        public long timerID;
        
        public Resender(int sock, TCP_packet pack, int times[]){
            this.sock = sock;
            nearest_resend = 0;
            resend_delay = 0;
            packet = pack;
            for(int i=0; i<times.length; i++){
                Integer iii = new Integer(times[i]);
                resend_time.add(iii);
            }
        }
        
        public boolean start(int cur_time){
            return (nextTime(cur_time)>0);
        }
        
        public boolean resend(int nt){
            boolean res = false;
            if(nt>=0){
                try{
                    IncSentDuplicatesNumber(1);
                    TCP_session tcps = getSession(mParentStack.SL().get_socket(sock).genKey());
                    if(tcps!=null){
                        tcps.inc_sent_duplicates();
                        sendPacket(packet, getSession(mParentStack.SL().get_socket(sock).genKey()));
                    }
                }catch(TransportLayerException e){
                    System.out.println(e.toString());
                }catch(LowLinkException e){
                    System.out.println(e.toString());
                }catch(CommunicationException e){
                    System.out.println(e.toString());
                }
            }
            if(nt>0){
                res = true;
            }
            else{
                TCP_session tcps = getSession(mSL.get_socket(sock).genKey());
                //TCP_session tcps = getSession(mParentStack.SL().get_socket(sock).genKey());
                if(tcps!=null){
                	if(packet!=null){
	                    tcps.inc_sent_duplicates();
	                    tcps.removeSegmentToResend(packet.get_sequence_number());
                	}
                    switch(nt){
                        case 0:{    //normal end, do nothing
                            break;
                        }
                        case -1:{
                            try{
                                Application app = mSL.get_socket(sock).app;
                                mSL.close(sock);
                                app.OnDisconnect(sock);
                            }catch(TransportLayerException e){
                                System.out.println(e.toString());
                            }
                            break;
                        }
                        case -2:{
                            try{
                                Application app = mSL.get_socket(sock).app;         //connect error
                                mSL.close(sock);
                                app.OnError(sock, -1);
                            }catch(TransportLayerException e){
                                System.out.println(e.toString());
                            }
                            break;
                        }
                        case -3:{   //acknowledgment have not recieved, close connection
                            // send RST segment
                            printLayerInfo("Sending RST segment");
                            try{
                                boolean flags[] = genFlags(Tcp.RST);
                                TCP_packet rstpack = getTCPPacket_tosend(sock, "", flags, packet.get_acknowledgment_number(), packet.get_sequence_number()+1);
                                sendPacket(rstpack, tcps);
                            }catch(TransportLayerException e){
                                System.out.println(e.toString());
                            }catch(LowLinkException e){
                                System.out.println(e.toString());
                            }catch(CommunicationException e){
                                System.out.println(e.toString());
                            }
                            // close seession
                            try{
                                Application app = mSL.get_socket(sock).app;     //disconnect on timeout, sending RST
                                mSL.close(sock);
                                app.OnError(sock, -2);
                            }catch(TransportLayerException e){
                                System.out.println(e.toString());
                            }
                            break;
                        }
                        case -4:{	//TIME_WAIT end, Application already closed
                            try{
                                mSL.close(sock);
                            }catch(TransportLayerException e){
                                System.out.println(e.toString());
                            }
                            break;
                        }
                        default: System.out.println("TCP error: unsupported resend time");
                    }
                }
            }
            return res;
        }
        
        public int getResendTime(){
            return nearest_resend;
        }
        public int getResendDelay(){
            return resend_delay;
        }
        
        public int compareTo(Object o){
            int diff = nearest_resend - ((Resender)o).nearest_resend;
            return (diff < -Tcp.MAXTIME/2 ? Tcp.MAXTIME-diff : diff);
        }
        
        public int nextTime(int cur_time){
            if(resend_time.isEmpty()){
            	nearest_resend = 0;
            	resend_delay = 0;
            }
            else{
                resend_delay = resend_time.poll().intValue();
                nearest_resend = resend_delay;
                if(nearest_resend>0){
                    nearest_resend += cur_time;
                    if(nearest_resend<0) nearest_resend+=Tcp.MAXTIME;
                }
            }
            return nearest_resend;
        }
    } 
    
    
    
    /*
     * TCP_HashTableElement.java
     *
     * Created on 25 November 2005, 17:33
     */                                 
    private static final int MAXTIME = 2000000000;
    
    private Hashtable<String,TCP_session> sessionTable = new Hashtable<String,TCP_session>();
    private Object sessionTable_lock = new Object();                         //for locking Hashtable sessionTable
    private ProtocolStack  mParentStack;
    private socketLayer mSL;
    private Timer timer;
    private PriorityQueue<Resender> tcp_timers = new PriorityQueue<Resender>();
    private int window_size = Tcp.DEFAULT_WINDOW_SIZE;                                           //tcp window-size for new sessions
    public final static int DEFAULT_WINDOW_SIZE = 10;
    private Object tcp_timers_lock = new Object();                         //for locking queue tcp_timers
    private int current_time = 0;
    private long nextTimerID = 0;
    private static final boolean USE_2MSL = true;
    private static final int MSL_TIME = 60000;
    private static final int resendtimes_SYN_ACK[] = {1500,3000,6000,12000,24000,28000,-2};
    private static final int resendtimes_SYN[] = {6000,24000,45000,-2};
    private static final int resendtimes_DEFAULT[] = {1500,3000,6000,12000,24000,48000,64000,64000,64000,64000,64000,64000,-3};
    private static final int resendtimes_NULL[] = {};
    private static final int resendtimes_2MSL[] = {2*MSL_TIME,-4};     // 2MSL
    private static final int resendtimes_EXIT[] = {75000,-1};     // non-RFC exit
    private static final int resendtimes_SYNC_CLOSE[] = {75000, -1};    // synchronous closing
//    private static final int MSL_TIME = 7500;
//    private static final int resendtimes_SYN_ACK[] = {1000,1000,1000,1000,1000,1000,-2};
//    private static final int resendtimes_SYN[] = {1000,1000,1000,1000,1000,1000,1000,-2};
//    private static final int resendtimes_DEFAULT[] = {1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,1000,-3};
//    private static final int resendtimes_NULL[] = {};
//    private static final int resendtimes_2MSL[] = {2*Tcp.MSL_TIME,-4};     // 2MSL
//    private static final int resendtimes_EXIT[] = {15000,-1};     // non-RFC exit
//    private static final int resendtimes_SYNC_CLOSE[] = {15000, -1};    // synchronous closing
            
    
    /*statistic block*/
    private int received_segments=0; //counter inc when a segment is received
    private int sent_segments=0; //counter inc when a segment is sent
    private int sent_ACK=0; //counter inc when an ACK is sent
    private int received_duplicates=0;//counter inc when a duplicate of a received segment is received again
    private int sent_duplicates=0; //counter inc when a duplicate of a segment is resent
    /*end of statistic block*/
    
    private static final int PORT_QUANT=55535;
    private static final int PORT_START_NUMBER=10000;
    /**
     * Used in timer
     */
    private static final long TCP_TIMER_DELAY = 500;
    
//    private static final long TCP_SERVER_TIME = 1000; //msec   //server closing idle connection time
//    private static final long TCP_CONNECT_SERVER_TIME = 250; //msec   //server closing idle connection time
//    private static final long TCP_SENDER_TIME = 200; //msec time to resend all segments in SegmentsToresend queue
//    private static final long TCP_CONNECT_TIME = 200; //msec time to resend all segments in SegmentsToresend queue when connecting
    
//    private int timerid = 0;
    
    private static final int URG_flag = 0;
    private static final int ACK_flag = 1;
    private static final int PSH_flag = 2;
    private static final int RST_flag = 3;
    private static final int SYN_flag = 4;
    private static final int FIN_flag = 5;
            
    private static final int NOFLAGS = 0;
    private static final int URG = 1;
    private static final int ACK = 2;
    private static final int PSH = 4;
    private static final int RST = 8;
    private static final int SYN = 16;
    private static final int FIN = 32;
    
    /**
     * This method assigns the ParentStack
     * @author gift (sourceforge.net user)
     * @param inParentStack protocol stack
     * @version v0.20
     */
    public Tcp(ProtocolStack inParentStack, socketLayer inSL)
    {
        mParentStack = inParentStack;
        mSL = inSL;
        timer=new Timer();
        timer.schedule(new TCPTask(),Tcp.TCP_TIMER_DELAY,Tcp.TCP_TIMER_DELAY);
    }
    
    protected void finalize(){
        timer.cancel();
        timer = null;
    }

    /**
     * This method reserves LOCAL port number for an application in case reserved. Port number range: [PORT_START_NUMBER; PORT_START_NUMBER+PORT_QUANT]
     * if not found then we reserve a new number
     * @author gift (sourceforge.net user)
     * @param  application that will: take free port or take already occupied port (only by itself) or tell that all ports are busy :(
     * @param inDestIPAddress destination IP address
     * @param indestPort destination port number
     * @return int port number that has been reserved for application
     * @exception TransportLayerException in several cases
     * @version v0.10
     * @see TransportLayerException
     */
    public void reserveFreePort(int sock) throws TransportLayerException {
        String sessionID;     
        boolean Found=false;
        Enumeration<String> LocalSessions = getSessionKeys();
        TCP_session Elm;

        // check if socket already busy
        while ( (LocalSessions.hasMoreElements())  && !(Found) )
        {                                  
            sessionID = LocalSessions.nextElement();                
            Elm = getSession(sessionID);                
            if ( sock == Elm.sock && mSL.get_socket(Elm.sock).open_state )  
            {
                Found=true;
                throw new TransportLayerException("TCP Error: socket is already busy (have port)!");                    
            }
        }

        //in case we have not found socket in hash table.....
        if (!Found)
        {
         //lets try to reserve any free port
            boolean Reserved=false;

            for(int attempt=0; attempt<2 && !Reserved; attempt++){
            	int start=0;
            	int end=Tcp.PORT_QUANT/2;
            	if(attempt==1){
            		start=Tcp.PORT_START_NUMBER;
                	end=Tcp.PORT_START_NUMBER+Tcp.PORT_QUANT;
            	}
	            for(int i=start; i<end && !Reserved; i++)
	            {
	            	int number=i;
	            	if(attempt==0) number=(int)Math.floor(Math.random()*Tcp.PORT_QUANT+Tcp.PORT_START_NUMBER);
	                Elm = getSession(jnSocket.genTCPkey(number,"0.0.0.0",0));
	                if (Elm == null)  //free port
	                {
	                    Reserved=true;
	                    mSL.get_socket(sock).src_port = number;
	                    addSession(jnSocket.genTCPkey(number,"0.0.0.0",0), sock);
	                    printLayerInfo("Local port " + number + " reserved for client app.");
	                }
	            }
            }

           if (!Reserved)  //all ports are busy :(
           {
             printLayerInfo("TCP Error: all ports are busy! Cannot reserve port for socket!");   
             throw new TransportLayerException("TCP Error: all ports are busy! Cannot reserve port for socket!");   
           }
        }
    }
    
    
    // bind port to socket
    public void bindPort(int sock_num, int in_Port) throws TransportLayerException
    {
         if(!mSL.get_socket(sock_num).open_state){
             if (in_Port>0 && in_Port<=65535){                            
                //create such a record in hashtable
                mSL.get_socket(sock_num).src_port = in_Port;
                printLayerInfo("Local port " + in_Port + " binded.");
             }
             else if(in_Port == 0){
                 reserveFreePort(sock_num);
             }
             else {
                   printLayerInfo("TCP error: can not bind to port "+ in_Port +"! Use port range from 1 to 65535 to listen to.");
                   throw new TransportLayerException("TCP error: can not bind to port "+ in_Port +"! Use port range from 1 to 65535 to listen to.");
             }
         }
         else{
             if (mSL.get_socket(sock_num).src_port==in_Port){
                printLayerInfo("TCP error: can not double bind to port "+ in_Port +"! Server is already listening to this port");
                throw new TransportLayerException("TCP error: can not bind listen to port "+ in_Port +"! Server is already listening to this port");
            }
            else{
                printLayerInfo("TCP error: can not bind to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);      
                throw new TransportLayerException("TCP error: can not bind to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);          
            }
         }
    }
    
        // bind port to socket
    public void listen(int sock_num) throws TransportLayerException
    {   
        int in_Port = mSL.get_socket(sock_num).src_port;
        if(  in_Port != 0 ) {
            if (in_Port>0 && in_Port<=65535){
                if(!mSL.get_socket(sock_num).open_state){
                    TCP_session tcps = addSession(jnSocket.genTCPkey(in_Port,"0.0.0.0",0), sock_num);
                    tcps.setState(TCP_session.LISTEN);
                    printLayerInfo("Local port " + in_Port + " is listening.");
                }
                else{
                    if (mSL.get_socket(sock_num).src_port==in_Port){
                        printLayerInfo("TCP error: can not double listen to port "+ in_Port +"! Server is already listening to this port");
                        throw new TransportLayerException("error: can not double listen to port "+ in_Port +"! Server is already listening to this port");
                    }
                    else{
                        printLayerInfo("TCP error: can not listen to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);      
                        throw new TransportLayerException("TCP error: can not listen to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);          
                    }
                } 
            } else {
                throw new TransportLayerException("TCP error: can not listen to port "+ in_Port +"! Use port range from 1 to 65535 to listen to.");
            }
        } else
        {
            throw new TransportLayerException("TCP error: can not listen to port 0! Use bind before to call listen!");
        }
    }
    
    /**
    * This method connects to the other side
    * NOTE: call this method from application to set up the TCP connection
    * @author gift (sourceforge.net user)  
    * @param application the application that sets the connection
    * @param inDestIPAddress destination IP address
    * @param indestPort destination port number 
    * @return int port number that has been reserved for application
    * @exception CommunicationException
    * @exception LowLinkException
    * @exception TransportLayerException
    * @version v0.10
    * @see CommunicationException
    * @see LowLinkException
    * @see TransportLayerException
    */
    public boolean connect(int sock, String dstIP, int dstPort) throws LowLinkException, CommunicationException, TransportLayerException
    {
        //int CONNECTION_DELAY_EXAM = (int)TCP_TIMER_DELAY/4;    //examination delay is 10ms, but may be any number
        boolean flags[] = genFlags(Tcp.SYN);
        boolean connected = false;
        
        if(IPV4Address.validateDecIP(dstIP)){
            mSL.get_socket(sock).src_IP = mParentStack.getSrcIP();
            reserveFreePort(sock);
            TCP_session Elm = addSession(mSL.get_socket(sock).genKey(), sock);
            Elm.setState(TCP_session.SYN_SENT);

            if (sendTCPSegment(sock, "", flags, -1, Tcp.resendtimes_SYN)) 
            {
                connected = true;
            }
            else{
                Elm.setState(TCP_session.CLOSED);
            }
        }
        else{
            throw new CommunicationException("Invalid IP address: " + dstIP);
        }
        return connected;
    }
    
    /**
    * This method connects to the other side
    * NOTE: call this method from application to set up the TCP connection
    * @author gift (sourceforge.net user)  
    * @param application the application that sets the connection
    * @param inDestIPAddress destination IP address
    * @param indestPort destination port number 
    * @return int port number that has been reserved for application
    * @exception CommunicationException
    * @exception LowLinkException
    * @exception TransportLayerException
    * @version v0.10
    * @see CommunicationException
    * @see LowLinkException
    * @see TransportLayerException
    */
    public boolean disconnect(int sock) throws LowLinkException, CommunicationException, TransportLayerException
    {
        TCP_session Elm = getSession(mSL.get_socket(sock).genKey());
        
        if(Elm != null){
            Elm.setState(TCP_session.FIN_WAIT_1);
            boolean flags[] = genFlags(Tcp.FIN);
            sendTCPSegment(Elm.getSocket(), "", flags, -1, Tcp.resendtimes_DEFAULT);
        }
        else {  
            throw new TransportLayerException("TCP Error: no session exists for socket '"+sock+"'");     
        }
        return true;
    }
    
    /**
     * This method closes the TCP port for an application
     * @author gift (sourceforge.net user)
     * @param application application that uses port
     * @return Nothing
     * @exception TransportLayerException
     * @version v0.10
     * @see TransportLayerException
     */
    public void closePort(int sock) throws TransportLayerException
    { 
        int portToClose=mSL.get_socket(sock).src_port; 
        
        if(portToClose!=0){
            TCP_session mainses = getSession(jnSocket.genTCPkey(portToClose,"0.0.0.0",0));
            if(mainses != null && mainses.getSocket() == sock){     // remove listening session on client side only
                removeSession(jnSocket.genTCPkey(portToClose,"0.0.0.0",0));
            }
            TCP_session Elm = getSession(mSL.get_socket(sock).genKey());
            if(Elm != null){
                Enumeration<Integer> segs = Elm.getAllSegmentToResendNumbers();
                while(segs.hasMoreElements()){
                    int segment = segs.nextElement().intValue();
                    long tid = Elm.removeSegmentToResend(segment);
                    removeTimer(tid);
                }
                removeSession(mSL.get_socket(sock).genKey());
            }
            printLayerInfo("Local port " + portToClose + " closed.");
        }
        else
            throw new TransportLayerException("TCP Error: port "+ portToClose +" is not being LISTENED.");
    }
    
    public void sendTCPData(int sock, String data) throws TransportLayerException, LowLinkException, CommunicationException
    {
        if(data.length()>0){
            boolean flags[] = genFlags(Tcp.NOFLAGS);
            sendTCPSegment(sock, data, flags, -1, Tcp.resendtimes_DEFAULT);
        }
    }
    
    /**
    * This method sends the TCP segments with certain flags
    * NOTE: DO <b>NOT</b> CALL this method from application use only <i>SendTCP(Object application, String inTCPMessage)</i> in the application to send TCP segments   
    * @author gift (sourceforge.net user)  
    * @param application the application that sends the message
    * @param inTCPMessage TCP message to send
    * @param flags[] 6 flags to set in segment
    * @param acknow_num int number of TCP segment that we want to confirm (-1 if we do not send ACK segment)
    * @return Nothing
    * @exception CommunicationException
    * @exception LowLinkException
    * @version v0.40
    * @see CommunicationException
    * @see LowLinkException
    */
        
    private boolean sendTCPSegment(int sock, String inTCPMessage, boolean flags[], int acknow_num, int resendtimes[]) throws TransportLayerException, LowLinkException, CommunicationException
    {
        TCP_packet tosend = getTCPPacket_tosend(sock,inTCPMessage,flags,acknow_num, -1);

        jnSocket jnsock = mSL.get_socket(sock);
        TCP_session Elm = getSession(jnsock.genKey());

        if((flags[Tcp.ACK_flag] && !flags[Tcp.SYN_flag] && !flags[Tcp.FIN_flag])|| flags[Tcp.RST_flag]){
        	sendPacket(tosend, Elm);
        }
        else{
        	Elm.addSendingSegment(tosend, resendtimes);
            trySendSegments(Elm);
        }
                
        return true;
    }
    
    private boolean trySendSegments(TCP_session Elm) throws TransportLayerException, LowLinkException, CommunicationException
    {
        boolean sended_at_last_one_packet = false;
        int sock = Elm.getSocket();
        while(Elm.hasNextSendingSegment()){
            
            TCP_session.TCPPacketTimesPair sendpair = Elm.getNextSendingSegment();
            TCP_packet tosend = sendpair.packet;
            if(sendpair.resendtimes.length > 0){
                long tid = createTimer(sock, (TCP_packet)tosend.clone(), sendpair.resendtimes);
                Elm.addSegmentToResend(tosend.get_sequence_number(),tid);
            }

            sendPacket(tosend, Elm);
            sended_at_last_one_packet = true;
        }
        return sended_at_last_one_packet;
    }
    
    private void sendPacket(TCP_packet packet, TCP_session Elm) throws TransportLayerException, LowLinkException, CommunicationException
    {
        String pack_src = packet.getSourceIPAddress()+":"+packet.get_srcPort();
        String pack_dst = packet.getDestIPAddress()+":"+packet.get_destPort();
        String pack_flags = (packet.get_ACK_flag()?"ACK,":"")+(packet.get_SYN_flag()?"SYN,":"")+(packet.get_FIN_flag()?"FIN,":"")+(packet.get_RST_flag()?"RST":"");
        System.out.println("||  <<< TCP  send pack: src="+pack_src+" dst="+pack_dst+" flags="+pack_flags+" seq="+packet.get_sequence_number()+" ack="+packet.get_acknowledgment_number()+" data='"+packet.getTCP_message()+"'");
        try{
            System.out.println("||  <<<< Host="+mParentStack.getHostName()+" IP="+mParentStack.getSrcIP()+" sock="+Elm.getSocket()+" state="+Elm.getStateString());
        }catch(NullPointerException e){
            System.out.println(e.getMessage());
        }
        
        String resend_mes = "";
        long tid = Elm.getTimerSegmentToResend(packet.get_sequence_number());
        Resender tmr = getTimer(tid);
        if(tmr!=null){
        	int nextrt = tmr.getResendDelay();
        	if(nextrt>0){
        		resend_mes = " Next retransmission on packet loss in "+(nextrt/1000.0)+"s.";
        	}
        }
        printLayerInfo("TCP packet send to "+pack_dst+" flags="+pack_flags+" seq="+packet.get_sequence_number()+" ack="+packet.get_acknowledgment_number()+" data='"+packet.getTCP_message()+"'." + resend_mes);

//        if(packet.get_acknowledgment_number()!=Elm.getNextSequenceNumber()){
//            try{
//                throw new Exception();
//            }
//            catch(Exception e){
//                e.printStackTrace();
//            }
//        }
        
        IncSentSegmentsNumber(1);
        Elm.inc_sent_segments();
        if(packet.get_ACK_flag()){
            IncSentACKSegmentsNumber(1);
            Elm.inc_sent_ACK();
        }
        mParentStack.sendPacket(packet);
    }
    
        /**
     * This method returns the TCP packet to send
     * and prints out a message to the layer info.
     * @author gift (sourceforge.net user)
     * @param sock socket for sending
     * @param inMessage TCP message to send
     * @param flags[] 6 flags to set in segment
     * @param acknow_num int ack number of the segment we want to send
     * @param seq_num int sequence number of the segment we want to send, -1 for autonumeration. If seq_num < Elm.seq_number, Elm.seq_number don't increment
     * @return TCP_packet to send in Network Layer
     * @exception TransportLayerException
     * @version v0.30
     * @see TransportLayerException
     */
    
    private TCP_packet getTCPPacket_tosend(int sock, String inMessage, boolean flags[], int acknow_num, int seq_num) throws TransportLayerException{
        jnSocket jnsock = mSL.get_socket(sock);
        String srcIP = jnsock.src_IP;
        int srcPort = jnsock.src_port;
        String dstIP = jnsock.dst_IP;
        int dstPort = jnsock.dst_port;
        TCP_session Elm = getSession(jnsock.genKey());

        TCP_packet tosend = new TCP_packet(dstIP,srcIP,dstPort,srcPort);
        try{
            if(seq_num == -1 || seq_num > Elm.seq_number) seq_num = Elm.seq_number;
            if(acknow_num==-1) acknow_num = Elm.getLastACK();

            //now we set all the flags
            /* URG, ACK, PSH, RST, SYN, FIN */
            tosend.set_URG_flag(flags[Tcp.URG_flag]);
            tosend.set_ACK_flag(flags[Tcp.ACK_flag]);
            tosend.set_PSH_flag(flags[Tcp.PSH_flag]);
            tosend.set_RST_flag(flags[Tcp.RST_flag]);
            tosend.set_SYN_flag(flags[Tcp.SYN_flag]);
            tosend.set_FIN_flag(flags[Tcp.FIN_flag]);
            tosend.set_sequence_number(seq_num);
            tosend.set_acknowledgment_number(acknow_num);
            tosend.setTCP_message(inMessage);

            //counters inc
            Elm.inc_sent_segments();
            if(seq_num == Elm.seq_number && (!(flags[Tcp.ACK_flag] || flags[Tcp.RST_flag]) || flags[Tcp.SYN_flag] || flags[Tcp.FIN_flag]))
                Elm.seq_number++;

            if (!(flags[Tcp.SYN_flag] && acknow_num==0) && flags[Tcp.ACK_flag]) Elm.inc_sent_ACK(); //inc in case this is an ACK-segment (this is statistic)
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        
        
//            this.total_sent++; //overall stats
//            IF NOT ACK then push a copy into Hashtable
//            if (!flags[1] || a_num<=1) Elm.SegmentsToResend.put(new Integer(s_num), tosend_clone);
//            if (!flags[1]) Elm.SegmentsToResend.put(new Integer(s_num), tosend_clone);
//            if (flags[4]) reps=5;
//            if (a_num==0 && flags[4]) //first SYN packet was sent => now he have to change port status to busy, as we will get reply to this port
//            {
//                Elm.PortStatus=2;
//                Elm.connectedtoIP=inDestIPAddress;
//                Elm.connectedtoPort=indestPort;
//                Elm.ApplicationStatus=1; //is being connected
//            }
//            if (flags[5]) Elm.isFIN_sent=true;
//            TCPsetTimer(Elm,reps); ///!!!!!!!!!!!!!
//            Elm=null;
//            //Create Layer info
//            LayerInfo TCP_Info = new LayerInfo(getClass().getName());
//            TCP_Info.setObjectName(mParentStack.getParentNodeName());
//            TCP_Info.setDataType("TCP Protocol");
//            TCP_Info.setLayer("Transport");
//            String s=new String();
//            if (flags[4]) s="Created TCP SYN-packet for " + inDestIPAddress + ":" + indestPort +"." + "(SEQ = " + s_num + "  ACK = " + a_num +")";
//            if (flags[5]) s="Created TCP FIN-packet for " + inDestIPAddress + ":" + indestPort +"." + "(SEQ = " + s_num + "  ACK = " + a_num +")";
//            if (!(flags[4] || flags[5])) s="Created TCP data packet for " + inDestIPAddress + ":" + indestPort +"." + "(SEQ = " + s_num + "  ACK = " + a_num +")";
//            if (!(flags[4] || flags[5]) && flags[1]) s="Created TCP acknowledgement packet for " + inDestIPAddress + ":" + indestPort +"." + "(SEQ = " + s_num + "  ACK = " + a_num +")";
//            TCP_Info.setDescription(s);
//            Simulation.addLayerInfo(TCP_Info);
            
            return tosend;
    }
    
    
    
    public void receiveTCPPacket( TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException {
        //test: check sum of UDP packet	                    
            if (inPacket.getCheck_Sum() == 1)
            {
                // test if this packet is for a local Address.
                if(mParentStack.isInternalIP(inPacket.getDestIPAddress())){
                    
                    String pack_src = inPacket.getSourceIPAddress()+":"+inPacket.get_srcPort();
                    String pack_dst = inPacket.getDestIPAddress()+":"+inPacket.get_destPort();
                    String pack_flags = (inPacket.get_ACK_flag()?"ACK,":"")+(inPacket.get_SYN_flag()?"SYN,":"")+(inPacket.get_FIN_flag()?"FIN,":"")+(inPacket.get_RST_flag()?"RST":"");
                    System.out.println("||  >>> TCP  recv pack: src="+pack_src+" dst="+pack_dst+" flags="+pack_flags+" seq="+inPacket.get_sequence_number()+" ack="+inPacket.get_acknowledgment_number()+" data='"+inPacket.getTCP_message()+"'");
        
                    // test if TCP session exists on this NeworkLayerDevice
                    TCP_session Elm;
                    Elm = getSession(jnSocket.genTCPkey(inPacket.get_destPort(), inPacket.getSourceIPAddress(), inPacket.get_srcPort()));
                    if(Elm == null && inPacket.get_SYN_flag()){
                        Elm = getSession(jnSocket.genTCPkey(inPacket.get_destPort(), "0.0.0.0", 0));
                        if(Elm != null){
                            int sock = mSL.accept(Elm.getSocket(), inPacket.getSourceIPAddress(), inPacket.get_srcPort());
                            Elm = addSession(jnSocket.genTCPkey(inPacket.get_destPort(), inPacket.getSourceIPAddress(), inPacket.get_srcPort()), sock);
                            Elm.setState(TCP_session.LISTEN);
                        }
                        else{
                            //printLayerInfo(">>> Session not existed: "+jnSocket.genTCPkey(inPacket.get_destPort(), "0.0.0.0", 0));
                        }
                    }
                    if (Elm !=null)
                    {
                        //int sock = Elm.getSocket();
                        if(!inPacket.get_ACK_flag() || inPacket.get_acknowledgment_number() > Elm.getLastACK() || inPacket.get_SYN_flag() || inPacket.get_FIN_flag()){
                            if((inPacket.get_ACK_flag() && (inPacket.get_SYN_flag() || inPacket.get_FIN_flag() || inPacket.get_RST_flag())) || !inPacket.get_ACK_flag()) {
                                IncReceivedSegmentsNumber(1);
                                Elm.inc_received_segments();
                            }
                            if(inPacket.get_ACK_flag() && inPacket.get_acknowledgment_number() > Elm.getLastACK()){
                                Enumeration<Integer> acks = Elm.getAllSegmentToResendNumbers();
                                long tid;
                                int lack = inPacket.get_acknowledgment_number();
                                int cack;
                                while(acks.hasMoreElements()){
                                    cack = acks.nextElement().intValue();
                                    if(cack<lack){
                                        tid = Elm.removeSegmentToResend(cack);
                                        removeTimer(tid);
                                    }
                                }
                                Elm.setLastACK(inPacket.get_acknowledgment_number());
                                trySendSegments(Elm);
                            }
                            if(inPacket.get_RST_flag() && !(inPacket.get_ACK_flag() || inPacket.get_SYN_flag() || inPacket.get_FIN_flag())){ // add chech flags ONLY RST 
                                Application app = mSL.get_socket(Elm.getSocket()).app;
                                mSL.close(Elm.getSocket());
                                app.OnDisconnect(Elm.getSocket());
                            }
                            else{
                                System.out.println("||  >>>> Host="+mParentStack.getHostName()+" IP="+mParentStack.getSrcIP()+" sock="+Elm.getSocket()+" state="+Elm.getStateString());
                                printLayerInfo("TCP packet received from "+ pack_src +" flags: "+pack_flags+" message: \"" +inPacket.getTCP_message() + "\".");

                                switch(Elm.getState()){
                                    case TCP_session.CLOSED: recv_CLOSED(Elm, inPacket); break;
                                    case TCP_session.LISTEN: recv_LISTEN(Elm, inPacket); break;
                                    case TCP_session.SYN_SENT: recv_SYN_SENT(Elm, inPacket); break;
                                    case TCP_session.SYN_RCVD: recv_SYN_RCVD(Elm, inPacket); break;
                                    case TCP_session.ESTABLISHED: recv_ESTABLISHED(Elm, inPacket); break;
                                    case TCP_session.CLOSE_WAIT: recv_CLOSE_WAIT(Elm, inPacket); break;
                                    case TCP_session.LAST_ACK: recv_LAST_ACK(Elm, inPacket); break;
                                    case TCP_session.FIN_WAIT_1: recv_FIN_WAIT_1(Elm, inPacket); break;
                                    case TCP_session.FIN_WAIT_2: recv_FIN_WAIT_2(Elm, inPacket); break;
                                    case TCP_session.CLOSING: recv_CLOSING(Elm, inPacket); break;
                                    case TCP_session.TIME_WAIT: recv_TIME_WAIT(Elm, inPacket); break;
                                    default:
                                }
                            }
                        }
                    }
                    else {  
                        //throw new TransportLayerPortException("UDP Error: port " + inPacket.get_destPort() + " does not exist. Host \"" + mParentStack.getParentNodeName()+"\".");
                        // send RST segment
//                        boolean flags[] = genFlags(RST);
//                        TCP_packet rstpack = new TCP_packet(inPacket.getDestIPAddress(), inPacket.getSourceIPAddress(), inPacket.get_destPort(), inPacket.get_srcPort());
//                        rstpack.set_RST_flag(true);
//                        rstpack.set_sequence_number(0);
//                        rstpack.set_acknowledgment_number(inPacket.get_sequence_number()+1);
//                        rstpack.setTCP_message("");
//                        mParentStack.sendPacket(rstpack);
                        printLayerInfo("TCP packet received from "+ pack_src +" flags: "+pack_flags+" message: \"" +inPacket.getTCP_message() + "\".");
                        throw new TransportLayerPortException("TCP Error: no application binded port "+inPacket.get_destPort() +" on host \""+ mParentStack.getParentNodeName()+"\"!");     
                    }
              }
           } else {
            throw new TransportLayerException("TCP Error: incorrect checksum on receiving!"); //comment this line if you have implemented check sum tests :)
          } 
    }
    
    public int getWindowSize(){
        return window_size;
    }
    
    public void setWindowSize(int windowSize){
        if(windowSize>0){
            window_size = windowSize;
            Enumeration<String> keys = getSessionKeys();
            while(keys.hasMoreElements()){
                String key = keys.nextElement();
                getSession(key).setWindowSize(windowSize);
            }
        }
    }
    
    private void recv_CLOSED(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        // hm... it is bug!
        throw new TransportLayerException("Session exists, but port "+ mSL.get_socket(Elm.getSocket()).dst_port +" not binded! It is scary bug!");
    }
    
    private void recv_LISTEN(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_SYN_flag()){
            Elm.setState(TCP_session.SYN_RCVD);
            boolean flags[] = genFlags(Tcp.ACK | Tcp.SYN);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_SYN_ACK);
        }
        else
            throw new TransportLayerException("TCP: recv_LISTEN: unexpected packet");
    }
    
    private void recv_SYN_SENT(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_SYN_flag() && inPacket.get_ACK_flag()){
            Elm.setState(TCP_session.ESTABLISHED);
            boolean flags[] = genFlags(Tcp.ACK);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_NULL);
            //mSL.get_socket(Elm.getSocket()).app.OnConnect(Elm.getSocket());
            jnSocket jns = mSL.get_socket(Elm.getSocket());
            jns.app.OnConnect(Elm.getSocket());
        }
        else
            throw new TransportLayerException("TCP: recv_SYN_SENT: unexpected packet");
    }
    
    private void recv_SYN_RCVD(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_ACK_flag() && !(inPacket.get_FIN_flag() || inPacket.get_SYN_flag())){
            Elm.setState(TCP_session.ESTABLISHED);
            mSL.get_socket(Elm.getSocket()).app.OnConnect(Elm.getSocket());
            processSegment(Elm, null);  // pass already received segments to application
        }
        else if(inPacket.get_SYN_flag() && !(inPacket.get_ACK_flag() || inPacket.get_FIN_flag())){
            IncReceivedDuplicatesNumber(1);
            Elm.inc_received_duplicates();
//            boolean flags[] = genFlags(ACK | SYN);
//            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, resendtimes_SYN_ACK);
        }
        else if(!(inPacket.get_ACK_flag() || inPacket.get_SYN_flag() || inPacket.get_FIN_flag())){
            if(!Elm.addReceivedSegment(inPacket)){     // save received segments, although connection have not established yet
                IncReceivedDuplicatesNumber(1);
                Elm.inc_received_duplicates();
            }
        }
        else
            throw new TransportLayerException("TCP: recv_SYN_RCVD: unexpected packet");
    }
    
    private void recv_ESTABLISHED(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_FIN_flag() && !inPacket.get_ACK_flag()){
            Elm.setState(TCP_session.CLOSE_WAIT);
            boolean flags[] = genFlags(Tcp.ACK);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_NULL);
            
            Elm.setState(TCP_session.LAST_ACK);
            flags = genFlags(Tcp.FIN);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_DEFAULT);
        }
        else if(inPacket.get_SYN_flag() && inPacket.get_ACK_flag()){
            IncReceivedDuplicatesNumber(1);
            Elm.inc_received_duplicates();
            boolean flags[] = genFlags(Tcp.ACK);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_NULL);
        }
        else if(inPacket.get_ACK_flag()){
            //do nothing, segment have been already processed
        }
        else if(!(inPacket.get_ACK_flag() || inPacket.get_SYN_flag() || inPacket.get_FIN_flag())){
            processSegment(Elm, inPacket);
        }
        else
            throw new TransportLayerException("TCP: recv_ESTABLISHED: unexpected packet");
    }
    
    private void recv_CLOSE_WAIT(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
    	throw new TransportLayerException("TCP: recv_CLOSE_WAIT: unexpected packet");
    }
    
    private void recv_LAST_ACK(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_ACK_flag()){
            Application app = mSL.get_socket(Elm.getSocket()).app;
            mSL.close(Elm.getSocket());
            app.OnDisconnect(Elm.getSocket());
        }
        else if(inPacket.get_FIN_flag()){
            IncReceivedDuplicatesNumber(1);
            Elm.inc_received_duplicates();
            boolean flags[] = genFlags(Tcp.ACK);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_NULL);
        }
        else
            throw new TransportLayerException("TCP: recv_LAST_ACK: unexpected packet");
    }
    
    private void recv_FIN_WAIT_1(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_ACK_flag() && inPacket.get_FIN_flag()){
            Elm.setState(TCP_session.TIME_WAIT);
            if(Tcp.USE_2MSL){
                Elm.last_timer = createTimer(Elm.getSocket(), null, Tcp.resendtimes_2MSL);
                printLayerInfo("Closing port "+mSL.get_socket(Elm.getSocket()).src_port+" in "+(Tcp.resendtimes_2MSL[0]/1000.0)+"s.");
            }
            boolean flags[] = genFlags(Tcp.ACK);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_NULL);
            Application app = mSL.get_socket(Elm.getSocket()).app;
            if(!Tcp.USE_2MSL){
                mSL.close(Elm.getSocket());
            }
            app.OnDisconnect(Elm.getSocket());
        }
        else if(inPacket.get_ACK_flag()){
            Elm.setState(TCP_session.FIN_WAIT_2);
            Elm.last_timer = createTimer(Elm.getSocket(), null, Tcp.resendtimes_EXIT);
            printLayerInfo("Closing port "+mSL.get_socket(Elm.getSocket()).src_port+" if FIN will not have received in "+(Tcp.resendtimes_EXIT[0]/1000.0)+"s.");
        }
        else if(inPacket.get_FIN_flag()){
            Elm.setState(TCP_session.CLOSING);
            Elm.last_timer = createTimer(Elm.getSocket(), null, Tcp.resendtimes_SYNC_CLOSE);
            printLayerInfo("Closing port "+mSL.get_socket(Elm.getSocket()).src_port+" if ACK will not have received in "+(Tcp.resendtimes_SYNC_CLOSE[0]/1000.0)+"s.");
            boolean flags[] = genFlags(Tcp.ACK);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_NULL);
        }
        else if(!(inPacket.get_ACK_flag() || inPacket.get_SYN_flag() || inPacket.get_FIN_flag())){
            processSegment(Elm, inPacket);
        }
        else
            throw new TransportLayerException("TCP: recv_FIN_WAIT_1: unexpected packet");
    }
    
    private void recv_FIN_WAIT_2(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_FIN_flag()){
            Elm.setState(TCP_session.TIME_WAIT);
            removeTimer(Elm.last_timer);
            if(Tcp.USE_2MSL){
                Elm.last_timer = createTimer(Elm.getSocket(), null, Tcp.resendtimes_2MSL);
                printLayerInfo("Closing port "+mSL.get_socket(Elm.getSocket()).src_port+" in "+(Tcp.resendtimes_2MSL[0]/1000.0)+"s.");
            }
            boolean flags[] = genFlags(Tcp.ACK);
            sendTCPSegment(Elm.getSocket(), "", flags, inPacket.get_sequence_number()+1, Tcp.resendtimes_NULL);
            Application app = mSL.get_socket(Elm.getSocket()).app;
            if(!Tcp.USE_2MSL){
                mSL.close(Elm.getSocket());
            }
            app.OnDisconnect(Elm.getSocket());
        }
        else if(!(inPacket.get_ACK_flag() || inPacket.get_SYN_flag() || inPacket.get_FIN_flag())){
            processSegment(Elm, inPacket);
        }
        else
            throw new TransportLayerException("TCP: recv_FIN_WAIT_2: unexpected packet");
    }
    
    private void recv_CLOSING(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        if(inPacket.get_ACK_flag()){
            Elm.setState(TCP_session.TIME_WAIT);
            removeTimer(Elm.last_timer);
            Application app = mSL.get_socket(Elm.getSocket()).app;
            if(Tcp.USE_2MSL){
                Elm.last_timer = createTimer(Elm.getSocket(), null, Tcp.resendtimes_2MSL);
                printLayerInfo("Closing port "+mSL.get_socket(Elm.getSocket()).src_port+" in "+(Tcp.resendtimes_2MSL[0]/1000.0)+"s.");
            }
            else{
                mSL.close(Elm.getSocket());
            }
            app.OnDisconnect(Elm.getSocket());
        }
        else
            throw new TransportLayerException("TCP: recv_CLOSING: unexpected packet");
    }
    
    private void recv_TIME_WAIT(TCP_session Elm, TCP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException, CommunicationException
    {
        throw new TransportLayerException("TCP: recv_TIME_WAIT: unexpected packet");
    }
    
    private void processSegment(TCP_session Elm, TCP_packet packet) throws LowLinkException, TransportLayerException, CommunicationException
    {
        if(packet!=null && !(packet.get_SYN_flag() || packet.get_FIN_flag())) {
            if(!Elm.addReceivedSegment(packet)){
                IncReceivedDuplicatesNumber(1);
                Elm.inc_received_duplicates();
            }
        }
        String out = "";
        int readySegments = 0;
        while(Elm.hasNextReceivedSegment()){
            TCP_packet tcpp = Elm.getNextReceivedSegment();
            out += tcpp.getTCP_message();
            readySegments = tcpp.get_sequence_number();
        }
        if(packet!=null){
            boolean flags[] = genFlags(Tcp.ACK);
            //sendTCPSegment(Elm.getSocket(), "", flags, readySegments+1, resendtimes_NULL);
            sendTCPSegment(Elm.getSocket(), "", flags, Elm.getNextSequenceNumber(), Tcp.resendtimes_NULL);
        }
        if(readySegments>0){
            mSL.recv(Elm.getSocket(), out);
        }
    }
    
    private long createTimer(int sock, TCP_packet pack, int times[]){
        Resender rsnd = new Resender(sock,pack,times);
        if(rsnd.start(current_time)){
            rsnd.timerID = nextTimerID++;
            synchronized(tcp_timers_lock){
                tcp_timers.add(rsnd);
                String ttt="";
                for(int i=0; i<times.length; i++){
                    ttt += times[i]+",";
                }
                //printLayerInfo(">>> Timer "+mParentStack.getHostName()+":"+rsnd.timerID+" was added {"+ttt+"}");
            }
        }
        else return -1;
        return rsnd.timerID;
    }
    
    private void removeTimer(long timerid){
        synchronized(tcp_timers_lock){
            Iterator<Resender> it = tcp_timers.iterator();
            boolean notFound = true;
            while(notFound && it.hasNext()){
                if(it.next().timerID == timerid){
                    notFound = false;
                    it.remove();
                    //printLayerInfo(">>> Timer "+mParentStack.getHostName()+":"+timerid+" was removed");
                }
            }
            if(notFound){
                printLayerInfo(">>> TCP error: timer "+mParentStack.getHostName()+":"+timerid+" removing failed");
                System.out.println(">>> TCP error: timer "+mParentStack.getHostName()+":"+timerid+" removing failed");
            }
        }
    }
    
    private Resender getTimer(long timerid){
        Resender tmr = null;
        synchronized(tcp_timers_lock){
            Iterator<Resender> it = tcp_timers.iterator();
            while(tmr==null && it.hasNext()){
            	Resender curtmr = it.next();
                if(curtmr.timerID == timerid){
                	tmr = curtmr;
                }
            }
        }
        return tmr;
    }
    
    private TCP_session addSession(String key, int sock){
        TCP_session tcps = new TCP_session(sock);
        tcps.setWindowSize(window_size);
        synchronized(sessionTable_lock){
            sessionTable.put(key, tcps);
        }
        //printLayerInfo(">>> Session added: "+key+" sock="+sock);
        return tcps;
    }
    
    private TCP_session removeSession(String key){
        TCP_session tcps;
        synchronized(sessionTable_lock){
            tcps = (TCP_session) sessionTable.remove(key);
        }
        //printLayerInfo(">>> Session removed: "+key);
        return tcps;
    }
    
    public TCP_session getSession(String key){
        TCP_session tcps;
        synchronized(sessionTable_lock){
            tcps = (TCP_session) sessionTable.get(key);
        }
        return tcps;
    }
    
    public Enumeration<String> getSessionKeys(){
        Enumeration<String> keys;
        synchronized(sessionTable_lock){
            keys = sessionTable.keys();
        }
        return keys;
    }
    
    public boolean[] genFlags(int flags){
        boolean f[] = {false, false, false, false, false, false};
        if((flags & Tcp.URG) == Tcp.URG) f[Tcp.URG_flag] = true;
        if((flags & Tcp.ACK) == Tcp.ACK) f[Tcp.ACK_flag] = true;
        if((flags & Tcp.PSH) == Tcp.PSH) f[Tcp.PSH_flag] = true;
        if((flags & Tcp.RST) == Tcp.RST) f[Tcp.RST_flag] = true;
        if((flags & Tcp.SYN) == Tcp.SYN) f[Tcp.SYN_flag] = true;
        if((flags & Tcp.FIN) == Tcp.FIN) f[Tcp.FIN_flag] = true;
        return f;
    }
    
    
    /*STATISTIC PART */
    
    /**
     * This method increments received segments counter
     * @author gift (sourceforge.net user)
     * @param numb the number to add
     * @return Nothing
     * @version v0.20
     */
    
    public void IncReceivedSegmentsNumber(int numb) {
        
        received_segments+=numb;
    }
    
    
    /**
     * This method increments sent segments counter
     * @author gift (sourceforge.net user)
     * @param numb the number to add
     * @return Nothing
     * @version v0.20
     */
    public void IncSentSegmentsNumber(int numb) {
        sent_segments+=numb;
    }
    
    /**
     * This method increments sent ACK segments counter
     * @author gift (sourceforge.net user)
     * @param numb the number to add
     * @return Nothing
     * @version v0.20
     */
    public void IncSentACKSegmentsNumber(int numb) {
        sent_ACK+=numb;
    }
    
    /**
     * This method increments received duplicates counter
     * @author gift (sourceforge.net user)
     * @param numb the number to add
     * @return Nothing
     * @version v0.20
     */
    public void IncReceivedDuplicatesNumber(int numb) {
        received_duplicates+=numb;
    }
    
    /**
     * This method increments sent duplicates counter
     * @author gift (sourceforge.net user)
     * @param numb the number to add
     * @return Nothing
     * @version v0.20
     */
    public void IncSentDuplicatesNumber(int numb) {
        sent_duplicates+=numb;
    }
              
    /**
     * This method returns the number of received segments
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return int the number of received segments
     * @version v0.20
     */
    public int GetReceivedSegmentsNumber() {
        return received_segments;
    }
    
    /**
     * This method returns the number of sent segments
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return int the number of sent segments
     * @version v0.20
     */
    public int GetSentSegmentsNumber() {
        return sent_segments;
    }
    
    /**
     * This method returns the number of sent ACK segments
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return int the number of sent ACK segments
     * @version v0.20
     */
    public int GetSentACKSegmentsNumber() {
        return sent_ACK;
    }
    
    /**
     * This method returns the number of received duplicates
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return int the number of received duplicates
     * @version v0.20
     */
    public int GetReceivedDuplicatesNumber() {
        return received_duplicates;
    }
    
    /**
     * This method returns the number of sent duplicates
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return int the number of sent duplicates
     * @version v0.20
     */
    public int GetSentDuplicatesNumber() {
        return sent_duplicates;
    }
    
    
    /**
     * This method resets TCP "receipt" counters
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return Nothing.
     * @version v0.20
     */
    public void ResetReceiptCounters() {
        received_duplicates=0;
        received_segments=0;
    }
    
    
    /**
     * This method resets TCP "" counters
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return Nothing.
     * @version v0.20
     */
    public void ResetSendingCounters() {
        sent_duplicates=0;
        sent_ACK=0;
        sent_segments=0;
    }
    
    /**
     * This method resets TCP counters
     * @author gift (sourceforge.net user)
     * @param Unused.
     * @return Nothing.
     * @version v0.20
     */
    public void ResetCounters() {
        ResetSendingCounters();
        ResetReceiptCounters();
    }
    
    protected void printLayerInfo(String s) {
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("TCP Protocol");
            protInfo.setLayer("Transport ");
            protInfo.setDescription(s);
            Simulation.addLayerInfo(protInfo);
    }
}

/*
 * TCP_session.java
 *
 * Created on 18 Сентябрь 2007 г., 18:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core.protocolsuite.tcp_ip;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author QweR
 */
public class TCP_session extends jnSession{
    
    public class TCPPacketTimesPair implements Comparable{
        public TCP_packet packet;
        public int resendtimes[];
        
        public TCPPacketTimesPair(TCP_packet pack, int times[]){
            packet = pack;
            resendtimes = new int[times.length];
            for(int i=0; i<times.length; i++)
                resendtimes[i] = times[i];
        }
        
        public int compareTo(Object o){
            return packet.compareTo(((TCPPacketTimesPair)o).packet);
        }
    }
   
    private int window_size=10; //2000;  //25
    
    /*statistic block*/
    private int received_segments=0; //counter inc when a segment is received
    private int sent_segments=0; //counter inc when a segment is sent
    private int sent_ACK=0; //counter inc when an ACK is sent
    private int received_duplicates=0;//counter inc when a duplicate of a received segment is received again
    private int sent_duplicates=0; //counter inc when a duplicate of a segment is resent
    /*end of statistic block*/
    
    public int seq_number = 0;
    private int next_pass_seq_num = -1;
    private int last_ack_number=0; //last sequence number value of the segment that has been passed upstairs
    private int state = TCP_session.CLOSED;
    public long last_timer = -1;
    
    public static final int CLOSED = 0;
    public static final int LISTEN = 1;
    public static final int SYN_SENT = 2;
    public static final int SYN_RCVD = 3;
    public static final int ESTABLISHED = 4;
    public static final int FIN_WAIT_1 = 5;
    public static final int FIN_WAIT_2 = 6;
    public static final int CLOSING = 7;
    public static final int TIME_WAIT = 8;
    public static final int CLOSE_WAIT = 9;
    public static final int LAST_ACK = 10;
            
    /**
     * Contains segments that will be resent in case no ACK for them will be received
     * <p>Key: Sequence number of the segment that we are sending</p>
     * <p>Object: timerID. timer with this timerID is resending packet</p>
     * <p>Aim of this hashtable: necessarily to have</p>
     */
    private Hashtable<Integer,Long> segmentsToResend = new Hashtable<Integer,Long>();
    
    private PriorityQueue<TCP_packet> receivedSegments = new PriorityQueue<TCP_packet>();
    
    private PriorityQueue<TCPPacketTimesPair> sendingSegments = new PriorityQueue<TCPPacketTimesPair>();
    
    
        /** 
         ******************************************************************
         *
         * Below variable was created by gift and not checked yet
         *
         ******************************************************************
         **/
        /**
         * Timer access flag
         */
//        public boolean busy = false;
//        
//        /**
//         * Contains received segments ONLY sequence_number for each segment is stored
//         * Integer type is used NOTE: Do remember about special "Integer" use by "Vector"
//         */
//        public Vector ReceivedSegments = new Vector();
//        
//        /**
//         * Here we store sent acknowledgements.
//         * <p>Key: Sequence number of the segment that we have received and acknowledged</p>
//         * <p>Object: Our ACK segment that we have received/<p>
//         * <p>Aim of this hashtable: if we receive a duplicate of the segment that we have received, we get this duplicate sequenece number
//         * then we get from this hashtable a copy of ACK segment we have sent and send it again.</p>
//         */
//        public Hashtable SentACKs = new Hashtable();
//        
//        /**
//         * Contains received acknowledgments from destination computer ONLY sequence_number for each ACK segment is stored
//         * Integer type is used NOTE: Do remember about special "Integer" use by "Vector"
//         */
//        public Vector ReceivedACKs = new Vector();
//        
//        /**
//         * Contains segments that have come in non consequential order
//         * maximun 200 segments can be stored
//         * <p>Key: Sequence number of the segment</p>
//         * <p>Object: The copy of the segment we have received</p>
//         * <p>Aim of this hashtable: necessarily to have, to pass segments upstairs in proper order</p>
//         */
//        public Hashtable OutputBuffer = new Hashtable(200);
//        /*end of database*/
//        
//        public boolean isFIN_sent=false;
//        public boolean isFIN_confirmed=false;
//        public boolean isPassive=false;
//       // public boolean isConnected=false;
//        /**
//         * 0 - no status; 1 - is being connected; 2 - is connected; 3 - connection error
//         */
//        public byte ApplicationStatus=0;
//        public boolean isServer=false;
//        public Timer timer=null; //our timer for this connection
//        public Timer Servertimer=null;
    
    
    
    
    /** Creates a new instance of TCP_session */
    public TCP_session(int sock) {
        super(sock);
    }
    
    public void addSegmentToResend(int segnum, long timerID){
        if(!segmentsToResend.containsKey(new Integer(segnum))){
            segmentsToResend.put(new Integer(segnum), new Long(timerID));
        }
        else
            System.out.println("TCP session error: it is not allowed to add duplicate segment");
    }
    
    public long removeSegmentToResend(int segnum){
        long timerID = -1;
        Integer sn = new Integer(segnum);
        if(segmentsToResend.containsKey(sn)){
            timerID = ((Long)segmentsToResend.get(sn)).longValue();
            segmentsToResend.remove(sn);
        }
        else
            System.out.println("TCP session error on remove: segment is not exists");
        return timerID;
    }
    
    public long getTimerSegmentToResend(int segnum){
        long timerID = -1;
        Integer sn = new Integer(segnum);
        if(segmentsToResend.containsKey(sn)){
            timerID = ((Long)segmentsToResend.get(sn)).longValue();
        }
        return timerID;
    }
    
    public Enumeration<Integer> getAllSegmentToResendNumbers(){
        return segmentsToResend.keys();
    }
    
    /**
     * Pop expected segment from buffer
     * @result null if expected segment have not received yet
     */
    public TCP_packet getNextReceivedSegment(){
        TCP_packet result = null;
        if(!receivedSegments.isEmpty() && ((TCP_packet)receivedSegments.peek()).get_sequence_number() == next_pass_seq_num){
            result = (TCP_packet)receivedSegments.poll();
            next_pass_seq_num++;
        }
        return result;
    }
    
    public boolean hasNextReceivedSegment(){
        boolean result = false;
        if(!receivedSegments.isEmpty() && ((TCP_packet)receivedSegments.peek()).get_sequence_number() == next_pass_seq_num){
            result = true;
        }
        return result;
    }
    
    /**
     *
     * @result true if segment have been added in buffer, false if segment have been already contained in buffer
     */
    public boolean addReceivedSegment(TCP_packet pack){
        boolean Found = false;
        if(pack.get_sequence_number() >= next_pass_seq_num){
            Iterator<TCP_packet> it = receivedSegments.iterator();
            while(it.hasNext() && !Found){
                TCP_packet p = it.next();
                Found = p.get_sequence_number() == pack.get_sequence_number();
            }
            if(!Found){
                receivedSegments.add(pack);
                if(next_pass_seq_num == -1)
                    next_pass_seq_num = pack.get_sequence_number();
            }
        }
        else{
            Found = true;
        }
        return !Found;
    }
    
     /**
     * Pop expected segment from buffer
     * @result null if expected segment have not received yet
     */
    public TCPPacketTimesPair getNextSendingSegment(){
        TCPPacketTimesPair result = null;
        if(!sendingSegments.isEmpty() && ((TCPPacketTimesPair)sendingSegments.peek()).packet.get_sequence_number() < getLastACK()+getWindowSize()){
            result = (TCPPacketTimesPair)sendingSegments.poll();
        }
        return result;
    }
    
    public boolean hasNextSendingSegment(){
        boolean result = false;
        if(!sendingSegments.isEmpty() && ((TCPPacketTimesPair)sendingSegments.peek()).packet.get_sequence_number() < getLastACK()+getWindowSize()){
            result = true;
        }
        return result;
    }
    
    /**
     *
     * @result true if segment have been added in buffer, false if segment have been already contained in buffer
     */
    public boolean addSendingSegment(TCP_packet pack, int resendtimes[]){
        sendingSegments.add(new TCPPacketTimesPair(pack, resendtimes));
        return true;
    }
    
    public int getState(){
        return state;
    }
    
    public String getStateString(){
        String out;
        switch(getState()){
            case CLOSED: out = "CLOSED"; break;
            case LISTEN: out = "LISTEN"; break;
            case SYN_SENT: out = "SYN_SENT"; break;
            case SYN_RCVD: out = "SYN_RCVD"; break;
            case ESTABLISHED: out = "ESTABLISHED"; break;
            case FIN_WAIT_1: out = "FIN_WAIT_1"; break;
            case FIN_WAIT_2: out = "FIN_WAIT_2"; break;
            case CLOSING: out = "CLOSING"; break;
            case TIME_WAIT: out = "TIME_WAIT"; break;
            case CLOSE_WAIT: out = "CLOSE_WAIT"; break;
            case LAST_ACK: out = "LAST_ACK"; break;
            default: out = "";
        }
        return out;
    }
    
    public void setState(int stt){
        if(stt >=0 && stt <= 10)
            state = stt;
        else
            System.out.println("TCP session error: unsupported state");
    }
    
    public int getLastACK(){
        return last_ack_number;
    }
    
    public void setLastACK(int acknum){
        if(last_ack_number <= acknum)
            last_ack_number = acknum;
    }
    
    public int getWindowSize(){
        return window_size;
    }
    
    public void setWindowSize(int windowSize){
        window_size = windowSize>0 ? windowSize : 0;
    }
    
    public int getNextSequenceNumber(){
        return next_pass_seq_num;
    }
    
    public void inc_received_segments(){
        received_segments++;
    }
    public void inc_sent_segments(){
        sent_segments++;
    }
    public void inc_sent_ACK(){
        sent_ACK++;
    }
    public void inc_received_duplicates(){
        received_duplicates++;
    }
    public void inc_sent_duplicates(){
        sent_duplicates++;
    }    
    public int get_received_segments(){
        return received_segments;
    }
    public int get_sent_segments(){
        return sent_segments;
    }
    public int get_sent_ACK(){
        return sent_ACK;
    }
    public int get_received_duplicates(){
        return received_duplicates;
    }
    public int get_sent_duplicates(){
        return sent_duplicates;
    }
    
}

package core.protocolsuite.tcp_ip;

//import java.io.Serializable;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.Vector;

import core.Error;

//import java.util.TimerTask;

/**
     * @author gift (sourceforge.net user)
     * @version v0.10, 25 Nov 2005
     */
    public class TCP_HashTableElement {
        public byte PortStatus=0; // 0 - free port; 1 - port is being listened to; 2 - busy.
        public Object application=null; //points to application that listens to this port | provided PortStatus==1
        public String connectedtoIP=""; //contains IP of the other connected computer | provided PortStatus==2
        public int connectedtoPort=0; //contains port number of the other connected computer | provided PortStatus==2
        /*statistic block*/
        public int received_segments=0; //counter inc when a segment is received
        public int sent_segments=0; //counter inc when a segment is sent
        public int sent_ACK=0; //counter inc when an ACK is sent
        public int received_duplicates=0;//counter inc when a duplicate of a received segment is received again
        public int sent_duplicates=0; //counter inc when a duplicate of a segment is resent
        /*end of statistic block*/
        public int seq_number=0;
        public int last_passed=-1; //last  sequence number value of the segment that has been passed upstairs
        public static int Element_id = 0;
        /*database*/
        
        /**
         * Timer access flag
         */
        public boolean busy = false;
        
        /**
         * Contains received segments ONLY sequence_number for each segment is stored
         * Integer type is used NOTE: Do remember about special "Integer" use by "Vector"
         */
        public Vector ReceivedSegments = new Vector();
        
        /**
         * Here we store sent acknowledgements.
         * <p>Key: Sequence number of the segment that we have received and acknowledged</p>
         * <p>Object: Our ACK segment that we have received/<p>
         * <p>Aim of this hashtable: if we receive a duplicate of the segment that we have received, we get this duplicate sequenece number
         * then we get from this hashtable a copy of ACK segment we have sent and send it again.</p>
         */
        public Hashtable SentACKs = new Hashtable();
        
        /**
         * Contains received acknowledgments from destination computer ONLY sequence_number for each ACK segment is stored
         * Integer type is used NOTE: Do remember about special "Integer" use by "Vector"
         */
        public Vector ReceivedACKs = new Vector();
        
        /**
         * Contains segments that will be resent in case no ACK for them will be received
         * <p>Key: Sequence number of the segment that we are sending</p>
         * <p>Object: The copy of the segment we have sent</p>
         * <p>Aim of this hashtable: necessarily to have</p>
         */
        public Hashtable SegmentsToResend = new Hashtable();
        
        /**
         * Contains segments that have come in non consequential order
         * maximun 200 segments can be stored
         * <p>Key: Sequence number of the segment</p>
         * <p>Object: The copy of the segment we have received</p>
         * <p>Aim of this hashtable: necessarily to have, to pass segments upstairs in proper order</p>
         */
        public Hashtable OutputBuffer = new Hashtable(200);
        /*end of database*/
        
        public boolean isFIN_sent=false;
        public boolean isFIN_confirmed=false;
        public boolean isPassive=false;
       // public boolean isConnected=false;
        /**
         * 0 - no status; 1 - is being connected; 2 - is connected; 3 - connection error
         */
        public byte ApplicationStatus=0;
        
        public boolean isServer=false;
        
        public Timer timer=null; //our timer for this connection
        
        public Timer Servertimer=null;
        
         /*
          * This method adds statistics to the TCP stack
          * @author gift (sourceforge.net user)
          * @param TCPStack Tcp stack to add statistics
          * @return Nothing.
          * @version v0.20
          */
        
        public TCP_HashTableElement(){
            TCP_HashTableElement.Element_id++;
        }
        
        public int returnID(){
            return TCP_HashTableElement.Element_id;
        }
        
        public void addstats(Tcp TCPStack) {
            TCPStack.IncReceivedDuplicatesNumber(this.received_duplicates);
            TCPStack.IncReceivedSegmentsNumber(this.received_segments);
            TCPStack.IncSentACKSegmentsNumber(this.sent_ACK);
            TCPStack.IncSentDuplicatesNumber(this.sent_duplicates);
            TCPStack.IncSentSegmentsNumber(this.sent_segments);
        }
        
        /*
         * This method resets element by setting default valuse
         * @author gift (sourceforge.net user)
         * @param Unused.
         * @return Nothing.
         * @version v0.20
         */
        public void reset() {
            if (timer!=null){
                System.out.println("Elm.reset: Timer cancel - part 1!  Elm id = " + this.returnID());
                try{
                    this.timer.cancel();
                }catch(Exception e){
                    System.out.println("Elm.reset: Timer cancel error.");
                    Error.Report(e);
                }
                System.out.println("Elm.reset: Timer cancel - part 2!  Elm id = " + this.returnID());
                this.timer = null;                
            }
            
            if (Servertimer!=null){
                System.out.println("Elm.reset: Servertimer cancel - part 1!  Elm id = " + this.returnID());
                try{
                    this.Servertimer.cancel();
                }catch(Exception e){
                    System.out.println("Elm.reset: Server timer cancel error.");
                    Error.Report(e);
                }
                System.out.println("Elm.reset: Servertimer cancel - part 2!  Elm id = " + this.returnID());
                this.Servertimer = null;
            }
            this.busy = false;
            this.PortStatus=0;
            this.application=null;
            this.connectedtoIP="";
            this.connectedtoPort=0;
            this.received_segments=0;
            this.sent_segments=0;
            this.sent_ACK=0;
            this.received_duplicates=0;
            this.sent_duplicates=0;
            this.seq_number=0;
            this.last_passed=-1;
            
            if (!this.ReceivedSegments.isEmpty()) this.ReceivedSegments.removeAllElements();
            if (!this.ReceivedACKs.isEmpty()) this.ReceivedACKs.removeAllElements();
            
            try {
                if (!this.SentACKs.isEmpty()) {
                       /* Set set = this.SentACKs.keySet();
                        Iterator itr = set.iterator();
                        while (itr.hasNext())
                        {
                            this.SentACKs.remove(itr.next());
                        }*/
                    Enumeration HTkeys;
                    Integer curkey;
                    HTkeys = SentACKs.keys();
                    while (HTkeys.hasMoreElements()) {
                        curkey= (Integer) HTkeys.nextElement();
                        this.SentACKs.remove(curkey);
                    }
                }
            }  catch(Exception e) {
                System.out.println("TCP Element.SentACKs reset error: " + e.toString());
                Error.Report(e);
            }
            
            
            try {
                if (!this.SegmentsToResend.isEmpty()) {
                    /*Set set = this.SegmentsToResend.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext())
                    {
                        this.SegmentsToResend.remove(itr.next());
                    }*/
                    Enumeration HTkeys;
                    Integer curkey;
                    HTkeys = SegmentsToResend.keys();
                    while (HTkeys.hasMoreElements()) {
                        curkey= (Integer) HTkeys.nextElement();
                        this.SegmentsToResend.remove(curkey);
                    }
                }
                
            }  catch(Exception e) {
                System.out.println("TCP Element.SegmentsToResend reset error: " + e.toString());
                Error.Report(e);
            }
            
            try {
                if (!this.OutputBuffer.isEmpty()) {
                    /*Set set = this.OutputBuffer.keySet();
                    Iterator itr = set.iterator();
                    while (itr.hasNext())
                    {
                        this.OutputBuffer.remove(itr.next());
                    }*/
                    Enumeration HTkeys;
                    Integer curkey;
                    HTkeys = OutputBuffer.keys();
                    while (HTkeys.hasMoreElements()) {
                        curkey= (Integer) HTkeys.nextElement();
                        this.OutputBuffer.remove(curkey);
                    }
                }
                
            }  catch(Exception e) {
                System.out.println("TCP Element.OutputBuffer reset error: " + e.toString());
            }
            
            this.isFIN_sent=false;
            this.isFIN_confirmed=false;
            this.isPassive=false;
           //this.isConnected=false;
            this.ApplicationStatus=0; //no status
            this.isServer=false;
            
        }
    }

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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import core.InvalidNetworkLayerDeviceException;
import core.LayerInfo;
import core.LowLinkException;
import core.Simulation;
import core.TransportLayerException;
import core.TransportLayerPortException;

/**
* This UDP class is constructed in a way that it represents its real world cousin (the UDP protocol) as accurately
* as possible.

* <P>This class contains methods which allow the transfer of packets to and from the lower layer. (Network Layer - IP
* only at this stage).  During a packets stay in this class, UDP headers are added, removed and examined
* depending on wether the packet is in compilation or decompilation. During compilation, a packet is created, headers are added (all based
* on real world UDP specification) and the packet is passed onto NetworkLayer (IP). During decompilation
* UDP headers are removed and the data that was contained in the packet is passed onto the ProtocolStack. Relavent
* information about what is happening in this class is added to the LayerInfo object so that it can be displayed
* to the user in a clear and concise way.</P>
*
* @author james_nikolaidis
* @since 10/06/2004
* @author gift (sourceforge.net user)
* @since 17 Nov 2005
* @version v0.11
*/

public class Udp implements Serializable             
{        
    /**
	 * 
	 */
	private static final long serialVersionUID = -3018809624034201712L;
	private Hashtable sessionTable = new Hashtable();
    private Object sessionTable_lock = new Object();
    private ProtocolStack  mParentStack;
    private socketLayer mSL;
    
    /*statistic block*/
    private int received_datagramms=0; //counter inc when a datagramm is received
    private int sent_datagramm=0; //counter inc when a datagramm is sent
    /*end of statistic block*/
        
    private static final int PORT_QUANT=100;
    private static final int PORT_START_NUMBER=3000;    
    /**
    * This method assigns the ParentStack
    * @author gift (sourceforge.net user)
    * @param inParentStack protocol stack 
    * @version v0.20
    */
	public Udp(ProtocolStack inParentStack, socketLayer inSL)
        {
            mParentStack = inParentStack;
            mSL = inSL;
	}
              
    
    /**
    * This method receives the UDP packet, checks CHECK_SUM 
    * and prints out a message to the layer info.
    * @author gift (sourceforge.net user)
    * @param inPacket a packet to receive (decompilate) 
    * @return Nothing.
    * @exception TransportLayerException If UDP CHECK_SUM != 1
    * @exception LowLinkException
    * @exception InvalidNetworkLayerDeviceException
    * @exception TransportLayerPortException
    * @version v0.20
    * @see TransportLayerException
    * @see LowLinkException
    * @see InvalidNetworkLayerDeviceException
    * @see TransportLayerPortException
    */

	public void receiveUDPPacket(UDP_packet inPacket) throws TransportLayerPortException, TransportLayerException, LowLinkException
        {
            //test: check sum of UDP packet	                    
            if (inPacket.getCheck_Sum() == 1)
            {
                // test if this packet is for a local Address.
                if(mParentStack.isInternalIP(inPacket.getDestIPAddress()) || mParentStack.isBroadcastIP(inPacket.getDestIPAddress())){

                    // test if destination UDP port exists on this NeworkLayerDevice
                    UDP_session Elm=getSession(jnSocket.genUDPkey(inPacket.get_destPort()));
                    if (Elm !=null)
                    {
                        int sock = Elm.getSocket();
                        
                        //now we decompose UDP datagram   
                        IncReceivedDatagrammsNumber();
                        Elm.IncReceivedDatagrammsNumber();

                        //Create Layer info 
                        LayerInfo UDP_Info = new LayerInfo(getClass().getName());
                        UDP_Info.setObjectName(mParentStack.getParentNodeName());
                        UDP_Info.setDataType("UDP Protocol");
                        UDP_Info.setLayer("Transport");
                        UDP_Info.setDescription("UDP packet received from "+ inPacket.getSourceIPAddress() + ":" + inPacket.get_srcPort() + " message: \"" +inPacket.getUDP_message() + "\"."); // + " UDP Port " + inPacket.get_destPort() + " has status \"busy\" from now.");
                        Simulation.addLayerInfo(UDP_Info);
                        mSL.recvFrom(sock, inPacket.getSourceIPAddress(), inPacket.get_srcPort(), inPacket.getUDP_message());                                                                          
                    } else {  
                      //throw new TransportLayerPortException("UDP Error: port " + inPacket.get_destPort() + " does not exist. Host \"" + mParentStack.getParentNodeName()+"\".");
                      throw new TransportLayerPortException("UDP Error: no application binded port "+inPacket.get_destPort() +" on host \""+ mParentStack.getParentNodeName()+"\"!");     
                   }
              }
           } else {
            throw new TransportLayerException("UDP Error: incorrect checksum on receiving!"); //comment this line if you have implemented check sum tests :)
          } 
	}
        
     /**
    * This method returns the UDP packet to send
    * and prints out a message to the layer info.
    * @author gift (sourceforge.net user)
    * @param inDestIPAddress destination IP address
    * @param inSourceIPAddress our IP address
    * @param indestPort destination port number    
    * @param insrcPort our port number
    * @param inMessage UDP message to send
    * @return UDP_packet to send in Network Layer
    * @exception TransportLayerException
    * @version v0.30
    * @see TransportLayerException
     */

	public void sendUDPPacket(int sock, String inDestIPAddress, int indestPort, String inMessage) throws TransportLayerException, LowLinkException
        {                                     
            
            String inSourceIPAddress;
            int insrcPort;
            
            insrcPort = 0;
            
            try{
                if(getSession(jnSocket.genUDPkey(mSL.get_socket(sock).src_port)) == null){
                    // we should reserve port for it
                    reserveFreePort(sock);
                    mSL.get_socket(sock).src_IP = mParentStack.getSrcIP();
                }

                inSourceIPAddress = mSL.get_socket(sock).src_IP;
                insrcPort = mSL.get_socket(sock).src_port;

                UDP_packet tosend = new UDP_packet(inDestIPAddress,inSourceIPAddress,indestPort,insrcPort);

                tosend.setUDP_message(inMessage);

                //Create Layer info 
                LayerInfo UDP_Info = new LayerInfo(getClass().getName());
                UDP_Info.setObjectName(mParentStack.getParentNodeName());
                UDP_Info.setDataType("UDP Protocol");
                UDP_Info.setLayer("Transport");
                UDP_Info.setDescription("Created UDP packet for " + inDestIPAddress + ":" + indestPort +". (Local is " + inSourceIPAddress + ":" + insrcPort + ", socket " + sock + " ).");
                Simulation.addLayerInfo(UDP_Info);

                IncSentDatagrammsNumber();
                if (getSession(jnSocket.genUDPkey(insrcPort)) !=null)
                {
                    getSession(jnSocket.genUDPkey(insrcPort)).IncSentDatagrammsNumber();
                }

                mParentStack.sendPacket(tosend);                 
                
          }catch(Exception e){
                
                try {               
                
                    LayerInfo UDP_Info = new LayerInfo(getClass().getName());
                    UDP_Info.setObjectName(mParentStack.getParentNodeName());
                    UDP_Info.setDataType("UDP Packet");
                    UDP_Info.setLayer("Transport");
                    UDP_Info.setDescription("UDP sending error: \""+ e.toString() + "\".");                            
                    Simulation.addLayerInfo(UDP_Info); 
                    closePort(sock); 
                //*TODO*: here we shall put some layerinfo for TransportException
                } catch (TransportLayerException te)
                    {
                        LayerInfo UDP_Info = new LayerInfo(getClass().getName());
                        UDP_Info.setObjectName(mParentStack.getParentNodeName());
                        UDP_Info.setDataType("UDP Packet");
                        UDP_Info.setLayer("Transport");
                        UDP_Info.setDescription("UDP port freeing: \""+ te.toString() + "\".");                            
                        Simulation.addLayerInfo(UDP_Info);    
                    }
                
            }
	}
        
        // if application tries to send, but no port binded, do it now..        
        public void reserveFreePort(int sock) throws TransportLayerException
        {
            String sessionID;     
            boolean Found=false;
            Enumeration<String> LocalSessions = getSessionKeys();
           
           
            UDP_session Elm;
            
            // check if socket already busy
            while ( (LocalSessions.hasMoreElements())  && !(Found) )
            {                                  
                sessionID = LocalSessions.nextElement();                
                Elm = getSession(sessionID);                
                if ( sock == Elm.sock && Elm.sock >=0 )  
                {
                    Found=true;
                    throw new TransportLayerException("UDP Error: socket is already busy (have port)!");                    
                }
            }
            
            //in case we have not found socket in hash table.....
            if (!Found)
            {
             //lets try to reserve any free port
                int number=Udp.PORT_START_NUMBER;
                boolean Reserved=false;
                while ( (number<Udp.PORT_START_NUMBER+Udp.PORT_QUANT) && !(Reserved) )
                {
                    Elm = getSession(jnSocket.genUDPkey(number));
                    if (Elm == null)  //free port
                    {
                        Reserved=true;
                        mSL.get_socket(sock).src_port = number;                            
                        addSession(jnSocket.genUDPkey(number), sock);
                        
                        LayerInfo UDP_Info = new LayerInfo(getClass().getName());
                        UDP_Info.setObjectName(mParentStack.getParentNodeName());
                        UDP_Info.setDataType("UDP Protocol");
                        UDP_Info.setLayer("Transport");
                        UDP_Info.setDescription("Local port " + number + " reserved for client app.");
                        Simulation.addLayerInfo(UDP_Info);
                    }
                    number++;   
                }
                
               if (!Reserved)  //all ports are busy :(
               {
                 throw new TransportLayerException("UDP Error: all ports are busy! Cannot reserve port for socket!");   
               }
            }
        }   
       
       // bind port to socket
       public void bindPort(int sock_num, int in_Port) throws TransportLayerException
       {
            if(!mSL.get_socket(sock_num).open_state){
                if (in_Port>=0 && in_Port<=65535) { //create such a record in hashtable
                   mSL.get_socket(sock_num).src_port = in_Port;
                   printLayerInfo("Local port " + in_Port + " binded.");
                } else //
                {
                   throw new TransportLayerException("UDP error: can not listen to port "+ in_Port +"! Use port range from 1 to 65535 to listen to.");
                }
            }
            else{
                if (mSL.get_socket(sock_num).src_port==in_Port){
                    printLayerInfo("UDP error: can not double bind to port "+ in_Port +"! Server is already listening to this port");
                    throw new TransportLayerException("UDP error: can not bind listen to port "+ in_Port +"! Server is already listening to this port");
                }
                else{
                    printLayerInfo("UDP error: can not bind to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);      
                    throw new TransportLayerException("UDP error: can not bind to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);          
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
                        addSession(jnSocket.genUDPkey(in_Port), sock_num); //hash table update
                        printLayerInfo("Local port " + in_Port + " is listening.");
                    }
                    else{
                        if (mSL.get_socket(sock_num).src_port==in_Port){
                            printLayerInfo("UDP error: can not double listen to port "+ in_Port +"! Server is already listening to this port");
                            throw new TransportLayerException("error: can not double listen to port "+ in_Port +"! Server is already listening to this port");
                        }
                        else{
                            printLayerInfo("UDP error: can not listen to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);      
                            throw new TransportLayerException("TCP error: can not listen to port "+ in_Port +"! Already listening to port " + mSL.get_socket(sock_num).src_port);          
                        }
                    } 
                } else {
                    throw new TransportLayerException("UDP error: can not listen to port "+ in_Port +"! Use port range from 1 to 65535 to listen to.");
                }
            } else
            {
                throw new TransportLayerException("UDP error: can not listen to port 0! Use bind before to call listen!");
            }
        }
       
    /**
    * This method closes the UDP port for an application 
    * @author gift (sourceforge.net user)
    * @param application application that uses port       
    * @return Nothing
    * @exception TransportLayerException
    * @version v0.10
    * @see TransportLayerException
    */
        public void closePort(int sock) throws TransportLayerException //changes port status from 1 (listen) or (see proc ;) to 0 (free)
        { 
          UDP_session Elm=null;
          int portToClose=mSL.get_socket(sock).src_port; 
          Elm= getSession(jnSocket.genUDPkey(portToClose));
          
          if(portToClose!=0 && Elm!=null){
             LayerInfo UDP_Info = new LayerInfo(getClass().getName());
             UDP_Info.setObjectName(mParentStack.getParentNodeName());
             UDP_Info.setDataType("UDP Protocol");
             UDP_Info.setLayer("Transport");
             UDP_Info.setDescription("Local port " + portToClose + " closed and set to free.");
             Simulation.addLayerInfo(UDP_Info);
             removeSession(jnSocket.genUDPkey(portToClose));
          } else throw new TransportLayerException("UDP Error: port "+ portToClose +" is not being LISTENED.");
        }
        
        private UDP_session addSession(String key, int sock){
            UDP_session udps = new UDP_session(sock);
            synchronized(sessionTable_lock){
                sessionTable.put(key, udps);
            }
            return udps;
        }

        private UDP_session removeSession(String key){
            UDP_session udps;
            synchronized(sessionTable_lock){
                udps = (UDP_session) sessionTable.remove(key);
            }
            return udps;
        }

        public UDP_session getSession(String key){
            UDP_session udps;
            synchronized(sessionTable_lock){
                udps = (UDP_session) sessionTable.get(key);
            }
            return udps;
        }

        public Enumeration<String> getSessionKeys(){
            Enumeration<String> keys;
            synchronized(sessionTable_lock){
                keys = sessionTable.keys();
            }
            return keys;
        }
        
        
/*STATISTIC PART */        
        
    /**
    * This method increments received datagramms counter
    * @author gift (sourceforge.net user)
    * @param Unused
    * @return Nothing 
    * @version v0.20
    */
        
        public void IncReceivedDatagrammsNumber()
        {
                received_datagramms++;
        }
        
        
    /**
    * This method increments sent datagramms counter
    * @author gift (sourceforge.net user)
    * @param Unused
    * @return Nothing 
    * @version v0.20
    */
        public void IncSentDatagrammsNumber()
        {
                sent_datagramm++;
        }
        
    /**
    * This method returns the number of received datagramms
    * @author gift (sourceforge.net user)
    * @param Unused.
    * @return int the number of received datagramms 
    * @version v0.20
    */
        public int GetReceivedDatagrammsNumber()
        {
            return received_datagramms;    
        }
    
    /**
    * This method returns the number of sent datagramms
    * @author gift (sourceforge.net user)
    * @param Unused.
    * @return int the number of sent datagramms 
    * @version v0.20
    */
        public int GetSentDatagrammsNumber()
        {
            return sent_datagramm;    
        }

    /**
    * This method resets UDP counters
    * @author gift (sourceforge.net user)
    * @param Unused.
    * @return Nothing. 
    * @version v0.20
    */
        public void ResetCounters()
        {
            sent_datagramm=0;  
            received_datagramms=0;
        }
            
        protected void printLayerInfo(String s) {
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("UDP Protocol");
            protInfo.setLayer("Transport ");
            protInfo.setDescription(s);
            Simulation.addLayerInfo(protInfo);
        }
} //EOF


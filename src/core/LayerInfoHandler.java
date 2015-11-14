package core;
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


import java.util.Iterator;
import java.util.Vector;

/**
 * The LayerInfoHandler's job is to receive and store LayerInfo objects and pass them
 * up to the Simulation (which then passes them to the GUI or CLI) when required. These LayerInfo objects
 * are passed to the LayerInfoHandler when an object in the Simulation (such as a Protocol or NetworkInterface)
 * has received, processed or is sending a Packet or some other data object. Any kind of
 * information that the user might want to see during a 'send' process should be passed to the LayerInfoHandler
 * encapsulated in a LayerInfo object.
 *
 * @author tristan_veness
 * @version 13 June 2004
 * @version v0.10
 */

class LayerInfoHandler {
     /** Queue to hold incoming info from an Object */
     private Vector infoQueue;
     /** Indicates when there is no more info to receive */
     private boolean ended;
     
     private boolean blocked;
     
     /** Constructs a new LayerInfoHandler */
     protected LayerInfoHandler() {
          blocked = false;
          infoQueue = new Vector();
          ended = false;
     }

     /**
     * Receives info and places it into the LayerInfoHandler's info queue.
     * @param info  - The LayerInfo object to receive.
     * @author tristan_veness
     * @version v0.10
     */
     public void receiveInfo(LayerInfo info) {
          block();
          if (!ended) {
               infoQueue.add(info);
          }
          unblock();
     }

     /**
     * Gets the next LayerInfo object from the infoQueue and removes it from the queue.
     * @author tristan_veness
     * @return null when there are no more items in the queue.
     * @version v0.10
     **/
	public LayerInfo getNext() {
            block();
            if (infoQueue.size() == 0) { 
                unblock();
                return null; 
            }
            LayerInfo info = (LayerInfo)infoQueue.elementAt(0);
            infoQueue.remove(0);
            unblock();
            return info;
	}

     /**
     * Sets the state of the LayerInfoHandler to indicate it can receive no more info
     * @author tristan_veness
     * @version v0.10
     **/
     public void end() {
          ended = true;
     }

     /**
     * Returns the state of the LayerInfoHandler
     * @author tristan_veness
     * @return true if the LayerInfoHandler can not receive any more info. 
     * False if it is still open to incoming info.
     * @version v0.10
     **/
     public boolean hasEnded() {
          return ended;
     }
     
     /**
      * This method clears the vector of all
      * Layer info objects ready for a new recording.      *
      * @author tristan_veness
      *  @version v0.10
      **/     
     public void clear(){
        block();	 
     	infoQueue = new Vector();
        unblock();	
     }
     
     /**
      * This method returns a vector containing String arrays that
      * represent each object recorded by the LayerInfoHandler.      * 
      * @author tristan_veness
      * @return Vector containg string arrays for each line recorded
      * @version v0.10
      **/     
     public Vector getRecordedInfo(){    
        block();	
	Vector output = new Vector();     	
     	Iterator it = infoQueue.iterator();
     	while(it.hasNext()){
     		LayerInfo tempInfo = (LayerInfo)it.next();
     		output.addElement(tempInfo.getRecordedInfo());     		
     	}
        unblock();	
     	return output;
     }
     
     protected void block(){
         try{
            while(blocked)
                 Thread.sleep(10);
         }catch(Exception e){ e.printStackTrace(); }
         blocked = true;
     }
     
     protected void unblock(){
         blocked = false;
     }
}//EOF

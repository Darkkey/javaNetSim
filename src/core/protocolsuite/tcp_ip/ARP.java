/*

Java Network Simulator (jNetSim)



Copyright (c) 2005, Ice Team;  All rights reserved.

Copyright (c) 2004, jFirewallSim development team;  All rights reserved.



Redistribution and use in source and binary forms, with or without modification, are

permitted provided that the following conditions are met:



	- Redistributions of source code must retain the above copyright notice, this list

	  of conditions and the following disclaimer.

	- Redistributions in binary form must reproduce the above copyright notice, this list

	  of conditions and the following disclaimer in the documentation and/or other

	  materials provided with the distribution.

	- Neither the name of the Ice Team nor the names of its

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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import core.CommunicationException;
import core.LayerInfo;
import core.LowLinkException;
import core.Simulation;



/**

 * Currently the ARP protocol is implemented using a ststic method.  This

 * means that each time the IP address is set for a node in the simulation the 

 * IP address and MAC address are sent to all computers and the ARP table 

 * is then updated for any entries that are local.  This leaves a BIG WHOLE in the

 * protocol because any new nodes added to the simulation do not have the ARP table

 * for any of the older computers in the Simulation.  This issue will need to be

 * covered in later versions of the Protocol.

 * @author bevan_calliess

 * @author robert_hulford

 * @author luke_hamilton

 * @since Sep 17, 2004

 * @version v0.20

 */

public class ARP implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2918133325615368085L;

	private ProtocolStack  mParentStack;

	private Vector ARPTable = new Vector();

	private String mLastIpRequest = "0.0.0.0";

	public final int ARP_MAXTRY  = 5;

	public ARP(ProtocolStack inParentStack){

		mParentStack = inParentStack;
		
	}	

	

	/**

	 * This method will add a new entry to the ARP table

	 * @author robert_hulford

	 * @author bevan_calliess

         * @author Key

	 * @param IPAddress - The IP address of the new entry

	 * @param MacAddress - The Mac address of the new entry

         * @param inEntryType - The type of the new entry : Static or Dynamic

	 * @version v0.22

	 */	

	public void addToArpTable(String inIPAddress, String inMacAddress, String inEntryType) {

		//look for an entry with this MAC address in the table

		Iterator it = ARPTable.iterator();

		while(it.hasNext())

		{

			ARPTableEntry currEnt = (ARPTableEntry)it.next();

			if (currEnt.getMACAddress().equals(inMacAddress))

			{

				it.remove();

			}

		}		

		ARPTableEntry newEntry = new ARPTableEntry(inIPAddress,inMacAddress,inEntryType);

		ARPTable.add(newEntry);

	}

	

	/**

	 * This method will remove all entries from the ARP table that match

	 * nay IP Address in the String Array.

	 * @author bevan_calliess

	 * @author robert_calliess

	 * @param IPAddress - An array of IP addreses that need to be removed

	 * @version v0.20

	 */	

	public void removeFromArpTable(String[] IPAddress){

		ARPTableEntry currentEntry;

		

		Iterator it = ARPTable.iterator();

		while(it.hasNext())

		{

			currentEntry = (ARPTableEntry)it.next();

			for(int y=0; y < IPAddress.length; y++)

			{

				if(IPAddress[y].equals(currentEntry.getIPAddress()))

				{

					it.remove();

				}

			}

		}		

	}

	

	/**

	 * This method will generate a String array containing all

	 * the entries from the ARP table

	 * @author robert_hulford

	 * @author bevan_calliess

	 * @return Vector<Vector<String>> - An array of strings that can be used to display the ARP table. Format (Internet Address,Physical Address,Type)

	 * @version v0.20

	 */	

	public Vector<Vector<String>> getARPTable()

	{

		// first clean out the ARP table so that there are no old entries

		cleanARPTable();
		
		Vector<Vector<String>> output = new Vector<Vector<String>>(0);
		int counter = 0;
		String ipAddress;
		char padding = ' ';

		if(ARPTable.size()>0){		
			Iterator it = ARPTable.iterator();
			while(it.hasNext())

			{
				counter++;
				ARPTableEntry currEnt = (ARPTableEntry)it.next();
				ipAddress = pad(currEnt.getIPAddress(),20,padding);
                                Vector<String> outrecord = new Vector<String>(3);
                                output.add(outrecord);
				outrecord.add(ipAddress);
                                outrecord.add(currEnt.getMACAddress());
                                outrecord.add(currEnt.getEntryType());
			}
		}else{
			//output.size() == 0 -- arp table is empty			
		}

		return output;

	}

	

	/**

	 * This method will generate a String containing the Mac address

	 * of the IP address passed in or null if it is not in the ARP table

	 * the entries from the ARP table are cleaned out after two minutes 

	 * as they are in the real protocol.

	 * @author bevan_calliess

	 * @author robert_hulford
         
         * @author key

	 * @param String - The IP address to search for in the ARP table

	 * @return String - Either the MAC of the ip passed in or null.

	 * @version v0.20

	 */	

	public String getMACAddress(String IPAddress, String inInterfaceKey) throws LowLinkException

	{
                
            

		String output= null;
		int tryings = ARP_MAXTRY;
                
        IPV4Address ip;
                
                try{
                    ip = new IPV4Address(IPAddress);
                	//this cleans out any entries older than 2 minutes
		
                        if(ip.isBroadcast()){
                            return "FF:FF:FF:FF:FF:FF";
                        }
               }catch(Exception e){}                        
                
                	cleanARPTable();				

			Iterator it = ARPTable.iterator();

			while(it.hasNext()){

				ARPTableEntry currEnt = (ARPTableEntry)it.next();

				if (currEnt.getIPAddress().equals(IPAddress)){

					output = currEnt.getMACAddress();

				}

			}

			if(output == null){

			//then send ARP Packet to try and find out about IP Address

             while(output==null && tryings-->0){
                            
				mLastIpRequest = IPAddress;
                                
				String srcMAC = mParentStack.getMACAddress(inInterfaceKey);

				ARP_packet ArpDisc = new ARP_packet(IPAddress);

				ArpDisc.setMessageCode(ARP_packet.ARP_REQUEST);

				ArpDisc.setSourceMAC(srcMAC);
				
				ArpDisc.setSourceIPAddress(mParentStack.getIPAddress(inInterfaceKey));

				//Create layer info object 

				LayerInfo arpInfo = new LayerInfo(getClass().getName());

				arpInfo.setObjectName(mParentStack.getParentNodeName());

				arpInfo.setDataType("ARP Discovery Packet");

				arpInfo.setLayer("DataLink");

				arpInfo.setDescription("Created ARP discovery packet to source MAC address for IP "+IPAddress);

				Simulation.addLayerInfo(arpInfo);

				try{

					mParentStack.broadcastPacket(ArpDisc,inInterfaceKey);				

				}catch(CommunicationException e){

					// no need to throw this. method will return null

				}

				Iterator it1 = ARPTable.iterator();

				while(it1.hasNext())

				{

					ARPTableEntry currEnt = (ARPTableEntry)it1.next();

					if (currEnt.getIPAddress().equals(IPAddress))

					{

						output = currEnt.getMACAddress();

					}

				}				
                            }

			}		

			return output;

		}

	

	/**

	 * This method will recieve a packet and determind the type 

	 * Then deal with the packet depending on the required response.

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param temp - The packet to be processed

	 * @version v0.20

	 */

	

	public void receiveARPPacket(ARP_packet inARPpacket) throws LowLinkException{

		// test if this packet is for a local Address.
                //mParentStack.isInternalIP(inARPpacket.getDestIPAddress())
            
		if(mParentStack.isInternalIP(inARPpacket.getDestIPAddress())){

			// test the type of ARP packet			

			if(inARPpacket.getMessageCode()==1){

				//If its an ARP Request add the entry to the arp table

				//		and send response

				try{			

					addToArpTable(inARPpacket.getSourceIPAddress(),inARPpacket.getSourceMAC(),"Dynamic");

					ARP_packet ArpResponse = new ARP_packet(inARPpacket.getSourceIPAddress());

					ArpResponse.setMessageCode(ARP_packet.ARP_REPLY);
					
					ArpResponse.setSourceIPAddress(inARPpacket.getDestIPAddress());

					String outInterface = mParentStack.getRouteInfo(inARPpacket.getSourceIPAddress());

					String srcMAC = mParentStack.getMACAddress(outInterface);			

					ArpResponse.setSourceMAC(srcMAC);

					//Create Layer info 

					LayerInfo arpInfo = new LayerInfo(getClass().getName());

					arpInfo.setObjectName(mParentStack.getParentNodeName());

					arpInfo.setDataType("ARP Response Packet");

					arpInfo.setLayer("DataLink");

					arpInfo.setDescription("Created ARP Response packet to "+inARPpacket.getSourceIPAddress() );

					Simulation.addLayerInfo(arpInfo);				

					//Send packet

					mParentStack.sendPacket(ArpResponse);					

				}catch(CommunicationException e){

					// no need to throw this. method will return null

				}catch(LowLinkException e){

					// no need to throw this. method will return null

                                        throw e;

				}			

			}else{

				//if its an ARP response Add the entry to the ARP table.

				addToArpTable(inARPpacket.getSourceIPAddress(),inARPpacket.getSourceMAC(),"Dynamic");	

			}

		}

	}

	

	/**

	 * This method is used to create a fixed length string

	 * If the string is already greater than the length no action is taken

	 * and the original string is returned.

	 * @author bevan_calliess

	 * @author robert_hulford

	 * @param inString - The string that you want padded to the fixed length.

	 * @param length - The length you would like to string to be.

	 * @param pad - The character you would like to use to pad the String. 

	 * @return String - Returns a gap lenght

	 * @version v0.20

	 */

	private String pad(String inString, int length, char pad) {

		StringBuffer buffer = new StringBuffer(inString);

		while (buffer.length() < length) {

			buffer.append(pad);

		}

		return buffer.toString();

	}

	/**

	 * This method will iterate through the ARP table and clean out

	 * any ARP entries that have been here for more than 2 minutes.

	 * Note: This is a simplified version of the Real ARP

	 * The Real ARP process that will only keep an entry for 2 minutes

	 * unless it is used again.  It will retain the entries that are 

	 * reused up to a maximum of 20 Minutes.

	 */

	private void cleanARPTable(){

		GregorianCalendar tempTime = new GregorianCalendar();

		tempTime.add(Calendar.MINUTE,-2);

		Iterator it =  ARPTable.iterator();

		

		while(it.hasNext()){

			ARPTableEntry tempEntry = (ARPTableEntry)it.next();

			GregorianCalendar entryTime = tempEntry.getEntryTime();

			if(entryTime.getTimeInMillis()<tempTime.getTimeInMillis()){

				if(tempEntry.getIPAddress().equals(mLastIpRequest)){

					mLastIpRequest = "0.0.0.0";

				}				

				if(tempEntry.getEntryType().contains("Dynamic")) it.remove();				

			}			

		}		

	}





}//EOF


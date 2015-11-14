package core;

import java.util.Enumeration;
import java.util.Hashtable;

import core.protocolsuite.tcp_ip.ARP_packet;
import core.protocolsuite.tcp_ip.IP_packet;

public class WiFiPort extends EthernetNetworkInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1737841009400766734L;

	protected int channel = 1; 
	
	protected String BSSID;	
	
	protected String SSID = "default";
		
	protected boolean active = false;
	
	public static final int MODE_AP = 0;
	public static final int MODE_STATION = 1;
	public static final int MODE_REPEATER = 2;
	
	protected int Mode = WiFiPort.MODE_AP; // 0 - AP, 1 - Station, 2 - Repeater
	
	protected boolean shared_auth = false; // true -- shared, false -- open
	
	protected String WEP_keys[] = {"", "", "", ""};
	
	protected boolean isSecureEnabled(){
		return ((new String(WEP_keys[0] + WEP_keys[1] + WEP_keys[2] + WEP_keys[3])).length() > 5);
	}
	
	protected static final int OFFLINE = 0;
	protected static final int PROBE = 1;
	protected static final int AUTH = 2;
	protected static final int ASSOC = 3;
	protected static final int DATA = 4;
	protected static final int DATA_ACK = 5;
	
	// resend block
	
	class ResendPacket{
		public int cnt;
		public W80211_packet packet;
		public long nextTime;
		public long period;
		
		public ResendPacket(W80211_packet p, long period){
			cnt = 5;
			this.period = period;
			packet = p;
			nextTime = System.currentTimeMillis() + period;
		}
	}
	
	Hashtable<Long, ResendPacket> ResendPackets = new Hashtable<Long, ResendPacket>();
	
	// station block
	
	protected boolean associated;
	protected boolean failed;
	protected int state;
	protected String apBSSID;
	protected boolean lineFree;
		
	// AP block

	class APClient{
		public String BSSID;
		public int state;
		
		public APClient(String BSSID){
			this.BSSID = BSSID;
			state = WiFiPort.PROBE;
		}
	}
	
	Hashtable<String, APClient> APClients = new Hashtable<String, APClient>();
	
	
	// Contructor		

	public WiFiPort(long UID, String inName, Node inParent) {

    	super(UID, inName,inParent);	
    	
    	BSSID = MACAddress; 
    	   	
    	associated = false;
    	
    	state = WiFiPort.OFFLINE;
    	
    	failed = false;
    	//channel = 0;
    	
    	lineFree=false;

  	}
	
	public void associate() throws LowLinkException{
		failed = false;
		associated = false;
		
		state = WiFiPort.PROBE;
		
		lineFree=false;
		
		sendProbeReq();
	}
	
	void sendWirelessPacket(W80211_packet inWP) throws LowLinkException{
		Simulation.Sim.sendWirelessPacket(this, inWP, channel);
	}
	
	void sendWirelessConfirmPacket(W80211_packet inWP, long resendTime) throws LowLinkException{
		boolean canSend = true;
		
		ResendPacket p =  new ResendPacket(inWP, resendTime);
		
		ResendPackets.put(p.packet.getID(), p);
		
		if(Mode == WiFiPort.MODE_STATION && inWP.Type == 2){
			canSend = checkLineFree();
			if(!canSend)
				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Data Packet", 
    					"Link", "Can't resend packet: no CTS -> medium is busy?Resending packet from interface " + name + " due to timeout.");
		}
		
		if(canSend)
			sendWirelessPacket(inWP);
	}
	
	void sendWirelessConfirmPacket(W80211_packet inWP) throws LowLinkException{
		sendWirelessConfirmPacket(inWP, 500);
	}
	
	void sendProbeReq() throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, "FF:FF:FF:FF:FF:FF", 0, 4, SSID);
		
		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
		   	"Link", "Sending probe req packet from interface "+ name);
		
		sendWirelessConfirmPacket(tempWiFi, 5000);
	}
	
	void sendProbeReply(String inBSSID, Long seq) throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, inBSSID, 0, 5, SSID);
		
		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
		   	"Link", "Sending probe reply packet to " + inBSSID + " from interface "+ name);
		
		sendWirelessPacket(tempWiFi);
	}
	
	void sendAuthReq(String inBSSID, int seq) throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, inBSSID, 0, 11, SSID);
		
		tempWiFi.setAuth(shared_auth, seq);
		
		tempWiFi.Cypher(WEP_keys[0], 0);
		
		if(seq == 0)
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
					"Link", "Sending auth req packet from interface "+ name);
		else
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
					"Link", "Sending auth challenge reply packet from interface "+ name);
		
		sendWirelessConfirmPacket(tempWiFi);
	}
	
	void sendAuthReply(String inBSSID, int seq) throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, inBSSID, 0, 11, SSID);
		
		tempWiFi.setAuth(shared_auth, seq);
		
		tempWiFi.Cypher(WEP_keys[0], 0);
		
		if(seq == 1 || !shared_auth)
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
					"Link", "Sending auth reply packet from interface "+ name);
		else
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
					"Link", "Sending auth challenge req packet from interface "+ name);
		
		sendWirelessPacket(tempWiFi);
	}
	
	void sendAssocReq(String inBSSID) throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, inBSSID, 0, 0, SSID);
		
		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
		   	"Link", "Sending assoc req packet from interface "+ name);
		
		sendWirelessConfirmPacket(tempWiFi);
	}
	
	
	
	void sendAssocReply(String inBSSID, Long seq) throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, inBSSID, 0, 1, SSID);
		
		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
		   	"Link", "Sending assoc reply packet from interface "+ name);
		
		sendWirelessPacket(tempWiFi);
	}
	
	void sendACK(String inBSSID, Long seq) throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, inBSSID, 1, 13, SSID);
		
		tempWiFi.setID(seq);
		
		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Control Packet", 
		   	"Link", "Sending ack packet from interface "+ name);
		
		sendWirelessPacket(tempWiFi);
	}
	
	void sendCTS(String inBSSID) throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, inBSSID, 1, 12, SSID);
		
		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Control Packet", 
		   	"Link", "Sending CTS packet from interface "+ name);
		
		sendWirelessPacket(tempWiFi);
	}
	
	void sendRTS() throws LowLinkException{
		W80211_packet tempWiFi =  new W80211_packet(null, BSSID, apBSSID, 1, 11, SSID);
		
		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Control Packet", 
		   	"Link", "Sending RTS packet from interface "+ name);
		
		sendWirelessPacket(tempWiFi);
	}
	
	boolean checkLineFree() throws LowLinkException{
		boolean actlineFree = false;		
		
		sendRTS();
		
		try{
			Thread.sleep(100);
		}catch(Exception e){ }
		
		actlineFree = lineFree;
		lineFree = false;
		
		return actlineFree;
	}

  	public void Timer(int temp){
  		Long pID;
  		ResendPacket p;
  		
  		for (Enumeration e=ResendPackets.keys(); e.hasMoreElements();) {  			
  			
            pID = (Long)e.nextElement();
            p = (ResendPacket)ResendPackets.get(pID);
            
            if(System.currentTimeMillis() > p.nextTime){

            	p.cnt --;
            	
            	p.nextTime = System.currentTimeMillis() + p.period;
            	
            	boolean canSend = true;
        		            	
            	try{
            		if(Mode == WiFiPort.MODE_STATION && p.packet.Type == 2){
            			canSend = checkLineFree();
            			
            			if(!canSend)
            				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Data Packet", 
                					"Link", "Can't resend packet: no CTS -> medium is busy?Resending packet from interface " + name + " due to timeout.");
            		}
        		
            		if(canSend){        		
            	
            			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Packet", 
            					"Link", "Resending packet from interface " + name + " due to timeout.");
            		
        				sendWirelessPacket(p.packet);
            		}
            	}catch(LowLinkException ex){ }
            	
            	if(p.cnt < 1){
            		ResendPackets.remove(pID);
            		
            		if(Mode == WiFiPort.MODE_STATION){
            			if(state > WiFiPort.ASSOC){
            				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Deassociated at AP " + apBSSID + "/" + SSID + " due to network timeout.");
            			}else if(state == WiFiPort.PROBE){
            				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Probing AP " + SSID + " failed: no such AP found.");
            			}else{            				
            				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Associating at AP " + SSID + " failed: protocol mistmatch.");
            			}
            			
            			associated = false;
        				failed = true;
        				DOWN();
        				return ;
        		
            			
            		}else if(Mode == WiFiPort.MODE_AP){
            			if(state >= WiFiPort.ASSOC){
            				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Deassociating client " + p.packet.getDstBSSID() + " due to network timeout.");
            			}
            			
            			APClients.remove(p.packet.getSrcBSSID());
            		}
            	}
            }
            
  		}
  	}

	
	public int getChannel(){
		return channel;
	}	
	
	public void setChannel(int channel){
		this.channel = channel;
	}	
	
	public long getID(){
		return UID;
	}
	
    public int getType(){
        return NetworkInterface.Wireless;
    }
    
    public String getBSSID() {
    	return BSSID;
    }
    
	public void setBSSID(String bssid) {
		if(Mode != WiFiPort.MODE_STATION)
			BSSID = bssid;
	}

	public String getAPBSSID() {
		return apBSSID;
	}
    
    public String getSSID() {
		return SSID;
	}

	public void setSSID(String ssid) {
		SSID = ssid;
	}

	public int getMode() {
		return Mode;
	}

	public void setMode(int mode) {
		Mode = mode;
	}

	public boolean isSharedAuth() {
		return shared_auth;
	}

	public void setSharedAuth(boolean shared_auth) {
		this.shared_auth = shared_auth;
	}

	public String getWEPKey(int keynum) {
		if(keynum>=1 && keynum<=4)
			return WEP_keys[keynum-1];
		return "";
	}

	public void setWEPKeys(int keynum, String wep_key) {
		if(keynum>=1 && keynum<=4)
			WEP_keys[keynum-1] = wep_key;
	}

	public Hashtable<String, APClient> getAPClients() {
		return APClients;
	}

	protected void setMacAddress(String macAddress){
         if(macAddress==null){
             this.MACAddress = defaultMACAddress;
         }else{
             this.MACAddress = macAddress;
         }
         if(Mode == WiFiPort.MODE_STATION)
        	 this.BSSID = this.MACAddress;
	}
    
    public boolean isActive(){
        return active;
    }
    
	public void UP(){		
		super.UP();
		
		if(!active) Mode = WiFiPort.MODE_AP;
		
		failed = false;
		apBSSID = "";
		
		ResendPackets = new Hashtable<Long, ResendPacket>();
		
		if(Mode != WiFiPort.MODE_STATION){
			APClients = new Hashtable<String, APClient>();
		}
		
		Simulation.Sim.connectWireless(this);
		parentNode.startTimerTask(this, 500);
		
		if(Mode == WiFiPort.MODE_STATION){
			try{
				associate();				
			}catch(Exception e){}
		}
		
	}
	
	public void DOWN(){
		Simulation.Sim.disconnectWireless(this);
		parentNode.cancelTimerTask(this.getUID());
		
		super.DOWN();
	}
	
	public void refresh(){
		
	}
	
	protected void receiveDataPacket( W80211_packet tempWiFi) throws LowLinkException {
		//&& 
		
    	if(isSecureEnabled() && !tempWiFi.WEP.trim().equals(WEP_keys[tempWiFi.keyNum].trim()) ){
    		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 packet", "Link", "Recieved and discarded packet: Wrong WEP params.");
    		
    		return;
    	}
    	
    	Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 packet", "Link", "Recieved and accepted packet at interface " + name);
    
    	Ethernet_packet tempPacket = (Ethernet_packet) tempWiFi.getData();
    
    	if(MACAddress.equals(tempPacket.getDestinationMACAddress()) 
            || tempPacket.getDestinationMACAddress().equals("FF:FF:FF:FF:FF:FF") || !active){
        //Packet is For this Interface or is broadcast so send it to the Parent Node
        
    	Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "Ethernet Packet", 
			   	"Link", "Recieved and accepted packet at interface " + MACAddress);
		
    	boolean allowReceive = true;
	   
    	Packet temp = tempPacket;
	   
	    if(active){
		   
		   temp = tempPacket.getData();
       
    	   NetworkLayerDevice device = (NetworkLayerDevice) parentNode;
           if(device!=null && getACLin()!=0 && temp instanceof IP_packet && !(temp instanceof ARP_packet)){
                allowReceive = device.getACL().passACL(getACLin(), temp);
           }
        }
	    
	    sendACK(tempWiFi.getSrcBSSID(), tempWiFi.getID());
                        
        if(allowReceive)
         	parentNode.receivePacket(temp, name);
                        
    	}else{
        //Packet is not for the Interface Drop Packet and record something in 
        //the Layerinfo object	 
    		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "Ethernet Packet", 
			   	"Link", "Recieved and dropped packet at interface " + MACAddress);
    	}
	}
	
	protected void receivePacket(Packet inPacket) throws LowLinkException {
        if(!parentNode.On) return;
                
        W80211_packet tempWiFi = (W80211_packet) inPacket;
        
        boolean b1 = (tempWiFi.getDstBSSID() != BSSID); 
        boolean b2 = !(tempWiFi.SSID.equals(SSID));
        boolean b3 = (tempWiFi.getDstBSSID()!="FF:FF:FF:FF:FF:FF");
        
        if((tempWiFi.getDstBSSID() != BSSID || !tempWiFi.SSID.equals(SSID)) && tempWiFi.getDstBSSID()!="FF:FF:FF:FF:FF:FF"){
    		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 packet", "Link", "Recieved and discarded packet at interface " + name);
    		
    		return;
    	}
    	        
        if(tempWiFi.Type == 2 && tempWiFi.subType == 0){
        	receiveDataPacket(tempWiFi);
        }else if(tempWiFi.Type == 1 && tempWiFi.subType == 13){
        	
        	ResendPackets.remove(tempWiFi.getID());        
        	
        	Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Control Packet", 
				   	"Link", "Recieved ack from " + tempWiFi.getSrcBSSID() + " on interface "+ name);
			
        }else{
        	
        	if(Mode == WiFiPort.MODE_STATION){
        		
        		if(tempWiFi.getDstBSSID() == "FF:FF:FF:FF:FF:FF"){
        			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 packet", "Link", "Recieved and discarded packet at interface " + name);
        			
        			return;
        		}
        		
        		if(state == WiFiPort.PROBE && tempWiFi.Type == 0 && tempWiFi.subType == 5){
        			ResendPackets.remove(tempWiFi.getID());
        			apBSSID = tempWiFi.getSrcBSSID();
        			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
        				   	"Link", "Recieved probe reply from " + apBSSID + " on interface "+ name);
        			ResendPackets.clear();
        			if(!isSecureEnabled()){
        				state = WiFiPort.ASSOC;
        				sendAssocReq(apBSSID);
        			}else{       			
        				state = WiFiPort.AUTH;
        				sendAuthReq(apBSSID, 0);
        			}
        		}else if(state == WiFiPort.AUTH && tempWiFi.Type == 0 && tempWiFi.subType == 11){
        			
        			if(!shared_auth && !tempWiFi.shared){
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
            				   	"Link", "Recieved successful auth reply from " + apBSSID + " on interface "+ name);
        				ResendPackets.clear();
        				state = WiFiPort.ASSOC;
        				sendAssocReq(apBSSID);
        			}else if(shared_auth && tempWiFi.shared){
        				if(tempWiFi.auth_seq == 1){
        					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Recieved auth challenge from " + apBSSID + " on interface "+ name);
            				ResendPackets.clear();
            				state = WiFiPort.AUTH;
            				sendAuthReq(apBSSID, 2);
        				}else{
        					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Recieved successful auth reply from " + apBSSID + " on interface "+ name);
            				ResendPackets.clear();
            				state = WiFiPort.ASSOC;
            				sendAssocReq(apBSSID);
        				}
        			}else{
        				ResendPackets.clear();
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 packet", "Link", "Wrong 802.11 protocol action!");
        				failed=true;
        				associated=false;
        				DOWN();
        			}
        			
        		}else if(state == WiFiPort.ASSOC && tempWiFi.Type == 0 && tempWiFi.subType == 1){
        			ResendPackets.remove(tempWiFi.getID());
        			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
        				   	"Link", "Recieved assoc reply from " + apBSSID + " on interface "+ name + ". Association successfull.");
        			state = WiFiPort.DATA;
        			ResendPackets.clear();
        			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
        				   	"Link", "Associated at AP " + apBSSID + "/" + SSID + ".");
        			associated = true;
        		}else if(state == WiFiPort.DATA && tempWiFi.Type == 1 && tempWiFi.subType == 12){
        			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Control Packet", 
        				   	"Link", "Recieved CTS packet from " + tempWiFi.getSrcBSSID() + " on interface "+ name + ".");
        			lineFree=true;        			
        		}
        	}else if(Mode == WiFiPort.MODE_AP){
        		
        		if(tempWiFi.Type == 0 && tempWiFi.subType == 4){
        			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
        				   	"Link", "Recieved probe req from " + tempWiFi.getSrcBSSID() + " on interface "+ name);
        			
        			if(APClients.get(tempWiFi.getSrcBSSID()) != null){
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11", 
            				   	"Link", "Connection state with " + tempWiFi.getSrcBSSID() + " cleared.");
            			APClients.remove(tempWiFi.getSrcBSSID());
        			}
        			  			
        			APClients.put(tempWiFi.getSrcBSSID() , new APClient(tempWiFi.getSrcBSSID()));
        			
        			sendProbeReply(tempWiFi.getSrcBSSID(), tempWiFi.getID());
        		}else if(tempWiFi.Type == 0 && tempWiFi.subType == 11){
        			if(APClients.get(tempWiFi.getSrcBSSID()) != null){
            			APClients.put(tempWiFi.getSrcBSSID() , new APClient(tempWiFi.getSrcBSSID()));
        			}
        			
        			if(!shared_auth && !tempWiFi.shared){
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
            				   	"Link", "Recieved auth req from " + apBSSID + " on interface "+ name);
        				ResendPackets.clear();
        				state = WiFiPort.AUTH;
        				sendAuthReply(tempWiFi.getSrcBSSID(), 1);
        			}else if(shared_auth && tempWiFi.shared){
        				if(tempWiFi.auth_seq == 0){
        					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Recieved auth req from " + apBSSID + " on interface "+ name);
            				ResendPackets.clear();
            				state = WiFiPort.AUTH;
            				sendAuthReply(tempWiFi.getSrcBSSID(), 1);
        				}else{
        					Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
                				   	"Link", "Recieved auth challenge reply from " + apBSSID + " on interface "+ name);
            				ResendPackets.clear();
            				state = WiFiPort.AUTH;
            				sendAuthReply(tempWiFi.getSrcBSSID(), 3);
        				}        				
        			}else{
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 packet", "Link", "Wrong 802.11 protocol action!");
        				if(APClients.get(tempWiFi.getSrcBSSID()) != null){
        					APClients.remove(tempWiFi.getSrcBSSID());
        				}
        			}
        		}else if(tempWiFi.Type == 0 && tempWiFi.subType == 0){
        			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Management Packet", 
        				   	"Link", "Recieved assoc req from " + tempWiFi.getSrcBSSID() + " on interface "+ name);
        			
        			if(APClients.get(tempWiFi.getSrcBSSID()) != null){
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11", 
            				   	"Link", "Station " + tempWiFi.getSrcBSSID() + " associated.");
        				
        				APClients.get(tempWiFi.getSrcBSSID()).state = WiFiPort.DATA;
        				sendAssocReply(tempWiFi.getSrcBSSID(), tempWiFi.getID());            			
        			}else{
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 packet", "Link", "Wrong 802.11 protocol action!");
        			}
        				
        		}else if(tempWiFi.Type == 1 && tempWiFi.subType == 11){
        			if(APClients.get(tempWiFi.getSrcBSSID()) != null){
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Control Packet", 
        				   	"Link", "Recieved RTS packet from " + tempWiFi.getSrcBSSID() + " on interface "+ name + ".");
        				sendCTS(tempWiFi.getSrcBSSID());
        			}else{
        				Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Control packet", "Link", "Wrong 802.11 protocol action!");
        			}
       			
        		}
        		
        	}
        	
        }

        return;
	}

	protected void sendPacket(Packet outPacket) throws LowLinkException{

        if(!parentNode.On) return;
        
        if(Mode == WiFiPort.MODE_AP){
        	
        	Ethernet_packet tempPacket = (Ethernet_packet)outPacket;
    		
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "Ethernet Packet", 
				"Link", "Sending packet from interface "+ MACAddress);
		
			W80211_packet tempWiFi =  new W80211_packet(tempPacket, BSSID, tempPacket.getDestinationMACAddress(), 2, 0, SSID);
			
			if(isSecureEnabled())
				tempWiFi.Cypher(WEP_keys[0], 0);
		
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Data Packet", 
			   	"Link", "Sending packet from interface "+ name);
       	
        	sendWirelessConfirmPacket(tempWiFi, 200);
        	
        }else if(Mode == WiFiPort.MODE_STATION){
        	
        	if(failed){
        		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "Ethernet Packet", 
        				"Link", "Can't send over air: all AP connections failed");
        		return;
        	}
        	
        	if(!associated && state == WiFiPort.OFFLINE){        		
        		Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "Ethernet Packet", 
        				"Link", "Can't send over air: no AP; AP searching process will be started immediately");
        		
        		associate();
        		        		
        		return;
        	}
        	
        	// else put packet to buffer....
        	           
        	Ethernet_packet tempPacket = (Ethernet_packet)outPacket;
		
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "Ethernet Packet", 
				"Link", "Sending packet from interface "+ MACAddress);
		
			W80211_packet tempWiFi =  new W80211_packet(tempPacket, BSSID, apBSSID, 2, 0, SSID);
			
			if(isSecureEnabled())
				tempWiFi.Cypher(WEP_keys[0], 0);
			
			Simulation.addLayerInfo(getClass().getName(), parentNode.getName(), "802.11 Data Packet", 
			   	"Link", "Sending packet from interface " + name + " to " + tempWiFi.getDstBSSID());
				
			sendWirelessConfirmPacket(tempWiFi, 200);
		}

	}
	
	protected void sendPacket(Packet inPacket, String inMacAddress) throws LowLinkException {
		
		Ethernet_packet tempPacket = new Ethernet_packet(inPacket,inMacAddress, MACAddress);
		
		sendPacket(tempPacket);

	}

	@Override
	public int getAcceptedPacketPercent() {
		return 100;
	}
	
	@Override
	public int getInterfaceBandwidth() {
		return 54;
	}
	
}

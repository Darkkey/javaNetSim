/*
 * 
 DNS.java
 *
 * Created on 09 Dec 2007 , 14:09
 * 
 */

package core.protocolsuite.tcp_ip;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LayerInfo;
import core.LowLinkException;
import core.Pair;
import core.Simulation;
import core.TransportLayerException;

/**
 *
 * @author Gek
 */
public class DNS extends Application
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2930522265631314366L;
	long utc1;
    public int received;
    public Hashtable<String,Vector<String>> records_a = new Hashtable<String,Vector<String>>();
    public Hashtable<String,Vector<String>> records_ptr = new Hashtable<String,Vector<String>>();
    public Hashtable<String,Vector<String>> records_cname = new Hashtable<String,Vector<String>>();
    public Hashtable<String,Vector<Pair>> records_mx = new Hashtable<String,Vector<Pair>>();	// pair(name,priority)
    public Hashtable<String,String> records_hinfo = new Hashtable<String,String>();
    public String DomainName = "";
    public String PrimaryNameServer = "";
    public String MailboxResponsiblePerson = "";
    public int RefreshTime = 86400;
    public int RefreshRetryTime = 3600;
    public int AuthorityExpireTime = 1209600;
    public int MinimumTTLZoneInfo = 86400;
    public boolean listening = false;
    public Hashtable<Integer,DNS_Message> receivedMessages = new Hashtable<Integer,DNS_Message>();
    private int last_sent_id = 0;
    
    /** Creates a new instance of DNS */
    public DNS(ProtocolStack inParentStack, int listenPort, int appType, long UID)
    {
        super(inParentStack, listenPort, appType, UID);
        appSock = mParentStack.SL().socket(jnSocket.UDP_socket, this);
    }
    
    @Override
	public void Timer(int code){ }
    
    /**
    * This method start to listen on application port
    **/
    @Override
	public void Listen() throws TransportLayerException{
        //      
        try{            
            mParentStack.SL().bind(appSock, mParentStack.getSrcIP(), listenPort);
            mParentStack.SL().listen(appSock);
            printLayerInfo("DNS app", "DNS server starts listening in port " + listenPort + ".");
            listening = true;
        } catch (Exception e)
        {
            printLayerInfo("Echo app", "Error: cannot bind port " + listenPort + ".");
            throw new TransportLayerException("Cannot bind port " + listenPort + "."); 
        }
    }
    
    @Override
	public void Accept(int listenSock, int sessionSock){
        
    }
    
    /**
    * This method stop listening on application port
    * @author key
    * @change_by Gek
    * @param Protocol Stack 
    * @return Nothing.
    * @version v0.01
    */
    @Override
	public void Close() throws TransportLayerException
    {
        if(appType == 0){
            printLayerInfo("DNS application", "DNS application closed socket."); 
        }else{
            printLayerInfo("DNS application", "DNS server closed socket.");
            listening = false;
        }
        mParentStack.SL().close(appSock);
    }
    
    @Override
	public void Free() throws TransportLayerException
    {
        if(appType == 0){
            printLayerInfo("DNS application", "DNS application freed socket.");
        }else{
            printLayerInfo("DNS application", "DNS server freed socket.");
            listening = false;
        }
        mParentStack.SL().free(appSock);
    }
    
     /**
    * This method connects to server on the other side (imaginations from the other side.... :+)
    * This is "fake" method cos of UDP
    * @author key
    * @param Host - hostname or ip of server.   
    * @param port - server's port
    * @version v0.01
    */
        
    @Override
	public boolean Connect(String Host, int port) throws TransportLayerException, InvalidNetworkLayerDeviceException, CommunicationException, LowLinkException 
    {     
        //clientPort = mParentStack.SL().reserveUDPPort(this, sdHost, sdPort);
        //if (clientPort>0) return true; else return false;        
        return mParentStack.SL().connect(appSock, Host, port);
    }   
    
    /**
    * This method should be called from UDP when client connects to server
    * @author key
    * @version v0.01
    */
    @Override
	public void OnConnect(int sock){
    }
    
    /**
    * This method is called when client disconnected from server.
    * @author key
    * @version v0.01
    */

    @Override
	public void OnDisconnect(int sock){
        
    }
    
    @Override
	public void OnError(int sock, int error){
        
    }
        
    /**
    * This method sends data to the other side.
    * @param data to send
    * @author Gek
    * @version v0.01
    */

    public int SendMessage(String host, int port, String mess, int qType) throws CommunicationException, LowLinkException, InvalidNetworkLayerDeviceException, TransportLayerException
    {
        if (Connect (host, port))
        {
        	if(DNS.isValidName(mess)){
	            SendData(mess.toLowerCase()+":"+Integer.toString(qType));
	            return last_sent_id;
        	}
        }
        return 0;
    }
    
    @Override
	public void SendData(int sock, String mess) throws LowLinkException, TransportLayerException, CommunicationException
    {
        //send request A-type and other dafault prametres
        String sdHost = mParentStack.SL().get_socket(sock).dst_IP;
        int sdPort = mParentStack.SL().get_socket(sock).dst_port;
        Vector<DNS_Message.Query> vmess = new Vector<DNS_Message.Query>(1);
        String smess[] = mess.split(":");
        vmess.add(new DNS_Message.Query(smess[0],Integer.parseInt(smess[1]),0));
        DNS_Message dnsMes = new DNS_Message(0, 0, 0, 1, 0, 1, 0, 0, vmess, null, null, null);
        last_sent_id = dnsMes.getID();
        mParentStack.SL().writeTo(sock, dnsMes.toString(), sdHost, sdPort);
        
        //processing the protocol doings.
    }
    
    
    public void SendData(int sock, DNS_Message dnsMes) throws LowLinkException, TransportLayerException, CommunicationException
    {
        String sdHost = mParentStack.SL().get_socket(sock).dst_IP;
        int sdPort = mParentStack.SL().get_socket(sock).dst_port;
        mParentStack.SL().writeTo(sock,dnsMes.toString(),sdHost, sdPort);
    }
    
    /**
    * This method receives DNS data from the other side.
    * @param data to recv
    * @author Gek
    * @version v0.01
    */
    @Override
	public void RecvData(int sock, String Data) throws LowLinkException, TransportLayerException {
        received++;
        //processing the protocol doings.
        if(appType == 0){
            //client processing receive request
            // printing some ...
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("DNS Protocol Data");
            protInfo.setLayer("Application ");
            //protInfo.setDescription("Receiving DNS message from server.");
           // Simulation.addLayerInfo(protInfo);
            DNS_Message dnsMsg = new DNS_Message(Data);
            String desc = "";
            if (dnsMsg.getRCode()==3)
            {
                desc = "No "+DNS_Message.getTypeString(dnsMsg.getQuery().get(0).type)+" record found for '"+dnsMsg.getQuery().get(0).name+"'.";
            }
            else if(dnsMsg.getRCode()==0)
            {
            	receivedMessages.put(dnsMsg.getID(), dnsMsg);
//                Vector<DNS_Message.Query> query = dnsMsg.getQuery();
                Vector<DNS_Message.Answer> answer = dnsMsg.getAnswer();
                for(int i=0; i<answer.size(); i++){
                	DNS_Message.Answer ans = answer.get(i);
                	desc += (i==0?"":"; ")+ans.name+" "+DNS_Message.getTypeString(ans.type)+" "+ans.resource;
                }
//                int qType = dnsMsg.getQueryType();
//                if (qType == DNS_Message.A_QUERY_TYPE)
//                    protInfo.setDescription("Host '"+query.substring(1, query.length()-1) + "' has IP-address '" + answer + "'.");
//                else if (qType == DNS_Message.PTR_QUERY_TYPE)
//                    protInfo.setDescription("IP-address '"+query.substring(1) + "' conform to host '" + answer.substring(0, answer.length()-1) + "'.");
//                else if (qType == DNS_Message.HINFO_QUERY_TYPE)
//                    protInfo.setDescription("HINFO section for host "+query.substring(1, query.length()-1)+": '"+ answer + "'.");                    
//                else if (qType == DNS_Message.MX_QUERY_TYPE)
//                    protInfo.setDescription("For '"+query.substring(1, query.length()-1)+"' mail exchanger(s) = " + answer + ".");
            }
            protInfo.setDescription(desc);
            Simulation.addLayerInfo(protInfo);
            //protInfo.setDescription(Data);
            //mParentStack.UDP().closePort(sock);

        }else{
            //server processing receive
          try{
            String sdHost = mParentStack.SL().get_socket(sock).dst_IP;
            int sdPort = mParentStack.SL().get_socket(sock).dst_port;
            
            LayerInfo protInfo = new LayerInfo(getClass().getName());
            protInfo.setObjectName(mParentStack.getParentNodeName());
            protInfo.setDataType("Echo Protocol Data");
            protInfo.setLayer("Application ");
            protInfo.setDescription("Receiving DNS message '" + Data + "' from client " + sdHost + ":" + sdPort + ".");
            Simulation.addLayerInfo(protInfo);
            
            DNS_Message inputMes = new DNS_Message(Data);
            DNS_Message answerPack;
            //parse query and search in HashTable
            Vector<DNS_Message.Query> query = inputMes.getQuery();
            LinkedList<DNS_Message.Query> qus = new LinkedList<DNS_Message.Query>();
            for(int i=0; i<query.size(); i++){
            	qus.add(query.get(i));
            }
            Vector<DNS_Message.Answer> answer = new Vector<DNS_Message.Answer>(0);
            while(qus.size()>0){
            	DNS_Message.Query qu = qus.getFirst();
            	qus.removeFirst();
            	switch(qu.type){
	            	case DNS_Message.A_QUERY_TYPE: {
	            		if(records_a.containsKey(qu.name)){
		            		Vector<String> ans = records_a.get(qu.name);
		            		for(int i=0; i<ans.size(); i++)
		            			answer.add(new DNS_Message.Answer(qu.name, qu.type, DNS_Message.TTL_DEFAULT, ans.get(i),0,0));
		            	}
	            		if(records_cname.containsKey(qu.name)){
		            		Vector<String> ans = records_cname.get(qu.name);
		            		for(int i=0; i<ans.size(); i++){
		            			answer.add(new DNS_Message.Answer(qu.name, DNS_Message.CNAME_QUERY_TYPE, DNS_Message.TTL_DEFAULT, ans.get(i),0,0));
		            			qus.add(0, new DNS_Message.Query(ans.get(i),DNS_Message.A_QUERY_TYPE,0));
		            		}
		            	}
	            		break;
	                }
	                case DNS_Message.CNAME_QUERY_TYPE: {
	                	if(records_cname.containsKey(qu.name)){
		            		Vector<String> ans = records_cname.get(qu.name);
		            		for(int i=0; i<ans.size(); i++){
		            			answer.add(new DNS_Message.Answer(qu.name, qu.type, DNS_Message.TTL_DEFAULT, ans.get(i),0,0));
		            			qus.add(0, new DNS_Message.Query(ans.get(i),DNS_Message.CNAME_QUERY_TYPE,0));
		            		}
		            	}
	                	break;
	                }
	                case DNS_Message.PTR_QUERY_TYPE: {
	                	if(records_ptr.containsKey(qu.name)){
		            		Vector<String> ans = records_ptr.get(qu.name);
		            		for(int i=0; i<ans.size(); i++)
		            			answer.add(new DNS_Message.Answer(qu.name, qu.type, DNS_Message.TTL_DEFAULT, ans.get(i),0,0));
		            	}
	                	break;
	                }
	                case DNS_Message.HINFO_QUERY_TYPE: {
	                	if(records_hinfo.containsKey(qu.name)){
	            			answer.add(new DNS_Message.Answer(qu.name, qu.type, DNS_Message.TTL_DEFAULT, records_hinfo.get(qu.name),0,0));
		            	}
	            		if(records_cname.containsKey(qu.name)){
		            		Vector<String> ans = records_cname.get(qu.name);
		            		for(int i=0; i<ans.size(); i++){
		            			answer.add(new DNS_Message.Answer(qu.name, DNS_Message.CNAME_QUERY_TYPE, DNS_Message.TTL_DEFAULT, ans.get(i),0,0));
		            			qus.add(0, new DNS_Message.Query(ans.get(i),DNS_Message.A_QUERY_TYPE,0));
		            		}
		            	}
	                	break;
	                }
	                case DNS_Message.MX_QUERY_TYPE: {
	                	if(records_mx.containsKey(qu.name)){
		            		Vector<Pair> ans = records_mx.get(qu.name);
		            		for(int i=0; i<ans.size(); i++)
		            			answer.add(new DNS_Message.Answer(qu.name, qu.type, DNS_Message.TTL_DEFAULT, (String)ans.get(i).getFirst(),((Integer)ans.get(i).getSecond()).intValue(),0));
		            	}
	            		if(records_cname.containsKey(qu.name)){
		            		Vector<String> ans = records_cname.get(qu.name);
		            		for(int i=0; i<ans.size(); i++){
		            			answer.add(new DNS_Message.Answer(qu.name, DNS_Message.CNAME_QUERY_TYPE, DNS_Message.TTL_DEFAULT, ans.get(i),0,0));
		            			qus.add(0, new DNS_Message.Query(ans.get(i),DNS_Message.A_QUERY_TYPE,0));
		            		}
		            	}
	                	break;
	                }
            	}
            }
            if (answer.size()>0)
                answerPack = new DNS_Message(inputMes.getID(), 1,0,1,0,1,0,0,query,answer,null,null);
            else
            	answerPack = new DNS_Message(inputMes.getID(), 1,0,1,0,1,0,3,query,answer,null,null);
            
            String answerMes = "";
            for(int i=0; i<answer.size(); i++){
            	DNS_Message.Answer ans = answer.get(i);
            	answerMes += (i==0?"":"; ")+ans.name+" "+DNS_Message.getTypeString(ans.type)+" "+ans.resource;
            }
            LayerInfo protInfo2 = new LayerInfo(getClass().getName());
            protInfo2.setObjectName(mParentStack.getParentNodeName());
            protInfo2.setDataType("DNS Protocol Data");
            protInfo2.setLayer("Application ");
            protInfo2.setDescription("Sending DNS message '" + answerMes + "' to client.");
            Simulation.addLayerInfo(protInfo2);
            SendData(appSock,answerPack);
            
            /*LayerInfo protInfo3 = new LayerInfo(getClass().getName());
            protInfo3.setObjectName(mParentStack.getParentNodeName());
            protInfo3.setDataType("Echo Protocol Data");
            protInfo3.setLayer("Application ");
            protInfo3.setDescription("Server closing connection. Now listening on " + listenPort + ".");
            Simulation.addLayerInfo(protInfo3);*/
            
            //Close();
            //Listen();
          }catch(Exception e){
        	  e.printStackTrace();
          }
        }
    }
    
    public void addRecord(String name, String value, int type){
    	String dname = name.toLowerCase();
    	switch(type){
	    	case DNS_Message.A_QUERY_TYPE: {
	    		if(records_a.containsKey(dname)){
            		Vector<String> ans = records_a.get(dname);
            		boolean found = false;
            		for(int i=0; i<ans.size() && !found; i++){
            			found = ans.get(i).equalsIgnoreCase(value);
            		}
            		if(!found){
            			ans.add(value.toLowerCase());
            		}
            	}
	    		else{
	    			Vector<String> ans = new Vector<String>(1);
	    			ans.add(value.toLowerCase());
	    			records_a.put(dname, ans);
	    		}
	        	break;
	        }
	        case DNS_Message.CNAME_QUERY_TYPE: {
	    		if(records_cname.containsKey(dname)){
            		Vector<String> ans = records_cname.get(dname);
            		boolean found = false;
            		for(int i=0; i<ans.size() && !found; i++){
            			found = ans.get(i).equalsIgnoreCase(value);
            		}
            		if(!found){
            			ans.add(value.toLowerCase());
            		}
            	}
	    		else{
	    			Vector<String> ans = new Vector<String>(1);
	    			ans.add(value.toLowerCase());
	    			records_cname.put(dname, ans);
	    		}
	        	break;
	        }
	        case DNS_Message.PTR_QUERY_TYPE: {
	    		if(records_ptr.containsKey(dname)){
            		Vector<String> ans = records_ptr.get(dname);
            		boolean found = false;
            		for(int i=0; i<ans.size() && !found; i++){
            			found = ans.get(i).equalsIgnoreCase(value);
            		}
            		if(!found){
            			ans.add(value.toLowerCase());
            		}
            	}
	    		else{
	    			Vector<String> ans = new Vector<String>(1);
	    			ans.add(value.toLowerCase());
	    			records_ptr.put(dname, ans);
	    		}
	        	break;
	        }
	        case DNS_Message.HINFO_QUERY_TYPE: {
	    		records_hinfo.put(dname, value);
	        	break;
	        }
	        case DNS_Message.MX_QUERY_TYPE: {
	        	String[] mxvalue = value.split(":");
	    		if(records_mx.containsKey(dname)){
            		Vector<Pair> ans = records_mx.get(dname);
            		boolean found = false;
            		int i;
            		for(i=0; i<ans.size() && !found; i++){
            			found = ((String)ans.get(i).getFirst()).equalsIgnoreCase(mxvalue[0]);
            		}
            		if(found){
            			ans.set(i, new Pair(mxvalue[0].toLowerCase(),Integer.valueOf(mxvalue[1])));
            		}
            		else{
            			ans.add(new Pair(mxvalue[0].toLowerCase(),Integer.valueOf(mxvalue[1])));
            		}
            	}
	    		else{
	    			Vector<Pair> ans = new Vector<Pair>(1);
	    			ans.add(new Pair(mxvalue[0].toLowerCase(),Integer.valueOf(mxvalue[1])));
	    			records_mx.put(dname, ans);
	    		}
	        	break;
	        }
    	}
    }
    
    public boolean removeRecord(String name, String value, int type){
    	String dname = name.toLowerCase();
    	boolean result = false;
    	switch(type){
	    	case DNS_Message.A_QUERY_TYPE: {
	    		if(records_a.containsKey(dname)){
            		Vector<String> ans = records_a.get(dname);
            		boolean found = false;
            		for(int i=0; i<ans.size() && !found; i++){
            			found = ans.get(i).equalsIgnoreCase(value);
            			if(found){
            				ans.remove(i);
        		    		result = true;
            			}
            		}
            	}
	        	break;
	        }
	        case DNS_Message.CNAME_QUERY_TYPE: {
	    		if(records_cname.containsKey(dname)){
            		Vector<String> ans = records_cname.get(dname);
            		boolean found = false;
            		for(int i=0; i<ans.size() && !found; i++){
            			found = ans.get(i).equalsIgnoreCase(value);
            			if(found){
            				ans.remove(i);
        		    		result = true;
            			}
            		}
            	}
	        	break;
	        }
	        case DNS_Message.PTR_QUERY_TYPE: {
	    		if(records_ptr.containsKey(dname)){
            		Vector<String> ans = records_ptr.get(dname);
            		boolean found = false;
            		for(int i=0; i<ans.size() && !found; i++){
            			found = ans.get(i).equalsIgnoreCase(value);
            			if(found){
            				ans.remove(i);
        		    		result = true;
            			}
            		}
            	}
	        	break;
	        }
	        case DNS_Message.HINFO_QUERY_TYPE: {
	        	if(records_hinfo.containsKey(dname)){
		    		records_hinfo.remove(dname);
		    		result = true;
            	}
	        	break;
	        }
	        case DNS_Message.MX_QUERY_TYPE: {
	        	String[] mxvalue = value.split(":");
	    		if(records_mx.containsKey(dname)){
            		Vector<Pair> ans = records_mx.get(dname);
            		boolean found = false;
            		for(int i=0; i<ans.size() && !found; i++){
            			found = ((String)ans.get(i).getFirst()).equalsIgnoreCase(mxvalue[0]);
            			if(found){
            				ans.remove(i);
        		    		result = true;
            			}
            		}
            	}
	        	break;
	        }
    	}
    	return result;
    }
    
    public static String toInAddrArpa(String ip){
		String[] ipn = ip.split("\\.");
		try{
			if(ipn.length==4){
				for(int i=0; i<4; i++){
					int num = Integer.parseInt(ipn[i]);
					if(num<0 || num>255) throw new NumberFormatException();
				}
				return ipn[3]+"."+ipn[2]+"."+ipn[1]+"."+ipn[0]+".in-addr.arpa";
			}
		}
		catch(NumberFormatException e){};
		return null;
    }
    
    public static String fromInAddrArpa(String arpa){
		String[] ipn = arpa.split("\\.");
		if(ipn.length==6 && ipn[4].equalsIgnoreCase("in-addr") && ipn[5].equalsIgnoreCase("arpa"))
			return ipn[3]+"."+ipn[2]+"."+ipn[1]+"."+ipn[0];
		return null;
    }
    
    public static boolean isValidName(String name){
    	if(name.length()==0) return false;
    	String dname = name.toLowerCase();
    	for(int i=0; i<dname.length(); i++){
    		char c = dname.charAt(i);
    		if(!((c>='0' && c<='9') || (c>='a' && c<='z') || c=='-'  || c=='.')) return false;
    	}
    	return (DNS.toInAddrArpa(dname)==null); // name is not valid IP-address
    }
    
    public static boolean isValidMail(String name){
    	if(name.length()==0) return false;
    	String dname = name.toLowerCase();
    	for(int i=0; i<dname.length(); i++){
    		char c = dname.charAt(i);
    		if(!((c>='0' && c<='9') || (c>='a' && c<='z') || c=='-'  || c=='.' || c=='@')) return false;
    	}
    	return true;
    }
}

/*
 * ApplicationLayerDevice.java
 *
 * Created on 19 Nov 2005, 15:48
 *
 */

package core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import core.protocolsuite.tcp_ip.Application;
import core.protocolsuite.tcp_ip.DNS;
import core.protocolsuite.tcp_ip.DNS_Message;
import core.protocolsuite.tcp_ip.IPV4Address;

/**
 *
 * @author key
 */
public class ApplicationLayerDevice extends NetworkLayerDevice{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4518741342608544289L;
	public final static int ECHO_SERVER_ID = 7;
    public final static int ECHO_CLIENT_ID = 30007;
    public final static int ECHO_TCP_SERVER_ID = 17;
    public final static int ECHO_TCP_CLIENT_ID = 30017;
    public final static int TELNET_SERVER_ID = 23;
    public final static int TELNET_CLIENT_ID = 30023;
    public final static int POSIX_TELNET_CLIENT_ID = 10023;
    public final static int SNMP_AGENT_ID = 161;
    public final static int SNMP_MANAGER_ID = 30161;
    public final static int DHCP_SERVER_ID = 67;
    public final static int DHCP_CLIENT_ID = 68;
    public final static int DNS_SERVER_ID = 90;
    public final static int DNS_CLIENT_ID = 91; 
    public final static int RIP_SERVER_ID = 520;	
	
    protected Hashtable<Integer, Application> Apps = null;
    protected Hashtable<String,String> userlist = new Hashtable<String,String>();
    private String nameServer = ""; 
    /** Creates a new instance of ApplicationLayerDevice */
    public ApplicationLayerDevice(String inName, int inProtocolStackLayers, boolean inOn) {
        super(inName, inProtocolStackLayers, inOn);
        Apps = new Hashtable<Integer, Application>();
        if(inOn) initApplications();
    } 
    
    public void addApp(Application app, int code){
        Apps.put(code, app);
    }
    
    public Application getApp(int code){
        if(Apps==null) return null;
        return Apps.get(code);
    }
    
    @Override
	public void turnOn(){
        super.turnOn();
        //initApplications();
    }
    
    @Override
	public void turnOff(){
        Enumeration<Application> ap = Apps.elements();
        while(ap.hasMoreElements()){
            Application appl = ap.nextElement();
            try{
                appl.Free();
            }catch(TransportLayerException e){
                e.printStackTrace();
            }
//            catch(Exception e){
//                System.out.println("Application "+appl.getUID()+" throws exception");
//                e.printStackTrace();
//            }
        }
        userlist.clear();
        super.turnOff();
    }
    
    @Override
	public void initApplications(){
        
    }
    
    public void addUser(String username, String password){
        if(userlist.containsKey(username)){
            userlist.remove(username);
        }
        userlist.put(username, password);
    }
    
    public void delUser(String username){
        userlist.remove(username);
    }
    
    public String getUserPassword(String username){
        if(userlist==null) return null;
        return userlist.get(username);
    }
    
    public Enumeration<String> getUserList(){
        if(userlist==null) return null;
        return userlist.keys();
    }
    
    public boolean setNameServer(String ns){
    	if(IPV4Address.isValidIp(ns)){
    		nameServer = ns;
    		return true;
    	}
    	return false;
    }
    
    public String getNameServer(){
    	return nameServer;
    }
    
    public Vector<String> resolve(String name){
    	Vector<String> out = new Vector<String>();
    	if(nameServer!=""){
	    	DNS dns = (DNS)this.getApp(core.ApplicationLayerDevice.DNS_CLIENT_ID);
	    	int id;
			try {
				id = dns.SendMessage(nameServer, 53, name, DNS_Message.A_QUERY_TYPE);
				if(dns.receivedMessages.containsKey(id)){
					DNS_Message dnsMes = dns.receivedMessages.get(id);
					Vector<DNS_Message.Answer> answer = dnsMes.getAnswer();
		            for(int i=0; i<answer.size(); i++){
		            	DNS_Message.Answer ans = answer.get(i);
		            	out.add(ans.resource);
		            }
				}
			} catch (CommunicationException e) {
			} catch (LowLinkException e) {
			} catch (InvalidNetworkLayerDeviceException e) {
			} catch (TransportLayerException e) {
			}
    	}
    	return out;
    }
}


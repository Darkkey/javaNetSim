package core;


public class WiFiInterface extends WiFiPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2285420407857276952L;

	public WiFiInterface(long UID, String inName, Node inParent) {

    	super(UID, inName,inParent);	
    	active = true;
    	Mode = 1;
    	
  	}
}

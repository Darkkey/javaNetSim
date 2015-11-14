package core;


public class FiberEthernetNetworkInterface extends EthernetNetworkInterface{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2543120389947473600L;

	protected FiberEthernetNetworkInterface(long UID, String inName, Node parent) {
             super(UID, inName,parent);
    }

    @Override
	public int getType(){
            return NetworkInterface.Ethernet100FX;
    }
        
    @Override
    public int getInterfaceBandwidth() {
    	return 100;
    }
    
}

package core;

public class FiberEthernetLink extends EthernetLink {

	/**
	 * 
	 */
	private static final long serialVersionUID = -139059828714817422L;

	public FiberEthernetLink(String inName, NetworkInterface inFirstNodeInterface, NetworkInterface inSecondNodeInterface)throws InvalidLinkConnectionException {
		super(inName, inFirstNodeInterface, inSecondNodeInterface);				
	}
        
        public FiberEthernetLink(String inName, NetworkInterface inFirstNodeInterface, NetworkInterface inSecondNodeInterface, double sieveCoeff)throws InvalidLinkConnectionException {
		super(inName, inFirstNodeInterface, inSecondNodeInterface, sieveCoeff);		
	}
	
}

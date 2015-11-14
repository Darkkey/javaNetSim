package core;




public class FiberNetworkInterfacePort extends NetworkInterfacePort{

	  

    	/**
	 * 
	 */
	private static final long serialVersionUID = -2896935432175230815L;






		protected FiberNetworkInterfacePort(long UID, String inName, Node inParent) {

    	super(UID, inName,inParent);			 

  	}



	

	
        @Override
		public int getType(){
            return NetworkInterface.Ethernet100FX;
        }
        
        @Override
        public int getInterfaceBandwidth() {
        	return 100;
        }
		
}//EOF



package core;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author key
 */
public class WANRMIServer extends UnicastRemoteObject implements WANRMI{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1790660178112345871L;
	WANNetworkInterface parentInterface;
    
    /** Creates a new instance of WANRMIServer */
    public WANRMIServer(WANNetworkInterface inParentInterface) throws RemoteException {
        super();
        parentInterface = inParentInterface;
    }
    
    public void recievePacket(String inPacket) throws RemoteException{
        try{
            parentInterface.receivePacket(inPacket);
        }catch(LowLinkException e){}
    }
    
    public void setServiceName(WANRMICallback callback) throws RemoteException{
        parentInterface.setRMICallback(callback);
        //parentInterface.connect();
    }
}

/*
 * WANRMIClient.java
 *
 * Created on 27 Ноябрь 2007 г., 17:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class WANRMIClient implements WANRMICallback {
        
    WANNetworkInterface parentInterface;
    
    /** Creates a new instance of WANRMIServer */
    public WANRMIClient(WANNetworkInterface inParentInterface) throws RemoteException {
        super();
        parentInterface = inParentInterface;
        
        try {
            UnicastRemoteObject.exportObject(this);
        }
        catch(RemoteException re) {
            re.printStackTrace();
        }
    }
    
    public void recievePacket(String inPacket) throws RemoteException{
        try{
            parentInterface.receivePacket(inPacket);
        }catch(LowLinkException e){}
    }
}


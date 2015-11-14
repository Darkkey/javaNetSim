/*
 * WANRMICallback.java
 *
 * Created on 27 Ноябрь 2007 г., 17:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

/**
 *
 * @author key
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WANRMICallback extends Remote {
     public void recievePacket(String inPacket) throws RemoteException;
}


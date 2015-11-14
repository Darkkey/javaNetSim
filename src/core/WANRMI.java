/*
 * WANRMI.java
 *
 * Created on 14 ќкт€брь 2007 г., 14:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;
import java.rmi.RemoteException;

/**
 *
 * @author key
 */
public interface WANRMI extends java.rmi.Remote {
     public void recievePacket(String inPacket) throws RemoteException;    
     
     public void setServiceName(WANRMICallback callback) throws RemoteException;
}

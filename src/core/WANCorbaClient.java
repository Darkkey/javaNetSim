/*
 * WANCorbaClient.java
 *
 * Created on 19 Ноябрь 2007 г., 16:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/**
 *
 * @author key
 */
public class WANCorbaClient {
    WANNetworkInterface parentInterface;
    WANCorba wanCorbaImpl;
    ORB orb;
    
    /** Creates a new instance of WANCorbaClient */
    public WANCorbaClient(WANNetworkInterface inParentInterface) {
        parentInterface = inParentInterface;
    }
    
    public void Connect(String ORBHostname, int ORBPort, String service){
         try{
            String args[] = {"","-ORBInitialPort",String.valueOf(ORBPort),"-ORBInitialHost",ORBHostname};
             
            orb = ORB.init(args, null);

            org.omg.CORBA.Object objRef = 
                orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            wanCorbaImpl = WANCorbaHelper.narrow(ncRef.resolve_str(service));
            
            parentInterface.addLayerInfo("Wan interface", "Connected to " + service);
            
	} catch (Exception e) {
          parentInterface.addLayerInfo("Wan interface", "CORBA Client error connecting to " + service + ": " + e.toString());
          System.out.println("ERROR : " + e) ;
          parentInterface.DOWN();
	  e.printStackTrace(System.out);
	}        
    }
    
    public void close(){
        parentInterface.addLayerInfo("Wan interface", "Shutting down CORBA Client...");
        wanCorbaImpl.shutdown();
    }
    
    public void sendPacket(String inPacket){
        wanCorbaImpl.receivePacket(inPacket);
    }
    
    public void setServiceName(String serviceName){
        parentInterface.addLayerInfo("Wan interface", "Setting service name for remote side...");
        wanCorbaImpl.setServiceName(serviceName);
    }
    
}

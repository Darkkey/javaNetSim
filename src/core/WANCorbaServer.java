package core;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

class WANCorbaImpl extends WANCorbaPOA {
  private ORB orb;
  WANNetworkInterface parentInterface;
  
  public WANCorbaImpl(WANNetworkInterface inParentInterface){
      super();
      parentInterface = inParentInterface;
  }

  public void setORB(ORB orb_val) {
    orb = orb_val; 
  }
    
  public void receivePacket (String inPacket){
      parentInterface.addLayerInfo("Wan interface", "Recieved packet from remote peer.");
      try{
          parentInterface.receivePacket(inPacket);
      }catch(Exception e){
          parentInterface.addLayerInfo("Wan interface", "LowLink Exception: " + e.toString());
      }
      System.out.println(inPacket);
  }
  public void setServiceName (String inService){
      parentInterface.addLayerInfo("Wan interface", "Recieved peer service name " + inService);
      parentInterface.setConnService(inService);      
  }
    
  // implement shutdown() method
  public void shutdown() {
    orb.shutdown(false);
  }
}


public class WANCorbaServer {
  ORB orb;
  WANNetworkInterface parentInterface;

  public WANCorbaServer(WANNetworkInterface inParentInterface, String ORBHostname, int ORBPort, String name) {
    parentInterface = inParentInterface;
    String args[] = {"","-ORBInitialPort",String.valueOf(ORBPort),"-ORBInitialHost",ORBHostname};
    parentInterface.addLayerInfo("Wan interface", "Starting CORBA Server...");
    orb = ORB.init(args, null);  
    WANCorbaServerHandler wh = new WANCorbaServerHandler(orb, name, parentInterface);
    wh.start();
  }
  
  public void close(){
    if(orb!=null){
        parentInterface.addLayerInfo("Wan interface", "Shutting down CORBA Server...");
        orb.shutdown(false);
        orb = null;
    }
  }
  
  class WANCorbaServerHandler extends Thread {
        ORB orb;
        boolean error;
        WANNetworkInterface parentInterface;
        
        WANCorbaServerHandler(ORB orb, String name, WANNetworkInterface inParentInterface) {
            parentInterface = inParentInterface;
            error = false;
            try{
                this.orb = orb;
                POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
                rootpoa.the_POAManager().activate();

                WANCorbaImpl wImpl = new WANCorbaImpl(parentInterface);
                wImpl.setORB(orb); 

                org.omg.CORBA.Object ref = rootpoa.servant_to_reference(wImpl);
                WANCorba href = WANCorbaHelper.narrow(ref);


                org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");

                NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

                NameComponent path[] = ncRef.to_name( name );
                ncRef.rebind(path, href);
                
                parentInterface.addLayerInfo("Wan interface", "Registered in NameService as " + name);
           } 
	
            catch (Exception e) {
                parentInterface.addLayerInfo("Wan interface", "Error during register in NameService as " + name + "; error: " + e.toString());
                error = true;
                e.printStackTrace(System.out);
            }
        }

        @Override
		public void run() {  
            if(error){ 
                parentInterface.DOWN();
                return;            
            }
            try{
                orb.run();
            }catch (Exception e) {
                parentInterface.addLayerInfo("Wan interface", "Error in CORBA Server: " + e.toString());
                parentInterface.DOWN();
                e.printStackTrace(System.out);
            }
        }
      
  }
}
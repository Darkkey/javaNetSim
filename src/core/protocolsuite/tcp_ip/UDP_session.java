/*
 * UDP_session.java
 *
 * Created on 18 Сентябрь 2007 г., 18:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core.protocolsuite.tcp_ip;

/**
 *
 * @author QweR
 */
public class UDP_session extends jnSession{
    
    /*statistic block*/
    private int received_datagramms=0; //counter inc when a packet is received
    private int sent_datagramm=0; //counter inc when a packet is sent
    /*end of statistic block*/
    
    /** Creates a new instance of UDP_session */
    public UDP_session(int sock) {
        super(sock);
    }
    
/*STATISTIC PART */        
        
    /**
    * This method increments received datagramms counter
    * @author gift (sourceforge.net user)
    * @param Unused
    * @return Nothing 
    * @version v0.20
    */
    public void IncReceivedDatagrammsNumber()
    {
        received_datagramms++;
    }
        
    /**
    * This method increments sent datagramms counter
    * @author gift (sourceforge.net user)
    * @param Unused
    * @return Nothing 
    * @version v0.20
    */
    public void IncSentDatagrammsNumber()
    {
        sent_datagramm++;    
    }
        
    /**
    * This method returns the number of received datagramms
    * @author gift (sourceforge.net user)
    * @param Unused.
    * @return int the number of received datagramms 
    * @version v0.20
    */
    public int GetReceivedDatagrammsNumber()
    {
        return received_datagramms;    
    }
    
    /**
    * This method returns the number of sent datagramms
    * @author gift (sourceforge.net user)
    * @param Unused.
    * @return int the number of sent datagramms 
    * @version v0.20
    */
    public int GetSentDatagrammsNumber()
    {
        return sent_datagramm;    
    }

    /**
    * This method resets UDP counters
    * @author gift (sourceforge.net user)
    * @param Unused.
    * @return Nothing. 
    * @version v0.20
    */
    public void ResetCounters()
    {
        sent_datagramm=0;  
        received_datagramms=0;
    }
    
}

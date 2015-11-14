
package core;
import java.io.Serializable;

/**
 *
 * @author key
 */
public abstract class TimerApp implements Serializable {
    
    protected long UID;
    
    public TimerApp(long UID){              
        this.UID = UID;        
    }
    
    public long getUID(){
        return this.UID;
    }
    
    public abstract void Timer(int code);    
}

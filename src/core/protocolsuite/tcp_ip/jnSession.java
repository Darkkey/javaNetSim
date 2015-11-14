/*
 * jnSession.java
 *
 * Created on 18 Сентябрь 2007 г., 18:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core.protocolsuite.tcp_ip;

/**
 *
 * @author QweR
 */
public abstract class jnSession {
    
    protected int sock;
    
    /** Creates a new instance of jnSession */
    public jnSession() {
        sock = -1;
    }
    
    public jnSession(int sock){
        this.sock = sock;
    }
    
    public int getSocket(){
        return sock;
    }
    
    public void setSocket(int new_sock){
        sock = new_sock;
    }
}

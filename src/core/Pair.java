/*
 * Pair.java
 *
 * Created on 8 ќкт€брь 2007 г., 0:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

/**
 *
 * @author QweR
 */
public class Pair {
    
    private Object first;
    private Object second;
    
    /** Creates a new instance of Pair */
    public Pair() {
        first = null;
        second = null;
    }
    
    public Pair(Object first, Object second){
        this.first = first;
        this.second = second;
    }
    
    public Object getFirst(){
        return first;
    }
    
    public Object getSecond(){
        return second;
    }
    
    public void setFirst(Object first){
        this.first = first;
    }
    
    public void setSecond(Object second){
        this.second = second;
    }
    
}

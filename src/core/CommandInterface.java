/*
 * CommandInterface.java
 *
 * Created on 5 ќкт€брь 2007 г., 19:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;
import java.util.Vector;

/**
 *
 * @author QweR
 */
abstract public class CommandInterface {
        
    public static final int NO_MODE = 0x01;
    public static final int STD_MODE = 0x02;
    public static final int CONF_MODE = 0x04;
    public static final int STD_CONF_MODE = CommandInterface.STD_MODE | CommandInterface.CONF_MODE;
    public static final int ALL_MODES =  CommandInterface.NO_MODE | CommandInterface.STD_MODE | CommandInterface.CONF_MODE;
    
    public static final int NO_LAYER = 0x01;
    public static final int NETWORK_LAYER = 0x02 | CommandInterface.NO_LAYER;
    public static final int TRANSPORT_LAYER = 0x04 | CommandInterface.NETWORK_LAYER;
    public static final int APPLICATION_LAYER = 0x08 | CommandInterface.TRANSPORT_LAYER;
    public static final int LAYERS = CommandInterface.APPLICATION_LAYER;
    
    public static final int CALL_ONLY = 0x01;
    public static final int NO_CALL = 0x02;
    public static final int CALLS =  CommandInterface.CALL_ONLY | CommandInterface.NO_CALL;
    
        
    static public class Modes{
        public int conf_mode;
        public int layer_mode;
        public int cmd_mode;
        
        public Modes(){
            conf_mode = CommandInterface.NO_MODE;
            layer_mode = CommandInterface.NO_LAYER;
            cmd_mode = CommandInterface.CALL_ONLY;
        }
        public Modes(int conf, int layer, int cmd){
            conf_mode = conf;
            layer_mode = layer;
            cmd_mode = cmd;
        }
        public void set(Modes mode){
            conf_mode = mode.conf_mode;
            layer_mode = mode.layer_mode;
            cmd_mode = mode.cmd_mode;
        }
        public void or(Modes mode){
            conf_mode |= mode.conf_mode;
            layer_mode |= mode.layer_mode;
            cmd_mode |= mode.cmd_mode;
        }
        public boolean isContain(Modes mode){
            return ((conf_mode & mode.conf_mode)!=0 && (layer_mode & mode.layer_mode)!=0 && (cmd_mode & mode.cmd_mode)!=0);
        }
    }
    
    public Modes modes; 
    public String call_params;
    public String no_call_params;
    
    
    public CommandInterface(){
        modes = new Modes(CommandInterface.NO_MODE, CommandInterface.NO_LAYER, CommandInterface.CALL_ONLY);
    }
    
    abstract public String call(Vector<String> params);
    
    public String no_call(@SuppressWarnings("unused") Vector<String> params){
        return "Function not implemented!!!";
    }
}

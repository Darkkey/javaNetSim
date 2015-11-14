/*
 * CommandsTree.java
 *
 * Created on 5 ������� 2007 �., 19:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

import java.util.LinkedList;
import java.util.Vector;

import core.CommandInterface.Modes;

/**
 *
 * @author QweR
 */
public class CommandsTree {
    
    public static final String ANYWORD = "*";
    public static final String COMMENT = "!";
    
    CommandNode root = new CommandNode(".");
    
    private class CommandNode implements Comparable<CommandNode>{
        
        private String name;
        private LinkedList<CommandNode> tree;
        private CommandInterface function;
        public Modes modes;
        private String description;
        
        /** Create node of tree
         * @param name name of node
         */
        public CommandNode(String name){
            modes = new Modes();
            this.name = name;
            tree = null;
            function = null;
            description = "";
        }
        
        public CommandNode(String name, CommandInterface function, String description){
            if(function != null){
                this.modes = function.modes;
            }
            else{
                this.modes = new Modes();
            }
            this.name = name;
            tree = null;
            this.function = function;
            this.description = description;
        }
        
        public String getName(){
            return name;
        }
                
        private Vector<CommandNode> getNodes(String command, Modes mode){
            Vector<CommandNode> nodes = new Vector<CommandNode>(0);
            
            if(tree != null){
                boolean allFounds = false;
                int cmdsize = command.length();
                CommandNode anywordNode = null;
                for(int i=0; i<tree.size() && !allFounds; i++){
                    CommandNode node = tree.get(i);
                    String nodename = node.getName();
                    if(!nodename.equals(CommandsTree.ANYWORD)){
                        if(nodename.length()>cmdsize) nodename = nodename.substring(0,cmdsize);
                        int cmp = command.compareToIgnoreCase(nodename);
                        if(cmp == 0 && node.modes.isContain(mode)){
                            nodes.add(tree.get(i));
                        }
                        else if(cmp < 0){
                            allFounds = true;
                        }
                    }
                    else{
                        anywordNode = tree.get(i);;
                    }
                }
                if(nodes.size()==0 && anywordNode!=null){
                    nodes.add(anywordNode);
                }
            }
            return nodes;
        }
        
        public Vector<String> getCommands(String command, Modes mode){
            Vector<CommandNode> nodes = getNodes(command, mode);
            Vector<String> nodenames = new Vector<String>(nodes.size());
            for(int i=0; i<nodes.size(); i++){
                nodenames.add(nodes.get(i).getName());
            }
            return nodenames;
        }
        
        public CommandNode getNode(String nodename, Modes mode){
            Vector<CommandNode> nodes = getNodes(nodename, mode);
            CommandNode node = null;
            if(nodes.size() == 1 || (nodes.size()>0 && nodes.get(0).getName().compareTo(nodename)==0)){
                node = nodes.get(0);
            }
            return node;
        }
        
        public CommandInterface getFunction(){
            return function;
        }
        
        public void setFunction(CommandInterface function){
            this.function = function;
        }
        
        public String getDescription(){
            return description;
        }
        
        public void setDescription(String description){
            this.description = description;
        }

        public boolean add(CommandNode node){
            boolean result = false;
            
            if(tree == null){
                tree = new LinkedList<CommandNode>();
            }
            boolean Found = false;
            for(int i=0; i<tree.size() && !Found; i++){
                int cmp = node.compareTo(tree.get(i));
                if(cmp <= 0){
                    Found = true;
                    if(cmp < 0){
                        result = true;
                        tree.add(i, node);
                    }
                }
            }
            if(!Found){
                result = true;
                tree.add(node);
            }
            return result;
        }

//        public boolean remove(String command){
//            boolean result = false;
//            
//            if(tree != null){
//                boolean Found = false;
//                for(int i=0; i<tree.size() && !Found; i++){
//                    int cmp = command.compareToIgnoreCase(((CommandNode)tree.get(i)).getName());
//                    if(cmp == 0){
//                        Found = true;
//                        result = true;
//                        tree.remove(i);
//                    }
//                }
//            }
//            return result;
//        }

        public int compareTo(CommandNode o){
            return (name.compareToIgnoreCase(o.name));
        }
    }
    
    /** Creates a new instance of CommandsTree */
    public CommandsTree() {
    }
    
    private Modes allModes(){
        return new Modes(CommandInterface.ALL_MODES, CommandInterface.LAYERS, CommandInterface.CALLS);
    }
    
    /** Bind 'command' with 'params' to 'function' working in 'modes'
     * @param command full command line without parameters
     * @param params parameters for 'command'
     * @param function function was binded for 'command'
     * @param modes modes where works 'command'
     * @return true command was executed, false otherwise
     */
    public boolean add(String command, CommandInterface function, String description){
        boolean result = false;
        String cmds[] = command.split(" +");
        
        CommandNode node = root;
        boolean existed = false;
        for(int i=0; i<cmds.length && !existed ; i++){
            CommandNode nextnode = node.getNode(cmds[i], allModes());
            if(nextnode != null && nextnode.getName().compareTo(cmds[i])==0){
                if(i == cmds.length-1){
                    if(nextnode.getFunction()==null){
                        nextnode.modes.or(function.modes);
                        nextnode.setFunction(function);
                        if(nextnode.getDescription().equals("") && !description.equals("")){
                            nextnode.setDescription(description);
                        }
                    }
                    else{
                        existed = true;     // exit from cycle
                    }
                }
            }
            else{
                if(i == cmds.length-1){
                    nextnode = new CommandNode(cmds[i], function, description);
                }
                else{
                    nextnode = new CommandNode(cmds[i]);
                    nextnode.modes.set(function.modes);
                }
                node.add(nextnode);
            }
            node = nextnode;
        }
        if(!existed){
            // set modes for nodes in path
            node = root;
            for(int i=0; i<cmds.length && node!=null ; i++){
                node.modes.or(function.modes);
                node = node.getNode(cmds[i], allModes());
            }
            result = true;
        }
        if(function.modes.cmd_mode==CommandInterface.NO_CALL && !cmds[0].equalsIgnoreCase("no")){
            result = result && add("no "+command, function, description);
        }
        return result;
    }
    
    public void addDescription(String command, String description){
        String cmds[] = command.split(" +");
        
        CommandNode node = root;
        for(int i=0; i<cmds.length; i++){
            CommandNode nextnode = node.getNode(cmds[i], allModes());
            if(nextnode != null && nextnode.getName().compareTo(cmds[i])==0){
                if(i == cmds.length-1){
                    nextnode.setDescription(description);
                }
            }
            else{
                if(i == cmds.length-1){
                    nextnode = new CommandNode(cmds[i], null, description);
                }
                else{
                    nextnode = new CommandNode(cmds[i]);
                }
                node.add(nextnode);
            }
            node = nextnode;
        }
        if(!cmds[0].equalsIgnoreCase("no")){
            addDescription("no "+command, description);
        }
    }
    
    /** Call function binded with 'command'
     * @param command command for executing
     * @param mode mode where calling command
     * @return true if command was executed, false otherwise
     */
    public String call(String command, Modes mode){
        String result = null;
        if(command.startsWith(CommandsTree.COMMENT)){
            //result = command;
            //result = command.substring(COMMENT.length());
            result = "";
        }
        else{
            String cmds[] = command.split(" +");

            CommandInterface func = null;
            Modes modes = new Modes(0,0,0);
            Vector<String> vprms = new Vector<String>(0);

            CommandNode node = root;
            int i;
            for(i=0; i<cmds.length && node!=null && node.modes.isContain(mode); i++){
                node = node.getNode(cmds[i], mode);
                if(node != null){
                    if(node.getName().equals(CommandsTree.ANYWORD)){
                        vprms.add(cmds[i]);
                    }
                    func = node.getFunction();
                    modes = node.modes;
                }
            }
            if(func!=null && modes.isContain(mode)){
                //String sprms[] = params.split(" +");
                if(node==null) i--;
                for(; i<cmds.length; i++){
                    vprms.add(cmds[i]);
                }
                try{
                    if(cmds[0].equalsIgnoreCase("no")){
                        result = func.no_call(vprms);
                    }
                    else{
                        result = func.call(vprms);
                    }
                }
                catch(Exception e){
                    result = "Fatal error was occur. Please contact to developer.\nAdditional info: "+e.toString()+"\n";
                }
            }
        }
        return result;
    }
       
    /** Complete command line if possible
     * @param command command for completing
     * @param mode mode where completing command
     * @return complete command, or "" if completing is impossible
     */
    public String complete(String command, Modes mode){
        String result = null;
        String cmds[] = command.split(" +");
        
        if(command.endsWith(" ")){
            result = command;
        }
        else{
            CommandNode node = root;
            for(int i=0; i<cmds.length && node!=null && node.modes.isContain(mode); i++){
                node = node.getNode(cmds[i], mode);
                if(node != null && i==cmds.length-1){
                    result = node.getName();
                }
            }
            if(result!=null){
                result = command.substring(0, command.length()-cmds[cmds.length-1].length()) + result;
            }
        }
        return result;
    }
    
    /** Return all posible complete commands
     * @param command command for helping
     * @param mode mode where helping command
     * @return complete command, or "" if helping is impossible
     */
    public Vector<Pair> help(String command, Modes mode){
        Vector<Pair> result = null;
        String cmds[] = command.split(" +");

        CommandNode node = root;
        for(int i=0; i<cmds.length && node!=null && node.modes.isContain(mode); i++){
            if(i<cmds.length-1){
                CommandNode nextnode = node.getNode(cmds[i], mode);
                if(nextnode==null && node.getFunction()!=null){
                    result = new Vector<Pair>(1);
                    if(cmds[0].equalsIgnoreCase("no")){
                        result.add(new Pair(node.getFunction().no_call_params, ""));
                    }
                    else{
                        result.add(new Pair(node.getFunction().call_params, ""));
                    }
                }
                node = nextnode;
            }
            else{
                if(command.endsWith(" ")){
                    node = node.getNode(cmds[i], mode);
                    if(node!=null){
                        Vector<String> nodecmds = node.getCommands("", mode);
                        result = new Vector<Pair>(nodecmds.size()+1);
                        for(int j=0; j<nodecmds.size(); j++){
                            String nodename = nodecmds.get(j);
                            result.add(new Pair(nodename, node.getNode(nodename,mode).getDescription()));
                        }
                        if(node.getFunction()!=null){
                            if(cmds[0].equalsIgnoreCase("no")){
                                result.add(new Pair(node.getFunction().no_call_params, ""));
                            }
                            else{
                                result.add(new Pair(node.getFunction().call_params, ""));
                            }
                        }
                    }
                }
                else{
                    Vector<String> nodecmds = node.getCommands(cmds[i], mode);
                    result = new Vector<Pair>(nodecmds.size());
                    for(int j=0; j<nodecmds.size(); j++){
                        String nodename = nodecmds.get(j);
                        result.add(new Pair(nodename, node.getNode(nodename,mode).getDescription()));
                    }
                }
            }
        }
        return result;
    }
    
}

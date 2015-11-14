/*
 * TelnetEmulator.java
 *
 * Created on 22 Feb 2006, 16:38
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package guiUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import core.ApplicationLayerDevice;
import core.CommandInterface;
import core.CommandProcessor;
import core.CommunicationException;
import core.DeviceConfig;
import core.InvalidNetworkInterfaceNameException;
import core.InvalidNetworkLayerDeviceException;
import core.LowLinkException;
import core.Pair;
import core.TransportLayerException;
import core.Version;
import core.CommandInterface.Modes;
import core.protocolsuite.tcp_ip.DNS;
import core.protocolsuite.tcp_ip.DNS_Message;
import core.protocolsuite.tcp_ip.ICMP_packet;
import core.protocolsuite.tcp_ip.IPV4Address;

/**
 *
 * @author qwer
 */
public class Terminal extends JFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1484879879775885583L;
	protected final static int DEF_MODE = 0;
    protected final static int IF_MODE = 1;
    protected final static int ACL_MODE = 2;
    
    private JPanel panel;
    private JScrollPane scrollpane;
    private JTextArea terminal;
    private JTextField cmdline;
    private String text = "";
    private core.NetworkLayerDevice device;
    MainScreen parent;
    private Vector<String> history = new Vector<String>(0);
    private int pos_history=0;
    private final static int max_history = 128;
    CmdVerifier cmdverifier = new CmdVerifier();
    private Modes current_mode;
    private CommandProcessor cmdproc;
    private int interface_mode = Terminal.DEF_MODE;
    private String command_prefix = "";
    private boolean blocked = false;
    private PingThread pt;
    
    private clear_terminal_CommandClass clear_terminal_Command = new clear_terminal_CommandClass();
//    private configure_replace_CommandClass configure_replace_Command = new configure_replace_CommandClass();
//    private configure_memory_CommandClass configure_memory_Command = new configure_memory_CommandClass();
    private configure_terminal_CommandClass configure_terminal_Command = new configure_terminal_CommandClass();
    private end_CommandClass end_Command = new end_CommandClass();
    private exit_CommandClass exit_Command = new exit_CommandClass();
    private interface__exit_CommandClass interface__exit_Command = new interface__exit_CommandClass();
    private interface_CommandClass interface_Command = new interface_CommandClass();private logout_CommandClass logout_Command = new logout_CommandClass();
    private nslookup_CommandClass nslookup_Command = new nslookup_CommandClass();
    private ping_CommandClass ping_Command = new ping_CommandClass();
	private traceroute_CommandClass traceroute_Command = new traceroute_CommandClass();
    private show_history_CommandClass show_history_Command = new show_history_CommandClass();
    
    // Creates a new instance of TelnetEmulator
    public Terminal(MainScreen parent, core.NetworkLayerDevice dev) {
        super("Console: " + dev.NodeProtocolStack.getParentNodeName());
        device = dev;
        this.parent = parent;
        cmdproc = new CommandProcessor(dev);
        
        if(device instanceof core.ApplicationLayerDevice){
            current_mode = new Modes(CommandInterface.STD_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALLS);
        }
        else{
            current_mode = new Modes(CommandInterface.STD_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALLS);
        }
        
        cmdproc.add("clear terminal", clear_terminal_Command, "Clear screen");
        cmdproc.addDescription("configure","Enter configuration mode");
//        cmdproc.add("configure replace", configure_replace_Command, "Replace configure from the terminal");
//        cmdproc.add("configure memory", configure_memory_Command, "Configure from memory");
        cmdproc.add("configure terminal", configure_terminal_Command, "Configure from the terminal");
        cmdproc.add("end", end_Command, "Exit from configuration mode");
        cmdproc.add("exit", exit_Command, "Exit from current mode");
        cmdproc.add("interface", interface_Command, "");
        cmdproc.add("interface *", interface_Command, "");
        cmdproc.add("interface * end", end_Command, "Exit from configuration mode");
        cmdproc.add("interface * exit", interface__exit_Command, "Exit from current mode");
        cmdproc.add("logout", logout_Command, "Exit from the console");
        cmdproc.add("nslookup", nslookup_Command, "Resolve domain name to ip-address and vice versa");
        cmdproc.add("ping", ping_Command, "Send echo messages");
        cmdproc.add("traceroute", traceroute_Command, "Trace route to destination");
        cmdproc.add("show history", show_history_Command, "Display the session command history");
        
        panel = new JPanel();
        scrollpane = new JScrollPane();
        terminal = new JTextArea(text);
        cmdline = new JTextField();
        this.setContentPane(panel);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(cmdline, BorderLayout.SOUTH);
        panel.add(scrollpane, BorderLayout.CENTER);
        panel.setPreferredSize(new java.awt.Dimension(600,500));

        cmdline.setVisible(true);
        cmdline.setEnabled(true);
        cmdline.setFocusable(true);
        cmdline.setFocusCycleRoot(true);
        cmdline.setFocusTraversalKeysEnabled(false);
        cmdline.setInputVerifier(cmdverifier);
        cmdline.requestFocus();
        cmdline.addKeyListener(cmdverifier);

        scrollpane.setViewportView(terminal);
        terminal.setEnabled(true);
        terminal.setEditable(false);
        //terminal.setFocusable(false);
        terminal.setBackground(Color.BLACK);
        terminal.setForeground(Color.WHITE);
        terminal.setFont(new Font("Courier New", Font.PLAIN, 12));
        addToTerminal("javaNetSim console "+Version.CORE_VERSION+", "+Version.YEARS);

        this.pack();
        cmdline.requestFocusInWindow();

        this.addWindowListener( new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {                
                //printInfo();
            }
            public void windowGainedFocus(WindowEvent e) {
                cmdline.requestFocusInWindow();
            }
        });
    }

    public void exitWindow() {
        //printInfo();
        this.dispose();
    }
    
    public void printInfo(){
        parent.printLayerInfo(false);
        //!!!!!: add more headers here
    }
    
    /** Add text and prompt to terminal
     *
     */
    private void addToTerminal(String data){
        text += data+"\n"+device.NodeProtocolStack.getParentNodeName();
        if(current_mode.conf_mode == CommandInterface.CONF_MODE){
            text += "(config";
            switch(interface_mode){
                case IF_MODE: text += "-if"; break;
                case ACL_MODE: text += "-ext-nacl"; break;
                default:
            }
            text += ")";
        }
        text += "# ";
        terminal.setText(text);
    }
    
    /** Add text to terminal
     *
     */
    private void appendToTerminal(String data){
        text += data;
        terminal.setText(text);
    }
    
    private void clearTerminal(){
        text = "";
    }
    
    public void addText(String str){
    	if(str.length()>=2 && str.substring(str.length()-2).compareTo("\\\\")==0)
    		appendToTerminal(str.substring(0,str.length()-2));
    	else
    		addToTerminal(str);
    }
    
    public void setBlocked(boolean block){
    	blocked = block;
    }
    
    public boolean isBlocked(){
    	return blocked;
    }
    
    public void sendSymbol(String symbol){
    	if(!isBlocked()){
    		if(symbol.length()==1)
    			switch(symbol.charAt(0)) {
    			case 0x0A: {
    				if(cmdline.getText().compareTo("")!=0) {
    					if(history.size()>=Terminal.max_history) history.remove(0);
    					history.add(cmdline.getText());
    					pos_history = history.size();
    				}
    				String tmptext = "";
    				if(cmdline.getText().compareTo("")!=0){
    					appendToTerminal(cmdline.getText() + "\n");
    					tmptext = cmdproc.call(cmdline.getText(), current_mode, command_prefix);
    					if(tmptext==null){
    						tmptext = "% Incomplete command.\n";
    					}
    				}
    				addText(tmptext);
    				cmdline.setText("");
    				//printInfo();
    				break;
    			}
    			case 0x04: {
    				exitWindow();
    				break;
    			}
    			case 0x11: {
    				interface__exit_Command.call(new Vector<String>(0));
    				addText("");
    				cmdline.setText("");
    				break;
    			}
    			case 0x1B: {
    				cmdline.setText("");
    				break;
    			}
    			case '\t': {
    				addToTerminal(cmdline.getText());
    				int caretPos = cmdline.getCaretPosition();
    				String compl_str = cmdproc.complete(cmdline.getText().substring(0,caretPos), current_mode, command_prefix);
    				//String last_str = cmdline.getText().substring(caretPos);
    				if(compl_str!=null && !compl_str.endsWith(" ")){
    					cmdline.setText(compl_str+" ");
    					//cmdline.setText(compl_str+last_str);
    				}
    				break;
    			}
    			case '?': {
    				String tmptext = cmdline.getText() + "?\n";
    				Vector<Pair> tmpv = cmdproc.help(cmdline.getText().substring(0,cmdline.getCaretPosition()), current_mode, command_prefix);
    				if(tmpv!=null){
    					for(int i=0; i<tmpv.size(); i++){
    						String first = (String)tmpv.get(i).getFirst();
    						for(int j=20-first.length(); j>0; j--){
    							first += " ";
    						}
    						tmptext += "  "+first+(String)tmpv.get(i).getSecond()+"\n";
    					}
    				}
    				addToTerminal(tmptext);
    				break;
    			}
    			default: {
    				pos_history = history.size();
    				//terminal.setText(text+"\n"+Integer.toHexString(symbol.charAt(0)));
    			}
    			}
    		else if(symbol.length()==3 && symbol.startsWith("^["))
    			switch(symbol.charAt(2)){
    			case 0x26: {
    				if(history.size()>0) {
    					if(pos_history == history.size() && cmdline.getText().compareTo("")!=0) {
    						if(history.size()>=Terminal.max_history) history.remove(0);
    						history.add(cmdline.getText());
    						pos_history = history.size()-1;
    					}
    					if(pos_history>0) {
    						cmdline.setText(history.get(--pos_history));
    					}
    					else if(pos_history>0) {
    						cmdline.setText(history.get(pos_history-1));
    					}
    				}
    				break;
    			}
    			case 0x28: {
    				if(history.size()>0) {
    					if(pos_history+1<history.size()) {
    						cmdline.setText(history.get(++pos_history));
    					}
    				}
    				break;
    			}
    			default: {
    				pos_history = history.size();
    				//terminal.setText(text+"\n"+Integer.toHexString(symbol.charAt(2)));
    			}
    			}
    		else{
    			pos_history = history.size();
    		}
    	}
    }
    
    class CmdVerifier extends InputVerifier implements KeyListener{
        public boolean verify(JComponent input){
            String text = ((JTextField)input).getText();
            boolean res = !text.contains("?");
            return res;
        }
        public boolean shouldYieldFocus(JComponent input) {
            if(!verify(input))
            {
                String text = ((JTextField)input).getText();
                ((JTextField)input).setText(text.replace("?",""));
            }
            return true;
        }
        public void keyTyped(KeyEvent e) {
            JTextField source = (JTextField)e.getSource();
            shouldYieldFocus(source);
        }
        
        public void keyReleased(KeyEvent e) {
            JTextField source = (JTextField)e.getSource();
            shouldYieldFocus(source);
        }
        
        public void keyPressed(KeyEvent e) {
            JTextField source = (JTextField)e.getSource();
            shouldYieldFocus(source);
            char c = e.getKeyChar();
            if(c>=65535) {
                sendSymbol("^["+(char)e.getKeyCode());
            }
            else {
                sendSymbol(String.valueOf(c));
            }
        }
    }
    
    class clear_terminal_CommandClass extends CommandInterface{
        public clear_terminal_CommandClass (){
            modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            clearTerminal();
            return "";
        }
    };
    class configure_terminal_CommandClass extends CommandInterface{
        public configure_terminal_CommandClass (){
            modes = new Modes(CommandInterface.STD_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            device.getConfig().working_config = DeviceConfig.RUNNING_CONFIG;
            current_mode.conf_mode = CommandInterface.CONF_MODE;
            return "Enter configuration commands, one per line. End with 'exit'";
        }
    };
//    class configure_replace_CommandClass extends CommandInterface{
//        public configure_replace_CommandClass (){
//            modes = new Modes(CommandInterface.STD_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
//            call_params = "<cr>";
//        }
//        public String call(Vector<String> params){
//            device.getConfig().working_config = DeviceConfig.RUNNING_CONFIG;
//            current_mode.conf_mode = CommandInterface.CONF_MODE;
//            device.getConfig().clear(DeviceConfig.RUNNING_CONFIG);
//            return "Running-config was erased\nEnter configuration commands, one per line. End with 'exit'";
//        }
//    };
//    class configure_memory_CommandClass extends CommandInterface{
//        public configure_memory_CommandClass (){
//            modes = new Modes(CommandInterface.STD_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
//            call_params = "<cr>";
//        }
//        public String call(Vector<String> params){
//            device.getConfig().working_config = DeviceConfig.STARTUP_CONFIG;
//            current_mode.conf_mode = CommandInterface.CONF_MODE;
//            return "Starting-config editing\nEnter configuration commands, one per line. End with 'exit'";
//        }
//    };
    class end_CommandClass extends CommandInterface{
        public end_CommandClass (){
            modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            if(current_mode.conf_mode == CommandInterface.CONF_MODE){
                interface_mode = Terminal.DEF_MODE;
                command_prefix = "";
                current_mode.conf_mode = CommandInterface.STD_MODE;
                device.getConfig().working_config = DeviceConfig.RUNNING_CONFIG;
            }
            return "";
        }
    };
    class exit_CommandClass extends CommandInterface{
        public exit_CommandClass (){
            modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            if(current_mode.conf_mode == CommandInterface.CONF_MODE){
                if(interface_mode!=Terminal.DEF_MODE){
                    interface_mode = Terminal.DEF_MODE;
                    command_prefix = "";
                }
                else{
                    current_mode.conf_mode = CommandInterface.STD_MODE;
                    device.getConfig().working_config = DeviceConfig.RUNNING_CONFIG;
                }
            }
            else if(current_mode.conf_mode == CommandInterface.STD_MODE){
                exitWindow();
            }
            return "";
        }
    };
    class interface__exit_CommandClass extends CommandInterface{
        public interface__exit_CommandClass (){
            modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            if(interface_mode!=Terminal.DEF_MODE){
                interface_mode = Terminal.DEF_MODE;
                command_prefix = "";
            }
            else{
                current_mode.conf_mode = CommandInterface.STD_MODE;
                device.getConfig().working_config = DeviceConfig.RUNNING_CONFIG;
            }
            return "";
        }
    };
    class interface_CommandClass extends CommandInterface{
        public interface_CommandClass (){
            modes = new Modes(CommandInterface.CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            String out = "";
            if(params.size()==1){
                try {
                    device.getNetworkInterface(params.get(0));
                    if(interface_mode==Terminal.DEF_MODE){
                        interface_mode = Terminal.IF_MODE;
                        command_prefix = "interface "+params.get(0);
                    }
                } catch (InvalidNetworkInterfaceNameException ex) {
                    out += "error: invalid inferface\n";
                }
            }
            else{
                out += "Invalid parameters\n";
            }
            return out;
        }
    };
    class logout_CommandClass extends CommandInterface{
        public logout_CommandClass (){
            modes = new Modes(CommandInterface.STD_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            exitWindow();
            return "";
        }
    };
    class nslookup_CommandClass extends CommandInterface{
        public nslookup_CommandClass(){
            modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.APPLICATION_LAYER, CommandInterface.CALL_ONLY);
            call_params = "(<host name>|<ip-address>) <server>[:<port>] [<query type>]";
        }
        public String call(Vector<String> params){
        	String out = "";
        	if(device instanceof ApplicationLayerDevice){
				DNS dns = (DNS)((ApplicationLayerDevice)device).getApp(core.ApplicationLayerDevice.DNS_CLIENT_ID);
				if(dns!=null){
		        	if(params.size()==2 || params.size()==3){
			        	String host = params.get(0).toLowerCase();
			        	String server[] = params.get(1).split(":");
			        	int qtype = 0;
			        	if(IPV4Address.validateDecIP(host)){
			        		host = DNS.toInAddrArpa(host);
			        		qtype = DNS_Message.PTR_QUERY_TYPE;
			        	}
			        	else if(DNS.isValidName(host)){
			        		qtype = DNS_Message.A_QUERY_TYPE;
			        	}
			        	else{
			        		out += "error: invalid name of host or IP-address\n";
			        	}
			        	if(qtype>0){
			        		int port = 53;
			        		if(IPV4Address.validateDecIP(server[0])){
			        			try{
			        				if(server.length==2) port = Integer.parseInt(server[1]);
			        				if(port<1 || port>65535) throw new NumberFormatException();
					        		if(params.size()==3) qtype = DNS_Message.getTypeInt(params.get(2));
					        		if(qtype>0){
					        			try {
											int id = dns.SendMessage(server[0], port, host, qtype);
											if(dns.receivedMessages.containsKey(id)){
												DNS_Message dnsMes = dns.receivedMessages.get(id);
												Vector<DNS_Message.Answer> answer = dnsMes.getAnswer();
								                for(int i=0; i<answer.size(); i++){
								                	DNS_Message.Answer ans = answer.get(i);
								                	out += (i==0?"":"\n")+ans.name+"\t"+DNS_Message.getTypeString(ans.type)+"\t"+ans.resource;
								                }
											}
											else{
												out += "No "+DNS_Message.getTypeString(qtype)+" record found for '"+host+"'.";
											}
										} catch (CommunicationException e) {
											out += "communitarion error\n";
										} catch (LowLinkException e) {
											out += "low link error\n";
										} catch (InvalidNetworkLayerDeviceException e) {
											out += "invalid network device error\n";
										} catch (TransportLayerException e) {
											out += "transport error\n";
										}
					        		}
					        		else{
					        			out += "error: invalid query type; valid values is A,PTR,CNAME,MX,HINFO\n";
					        		}
			        			}
			        			catch(NumberFormatException e){
			        				out += "error: invalid port number\n";
			        			}
			        		}
			        		else{
			        			out += "error: invalid server IP-address\n";
			        		}
			        	}
		        	}
		        	else{
		        		out += "error: invalid parameters\n";
		        	}
				}
				else{
					out += "This instruction not supported by device\n";
				}
			}
			else{
				out += "This instruction not supported by device\n";
			}
            return out;
        }
    };
    class PingThread extends Thread{
    	int count;
    	long delay;
    	String ip;
    	public PingThread(int count, long delay, String ip){
    		this.count = count;
    		this.delay = delay;
    		this.ip = ip;
    	}
    	
    	public void run(){
    		setBlocked(true);
    		try {
    			int success = 0;
            	for(int pi=0; pi<count; pi++){
            		ICMP_packet icmpout = null;
            		ICMP_packet icmpin = null;
            		try{
	        			icmpout = device.sendPing(ip);
	             		icmpin = device.getReceivedICMPPacket(icmpout.getMessageID());
        			} catch (CommunicationException e) {
        				// some error - skip :)
        			} catch (LowLinkException e) {
        				// some error - skip :)
        			}
					if(icmpin!=null){
						if(icmpin.getMessageCode()==ICMP_packet.DESTINATION_UNREACHABLE){
							appendToTerminal("U");
						}
						else if(icmpin.getMessageCode()==ICMP_packet.TIME_EXCEEDED){
							appendToTerminal("T");
						}
						else{
							appendToTerminal("!");
							success++;
						}
					}
					else if((icmpin = device.getReceivedICMPPacket(0))!=null){
						if(icmpin.getMessageCode()==ICMP_packet.DESTINATION_UNREACHABLE){
							appendToTerminal("U");
						}
						else if(icmpin.getMessageCode()==ICMP_packet.TIME_EXCEEDED){
							appendToTerminal("T");
						}
						device.removeReceivedICMPPacket(0);
					}
					else{
						appendToTerminal(".");
					}
					if(pi<count-1){
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
						}
					}
            	}
            	addToTerminal("\nSuccess rate is " +(int)(success*100/count)+ " percent ("+success+"/"+count+")\n");
			} catch (Exception e){
				addToTerminal("Internal error: ping: Exception: "+e.getMessage()+"\n");
			}
			setBlocked(false);
    	}
    }
    class ping_CommandClass extends CommandInterface{
        public ping_CommandClass(){
            modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<ip address>";
        }
        public String call(Vector<String> params){
        	String out = "";
        	if(params.size()==1){
	        	String ip = params.get(0);
	        	String dest = null;
	    		if(!IPV4Address.isValidIp(ip)){
	    			Vector<String> addrs = ((ApplicationLayerDevice)device).resolve(ip);
	    			if(addrs.size()>0){
	    				dest = addrs.get(0);
	    			}
	    		}
	    		else{
	    			dest = ip;
	    		}
	        	if(dest!=null){
	        		out += "Type escape sequence to abort.\n";
	                out += "Sending 5, 4-byte ICMP Echos to "+params.get(0)+", timeout is 1 second:\n";
		            pt = new PingThread(5,1000,ip);
		            pt.start();
		            out += "\\\\";
	        	}
	        	else{
	        		out += "Name '"+ip+"' was not resolved\n";
	        	}
        	}
        	else{
        		out += "error: invalid parameters\n";
        	}
            return out;
        }
    };
	class traceroute_CommandClass extends CommandInterface{
		public traceroute_CommandClass(){
			modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
			call_params = "<IP>";
		}
		public String call(Vector<String> params){
			return "Command not supported yet.\n";
		}
	};
    class show_history_CommandClass extends CommandInterface{
        public show_history_CommandClass (){
            modes = new Modes(CommandInterface.STD_CONF_MODE, CommandInterface.NETWORK_LAYER, CommandInterface.CALL_ONLY);
            call_params = "<cr>";
        }
        public String call(Vector<String> params){
            String out = "";
            for(int i=0; i<history.size(); i++){
                out += history.get(i) + "\n";
            }
            return out;
        }
    };
}

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import core.protocolsuite.tcp_ip.PosixTelnetClient;

/**
 *
 * @author key
 */
public class PosixTelnetClientGUI extends JFrame {  
    /**
	 * 
	 */
	private static final long serialVersionUID = -471040634350408360L;
	private JPanel panel;
    private JScrollPane scrollpane;
    private JTextArea terminal;
    private JTextField cmdline;
    private String text;
    //private KeyListener kl;
    //private core.NetworkLayerDevice device;
    MainScreen parent;
    private Vector<String> history = new Vector<String>(0);
    private int pos_history=0;
    private final static int max_history = 128;
    private PosixTelnetClient ptc;
    private String Host;
    private int port;
    public boolean busy;
    
    /** Creates a new instance of TelnetEmulator */
    public PosixTelnetClientGUI(MainScreen parent, core.ApplicationLayerDevice dev, String Host, int port) {
        super("Console: " + dev.NodeProtocolStack.getParentNodeName());
        //device = dev;
        this.Host = Host;
        this.port = port;
        this.parent = parent;
        
        ptc = (PosixTelnetClient) dev.getApp(10023);

        text = "Connecting to " + Host + ":" + port + "...\n";
        panel = new JPanel();
        scrollpane = new JScrollPane();
        terminal = new JTextArea(text);
        cmdline = new JTextField();
        this.setContentPane(panel);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(scrollpane);
        panel.add(cmdline, BorderLayout.SOUTH);
        panel.setPreferredSize(new java.awt.Dimension(600,500));
        scrollpane.setViewportView(terminal);
        terminal.setEnabled(true);
        terminal.setEditable(false);
        terminal.setBackground(Color.BLACK);
        terminal.setForeground(Color.WHITE);
        terminal.setFont(new Font("Courier New", Font.PLAIN, 12));
        cmdline.setFocusable(true);

        //cmdline.addKeyListener( kl = new KeyAdapter(){
        cmdline.addKeyListener( new KeyAdapter(){
            @Override
			public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                switch(c) {
                    case 0x0A: {
                        if(cmdline.getText().compareTo("")!=0) {
                            if(history.size()>=PosixTelnetClientGUI.max_history) history.remove(0);
                            history.add(cmdline.getText());
                            pos_history = history.size();
                        }
                        String toSend = cmdline.getText() + "\r\n";
                        try{
                            ptc.SendData(toSend);
                        }catch(Exception exc){
                            
                        }
                        //text += cmdline.getText() + "\r\n";
                        cmdline.setText("");
                        
                        printInfo();
                        break;
                    }
                    case 0x04: {
                        exitWindow();
                        break;
                    }
                    case 0x1B: {
                        cmdline.setText("");
                        break;
                    }
                    default: {
                        if(c>=65535) {
                            int i = e.getKeyCode();
                            switch(i) {
                                case 0x26: {
                                    if(history.size()>0) {
                                        if(pos_history == history.size() && cmdline.getText().compareTo("")!=0) {
                                            if(history.size()>=PosixTelnetClientGUI.max_history) history.remove(0);
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
                                default: //terminal.setText(text+"\n"+Integer.toHexString(i));
                            }
                        }
                        else {
                            pos_history = history.size();
                        }
                        break;
                    }
                }
            }
        });
        
        this.addWindowListener( new java.awt.event.WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent winEvt) {                
                printInfo();
            }
        });
        
        
    }
    
    public void start(){
        try{
            ptc.Telnet(this, Host, port);
        }catch(Exception e){
            
        }
    }
    
    public void recvData(String Data){
        busy = true;
        text += Data + "\n";
        terminal.setText(text);  
        busy = false;
        //try{
        //    ptc.SendData("");
        //}catch(Exception e){
        //}
    }

    public void exitWindow() {
        printInfo();
        this.dispose();
    }
    
    public void printInfo(){
        parent.printLayerInfo(false);
        //!!!!!: add more headers here
    }
            
    private String removeSpaces(String s) {
        while(s.startsWith(" ")) s = s.substring(1);
        while(s.endsWith(" ")) s = s.substring(0, s.length()-1);
        return s;
    }
}

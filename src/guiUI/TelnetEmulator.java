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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.CommunicationException;
import core.InvalidNetworkLayerDeviceException;
import core.LowLinkException;
import core.TransportLayerException;
import core.protocolsuite.tcp_ip.Telnet_client;

/**
 *
 * @author key
 */
public class TelnetEmulator extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6645304083337196382L;
	private final Telnet_client telnet;    
    private JPanel panel;
    private JScrollPane scrollpane;
    private JTextArea terminal;
    private String text;
    private String Host;
    private String cmdline;
    private int Port;
    private KeyListener kl;
    MainScreen parent;
    
    /** Creates a new instance of TelnetEmulator */
    public TelnetEmulator(MainScreen parent, Telnet_client telnet, String Host, int Port) {
        super("Telnet client");
        this.telnet = telnet;
        this.parent = parent;
        this.Host = Host;
        this.Port = Port;
        cmdline = "";
        
               
        text = "";
        text += "Opening " + Host + ":" + Port + "...\r\n";
        
        //terminal = new JLabel(text);
        terminal = new JTextArea(text);
        terminal.setEnabled(false);
        terminal.setEditable(false);
        terminal.setBackground(Color.BLACK);
        terminal.setForeground(Color.WHITE);
        terminal.setFont(new Font("Courier New", Font.PLAIN, 12));
        panel = new JPanel();
        scrollpane = new JScrollPane();
        
        this.addKeyListener( kl = new KeyAdapter(){
//        terminal.addKeyListener( kl = new KeyAdapter(){
            @Override
			public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                if(c<65535) {
                    String s;
                    switch(c){
                        case 0x4:{
                            cmdline = "quit";
                            s = "\r\n";
                            break;
                        }
                        case 0xD:
                        case 0xA:   {
                            s = "\r\n";
                            break;
                        }
                        default: s = String.valueOf(c).toString();
                    }
                    sendData(s);
                }
                else {
//                    String s = String.valueOf(e.getKeyCode()).toString();
//                    sendData(s);
//                    printInfo();
                }
            }
        });
         
        
        this.addWindowListener( new java.awt.event.WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent winEvt) {
                exitWindow();
            }
        });
        
        panel.setLayout(new java.awt.BorderLayout());
        
        panel.setPreferredSize(new java.awt.Dimension(600,500));
        
        this.setContentPane(panel);
        panel.add(scrollpane);  
        scrollpane.setViewportView(terminal);    
        

    }

    public void start(){
        try{
            this.telnet.TelnetConnect(this, Host, Port);
        }catch(CommunicationException e){
            
            ///
            ///FIXME!!!!!! CATCH EXCEPTION!!!!!
            ///
            
        }catch(LowLinkException e){
            
            ///
            ///FIXME!!!!!! CATCH EXCEPTION!!!!!
            ///
            
        }catch(InvalidNetworkLayerDeviceException e){
            
            ///
            ///FIXME!!!!!! CATCH EXCEPTION!!!!!
            ///
            
         }catch(TransportLayerException e){
            
            ///
            ///FIXME!!!!!! CATCH EXCEPTION!!!!!
            ///
            
        }        
    }
    
    public void recvData(String Data){
//        if(Data.compareTo("\r\nQUIT")==0) {
//            this.removeKeyListener(kl);
//        }
        if(Data.compareTo("\b")==0) text = text.substring(0, text.length()-1);
        else text += Data;
        terminal.setText(text + "_");
    }
    
    public void sendData(String Data){
        try{
            if(Data.length()>0){
                if(Data.compareTo("\r\n")==0) {
                    if(cmdline.compareToIgnoreCase("quit")==0){
                        this.removeKeyListener(kl);
                        telnet.Disconnect();
                        Data = "";
                    }
                    else{
                        cmdline = "";
                    }
                }
                else if(Data.compareTo("\b")==0){
                    cmdline = cmdline.substring(0, cmdline.length()-1);
                }
                else{
                    cmdline += Data;
                }
                telnet.SendData(Data);
            }
        }catch(LowLinkException e){
            printLayerInfo(e.toString());
        }catch(TransportLayerException e){
            printLayerInfo(e.toString());
        }catch(CommunicationException e){
            printLayerInfo(e.toString());
        }
    }
    
    public void close(){
        this.removeKeyListener(kl);
        recvData("\r\n Telnet client was closed");
    }
    
    public void exitWindow(){
//        this.dispose();
        try {
            this.removeKeyListener(kl);
            telnet.Disconnect();
        }catch(LowLinkException e){
            printLayerInfo(e.toString());
        }catch(TransportLayerException e){
            printLayerInfo(e.toString());
        }catch(CommunicationException e){
            printLayerInfo(e.toString());
        }
    }
    
    protected void printLayerInfo(String s) {
        telnet.printLayerInfo(s);
    }
}

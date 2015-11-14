/*
 * InfoBox.java
 *
 * Created on 13 �������� 2007 �., 19:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package guiUI;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author QweR
 */
public class InfoBox extends JDialog{

    //private static final long serialVersionUID = -5836895270535785031L;
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8155022723311994317L;
	JTextArea message = new JTextArea();
    JScrollPane scroll = new JScrollPane();
    JLabel w1 = new JLabel("  ");
    JLabel e1 = new JLabel("  ");
    JButton Ok = new JButton("Ok");
    
    final static int wx = 500;
    final static int hy = 400;
    /** Creates a new instance of MessageBox */
    public InfoBox() {
        super();
        setData("", 100, 50);
    }
    
    public InfoBox(java.awt.Frame owner, String title, String mes){
        super(owner, title, true);
        setLocation(owner.getLocation().x+(owner.getWidth()-InfoBox.wx)/2,owner.getLocation().y+(owner.getHeight()-InfoBox.hy)/2);
        setData(mes, InfoBox.wx, InfoBox.hy);
    }
    
    public InfoBox(java.awt.Frame owner, String title, String mes, int width, int height){
        super(owner, title, true);
        setLocation(owner.getLocation().x+(owner.getWidth()-width)/2,owner.getLocation().y+(owner.getHeight()-height)/2);
        setData(mes, width, height);
    }
    
    private void setData(String mes, int x, int y){        
        message.setText(mes);
        message.setEditable(true);
        message.setBackground(this.getBackground());
        scroll.setViewportView(message);
        getContentPane().setLayout(new java.awt.BorderLayout(5,5));
        getContentPane().add(scroll, java.awt.BorderLayout.CENTER);
        
        Ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkActionPerformed(evt);
            }
        });
        getContentPane().add(Ok, java.awt.BorderLayout.SOUTH);
        getContentPane().add(w1, java.awt.BorderLayout.WEST);
        getContentPane().add(e1, java.awt.BorderLayout.EAST);
        this.setSize(x, y);
        setVisible(true);
    }
    
    private void OkActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }
    
}

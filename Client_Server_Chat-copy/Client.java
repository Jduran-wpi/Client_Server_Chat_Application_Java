import java.net.*;
import java.io.*;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 *  Client side of client/server chat application
 * Jonathan Duran
 * @version(Final July/7/2021)
 */
public class Client extends JFrame
{
    // initialize socket and input output streams
    static Socket s;
    static DataInputStream  in;
    static DataOutputStream out;
  
    
    //Port and GUI
    static final int PORT = 4000;
    static TextArea textArea;
    static TextField textField;
   
    //
    static String msgout = "";
    static DatagramSocket socket = null;
    
    //
    static boolean uname = false;
    static String begin = ("\nWelcome!\n"+
                           "To group chat just type and send.\n"+
                           "To whisper type 'whisper#username#message..'.\n"+
                           "To logout type 'logout'.\n");
    
    
//-------------------------------------------------------------  
//GUI Constructor
//-------------------------------------------------   
    public Client(){

    initComponents();
     
    }
//-------------------------------------------------  
//Main 
//------------------------------------------------- 
    public static void main(String[] args) {
                    
        Client c = new Client();
          
                      
          
           try{
            s = new Socket("127.0.0.1", PORT);
            
            out = new DataOutputStream(s.getOutputStream());
            
            in = new DataInputStream(s.getInputStream());
            
            c.setVisible(true);
            
            
            
            username(out,in);
            
            addText(begin);
            
            String msgin = "";
            
            while(!msgin.equals("logout")){
                msgin = in.readUTF();
                addText(msgin);
            }
            
            if(msgin.equals("logout")){
                addText("Client is logging out");
                 // closing resources
                    in.close();
                    out.close();
                    s.close(); 
            }
            
           }catch(IOException ioe){
         textArea.append("Server Offline...");
         JOptionPane.showMessageDialog(null, "Server Offline...");
         System.exit(0);                     
        }
        catch(NullPointerException npe){
        }    
    }
 //-------------------------------------------------
 //GUI
//-------------------------------------------------
   public void initComponents(){
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 450, 300);
      
      Container pane = getContentPane();
      JLabel title = new JLabel("Client");
      textArea = new TextArea(5, 20);
      
      textField = new TextField();
      
      JButton send = new JButton("SEND");
      
      JPanel bottomOfChat = new JPanel();
      
      bottomOfChat.setLayout(new BorderLayout());
      
      
      pane.add(title,BorderLayout.PAGE_START);
      pane.add(textArea, BorderLayout.CENTER);
      bottomOfChat.add(textField, BorderLayout.CENTER);
      bottomOfChat.add(send, BorderLayout.EAST);
      
      pane.add(bottomOfChat, BorderLayout.PAGE_END);
      
       textArea.setEditable(false);
       
       send.addActionListener(e -> {
            sendActionPerformed();
        });
    }
//--------------------------------------------------
//Send button
//-------------------------------------------------
private void sendActionPerformed(){
    try{
     
        msgout = textField.getText();
        
        if(msgout.length() == 0){
            
        }
        else{
        out.writeUTF(msgout); // from Client to server
        
        if(uname == false){
            addText("Sent: " + msgout);
        }
        
        }   
        
    } catch(UnknownHostException uhe) {
         addText("Unable to connect to host."); 
    }
      catch(IOException ie) {   
        addText("Unable to send message not connected"); 
    }
    textField.setText("");
}
//--------------------------------------------------------
//Posting text to chatbox
//-------------------------------------------------
public static void addText(String s){
    textArea.append(s + "\n");
}
//--------------------------------------------------------
//Verify Username is not taken with server.
//--------------------------------------------------------
public static void username(DataOutputStream out, DataInputStream in){
    
    String input = "";
    Scanner scnr = new Scanner(System.in);
    String username = "";
    
    try{
        
    while(!input.equals("Not Taken")){
        addText("To enter the chat.\nEnter username:");
        
        input = in.readUTF();
        addText("Read: "+ input);
        
    }
    uname = true;
    }catch(IOException i){
        i.printStackTrace();
    }
    
    
}


}
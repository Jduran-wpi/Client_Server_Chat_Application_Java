import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

/**Server side of client/server chat application
 * Jonathan Duran
 * @version(Final July/7/2021)
 */
public class Server extends JFrame
{
//Instance variables
//-----------------------------------------------------------

    static Socket          clientSocket;
    static ServerSocket    server;
    static DataInputStream dis;
    static DataOutputStream dos;
    static TextArea textArea;
    String msgin = "";
    
    // Vector to store active clients
    static ArrayList<ClientHandler> ar = new ArrayList<>();
    
//-----------------------------------------------------------------
//Constructor GUI   
//-----------------------------------------------------------------    
    public Server(){
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 450, 300);
      
      Container pane = getContentPane();
      JLabel title = new JLabel("SERVER");
      textArea = new TextArea(5, 20);
      
      JPanel bottomOfChat = new JPanel();
      
      bottomOfChat.setLayout(new BorderLayout());
      
      
      pane.add(title,BorderLayout.PAGE_START);
      pane.add(textArea, BorderLayout.CENTER);
      pane.add(bottomOfChat, BorderLayout.PAGE_END);
    }
    
 //--------------------------------------------------------
//Posting text to chatbox
//-------------------------------------------------
public static void addText(String s){
    textArea.append(s + "\n");
}   
    
//---------------------------------------------------------------
//Verify Username on Server side with active usernames
//---------------------------------------------------------------
public static String verify(DataOutputStream out, DataInputStream in){
    
    boolean flag = true;
    String input = "";
    
    while(flag){
        try{
            
        input = in.readUTF();
        boolean taken = false;
        addText("Verify: " + input);
        
        if(ar.size() == 0){
            addText("Username not Taken");
            out.writeUTF("Not Taken");
            flag = false;
            return input;
        }
        else{
        
        for(ClientHandler mc : Server.ar){
            
            if(mc.getName().equals(input)){
                taken = true;
                addText("Username Taken");
                out.writeUTF("Taken");
                break;
            }  
        }
        
        if(taken == false){
            addText("Username not Taken");
            out.writeUTF("Not Taken");
            flag = false;
        }}
        
        
    }catch(IOException i){
        i.printStackTrace();
    }
    
    }
    
     return input;    
}     
 
//Main
//----------------------------------------------------------    
    public static void main(String[] args) throws IOException {
        Server gui = new Server();
        
        gui.setVisible(true);
        
        server = new ServerSocket(4000);
        addText("Server Started");
        
        while(true){ 
            
            //Accept client requests
            clientSocket = server.accept();
            addText("New client request received : ");
            
            // obtain input and output streams
              dis = new DataInputStream(clientSocket.getInputStream());
              dos = new DataOutputStream(clientSocket.getOutputStream()); 
              
           //Verify Client username   
           String cname = verify(dos, dis);  
              
           addText("Creating a new handler for this client...");   
           
           // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler( clientSocket,cname, dis, dos);
              
            
            // Create a new Thread with this object.
            Thread t = new Thread(mtch);
  
            // add this client to active clients list
            addText("Adding "+ cname +" to active client list");
            ar.add(mtch);
            
            // start the thread.
            t.start();
            
    }}
}

/**
*--------------------------------------------------------------------------
*                  ClientHandler below
*--------------------------------------------------------------------------
*/
// ClientHandler class
class ClientHandler implements Runnable 
{
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    boolean welcomeSent;
      
    // constructor
    public ClientHandler(Socket s, String name,
                            DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
        this.welcomeSent = false;
    }
  
    @Override
    public void run() {
  
        String received;
        while (this.isloggedin) 
        {
            try
            {
                if (!this.welcomeSent) {
                for (ClientHandler mc : Server.ar) 
                {
                  mc.dos.writeUTF(this.name + " has joined the server.");
                }
                this.welcomeSent = true;
                activeList();
              }
                
                
                // receive the string
                received = dis.readUTF();
                
                Server.addText(this.name+": "+ received);
                
                // break the string into message
                String [] tokens = received.split("#");
                  
                //Logout
                if(tokens[0].equals("logout")){
                    this.isloggedin=false;
                    for (ClientHandler mc : Server.ar) 
                { 
                    mc.dos.writeUTF(this.name+" : Has logged out");
                }
                    Server.addText(this.name + " Has requested to logout");
                    
                    
                    boolean removed = false;
                    
                //find user and remove from active list
                    for( int i = 0 ; i < Server.ar.size() ; i++){
                        
                        if(Server.ar.get(i).getName().equals(this.name)){
                           Server.addText(this.name + " removed");
                           Server.ar.remove(i); 
                           removed = true;
                        }
                    }
                    
                    if(removed == false){Server.addText("Client not removed");}
                    activeList();
                    this.dos.writeUTF("logout");
                    this.s.close();
                    break;
                }
                //Direct message
                else if(tokens[0].equals("whisper")){
                    String target = tokens [1];
                    boolean found = false;
                    for( ClientHandler mc : Server.ar){
                        
                        if(mc.name.equals(target)){
                           found =true;
                           String outMsg = (this.name + ":" + tokens[2]);
                           this.dos.writeUTF(outMsg);
                           mc.dos.writeUTF(outMsg);
                           break;
                        }
                    }
                    if(found == false){
                     this.dos.writeUTF("User not found");
                    }
                }
                //Broadcast
                else{
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.ar) 
                {
                        mc.dos.writeUTF(this.name+" : "+ received);
 
                }}
                
            } catch (IOException e) {
                  
                e.printStackTrace();
            }
              
        }
        try
        {
            
            // closing resources
             this.dis.close();
            this.dos.close();
            Server.addText("Resources Closed");
            stopRunning();
        }catch(IOException e){
            e.printStackTrace();
        }
       
    }
//--------------------------------------------------------------------------
//Stops thread
//--------------------------------------------------------------------------
public void stopRunning()
{
    Thread.currentThread().stop();
}

//--------------------------------------------------------------------------
//Print Active Client List
//-------------------------------------------------------------------------- 
public void activeList(){
    
    if(Server.ar.size() != 0){
    for (ClientHandler mc : Server.ar){ 
            try
            {
                
                mc.dos.writeUTF("ACTIVE CLIENT LIST:");
                for(int i = 0 ; i < Server.ar.size(); i++){
                mc.dos.writeUTF(Server.ar.get(i).name);
            }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            
        }}  
    }
//---------------------------------------------------------------------------
//Getter for client name.
//---------------------------------------------------------------------------
public String getName(){
  return this.name;
}    
}
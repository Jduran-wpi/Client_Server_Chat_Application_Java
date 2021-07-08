import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

public class ChatWindow extends JFrame implements ActionListener
{
      private static TextField textField;
      private static TextArea textArea; 
    
    
      public ChatWindow(){
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 450, 300);
      
      Container pane = getContentPane();
      JLabel title = new JLabel("ChatWindow");
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
       
       send.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e){
        
        textField.setText("");
    }
    
    public static void addText(String s){
        textArea.append(s + "\n");
    }
  //--------------------------------------
    public static void main(String[]args) {
         ChatWindow w = new ChatWindow();
         
         
          w.setVisible(true);
         
    
}}
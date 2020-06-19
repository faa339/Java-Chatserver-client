/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.client;

/**
 *
 * @author Owner
 */
import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class client {

    /**
     * @param args the command line arguments
     */
    static JTextArea View;
    static JTextField Type;
    static WriteScreen writer;
    static PrintStream sout;
    static Scanner sin;
    static JFrame Chatwindow;
    static Socket s;
    
    public static void main(String[] args) {
        Chatwindow = new JFrame();
        Chatwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Chatwindow.setSize(500,500);
        
        JPanel ViewPanel = new JPanel(new BorderLayout());
        JPanel SendPanel = new JPanel();
        JPanel MainPanel = new JPanel(new GridLayout(2,1));
        
        
        View = new JTextArea("Enter servername");
        View.setEditable(false);
        
        
        Type = new JTextField(20);
        
        JButton Send = new JButton();
        Send.setText("Send");
        Send.addActionListener(new ButtonListener());

        ViewPanel.add(View);
        SendPanel.add(Type);
        SendPanel.add(Send);
        
        MainPanel.add(ViewPanel);
        MainPanel.add(SendPanel);
        Chatwindow.add(MainPanel);
        Chatwindow.setVisible(true);
    }
    static public void connect(String host){
        try{
            s = new Socket(host, 5190);
            sin = new Scanner(s.getInputStream());
            sout = new PrintStream(s.getOutputStream());
            writer = new WriteScreen(sin);
            writer.start();
        }catch (IOException e){
            System.out.println("error " + e.toString());
            View.append("\nerror: " + e.toString());
        }
    }
}

class ButtonListener implements ActionListener{
    Boolean connected = false;
    public void actionPerformed(ActionEvent e){
        String text = client.Type.getText();
        if (text.length() != 0){
            if(!connected){
                client.connect(text);
                client.View.append("\nEnter username");
                connected = true;
                client.Type.setText("");
            }
            
            if(text.equals("exit")){
                try{
                    client.s.close();
                    WriteScreen.printmsg("Disconnected");
                    client.Type.setText("");
                    client.Type.setEditable(false);
                }catch(IOException n){
                    System.out.println("error "+n.toString());
                }
            }else {
                client.sout.println(text);
                client.Type.setText("");
            }
        }
    }
}

class WriteScreen extends Thread{
Scanner sin;

    WriteScreen(Scanner serverin){
        sin = serverin;
    }

    public void run(){
            while(true){
            if(sin.hasNext()){
                String ins = sin.nextLine();
                printmsg(ins);
            }
        }
    }
    
    static synchronized void printmsg(String msg){
        client.View.append("\n"+msg);
    }
}

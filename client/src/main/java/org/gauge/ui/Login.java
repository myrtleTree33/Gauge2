package org.gauge.ui;

import org.gauge.App;
import org.gauge.Client;
import org.gauge.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Created by AdminNUS on 6/4/2015.
 */
public class Login {
    private JTextField UserName;
    private JPanel panel1;
    private JPasswordField Password;
    private JTextField ServerName;
    private JButton loginButton;
    private JTextPane textPane1;
    char[] pwd;
    String user;
    String servername;
    User user1;
    JFrame frame;

    public Login() {
        frame = new JFrame("Login");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        user1 = new User();
        user1.setPort(9060);
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine(); //you get the IP as a String
            user1.setIp(ip);
        }catch (IOException e){
        }
        System.out.println("Local IP:" + user1.getIp());

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initiateLogin();
            }
        });
        Password.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initiateLogin();
            }
        });
    }

    private void initiateLogin(){
        servername = ServerName.getText();
        user = UserName.getText();
        pwd = Password.getPassword();

        user1.setUsername(user);
        user1.setPassword(new String(pwd));

        if(App.client != null)
            App.client.stop();

        App.client = new Client(servername, 9000, 9060);
        textPane1.setText("Connecting to server...");
        App.client.start();

        try{
            App.client.login(user1);
        }catch(Exception e1){

        }
        try {
            Thread.sleep(1000);
        } catch (Exception e1) {

        }
        //actionLogin = false;
        if(App.client.isLoggedIn()) {
            frame.setVisible(false);
            MainView mv = new MainView(user1);
        }
        else
            textPane1.setText("Either you entered wrong credentials or we are currently suffering from network congestion");
    }
}

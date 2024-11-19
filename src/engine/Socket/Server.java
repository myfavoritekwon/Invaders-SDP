package engine.Socket;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class Server {
    public String hostIp;
    public int port = 9000;
    private int Button;
    private int moving;

    public void setHostIp() {
        try {
            hostIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Host IP Address: " + hostIp);
        } catch (UnknownHostException e) {
            System.out.println("fail :: " + e.getMessage());
        }
    }

    public void connectSocket(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.accept();
            Socket serverUser = null;
            OutputStream out;
            InputStream in;
            while(true){//while 조건문에 조건 넣기
                serverUser = serverSocket.accept();
                System.out.println("Open Server from : " + serverUser.toString());
                in = serverUser.getInputStream();
                moving = in.read();
                out = serverUser.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);
                writer.println(Button);
            }
            //port++;
        }catch (IOException e){
            System.out.println("fail :: " + e.getMessage());
        }
    }

    public void getButton(int button){
        this.Button = button;
    }
}

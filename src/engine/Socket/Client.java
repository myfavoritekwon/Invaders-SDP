package engine.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {
    private Logger logger;
    private String hostIp;
    private int Button;
    private int port;
    public int moving;

    public void connectServer() {
        try {
            Socket socket = new Socket(hostIp, port);
            logger.info("Client connected to " + hostIp + ":" + port);
            OutputStream out;
            InputStream in;
            while (true) {
                in = socket.getInputStream();
                moving = in.read();
                out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);
                writer.println(Button);
            }
        } catch (IOException e) {
            logger.info("fail :: " + e.getMessage());
        }
    }



    public void getButton(int button){
        this.Button = button;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public void setPort(int port) {

    }
}

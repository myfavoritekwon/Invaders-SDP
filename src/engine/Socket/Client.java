package engine.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class Client {
    private String hostIp; // 서버의 실제 IP 주소
    private int Button;
    private int port;
    public int moving;

    public void connectServer() {
        this.hostIp = "172.17.100.13"; // 서버 컴퓨터의 실제 IP 주소 입력
        this.port = 9000; // 서버 포트
        System.out.println("Connecting to server at " + hostIp + ":" + port);
        try {
            // 서버에 연결
            Socket socket = new Socket(hostIp, port);
            System.out.println("Client connected to " + hostIp + ":" + port);

            OutputStream out;
            InputStream in;

            while (true) {
                // 서버로부터 데이터 수신
                in = socket.getInputStream();
                moving = in.read();
                System.out.println("Received moving data: " + moving);

                // 서버로 데이터 전송
                out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);
                writer.println(Button);
                System.out.println("Sent button data: " + Button);
            }
        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public void getButton(int button) {
        this.Button = button;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

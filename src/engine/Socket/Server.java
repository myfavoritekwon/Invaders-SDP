package engine.Socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private String hostIp = "172.17.100.13"; // 기본값 설정
    public int port = 9000;
    private int Button = 0; // 초기화
    private int moving = 0;

    public void connectSocket() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port: " + port + " Ip" + hostIp);
            while (true) {
                // 클라이언트 연결 수락
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                serverSocket.close();
                return;
                // 클라이언트 처리 스레드 실행
                //new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server failed: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream();
             PrintWriter writer = new PrintWriter(out, true)) {

            // 클라이언트로부터 데이터 읽기
            moving = in.read();
            System.out.println("Received moving data: " + moving);

            // 클라이언트로 데이터 보내기
            writer.println(Button); // Button 값을 클라이언트로 전송
            System.out.println("Sent Button data: " + Button);

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Failed to close client socket: " + e.getMessage());
            }
        }
    }

    public void getButton(int button) {
        this.Button = button;
    }

    public String getHostIp() {
        return hostIp;
    }
}

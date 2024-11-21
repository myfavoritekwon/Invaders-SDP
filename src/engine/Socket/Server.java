package engine.Socket;

import engine.Core;
import engine.InputManager;
import engine.ServerManager;
import entity.Room;
import screen.MultiRoomScreen;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static Client client = new Client();
    protected int returnCode;
    private static String hostIp; // 기본값 설정
    private static final int INFO_PORT = 9000;
    private static int MAIN_PORT = 9001;
    private int Button; // 초기화
    private int moving = 0;
    private List<Room> rooms = new ArrayList<>();

    // 서버 정보 전송용 서버
    public static void startInfoServer() {
        new Thread(() -> {
            try (ServerSocket infoServerSocket = new ServerSocket(INFO_PORT)) {
                System.out.println("Info server running on port " + INFO_PORT);

                while (true) {
                    Socket socket = infoServerSocket.accept();
                    System.out.println("Client connected to info server: " + socket.getInetAddress());
                    // 서버 IP와 포트 정보 전송
                    sendServerInfo(socket);
                }
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("go to client");
                client.connectServer(hostIp);
            }
        }).start();
    }

    // 클라이언트와의 통신용 서버
    public static void startMainServer() {
        new Thread(() -> {
            try (ServerSocket mainServerSocket = new ServerSocket(MAIN_PORT)) {
                System.out.println("Main server running on port " + MAIN_PORT);

                while (true) {
                    mainServerSocket.setSoTimeout(30000);
                    Socket clientSocket = mainServerSocket.accept();
                    System.out.println("Client connected to main server: " + clientSocket.getInetAddress());

                    // 클라이언트와 통신 처리
                    handleClient(clientSocket);
                }
            } catch (IOException e) {
                //e.printStackTrace();
                MultiRoomScreen.getErrorCheck(1);
            }
        }).start();
    }

    // 서버 IP와 포트 정보를 클라이언트로 전송
    private static void sendServerInfo(Socket clientSocket) {
        try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String serverIp = hostIp;
            writer.println(serverIp);
            writer.println(MAIN_PORT); // 통신용 서버 포트
            System.out.println("Sent server info: IP=" + serverIp + ", Port=" + MAIN_PORT);
        } catch (IOException e) {
            System.out.println("Error sending server info: " + e.getMessage());
            MultiRoomScreen.getErrorCheck(1);
        }
    }

    //클라이언트 처리
    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        ) {
            writer.println("Welcome to the main server!");

            // 수신 스레드
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("Client: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost: " + clientSocket.getInetAddress());
                }
            });

            // 송신 스레드
            Thread sendThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = consoleReader.readLine()) != null) {
                        writer.println(message);
                        System.out.println("You: " + message);
                    }
                } catch (IOException e) {
                    System.out.println("Error sending message");
                }
            });

            // 스레드 시작
            receiveThread.start();
            sendThread.start();

            // 두 스레드가 모두 종료될 때까지 대기
            receiveThread.join();
            sendThread.join();
        } catch (IOException | InterruptedException e) {
            System.out.println("Connection lost: " + clientSocket.getInetAddress());
            MultiRoomScreen.getErrorCheck(1);
        }
    }

    public void setIp(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        hostIp = inetAddress.getHostAddress();
                        System.out.println("내부 IP 주소: " + inetAddress.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getButton(int button) {
        this.Button = button;
    }

    public String getHostIp() {
        return hostIp;
    }

    public int getPort() {
        return MAIN_PORT;
    }

}

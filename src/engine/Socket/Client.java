package engine.Socket;

import engine.Core;
import engine.ServerManager;
import screen.GameScreen;
import screen.MultiRoomScreen;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

public class Client {
    private static ServerManager serverManager;
    private String hostIp; // 서버의 실제 IP 주소
    private static String Button;
    private int port;
    private static int sleepTime = 50;
    private static String takeButton;
    private static List<String> giveShooter;

    public Client(ServerManager serverManager){
        Client.serverManager = serverManager;
        serverManager.setClient(this);
    }

    public void connectServer(String hostIp) {
        this.hostIp = hostIp;// 서버 컴퓨터의 실제 IP 주소 입력
        this.port = 9000;
        System.out.println("Connecting to server at " + hostIp + ":" + port);
        String serverIp = "172.17.72.170"; // 서버 정보 전송용 서버 IP
        int infoPort = 9000; // 서버 정보 전송용 서버 포트
        GameScreen.setSORC(false);
        try (
                Socket infoSocket = new Socket(serverIp, infoPort);
                BufferedReader reader = new BufferedReader(new InputStreamReader(infoSocket.getInputStream()))
        ) {
            // 서버 정보 수신
            String receivedIp = reader.readLine();
            System.out.println(receivedIp);
            int receivedPort = Integer.parseInt(reader.readLine());
            System.out.println("Received server info: IP=" + receivedIp + ", Port=" + receivedPort);
            // 통신용 서버에 연결
            connectToMainServer(receivedIp, receivedPort);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectToMainServer(String serverIp, int serverPort) {
        try (
                Socket mainSocket = new Socket(serverIp, serverPort);
                BufferedReader reader = new BufferedReader(new InputStreamReader(mainSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(mainSocket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(mainSocket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(mainSocket.getInputStream());
        ) {
            System.out.println("Connected to server at " + serverIp + ":" + serverPort);
            writer.println(10);
            // 수신 스레드
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {//(message = reader.readLine()) != null
                        Object object = objectInputStream.readObject();// 타입 체크
                        DataPacket dataPacket = (DataPacket) object;
                        serverManager.setClientButton(dataPacket.getCommand());
                        serverManager.setGiveShooter(dataPacket.getData());
                        System.out.println("Server: " + dataPacket.getCommand());
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Connection lost from server.");} catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
            });

            // 송신 스레드
            Thread sendThread = new Thread(() -> {
                String message;
                while (true) {//(message = String.valueOf(Button)) != null
                    writer.println(Button);
                    System.out.println("You: " + Button);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // 스레드 시작
            receiveThread.start();
            sendThread.start();

            // 두 스레드가 모두 종료될 때까지 대기
            receiveThread.join();
            sendThread.join();
            Thread.sleep(10000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setButton(String button) {
        this.Button = button;
    }
}

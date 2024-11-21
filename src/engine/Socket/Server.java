package engine.Socket;

import engine.Core;
import engine.InputManager;
import entity.Room;
import screen.Screen;

import java.awt.event.KeyEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Server {
    private boolean state = true;
    private String hostIp; // 기본값 설정
    private int port = 9000;
    private int Button = 0; // 초기화
    private int moving = 0;
    private List<Room> rooms = new ArrayList<Room>();
    protected InputManager inputManager;
    private KeyEvent e;


    // 서버 역할: 클라이언트 연결 요청 수락
    public void startServer() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server running at " + hostIp + ":" + port);
                Socket socket;
                while (true) {
                    serverSocket.setSoTimeout(3000);
                    socket = serverSocket.accept();
                    System.out.println("Client connected: " + socket.getInetAddress());

                    // Peer 리스트 전송
                    sendPeerList(socket);

                    // 키 정보 송수신 시작
                    startKeyCommunication(socket);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    // 클라이언트 역할: 서버에서 Peer 리스트를 받고 선택 후 연결
    public void connectToServer(String serverIp, int serverPort) {
        try (Socket socket = new Socket(serverIp, serverPort)) {
            System.out.println("Connected to server: " + serverIp + ":" + serverPort);

            // Peer 리스트 수신
            List<Room> receivedList = receivePeerList(socket);

            // Peer 선택 및 연결
            Room selectedPeer = selectPeer(receivedList, 0);//선택한 리스트 번호 입력
            if (selectedPeer != null) {
                connectToPeer(selectedPeer.getIp(), selectedPeer.getPort());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Peer 리스트 전송
    private void sendPeerList(Socket socket) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            outputStream.writeObject(rooms);
            System.out.println("Sent Peer List: " + rooms);
        }
    }

    // Peer 리스트 수신
    private List<Room> receivePeerList(Socket socket) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            @SuppressWarnings("unchecked")
            List<Room> receivedList = (List<Room>) inputStream.readObject();
            System.out.println("Received Peer List: " + receivedList);
            return receivedList;
        }
    }

    // Peer 선택
    private Room selectPeer(List<Room> peerList, int choice) {

        if (choice < 1 || choice > peerList.size()) {
            System.out.println("Invalid selection. Exiting...");
            return null;
        }

        Room selectedPeer = peerList.get(choice - 1);
        System.out.println("Selected Peer: " + selectedPeer);
        return selectedPeer;
    }

    // Peer로 직접 연결 및 키 정보 송수신
    public void connectToPeer(String peerIp, int peerPort) {
        try (Socket socket = new Socket(peerIp, peerPort)) {
            System.out.println("Connected to peer: " + peerIp + ":" + peerPort);

            // 키 정보 송수신 시작
            startKeyCommunication(socket);

        } catch (IOException e) {
            System.out.println("Failed to connect to peer: " + peerIp + ":" + peerPort);
        }
    }

    // 키 정보 송수신
    private void startKeyCommunication(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                //키 입력값 넣기
        ) {
            // 수신 스레드
            Thread receiveThread = new Thread(() -> {
                try {
                    String receivedKey;
                    while ((receivedKey = reader.readLine()) != null) {
                        System.out.println("Received key: " + receivedKey);
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost while receiving.");
                }
            });

            // 송신 스레드
            Thread sendThread = new Thread(() -> {
                System.out.println("Type keys to send (type 'exit' to quit):");
                while (true) {
                    //키 입력값 받기
                    int keyInput = e.getKeyCode();
                    if (27 == keyInput) {
                        System.out.println("Exiting communication.");
                        break;
                    }
                    writer.println(keyInput);
                    System.out.println("Sent key: " + keyInput);
                }
            });

            // 스레드 실행
            receiveThread.start();
            sendThread.start();

            // 송수신 종료 시까지 대기
            receiveThread.join();
            sendThread.join();

        } catch (Exception e) {
            System.out.println("Error during key communication: " + e.getMessage());
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
        return port;
    }
}

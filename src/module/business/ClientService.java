package module.business;

import module.abstraction.IClientService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientService implements IClientService {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    @Override
    public void connect(String serverIp, int serverPort) {
        try {
            this.socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(serverIp);
            this.serverPort = serverPort;
            System.out.println("Connected to server " + serverIp + ":" + serverPort);
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    @Override
    public String receiveMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength()).trim();
        } catch (Exception e) {
            System.err.println("Failed to receive message: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void disconnect() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Disconnected from server");
        }
    }
}


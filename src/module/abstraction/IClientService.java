package module.abstraction;

public interface IClientService {
    void connect(String serverIp, int serverPort);
    void sendMessage(String message);
    String receiveMessage();
    void disconnect();
}

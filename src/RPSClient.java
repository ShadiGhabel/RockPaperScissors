import core.DIContainer;
import core.DependencyInstaller;
import module.abstraction.IClientService;

import java.util.Scanner;

public class RPSClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9001;

    private static IClientService clientService;
    private static Scanner scanner;
    private static boolean running = true;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        try {
            DependencyInstaller.install();

            clientService = DIContainer.resolve(IClientService.class);
            clientService.connect(SERVER_IP, SERVER_PORT);

            System.out.println("Welcome to Rock Paper Scissors!");
            clientService.sendMessage("JOIN");

            Thread receiveThread = new Thread(() -> receiveMessages());
            receiveThread.start();

            receiveThread.join();

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private static void receiveMessages() {
        while (running) {
            try {
                String message = clientService.receiveMessage();

                if (message == null) {
                    continue;
                }

                String[] parts = message.split(":", 2);
                String messageType = parts[0];
                String content = parts.length > 1 ? parts[1] : "";

                switch (messageType) {
                    case "WAITING":
                        System.out.println(content);
                        break;

                    case "GAME_START":
                        System.out.println("\n" + "=".repeat(40));
                        System.out.println(content);
                        System.out.println("=".repeat(40));
                        break;

                    case "REQUEST_MOVE":
                        System.out.println("\n" + content);
                        System.out.print("Enter your choice (1=Rock, 2=Paper, 3=Scissors): ");
                        String move = scanner.nextLine().trim();
                        clientService.sendMessage("MOVE:" + move);
                        break;

                    case "INVALID_MOVE":
                        System.out.println(content);
                        System.out.print("Try again: ");
                        String retryMove = scanner.nextLine().trim();
                        clientService.sendMessage("MOVE:" + retryMove);
                        break;

                    case "ROUND_RESULT":
                        System.out.println("\n" + "-".repeat(40));
                        System.out.println(content);
                        System.out.println("-".repeat(40));
                        break;

                    case "GAME_OVER":
                        System.out.println("\n" + "=".repeat(40));
                        System.out.println(content);
                        System.out.println("=".repeat(40));
                        running = false;
                        break;

                    default:
                        System.out.println(message);
                }

            } catch (Exception e) {
                if (running) {
                    System.err.println("Error receiving message: " + e.getMessage());
                }
            }
        }
    }

    private static void cleanup() {
        if (clientService != null) {
            clientService.disconnect();
        }
        if (scanner != null) {
            scanner.close();
        }
        System.out.println("Goodbye!");
    }
}


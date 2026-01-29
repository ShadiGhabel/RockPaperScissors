package module.business;

import core.DIContainer;
import module.abstraction.IGameService;
import module.abstraction.IServerService;
import model.GameResult;
import model.Move;
import model.Player;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerService implements IServerService {
    private static final int SERVER_PORT = 9001;
    private DatagramSocket socket;
    private boolean running;
    private Map<String, Player> players;
    private List<Player> waitingPlayers;
    private IGameService gameService;

    public ServerService() {
        this.players = new ConcurrentHashMap<>();
        this.waitingPlayers = Collections.synchronizedList(new ArrayList<>());
        this.gameService = DIContainer.resolve(IGameService.class);
    }

    @Override
    public void start() {
        try {
            socket = new DatagramSocket(SERVER_PORT);
            running = true;

            System.out.println("Server started on port " + SERVER_PORT);
            System.out.println("Waiting for players...");

            handleClients();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClients() {
        byte[] buffer = new byte[1024];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                handleClientMessage(message, clientAddress, clientPort);

            } catch (Exception e) {
                if (running) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        }
    }

    private void handleClientMessage(String message, InetAddress address, int port) {
        String playerId = address.getHostAddress() + ":" + port;

        if (message.startsWith("JOIN")) {
            handleJoinRequest(address, port, playerId);
        } else if (message.startsWith("MOVE:")) {
            handleMoveRequest(message, playerId);
        }
    }

    private void handleJoinRequest(InetAddress address, int port, String playerId) {
        Player player = new Player(playerId, address, port);
        players.put(playerId, player);

        System.out.println("Player connected: " + playerId);

        waitingPlayers.add(player);

        if (waitingPlayers.size() >= 2) {
            Player player1 = waitingPlayers.remove(0);
            Player player2 = waitingPlayers.remove(0);

            startGame(player1, player2);
        } else {
            sendToClient("WAITING:Waiting for another player...", address, port);
        }
    }

    private void startGame(Player player1, Player player2) {
        System.out.println("Game started between " + player1.getId() + " and " + player2.getId());

        sendToPlayer(player1, "GAME_START:Game started!");
        sendToPlayer(player2, "GAME_START:Game started!");

        new Thread(() -> manageGame(player1, player2)).start();
    }

    private void manageGame(Player player1, Player player2) {
        while (!gameService.isGameOver(player1, player2)) {
            try {
                sendToPlayer(player1, "REQUEST_MOVE:let's play");
                sendToPlayer(player2, "REQUEST_MOVE:let's play");

                waitForMoves(player1, player2);

                GameResult result = gameService.playRound(player1, player2);

                String resultMessage = String.format(
                        "ROUND_RESULT:%s | Score: %d-%d",
                        result.getMessage(),
                        player1.getWins(),
                        player2.getWins()
                );

                sendToPlayer(player1, resultMessage);
                sendToPlayer(player2, resultMessage);

                System.out.println("Round played: " + result.getMessage());

                Thread.sleep(1000);

            } catch (Exception e) {
                System.err.println("Error in game management: " + e.getMessage());
                break;
            }
        }

        Player winner = gameService.getWinner(player1, player2);
        sendToPlayer(player1, "GAME_OVER:Game Over! Winner: " +
                (winner.equals(player1) ? "You" : "Opponent"));
        sendToPlayer(player2, "GAME_OVER:Game Over! Winner: " +
                (winner.equals(player2) ? "You" : "Opponent"));

        System.out.println("Game finished. Winner: " + winner.getId());
    }

    private void waitForMoves(Player player1, Player player2) throws InterruptedException {
        player1.setCurrentMove(null);
        player2.setCurrentMove(null);

        int timeout = 300;
        int elapsed = 0;

        while ((player1.getCurrentMove() == null || player2.getCurrentMove() == null) && elapsed < timeout) {
            Thread.sleep(100);
            elapsed++;
        }
    }

    private void handleMoveRequest(String message, String playerId) {
        try {
            String[] parts = message.split(":");
            int moveValue = Integer.parseInt(parts[1]);

            Player player = players.get(playerId);
            if (player != null) {
                Move move = Move.fromInt(moveValue);

                if (gameService.isValidMove(move)) {
                    player.setCurrentMove(move);
                    System.out.println("Player " + playerId + " chose: " + move.getName());
                } else {
                    sendToPlayer(player, "INVALID_MOVE:Invalid input! Please enter 1, 2, or 3.");
                }
            }
        } catch (NumberFormatException e) {
            Player player = players.get(playerId);
            if (player != null) {
                sendToPlayer(player, "INVALID_MOVE:Invalid input! Please enter a number.");
            }
        }
    }

    private void sendToPlayer(Player player, String message) {
        sendToClient(message, player.getAddress(), player.getPort());
    }

    private void sendToClient(String message, InetAddress address, int port) {
        try {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
        } catch (Exception e) {
            System.err.println("Failed to send message to client: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Server stopped");
    }
}


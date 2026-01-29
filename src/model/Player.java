package model;

import java.net.InetAddress;

public class Player {
    private String id;
    private InetAddress address;
    private int port;
    private int wins;
    private Move currentMove;

    public Player(String id, InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.wins = 0;
    }

    public String getId() {
        return id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        this.wins++;
    }

    public Move getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Move move) {
        this.currentMove = move;
    }

    public void resetWins() {
        this.wins = 0;
    }
}


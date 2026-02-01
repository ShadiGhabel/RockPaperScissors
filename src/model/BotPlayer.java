package model;

import java.net.InetAddress;
import java.util.Random;

public class BotPlayer extends Player {
    private Random random;

    public BotPlayer() {
        super("BOT", getBotAddress(), 0);
        this.random = new Random();
    }

    private static InetAddress getBotAddress() {
        try {
            return InetAddress.getByName("0.0.0.0");
        } catch (Exception e) {
            return null;
        }
    }

    public Move makeRandomMove() {
        int choice = random.nextInt(3) + 1; // Random number between 1-3
        Move move = Move.fromInt(choice);
        this.setCurrentMove(move);
        return move;
    }

    @Override
    public String getId() {
        return "BOT";
    }
}
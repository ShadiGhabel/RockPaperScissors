package model;

public class GameResult {
    private Player winner;
    private Player loser;
    private boolean isDraw;
    private String message;

    public GameResult(Player winner, Player loser, boolean isDraw, String message) {
        this.winner = winner;
        this.loser = loser;
        this.isDraw = isDraw;
        this.message = message;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public String getMessage() {
        return message;
    }
}


package module.business;

import module.abstraction.IGameService;
import model.GameResult;
import model.Move;
import model.Player;

public class GameService implements IGameService {
    private static final int WINS_REQUIRED = 3;

    @Override
    public GameResult playRound(Player player1, Player player2) {
        Move move1 = player1.getCurrentMove();
        Move move2 = player2.getCurrentMove();

        if (move1 == move2) {
            return new GameResult(null, null, true,
                    String.format("Draw! Both chose %s", move1.getName()));
        }

        Player winner, loser;
        if (move1.beats(move2)) {
            winner = player1;
            loser = player2;
            player1.incrementWins();
        } else {
            winner = player2;
            loser = player1;
            player2.incrementWins();
        }

        String message = String.format(
                "%s beats %s",
                winner.getCurrentMove().getName(),
                loser.getCurrentMove().getName()
        );

        return new GameResult(winner, loser, false, message);
    }

    @Override
    public boolean isGameOver(Player player1, Player player2) {
        return player1.getWins() >= WINS_REQUIRED || player2.getWins() >= WINS_REQUIRED;
    }

    @Override
    public Player getWinner(Player player1, Player player2) {
        if (player1.getWins() >= WINS_REQUIRED) {
            return player1;
        } else if (player2.getWins() >= WINS_REQUIRED) {
            return player2;
        }
        return null;
    }

    @Override
    public boolean isValidMove(Move move) {
        return move != Move.INVALID;
    }
}
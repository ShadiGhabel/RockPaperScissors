package module.abstraction;

import model.GameResult;
import model.Move;
import model.Player;

public interface IGameService {
    GameResult playRound(Player player1, Player player2);
    boolean isGameOver(Player player1, Player player2);
    Player getWinner(Player player1, Player player2);
    boolean isValidMove(Move move);
}



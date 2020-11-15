package game.board;

import game.players.Player;

public class PrepareGameBoard {
    private final Player whitePlayer;
    private final Player blackPlayer;

    public PrepareGameBoard(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }
}

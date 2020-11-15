package game.mainGame;

import game.board.PrepareGameBoard;

public class CreateGame {
    public Game createGame(PrepareGameBoard prepareGameBoard) {
        return new Game(prepareGameBoard.getWhitePlayer(), prepareGameBoard.getBlackPlayer());
    }

    public Game emptyGame() {
        return new Game(null, null);
    }
}

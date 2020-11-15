package game;


import game.board.BoardInterface;
import game.board.BoardView;
import game.board.PrepareGameBoard;
import game.mainGame.CreateGame;
import game.mainGame.GameController;
import game.moves.MoveService;
import game.players.CreateOpponent;
import game.players.Human;

import game.tools.PropertyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private final MoveService moveService = new MoveService();

    private final PropertyReader propertyReader = new PropertyReader();
    private final CreateGame game = new CreateGame();
    private final CreateOpponent opponent = new CreateOpponent(moveService);

    public Main(){
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Main());
    }


    @Override
    public void run() {
        PrepareGameBoard gameSetup = new PrepareGameBoard(new Human("Player"), opponent.getBot());

        BoardInterface boardView = new BoardView("Chess game project", propertyReader);
        GameController gameController = new GameController(boardView, game, opponent, moveService);
        gameController.newGame(gameSetup, true, r -> {});
    }
}

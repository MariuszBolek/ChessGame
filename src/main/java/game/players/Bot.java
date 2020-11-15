package game.players;

import game.mainGame.Game;
import game.moves.Move;
import game.moves.MoveService;

import java.util.List;
import java.util.Random;

public  class Bot extends Player{
    protected final MoveService moveService;
    private final static Random RANDOM = new Random();



    public Bot (MoveService moveService) {
        super("Worthy Opponent");
        this.moveService = moveService;

    }

    public  Move selectMove(Game game) {
        List<Move> moves = moveService.computeAllMoves(game.getBoard(), game.getToPlay(), game.getHistory(), true);
        return moves.get(RANDOM.nextInt(moves.size()));
    }

    public boolean isDrawAcceptable(Game game) {
       return RANDOM.nextBoolean();
    }
}


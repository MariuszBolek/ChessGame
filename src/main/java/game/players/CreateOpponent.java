package game.players;

import game.moves.MoveService;

public class CreateOpponent {

    private final MoveService moveService;


    public CreateOpponent(MoveService moveService) {
        this.moveService = moveService;

    }

    public Bot getBot() {
        return new Bot(moveService);
    }




}

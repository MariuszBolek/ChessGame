package game.players;

import game.moves.MoveService;

public class CreateOpponent {
    private static final int LEVEL_MAX = 2;
    private static final int LEVEL_MIN = 0;
    private static final int TIMEOUT_MAX = 10;

    private final MoveService moveService;


    public CreateOpponent(MoveService moveService) {
        this.moveService = moveService;

    }

    public Bot getBot() {
        return new Bot(moveService);
    }

//    public Bot getBot(int level, Integer timeout) {
//        checkLevel(level);
//        checkTimeout(timeout);
//        return new Bot(level,timeout, moveService);
//    }


    private void checkLevel(int level) {
        assert level >= LEVEL_MIN;
        assert level <= LEVEL_MAX;
    }

    private void checkTimeout(Integer timeout) {
        assert timeout == null || timeout <= TIMEOUT_MAX;
    }
}

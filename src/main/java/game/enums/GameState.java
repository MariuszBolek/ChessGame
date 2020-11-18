package game.enums;

public enum GameState {
    IN_PROGRESS, LOSS, DRAW_STALEMATE, DRAW_THREEFOLD, DRAW_50_MOVES, DRAW_AGREEMENT;

    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }
}

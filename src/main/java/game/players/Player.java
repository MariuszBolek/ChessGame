package game.players;

public abstract class Player {

    public Player(String name) {
    }

    public boolean isBot() {
        return this instanceof Bot;
    }
}


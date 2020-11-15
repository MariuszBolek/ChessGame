package game.enums;

import java.awt.*;

public enum BoardSquares {
    DARK(new Color(128, 64, 64)),
    LIGHT(new Color(255, 184, 135));

    private final Color color;

    BoardSquares(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}

package game.board;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {
    Label(String label) {
        super(label);
        setFont(new Font("Verdana", Font.PLAIN, 18));
        setOpaque(true);
        setHorizontalAlignment(CENTER);
    }
}

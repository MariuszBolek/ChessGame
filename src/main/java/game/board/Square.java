package game.board;

import game.enums.BoardSquares;
import game.pieces.Piece;

import javax.swing.*;
import java.awt.*;

public class Square extends JLabel {
    private Piece piece;
    private final Position position;

    Square(Piece piece, Position position, BoardSquares squares, Font font) {
        super(getPieceText(piece));
        this.piece = piece;
        this.position = position;
        setFont(font);
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setBackground(squares.getColor());
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        this.setText(getPieceText(piece));
    }

    public Position getPosition() {
        return position;
    }

    private static String getPieceText(Piece piece) {
        if (piece != null) {
            return piece.getPieceUnicode();
        }
        return "";
    }
}

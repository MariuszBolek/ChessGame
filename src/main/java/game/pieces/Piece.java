package game.pieces;

import game.enums.PieceColor;

public abstract class Piece {
    final protected PieceColor color;
    final protected int value;

    public Piece(PieceColor color, int value) {
        this.color = color;
        this.value = value;
    }

    //Each chess piece is represented as unicode, so we can search for it instead of loading images to represent pieces
    abstract public String getPieceUnicode();

    public PieceColor getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
    public String toString() {
        return this.color + " " + this.getClass().getSimpleName();
    }

    public abstract Piece copy();

    public static Piece fromUnicodeChar(char c) {
        Piece piece;
        switch (c) {
            case '♜':
                piece = new Rook(PieceColor.BLACK);
                break;
            case '♞':
                piece = new Knight(PieceColor.BLACK);
                break;
            case '♝':
                piece = new Bishop(PieceColor.BLACK);
                break;
            case '♛':
                piece = new Queen(PieceColor.BLACK);
                break;
            case '♚':
                piece = new King(PieceColor.BLACK);
                break;
            case '♟':
                piece = new Pawn(PieceColor.BLACK);
                break;
            case '♖':
                piece = new Rook(PieceColor.WHITE);
                break;
            case '♘':
                piece = new Knight(PieceColor.WHITE);
                break;
            case '♗':
                piece = new Bishop(PieceColor.WHITE);
                break;
            case '♕':
                piece = new Queen(PieceColor.WHITE);
                break;
            case '♔':
                piece = new King(PieceColor.WHITE);
                break;
            case '♙':
                piece = new Pawn(PieceColor.WHITE);
                break;
            default:
                throw new IllegalArgumentException("Unexpected piece unicode: " + c);
        }
        return piece;
    }
}

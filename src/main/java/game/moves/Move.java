package game.moves;

import game.pieces.Piece;

import java.util.Objects;

public class Move {
    private final Piece piece;
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;
    private Piece tookPiece = null;
    private boolean isChecking;

    public Move(Piece piece, int fromX, int fromY, int toX, int toY) {
        this.piece = piece;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getFromX() {
        return fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    public boolean isTaking() {
        return tookPiece != null;
    }

    public Piece getTookPiece() {
        return tookPiece;
    }

    public void setTookPiece(Piece tookPiece) {
        this.tookPiece = tookPiece;
    }

    public boolean isChecking() {
        return isChecking;
    }

    public void setChecking(boolean checking) {
        isChecking = checking;
    }

    public String getPrettyNotation() {
        return piece.getColor() +
                " " +
                getBasicNotation() +
                " (" +
                piece.getClass().getSimpleName() +
                ")";
    }

    public String getBasicNotation() {
        StringBuilder builder = new StringBuilder();
        builder.append(convertXToChar(fromX))
                .append(fromY + 1)
                .append(isTaking() ? "x" : "-")
                .append(convertXToChar(toX))
                .append(toY + 1);
        if (isChecking) {
            builder.append("+");
        }
        return builder.toString();
    }

    public static String convertXToChar(int x) {
        final int aAscii = 'a';
        return String.valueOf((char) (aAscii + x));
    }

    public boolean equalsForPositions(Move move) {
        return fromX == move.fromX && fromY == move.fromY && toX == move.toX && toY == move.toY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return equalsForPositions(move)
                && tookPiece == move.tookPiece && isChecking == move.isChecking && Objects.equals(piece, move.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, fromX, fromY, toX, toY, tookPiece, isChecking);
    }

    @Override
    public String toString() {
        return getPrettyNotation();
    }
}

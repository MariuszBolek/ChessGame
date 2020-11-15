package game.moves;

//En passant: https://en.wikipedia.org/wiki/En_passant
public class EnPassant extends Move {

    private final int tookPiecePosX;
    private final int tookPiecePosY;

    public EnPassant(Move move, int tookPiecePosX, int tookPiecePosY) {
        super(move.getPiece(), move.getFromX(), move.getFromY(), move.getToX(), move.getToY());
        this.tookPiecePosX = tookPiecePosX;
        this.tookPiecePosY = tookPiecePosY;
        setChecking(move.isChecking());
        setTookPiece(move.getTookPiece());
    }

    public int getTookPiecePosX() {
        return tookPiecePosX;
    }

    public int getTookPiecePosY() {
        return tookPiecePosY;
    }

    public String getPrettyNotation() {
        return super.getPrettyNotation() + " (en passant)";
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

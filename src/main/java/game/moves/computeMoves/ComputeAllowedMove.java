package game.moves.computeMoves;

import game.board.Board;
import game.moves.Move;
import game.pieces.Pawn;
import game.pieces.Piece;

import java.util.Optional;

import static game.board.Board.SIZE;

public class ComputeAllowedMove {

    private final static int MAX_MOVE = SIZE - 1;

    public Optional<Move> getAllowedMove(Piece piece, int posX, int posY, int deltaX, int deltaY, Board board) {
        return getAllowedMove(piece, posX, posY, deltaX, deltaY, board, true);
    }

    /** Board boundaries */
    public boolean isOutOfBounds(Move move) {

        return move.getToX() > MAX_MOVE || move.getToY() > MAX_MOVE || move.getToX() < 0 || move.getToY() < 0;
    }

    /** Calculates all allowed moves */
   public Optional<Move> getAllowedMove(Piece piece, int posX, int posY, int deltaX, int deltaY, Board board,
                                  boolean checkTakingDestPiece) {
        Move move = new Move(piece, posX, posY, posX + deltaX, posY + deltaY);
        if (isOutOfBounds(move)) {
            return Optional.empty();
        }
        if (checkTakingDestPiece) {
            Optional<Piece> destPiece = board.getPiece(move.getToX(), move.getToY());
            if (piece instanceof Pawn) {
                if (deltaX == 0) {
                    // Move forward, taking is not allowed, dest must be free
                    if (destPiece.isPresent()) {
                        return Optional.empty();
                    }
                } else {
                    // Move diagonal, taking is mandatory, dest must be other color
                    if (destPiece.isPresent()) {
                        if (destPiece.get().getColor() != piece.getColor()) {
                            move.setTookPiece(destPiece.get());
                        } else {
                            return Optional.empty();
                        }
                    } else {
                        return Optional.empty();
                    }
                }
            } else {
                if (destPiece.isPresent()) {
                    if (destPiece.get().getColor() != piece.getColor()) {
                        move.setTookPiece(destPiece.get());
                    } else {
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.of(move);
    }
}

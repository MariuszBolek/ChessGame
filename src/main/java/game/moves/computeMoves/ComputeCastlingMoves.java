package game.moves.computeMoves;

import game.board.Board;
import game.enums.PieceColor;
import game.moves.CastlingMove;
import game.moves.Move;
import game.pieces.King;
import game.pieces.Piece;
import game.pieces.Rook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ComputeCastlingMoves {

    private ComputeKingMoves kingMoves = new ComputeKingMoves();

 public    List<Move> computeCastlingMoves(Piece piece, int posX, int posY, Board board, List<Move> history) {
        if (!isValidKingPositionForCastling(piece, posX, posY, board)) {
            return Collections.emptyList();
        }

        List<Move> moves = new ArrayList<>();

        // castling theoretical positions - calculated from the queen position
        getCastlingMove(board, piece, posX, posY, 2, 0, 3, history).ifPresent(moves::add);
        // castling theoretical positions - - calculated from the king position
        getCastlingMove(board, piece, posX, posY, 6, 7, 5, history).ifPresent(moves::add);

        return moves;
    }

  public   Optional<Move> getCastlingMove(Board board, Piece piece, int kingFromX, int kingFromY, int kingToX,
                                   int rookFromX, int rookToX, List<Move> history) {

        if (history.stream().anyMatch(m ->
                (m.getFromX() == kingFromX && m.getFromY() == kingFromY)
                        || (m.getFromX() == rookFromX && m.getFromY() == kingFromY))) {
            return Optional.empty();
        }

        final PieceColor color = piece.getColor();

        // Check rook position
        Optional<Piece> rookOpt = board.getPiece(rookFromX, kingFromY);
        if (!(rookOpt.isPresent() && rookOpt.get() instanceof Rook && rookOpt.get().getColor() == color)) {
            return Optional.empty();
        }

        // Check room between rook and king
        for (int x = Math.min(rookFromX, kingFromX) + 1; x < Math.max(kingFromX, rookFromX); x++) {
            Optional<Piece> pieceBetween = board.getPiece(x, kingFromY);
            if (pieceBetween.isPresent()) {
                return Optional.empty();
            }
        }

        // Check that king does not cross fire during move
        for (int x = Math.min(kingToX, kingFromX + 1); x < Math.max(kingFromX, kingToX + 1); x++) {
            Move move = new Move(piece, kingFromX, kingFromY, x, kingFromY);
            board.doMove(move);
            boolean inCheck = kingMoves.isInCheck(board, color);
            board.undoMove(move);
            if (inCheck) {
                return Optional.empty();
            }
        }

        return Optional
                .of(new CastlingMove(piece, kingFromX, kingFromY, kingToX, kingFromY, rookOpt.get(), rookFromX, kingFromY,
                        rookToX, kingFromY));
    }

   public boolean isValidKingPositionForCastling(Piece piece, int posX, int posY, Board board) {
        final PieceColor color = piece.getColor();
        if (posX != 4) {
            return false;
        } else {
            if (color == PieceColor.WHITE) {
                if (posY != 0) {
                    return false;
                }
            } else {
                if (posY != 7) {
                    return false;
                }
            }
        }
        Optional<Piece> kingOpt = board.getPiece(posX, posY);
        if (kingOpt.isEmpty() || !(kingOpt.get() instanceof King) || kingOpt.get().getColor() != color) {
            return false;
        }
        return !kingMoves.isInCheck(board, color);
    }
}

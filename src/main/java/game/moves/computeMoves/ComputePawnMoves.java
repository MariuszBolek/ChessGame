package game.moves.computeMoves;

import game.board.Board;
import game.enums.PieceColor;
import game.moves.EnPassant;
import game.moves.Move;
import game.moves.PromotionMove;
import game.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComputePawnMoves {

   private ComputeAllowedMove allowedMove = new ComputeAllowedMove();

    /** Method computes Pawn moves, including en passant and promotion */
    public List<Move> computePawnMoves(Piece piece, int posX, int posY, Board board, List<Move> history,
                                       boolean takingMovesOnly) {
        List<Move> moves = new ArrayList<>();
        final PieceColor color = piece.getColor();

        int factor;
        int initialY;
        if (color == PieceColor.WHITE) {
            factor = 1;
            initialY = 1;
        } else {
            factor = -1;
            initialY = 6;
        }

        if (!takingMovesOnly) {

            Optional<Move> move1 = allowedMove.getAllowedMove(piece, posX, posY, 0, factor, board);
            if (move1.isPresent()) {
                moves.add(move1.get());

                if (posY == initialY) {
                    Optional<Move> move2 = allowedMove.getAllowedMove(piece, posX, posY, 0, 2 * factor, board);
                    move2.ifPresent(moves::add);
                }
            }
        }


        Optional<Move> move3 = allowedMove.getAllowedMove(piece, posX, posY, -1, factor, board, !takingMovesOnly);
        move3.ifPresent(moves::add);
        Optional<Move> move4 = allowedMove.getAllowedMove(piece, posX, posY, 1, factor, board, !takingMovesOnly);
        move4.ifPresent(moves::add);

        // en passant moves
        if (!history.isEmpty()) {
            Move lastMove = history.get(history.size() - 1);
            if (lastMove.getPiece() instanceof Pawn && lastMove.getFromY() - lastMove.getToY() == (2 * factor)
                    && lastMove.getToY() == posY && (lastMove.getToX() == posX - 1 || lastMove.getToX() == posX + 1)) {
                EnPassant move =
                        new EnPassant(new Move(piece, posX, posY, lastMove.getToX(), posY + factor), lastMove.getToX(),
                                lastMove.getToY());
                move.setTookPiece(board.getPiece(lastMove.getToX(), lastMove.getToY())
                        .orElseThrow(() -> new RuntimeException("En passant move expects a piece here")));
                moves.add(move);
            }
        }

        // promotion special moves
        List<Move> movesWithPromotion = new ArrayList<>();
        moves.forEach(move -> {
            if (move.getToY() == initialY + factor * 6) {
                movesWithPromotion.add(new PromotionMove(move, new Queen(move.getPiece().getColor())));
                movesWithPromotion.add(new PromotionMove(move, new Knight(move.getPiece().getColor())));
                movesWithPromotion.add(new PromotionMove(move, new Bishop(move.getPiece().getColor())));
                movesWithPromotion.add(new PromotionMove(move, new Rook(move.getPiece().getColor())));
            } else {
                movesWithPromotion.add(move);
            }
        });
        return movesWithPromotion;
    }
}

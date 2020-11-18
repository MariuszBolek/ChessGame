package game.moves.computeMoves;

import game.board.Board;
import game.moves.Move;
import game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static game.board.Board.SIZE;


public class ComputeStraightMoves {

    private final static int MAX_MOVE = SIZE - 1;
    private ComputeAllowedMove allowedMove = new ComputeAllowedMove();

    public List<Move> computeStraightMoves(Piece piece, int posX, int posY, Board board) {
        return computeStraightMoves(piece, posX, posY, board, SIZE);
    }

   public List<Move> computeStraightMoves(Piece piece, int posX, int posY, Board board, int maxDistance) {
        List<Move> moves = new ArrayList<>();

        computeStraightUpMove(piece, posX, posY, board, maxDistance, moves);
        computeStraightDownMove(piece, posX, posY, board, maxDistance, moves);
        computeStraightLeftMove(piece, posX, posY, board, maxDistance, moves);
        computeStraightRightMove(piece, posX, posY, board, maxDistance, moves);
        return moves;
    }

    public void computeStraightRightMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(MAX_MOVE-posX, maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, i+1, 0, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }

    public void computeStraightLeftMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(posX, maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, -i-1, 0, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }

    public void computeStraightDownMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(posY, maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, 0, -i-1, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }

   public void computeStraightUpMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(MAX_MOVE-posY, maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, 0, i+1, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }
}

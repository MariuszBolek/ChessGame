package game.moves.computeMoves;

import game.board.Board;
import game.moves.Move;
import game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static game.board.Board.SIZE;

public class ComputeDiagonalMoves {

    private final static int MAX_MOVE = SIZE - 1;
    private ComputeAllowedMove allowedMove = new ComputeAllowedMove();

    public List<Move> computeDiagonalMoves(Piece piece, int posX, int posY, Board board) {
        return computeDiagonalMoves(piece, posX, posY, board, SIZE);
    }

    public List<Move> computeDiagonalMoves(Piece piece, int posX, int posY, Board board, int maxDistance) {
        List<Move> moves = new ArrayList<>();

        computeLeftUpMove(piece, posX, posY, board, maxDistance, moves);
        computeRightUpMove(piece, posX, posY, board, maxDistance, moves);
        computeRightDownMove(piece, posX, posY, board, maxDistance, moves);
        computeLeftDownMove(piece, posX, posY, board, maxDistance, moves);

        return moves;
    }

    void computeLeftUpMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(Math.min(posX, MAX_MOVE-posY), maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, -i-1, i+1, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }

    void computeLeftDownMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(Math.min(posX, posY), maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, -i-1, -i-1, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }

    void computeRightDownMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(Math.min(MAX_MOVE-posX, posY), maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, i+1, -i-1, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }

    void computeRightUpMove(Piece piece, int posX, int posY, Board board, int maxDistance, List<Move> moves) {
        for (int i = 0; i < Math.min(Math.min(MAX_MOVE-posX, MAX_MOVE-posY), maxDistance); i++) {
            Optional<Move> move = allowedMove.getAllowedMove(piece, posX, posY, i+1, i+1, board);
            move.ifPresent(moves::add);
            if (move.isEmpty() || move.get().isTaking()) {
                break;
            }
        }
    }
}

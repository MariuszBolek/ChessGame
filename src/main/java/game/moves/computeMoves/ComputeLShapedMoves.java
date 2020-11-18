package game.moves.computeMoves;

import game.board.Board;
import game.moves.Move;
import game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;


public class ComputeLShapedMoves {
    private ComputeAllowedMove allowedMove = new ComputeAllowedMove();

    public List<Move> computeLShapeMoves(Piece piece, int posX, int posY, Board board) {
        List<Move> moves = new ArrayList<>();
        allowedMove.getAllowedMove(piece, posX, posY, 1, 2, board).ifPresent(moves::add);
        allowedMove.getAllowedMove(piece, posX, posY, 1, -2, board).ifPresent(moves::add);
        allowedMove.getAllowedMove(piece, posX, posY, -1, 2, board).ifPresent(moves::add);
        allowedMove.getAllowedMove(piece, posX, posY, -1, -2, board).ifPresent(moves::add);
        allowedMove.getAllowedMove(piece, posX, posY, 2, 1, board).ifPresent(moves::add);
        allowedMove.getAllowedMove(piece, posX, posY, 2, -1, board).ifPresent(moves::add);
        allowedMove.getAllowedMove(piece, posX, posY, -2, 1, board).ifPresent(moves::add);
        allowedMove.getAllowedMove(piece, posX, posY, -2, -1, board).ifPresent(moves::add);
        return moves;
    }
}

package game.moves;

import game.board.Board;
import game.board.Position;
import game.enums.GameState;
import game.enums.PieceColor;
import game.moves.computeMoves.*;
import game.pieces.*;

import java.util.*;

import static game.board.Board.SIZE;
import static java.util.stream.Collectors.toList;
import static game.pieces.ColorSwap.swap;

public class MoveService {

    private ComputeStraightMoves straightMoves = new ComputeStraightMoves();
    private ComputeDiagonalMoves diagonalMoves = new ComputeDiagonalMoves();
    private ComputeLShapedMoves lShapedMoves = new ComputeLShapedMoves();
    private ComputeKingMoves kingMoves = new ComputeKingMoves();
    private ComputeCastlingMoves castlingMoves = new ComputeCastlingMoves();
    private ComputePawnMoves pawnMoves = new ComputePawnMoves();


    public List<Move> computeAllMoves(Board board, PieceColor color, List<Move> history, boolean withAdditionalInfo) {
        return computeAllMoves(board, color, history, withAdditionalInfo, false);
    }

    public List<Move> computeAllMoves(Board board, PieceColor color, List<Move> history, boolean withAdditionalInfo,
                                      boolean takingMovesOnly) {
        return computeBoardMoves(board, color, history, withAdditionalInfo, false, takingMovesOnly);
    }

    public List<Move> computeMoves(Board board, Piece piece, int posX, int posY, List<Move> history,
                                   boolean withAdditionalInfo, boolean takingMovesOnly) {
        List<Move> moves = new ArrayList<>();
        final PieceColor color = piece.getColor();

        if (piece instanceof Pawn) {
            moves.addAll(pawnMoves.computePawnMoves(piece, posX, posY, board, history, takingMovesOnly));
        } else if (piece instanceof Knight) {
            moves.addAll(lShapedMoves.computeLShapeMoves(piece, posX, posY, board));
        } else if (piece instanceof Bishop) {
            moves.addAll(diagonalMoves.computeDiagonalMoves(piece, posX, posY, board));
        } else if (piece instanceof Rook) {
            moves.addAll(straightMoves.computeStraightMoves(piece, posX, posY, board));
        } else if (piece instanceof Queen) {
            moves.addAll(straightMoves.computeStraightMoves(piece, posX, posY, board));
            moves.addAll(diagonalMoves.computeDiagonalMoves(piece, posX, posY, board));
        } else if (piece instanceof King) {
            moves.addAll(straightMoves.computeStraightMoves(piece, posX, posY, board, 1));
            moves.addAll(diagonalMoves.computeDiagonalMoves(piece, posX, posY, board, 1));
            if (!takingMovesOnly) {
                moves.addAll(castlingMoves.computeCastlingMoves(piece, posX, posY, board, history));
            }
        } else {
            throw new RuntimeException("Unexpected piece type");
        }

        if (withAdditionalInfo) {
            return moves.stream().filter(move -> {
                board.doMove(move);

                // Checking opponent's king
                move.setChecking(kingMoves.isInCheck(board, swap(color)));

                boolean valid = kingMoves.isValidSituation(board, color);
                board.undoMove(move);
                return valid;
            }).collect(toList());
        } else {
            return moves;
        }
    }

    public GameState getGameState(Board board, PieceColor colorToPlay, List<Move> history) {
        if (!canMove(board, colorToPlay, history)) {
            if (kingMoves.isInCheck(board, colorToPlay)) {
                // Checkmate
                return GameState.LOSS;
            } else {
                // Stalemate
                return GameState.DRAW_STALEMATE;
            }
        }

        if (history.size() >= 10) {
            Move move6 = history.get(history.size() - 1);
            Move move4 = history.get(history.size() - 5);
            Move move2 = history.get(history.size() - 9);
            Move move5 = history.get(history.size() - 2);
            Move move3 = history.get(history.size() - 6);
            Move move1 = history.get(history.size() - 10);
            if (move6.equals(move4) && move6.equals(move2) && move5.equals(move3) && move5.equals(move1)) {
                // Threefold repetition
                return GameState.DRAW_THREEFOLD;
            }
        }

        if (history.size() >= 50) {
            List<Move> last50Moves = history.subList(history.size() - 50, history.size() - 1);
            if (last50Moves.stream().noneMatch(move -> move.isTaking() || move.getPiece() instanceof Pawn)) {
                // 50-move (no pawn moved, no capture)
                return GameState.DRAW_50_MOVES;
            }
        }

        return GameState.IN_PROGRESS;
    }


    boolean canMove(Board board, PieceColor color, List<Move> history) {
        List<Move> moves = computeBoardMoves(board, color, history, true, true, false);
        return !moves.isEmpty();
    }

    List<Move> computeBoardMoves(Board board, PieceColor color, List<Move> history, boolean withAdditionalInfo,
                                 boolean returnFirstPieceMoves, boolean takingMovesOnly) {
        List<Move> moves = new ArrayList<>();
        List<PiecePosition> piecePositions = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Optional<Piece> piece = board.getPiece(i, j);
                if (piece.isPresent() && piece.get().getColor() == color) {
                    piecePositions.add(new PiecePosition(piece.get(), new Position(i, j)));
                }
            }
        }

        piecePositions.sort((p1, p2) -> p2.getPiece().getValue() - p1.getPiece().getValue());

        for (PiecePosition piecePosition : piecePositions) {
            List<Move> pieceMoves = computeMoves(board, piecePosition.getPiece(), piecePosition.getPosition().getX(),
                    piecePosition.getPosition().getY(), history, withAdditionalInfo, takingMovesOnly);
            if (!pieceMoves.isEmpty() && returnFirstPieceMoves) {
                return pieceMoves;
            }
            moves.addAll(pieceMoves);
        }
        return moves;
    }


    private static class PiecePosition {
        private final Piece piece;
        private final Position position;

        public PiecePosition(Piece piece, Position position) {
            this.piece = piece;
            this.position = position;
        }

        public Piece getPiece() {
            return piece;
        }

        public Position getPosition() {
            return position;
        }
    }
}

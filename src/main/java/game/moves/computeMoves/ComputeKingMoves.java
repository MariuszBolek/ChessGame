package game.moves.computeMoves;

import game.board.Board;
import game.board.Position;
import game.enums.PieceColor;
import game.moves.Move;
import game.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static game.board.Board.SIZE;
import static game.pieces.ColorSwap.swap;

public class ComputeKingMoves {
    private final static int MAX_MOVE = SIZE - 1;
    private ComputeStraightMoves straightMoves = new ComputeStraightMoves();
    private ComputeDiagonalMoves diagonalMoves = new ComputeDiagonalMoves();
    private ComputeLShapedMoves lShapedMoves = new ComputeLShapedMoves();

    public Optional<Position> findKingPosition(Board board, PieceColor color) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Optional<Piece> pieceOpt = board.getPiece(x, y);
                if (pieceOpt.isPresent()) {
                    Piece piece = pieceOpt.get();
                    if (piece instanceof King && piece.getColor() == color) {
                        return Optional.of(new Position(x, y));
                    }
                }
            }
        }
        return Optional.empty();
    }
   public boolean isInCheck(Board board, PieceColor color) {
        final Position kingPosition = findKingPosition(board, color).orElseThrow(() -> new RuntimeException("King not found"));

        return isInStraightCheck(board, kingPosition, color)
                || isInDiagonalCheck(board, kingPosition, color)
                || isInLCheck(board, kingPosition, color)
                || isInPawnCheck(board, kingPosition, color);
    }
    public boolean isValidSituation(Board boardAfterMove, PieceColor color) {
        // check kings mutual distance
        Optional<Position> king1 = findKingPosition(boardAfterMove, color);
        Optional<Position> king2 = findKingPosition(boardAfterMove, swap(color));
        if (king1.isEmpty() || king2.isEmpty()) {
            // cannot eat king (would mean being checkmate)
            return false;
        }
        if (Math.max(Math.abs(king1.get().getX() - king2.get().getX()), Math.abs(king1.get().getY() - king2.get().getY())) <= 1) {
            return false;
        }

        return !isInCheck(boardAfterMove, color);
    }

    boolean isInPawnCheck(Board board, Position kingPosition, PieceColor color) {
        int factor = color == PieceColor.BLACK ? -1 : 1;
        int y = kingPosition.getY() + factor;
        if (y >= 0 && y <= MAX_MOVE) {
            List<Piece> destinations = new ArrayList<>();
            if (kingPosition.getX() >= 1) {
                board.getPiece(kingPosition.getX() - 1, y).ifPresent(destinations::add);
            }
            if (kingPosition.getX() < MAX_MOVE) {
                board.getPiece(kingPosition.getX() + 1, y).ifPresent(destinations::add);
            }
            return destinations.stream()
                    .anyMatch(destination -> destination.getColor() != color && destination instanceof Pawn);
        }
        return false;
    }

    boolean isInLCheck(Board board, Position kingPosition, PieceColor color) {
        final Piece fakeKnight = new Knight(color);
        return lShapedMoves.computeLShapeMoves(fakeKnight, kingPosition.getX(), kingPosition.getY(), board).stream()
                .filter(Move::isTaking).anyMatch(move -> {
                    Piece takenPiece = board.getPiece(move.getToX(), move.getToY())
                            .orElseThrow(() -> new RuntimeException("Cannot take an empty piece!"));
                    return takenPiece instanceof Knight;
                });
    }

    boolean isInDiagonalCheck(Board board, Position kingPosition, PieceColor color) {
        final Piece fakeBishop = new Bishop(color);
        return diagonalMoves.computeDiagonalMoves(fakeBishop, kingPosition.getX(), kingPosition.getY(), board).stream()
                .filter(Move::isTaking).anyMatch(move -> {
                    Piece takenPiece = board.getPiece(move.getToX(), move.getToY())
                            .orElseThrow(() -> new RuntimeException("Cannot take an empty piece!"));
                    return takenPiece instanceof Bishop || takenPiece instanceof Queen;
                });
    }

    boolean isInStraightCheck(Board board, Position kingPosition, PieceColor color) {
        Piece fakeRook = new Rook(color);
        return straightMoves.computeStraightMoves(fakeRook, kingPosition.getX(), kingPosition.getY(), board).stream()
                .filter(Move::isTaking).anyMatch(move -> {
                    Piece takenPiece = board.getPiece(move.getToX(), move.getToY())
                            .orElseThrow(() -> new RuntimeException("Cannot take an empty piece!"));
                    return takenPiece instanceof Rook || takenPiece instanceof Queen;
                });
    }

}

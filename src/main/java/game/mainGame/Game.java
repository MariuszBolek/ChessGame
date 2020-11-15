package game.mainGame;

import static game.board.Board.SIZE;

import game.board.Board;
import game.enums.GameState;
import game.enums.PieceColor;
import game.moves.Move;
import game.pieces.*;
import game.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    final Player whitePlayer;
    final Player blackPlayer;
    final Board board;
    final List<Move> history;
    PieceColor toPlay;
    GameState state;
    String opening;

    public Game(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.toPlay = PieceColor.WHITE;
        this.history = new ArrayList<>();
        this.state = GameState.IN_PROGRESS;
        this.board = new Board(getInitialPiecesPositions());
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public List<Move> getHistory() {
        return history;
    }

    public PieceColor getToPlay() {
        return toPlay;
    }

    public void setToPlay(PieceColor toPlay) {
        this.toPlay = toPlay;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public void addMoveToHistory(Move move) {
        this.history.add(move);
    }

    public Player getPlayerByColor(PieceColor color) {
        switch (color) {
            case WHITE:
                return whitePlayer;
            case BLACK:
                return blackPlayer;
        }
        throw new RuntimeException("Unexpected color");
    }

    public Player getPlayerToPlay() {
        return getPlayerByColor(toPlay);
    }

    public Player getPlayerWaiting() {
        switch (toPlay) {
            case WHITE:
                return blackPlayer;
            case BLACK:
                return whitePlayer;
        }
        throw new RuntimeException("Unexpected color");
    }

    public void removeLastMoveFromHistory() {
        this.history.remove(this.history.size() - 1);
    }

    public boolean canBePlayed() {
        return whitePlayer != null && blackPlayer != null && state == GameState.IN_PROGRESS;
    }

    private Piece[][] getInitialPiecesPositions() {
        Piece[][] pos = new Piece[SIZE][SIZE];
        pos[0][0] = new Rook(PieceColor.WHITE);
        pos[0][1] = new Knight(PieceColor.WHITE);
        pos[0][2] = new Bishop(PieceColor.WHITE);
        pos[0][3] = new Queen(PieceColor.WHITE);
        pos[0][4] = new King(PieceColor.WHITE);
        pos[0][5] = new Bishop(PieceColor.WHITE);
        pos[0][6] = new Knight(PieceColor.WHITE);
        pos[0][7] = new Rook(PieceColor.WHITE);
        pos[1][0] = new Pawn(PieceColor.WHITE);
        pos[1][1] = new Pawn(PieceColor.WHITE);
        pos[1][2] = new Pawn(PieceColor.WHITE);
        pos[1][3] = new Pawn(PieceColor.WHITE);
        pos[1][4] = new Pawn(PieceColor.WHITE);
        pos[1][5] = new Pawn(PieceColor.WHITE);
        pos[1][6] = new Pawn(PieceColor.WHITE);
        pos[1][7] = new Pawn(PieceColor.WHITE);

        pos[7][0] = new Rook(PieceColor.BLACK);
        pos[7][1] = new Knight(PieceColor.BLACK);
        pos[7][2] = new Bishop(PieceColor.BLACK);
        pos[7][3] = new Queen(PieceColor.BLACK);
        pos[7][4] = new King(PieceColor.BLACK);
        pos[7][5] = new Bishop(PieceColor.BLACK);
        pos[7][6] = new Knight(PieceColor.BLACK);
        pos[7][7] = new Rook(PieceColor.BLACK);
        pos[6][0] = new Pawn(PieceColor.BLACK);
        pos[6][1] = new Pawn(PieceColor.BLACK);
        pos[6][2] = new Pawn(PieceColor.BLACK);
        pos[6][3] = new Pawn(PieceColor.BLACK);
        pos[6][4] = new Pawn(PieceColor.BLACK);
        pos[6][5] = new Pawn(PieceColor.BLACK);
        pos[6][6] = new Pawn(PieceColor.BLACK);
        pos[6][7] = new Pawn(PieceColor.BLACK);

        return pos;
    }
}

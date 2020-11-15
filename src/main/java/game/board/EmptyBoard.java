package game.board;

import game.moves.Move;
import game.pieces.Piece;
import game.enums.PieceColor;
import game.players.CreateOpponent;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.Optional;

public class EmptyBoard implements BoardInterface {
    @Override
    public Square[][] getSquares() {
        return new Square[0][];
    }

    @Override
    public void setItemNewActionListener(ActionListener actionListener) {

    }


    @Override
    public void setItemPrintToConsoleActionListener(ActionListener actionListener) {

    }


    @Override
    public void setItemUndoMoveActionListener(ActionListener actionListener) {

    }

    @Override
    public void setItemProposeDrawActionListener(ActionListener actionListener) {

    }

    @Override
    public void display(Piece[][] positions, boolean isReversed) {

    }

    @Override
    public void refresh(Piece[][] positions) {

    }

    @Override
    public void resetAllClickables() {

    }

    @Override
    public void cleanSquaresBorder() {

    }

    @Override
    public void addBorderToLastMoveSquares(Move move) {

    }

    @Override
    public Optional<File> saveGameDialog() {
        return Optional.empty();
    }

    @Override
    public Optional<File> loadGameDialog() {
        return Optional.empty();
    }

    @Override
    public PrepareGameBoard prepareGameDialog(CreateOpponent CreateOpponent, boolean exitOnCancel) {
        return null;
    }

    @Override
    public Piece promotionDialog(PieceColor color) {
        return null;
    }

    @Override
    public void popupInfo(String message) {

    }

    @Override
    public void popupError(String message) {

    }
}

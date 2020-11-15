package game.board;


import game.moves.Move;
import game.pieces.Piece;
import game.enums.PieceColor;
import game.players.CreateOpponent;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.Optional;

public interface BoardInterface {
    Square[][] getSquares();

    void setItemNewActionListener(ActionListener actionListener);


    void setItemPrintToConsoleActionListener(ActionListener actionListener);


    void setItemUndoMoveActionListener(ActionListener actionListener);

    void setItemProposeDrawActionListener(ActionListener actionListener);

    void display(Piece[][] positions, boolean isReversed);

    void refresh(Piece[][] positions);

    void resetAllClickables();

    void cleanSquaresBorder();

    void addBorderToLastMoveSquares(Move move);

    Optional<File> saveGameDialog();

    Optional<File> loadGameDialog();

    PrepareGameBoard prepareGameDialog(CreateOpponent CreateOpponent, boolean exitOnCancel);

    Piece promotionDialog(PieceColor color);

    void popupInfo(String message);

    void popupError(String message);
}

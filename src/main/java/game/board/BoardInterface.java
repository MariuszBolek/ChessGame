package game.board;


import game.moves.Move;
import game.pieces.Piece;
import game.enums.PieceColor;
import game.players.CreateOpponent;

import java.awt.event.ActionListener;

public interface BoardInterface {
    Square[][] getSquares();

    void setItemNewActionListener(ActionListener actionListener);


    void setItemPrintToConsoleActionListener(ActionListener actionListener);


    void setItemUndoMoveActionListener(ActionListener actionListener);

    void setItemProposeDrawActionListener(ActionListener actionListener);

    void display(Piece[][] positions, boolean isReversed);

    void refresh(Piece[][] positions);

    void resetAllClickedSquares();

    void cleanSquaresBorder();

    void addBorderToLastMoveSquares(Move move);

    PrepareGameBoard createNewGameOptions(CreateOpponent CreateOpponent, boolean exitOnCancel);

    Piece promotionDialog(PieceColor color);

    void popupInfo(String message);

    void popupError(String message);
}

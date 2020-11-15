package game.mainGame;

import static game.board.Board.SIZE;
import static game.pieces.ColorSwap.swap;

import game.board.Board;
import game.board.BoardInterface;
import game.board.PrepareGameBoard;
import game.board.Square;
import game.enums.GameState;
import game.enums.PieceColor;
import game.moves.Move;
import game.moves.MoveService;
import game.moves.PromotionMove;
import game.pieces.Piece;
import game.pieces.Queen;
import game.players.Bot;
import game.players.CreateOpponent;
import game.players.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameController {
    private final static Logger logger = LoggerFactory.getLogger(GameController.class);

    private static final Border RED_BORDER = BorderFactory.createLineBorder(java.awt.Color.red, 3, true);
    private static final Border BLUE_BORDER = BorderFactory.createLineBorder(java.awt.Color.blue, 3, true);

    private Consumer<GameResult> gameResultConsumer;

    private final BoardInterface boardInterface;
    private Board board;
    private Game game;
    private final CreateGame createGame;
    private final CreateOpponent createOpponent;
    private final MoveService moveService;


    private Square selectedSquare = null;

    public GameController(BoardInterface boardInterface, CreateGame createGame, CreateOpponent createOpponent, MoveService moveService) {
        this.moveService = moveService;
        this.boardInterface = boardInterface;
        this.createGame = createGame;
        this.createOpponent = createOpponent;
        initView(createGame.emptyGame().getBoard());
    }

    void initView(Board board) {
        refreshBoardView(board);
        boardInterface.setItemNewActionListener(actionEvent -> {
            newGame(null, false, r -> {});
        });
        boardInterface.setItemPrintToConsoleActionListener(actionEvent -> printGameToConsole());
        boardInterface.setItemUndoMoveActionListener(actionEvent -> undoLastMove());
        boardInterface.setItemProposeDrawActionListener(actionEvent -> evaluateDrawProposal());
    }

    public void newGame(PrepareGameBoard prepareGameBoard, boolean exitOnCancel, Consumer<GameResult> gameResultConsumer) {
        if (prepareGameBoard == null) {
            PrepareGameBoard prepareGameBoardFromDialog = boardInterface.prepareGameDialog(createOpponent, exitOnCancel);
            if (prepareGameBoardFromDialog == null && !exitOnCancel) {
                return;
            } else {
                prepareGameBoard = prepareGameBoardFromDialog;
            }
        }
        this.game = createGame.createGame(prepareGameBoard);
        this.board = game.getBoard();
        this.gameResultConsumer = gameResultConsumer;
        refreshBoardView(board);
        cleanSelectedSquare();
//        initView(createGame.emptyGame().getBoard());
        if (game.canBePlayed()) {
            play();
        }
    }

    void refreshBoardView(Board board) {
        boolean isReversed = false;
        if (game != null && game.canBePlayed() && game.getWhitePlayer().isBot()) {
            isReversed = true;
        }
        boardInterface.display(board.getBoard(), isReversed);
    }

    void play() {
        SwingUtilities.invokeLater(this::playNextMove);
    }

    void playNextMove() {
        while (game.getPlayerToPlay().isBot() && isGameOver(game)) {
            Player player = game.getPlayerToPlay();
            if (!(player instanceof Bot)) {
                throw new RuntimeException("Player has to be a bot");
            }
            Bot bot = (Bot) player;
            Instant start = Instant.now();

            FindBestMoveTask findBestMoveTask = new FindBestMoveTask(bot, game);
            findBestMoveTask.execute();
            Move move;

            try {
                move = findBestMoveTask.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Move computation failed", e);
            }
            Instant end = Instant.now();
            boolean showTiming = true;
            if (showTiming) {
                logger.debug("Time to select move by bot: {}", Duration.between(start, end));
            }
            doMove(move);
        }

        if (!game.getPlayerToPlay().isBot() && isGameOver(game)) {
            boardInterface.resetAllClickables();
            markSquaresClickableByColor(game.getToPlay());
        }
    }

    void doMove(Move move) {
        Player player = game.getPlayerByColor(move.getPiece().getColor());
        boardInterface.cleanSquaresBorder();
        if (!player.isBot()) {
            cleanSelectedSquare();
            boardInterface.resetAllClickables();
        }

        List<Move> matchingMoves = moveService
                .computeMoves(board, move.getPiece(), move.getFromX(), move.getFromY(), game.getHistory(), true, false)
                .stream().filter(m -> m.equalsForPositions(move)).collect(Collectors.toList());
        Move allowedMove = getAllowedMove(move, player, matchingMoves);

        board.doMove(allowedMove);
        boardInterface.refresh(board.getBoard());
        boardInterface.addBorderToLastMoveSquares(allowedMove);
        info(allowedMove.getPrettyNotation(), false);
        game.addMoveToHistory(allowedMove);
        game.setToPlay(swap(allowedMove.getPiece().getColor()));
        displayGameInfo(allowedMove);
    }

    Move getAllowedMove(Move move, Player player, List<Move> matchingMoves) {
        List<Move> allowedMoves;
        if (!matchingMoves.isEmpty() && matchingMoves.stream().allMatch(m -> m instanceof PromotionMove)) {
            Piece promotedPiece;
            if (move instanceof PromotionMove) {
                promotedPiece = ((PromotionMove) move).getPromotedPiece();
            } else {
                if (player.isBot()) {
                    promotedPiece = new Queen(move.getPiece().getColor());
                } else {
                    promotedPiece = boardInterface.promotionDialog(move.getPiece().getColor());
                }
            }
            allowedMoves = matchingMoves.stream()
                    .filter(m -> ((PromotionMove) m).getPromotedPiece().getClass().equals(promotedPiece.getClass()))
                    .collect(Collectors.toList());
        } else {
            allowedMoves = matchingMoves;
        }
        if (allowedMoves.isEmpty()) {
            throw new RuntimeException("Unauthorized move: " + move.getBasicNotation());
        }
        if (allowedMoves.size() > 1) {
            throw new RuntimeException(
                    "Ambiguous move: " + move.getBasicNotation() + ". Multiple moves possible here: " + allowedMoves
                            .toString());
        }
        return allowedMoves.get(0);
    }

    void undoLastMove(Move move) {
        Player player = game.getPlayerByColor(move.getPiece().getColor());
        if (!player.isBot()) {
            cleanSelectedSquare();
            boardInterface.cleanSquaresBorder();
            boardInterface.resetAllClickables();
        }

        board.undoMove(move);
        boardInterface.refresh(board.getBoard());
        info("Undo: " + move.getPrettyNotation(), false);
        game.removeLastMoveFromHistory();
        game.setToPlay(move.getPiece().getColor());
    }

    void displayGameInfo(Move move) {
        boolean showPopup = !game.getWhitePlayer().isBot() || !game.getBlackPlayer().isBot();
        GameState state;
        if (game.getState() != null && !game.getState().isInProgress()) {
            state = game.getState();
        } else {
            state = moveService.getGameState(game.getBoard(), game.getToPlay(), game.getHistory());
        }
        switch (state) {
            case LOSS:
                PieceColor winningColor = move.getPiece().getColor();
                Player winner = game.getPlayerByColor(winningColor);
                if (winningColor == PieceColor.WHITE) {
                    info("1-0" + getNbMovesInfo(game), false);
                    gameResultConsumer.accept(new GameResult(game.getHistory().size(), GameResult.Result.WHITE_WINS));
                } else {
                    info("0-1" + getNbMovesInfo(game), false);
                    gameResultConsumer.accept(new GameResult(game.getHistory().size(), GameResult.Result.BLACK_WINS));
                }
                if (winner.isBot()) {
                    info("Checkmate!" +
                            "\nYou did your best.", showPopup);
                } else {
                    info(
                            "Checkmate!" +
                                    "\nCongratulations on winning!",
                            showPopup);
                }
                break;
            case DRAW_STALEMATE:
                info("1/2-1/2" + getNbMovesInfo(game), false);
                info("Draw (Stalemate). The game is over.", showPopup);
                gameResultConsumer.accept(new GameResult(game.getHistory().size(), GameResult.Result.DRAW));
                break;
            case DRAW_50_MOVES:
                info("1/2-1/2" + getNbMovesInfo(game), false);
                info("Draw (50 moves). The game is over.", showPopup);
                gameResultConsumer.accept(new GameResult(game.getHistory().size(), GameResult.Result.DRAW));
                break;
            case DRAW_THREEFOLD:
                info("1/2-1/2" + getNbMovesInfo(game), false);
                info("Draw (threefold). The game is over.", showPopup);
                gameResultConsumer.accept(new GameResult(game.getHistory().size(), GameResult.Result.DRAW));
                break;
            case DRAW_AGREEMENT:
                info("1/2-1/2" + getNbMovesInfo(game), false);
                info("Draw (agreement). The game is over.", showPopup);
                gameResultConsumer.accept(new GameResult(game.getHistory().size(), GameResult.Result.DRAW));
                break;
            case IN_PROGRESS:
            default:
                if (move.isChecking()) {
                    info("Check!", showPopup);
                }
                break;
        }
    }

    private String getNbMovesInfo(Game game) {
        return " (" + game.getHistory().size() + " moves)";
    }

    private void markSquaresClickableByColor(PieceColor color) {
        Square[][] squares = boardInterface.getSquares();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Square square = squares[i][j];
                Piece piece = square.getPiece();
                if (piece != null && piece.getColor() == color) {
                    markSquareClickable(square);
                }
            }
        }
    }

    private void markSquareClickable(Square square) {
        if (square.getMouseListeners().length == 0) {
            square.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    try {
                        squareClicked(square);
                    } catch (Exception exception) {
                        error(exception, true);
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    square.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(MouseEvent e) {
                    square.setCursor(Cursor.getDefaultCursor());
                }
            });
        }
    }

    private void squareClicked(Square square) {
        if (selectedSquare != null) {
            if (selectedSquare == square) {
                // cancel current selection
                boardInterface.cleanSquaresBorder();
                cleanSelectedSquare();
                boardInterface.resetAllClickables();
                markSquaresClickableByColor(game.getToPlay());
            } else {
                doMove(new Move(selectedSquare.getPiece(), selectedSquare.getPosition().getX(), selectedSquare.getPosition().getY(), square.getPosition().getX(), square.getPosition().getY()));
                play();
            }
        } else {
            if (square.getPiece() != null) {
                if (square.getPiece().getColor() == game.getToPlay()) {
                    selectedSquare = square;
                    boardInterface.cleanSquaresBorder();
                    boardInterface.resetAllClickables();
                    // Self piece is clickable so that it selection can be cancelled
                    markSquareClickable(square);
                    square.setBorder(RED_BORDER);
                    List<Move> moves = moveService.computeMoves(board, square.getPiece(), square.getPosition().getX(),
                            square.getPosition().getY(), game.getHistory(), true, false);
                    for (Move move : moves) {
                        Square destination = boardInterface.getSquares()[move.getToY()][move.getToX()];
                        destination.setBorder(BLUE_BORDER);
                        markSquareClickable(destination);
                    }
                } else {
                    throw new RuntimeException("Cannot select a piece from opponent to start a move");
                }
            } else {
                throw new RuntimeException("Cannot select an empty square to start a move");
            }
        }
    }

    private void info(String text, boolean withPopup) {
        logger.info("[INFO] {}", text);
        if (withPopup) {
            boardInterface.popupInfo(text);
        }
    }

    private void error(Exception exception, boolean withPopup) {
        logger.error("An error happened: {}", exception.getMessage(), exception);
        if (withPopup) {
            boardInterface.popupError(exception.getMessage());
        }
    }

    private boolean isGameOver(Game game) {
        return moveService.getGameState(game.getBoard(), game.getToPlay(), game.getHistory()).isInProgress();
    }

    private void cleanSelectedSquare() {
        this.selectedSquare = null;
    }


    void printGameToConsole() {
        logger.debug("Current board: \n{}", board.toString());
    }

    void undoLastMove() {
        if (game.getHistory().size() < 2) {
            return;
        }
        Move lastMove = getLastMove();
        undoLastMove(lastMove);
        Move secondLastMove = getLastMove();
        undoLastMove(secondLastMove);
        play();
    }

    void evaluateDrawProposal() {
        Player playerWaiting = game.getPlayerWaiting();
        if (playerWaiting.isBot()) {
            boolean drawAccepted = ((Bot) playerWaiting).isDrawAcceptable(game);
            if (drawAccepted) {
                info("Hmmm OK, I hate draws but you played quite well... Accepted!", true);
                game.setState(GameState.DRAW_AGREEMENT);
                cleanSelectedSquare();
                boardInterface.cleanSquaresBorder();
                boardInterface.resetAllClickables();
                displayGameInfo(null);
            } else {
                info("Are you kidding me? A champion like me can't accept such proposal (at least not now).", true);
            }
        } else {
            info("Sorry, it is not your turn", true);
        }
    }

    private Move getLastMove() {
        return game.getHistory().get(game.getHistory().size() - 1);
    }

    private static class FindBestMoveTask extends SwingWorker<Move, Object> {
        final private Bot bot;
        final private Game game;

        public FindBestMoveTask(Bot bot, Game game) {
            this.bot = bot;
            this.game = game;
        }

        @Override
        protected Move doInBackground() {
            return bot.selectMove(game);
        }
    }
}

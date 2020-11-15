package game.board;

import game.enums.BoardSquares;
import game.enums.PieceColor;
import game.moves.Move;
import game.pieces.*;
import game.players.CreateOpponent;
import game.players.Human;
import game.players.Player;
import game.tools.PropertyReader;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static game.board.Board.SIZE;

public class BoardView extends JFrame implements BoardInterface {

    private static final Border NO_BORDER = BorderFactory.createEmptyBorder();
    private static final Border GREEN_BORDER = BorderFactory.createLineBorder(java.awt.Color.green, 3, true);

    private final boolean visible;
    private final Container contentPane;

    private final PropertyReader reader;
    private Square[][] squares = new Square[SIZE][SIZE];

    private JMenuItem itemNew;
    private JMenuItem itemPrintToConsole;
    private JMenuItem itemUndoMove;
    private JMenuItem itemProposeDraw;

    public BoardView(String title, PropertyReader reader) {
        this(title, reader, true);
    }

    public BoardView(String title, PropertyReader reader, boolean visible) {
        this.reader = reader;
        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.visible = visible;
        this.contentPane = getContentPane();
          int sizeWithLabels = 10;
        GridLayout gridLayout = new GridLayout(sizeWithLabels, sizeWithLabels);
        contentPane.setLayout(gridLayout);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setMenu();
    }


    public Square[][] getSquares() {
        return squares;
    }

    public void setItemNewActionListener(ActionListener actionListener) {
        itemNew.addActionListener(actionListener);
    }

    public void setItemPrintToConsoleActionListener(ActionListener actionListener) {
        itemPrintToConsole.addActionListener(actionListener);
    }


    public void setItemUndoMoveActionListener(ActionListener actionListener) {
        itemUndoMove.addActionListener(actionListener);
    }

    public void setItemProposeDrawActionListener(ActionListener actionListener) {
        itemProposeDraw.addActionListener(actionListener);
    }

    public void display(Piece[][] positions, boolean isReversed) {
        java.util.List<Component> components = new ArrayList<>((SIZE + 2) ^ 2);
        addFilesLabels(components);
        BoardSquares background;
        for (int i = positions.length - 1; i >= 0; i--) {
            components.add(getRankLabel(i));
            background = getFirstSquareBackground(i);
            for (int j = 0; j < positions[i].length; j++) {
                Piece piece = positions[i][j];
                // Inverse coordinates (positions is a 2D array, reversed)
                Square square = new Square(piece, new Position(j, i), background, reader.getPieceFont());
                background = swapBackground(background);
                components.add(square);
                squares[i][j] = square;
            }
            components.add(getRankLabel(i));
        }
        addFilesLabels(components);
        contentPane.removeAll();
        if (isReversed) {
            ListIterator<Component> li = components.listIterator(components.size());
            while (li.hasPrevious()) {
                contentPane.add(li.previous());
            }
        } else {
            components.forEach(contentPane::add);
        }
        setVisible(visible);
    }

    public void refresh(Piece[][] positions) {
        for (int i = positions.length - 1; i >= 0; i--) {
            for (int j = 0; j < positions[i].length; j++) {
                Piece piece = positions[i][j];
                // get current square, so that only its label is updated
                squares[i][j].setPiece(piece);
            }
        }
    }

    public void resetAllClickables() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Square square = squares[i][j];
                Stream.of(square.getMouseListeners()).forEach(square::removeMouseListener);
                square.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    public void cleanSquaresBorder() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Square square = squares[i][j];
                square.setBorder(NO_BORDER);
            }
        }
    }

    public void addBorderToLastMoveSquares(Move move) {
        Square from = squares[move.getFromY()][move.getFromX()];
        Square to = squares[move.getToY()][move.getToX()];
        from.setBorder(GREEN_BORDER);
        to.setBorder(GREEN_BORDER);
    }

    public Optional<File> saveGameDialog() {
        final JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.of(fileChooser.getSelectedFile());
        }
        return Optional.empty();
    }

    public Optional<File> loadGameDialog() {
        final JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return Optional.of(fileChooser.getSelectedFile());
        }
        return Optional.empty();
    }

    public PrepareGameBoard prepareGameDialog(CreateOpponent CreateOpponent, boolean exitOnCancel) {

        JLabel colorLabel = new JLabel("Your color");
        setBoldAndBorder(colorLabel);
        JRadioButton whiteRadioButton = new JRadioButton("White", true);
        JRadioButton blackRadioButton = new JRadioButton("Black", false);
        ButtonGroup colorButtonGroup = new ButtonGroup();
        colorButtonGroup.add(whiteRadioButton);
        colorButtonGroup.add(blackRadioButton);


        final JComponent[] inputs = new JComponent[]{
                colorLabel,
                whiteRadioButton,
                blackRadioButton,
                new JSeparator(),

        };

        int result = JOptionPane
                .showConfirmDialog(this, inputs, "Game setup", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            if (exitOnCancel) {
                exit();
            } else {
                return null;
            }
        }

        Player whitePlayer;
        Player blackPlayer;

        Player human = new Human("Player");
        Player bot;
//
        bot = CreateOpponent.getBot();


        if (whiteRadioButton.isSelected()) {
            whitePlayer = human;
            blackPlayer = bot;
        } else {
            whitePlayer = bot;
            blackPlayer = human;
        }

        return new PrepareGameBoard(whitePlayer, blackPlayer);
    }

    public Piece promotionDialog(PieceColor color) {
        JLabel funLabel = new JLabel("Wow! Your pawn jus reached the end of the world!\n");
        JLabel promoteLabel = new JLabel("Promote pawn to");
        setBoldAndBorder(promoteLabel);
        JRadioButton queenRadioButton = new JRadioButton("♕ Queen", true);
        JRadioButton rookRadioButton = new JRadioButton("♖ Rook", false);
        JRadioButton bishopRadioButton = new JRadioButton("♗ Bishop", false);
        JRadioButton knightRadioButton = new JRadioButton("♘ Knight", false);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(queenRadioButton);
        buttonGroup.add(rookRadioButton);
        buttonGroup.add(bishopRadioButton);
        buttonGroup.add(knightRadioButton);

        final JComponent[] inputs = new JComponent[]{
                funLabel,
                new JSeparator(),
                promoteLabel,
                queenRadioButton,
                rookRadioButton,
                bishopRadioButton,
                knightRadioButton
        };

        JOptionPane
                .showConfirmDialog(this, inputs, "Promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);

        Piece piece;
        if (rookRadioButton.isSelected()) {
            piece = new Rook(color);
        } else if (bishopRadioButton.isSelected()) {
            piece = new Bishop(color);
        } else if (knightRadioButton.isSelected()) {
            piece = new Knight(color);
        } else {
            piece = new Queen(color);
        }
        return piece;
    }

    private void setBoldAndBorder(JLabel label) {
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
        label.setBorder(new EmptyBorder(10, 0, 5, 0));
    }

    public void popupInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void popupError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    private void setMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem itemExit = new JMenuItem("Exit");
        fileMenu.add(itemExit);
        itemExit.addActionListener(actionEvent -> exit());

        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        this.itemNew = new JMenuItem("New");
        gameMenu.add(itemNew);
        gameMenu.addSeparator();

        this.itemUndoMove = new JMenuItem("Undo move");
        gameMenu.add(itemUndoMove);
        gameMenu.addSeparator();

        this.itemProposeDraw = new JMenuItem("Propose draw");
        gameMenu.add(itemProposeDraw);

        JMenu debugMenu = new JMenu("Debug");
        menuBar.add(debugMenu);

        this.itemPrintToConsole = new JMenuItem("Print to console");
        debugMenu.add(itemPrintToConsole);

        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        JMenuItem itemAbout = new JMenuItem("About");
        helpMenu.add(itemAbout);
        itemAbout.addActionListener(actionEvent -> showAboutDialog());
    }

    private void addFilesLabels(java.util.List<Component> components) {
        char a = 'a';
        char h = 'h';
        components.add(new Label(""));
        Stream<Integer> intStream = IntStream.range(a, h + 1).boxed();
        intStream.forEach(i -> components.add(new Label(Character.toString((char) (int) i))));
        components.add(new Label(""));
    }

    private Label getRankLabel(int i) {
        return new Label(String.valueOf(i + 1));
    }

    private BoardSquares swapBackground(BoardSquares background) {
        if (background == BoardSquares.DARK) {
            return BoardSquares.LIGHT;
        } else {
            return BoardSquares.DARK;
        }
    }

    private BoardSquares getFirstSquareBackground(int i) {
        BoardSquares background;
        if (i % 2 != 0) {
            background = BoardSquares.LIGHT;
        } else {
            background = BoardSquares.DARK;
        }
        return background;
    }


    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "My chess game - version " + reader.getVersion()
                        + " \n"
                        + "Created by Mariusz Bolek\n",
                "About ChessGameProject", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exit() {
        System.exit(0);
    }
}

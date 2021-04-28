package chess.gui.view;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.MoveFactory;
import chess.engine.move.MoveTransition;
import chess.engine.pieces.Piece;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.Collection;

public class BoardGUI extends Application{
    private int firstLocation = -1;
    private int secondLocation = -1;
    private Tile sourceTile = null;
    private Piece movedPiece = null;
    private StackPane stackPane;
    private String string = "";
    private Text text = new Text();
    private Stage primaryStage;
    private Board[] boards = new Board[1];
    private TilePane tiles = new TilePane();
    private TilePane pieces = new TilePane();
    private int checkLocation;
    private Collection<Move> legalMoves;
    private BorderPane borderPane;
    private Button newGameButton;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chess Game");
        Board board = Board.createStartingGameBoard();
        boards[0] = board;
        tiles = drawBoard(board);
        pieces = addPieces(board);
        stackPane = new StackPane();
        stackPane.getChildren().addAll(tiles, pieces);
        borderPane = new BorderPane();
        borderPane.setCenter(stackPane);
        text.setText(string);
        borderPane.setTop(text);
        Scene scene = newScene(borderPane);

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                int col = (int) e.getSceneX() / 100;
                int row = (int) e.getSceneY() / 100;
                int location = (row * 8) + col;
                Board lastBoard = boards[0];
                if (e.getButton() == MouseButton.PRIMARY) {
                    if (sourceTile == null) {
                        firstLocation = location;
                        sourceTile = lastBoard.getTile(firstLocation);
                        if (sourceTile.isTileOccupied()) {
                            if(sourceTile.getPiece().getAlliance() == lastBoard.getCurrentPlayer().getAlliance()) {
                                showLegalMoves(firstLocation, lastBoard);
                                highlightSelectedTile(firstLocation);
                                string = "First " + firstLocation;
                            } else {
                                string = "That's the other player's piece";
                                highlightSelectedTile(firstLocation);
                            }
                        } else {
                            sourceTile = null;
                        }
                    } else {
                        secondLocation = location;
                        if(lastBoard.getTile(secondLocation).isTileOccupied()) {
                            if(lastBoard.getTile(secondLocation).getPiece().getAlliance() == lastBoard.getCurrentPlayer().getAlliance()) {
                                sourceTile = lastBoard.getTile(secondLocation);
                                removePieceHighlight(firstLocation);
                                removeLegalMoves();
                                firstLocation = secondLocation;
                                secondLocation = -1;
                                highlightSelectedTile(firstLocation);
                                showLegalMoves(firstLocation, lastBoard);
                                string = "First " + firstLocation;
                            } else {
                                tryMove(lastBoard);
                            }
                        } else {
                            tryMove(lastBoard);
                        }
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    if(firstLocation != -1) {
                        removePieceHighlight(firstLocation);
                    }
                    firstLocation = -1;
                    secondLocation = -1;
                    sourceTile = null;
                    string = "";
                    removeLegalMoves();
                }

                text.setText(string);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void tryMove(Board lastBoard) {
        string = "Second: " + secondLocation;
        Move move = MoveFactory.createMove(lastBoard, firstLocation, secondLocation);
        MoveTransition transition = lastBoard.getCurrentPlayer().makeMove(move);
        if (transition.getMoveStatus().isDone()) {
            Board nextBoard = transition.getTransitionBoard();
            try {
                movePiece(nextBoard, move);
                kingInCheck(nextBoard);
                removeLegalMoves();
                removePieceHighlight(firstLocation);
                boards[0] = nextBoard;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        } else if(transition.getMoveStatus().leavesInCheck()) {
            string = "Move leaves player in check";
        } else {
            string = "Move is illegal";
        }
        removePieceHighlight(firstLocation);
        firstLocation = -1;
        secondLocation = -1;
        sourceTile = null;
        removeLegalMoves();
    }

    public Scene newScene(BorderPane borderPane) {
        return new Scene(borderPane);
    }

    public void movePiece(Board board, Move move) throws FileNotFoundException {
        pieces.getChildren().remove(move.getDestinationCoordinate());
        Image image;
        image = board.getTile(move.getDestinationCoordinate()).getPiece().getPieceType().getImage(board.getTile(move.getDestinationCoordinate()).getPiece().getAlliance());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        pieces.getChildren().add(move.getDestinationCoordinate(), imageView);

        pieces.getChildren().remove(move.getCurrentCoordinate());
        Rectangle rectangle = new Rectangle();
        rectangle.setVisible(false);
        pieces.getChildren().add(move.getCurrentCoordinate(), rectangle);

        if(move.isCastlingMove()) {
            pieces.getChildren().remove(move.getCastleRookStart());
            Rectangle rectangle1 = new Rectangle();
            rectangle1.setVisible(false);
            pieces.getChildren().add(move.getCastleRookStart(), rectangle1);
            pieces.getChildren().remove(move.getCastleRookDestination());
            Image rookImage = board.getTile(move.getCastleRookDestination()).getPiece().getPieceType().getImage(board.getTile(move.getCastleRookDestination()).getPiece().getAlliance());
            ImageView rookView = new ImageView(rookImage);
            rookView.setFitWidth(100);
            rookView.setFitHeight(100);
            pieces.getChildren().add(move.getCastleRookDestination(), rookView);
        }
    }

    public void highlightSelectedTile(int location) {
        Rectangle rectangle = (Rectangle)tiles.getChildren().get(location);
        rectangle.setFill(Color.YELLOW);
    }

    public void removePieceHighlight(int location) {
        Rectangle rectangle = (Rectangle)tiles.getChildren().get(location);
        rectangle.setFill(BoardUtils.TILE_COLORS[location]);
    }

    public void showLegalMoves(int firstLocation, Board board) {
        if(board.getTile(firstLocation).getPiece().getAlliance() == board.getCurrentPlayer().getAlliance()) {
            legalMoves = board.getTile(firstLocation).getPiece().calculateLegalMoves(board);
            for (Move move : legalMoves) {
                int destination = move.getDestinationCoordinate();
                Rectangle rectangle = (Rectangle) tiles.getChildren().get(destination);
                Glow glow = new Glow();
                glow.setLevel(1);
                rectangle.setEffect(glow);
            }
        }
    }

    public void removeLegalMoves() {
        for(Move move : legalMoves) {
            int destination = move.getDestinationCoordinate();
            Rectangle rectangle = (Rectangle)tiles.getChildren().get(destination);
            Glow glow = new Glow();
            glow.setLevel(0);
            rectangle.setEffect(glow);
        }
    }

    public void kingInCheck(Board board) {
        Rectangle rectangle;
        if(board.playerInCheck(board.getBlackPlayer()) || board.playerInCheck(board.getWhitePlayer())) {
            checkLocation = board.getPlayerInCheck().getPlayerKing().getPiecePosition();
            rectangle = (Rectangle)tiles.getChildren().get(checkLocation);
            rectangle.setFill(Color.RED);
        } else {
            rectangle = (Rectangle)tiles.getChildren().get(checkLocation);
            rectangle.setFill(BoardUtils.TILE_COLORS[checkLocation]);
        }
    }

    public TilePane drawBoard(Board board) {
        TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(8);
        tilePane.setPrefRows(8);
        tilePane.setHgap(0);
        tilePane.setVgap(0);
        tilePane.setPrefTileHeight(100);
        tilePane.setPrefTileWidth(100);

        for(int location = 0; location < 64; location++) {
            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(100);
            rectangle.setHeight(100);
            rectangle.setFill(BoardUtils.TILE_COLORS[location]);
            tilePane.getChildren().add(rectangle);
        }
        return tilePane;
    }

    public TilePane addPieces(Board board) throws FileNotFoundException {
        TilePane tilePane = new TilePane();
        tilePane.setPrefColumns(8);
        tilePane.setPrefRows(8);
        tilePane.setHgap(0);
        tilePane.setVgap(0);
        tilePane.setPrefTileHeight(100);
        tilePane.setPrefTileWidth(100);

        for(int location = 0; location < 64; location++) {
            if(board.getTile(location).isTileOccupied()) {
                Image image;
                image = board.getTile(location).getPiece().getPieceType().getImage(board.getTile(location).getPiece().getAlliance());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                tilePane.getChildren().add(imageView);
            } else {
                Rectangle rectangle = new Rectangle();
                rectangle.setVisible(false);
                tilePane.getChildren().add(rectangle);
            }
        }
        return tilePane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
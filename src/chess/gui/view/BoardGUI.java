package chess.gui.view;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Tile;
import chess.engine.move.Move;
import chess.engine.move.Move.MoveFactory;
import chess.engine.move.MoveTransition;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Piece.PieceType;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
    private PieceType promotionPieceType = null;
    private boolean isInPromotionSequence = false;

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

        pieces.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                int col = (int) e.getX() / 100;
                int row = (int) e.getY() / 100;
                int location = (row * 8) + col;
                Board lastBoard = boards[0];
                if (e.getButton() == MouseButton.PRIMARY) {
                    try {
                        leftClickEvent(lastBoard, location);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    rightClickEvent();
                }

                text.setText(string);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void leftClickEvent(Board lastBoard, int location) throws FileNotFoundException {
        if (sourceTile == null) {
            firstLocation = location;
            firstLeftClickEvent(lastBoard);
        }
        else {
            secondLocation = location;
            secondLeftClickEvent(lastBoard);
        }
    }

    private void firstLeftClickEvent(Board lastBoard) {
        sourceTile = lastBoard.getTile(firstLocation);
        if (sourceTile.isTileOccupied()) {
            if(sourceTile.getPiece().getAlliance() == lastBoard.getCurrentPlayer().getAlliance()) {
                showLegalMoves(firstLocation, lastBoard);
                highlightSelectedTile(firstLocation);
                string = "First " + firstLocation;
                movedPiece = sourceTile.getPiece();
            }
            else {
                string = "That's the other player's piece";
                highlightSelectedTile(firstLocation);
            }
        }
        else {
            sourceTile = null;
        }
    }

    private void secondLeftClickEvent(Board lastBoard) throws FileNotFoundException {
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
                if(movedPiece.getPieceType() == PieceType.PAWN) {
                    if((BoardUtils.FIRST_ROW[secondLocation] && movedPiece.getAlliance() == Alliance.WHITE) ||
                            (BoardUtils.EIGHTH_ROW[secondLocation] && movedPiece.getAlliance() == Alliance.BLACK)) {
                        this.isInPromotionSequence = true;
                        showPromotionPieceTypes(lastBoard, movedPiece.getAlliance());
                    } else {
                        Move move = MoveFactory.createMove(lastBoard, firstLocation, secondLocation);
                        tryMove(lastBoard, move);
                    }
                } else {
                    Move move = MoveFactory.createMove(lastBoard, firstLocation, secondLocation);
                    tryMove(lastBoard, move);
                }
            }
        } else {
            if(movedPiece.getPieceType() == PieceType.PAWN) {
                if((BoardUtils.FIRST_ROW[secondLocation] && movedPiece.getAlliance() == Alliance.WHITE) ||
                        (BoardUtils.EIGHTH_ROW[secondLocation] && movedPiece.getAlliance() == Alliance.BLACK)) {
                    this.isInPromotionSequence = true;
                    showPromotionPieceTypes(lastBoard, movedPiece.getAlliance());
                } else {
                    Move move = MoveFactory.createMove(lastBoard, firstLocation, secondLocation);
                    tryMove(lastBoard, move);
                }
            } else {
                Move move = MoveFactory.createMove(lastBoard, firstLocation, secondLocation);
                tryMove(lastBoard, move);
            }
        }
    }

    private void showPromotionPieceTypes(Board lastBoard, Alliance alliance) throws FileNotFoundException {
        PieceType[] pieceTypes = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        VBox vBox1 = new VBox();
        Text text1 = new Text();
        text1.setText("Choose piece");
        StackPane stackPane1 = new StackPane();
        VBox backGround = new VBox();
        for(PieceType pieceType : pieceTypes) {
            Image image = pieceType.getImage(alliance);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            vBox1.getChildren().add(imageView);
        }
        for(int i = 0; i < 5; i++) {
            Rectangle rectangle = new Rectangle();
            rectangle.setHeight(100);
            rectangle.setWidth(100);
            rectangle.setFill(Color.WHITE);
            backGround.getChildren().add(rectangle);
        }
        stackPane1.getChildren().add(backGround);
        stackPane1.getChildren().add(vBox1);
        vBox1.setOnMouseClicked(e -> {
            int row = (int)e.getY()/100;
            int col = (int)e.getX()/100;
            if(col == 0 && row < 5) {
                if (row == 0)
                    promotionPieceType = PieceType.QUEEN;
                if (row == 1)
                    promotionPieceType = PieceType.ROOK;
                if (row == 2)
                    promotionPieceType = PieceType.BISHOP;
                if (row == 3)
                    promotionPieceType = PieceType.KNIGHT;
                if (row == 4) {
                    promotionPieceType = null;
                }
                stackPane.getChildren().remove(2);
                Move move = MoveFactory.createPromotionMove(lastBoard, firstLocation, secondLocation, promotionPieceType);
                tryMove(lastBoard, move);
            }
        });
        stackPane.getChildren().add(stackPane1);
    }

    private void tryMove(Board lastBoard, Move move) {
        string = "Second: " + secondLocation;
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

    private void rightClickEvent() {
        if(firstLocation != -1) {
            removePieceHighlight(firstLocation);
        }
        firstLocation = -1;
        secondLocation = -1;
        sourceTile = null;
        string = "";
        removeLegalMoves();
    }

    private Scene newScene(BorderPane borderPane) {
        return new Scene(borderPane);
    }

    private void movePiece(Board board, Move move) throws FileNotFoundException {
        Image image;
        image = board.getTile(move.getDestinationCoordinate()).getPiece().getPieceType().getImage(board.getTile(move.getDestinationCoordinate()).getPiece().getAlliance());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        pieces.getChildren().set(move.getDestinationCoordinate(), imageView);

        Rectangle rectangle = new Rectangle();
        rectangle.setVisible(false);
        pieces.getChildren().set(move.getCurrentCoordinate(), rectangle);

        if(move.isEnPassantMove()) {
            Rectangle rectangle1 = new Rectangle();
            rectangle.setVisible(false);
            pieces.getChildren().set(move.getAttackedPiece().getPiecePosition(), rectangle1);
        }

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

    private void highlightSelectedTile(int location) {
        Rectangle rectangle = (Rectangle)tiles.getChildren().get(location);
        rectangle.setFill(Color.YELLOW);
    }

    private void removePieceHighlight(int location) {
        Rectangle rectangle = (Rectangle)tiles.getChildren().get(location);
        rectangle.setFill(BoardUtils.TILE_COLORS[location]);
    }

    private void showLegalMoves(int firstLocation, Board board) {
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

    private void removeLegalMoves() {
        for(Move move : legalMoves) {
            int destination = move.getDestinationCoordinate();
            Rectangle rectangle = (Rectangle)tiles.getChildren().get(destination);
            Glow glow = new Glow();
            glow.setLevel(0);
            rectangle.setEffect(glow);
        }
    }

    private void kingInCheck(Board board) {
        Rectangle rectangle;
        if(board.playerInCheck(board.getBlackPlayer()) || board.playerInCheck(board.getWhitePlayer())) {
            if(board.getCurrentPlayer().isInCheckMate()) {
                System.out.println("Checkmate");
            }
            if(board.getCurrentPlayer().isInCheck()) {
                System.out.println("Check");
            }
            checkLocation = board.getPlayerInCheck().getPlayerKing().getPiecePosition();
            rectangle = (Rectangle)tiles.getChildren().get(checkLocation);
            rectangle.setFill(Color.RED);
        } else {
            if(board.getCurrentPlayer().isInStaleMate()) {
                System.out.println("Stalemate");
            }
            rectangle = (Rectangle)tiles.getChildren().get(checkLocation);
            rectangle.setFill(BoardUtils.TILE_COLORS[checkLocation]);
        }
    }

    private TilePane drawBoard(Board board) {
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

    private TilePane addPieces(Board board) throws FileNotFoundException {
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
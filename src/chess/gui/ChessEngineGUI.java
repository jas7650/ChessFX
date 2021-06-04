package chess.gui;

import chess.engine.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class ChessEngineGUI extends Application {
    private static Stage primaryStage;
    private Stage gameStage;
    private BoardGUI currentBoardGUI;
    private BoardGUI nextBoardGUI;

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        primaryStage = stage;
        primaryStage.setTitle("ChessGameFX");
        HomeScreenGUI homeScreenGUI = new HomeScreenGUI(this);
        primaryStage.setScene(homeScreenGUI.getHomeScreenScene());
        primaryStage.show();
        this.nextBoardGUI = new BoardGUI(new Game());
        this.gameStage = new Stage();
        gameStage.setScene(nextBoardGUI.createBoardScene());
    }

    public void newGame() {
        BoardGUI newBoardGUI = new BoardGUI(new Game());
        this.currentBoardGUI = this.nextBoardGUI;
        this.nextBoardGUI = newBoardGUI;
        primaryStage.hide();
        gameStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

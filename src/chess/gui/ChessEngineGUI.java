package chess.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class ChessEngineGUI extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("ChessGameFX");
        HomeScreenGUI homeScreenGUI = new HomeScreenGUI();
        primaryStage.setScene(homeScreenGUI.getHomeScreenScene());
        primaryStage.show();
    }

    public static void changeToGameScreen() {
        BoardGUI boardGUI = new BoardGUI();
        try {
            primaryStage.setScene(boardGUI.createBoardScene());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

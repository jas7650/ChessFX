package chess.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HomeScreenGUI {
    private Scene homeScreenScene;
    private BorderPane borderPane;
    private ChessEngineGUI chessEngineGUI;

    public HomeScreenGUI(ChessEngineGUI chessEngineGUI) {
        this.chessEngineGUI = chessEngineGUI;
        createHomeScreenScene();
    }

    private void createHomeScreenScene() {
        borderPane = new BorderPane();
        Text text = new Text();
        text.setText("Justin's Chess Engine");
        Font font = new Font("Times New Roman", 30);
        text.setFont(font);
        borderPane.setBackground(new Background(new BackgroundFill(Color.TAN, new CornerRadii(0), Insets.EMPTY)));
        borderPane.setTop(text);
        VBox buttons = createHomeScreenButtons();
        borderPane.setRight(buttons);
        BorderPane.setAlignment(text, Pos.CENTER);
        this.borderPane.setMinHeight(800);
        this.borderPane.setMinWidth(800);
        this.homeScreenScene = new Scene(borderPane);
    }

    private VBox createHomeScreenButtons() {
        VBox vBox = new VBox();
        Button newGame = new Button();
        newGame.setText("New Game");
        newGame.setOnMouseClicked(e -> {
            this.chessEngineGUI.newGame();
        });
        vBox.getChildren().add(newGame);
        return vBox;
    }

    public Scene getHomeScreenScene() {
        return this.homeScreenScene;
    }
}

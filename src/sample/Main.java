package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Order Processing");
        primaryStage.setScene(new Scene(root, 1080, 560));
        primaryStage.setMinWidth(1015);
        primaryStage.setMinHeight(480);
        primaryStage.show();
    }


}

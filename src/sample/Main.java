package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("3D Linear Transform Visualizer");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}

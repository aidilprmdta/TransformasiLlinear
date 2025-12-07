package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Coba semua kemungkinan path
        URL fxmlResource = null;

        // Urutan prioritas pencarian
        String[] possiblePaths = {
                "layout.fxml",                    // Relative path
                "/layout.fxml",                   // Absolute dari root
                "/sample/layout.fxml",            // Absolute dengan package
                "sample/layout.fxml"              // Relative dengan package
        };

        for (String path : possiblePaths) {
            fxmlResource = getClass().getResource(path);
            if (fxmlResource != null) {
                System.out.println("✓ Found FXML at: " + path);
                break;
            }
        }

        if (fxmlResource == null) {
            throw new IllegalStateException("""
                File FXML tidak ditemukan! Pastikan:
                1. File layout.fxml ada di package 'sample'
                2. Folder src sudah di-mark sebagai Sources Root
                3. Build project (Build → Rebuild Project)
                4. Cek di out/production/<project>/sample/ apakah ada layout.fxml
                """);
        }

        FXMLLoader loader = new FXMLLoader(fxmlResource);
        Scene scene = new Scene(loader.load());
        stage.setTitle("3D Linear Transform Visualizer");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
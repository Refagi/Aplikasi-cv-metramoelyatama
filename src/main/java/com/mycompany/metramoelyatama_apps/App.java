package com.mycompany.metramoelyatama_apps;

import controller.LoadingController;
import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.concurrent.Task;
import java.io.IOException;

public class App extends Application {

    private Stage loadingStage;
    private Stage authStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.authStage = stage;
        
        FXMLLoader loader = showLoadingScreen();        
        LoadingController controller = loader.getController();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Mencoba koneksi ke database...");
                    utils.DBConnection.getInstance().getConnection();
                } catch (Exception e) {
                    System.out.println("Koneksi gagal!");
                    e.printStackTrace();
                }
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(60);
                }
                return null;
            }
        };

        controller.progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            loadingStage.close();
            showLoginScreen();
        });

        new Thread(task).start();
    }

    private FXMLLoader showLoadingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Loading.fxml")
            );

            Scene scene = new Scene(loader.load());

            loadingStage = new Stage();
            loadingStage.initStyle(StageStyle.UNDECORATED);
            loadingStage.setScene(scene);
            loadingStage.centerOnScreen();
            loadingStage.show();
            
            return loader;

        } catch (IOException e) {
            System.out.println("Gagal memuat Loading.fxml");
            e.printStackTrace();
            return null;
        }
    }
    
    
    private void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Login.fxml")
            );
            Scene scene = new Scene(loader.load());
            
            scene.getStylesheets().add(
                getClass().getResource("login.css")
                          .toExternalForm()
            );
            
            authStage.setScene(scene);
            authStage.setTitle("Login - CV Metramoelyatama");
            authStage.setResizable(false);
            authStage.centerOnScreen();
            authStage.show();
            
        } catch (IOException e) {
            System.out.println("Gagal memuat Login.fxml");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
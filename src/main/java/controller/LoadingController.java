/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class LoadingController {

    @FXML
    public ProgressBar progressBar;
    
        @FXML
    public Label loadingText;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        startLoading();
    }

    private void startLoading() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                
                try {
                    System.out.println("Mencoba koneksi ke database...");
                    utils.DBConnection.getInstance().getConnection();
                    System.out.println("Koneksi berhasil!");
                } catch (Exception e) {
                    System.out.println("Koneksi gagal!");
                    e.printStackTrace();
                }

                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(30);
                }

                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            System.out.println("Loading selesai, membuka login screen...");
            stage.close();
            showLoginScreen();
        });

        task.setOnFailed(e -> {
            System.err.println("Loading gagal!");
            e.getSource().getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mycompany/metramoelyatama_apps/Login.fxml")
            );
            
            Scene scene = new Scene(loader.load(), 800, 600);

            scene.getStylesheets().add(
                getClass().getResource("/com/mycompany/metramoelyatama_apps/css/Login.css")
                          .toExternalForm()
            );

            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Login - CV Metramoelyatama");
            loginStage.setResizable(false);
            loginStage.centerOnScreen();
            loginStage.show();

            System.out.println("Login screen berhasil ditampilkan!");

        } catch (Exception e) {
            System.err.println("Gagal membuka Login screen!");
            e.printStackTrace();
        }
    }
}
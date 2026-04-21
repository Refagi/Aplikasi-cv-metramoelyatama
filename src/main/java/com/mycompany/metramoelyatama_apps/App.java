//package com.mycompany.metramoelyatama_apps;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import javafx.concurrent.Task;
//import java.io.IOException;
//
//public class App extends Application {
//
//    private Stage loadingStage;
//
//    @Override
//    public void start(Stage stage) throws IOException {
//
////        showLoadingScreen();
//        FXMLLoader loader = showLoadingScreen();
//        LoadingController controller = loader.getController(); // Ambil controller dari Loading.fxml
////        myProgressBar.progressProperty().bind(task.progressProperty()); // Sambungkan!
//
//        Task<Void> task = new Task<>() {
//            @Override
//            protected Void call() throws Exception {
//                for (int i = 0; i <= 100; i++) {
//                    updateProgress(i, 100);
//                    Thread.sleep(60);
//                }
//                return null;
//            }
//        };
//
//        task.setOnSucceeded(e -> {
//            loadingStage.close();
//
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
//                Scene scene = new Scene(loader.load());
//
//                stage.setScene(scene);
//                stage.setTitle("CV Metramoelyatama");
//                stage.setResizable(false);
//                stage.centerOnScreen();
//                stage.show();
//
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        });
//
//        new Thread(task).start();
//    }
//
//    private void showLoadingScreen() {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                getClass().getResource("/com/mycompany/metramoelyatama_apps/Loading.fxml")
//            );
//
//            Scene scene = new Scene(loader.load());
//
//            loadingStage = new Stage();
//            loadingStage.initStyle(StageStyle.UNDECORATED);
//            loadingStage.setScene(scene);
//            loadingStage.centerOnScreen();
//            loadingStage.show();
//            
//            return loader;
//
//        } catch (IOException e) {
//            System.out.println("Gagal memuat Loading.fxml");
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}


package com.mycompany.metramoelyatama_apps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.concurrent.Task;
import java.io.IOException;

public class App extends Application {

    private Stage loadingStage;

    @Override
    public void start(Stage stage) throws IOException {
        
        FXMLLoader loader = showLoadingScreen();        
        LoadingController controller = loader.getController();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 50; i++) {
                    updateProgress(i, 50);
                    Thread.sleep(60);
                }
                return null;
            }
        };

        controller.progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            loadingStage.close();

            try {
                FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Scene scene = new Scene(mainLoader.load());

                stage.setScene(scene);
                stage.setTitle("CV Metramoelyatama");
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        new Thread(task).start();
    }

    private FXMLLoader showLoadingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mycompany/metramoelyatama_apps/Loading.fxml")
            );

            Scene scene = new Scene(loader.load());

            loadingStage = new Stage();
            loadingStage.initStyle(StageStyle.UNDECORATED);
            loadingStage.setScene(scene);
            loadingStage.centerOnScreen();
            loadingStage.show();
            
            return loader;

        } catch (IOException e) {
            System.out.println("❌ Gagal memuat Loading.fxml");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
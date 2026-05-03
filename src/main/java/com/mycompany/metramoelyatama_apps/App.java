package com.mycompany.metramoelyatama_apps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/mycompany/metramoelyatama_apps/Loading.fxml")
        );

        Scene scene = new Scene(loader.load());

        Stage loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.UNDECORATED);
        loadingStage.setScene(scene);
        loadingStage.centerOnScreen();

        controller.LoadingController controller = loader.getController();
        controller.setStage(loadingStage);

        loadingStage.show();
        
        System.out.println("✅ Loading screen berhasil ditampilkan!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
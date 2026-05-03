module com.mycompany.metramoelyatama_apps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    exports com.mycompany.metramoelyatama_apps;
    exports controller;
        exports models;

    opens com.mycompany.metramoelyatama_apps to javafx.fxml;
    opens controller to javafx.fxml;
    opens models to javafx.base, javafx.fxml;
}
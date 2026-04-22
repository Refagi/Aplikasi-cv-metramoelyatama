module com.mycompany.metramoelyatama_apps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens com.mycompany.metramoelyatama_apps to javafx.fxml;
    exports com.mycompany.metramoelyatama_apps;
}

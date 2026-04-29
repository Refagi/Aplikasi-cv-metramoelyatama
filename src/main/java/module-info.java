module com.mycompany.metramoelyatama_apps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens controller to javafx.fxml;
    opens utils to javafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.mycompany.metramoelyatama_apps to javafx.fxml;
    exports com.mycompany.metramoelyatama_apps;
}

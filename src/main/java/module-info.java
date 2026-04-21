module com.mycompany.metramoelyatama_apps {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.metramoelyatama_apps to javafx.fxml;
    exports com.mycompany.metramoelyatama_apps;
}

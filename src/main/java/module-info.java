module com.mycompany.metramoelyatama_apps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires jasperreports;
    requires jfreechart;
    requires jcommon;
    requires commons.logging;
    requires commons.digester;
    requires org.apache.commons.collections4;
    requires commons.beanutils;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    exports com.mycompany.metramoelyatama_apps;
    exports controller;
    exports controller.laporan;
    exports models;
    opens com.mycompany.metramoelyatama_apps to javafx.fxml;
    opens controller to javafx.fxml;
    opens controller.laporan to javafx.fxml;
    opens models to javafx.base, javafx.fxml;
    opens models.laporan to javafx.base, javafx.fxml, commons.beanutils;
    exports models.laporan;
}
package controller;

import java.io.IOException;
import java.net.URL;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;


public class DashboardController implements Initializable {

    @FXML private StackPane mainForm;

    @FXML private Label lblPageTitle;
    @FXML private Label lblPageTitle1;
    @FXML private Label lblPageTitle11;
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

    @FXML private Button menuHome;
    @FXML private Button menuMaster;
    @FXML private Button menuTransaksi;
    @FXML private Button menuInventory;
    @FXML private Button menuLaporan;

    @FXML private VBox submenuMaster;
    @FXML private VBox submenuTransaksi;
    @FXML private VBox submenuInventory;
    @FXML private VBox submenuLaporan;

    @FXML private FontIcon iconMaster;
    @FXML private FontIcon iconTransaksi;
    @FXML private FontIcon iconLaporan;

    @FXML private Button menuClients;
    @FXML private Button menuKaryawan;
    @FXML private Button menuSupplier;
    @FXML private Button menuLayanan;
    @FXML private Button menuBarang;

    @FXML private Button menuOrder;
    @FXML private Button menuPengerjaan;
    @FXML private Button menuInvoice;
    @FXML private Button menuPembelian;
    @FXML private Button menuPenjualan;

    @FXML private Button menuLaporanOrder;
    @FXML private Button menuLaporanPengerjaan;
    @FXML private Button menuLaporanInvoice;
    @FXML private Button menuLaporanPembelian;
    @FXML private Button menuLaporanKeuntungan;

    @FXML private Button btnLogout;

    private String userId;
    private String userName;
    private String userRole;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPage("/com/mycompany/metramoelyatama_apps/Master/Client.fxml");

        menuClients.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Master/Client.fxml");
            lblPageTitle.setText("Data Client");
            setActiveSubmenu(menuClients);
        });

        menuKaryawan.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Master/Karyawan.fxml");
            lblPageTitle.setText("Data Karyawan");
            setActiveSubmenu(menuKaryawan);
        });

        menuSupplier.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Master/Supplier.fxml");
            lblPageTitle.setText("Data Supplier");
            setActiveSubmenu(menuSupplier);
        });


        menuLayanan.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Master/JenisLayanan.fxml");
            lblPageTitle.setText("Data Jenis Layanan");
            setActiveSubmenu(menuLayanan);
        });
        
        menuBarang.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Master/Barang.fxml");
            lblPageTitle.setText("Data Barang");
            setActiveSubmenu(menuBarang);
        });
        
        menuOrder.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Transaksi/Order.fxml");
            lblPageTitle.setText("Data Orders");
            setActiveSubmenu(menuOrder);
        });
        
        menuPengerjaan.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Transaksi/Pengerjaan.fxml");
            lblPageTitle.setText("Data Pengerjaan");
            setActiveSubmenu(menuPengerjaan);
        });
        
        menuInvoice.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Transaksi/Invoice.fxml");
            lblPageTitle.setText("Data Invoice");
            setActiveSubmenu(menuInvoice);
        });
        
        menuPembelian.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Transaksi/Pembelian.fxml");
            lblPageTitle.setText("Data Pembelian");
            setActiveSubmenu(menuPembelian);
        });
        
        menuLaporanOrder.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Laporan/LaporanOrder.fxml");
            lblPageTitle.setText("Laporan Order");
            setActiveSubmenu(menuLaporanOrder);
        });
        
        menuLaporanPengerjaan.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Laporan/LaporanPengerjaan.fxml");
            lblPageTitle.setText("Laporan Pengerjaan");
            setActiveSubmenu(menuLaporanPengerjaan);
        });
        
        menuLaporanInvoice.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Laporan/LaporanInvoice.fxml");
            lblPageTitle.setText("Laporan Invoice");
            setActiveSubmenu(menuLaporanInvoice);
        });
        
        menuLaporanPembelian.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Laporan/LaporanPembelian.fxml");
            lblPageTitle.setText("Laporan Pembelian");
            setActiveSubmenu(menuLaporanPembelian);
        });
        
        menuLaporanKeuntungan.setOnAction(e -> {
            loadPage("/com/mycompany/metramoelyatama_apps/Laporan/LaporanKeuntungan.fxml");
            lblPageTitle.setText("Laporan Keuntungan");
            setActiveSubmenu(menuLaporanKeuntungan);
        });

    }

    private void loadPage(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            mainForm.getChildren().clear();
            mainForm.getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserData(String userId, String nama, String role) {
        this.userId = userId;
        this.userName = nama;
        this.userRole = role;

        lblUserName.setText(nama);
        lblUserRole.setText(role);

        setupMenuByRole(role);
    }

    private void setupMenuByRole(String role) {
        if ("Karyawan".equals(role)) {
            setNodeVisible(menuKaryawan, false);
            setNodeVisible(menuLaporanKeuntungan, false);
            setNodeVisible(menuInvoice, false);
            setNodeVisible(menuPembelian, false);
        }
    }


    @FXML
    void toggleMaster(ActionEvent event) {
        toggleSubmenu(submenuMaster, iconMaster);
        setActiveMenu(menuMaster);
    }

    @FXML
    void toggleTransaksi(ActionEvent event) {
        toggleSubmenu(submenuTransaksi, iconTransaksi);
        setActiveMenu(menuTransaksi);
    }

    @FXML
    void toggleLaporan(ActionEvent event) {
        toggleSubmenu(submenuLaporan, iconLaporan);
        setActiveMenu(menuLaporan);
    }



//    private void showPlaceholder(String menuName) {
//        showForm(null);
//        lblPageTitle.setText(menuName);
//
//        homeForm.setVisible(true);
//        homeForm.setManaged(true);
//    }

    private void toggleSubmenu(VBox submenu, FontIcon icon) {
        boolean isVisible = submenu.isVisible();

        submenu.setVisible(!isVisible);
        submenu.setManaged(!isVisible);

        rotateIcon(icon, isVisible ? 0 : 180);
    }

    private void rotateIcon(FontIcon icon, double toAngle) {
        if (icon == null) return;

        RotateTransition rotate = new RotateTransition(Duration.millis(200), icon);
        rotate.setToAngle(toAngle);
        rotate.play();
    }


    private void setActiveMenu(Button activeButton) {
        Button[] mainMenus = {menuHome, menuMaster, menuTransaksi, menuLaporan};
        for (Button btn : mainMenus) {
            if (btn != null) btn.getStyleClass().remove("active");
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    private void setActiveSubmenu(Button activeButton) {
        VBox[] allSubmenus = {submenuMaster, submenuTransaksi, submenuLaporan};
        for (VBox submenu : allSubmenus) {
            submenu.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    node.getStyleClass().remove("active");
                }
            });
        }

        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }


    private void setNodeVisible(javafx.scene.Node node, boolean visible) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }


    @FXML
    void handleLogout(ActionEvent event) {
        handleLogout();
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Logout");
        confirm.setHeaderText("Apakah Anda yakin ingin keluar?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DBConnection.getInstance().getConnection();
                String sql = "UPDATE `Users` SET status = 'offline' WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, userId);
                stmt.executeUpdate();
                stmt.close();

                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/mycompany/metramoelyatama_apps/Login.fxml")
                );
                Parent root = loader.load();

                Stage stage = (Stage) btnLogout.getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.centerOnScreen();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

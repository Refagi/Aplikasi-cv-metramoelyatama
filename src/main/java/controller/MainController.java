package controller;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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

public class MainController {

    @FXML private TextField txtSearch;
    @FXML private Label lblUserRole;
    @FXML private Label lblUserName;
    @FXML private Label lblPageTitle;

    // Sidebar Menu - Main
    @FXML private VBox menuContainer;
    @FXML private Button menuHome;
    @FXML private Button menuMaster;
    @FXML private Button menuTransaksi;
    @FXML private Button menuInventory;
    @FXML private Button menuLaporan;
    @FXML private Button btnLogout;

    @FXML private FontIcon iconMaster;
    @FXML private FontIcon iconTransaksi;
    @FXML private FontIcon iconInventory;
    @FXML private FontIcon iconLaporan;

    // Submenu Containers
    @FXML private VBox submenuMaster;
    @FXML private VBox submenuTransaksi;
    @FXML private VBox submenuInventory;
    @FXML private VBox submenuLaporan;

    // Submenu Items - Master
    @FXML private Button menuUsers;
    @FXML private Button menuClients;
    @FXML private Button menuKaryawan;
    @FXML private Button menuSupplier;
    @FXML private Button menuLayanan;

    // Submenu Items - Transaksi
    @FXML private Button menuOrder;
    @FXML private Button menuPengerjaan;
    @FXML private Button menuInvoice;
    @FXML private Button menuPembelian;
    @FXML private Button menuPenjualan;

    // Submenu Items - Inventory
    @FXML private Button menuBarang;
    @FXML private Button menuStokBarang;

    // Submenu Items - Laporan
    @FXML private Button menuLaporanOrder;
    @FXML private Button menuLaporanPengerjaan;
    @FXML private Button menuLaporanInvoice;
    @FXML private Button menuLaporanPembelian;
    @FXML private Button menuLaporanPenjualan;
    @FXML private Button menuLaporanKeuntungan;

    // Content Area
    @FXML private AnchorPane contentArea;

    // User Data
    private String userId;
    private String userName;
    private String userRole;

    @FXML
    private void initialize() {
        // Default: semua submenu collapsed
    }

    public void setUserData(String userId, String nama, String role) {
        this.userId = userId;
        this.userName = nama;
        this.userRole = role;

        lblUserName.setText(nama);
        lblUserRole.setText(role);

        setupMenuByRole(role);
        showDashboard();
    }

    private void setupMenuByRole(String role) {
        switch (role) {
            case "Admin":
                // Admin: full access
                break;

            case "Karyawan":
                // Hide user management
                menuUsers.setVisible(false);
                menuUsers.setManaged(false);
                menuKaryawan.setVisible(false);
                menuKaryawan.setManaged(false);
                menuLaporanKeuntungan.setVisible(false);
                menuLaporanKeuntungan.setManaged(false);
                break;
        }
    }

    @FXML
    private void toggleMaster() {
        toggleSubmenu(submenuMaster, iconMaster);
        setActiveMenu(menuMaster);
    }

    @FXML
    private void toggleTransaksi() {
        toggleSubmenu(submenuTransaksi, iconTransaksi);
        setActiveMenu(menuTransaksi);
    }

    @FXML
    private void toggleInventory() {
        toggleSubmenu(submenuInventory, iconInventory);
        setActiveMenu(menuInventory);
    }

    @FXML
    private void toggleLaporan() {
        toggleSubmenu(submenuLaporan, iconLaporan);
        setActiveMenu(menuLaporan);
    }

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

    @FXML
    private void showDashboard() {
        setActiveMenu(menuHome);
        lblPageTitle.setText("Home Dashboard");
        loadContent("Dashboard.fxml");
    }

    @FXML
    private void showUsers() {
        setActiveSubmenu(menuUsers);
        lblPageTitle.setText("User Management");
        loadContent("UserManagement.fxml");
    }

    @FXML
    private void showClients() {
        setActiveSubmenu(menuClients);
        lblPageTitle.setText("Client Management");
        loadContent("ClientManagement.fxml");
    }

    @FXML
    private void showKaryawan() {
        setActiveSubmenu(menuKaryawan);
        lblPageTitle.setText("Karyawan Management");
        loadContent("KaryawanManagement.fxml");
    }

    @FXML
    private void showSupplier() {
        setActiveSubmenu(menuSupplier);
        lblPageTitle.setText("Supplier Management");
        loadContent("SupplierManagement.fxml");
    }

    @FXML
    private void showLayanan() {
        setActiveSubmenu(menuLayanan);
        lblPageTitle.setText("Jenis Layanan");
        loadContent("LayananManagement.fxml");
    }

    @FXML
    private void showOrder() {
        setActiveSubmenu(menuOrder);
        lblPageTitle.setText("Order Management");
        loadContent("OrderManagement.fxml");
    }

    @FXML
    private void showPengerjaan() {
        setActiveSubmenu(menuPengerjaan);
        lblPageTitle.setText("Pengerjaan");
        loadContent("PengerjaanManagement.fxml");
    }

    @FXML
    private void showInvoice() {
        setActiveSubmenu(menuInvoice);
        lblPageTitle.setText("Invoice");
        loadContent("InvoiceManagement.fxml");
    }

    @FXML
    private void showPembelian() {
        setActiveSubmenu(menuPembelian);
        lblPageTitle.setText("Pembelian");
        loadContent("PembelianManagement.fxml");
    }

    @FXML
    private void showPenjualan() {
        setActiveSubmenu(menuPenjualan);
        lblPageTitle.setText("Penjualan");
        loadContent("PenjualanManagement.fxml");
    }

    @FXML
    private void showBarang() {
        setActiveSubmenu(menuBarang);
        lblPageTitle.setText("Data Barang");
        loadContent("BarangManagement.fxml");
    }

    @FXML
    private void showStokBarang() {
        setActiveSubmenu(menuStokBarang);
        lblPageTitle.setText("Stok Barang");
        loadContent("StokBarangManagement.fxml");
    }

    @FXML
    private void showLaporanOrder() {
        setActiveSubmenu(menuLaporanOrder);
        lblPageTitle.setText("Laporan Order");
        loadContent("LaporanOrder.fxml");
    }

    @FXML
    private void showLaporanPengerjaan() {
        setActiveSubmenu(menuLaporanPengerjaan);
        lblPageTitle.setText("Laporan Pengerjaan");
        loadContent("LaporanPengerjaan.fxml");
    }

    @FXML
    private void showLaporanInvoice() {
        setActiveSubmenu(menuLaporanInvoice);
        lblPageTitle.setText("Laporan Invoice");
        loadContent("LaporanInvoice.fxml");
    }

    @FXML
    private void showLaporanPembelian() {
        setActiveSubmenu(menuLaporanPembelian);
        lblPageTitle.setText("Laporan Pembelian");
        loadContent("LaporanPembelian.fxml");
    }

    @FXML
    private void showLaporanPenjualan() {
        setActiveSubmenu(menuLaporanPenjualan);
        lblPageTitle.setText("Laporan Penjualan");
        loadContent("LaporanPenjualan.fxml");
    }

    @FXML
    private void showLaporanKeuntungan() {
        setActiveSubmenu(menuLaporanKeuntungan);
        lblPageTitle.setText("Laporan Keuntungan");
        loadContent("LaporanKeuntungan.fxml");
    }

    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mycompany/metramoelyatama_apps/" + fxmlFile)
            );
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

        } catch (Exception e) {
            System.err.println("❌ Gagal load: " + fxmlFile);
            e.printStackTrace();

            Label placeholder = new Label("🚧 " + fxmlFile + " belum dibuat");
            placeholder.setStyle("-fx-font-size: 24px; -fx-text-fill: #9C9A92;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(placeholder);
        }
    }

    private void setActiveMenu(Button activeButton) {
        menuHome.getStyleClass().remove("active");
        menuMaster.getStyleClass().remove("active");
        menuTransaksi.getStyleClass().remove("active");
        menuInventory.getStyleClass().remove("active");
        menuLaporan.getStyleClass().remove("active");

        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }

    private void setActiveSubmenu(Button activeButton) {
        VBox[] allSubmenus = {submenuMaster, submenuTransaksi, submenuInventory, submenuLaporan};
        for (VBox submenu : allSubmenus) {
            submenu.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    node.getStyleClass().remove("active");
                }
            });
        }

        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
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
                Scene scene = new Scene(root, 800, 600);
                scene.getStylesheets().add(
                    getClass().getResource("/com/mycompany/metramoelyatama_apps/login.css")
                              .toExternalForm()
                );

                stage.setScene(scene);
                stage.centerOnScreen();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import models.Client;
import utils.DBConnection;

public class ClientController implements Initializable {
        
    //Form Input
    @FXML private TextField     txtnamaClient;
    @FXML private ComboBox<String> cmbJenisClient;
    @FXML private TextField     txtnpwpClient;
    @FXML private TextArea      txtalamatClient;
    @FXML private TextField     txtnoClient;
    @FXML private TextField     txtemailClient;
    @FXML private DatePicker    txttglClient;

    //Button
    @FXML private Button        btnsaveClient;
    @FXML private Button        btnupdateClient;
    @FXML private Button        btndeleteClient;
    @FXML private Button        btnresetClient;

    // Table
    @FXML private TableView<Client>         tblClient;
    @FXML private TableColumn<Client, String> clmidClient;
    @FXML private TableColumn<Client, String> clmnamaClient;
    @FXML private TableColumn<Client, String> clmjenisClient;
    @FXML private TableColumn<Client, String> clmnpwpClient;
    @FXML private TableColumn<Client, String> clmalamatClient;
    @FXML private TableColumn<Client, String> clmnoClient;
    @FXML private TableColumn<Client, String> clmemailClient;
    @FXML private TableColumn<Client, String> clmtglClient;

    // Search
    @FXML private TextField txtcariClient;


    private ObservableList<Client> daftarCLient = FXCollections.observableArrayList();
    private FilteredList<Client>   filterListClient;
    private String                 selectedId  = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableColumns();
        setupTableClick();
        setupSearch();
        loadData();

        btnupdateClient.setDisable(true);
        btndeleteClient.setDisable(true);
    }

    private void setupComboBox() {
        cmbJenisClient.setItems(FXCollections.observableArrayList(
            "Perusahaan", "Perorangan", "Event Organizer"
        ));
    }

    private void setupTableColumns() {
        clmidClient.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmnamaClient.setCellValueFactory(new PropertyValueFactory<>("nama"));
        clmjenisClient.setCellValueFactory(new PropertyValueFactory<>("jenisClient"));
        clmnpwpClient.setCellValueFactory(new PropertyValueFactory<>("npwp"));
        clmalamatClient.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        clmnoClient.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        clmemailClient.setCellValueFactory(new PropertyValueFactory<>("email"));
        clmtglClient.setCellValueFactory(new PropertyValueFactory<>("tglDaftar"));
    }

    private void setupTableClick() {
        tblClient.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                isiDataForm(newVal);
            }
        });
    }

    private void setupSearch() {
        filterListClient = new FilteredList<>(daftarCLient , p -> true);

        txtcariClient.textProperty().addListener((obs, oldVal, newVal) -> {
            filterListClient.setPredicate(client -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String keyword = newVal.toLowerCase();
                return client.getNama().toLowerCase().contains(keyword)
                    || client.getJenisClient().toLowerCase().contains(keyword)
                    || (client.getEmail() != null && client.getEmail().toLowerCase().contains(keyword))
                    || (client.getNoTelp() != null && client.getNoTelp().toLowerCase().contains(keyword));
            });
        });

        tblClient.setItems(filterListClient);
    }

    private void loadData() {
        daftarCLient.clear();
        String sql = "SELECT id, nama, jenis_client, npwp, alamat, no_telp, email, tgl_daftar FROM `Client` ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarCLient.add(new Client(
                    rs.getString("id"),
                    rs.getString("nama"),
                    rs.getString("jenis_client"),
                    rs.getString("npwp"),
                    rs.getString("alamat"),
                    rs.getString("no_telp"),
                    rs.getString("email"),
                    rs.getString("tgl_daftar")
                ));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data: " + e.getMessage());
        }
    }


    private void isiDataForm(Client client) {
        selectedId = client.getId();

        txtnamaClient.setText(client.getNama());
        cmbJenisClient.setValue(client.getJenisClient());
        txtnpwpClient.setText(client.getNpwp() != null ? client.getNpwp() : "");
        txtalamatClient.setText(client.getAlamat() != null ? client.getAlamat() : "");
        txtnoClient.setText(client.getNoTelp() != null ? client.getNoTelp() : "");
        txtemailClient.setText(client.getEmail() != null ? client.getEmail() : "");

        if (client.getTglDaftar() != null && !client.getTglDaftar().isEmpty()) {
            txttglClient.setValue(LocalDate.parse(client.getTglDaftar()));
        } else {
            txttglClient.setValue(null);
        }

        btnsaveClient.setDisable(true);
        btnupdateClient.setDisable(false);
        btndeleteClient.setDisable(false);
    }


    @FXML
    public void btnsaveClient() {
        if (!validateForm()) return;

        String sql = "INSERT INTO `Client` (id, nama, jenis_client, npwp, alamat, no_telp, email, tgl_daftar) VALUES (?,?,?,?,?,?,?,?)";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, txtnamaClient.getText().trim());
            stmt.setString(3, cmbJenisClient.getValue());
            stmt.setString(4, getNullable(txtnpwpClient.getText()));
            stmt.setString(5, getNullable(txtalamatClient.getText()));
            stmt.setString(6, getNullable(txtnoClient.getText()));
            stmt.setString(7, getNullable(txtemailClient.getText()));
            stmt.setString(8, txttglClient.getValue() != null ? txttglClient.getValue().toString() : null);

            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data client berhasil disimpan.");
            resetForm();
            loadData();

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "NPWP sudah terdaftar di sistem.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    @FXML
    public void btnupdateClient() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data client yang ingin diubah.");
            return;
        }
        if (!validateForm()) return;

        String sql = "UPDATE `Client` SET nama=?, jenis_client=?, npwp=?, alamat=?, no_telp=?, email=?, tgl_daftar=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtnamaClient.getText().trim());
            stmt.setString(2, cmbJenisClient.getValue());
            stmt.setString(3, getNullable(txtnpwpClient.getText()));
            stmt.setString(4, getNullable(txtalamatClient.getText()));
            stmt.setString(5, getNullable(txtnoClient.getText()));
            stmt.setString(6, getNullable(txtemailClient.getText()));
            stmt.setString(7, txttglClient.getValue() != null ? txttglClient.getValue().toString() : null);
            stmt.setString(8, selectedId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data client berhasil diperbarui.");
                resetForm();
                loadData();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "NPWP sudah digunakan oleh client lain.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        }
    }

    @FXML
    public void btndeleteClient() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data client yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
            "Konfirmasi Hapus",
            "Apakah Anda yakin ingin menghapus client ini?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM `Client` WHERE id = ?";

            try (Connection conn = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, selectedId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data client berhasil dihapus.");
                    resetForm();
                    loadData();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    @FXML
    public void btnresetClient() {
        resetForm();
    }


    private void resetForm() {
        selectedId = null;

        txtnamaClient.clear();
        cmbJenisClient.setValue(null);
        txtnpwpClient.clear();
        txtalamatClient.clear();
        txtnoClient.clear();
        txtemailClient.clear();
        txttglClient.setValue(null);

        tblClient.getSelectionModel().clearSelection();

        btnsaveClient.setDisable(false);
        btnupdateClient.setDisable(true);
        btndeleteClient.setDisable(true);
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtnamaClient.getText().trim().isEmpty())
            errors.append("• Nama client tidak boleh kosong.\n");

        if (cmbJenisClient.getValue() == null)
            errors.append("• Jenis client harus dipilih.\n");

        String npwp = txtnpwpClient.getText().trim();
        if (!npwp.isEmpty() && !npwp.matches("\\d{2}\\.\\d{3}\\.\\d{3}\\.\\d{1}-\\d{3}\\.\\d{3}")) {
            errors.append("• Format NPWP tidak valid (contoh: 09.254.292.9-407.000).\n");
        }

        String email = txtemailClient.getText().trim();
        if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            errors.append("• Format email tidak valid.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", errors.toString());
            return false;
        }

        return true;
    }

    private String getNullable(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirm(String title, String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(null);
        confirm.setContentText(message);
        return confirm.showAndWait();
    }
}
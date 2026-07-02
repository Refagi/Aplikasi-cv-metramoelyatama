package controller;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import models.Supplier;
import utils.DBConnection;

public class SupplierController implements Initializable {

    @FXML private TextField txtnamaSupplier;
    @FXML private TextField txtemailSupplier;
    @FXML private TextField txtnoSupplier;
    @FXML private TextArea  txtalamatSupplier;

    @FXML private Button btnsaveSupplier;
    @FXML private Button btnupdateSupplier;
    @FXML private Button btndeleteSupplier;
    @FXML private Button btnresetSupplier;

    @FXML private TableView<Supplier>           tblSupplier;
    @FXML private TableColumn<Supplier, String> clmidSupplier;
    @FXML private TableColumn<Supplier, String> clmnamaSupplier;
    @FXML private TableColumn<Supplier, String> clmemailSupplier;
    @FXML private TableColumn<Supplier, String> clmnoSupplier;
    @FXML private TableColumn<Supplier, String> clmalamatSupplier;

    @FXML private TextField txtcariSupplier;

    private ObservableList<Supplier> daftarSupplier = FXCollections.observableArrayList();
    private FilteredList<Supplier>   filterSupplier;
    private String                   selectedId     = null;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupTableClick();
        setupSearch();
        loadData();

        btnupdateSupplier.setDisable(true);
        btndeleteSupplier.setDisable(true);
    }

    private void setupTableColumns() {
        clmidSupplier.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmnamaSupplier.setCellValueFactory(new PropertyValueFactory<>("nama"));
        clmemailSupplier.setCellValueFactory(new PropertyValueFactory<>("email"));
        clmalamatSupplier.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        clmnoSupplier.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
    }

    private void setupTableClick() {
        tblSupplier.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) isiForm(newVal);
            });
    }

    private void setupSearch() {
        filterSupplier = new FilteredList<>(daftarSupplier, p -> true);

        txtcariSupplier.textProperty().addListener((obs, oldVal, newVal) -> {
            filterSupplier.setPredicate(s -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String kw = newVal.toLowerCase();
                return s.getNama().toLowerCase().contains(kw)
                    || s.getEmail().toLowerCase().contains(kw)
                    || s.getNoTelp().toLowerCase().contains(kw)
                    || s.getAlamat().toLowerCase().contains(kw);
            });
        });

        tblSupplier.setItems(filterSupplier);
    }

    private void loadData() {
        daftarSupplier.clear();
        String sql = "SELECT id, nama, email, alamat, no_telp FROM `Supplier` ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarSupplier.add(new Supplier(
                    rs.getString("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("alamat"),
                    rs.getString("no_telp")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data: " + e.getMessage());
        }
    }


    private void isiForm(Supplier s) {
        selectedId = s.getId();
        txtnamaSupplier.setText(s.getNama());
        txtemailSupplier.setText(s.getEmail());
        txtnoSupplier.setText(s.getNoTelp());
        txtalamatSupplier.setText(s.getAlamat());

        btnsaveSupplier.setDisable(true);
        btnupdateSupplier.setDisable(false);
        btndeleteSupplier.setDisable(false);
    }

    @FXML
    public void btnsaveSupplier() {
        if (!validateForm()) return;

        String sql = "INSERT INTO `Supplier` (id, nama, email, no_telp, alamat) VALUES (?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, txtnamaSupplier.getText().trim());
            stmt.setString(3, getNullable(txtemailSupplier.getText()));
            stmt.setString(4, getNullable(txtnoSupplier.getText()));
            stmt.setString(5, getNullable(txtalamatSupplier.getText()));

            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data supplier berhasil disimpan.");
            resetForm();
            loadData();

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "No. Telepon sudah terdaftar di sistem.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    @FXML
    public void btnupdateSupplier() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data supplier yang ingin diubah.");
            return;
        }
        if (!validateForm()) return;

        String sql = "UPDATE `Supplier` SET nama=?, email=?, alamat=?, no_telp=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtnamaSupplier.getText().trim());
            stmt.setString(2, getNullable(txtemailSupplier.getText()));
            stmt.setString(3, getNullable(txtalamatSupplier.getText()));
            stmt.setString(4, getNullable(txtnoSupplier.getText()));
            stmt.setString(5, selectedId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data supplier berhasil diperbarui.");
                resetForm();
                loadData();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "No. Telepon sudah digunakan supplier lain.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        }
    }


    @FXML
    public void btndeleteSupplier() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data supplier yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
            "Konfirmasi Hapus",
            "Yakin ingin menghapus supplier ini?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM `Supplier` WHERE id=?";

            try (Connection conn = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, selectedId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data supplier berhasil dihapus.");
                    resetForm();
                    loadData();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    @FXML
    public void btnresetSupplier() {
        resetForm();
    }

    private void resetForm() {
        selectedId = null;
        txtnamaSupplier.clear();
        txtemailSupplier.clear();
        txtnoSupplier.clear();
        txtalamatSupplier.clear();

        tblSupplier.getSelectionModel().clearSelection();

        btnsaveSupplier.setDisable(false);
        btnupdateSupplier.setDisable(true);
        btndeleteSupplier.setDisable(true);
    }


    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtnamaSupplier.getText().trim().isEmpty())
            errors.append("• Nama supplier tidak boleh kosong.\n");

        String email = txtemailSupplier.getText().trim();
        if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            errors.append("• Format email tidak valid.\n");

        String noTelp = txtnoSupplier.getText().trim();
        if (!noTelp.isEmpty() && !noTelp.matches("^(08|\\+62|021|022|024|0251)[\\d\\-]{6,12}$"))
            errors.append("• Format No.Telp tidak valid.\n");

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

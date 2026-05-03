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

import models.Karyawan;
import models.UserAkun;
import utils.DBConnection;

public class KaryawanController implements Initializable {
    @FXML private TextField         txtemailKaryawan;
    @FXML private TextField         txtpasswordKaryawan;
    @FXML private ComboBox<String>  txtstatusKaryawan;
    @FXML private ComboBox<String>  txtroleKaryawan;
    @FXML private TextField         txtnamaKaryawan;
    @FXML private TextField         txtjabatanKaryawan;
    @FXML private TextField         txtnoKaryawan;
    @FXML private DatePicker        txttglKaryawan;

    @FXML private Button btnsaveKaryawan;
    @FXML private Button btnupdateKaryawan;
    @FXML private Button btndeleteKaryawan;
    @FXML private Button btnresetkaryawan;

    @FXML private TableView<UserAkun>           tbluserKaryawan;
    @FXML private TableColumn<UserAkun, String> clmiduserKaryawan;
    @FXML private TableColumn<UserAkun, String> clmemailuserKaryawan;
    @FXML private TableColumn<UserAkun, String> clmpassworduserKaryawan;
    @FXML private TableColumn<UserAkun, String> clmstatususerKaryawan;
    @FXML private TableColumn<UserAkun, String> clmroleuserKaryawan;
    @FXML private TextField                     txtcariKaryawan;

    @FXML private TableView<Karyawan>           tblClient11;
    @FXML private TableColumn<Karyawan, String> clmidKaryawan;
    @FXML private TableColumn<Karyawan, String> clmnamaKaryawan;
    @FXML private TableColumn<Karyawan, String> clmjabatanKaryawan;
    @FXML private TableColumn<Karyawan, String> clmnoKaryawan;
    @FXML private TableColumn<Karyawan, String> clmtglKaryawan;
    @FXML private TextField                     txtcariClient11;

    private ObservableList<UserAkun> daftarUser     = FXCollections.observableArrayList();
    private ObservableList<Karyawan> daftarKaryawan = FXCollections.observableArrayList();
    private FilteredList<UserAkun>   filterUser;
    private FilteredList<Karyawan>   filterKaryawan;

    private String selectedUserId    = null;
    private String selectedKaryawanId = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableUserColumns();
        setupTableKaryawanColumns();
        setupTableUserClick();
        setupTableKaryawanClick();
        setupSearch();
        loadDataUser();
        loadDataKaryawan();

        btnupdateKaryawan.setDisable(true);
        btndeleteKaryawan.setDisable(true);
    }

    private void setupComboBox() {
        txtstatusKaryawan.setItems(FXCollections.observableArrayList(
            "online", "offline", "blacklisted"
        ));
        txtroleKaryawan.setItems(FXCollections.observableArrayList(
            "Admin", "Karyawan"
        ));
    }

    private void setupTableUserColumns() {
        clmiduserKaryawan.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmemailuserKaryawan.setCellValueFactory(new PropertyValueFactory<>("email"));
        clmpassworduserKaryawan.setCellValueFactory(new PropertyValueFactory<>("password"));
        clmstatususerKaryawan.setCellValueFactory(new PropertyValueFactory<>("status"));
        clmroleuserKaryawan.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void setupTableKaryawanColumns() {
        clmidKaryawan.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmnamaKaryawan.setCellValueFactory(new PropertyValueFactory<>("nama"));
        clmjabatanKaryawan.setCellValueFactory(new PropertyValueFactory<>("jabatan"));
        clmnoKaryawan.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        clmtglKaryawan.setCellValueFactory(new PropertyValueFactory<>("tglMasuk"));
    }

    /**
     * Klik baris di tabel USER → isi form bagian akun login.
     * Cari data karyawan yang userId-nya cocok lalu isi form karyawan juga.
     */
    private void setupTableUserClick() {
        tbluserKaryawan.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    isiFormUser(newVal);
                    daftarKaryawan.stream()
                        .filter(k -> newVal.getId().equals(k.getUserId()))
                        .findFirst()
                        .ifPresent(this::isiFormKaryawan);
                }
            });
    }

    /**
     * Klik baris di tabel KARYAWAN → isi form karyawan.
     * Cari akun user yang cocok lalu isi form akun juga.
     */
    private void setupTableKaryawanClick() {
        tblClient11.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    isiFormKaryawan(newVal);
                    daftarUser.stream()
                        .filter(u -> u.getId().equals(newVal.getUserId()))
                        .findFirst()
                        .ifPresent(this::isiFormUser);
                }
            });
    }

    private void setupSearch() {
        filterUser = new FilteredList<>(daftarUser, p -> true);
        txtcariKaryawan.textProperty().addListener((obs, oldVal, newVal) -> {
            filterUser.setPredicate(u -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String kw = newVal.toLowerCase();
                return u.getEmail().toLowerCase().contains(kw)
                    || u.getRole().toLowerCase().contains(kw)
                    || u.getStatus().toLowerCase().contains(kw);
            });
        });
        tbluserKaryawan.setItems(filterUser);

        filterKaryawan = new FilteredList<>(daftarKaryawan, p -> true);
        txtcariClient11.textProperty().addListener((obs, oldVal, newVal) -> {
            filterKaryawan.setPredicate(k -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String kw = newVal.toLowerCase();
                return k.getNama().toLowerCase().contains(kw)
                    || (k.getJabatan() != null && k.getJabatan().toLowerCase().contains(kw))
                    || (k.getNoTelp() != null && k.getNoTelp().toLowerCase().contains(kw));
            });
        });
        tblClient11.setItems(filterKaryawan);
    }


    private void loadDataUser() {
        daftarUser.clear();
        String sql = "SELECT id, email, password, status, role FROM `Users` ORDER BY created_at DESC";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                daftarUser.add(new UserAkun(
                    rs.getString("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data user: " + e.getMessage());
        }
    }

    private void loadDataKaryawan() {
        daftarKaryawan.clear();
        String sql = "SELECT id, userId, nama, jabatan, no_telp, tgl_masuk FROM Karyawan ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarKaryawan.add(new Karyawan(
                    rs.getString("id"),
                    rs.getString("userId"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("no_telp"),
                    rs.getString("tgl_masuk")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data karyawan: " + e.getMessage());
        }
    }


    private void isiFormUser(UserAkun u) {
        selectedUserId = u.getId();
        txtemailKaryawan.setText(u.getEmail());
        txtpasswordKaryawan.setText(u.getPassword());
        txtstatusKaryawan.setValue(u.getStatus());
        txtroleKaryawan.setValue(u.getRole());

        btnsaveKaryawan.setDisable(true);
        btnupdateKaryawan.setDisable(false);
        btndeleteKaryawan.setDisable(false);
    }

    private void isiFormKaryawan(Karyawan k) {
        selectedKaryawanId = k.getId();
        txtnamaKaryawan.setText(k.getNama());
        txtjabatanKaryawan.setText(k.getJabatan() != null ? k.getJabatan() : "");
        txtnoKaryawan.setText(k.getNoTelp() != null ? k.getNoTelp() : "");

        if (k.getTglMasuk() != null && !k.getTglMasuk().isEmpty()) {
            txttglKaryawan.setValue(LocalDate.parse(k.getTglMasuk()));
        } else {
            txttglKaryawan.setValue(null);
        }
    }


    @FXML
    public void btnsaveKaryawan() {
        if (!validateForm()) return;

        String sqlUser     = "INSERT INTO `Users` (id, email, password, role, status) VALUES (?,?,?,?,?)";
        String sqlKaryawan = "INSERT INTO Karyawan (id, userId, nama, jabatan, no_telp, tgl_masuk) VALUES (?,?,?,?,?,?)";

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String newUserId     = UUID.randomUUID().toString();
            String newKaryawanId = UUID.randomUUID().toString();

            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
                stmtUser.setString(1, newUserId);
                stmtUser.setString(2, txtemailKaryawan.getText().trim());
                stmtUser.setString(3, txtpasswordKaryawan.getText().trim());
                stmtUser.setString(4, txtroleKaryawan.getValue());
                stmtUser.setString(5, txtstatusKaryawan.getValue() );
                stmtUser.executeUpdate();
            }

            try (PreparedStatement stmtKry = conn.prepareStatement(sqlKaryawan)) {
                stmtKry.setString(1, newKaryawanId);
                stmtKry.setString(2, newUserId);
                stmtKry.setString(3, txtnamaKaryawan.getText().trim());
                stmtKry.setString(4, getNullable(txtjabatanKaryawan.getText()));
                stmtKry.setString(5, getNullable(txtnoKaryawan.getText()));
                stmtKry.setString(6, txttglKaryawan.getValue() != null
                    ? txttglKaryawan.getValue().toString() : null);
                stmtKry.executeUpdate();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data karyawan berhasil disimpan.");
            resetForm();
            loadDataUser();
            loadDataKaryawan();

        } catch (SQLIntegrityConstraintViolationException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "Email sudah terdaftar di sistem.");
        } catch (SQLException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        } finally {
            resetAutoCommit(conn);
        }
    }

    @FXML
    public void btnupdateKaryawan() {
        if (selectedUserId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data karyawan yang ingin diubah.");
            return;
        }
        if (!validateForm()) return;

        String sqlUser     = "UPDATE `Users` SET email=?, password=?, role=?, status=? WHERE id=?";
        String sqlKaryawan = "UPDATE Karyawan SET nama=?, jabatan=?, no_telp=?, tgl_masuk=? WHERE id=?";

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
                stmtUser.setString(1, txtemailKaryawan.getText().trim());
                stmtUser.setString(2, txtpasswordKaryawan.getText().trim());
                stmtUser.setString(3, txtroleKaryawan.getValue());
                stmtUser.setString(4, txtstatusKaryawan.getValue());
                stmtUser.setString(5, selectedUserId);
                stmtUser.executeUpdate();
            }

            if (selectedKaryawanId != null) {
                try (PreparedStatement stmtKry = conn.prepareStatement(sqlKaryawan)) {
                    stmtKry.setString(1, txtnamaKaryawan.getText().trim());
                    stmtKry.setString(2, getNullable(txtjabatanKaryawan.getText()));
                    stmtKry.setString(3, getNullable(txtnoKaryawan.getText()));
                    stmtKry.setString(4, txttglKaryawan.getValue() != null
                        ? txttglKaryawan.getValue().toString() : null);
                    stmtKry.setString(5, selectedKaryawanId);
                    stmtKry.executeUpdate();
                }
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data karyawan berhasil diperbarui.");
            resetForm();
            loadDataUser();
            loadDataKaryawan();

        } catch (SQLIntegrityConstraintViolationException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "Email sudah digunakan karyawan lain.");
        } catch (SQLException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        } finally {
            resetAutoCommit(conn);
        }
    }

    @FXML
    public void btndeleteKaryawan() {
        if (selectedUserId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data karyawan yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
            "Konfirmasi Hapus",
            "Yakin ingin menghapus akun dan data karyawan ini?\nAksi ini tidak dapat dibatalkan."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connection conn = null;
            try {
                conn = DBConnection.getInstance().getConnection();
                conn.setAutoCommit(false);

                // Hapus karyawan dulu (child), baru user (parent)
                if (selectedKaryawanId != null) {
                    try (PreparedStatement stmtKry = conn.prepareStatement(
                            "DELETE FROM Karyawan WHERE id=?")) {
                        stmtKry.setString(1, selectedKaryawanId);
                        stmtKry.executeUpdate();
                    }
                }

                try (PreparedStatement stmtUser = conn.prepareStatement(
                        "DELETE FROM `Users` WHERE id=?")) {
                    stmtUser.setString(1, selectedUserId);
                    stmtUser.executeUpdate();
                }

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data karyawan berhasil dihapus.");
                resetForm();
                loadDataUser();
                loadDataKaryawan();

            } catch (SQLException e) {
                rollback(conn);
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            } finally {
                resetAutoCommit(conn);
            }
        }
    }


    @FXML
    public void btnresetKaryawan() {
        resetForm();
    }

    private void resetForm() {
        selectedUserId     = null;
        selectedKaryawanId = null;

        txtemailKaryawan.clear();
        txtpasswordKaryawan.clear();
        txtstatusKaryawan.setValue(null);
        txtroleKaryawan.setValue(null);
        txtnamaKaryawan.clear();
        txtjabatanKaryawan.clear();
        txtnoKaryawan.clear();
        txttglKaryawan.setValue(null);

        tbluserKaryawan.getSelectionModel().clearSelection();
        tblClient11.getSelectionModel().clearSelection();

        btnsaveKaryawan.setDisable(false);
        btnupdateKaryawan.setDisable(true);
        btndeleteKaryawan.setDisable(true);
    }


    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtemailKaryawan.getText().trim().isEmpty())
            errors.append("• Email tidak boleh kosong.\n");
        else if (!txtemailKaryawan.getText().trim().matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$"))
            errors.append("• Format email tidak valid (harus @gmail.com).\n");

        if (txtpasswordKaryawan.getText().trim().isEmpty())
            errors.append("• Password tidak boleh kosong.\n");
        else if (txtpasswordKaryawan.getText().trim().length() < 6)
            errors.append("• Password minimal 6 karakter.\n");

        if (txtstatusKaryawan.getValue() == null)
            errors.append("• Status user harus dipilih.\n");

        if (txtroleKaryawan.getValue() == null)
            errors.append("• Role harus dipilih.\n");

        if (txtnamaKaryawan.getText().trim().isEmpty())
            errors.append("• Nama karyawan tidak boleh kosong.\n");

        String noTelp = txtnoKaryawan.getText().trim();
        if (!noTelp.isEmpty() && !noTelp.matches("^(08|\\+62)\\d{8,11}$"))
            errors.append("• Format No.Telp tidak valid (contoh: 08123456789).\n");

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", errors.toString());
            return false;
        }
        return true;
    }

    private String getNullable(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    private void resetAutoCommit(Connection conn) {
        if (conn != null) {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
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
package controller;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
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

import models.Pengerjaan;
import utils.DBConnection;

public class PengerjaanController implements Initializable {

    @FXML private ComboBox<String> cmborderPengerjaan;
    @FXML private ComboBox<String> cmbkarPengerjaan;
    @FXML private DatePicker       txttglMPengerjaan;
    @FXML private DatePicker       txttglSPengerjaan;
    @FXML private ComboBox<String> cmbstatusPengerjaan;
    @FXML private TextArea         txtcttnPengerjaan;

    @FXML private Button btnsavePengerjaan;
    @FXML private Button btnupdatePengerjaan;
    @FXML private Button btndeletePengerjaan;
    @FXML private Button btnresetPengerjaan;

    @FXML private TableView<Pengerjaan>           tblPengerjaan;
    @FXML private TableColumn<Pengerjaan, String> clmidPengerjaan;
    @FXML private TableColumn<Pengerjaan, String> clmorderIdPengerjaan;
    @FXML private TableColumn<Pengerjaan, String> clmkarIdPengerjaan;
    @FXML private TableColumn<Pengerjaan, String> clmtglMPengerjaan;
    @FXML private TableColumn<Pengerjaan, String> clmtglSPengerjaan;
    @FXML private TableColumn<Pengerjaan, String> clmstatusPengerjaan;
    @FXML private TableColumn<Pengerjaan, String> clmcttnPengerjaan;
    @FXML private TextField                       txtcariPengerjaan;

    private ObservableList<Pengerjaan> daftarPengerjaan = FXCollections.observableArrayList();
    private FilteredList<Pengerjaan>   filterPengerjaan;
    private String                     selectedId       = null;

    private final Map<String, String> mapOrder    = new LinkedHashMap<>();
    private final Map<String, String> mapKaryawan = new LinkedHashMap<>();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableColumns();
        setupTableClick();
        setupSearch();

        loadComboData();
        loadData();

        btnupdatePengerjaan.setDisable(true);
        btndeletePengerjaan.setDisable(true);
    }

    private void setupComboBox() {
        cmbstatusPengerjaan.setItems(FXCollections.observableArrayList(
            "Belum Mulai", "On Progress", "Selesai"
        ));
    }

    private void setupTableColumns() {
        clmidPengerjaan.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmorderIdPengerjaan.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        clmkarIdPengerjaan.setCellValueFactory(new PropertyValueFactory<>("karyawanId"));
        clmtglMPengerjaan.setCellValueFactory(new PropertyValueFactory<>("tglMulai"));
        clmtglSPengerjaan.setCellValueFactory(new PropertyValueFactory<>("tglSelesai"));
        clmstatusPengerjaan.setCellValueFactory(new PropertyValueFactory<>("status"));
        clmcttnPengerjaan.setCellValueFactory(new PropertyValueFactory<>("catatan"));
    }

    private void setupTableClick() {
        tblPengerjaan.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) isiForm(newVal);
            });
    }

    private void setupSearch() {
        filterPengerjaan = new FilteredList<>(daftarPengerjaan, p -> true);

        txtcariPengerjaan.textProperty().addListener((obs, oldVal, newVal) -> {
            filterPengerjaan.setPredicate(p -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String kw = newVal.toLowerCase();
                return p.getOrderId().toLowerCase().contains(kw)
                    || p.getKaryawanId().toLowerCase().contains(kw)
                    || p.getStatus().toLowerCase().contains(kw)
                    || p.getCatatan().toLowerCase().contains(kw);
            });
        });

        tblPengerjaan.setItems(filterPengerjaan);
    }


    private void loadComboData() {
        mapOrder.clear();
        mapKaryawan.clear();

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, status_order, tgl_order, batas_waktu FROM `Orders` ORDER BY created_at DESC");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String label = rs.getString("status_order") + " (" + rs.getString("tgl_order") + ") - " + " (" + rs.getString("batas_waktu") + ")";
                    mapOrder.put(label, rs.getString("id"));
                }
            }
            cmborderPengerjaan.setItems(FXCollections.observableArrayList(mapOrder.keySet()));

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nama FROM `Karyawan` ORDER BY nama");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapKaryawan.put(rs.getString("nama"), rs.getString("id"));
                }
            }
            cmbkarPengerjaan.setItems(FXCollections.observableArrayList(mapKaryawan.keySet()));

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data combobox: " + e.getMessage());
        }
    }


    private void loadData() {
        daftarPengerjaan.clear();
        String sql = "SELECT id, orderId, karyawanId, tgl_mulai, tgl_selesai, catatan, status " +
                     "FROM `Pengerjaan` ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarPengerjaan.add(new Pengerjaan(
                    rs.getString("id"),
                    rs.getString("orderId"),
                    rs.getString("karyawanId"),
                    rs.getString("tgl_mulai"),
                    rs.getString("tgl_selesai"),
                    rs.getString("catatan"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data: " + e.getMessage());
        }
    }


    private void isiForm(Pengerjaan p) {
        selectedId = p.getId();

        setComboByValue(cmborderPengerjaan, mapOrder, p.getOrderId());
        setComboByValue(cmbkarPengerjaan, mapKaryawan, p.getKaryawanId());
        cmbstatusPengerjaan.setValue(p.getStatus());
        txtcttnPengerjaan.setText(p.getCatatan());

        if (p.getTglMulai() != null && !p.getTglMulai().isEmpty())
            txttglMPengerjaan.setValue(LocalDate.parse(p.getTglMulai()));
        else
            txttglMPengerjaan.setValue(null);

        if (p.getTglSelesai() != null && !p.getTglSelesai().isEmpty())
            txttglSPengerjaan.setValue(LocalDate.parse(p.getTglSelesai()));
        else
            txttglSPengerjaan.setValue(null);

        btnsavePengerjaan.setDisable(true);
        btnupdatePengerjaan.setDisable(false);
        btndeletePengerjaan.setDisable(false);
    }

    private void setComboByValue(ComboBox<String> combo, Map<String, String> map, String id) {
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(id)) {
                combo.setValue(entry.getKey());
                return;
            }
        }
        combo.setValue(null);
    }

    @FXML
    private void btnsavePengerjaan() {
        if (!validateForm()) return;

        String sql = "INSERT INTO `Pengerjaan` (id, orderId, karyawanId, tgl_mulai, tgl_selesai, catatan, status) " +
                     "VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String orderId    = mapOrder.get(cmborderPengerjaan.getValue());
            String karyawanId = mapKaryawan.get(cmbkarPengerjaan.getValue());

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, orderId);
            stmt.setString(3, karyawanId);
            stmt.setString(4, txttglMPengerjaan.getValue() != null ? txttglMPengerjaan.getValue().toString() : null);
            stmt.setString(5, txttglSPengerjaan.getValue() != null ? txttglSPengerjaan.getValue().toString() : null);
            stmt.setString(6, getNullable(txtcttnPengerjaan.getText()));
            stmt.setString(7, cmbstatusPengerjaan.getValue());

            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data pengerjaan berhasil disimpan.");
            resetForm();
            loadData();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    @FXML
    private void btnupdatePengerjaan() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data pengerjaan yang ingin diubah.");
            return;
        }
        if (!validateForm()) return;

        String sql = "UPDATE `Pengerjaan` SET orderId=?, karyawanId=?, tgl_mulai=?, tgl_selesai=?, " +
                     "catatan=?, status=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String orderId    = mapOrder.get(cmborderPengerjaan.getValue());
            String karyawanId = mapKaryawan.get(cmbkarPengerjaan.getValue());

            stmt.setString(1, orderId);
            stmt.setString(2, karyawanId);
            stmt.setString(3, txttglMPengerjaan.getValue() != null ? txttglMPengerjaan.getValue().toString() : null);
            stmt.setString(4, txttglSPengerjaan.getValue() != null ? txttglSPengerjaan.getValue().toString() : null);
            stmt.setString(5, getNullable(txtcttnPengerjaan.getText()));
            stmt.setString(6, cmbstatusPengerjaan.getValue());
            stmt.setString(7, selectedId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data pengerjaan berhasil diperbarui.");
                resetForm();
                loadData();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        }
    }

    @FXML
    private void btndeletePengerjaan() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data pengerjaan yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
            "Konfirmasi Hapus",
            "Yakin ingin menghapus data pengerjaan ini?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "DELETE FROM `Pengerjaan` WHERE id=?")) {

                stmt.setString(1, selectedId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data pengerjaan berhasil dihapus.");
                    resetForm();
                    loadData();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnresetPengerjaan() {
        resetForm();
    }

    private void resetForm() {
        selectedId = null;

        cmborderPengerjaan.setValue(null);
        cmbkarPengerjaan.setValue(null);
        txttglMPengerjaan.setValue(null);
        txttglSPengerjaan.setValue(null);
        cmbstatusPengerjaan.setValue(null);
        txtcttnPengerjaan.clear();

        tblPengerjaan.getSelectionModel().clearSelection();

        btnsavePengerjaan.setDisable(false);
        btnupdatePengerjaan.setDisable(true);
        btndeletePengerjaan.setDisable(true);
    }
    
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (cmborderPengerjaan.getValue() == null)
            errors.append("• Order harus dipilih.\n");

        if (cmbkarPengerjaan.getValue() == null)
            errors.append("• Karyawan PJ harus dipilih.\n");

        if (cmbstatusPengerjaan.getValue() == null)
            errors.append("• Status harus dipilih.\n");

        LocalDate mulai   = txttglMPengerjaan.getValue();
        LocalDate selesai = txttglSPengerjaan.getValue();

        if (mulai != null && selesai != null && selesai.isBefore(mulai))
            errors.append("• Tgl Selesai tidak boleh sebelum Tgl Mulai.\n");

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
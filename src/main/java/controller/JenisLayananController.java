/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
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

import models.JenisLayanan;
import utils.DBConnection;

public class JenisLayananController implements Initializable {

    @FXML private TextField  txtnamaLayanan;
    @FXML private ComboBox<String> cmbkategoriLayanan;
    @FXML private TextField  txttarifLayanan;
    @FXML private ComboBox<String>  cmbsatuanLayanan;
    @FXML private TextArea   txtdescLayanan;

    @FXML private Button btnsaveLayanan;
    @FXML private Button btnupdateLayanan;
    @FXML private Button btndeleteLayanan;
    @FXML private Button btnresetLayanan;

    @FXML private TableView<JenisLayanan>               tblLayanan;
    @FXML private TableColumn<JenisLayanan, String>     clmidLayanan;
    @FXML private TableColumn<JenisLayanan, String>     clmnamaLayanan;
    @FXML private TableColumn<JenisLayanan, String>     clmkategoriLayanan;
    @FXML private TableColumn<JenisLayanan, String>     clmtarifLayanan;
    @FXML private TableColumn<JenisLayanan, String>     clmsatuanLayanan;
    @FXML private TableColumn<JenisLayanan, String>     clmdescLayanan;

    @FXML private TextField txtcariLayanan;

    private ObservableList<JenisLayanan> daftarLayanan = FXCollections.observableArrayList();
    private FilteredList<JenisLayanan>   filterLayanan;
    private String                       selectedId    = null;

    private static final NumberFormat CURRENCY =
        NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableColumns();
        setupTableClick();
        setupSearch();
        loadData();

        btnupdateLayanan.setDisable(true);
        btndeleteLayanan.setDisable(true);
    }

    private void setupComboBox() {
        cmbkategoriLayanan.setItems(FXCollections.observableArrayList(
            "Catering", "Pernikahan", "Bangunan", "Dekorasi", "Lainnya"
        ));
        
        cmbsatuanLayanan.setItems(FXCollections.observableArrayList(
                "per pax",
                "per Pcs",
                "per Box",
                "per Kg",
                "per Liter",
                "per Unit",
                "per Sak",
                "per m3",
                "per m2",
                "per Paket",
                "per Set"
        ));
    }

    private void setupTableColumns() {
        clmidLayanan.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmnamaLayanan.setCellValueFactory(new PropertyValueFactory<>("nama"));
        clmdescLayanan.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        clmdescLayanan.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item.length() > 30 ? item.substring(0, 30) + "..." : item);
                    setTooltip(new Tooltip(item));
                }
            }
        });
        clmkategoriLayanan.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        clmsatuanLayanan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        clmtarifLayanan.setCellValueFactory(new PropertyValueFactory<>("tarif"));
        clmtarifLayanan.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    try {
                        double val = Double.parseDouble(item);
                        setText("Rp " + CURRENCY.format(val));
                    } catch (NumberFormatException e) {
                        setText(item);
                    }
                }
            }
        });
    }

    private void setupTableClick() {
        tblLayanan.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) isiForm(newVal);
            });
    }

    private void setupSearch() {
        filterLayanan = new FilteredList<>(daftarLayanan, p -> true);

        txtcariLayanan.textProperty().addListener((obs, oldVal, newVal) -> {
            filterLayanan.setPredicate(l -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String kw = newVal.toLowerCase();
                return l.getNama().toLowerCase().contains(kw)
                    || l.getKategori().toLowerCase().contains(kw)
                    || l.getSatuan().toLowerCase().contains(kw)
                    || l.getDeskripsi().toLowerCase().contains(kw);
            });
        });

        tblLayanan.setItems(filterLayanan);
    }


    private void loadData() {
        daftarLayanan.clear();
        String sql = "SELECT id, nama, kategori, tarif, satuan, deskripsi " +
                     "FROM `Jenis_layanan` ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarLayanan.add(new JenisLayanan(
                    rs.getString("id"),
                    rs.getString("nama"),
                    rs.getString("kategori"),
                    rs.getString("tarif"),
                    rs.getString("satuan"),
                    rs.getString("deskripsi")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data: " + e.getMessage());
        }
    }


    private void isiForm(JenisLayanan l) {
        selectedId = l.getId();
        txtnamaLayanan.setText(l.getNama());
        cmbkategoriLayanan.setValue(l.getKategori());
        txttarifLayanan.setText(l.getTarif());
        cmbsatuanLayanan.setValue(l.getSatuan());
        txtdescLayanan.setText(l.getDeskripsi());

        btnsaveLayanan.setDisable(true);
        btnupdateLayanan.setDisable(false);
        btndeleteLayanan.setDisable(false);
    }


    @FXML
    public void btnsaveLayanan() {
        if (!validateForm()) return;

        String sql = "INSERT INTO `Jenis_layanan` (id, nama, kategori, tarif, satuan, deskripsi) " +
                     "VALUES (?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, txtnamaLayanan.getText().trim());
            stmt.setString(3, cmbkategoriLayanan.getValue());
            stmt.setDouble(4, Double.parseDouble(txttarifLayanan.getText().trim()));
            stmt.setString(5, getNullable(cmbsatuanLayanan.getValue()));
            stmt.setString(6, getNullable(txtdescLayanan.getText()));

            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data layanan berhasil disimpan.");
            resetForm();
            loadData();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    @FXML
    public void btnupdateLayanan() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data layanan yang ingin diubah.");
            return;
        }
        if (!validateForm()) return;

        String sql = "UPDATE `Jenis_layanan` SET nama=?, kategori=?, tarif=?, satuan=?, deskripsi=? " +
                     "WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtnamaLayanan.getText().trim());
            stmt.setString(2, cmbkategoriLayanan.getValue());
            stmt.setDouble(3, Double.parseDouble(txttarifLayanan.getText().trim()));
            stmt.setString(4, getNullable(cmbsatuanLayanan.getValue()));
            stmt.setString(5, getNullable(txtdescLayanan.getText()));
            stmt.setString(6, selectedId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data layanan berhasil diperbarui.");
                resetForm();
                loadData();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        }
    }


    @FXML
    public void btndeleteLayanan() {
        if (selectedId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data layanan yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
            "Konfirmasi Hapus",
            "Yakin ingin menghapus layanan ini?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "DELETE FROM `Jenis_layanan` WHERE id=?")) {

                stmt.setString(1, selectedId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data layanan berhasil dihapus.");
                    resetForm();
                    loadData();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    @FXML
    public void btnresetLayanan() {
        resetForm();
    }

    private void resetForm() {
        selectedId = null;
        txtnamaLayanan.clear();
        cmbkategoriLayanan.setValue(null);
        txttarifLayanan.clear();
        cmbsatuanLayanan.setValue(null);
        txtdescLayanan.clear();

        tblLayanan.getSelectionModel().clearSelection();

        btnsaveLayanan.setDisable(false);
        btnupdateLayanan.setDisable(true);
        btndeleteLayanan.setDisable(true);
    }


    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtnamaLayanan.getText().trim().isEmpty())
            errors.append("• Nama layanan tidak boleh kosong.\n");

        if (cmbkategoriLayanan.getValue() == null)
            errors.append("• Kategori harus dipilih.\n");

        String tarif = txttarifLayanan.getText().trim();
        if (tarif.isEmpty()) {
            errors.append("• Tarif tidak boleh kosong.\n");
        } else {
            try {
                double val = Double.parseDouble(tarif);
                if (val < 0) errors.append("• Tarif tidak boleh negatif.\n");
            } catch (NumberFormatException e) {
                errors.append("• Tarif harus berupa angka (contoh: 45000).\n");
            }
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
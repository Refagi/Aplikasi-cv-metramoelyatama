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

import models.DetailPembelian;
import models.Pembelian;
import utils.DBConnection;

public class PembelianController implements Initializable {

    @FXML
    private TextField txtfakturPembelian;
    @FXML
    private DatePicker txttglPembelian;
    @FXML
    private ComboBox<String> cmbsuppPembelian;
    @FXML
    private ComboBox<String> cmbkarPembelian;
    @FXML
    private TextField txttotalPembelian;

    @FXML
    private ComboBox<String> cmbbrgDp;
    @FXML
    private Spinner<Integer> spnqtyDp;
    @FXML
    private TextField txthrgDp;

    @FXML
    private Button btnsavePembelian;
    @FXML
    private Button btnupdatePembelian;
    @FXML
    private Button btndeletePembelian;
    @FXML
    private Button btnresetPembelian;

    @FXML
    private Button btntambahDp;
    @FXML
    private Button btnupdateDp;
    @FXML
    private Button btndeleteDp;

    @FXML
    private TableView<Pembelian> tblPembelian;
    @FXML
    private TableColumn<Pembelian, String> clmidPembelian;
    @FXML
    private TableColumn<Pembelian, String> clmfakturPembelian;
    @FXML
    private TableColumn<Pembelian, String> clmtglPembelian;
    @FXML
    private TableColumn<Pembelian, String> clmsuppPembelian;
    @FXML
    private TableColumn<Pembelian, String> clmkarPembelian;
    @FXML
    private TableColumn<Pembelian, String> clmtotalPembelian;
    @FXML
    private TextField txtcariPembelian;

    @FXML
    private TableView<DetailPembelian> tblDetailPembelian;
    @FXML
    private TableColumn<DetailPembelian, String> clmidDp;
    @FXML
    private TableColumn<DetailPembelian, String> clmpembelianidDp;
    @FXML
    private TableColumn<DetailPembelian, String> clmbarangidDp;
    @FXML
    private TableColumn<DetailPembelian, String> clmqtyDp;
    @FXML
    private TableColumn<DetailPembelian, String> clmhargaDp;
    @FXML
    private TableColumn<DetailPembelian, String> clmsubtotalDp;
    @FXML
    private TextField txtcariDetailPembelian;

    private ObservableList<Pembelian> daftarPembelian = FXCollections.observableArrayList();
    private ObservableList<DetailPembelian> daftarDetailPembelian = FXCollections.observableArrayList();
    private FilteredList<Pembelian> filterPembelian;
    private FilteredList<DetailPembelian> filterDetailPembelian;

    private String activePembelianId = null;
    private String selectedPembelianId = null;
    private String selectedItemId = null;

    private final Map<String, String> mapSupplier = new LinkedHashMap<>();
    private final Map<String, String> mapKaryawan = new LinkedHashMap<>();
    private final Map<String, String> mapBarang = new LinkedHashMap<>();
    private final Map<String, Double> mapHargaBarang = new LinkedHashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupSpinner();
        setupTableColumns();
        setupTableClick();
        setupSearch();
        setupBarangListener();

        loadComboData();
        loadDataPembelian();
        txtfakturPembelian.setText(generateNoFaktur());

        btnupdatePembelian.setDisable(true);
        btndeletePembelian.setDisable(true);
        setItemButtons(false);
    }

    private void setupSpinner() {
        spnqtyDp.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99999, 1));
    }

    private void setupTableColumns() {
        clmidPembelian.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmfakturPembelian.setCellValueFactory(new PropertyValueFactory<>("noFaktur"));
        clmtglPembelian.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        clmsuppPembelian.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        clmkarPembelian.setCellValueFactory(new PropertyValueFactory<>("karyawanId"));
        clmtotalPembelian.setCellValueFactory(new PropertyValueFactory<>("total"));

        clmidDp.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmpembelianidDp.setCellValueFactory(new PropertyValueFactory<>("pembelianId"));
        clmbarangidDp.setCellValueFactory(new PropertyValueFactory<>("barangId"));
        clmqtyDp.setCellValueFactory(new PropertyValueFactory<>("qty"));
        clmhargaDp.setCellValueFactory(new PropertyValueFactory<>("harga"));
        clmsubtotalDp.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void setupTableClick() {
        tblPembelian.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        isiFormPembelian(newVal);
                    }
                });

        tblDetailPembelian.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        isiFormItem(newVal);
                    }
                });
    }

    private void setupSearch() {
        filterPembelian = new FilteredList<>(daftarPembelian, p -> true);
        txtcariPembelian.textProperty().addListener((obs, oldVal, newVal) -> {
            filterPembelian.setPredicate(p -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String kw = newVal.toLowerCase();
                return p.getNoFaktur().toLowerCase().contains(kw)
                        || p.getSupplierId().toLowerCase().contains(kw)
                        || p.getKaryawanId().toLowerCase().contains(kw);
            });
        });
        tblPembelian.setItems(filterPembelian);

        filterDetailPembelian = new FilteredList<>(daftarDetailPembelian, p -> true);
        txtcariDetailPembelian.textProperty().addListener((obs, oldVal, newVal) -> {
            filterDetailPembelian.setPredicate(d -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                return d.getBarangId().toLowerCase().contains(newVal.toLowerCase());
            });
        });
        tblDetailPembelian.setItems(filterDetailPembelian);
    }

    private void setupBarangListener() {
        cmbbrgDp.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && mapHargaBarang.containsKey(newVal)) {
                txthrgDp.setText(formatAngka(mapHargaBarang.get(newVal)));
            } else {
                txthrgDp.clear();
            }
        });
    }

    private void loadComboData() {
        mapSupplier.clear();
        mapKaryawan.clear();
        mapBarang.clear();
        mapHargaBarang.clear();

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nama FROM `Supplier` ORDER BY nama"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapSupplier.put(rs.getString("nama"), rs.getString("id"));
                }
            }
            cmbsuppPembelian.setItems(FXCollections.observableArrayList(mapSupplier.keySet()));

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nama FROM `Karyawan` ORDER BY nama"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapKaryawan.put(rs.getString("nama"), rs.getString("id"));
                }
            }
            cmbkarPembelian.setItems(FXCollections.observableArrayList(mapKaryawan.keySet()));

            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nama_barang, harga_beli FROM `Barang` ORDER BY nama_barang"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapBarang.put(rs.getString("nama_barang"), rs.getString("id"));
                    mapHargaBarang.put(rs.getString("nama_barang"), rs.getDouble("harga_beli"));
                }
            }
            cmbbrgDp.setItems(FXCollections.observableArrayList(mapBarang.keySet()));

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data combobox: " + e.getMessage());
        }
    }

    private void loadDataPembelian() {
        daftarPembelian.clear();
        String sql = "SELECT id, no_faktur, tanggal, supplierId, karyawanId, total "
                + "FROM `Pembelian` ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarPembelian.add(new Pembelian(
                        rs.getString("id"), rs.getString("no_faktur"), rs.getString("tanggal"),
                        rs.getString("supplierId"), rs.getString("karyawanId"), rs.getString("total")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data: " + e.getMessage());
        }
    }

    private void loadDataDetailPembelian(String pembelianId) {
        daftarDetailPembelian.clear();
        if (pembelianId == null) {
            return;
        }

        String sql = "SELECT id, pembelianId, barangId, qty, harga, subtotal "
                + "FROM `Detail_pembelian` WHERE pembelianId=? ORDER BY created_at ASC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pembelianId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    daftarDetailPembelian.add(new DetailPembelian(
                            rs.getString("id"), rs.getString("pembelianId"), rs.getString("barangId"),
                            rs.getString("qty"), rs.getString("harga"), rs.getString("subtotal")
                    ));
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat detail: " + e.getMessage());
        }
    }

    private void isiFormPembelian(Pembelian p) {
        selectedPembelianId = p.getId();
        activePembelianId = p.getId();

        txtfakturPembelian.setText(p.getNoFaktur());
        setComboByValue(cmbsuppPembelian, mapSupplier, p.getSupplierId());
        setComboByValue(cmbkarPembelian, mapKaryawan, p.getKaryawanId());
        txttotalPembelian.setText(formatAngka(Double.parseDouble(p.getTotal())));

        if (p.getTanggal() != null && !p.getTanggal().isEmpty()) {
            try {
                txttglPembelian.setValue(LocalDate.parse(p.getTanggal().split(" ")[0]));
            } catch (Exception e) {
                txttglPembelian.setValue(null);
            }
        }

        btnsavePembelian.setDisable(true);
        btnupdatePembelian.setDisable(false);
        btndeletePembelian.setDisable(false);

        resetFormItem();
        loadDataDetailPembelian(activePembelianId);
        setItemButtons(true);
    }

    private void isiFormItem(DetailPembelian d) {
        selectedItemId = d.getId();
        setComboByValue(cmbbrgDp, mapBarang, d.getBarangId());
        spnqtyDp.getValueFactory().setValue(Integer.parseInt(d.getQty()));
        txthrgDp.setText(formatAngka(Double.parseDouble(d.getHarga())));

        btntambahDp.setDisable(true);
        btnupdateDp.setDisable(false);
        btndeleteDp.setDisable(false);
    }

    private void setComboByValue(ComboBox<String> combo, Map<String, String> map, String id) {
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(id)) {
                combo.setValue(entry.getKey());
                return;
            }
        }
    }

    private void setItemButtons(boolean enabled) {
        btntambahDp.setDisable(!enabled);
        btnupdateDp.setDisable(true);
        btndeleteDp.setDisable(true);
    }

    @FXML
    private void btnsavePembelian() {
        if (!validateFormPembelian()) {
            return;
        }

        String sql = "INSERT INTO `Pembelian` (id, no_faktur, tanggal, supplierId, karyawanId, total) " + "VALUES (?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String newId = UUID.randomUUID().toString();
            String supplierId = mapSupplier.get(cmbsuppPembelian.getValue());
            String karyawanId = mapKaryawan.get(cmbkarPembelian.getValue());

            stmt.setString(1, newId);
            stmt.setString(2, txtfakturPembelian.getText().trim());
            stmt.setString(3, txttglPembelian.getValue() != null ? txttglPembelian.getValue().toString() + " 00:00:00" : null);
            stmt.setString(4, supplierId);
            stmt.setString(5, karyawanId);
            stmt.setDouble(6, 0);
            stmt.executeUpdate();

            activePembelianId = newId;
            selectedPembelianId = newId;

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pembelian berhasil disimpan. Silakan tambahkan barang di bawah.");
            loadDataPembelian();
            daftarDetailPembelian.clear();
            setItemButtons(true);

            btnsavePembelian.setDisable(true);
            btnupdatePembelian.setDisable(false);
            btndeletePembelian.setDisable(false);

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "No Faktur sudah digunakan.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    @FXML
    private void btnupdatePembelian() {
        if (selectedPembelianId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih pembelian yang ingin diubah.");
            return;
        }
        if (!validateFormPembelian()) {
            return;
        }

        String sql = "UPDATE `Pembelian` SET no_faktur=?, tanggal=?, supplierId=?, karyawanId=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String supplierId = mapSupplier.get(cmbsuppPembelian.getValue());
            String karyawanId = mapKaryawan.get(cmbkarPembelian.getValue());

            stmt.setString(1, txtfakturPembelian.getText().trim());
            stmt.setString(2, txttglPembelian.getValue() != null ? txttglPembelian.getValue().toString() + " 00:00:00" : null);
            stmt.setString(3, supplierId);
            stmt.setString(4, karyawanId);
            stmt.setString(5, selectedPembelianId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pembelian berhasil diperbarui.");
                loadDataPembelian();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "No Faktur sudah digunakan pembelian lain.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        }
    }

    @FXML
    private void btndeletePembelian() {
        if (selectedPembelianId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih pembelian yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus pembelian ini?\nStok barang terkait akan dikurangi kembali."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connection conn = null;
            try {
                conn = DBConnection.getInstance().getConnection();
                conn.setAutoCommit(false);

                String sqlItems = "SELECT barangId, qty FROM `Detail_pembelian` WHERE pembelianId=?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlItems)) {
                    stmt.setString(1, selectedPembelianId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            kurangiStok(conn, rs.getString("barangId"), rs.getInt("qty"));
                        }
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM `Pembelian` WHERE id=?")) {
                    stmt.setString(1, selectedPembelianId);
                    stmt.executeUpdate();
                }

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pembelian berhasil dihapus, stok disesuaikan.");
                resetFormPembelian();
                loadDataPembelian();
                loadComboData();

            } catch (SQLException e) {
                rollback(conn);
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            } finally {
                resetAutoCommit(conn);
            }
        }
    }

    @FXML
    private void btntambahItem() {
        if (activePembelianId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Simpan atau pilih Pembelian terlebih dahulu.");
            return;
        }
        if (!validateFormItem()) {
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            String barangId = mapBarang.get(cmbbrgDp.getValue());
            int qty = spnqtyDp.getValue();
            double harga = Double.parseDouble(txthrgDp.getText().trim());
            double subtotal = qty * harga;

            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO `Detail_pembelian` (id, pembelianId, barangId, qty, harga, subtotal) "
                    + "VALUES (?,?,?,?,?,?)")) {
                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, activePembelianId);
                stmt.setString(3, barangId);
                stmt.setInt(4, qty);
                stmt.setDouble(5, harga);
                stmt.setDouble(6, subtotal);
                stmt.executeUpdate();
            }

            tambahStok(conn, barangId, qty);

            updateTotalPembelian(conn, activePembelianId);

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Item barang berhasil ditambahkan.");
            resetFormItem();
            loadDataDetailPembelian(activePembelianId);
            loadDataPembelian();
            loadComboData();

        } catch (SQLException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambah item: " + e.getMessage());
        } catch (NumberFormatException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Error", "Harga/Qty tidak valid.");
        } finally {
            resetAutoCommit(conn);
        }
    }

    @FXML
    private void btnupdateItem() {
        if (selectedItemId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih item yang ingin diubah.");
            return;
        }
        if (!validateFormItem()) {
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            int qtyLama = 0;
            String barangIdLama = null;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT barangId, qty FROM `Detail_pembelian` WHERE id=?")) {
                stmt.setString(1, selectedItemId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        barangIdLama = rs.getString("barangId");
                        qtyLama = rs.getInt("qty");
                    }
                }
            }

            String barangIdBaru = mapBarang.get(cmbbrgDp.getValue());
            int qtyBaru = spnqtyDp.getValue();
            double harga = Double.parseDouble(txthrgDp.getText().trim());
            double subtotal = qtyBaru * harga;

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE `Detail_pembelian` SET barangId=?, qty=?, harga=?, subtotal=? WHERE id=?")) {
                stmt.setString(1, barangIdBaru);
                stmt.setInt(2, qtyBaru);
                stmt.setDouble(3, harga);
                stmt.setDouble(4, subtotal);
                stmt.setString(5, selectedItemId);
                stmt.executeUpdate();
            }

            if (barangIdLama != null) {
                kurangiStok(conn, barangIdLama, qtyLama);
            }
            tambahStok(conn, barangIdBaru, qtyBaru);

            updateTotalPembelian(conn, activePembelianId);

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Item berhasil diperbarui.");
            resetFormItem();
            loadDataDetailPembelian(activePembelianId);
            loadDataPembelian();
            loadComboData();

        } catch (SQLException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah item: " + e.getMessage());
        } catch (NumberFormatException e) {
            rollback(conn);
            showAlert(Alert.AlertType.ERROR, "Error", "Harga/Qty tidak valid.");
        } finally {
            resetAutoCommit(conn);
        }
    }

    @FXML
    private void btndeleteItem() {
        if (selectedItemId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih item yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus item ini?\nStok barang akan dikurangi kembali."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connection conn = null;
            try {
                conn = DBConnection.getInstance().getConnection();
                conn.setAutoCommit(false);

                String barangId = null;
                int qty = 0;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT barangId, qty FROM `Detail_pembelian` WHERE id=?")) {
                    stmt.setString(1, selectedItemId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            barangId = rs.getString("barangId");
                            qty = rs.getInt("qty");
                        }
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM `Detail_pembelian` WHERE id=?")) {
                    stmt.setString(1, selectedItemId);
                    stmt.executeUpdate();
                }

                if (barangId != null) {
                    kurangiStok(conn, barangId, qty);
                }
                updateTotalPembelian(conn, activePembelianId);

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Item berhasil dihapus, stok disesuaikan.");
                resetFormItem();
                loadDataDetailPembelian(activePembelianId);
                loadDataPembelian();
                loadComboData();

            } catch (SQLException e) {
                rollback(conn);
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus item: " + e.getMessage());
            } finally {
                resetAutoCommit(conn);
            }
        }
    }

    private void tambahStok(Connection conn, String barangId, int qty) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE `Barang` SET stok = stok + ? WHERE id=?")) {
            stmt.setInt(1, qty);
            stmt.setString(2, barangId);
            stmt.executeUpdate();
        }
    }

    private void kurangiStok(Connection conn, String barangId, int qty) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE `Barang` SET stok = stok - ? WHERE id=?")) {
            stmt.setInt(1, qty);
            stmt.setString(2, barangId);
            stmt.executeUpdate();
        }
    }

    private void updateTotalPembelian(Connection conn, String pembelianId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE `Pembelian` SET total = (SELECT COALESCE(SUM(subtotal),0) "
                + "FROM `Detail_pembelian` WHERE pembelianId=?) WHERE id=?")) {
            stmt.setString(1, pembelianId);
            stmt.setString(2, pembelianId);
            stmt.executeUpdate();
        }
    }

    @FXML
    private void btnresetPembelian() {
        resetFormPembelian();
    }

    private void resetFormPembelian() {
        selectedPembelianId = null;
        activePembelianId = null;
        
        txtfakturPembelian.setText(generateNoFaktur());
        txttglPembelian.setValue(null);
        cmbsuppPembelian.setValue(null);
        cmbkarPembelian.setValue(null);
        txttotalPembelian.clear();

        tblPembelian.getSelectionModel().clearSelection();
        daftarDetailPembelian.clear();

        btnsavePembelian.setDisable(false);
        btnupdatePembelian.setDisable(true);
        btndeletePembelian.setDisable(true);
        setItemButtons(false);

        resetFormItem();
    }

    private void resetFormItem() {
        selectedItemId = null;
        cmbbrgDp.setValue(null);
        spnqtyDp.getValueFactory().setValue(1);
        txthrgDp.clear();

        tblDetailPembelian.getSelectionModel().clearSelection();

        btntambahDp.setDisable(activePembelianId == null);
        btnupdateDp.setDisable(true);
        btndeleteDp.setDisable(true);
    }

    private String generateNoFaktur() {
        int tahunSekarang = LocalDate.now().getYear();
        String prefix = "PBL/" + tahunSekarang + "/";

        String sql = "SELECT no_faktur FROM `Pembelian` WHERE no_faktur LIKE ? ORDER BY no_faktur DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, prefix + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String lastFaktur = rs.getString("no_faktur"); 
                    String[] parts = lastFaktur.split("/");
                    int lastNumber = Integer.parseInt(parts[2]);
                    int nextNumber = lastNumber + 1;
                    return prefix + String.format("%03d", nextNumber);
                } else {
                    return prefix + "001";
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal generate no faktur: " + e.getMessage());
            return prefix + "001";
        }
    }

    private boolean validateFormPembelian() {
        StringBuilder errors = new StringBuilder();

        if (txttglPembelian.getValue() == null) {
            errors.append("• Tanggal harus diisi.\n");
        }
        if (cmbsuppPembelian.getValue() == null) {
            errors.append("• Supplier harus dipilih.\n");
        }
        if (cmbkarPembelian.getValue() == null) {
            errors.append("• Karyawan harus dipilih.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", errors.toString());
            return false;
        }
        return true;
    }

    private boolean validateFormItem() {
        StringBuilder errors = new StringBuilder();
        if (cmbbrgDp.getValue() == null) {
            errors.append("• Barang harus dipilih.\n");
        }
        if (txthrgDp.getText() == null || txthrgDp.getText().trim().isEmpty()) {
            errors.append("• Harga belum terisi, pilih ulang Barang.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", errors.toString());
            return false;
        }
        return true;
    }

    private String formatAngka(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void resetAutoCommit(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
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

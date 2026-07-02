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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import models.DetailOrder;
import models.Order;
import utils.DBConnection;

public class OrderController implements Initializable {

    @FXML
    private ComboBox<String> cmbclOrder;
    @FXML
    private ComboBox<String> cmbkrOrder;
    @FXML
    private TextArea txtketOrder;
    @FXML
    private ComboBox<String> cmbstatusOrder;
    @FXML
    private DatePicker txttglOrder;
    @FXML
    private DatePicker txtbwOrder;

    @FXML
    private ComboBox<String> cmbjlOrder;
    @FXML
    private Spinner<Integer> spnJumlahItem;
    @FXML
    private TextField txttarifItem;

    @FXML
    private Button btnsaveOrder;
    @FXML
    private Button btnupdateOrder;
    @FXML
    private Button btndeleteOrder;
    @FXML
    private Button btnresetOrder;
    @FXML
    private Button btntambahItem;

    @FXML
    private TableView<Order> tblOrder;
    @FXML
    private TableColumn<Order, String> clmidOrder;
    @FXML
    private TableColumn<Order, String> clmclidOrder;
    @FXML
    private TableColumn<Order, String> clmkridOrder;
    @FXML
    private TableColumn<Order, String> clmtglOrder;
    @FXML
    private TableColumn<Order, String> clmbwOrder;
    @FXML
    private TableColumn<Order, String> clmstatusOrder;
    @FXML
    private TableColumn<Order, String> clmketOrder;
    @FXML
    private TextField txtcariOrder;

    @FXML
    private TableView<DetailOrder> tblDetailOrder;
    @FXML
    private TableColumn<DetailOrder, String> clmidItem;
    @FXML
    private TableColumn<DetailOrder, String> clmnamaItem;
    @FXML
    private TableColumn<DetailOrder, String> clmlynidItem;
    @FXML
    private TableColumn<DetailOrder, String> clmjmlhItem;
    @FXML
    private TableColumn<DetailOrder, String> clmsubtotItem;
    @FXML
    private TableColumn<DetailOrder, String> clmtarifItem;
    @FXML
    private TextField txtcariDetailOrder;

    private ObservableList<Order> daftarOrder = FXCollections.observableArrayList();
    private ObservableList<DetailOrder> daftarDetailOrder = FXCollections.observableArrayList();
    private FilteredList<Order> filterOrder;
    private FilteredList<DetailOrder> filterDetailOrder;

    private String selectedOrderId = null;
    private String selectedDetailId = null;
    private String activeOrderId = null;

    private final java.util.Map<String, String> mapClient = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, String> mapKaryawan = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, String> mapLayanan = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> mapTarifLayanan = new java.util.LinkedHashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupSpinner();
        setupTableColumns();
        setupTableClick();
        setupSearch();
        setupLayananListener();

        loadComboData();
        loadDataOrder();
        loadDataDetailOrder();

        btnupdateOrder.setDisable(true);
        btndeleteOrder.setDisable(true);
    }

    private void setupComboBox() {
        cmbstatusOrder.setItems(FXCollections.observableArrayList(
                "Pending", "Proses", "Selesai", "Batal"
        ));
    }

    private void setupSpinner() {
        spnJumlahItem.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1)
        );
    }

    private void setupTableColumns() {
        clmidOrder.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmclidOrder.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clmkridOrder.setCellValueFactory(new PropertyValueFactory<>("karyawanId"));
        clmtglOrder.setCellValueFactory(new PropertyValueFactory<>("tglOrder"));
        clmbwOrder.setCellValueFactory(new PropertyValueFactory<>("batasWaktu"));
        clmstatusOrder.setCellValueFactory(new PropertyValueFactory<>("statusOrder"));
        clmketOrder.setCellValueFactory(new PropertyValueFactory<>("keterangan"));

        clmidItem.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmnamaItem.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        clmlynidItem.setCellValueFactory(new PropertyValueFactory<>("layananId"));
        clmjmlhItem.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        clmtarifItem.setCellValueFactory(new PropertyValueFactory<>("tarif"));
        clmsubtotItem.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void setupTableClick() {
        tblOrder.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        isiFormOrder(newVal);
                    }
                });

        tblDetailOrder.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        isiFormDetail(newVal);
                    }
                });
    }

    private void setupSearch() {
        filterOrder = new FilteredList<>(daftarOrder, p -> true);
        txtcariOrder.textProperty().addListener((obs, oldVal, newVal) -> {
            filterOrder.setPredicate(o -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String kw = newVal.toLowerCase();
                return o.getClientId().toLowerCase().contains(kw)
                        || o.getKaryawanId().toLowerCase().contains(kw)
                        || o.getStatusOrder().toLowerCase().contains(kw)
                        || o.getKeterangan().toLowerCase().contains(kw);
            });
        });
        tblOrder.setItems(filterOrder);

        filterDetailOrder = new FilteredList<>(daftarDetailOrder, p -> true);
        txtcariDetailOrder.textProperty().addListener((obs, oldVal, newVal) -> {
            filterDetailOrder.setPredicate(d -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String kw = newVal.toLowerCase();
                return d.getOrderId().toLowerCase().contains(kw)
                        || d.getLayananId().toLowerCase().contains(kw);
            });
        });
        tblDetailOrder.setItems(filterDetailOrder);
    }


    private void setupLayananListener() {
        cmbjlOrder.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && mapTarifLayanan.containsKey(newVal)) {
                txttarifItem.setText(String.valueOf(mapTarifLayanan.get(newVal)));
            } else {
                txttarifItem.clear();
            }
        });
    }

    private void loadComboData() {
        mapClient.clear();
        mapKaryawan.clear();
        mapLayanan.clear();
        mapTarifLayanan.clear();

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            // Client
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nama FROM `Client` ORDER BY nama"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapClient.put(rs.getString("nama"), rs.getString("id"));
                }
            }
            cmbclOrder.setItems(FXCollections.observableArrayList(mapClient.keySet()));

            // Karyawan
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nama FROM `Karyawan` ORDER BY nama"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapKaryawan.put(rs.getString("nama"), rs.getString("id"));
                }
            }
            cmbkrOrder.setItems(FXCollections.observableArrayList(mapKaryawan.keySet()));

            // Jenis Layanan
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nama, tarif FROM `Jenis_layanan` ORDER BY nama"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapLayanan.put(rs.getString("nama"), rs.getString("id"));
                    mapTarifLayanan.put(rs.getString("nama"), rs.getDouble("tarif"));
                }
            }
            cmbjlOrder.setItems(FXCollections.observableArrayList(mapLayanan.keySet()));

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data combobox: " + e.getMessage());
        }
    }

    private void loadDataOrder() {
        daftarOrder.clear();
        String sql = "SELECT id, clientId, karyawanId, tgl_order, batas_waktu, status_order, keterangan "
                + "FROM `Orders` ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarOrder.add(new Order(
                        rs.getString("id"),
                        rs.getString("clientId"),
                        rs.getString("karyawanId"),
                        rs.getString("tgl_order"),
                        rs.getString("batas_waktu"),
                        rs.getString("status_order"),
                        rs.getString("keterangan")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data order: " + e.getMessage());
        }
    }

    private void loadDataDetailOrder() {
        daftarDetailOrder.clear();
        String sql = "SELECT id, orderId, layananId, jumlah, tarif, subtotal "
                + "FROM `Detail_order` ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarDetailOrder.add(new DetailOrder(
                        rs.getString("id"),
                        rs.getString("orderId"),
                        rs.getString("layananId"),
                        rs.getString("jumlah"),
                        rs.getString("tarif"),
                        rs.getString("subtotal")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data detail order: " + e.getMessage());
        }
    }

    private void isiFormOrder(Order o) {
        selectedOrderId = o.getId();
        activeOrderId = o.getId();

        setComboByValue(cmbclOrder, mapClient, o.getClientId());
        setComboByValue(cmbkrOrder, mapKaryawan, o.getKaryawanId());

        cmbstatusOrder.setValue(o.getStatusOrder());
        txtketOrder.setText(o.getKeterangan());

        if (o.getTglOrder() != null && !o.getTglOrder().isEmpty()) {
            txttglOrder.setValue(LocalDate.parse(o.getTglOrder()));
        }
        if (o.getBatasWaktu() != null && !o.getBatasWaktu().isEmpty()) {
            txtbwOrder.setValue(LocalDate.parse(o.getBatasWaktu()));
        }

        selectedDetailId = null;
        cmbjlOrder.setValue(null);
        spnJumlahItem.getValueFactory().setValue(1);
        txttarifItem.clear();

        btnsaveOrder.setDisable(true);
        btnupdateOrder.setDisable(false);
        btndeleteOrder.setDisable(false);
    }

    private void isiFormDetail(DetailOrder d) {
        selectedDetailId = d.getId();
        setComboByValue(cmbjlOrder, mapLayanan, d.getLayananId());
        spnJumlahItem.getValueFactory().setValue(Integer.parseInt(d.getJumlah()));
        txttarifItem.setText(d.getTarif());

        daftarOrder.stream()
                .filter(o -> o.getId().equals(d.getOrderId()))
                .findFirst()
                .ifPresent(order -> {
                    selectedOrderId = order.getId();
                    setComboByValue(cmbclOrder, mapClient, order.getClientId());
                    setComboByValue(cmbkrOrder, mapKaryawan, order.getKaryawanId());
                    cmbstatusOrder.setValue(order.getStatusOrder());
                    txtketOrder.setText(order.getKeterangan());

                    if (order.getTglOrder() != null && !order.getTglOrder().isEmpty()) {
                        txttglOrder.setValue(LocalDate.parse(order.getTglOrder()));
                    }
                    if (order.getBatasWaktu() != null && !order.getBatasWaktu().isEmpty()) {
                        txtbwOrder.setValue(LocalDate.parse(order.getBatasWaktu()));
                    }
                });

        btnsaveOrder.setDisable(true);
        btnupdateOrder.setDisable(false);
        btndeleteOrder.setDisable(false);
    }

    private void setComboByValue(ComboBox<String> combo, java.util.Map<String, String> map, String id) {
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(id)) {
                combo.setValue(entry.getKey());
                return;
            }
        }
        combo.setValue(null);
    }

    @FXML
    private void btntambahItem() {
        if (activeOrderId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Simpan atau pilih Order terlebih dahulu sebelum menambah layanan.");
            return;
        }
        if (!validateFormDetail()) {
            return;
        }

        String sqlDetail = "INSERT INTO `Detail_order` " + "(id, orderId, layananId, jumlah, tarif, subtotal) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlDetail)) {

            String layananId = mapLayanan.get(cmbjlOrder.getValue());
            int jumlah = spnJumlahItem.getValue();
            double tarif = Double.parseDouble(txttarifItem.getText().trim());
            double subtotal = jumlah * tarif;

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, activeOrderId);   // ⬅ pakai order yang sama terus
            stmt.setString(3, layananId);
            stmt.setInt(4, jumlah);
            stmt.setDouble(5, tarif);
            stmt.setDouble(6, subtotal);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Item layanan ditambahkan ke order.");

            cmbjlOrder.setValue(null);
            spnJumlahItem.getValueFactory().setValue(1);
            txttarifItem.clear();
            loadDataDetailOrder();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambah item: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Tarif/jumlah tidak valid.");
        }
    }

    @FXML
    private void btnsaveOrder() {
        if (!validateFormOrder()) {
            return;
        }
        String sqlOrder = "INSERT INTO `Orders` " + "(id, clientId, karyawanId, tgl_order, batas_waktu, status_order, keterangan) " + "VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlOrder)) {

            String newOrderId = UUID.randomUUID().toString();
            String clientId = mapClient.get(cmbclOrder.getValue());
            String karyawanId = mapKaryawan.get(cmbkrOrder.getValue());

            stmt.setString(1, newOrderId);
            stmt.setString(2, clientId);
            stmt.setString(3, karyawanId);
            stmt.setString(4, txttglOrder.getValue() != null ? txttglOrder.getValue().toString() : null);
            stmt.setString(5, txtbwOrder.getValue() != null ? txtbwOrder.getValue().toString() : null);
            stmt.setString(6, cmbstatusOrder.getValue());
            stmt.setString(7, getNullable(txtketOrder.getText()));
            stmt.executeUpdate();

            activeOrderId = newOrderId;   // ⬅ tandai order ini "aktif", siap ditambah item
            selectedOrderId = newOrderId;

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Order berhasil disimpan. Silakan tambahkan Jenis Layanan di bawah.");

            loadDataOrder();

            btnsaveOrder.setDisable(true);
            btnupdateOrder.setDisable(false);
            btndeleteOrder.setDisable(false);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan order: " + e.getMessage());
        }
    }

    @FXML

    private void btnupdateOrder() {
        if (selectedOrderId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih order yang ingin diubah.");
            return;
        }
        if (!validateFormOrder()) {
            return;
        }

        String sqlOrder = "UPDATE `Orders` SET clientId=?, karyawanId=?, tgl_order=?, " + "batas_waktu=?, status_order=?, keterangan=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlOrder)) {

            String clientId = mapClient.get(cmbclOrder.getValue());
            String karyawanId = mapKaryawan.get(cmbkrOrder.getValue());

            stmt.setString(1, clientId);
            stmt.setString(2, karyawanId);
            stmt.setString(3, txttglOrder.getValue() != null ? txttglOrder.getValue().toString() : null);
            stmt.setString(4, txtbwOrder.getValue() != null ? txtbwOrder.getValue().toString() : null);
            stmt.setString(5, cmbstatusOrder.getValue());
            stmt.setString(6, getNullable(txtketOrder.getText()));
            stmt.setString(7, selectedOrderId);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Order berhasil diperbarui.");
                loadDataOrder();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah order: " + e.getMessage());
        }
    }

    @FXML
    private void btnupdateDetail() {
        if (selectedDetailId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih item layanan yang ingin diubah.");
            return;
        }

        if (!validateFormDetail()) {
            return;
        }

        String sqlDetail = "UPDATE `Detail_order` SET layananId=?, jumlah=?, tarif=?, subtotal=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlDetail)) {

            String layananId = mapLayanan.get(cmbjlOrder.getValue());
            int jumlah = spnJumlahItem.getValue();
            double tarif = Double.parseDouble(txttarifItem.getText().trim());
            double subtotal = jumlah * tarif;

            stmt.setString(1, layananId);
            stmt.setInt(2, jumlah);
            stmt.setDouble(3, tarif);
            stmt.setDouble(4, subtotal);
            stmt.setString(5, selectedDetailId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Item layanan berhasil diperbarui.");
                loadDataDetailOrder();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah item: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Tarif/jumlah tidak valid.");
        }
    }

    @FXML
    private void btndeleteOrder() {
        if (selectedOrderId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih order yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus order ini?\nDetail Order, Pengerjaan & Invoice terkait juga akan terhapus."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM `Orders` WHERE id=?")) {

                // Detail_order, Pengerjaan, Invoice otomatis ikut terhapus (ON DELETE CASCADE)
                stmt.setString(1, selectedOrderId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Order berhasil dihapus.");
                    resetForm();
                    loadDataOrder();
                    loadDataDetailOrder();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus order: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btndeleteDetail() {
        if (selectedDetailId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih item layanan yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus item layanan ini dari order?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM `Detail_order` WHERE id=?")) {

                stmt.setString(1, selectedDetailId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Item layanan berhasil dihapus.");
                    selectedDetailId = null;
                    cmbjlOrder.setValue(null);
                    spnJumlahItem.getValueFactory().setValue(1);
                    txttarifItem.clear();
                    loadDataDetailOrder();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus item: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnresetOrder() {
        resetForm();
    }

    private void resetForm() {
        selectedOrderId = null;
        selectedDetailId = null;
        activeOrderId = null;

        cmbclOrder.setValue(null);
        cmbkrOrder.setValue(null);
        cmbstatusOrder.setValue(null);
        txtketOrder.clear();
        txttglOrder.setValue(null);
        txtbwOrder.setValue(null);

        cmbjlOrder.setValue(null);
        spnJumlahItem.getValueFactory().setValue(1);
        txttarifItem.clear();

        tblOrder.getSelectionModel().clearSelection();
        tblDetailOrder.getSelectionModel().clearSelection();

        btnsaveOrder.setDisable(false);
        btnupdateOrder.setDisable(true);
        btndeleteOrder.setDisable(true);
    }

    private boolean validateFormOrder() {
        StringBuilder errors = new StringBuilder();
        if (cmbclOrder.getValue() == null) {
            errors.append("• Client harus dipilih.\n");
        }
        if (cmbkrOrder.getValue() == null) {
            errors.append("• Karyawan harus dipilih.\n");
        }
        if (cmbstatusOrder.getValue() == null) {
            errors.append("• Status order harus dipilih.\n");
        }
        if (txttglOrder.getValue() == null) {
            errors.append("• Tgl Order harus diisi.\n");
        }

        if (txtbwOrder.getValue() == null) {
            errors.append("• Batas Waktu harus diisi.\n");
        } else if (txttglOrder.getValue() != null && txtbwOrder.getValue().isBefore(txttglOrder.getValue())) {
            errors.append("• Batas Waktu tidak boleh sebelum Tgl Order.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", errors.toString());
            return false;
        }
        return true;
    }

    private boolean validateFormDetail() {
        StringBuilder errors = new StringBuilder();
        if (cmbjlOrder.getValue() == null) {
            errors.append("• Jenis Layanan harus dipilih.\n");
        }
        if (txttarifItem.getText() == null || txttarifItem.getText().trim().isEmpty()) {
            errors.append("• Tarif belum terisi, pilih ulang Jenis Layanan.\n");
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

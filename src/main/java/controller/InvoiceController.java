package controller;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
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

import models.Invoice;
import models.Pembayaran;
import utils.DBConnection;

public class InvoiceController implements Initializable {

    @FXML
    private ComboBox<String> cmborderInvoice;
    @FXML
    private TextField txttotalInvoice;
    @FXML private Label lblSisaBayar;
    @FXML
    private DatePicker txttglInvoice;
    @FXML
    private ComboBox<String> cmbstatusInvoice;

    @FXML
    private DatePicker txttglByr;
    @FXML
    private TextField txtjmlhByr;
    @FXML
    private ComboBox<String> cmbmetodeByr;
    @FXML
    private TextArea txtcttnByr;

    @FXML
    private Button btnsaveInvoice;
    @FXML
    private Button btnupdateInvoice;
    @FXML
    private Button btndeleteInvoice;
    @FXML
    private Button btnresetInvoice;

    @FXML
    private Button btntambahByr;
    @FXML
    private Button btnupdateByr;
    @FXML
    private Button btndeleteByr;

    @FXML
    private TableView<Invoice> tblInvoice;
    @FXML
    private TableColumn<Invoice, String> clmidInvoice;
    @FXML
    private TableColumn<Invoice, String> clmorderidInvoice;
    @FXML
    private TableColumn<Invoice, String> clmtglbyrInvoice;
    @FXML
    private TableColumn<Invoice, String> clmtotalbyrInvoice;
    @FXML
    private TableColumn<Invoice, String> clmstatusbyrInvoice;
    @FXML
    private TextField txtcariInvoice;

    @FXML
    private TableView<Pembayaran> tblByr;
    @FXML
    private TableColumn<Pembayaran, String> clmIdByr;
    @FXML
    private TableColumn<Pembayaran, String> clminvoiceidByr;
    @FXML
    private TableColumn<Pembayaran, String> clmtglByr;
    @FXML
    private TableColumn<Pembayaran, String> clmjmlhByr;
    @FXML
    private TableColumn<Pembayaran, String> clmmetodeByr;
    @FXML
    private TableColumn<Pembayaran, String> clmcttnByr;
    @FXML
    private TextField txtcariByr;

    private ObservableList<Invoice> daftarInvoice = FXCollections.observableArrayList();
    private ObservableList<Pembayaran> daftarPembayaran = FXCollections.observableArrayList();
    private FilteredList<Invoice> filterInvoice;
    private FilteredList<Pembayaran> filterPembayaran;

    private String activeInvoiceId = null;
    private String selectedInvoiceId = null;
    private String selectedBayarId = null;

    private final Map<String, String> mapOrder = new LinkedHashMap<>();
    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableColumns();
        setupTableClick();
        setupSearch();
        setupOrderListener();

        loadComboOrder();
        loadDataInvoice();

        btnupdateInvoice.setDisable(true);
        btndeleteInvoice.setDisable(true);
        setPembayaranButtons(false);
    }

    private void setupComboBox() {
        cmbstatusInvoice.setItems(FXCollections.observableArrayList(
                "Belum Bayar", "Cicilan", "Lunas"
        ));
        cmbstatusInvoice.setDisable(true);

        cmbmetodeByr.setItems(FXCollections.observableArrayList("Transfer", "Cash"));
    }

    private void setupTableColumns() {
        clmidInvoice.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmorderidInvoice.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        clmtglbyrInvoice.setCellValueFactory(new PropertyValueFactory<>("tglInvoice"));
        clmstatusbyrInvoice.setCellValueFactory(new PropertyValueFactory<>("statusBayar"));

        clmtotalbyrInvoice.setCellValueFactory(new PropertyValueFactory<>("totalBayar"));
        clmtotalbyrInvoice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                try {
                    setText("Rp " + CURRENCY.format(Double.parseDouble(item)));
                } catch (NumberFormatException e) {
                    setText(item);
                }
            }
        });

        clmIdByr.setCellValueFactory(new PropertyValueFactory<>("id"));
        clminvoiceidByr.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        clmtglByr.setCellValueFactory(new PropertyValueFactory<>("tglBayar"));
        clmmetodeByr.setCellValueFactory(new PropertyValueFactory<>("metodeBayar"));
        clmcttnByr.setCellValueFactory(new PropertyValueFactory<>("catatan"));

        clmjmlhByr.setCellValueFactory(new PropertyValueFactory<>("jumlahBayar"));
        clmjmlhByr.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                try {
                    setText("Rp " + CURRENCY.format(Double.parseDouble(item)));
                } catch (NumberFormatException e) {
                    setText(item);
                }
            }
        });
    }

    private void setupTableClick() {
        tblInvoice.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        isiFormInvoice(newVal);
                    }
                });

        tblByr.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        isiFormBayar(newVal);
                    }
                });
    }

    private void setupSearch() {
        filterInvoice = new FilteredList<>(daftarInvoice, p -> true);
        txtcariInvoice.textProperty().addListener((obs, oldVal, newVal) -> {
            filterInvoice.setPredicate(i -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String kw = newVal.toLowerCase();
                return i.getOrderId().toLowerCase().contains(kw)
                        || i.getStatusBayar().toLowerCase().contains(kw);
            });
        });
        tblInvoice.setItems(filterInvoice);

        filterPembayaran = new FilteredList<>(daftarPembayaran, p -> true);
        txtcariByr.textProperty().addListener((obs, oldVal, newVal) -> {
            filterPembayaran.setPredicate(p -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String kw = newVal.toLowerCase();
                return p.getMetodeBayar().toLowerCase().contains(kw)
                        || (p.getCatatan() != null && p.getCatatan().toLowerCase().contains(kw));
            });
        });
        tblByr.setItems(filterPembayaran);
    }

    private void setupOrderListener() {
        cmborderInvoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && mapOrder.containsKey(newVal)) {
                hitungTotalDariOrder(mapOrder.get(newVal));
            } else {
                txttotalInvoice.clear();
            }
        });
    }

    private void hitungTotalDariOrder(String orderId) {
        String sql = "SELECT COALESCE(SUM(subtotal),0) AS total FROM `Detail_order` WHERE orderId=?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total");
                    txttotalInvoice.setText(formatAngka(total));
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal hitung total: " + e.getMessage());
        }
    }

    private void loadComboOrder() {
        mapOrder.clear();
        String sql = "SELECT o.id, o.status_order, o.tgl_order FROM `Orders` o "
                + "LEFT JOIN `Invoice` i ON i.orderId = o.id "
                + "WHERE i.id IS NULL ORDER BY o.created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String label = rs.getString("id") + " - " + rs.getString("status_order")
                        + " (" + rs.getString("tgl_order") + ")";
                mapOrder.put(label, rs.getString("id"));
            }
            cmborderInvoice.setItems(FXCollections.observableArrayList(mapOrder.keySet()));

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data order: " + e.getMessage());
        }
    }

    private void loadDataInvoice() {
        daftarInvoice.clear();
        String sql = "SELECT id, orderId, tgl_invoice, total_bayar, status_bayar FROM `Invoice` "
                + "ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarInvoice.add(new Invoice(
                        rs.getString("id"), rs.getString("orderId"), rs.getString("tgl_invoice"),
                        rs.getString("total_bayar"), rs.getString("status_bayar")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data invoice: " + e.getMessage());
        }
    }

    private void loadDataPembayaran(String invoiceId) {
        daftarPembayaran.clear();
        if (invoiceId == null) {
            return;
        }

        String sql = "SELECT id, invoiceId, tgl_bayar, jumlah_bayar, metode_bayar, catatan "
                + "FROM `Pembayaran` WHERE invoiceId=? ORDER BY tgl_bayar ASC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    daftarPembayaran.add(new Pembayaran(
                            rs.getString("id"), rs.getString("invoiceId"), rs.getString("tgl_bayar"),
                            rs.getString("jumlah_bayar"), rs.getString("metode_bayar"), rs.getString("catatan")
                    ));
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data pembayaran: " + e.getMessage());
        }
    }

    private void isiFormInvoice(Invoice i) {
        selectedInvoiceId = i.getId();
        activeInvoiceId = i.getId();

        cmborderInvoice.setValue(i.getOrderId());

        txttotalInvoice.setText(i.getTotalBayar());
        txttglInvoice.setValue(parseDateOrNull(i.getTglInvoice()));
        cmbstatusInvoice.setValue(i.getStatusBayar());

        btnsaveInvoice.setDisable(true);
        btnupdateInvoice.setDisable(false);
        btndeleteInvoice.setDisable(false);

        resetFormBayar();
        loadDataPembayaran(activeInvoiceId);
        setPembayaranButtons(true);
        tampilSisaBayar(i.getId(), Double.parseDouble(i.getTotalBayar()));
    }

    private void isiFormBayar(Pembayaran p) {
        selectedBayarId = p.getId();
        txttglByr.setValue(parseDateOrNull(p.getTglBayar()));
        txtjmlhByr.setText(p.getJumlahBayar());
        cmbmetodeByr.setValue(p.getMetodeBayar());
        txtcttnByr.setText(p.getCatatan());

        btntambahByr.setDisable(true);
        btnupdateByr.setDisable(false);
        btndeleteByr.setDisable(false);
    }

    private LocalDate parseDateOrNull(String date) {
        return (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
    }

    private void setComboByValue(ComboBox<String> combo, Map<String, String> map, String id) {
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(id)) {
                combo.setValue(entry.getKey());
                return;
            }
        }
    }

    private void setPembayaranButtons(boolean enabled) {
        btntambahByr.setDisable(!enabled);
        btnupdateByr.setDisable(true);
        btndeleteByr.setDisable(true);
    }

    @FXML
    private void btnsaveInvoice() {
        if (!validateFormInvoice()) {
            return;
        }

        String sql = "INSERT INTO `Invoice` (id, orderId, tgl_invoice, total_bayar, status_bayar) "
                + "VALUES (?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String newId = UUID.randomUUID().toString();
            String orderId = mapOrder.get(cmborderInvoice.getValue());

            stmt.setString(1, newId);
            stmt.setString(2, orderId);
            stmt.setString(3, txttglInvoice.getValue() != null ? txttglInvoice.getValue().toString() : null);
            stmt.setDouble(4, Double.parseDouble(txttotalInvoice.getText().trim()));
            stmt.setString(5, "Belum Bayar");
            stmt.executeUpdate();

            activeInvoiceId = newId;
            selectedInvoiceId = newId;

            showAlert(Alert.AlertType.INFORMATION, "Berhasil",
                    "Invoice berhasil disimpan. Silakan tambahkan pembayaran di bawah.");
            loadDataInvoice();
            loadComboOrder();
            daftarPembayaran.clear();
            setPembayaranButtons(true);

            btnsaveInvoice.setDisable(true);
            btnupdateInvoice.setDisable(false);
            btndeleteInvoice.setDisable(false);

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat Data", "Order ini sudah memiliki invoice.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    @FXML
    private void btnupdateInvoice() {
        if (selectedInvoiceId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih invoice yang ingin diubah.");
            return;
        }

        String sql = "UPDATE `Invoice` SET tgl_invoice=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txttglInvoice.getValue() != null ? txttglInvoice.getValue().toString() : null);
            stmt.setString(2, selectedInvoiceId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Tanggal invoice berhasil diperbarui.");
                loadDataInvoice();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        }
    }

    @FXML
    private void btndeleteInvoice() {
        if (selectedInvoiceId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih invoice yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Konfirmasi Hapus",
                "Yakin ingin menghapus invoice ini?\nSemua riwayat pembayaran terkait juga akan terhapus."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM `Invoice` WHERE id=?")) {

                stmt.setString(1, selectedInvoiceId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Invoice berhasil dihapus.");
                    resetFormInvoice();
                    loadDataInvoice();
                    loadComboOrder();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btntambahByr() {
        if (activeInvoiceId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih atau simpan Invoice terlebih dahulu.");
            return;
        }
        if (!validateFormBayar()) {
            return;
        }

        String sql = "INSERT INTO `Pembayaran` (id, invoiceId, tgl_bayar, jumlah_bayar, metode_bayar, catatan) "
                + "VALUES (?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, activeInvoiceId);
            stmt.setString(3, txttglByr.getValue().toString());
            stmt.setDouble(4, Double.parseDouble(txtjmlhByr.getText().trim()));
            stmt.setString(5, cmbmetodeByr.getValue());
            stmt.setString(6, getNullable(txtcttnByr.getText()));
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pembayaran berhasil ditambahkan.");
            resetFormBayar();
            loadDataPembayaran(activeInvoiceId);
            updateStatusInvoiceOtomatis(activeInvoiceId);
            loadDataInvoice();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menambah pembayaran: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Jumlah bayar tidak valid.");
        }
    }

    @FXML
    private void btnupdateByr() {
        if (selectedBayarId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih pembayaran yang ingin diubah.");
            return;
        }
        if (!validateFormBayar()) {
            return;
        }

        String sql = "UPDATE `Pembayaran` SET tgl_bayar=?, jumlah_bayar=?, metode_bayar=?, catatan=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txttglByr.getValue().toString());
            stmt.setDouble(2, Double.parseDouble(txtjmlhByr.getText().trim()));
            stmt.setString(3, cmbmetodeByr.getValue());
            stmt.setString(4, getNullable(txtcttnByr.getText()));
            stmt.setString(5, selectedBayarId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pembayaran berhasil diperbarui.");
                resetFormBayar();
                loadDataPembayaran(activeInvoiceId);
                updateStatusInvoiceOtomatis(activeInvoiceId);
                loadDataInvoice();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah pembayaran: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Jumlah bayar tidak valid.");
        }
    }

    @FXML
    private void btndeleteByr() {
        if (selectedBayarId == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih pembayaran yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm("Konfirmasi Hapus", "Yakin ingin menghapus riwayat pembayaran ini?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM `Pembayaran` WHERE id=?")) {

                stmt.setString(1, selectedBayarId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pembayaran berhasil dihapus.");
                    resetFormBayar();
                    loadDataPembayaran(activeInvoiceId);
                    updateStatusInvoiceOtomatis(activeInvoiceId);
                    loadDataInvoice();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus pembayaran: " + e.getMessage());
            }
        }
    }

    private void updateStatusInvoiceOtomatis(String invoiceId) {
        String sqlTotal = "SELECT total_bayar FROM `Invoice` WHERE id=?";
        String sqlSum = "SELECT COALESCE(SUM(jumlah_bayar),0) AS total_dibayar FROM `Pembayaran` WHERE invoiceId=?";
        String sqlUpdate = "UPDATE `Invoice` SET status_bayar=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            double totalBayar = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlTotal)) {
                stmt.setString(1, invoiceId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalBayar = rs.getDouble("total_bayar");
                    }
                }
            }

            double totalDibayar = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlSum)) {
                stmt.setString(1, invoiceId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalDibayar = rs.getDouble("total_dibayar");
                    }
                }
            }

            String statusBaru;
            if (totalDibayar <= 0) {
                statusBaru = "Belum Bayar";
            } else if (totalDibayar < totalBayar) {
                statusBaru = "Cicilan";
            } else {
                statusBaru = "Lunas";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, statusBaru);
                stmt.setString(2, invoiceId);
                stmt.executeUpdate();
            }

            cmbstatusInvoice.setValue(statusBaru);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal update status invoice: " + e.getMessage());
        }
    }

    @FXML
    private void btnresetInvoice() {
        resetFormInvoice();
    }

    private void resetFormInvoice() {
        selectedInvoiceId = null;
        activeInvoiceId = null;

        cmborderInvoice.setValue(null);
        txttotalInvoice.clear();
        txttglInvoice.setValue(null);
        cmbstatusInvoice.setValue(null);

        tblInvoice.getSelectionModel().clearSelection();
        daftarPembayaran.clear();

        btnsaveInvoice.setDisable(false);
        btnupdateInvoice.setDisable(true);
        btndeleteInvoice.setDisable(true);
        setPembayaranButtons(false);

        resetFormBayar();
    }

    private void resetFormBayar() {
        selectedBayarId = null;
        txttglByr.setValue(null);
        txtjmlhByr.clear();
        cmbmetodeByr.setValue(null);
        txtcttnByr.clear();

        tblByr.getSelectionModel().clearSelection();

        btntambahByr.setDisable(activeInvoiceId == null);
        btnupdateByr.setDisable(true);
        btndeleteByr.setDisable(true);
    }

    private boolean validateFormInvoice() {
        StringBuilder errors = new StringBuilder();
        if (cmborderInvoice.getValue() == null) {
            errors.append("• Order harus dipilih.\n");
        }
        if (txttglInvoice.getValue() == null) {
            errors.append("• Tgl Invoice harus diisi.\n");
        }

        if (txttotalInvoice.getText() == null || txttotalInvoice.getText().trim().isEmpty()
                || txttotalInvoice.getText().trim().equals("0.0")) {
            errors.append("• Order belum memiliki Detail Layanan (total = 0).\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", errors.toString());
            return false;
        }
        return true;
    }

    private boolean validateFormBayar() {
        StringBuilder errors = new StringBuilder();
        if (txttglByr.getValue() == null) {
            errors.append("• Tgl Bayar harus diisi.\n");
        }
        if (cmbmetodeByr.getValue() == null) {
            errors.append("• Metode Bayar harus dipilih.\n");
        }

        String jumlah = txtjmlhByr.getText().trim();
        if (jumlah.isEmpty()) {
            errors.append("• Jumlah Bayar harus diisi.\n");
        } else {
            try {
                double val = Double.parseDouble(jumlah);
                if (val <= 0) {
                    errors.append("• Jumlah Bayar harus lebih dari 0.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Jumlah Bayar harus berupa angka.\n");
            }
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

    private void tampilSisaBayar(String invoiceId, double totalBayar) {
        String sql = "SELECT COALESCE(SUM(jumlah_bayar),0) AS total_dibayar FROM `Pembayaran` WHERE invoiceId=?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalDibayar = rs.getDouble("total_dibayar");
                    double sisa = totalBayar - totalDibayar;

                    if (sisa <= 0) {
                        lblSisaBayar.setText("LUNAS - Sudah dibayar penuh");
                    } else {
                        lblSisaBayar.setText("Sisa Tagihan: Rp " + CURRENCY.format(sisa));
                    }
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal hitung sisa: " + e.getMessage());
        }
    }
}

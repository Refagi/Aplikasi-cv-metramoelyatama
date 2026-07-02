package controller.laporan;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import models.laporan.DetailPembelianRow;
import models.laporan.PembelianRow;
import utils.DBConnection;

public class LaporanPembelianController implements Initializable {

    @FXML private DatePicker       txtdariPembelian;
    @FXML private DatePicker       txtsampaiPembelian;
    @FXML private ComboBox<String> cmbsupplierFilterPembelian;
    @FXML private Button           btntampilkanPembelian;
    @FXML private Button           btnexportPembelian;

    @FXML private Label lblTotalPembelian;
    @FXML private Label lblTotalNilaiPembelian;

    @FXML private TableView<PembelianRow>           tblLaporanPembelian;
    @FXML private TableColumn<PembelianRow, String> clmnoPembelian;
    @FXML private TableColumn<PembelianRow, String> clmfakturPembelian;
    @FXML private TableColumn<PembelianRow, String> clmtglPembelian;
    @FXML private TableColumn<PembelianRow, String> clmsupplierPembelian;
    @FXML private TableColumn<PembelianRow, String> clmkaryawanPembelian;
    @FXML private TableColumn<PembelianRow, String> clmtotalPembelian;

    @FXML private TableView<DetailPembelianRow>           tblDetailLaporanPembelian;
    @FXML private TableColumn<DetailPembelianRow, String> clmnamaBarangDetail;
    @FXML private TableColumn<DetailPembelianRow, String> clmqtyDetail;
    @FXML private TableColumn<DetailPembelianRow, String> clmhargaDetail;
    @FXML private TableColumn<DetailPembelianRow, String> clmsubtotalDetail;

    private ObservableList<PembelianRow>       daftarLaporan       = FXCollections.observableArrayList();
    private ObservableList<DetailPembelianRow> daftarDetailLaporan = FXCollections.observableArrayList();

    private final Map<String, String> mapSupplier = new LinkedHashMap<>();
    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupTableClick();
        loadComboSupplier();

        txtdariPembelian.setValue(LocalDate.now().withDayOfMonth(1));
        txtsampaiPembelian.setValue(LocalDate.now());

        tampilkanData();
    }

    private void setupTableColumns() {
        clmnoPembelian.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        clmfakturPembelian.setCellValueFactory(new PropertyValueFactory<>("noFaktur"));
        clmtglPembelian.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        clmsupplierPembelian.setCellValueFactory(new PropertyValueFactory<>("namaSupplier"));
        clmkaryawanPembelian.setCellValueFactory(new PropertyValueFactory<>("namaKaryawan"));

        clmtotalPembelian.setCellValueFactory(new PropertyValueFactory<>("total"));
        clmtotalPembelian.setCellFactory(col -> currencyCellPembelian());

        clmnamaBarangDetail.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        clmqtyDetail.setCellValueFactory(new PropertyValueFactory<>("qty"));

        clmhargaDetail.setCellValueFactory(new PropertyValueFactory<>("harga"));
        clmhargaDetail.setCellFactory(col -> currencyCellDetail());

        clmsubtotalDetail.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        clmsubtotalDetail.setCellFactory(col -> currencyCellDetail());

        tblLaporanPembelian.setItems(daftarLaporan);
        tblDetailLaporanPembelian.setItems(daftarDetailLaporan);
    }

    private TableCell<PembelianRow, String> currencyCellPembelian() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                try { setText("Rp " + CURRENCY.format(Double.parseDouble(item))); }
                catch (NumberFormatException e) { setText(item); }
            }
        };
    }

    private TableCell<DetailPembelianRow, String> currencyCellDetail() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                try { setText("Rp " + CURRENCY.format(Double.parseDouble(item))); }
                catch (NumberFormatException e) { setText(item); }
            }
        };
    }

    private void setupTableClick() {
        tblLaporanPembelian.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) loadDetailPembelian(newVal.getId());
            });
    }

    private void loadComboSupplier() {
        mapSupplier.clear();
        cmbsupplierFilterPembelian.getItems().add("Semua Supplier");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nama FROM `Supplier` ORDER BY nama");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                mapSupplier.put(rs.getString("nama"), rs.getString("id"));
            }
            cmbsupplierFilterPembelian.getItems().addAll(mapSupplier.keySet());
            cmbsupplierFilterPembelian.setValue("Semua Supplier");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data supplier: " + e.getMessage());
        }
    }

    @FXML
    private void btntampilkanPembelian() {
        tampilkanData();
    }

    private void tampilkanData() {
        daftarLaporan.clear();
        daftarDetailLaporan.clear();

        if (txtdariPembelian.getValue() == null || txtsampaiPembelian.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih rentang tanggal terlebih dahulu.");
            return;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT p.id, p.no_faktur, p.tanggal, s.nama AS namaSupplier, " +
            "k.nama AS namaKaryawan, p.total " +
            "FROM `Pembelian` p " +
            "LEFT JOIN `Supplier` s ON s.id = p.supplierId " +
            "LEFT JOIN `Karyawan` k ON k.id = p.karyawanId " +
            "WHERE DATE(p.tanggal) BETWEEN ? AND ? "
        );

        String supplierFilter = cmbsupplierFilterPembelian.getValue();
        boolean adaFilterSupplier = supplierFilter != null && !supplierFilter.equals("Semua Supplier");
        if (adaFilterSupplier) {
            sql.append("AND p.supplierId = ? ");
        }

        sql.append("ORDER BY p.tanggal ASC");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, txtdariPembelian.getValue().toString());
            stmt.setString(2, txtsampaiPembelian.getValue().toString());
            if (adaFilterSupplier) {
                stmt.setString(3, mapSupplier.get(supplierFilter));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int no = 1;
                double totalSemua = 0;

                while (rs.next()) {
                    double total = rs.getDouble("total");
                    totalSemua += total;

                    daftarLaporan.add(new PembelianRow(
                        String.valueOf(no++),
                        rs.getString("id"),
                        rs.getString("no_faktur"),
                        rs.getString("tanggal"),
                        rs.getString("namaSupplier"),
                        rs.getString("namaKaryawan"),
                        formatAngka(total)
                    ));
                }

                lblTotalPembelian.setText("Total Transaksi: " + (no - 1));
                lblTotalNilaiPembelian.setText("Total Pengeluaran: Rp " + CURRENCY.format(totalSemua));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    /** Saat baris pembelian diklik, tampilkan rincian barang yang dibeli */
    private void loadDetailPembelian(String pembelianId) {
        daftarDetailLaporan.clear();

        String sql = "SELECT b.nama_barang, d.qty, d.harga, d.subtotal " +
                     "FROM `Detail_pembelian` d " +
                     "LEFT JOIN `Barang` b ON b.id = d.barangId " +
                     "WHERE d.pembelianId = ? ORDER BY b.nama_barang ASC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pembelianId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    daftarDetailLaporan.add(new DetailPembelianRow(
                        rs.getString("nama_barang"),
                        rs.getString("qty"),
                        formatAngka(rs.getDouble("harga")),
                        formatAngka(rs.getDouble("subtotal"))
                    ));
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat detail: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════
    //  EXPORT PDF (JasperReports)
    // ════════════════════════════════════════════════════════
    @FXML
    private void btnexportPembelian() {
        if (daftarLaporan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Tidak ada data untuk dicetak. Tampilkan data dahulu.");
            return;
        }

        // TODO: integrasi JasperReports (LaporanPembelian.jrxml)
        showAlert(Alert.AlertType.INFORMATION, "Info", "Export PDF akan diintegrasikan dengan JasperReports.");
    }

    private String formatAngka(double value) {
        if (value == Math.floor(value)) return String.valueOf((long) value);
        return String.valueOf(value);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
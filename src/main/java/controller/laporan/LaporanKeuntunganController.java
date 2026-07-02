package controller.laporan;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import models.KeuntunganRow;
import utils.DBConnection;

public class LaporanKeuntunganController implements Initializable {

    @FXML private DatePicker txtdariKeuntungan;
    @FXML private DatePicker txtsampaiKeuntungan;
    @FXML private Button     btntampilkanKeuntungan;
    @FXML private Button     btnexportKeuntungan;

    @FXML private Label lblTotalPemasukan;
    @FXML private Label lblTotalPengeluaran;
    @FXML private Label lblTotalKeuntungan;

    @FXML private TableView<KeuntunganRow>           tblLaporanKeuntungan;
    @FXML private TableColumn<KeuntunganRow, String> clmbulanKeuntungan;
    @FXML private TableColumn<KeuntunganRow, String> clmpemasukanKeuntungan;
    @FXML private TableColumn<KeuntunganRow, String> clmpengeluaranKeuntungan;
    @FXML private TableColumn<KeuntunganRow, String> clmkeuntunganKeuntungan;

    private ObservableList<KeuntunganRow> daftarLaporan = FXCollections.observableArrayList();
    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("id", "ID"));
    private static final Locale LOCALE_ID = new Locale("id", "ID");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();

        // Default: 6 bulan terakhir
        txtdariKeuntungan.setValue(LocalDate.now().minusMonths(5).withDayOfMonth(1));
        txtsampaiKeuntungan.setValue(LocalDate.now());

        tampilkanData();
    }

    private void setupTableColumns() {
        clmbulanKeuntungan.setCellValueFactory(new PropertyValueFactory<>("bulan"));

        clmpemasukanKeuntungan.setCellValueFactory(new PropertyValueFactory<>("pemasukan"));
        clmpemasukanKeuntungan.setCellFactory(col -> currencyCell("#1b7d3c"));

        clmpengeluaranKeuntungan.setCellValueFactory(new PropertyValueFactory<>("pengeluaran"));
        clmpengeluaranKeuntungan.setCellFactory(col -> currencyCell("#ba1a1a"));

        clmkeuntunganKeuntungan.setCellValueFactory(new PropertyValueFactory<>("keuntungan"));
        clmkeuntunganKeuntungan.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                try {
                    double val = Double.parseDouble(item);
                    setText("Rp " + CURRENCY.format(val));
                    // Hijau kalau untung, merah kalau rugi
                    setStyle(val >= 0
                        ? "-fx-text-fill: #1b7d3c; -fx-font-weight: bold;"
                        : "-fx-text-fill: #ba1a1a; -fx-font-weight: bold;");
                } catch (NumberFormatException e) {
                    setText(item);
                }
            }
        });

        tblLaporanKeuntungan.setItems(daftarLaporan);
    }

    private TableCell<KeuntunganRow, String> currencyCell(String warna) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                try {
                    setText("Rp " + CURRENCY.format(Double.parseDouble(item)));
                    setStyle("-fx-text-fill: " + warna + ";");
                } catch (NumberFormatException e) {
                    setText(item);
                }
            }
        };
    }

    @FXML
    private void btntampilkanKeuntungan() {
        tampilkanData();
    }

    private void tampilkanData() {
        daftarLaporan.clear();

        LocalDate dari   = txtdariKeuntungan.getValue();
        LocalDate sampai = txtsampaiKeuntungan.getValue();

        if (dari == null || sampai == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih rentang tanggal terlebih dahulu.");
            return;
        }

        if (dari.isAfter(sampai)) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Tgl Mulai tidak boleh setelah Tgl Selesai.");
            return;
        }

        List<YearMonth> daftarBulan = getRentangBulan(dari, sampai);

        double totalPemasukanSemua   = 0;
        double totalPengeluaranSemua = 0;

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            for (YearMonth ym : daftarBulan) {
                LocalDate awalBulan  = ym.atDay(1);
                LocalDate akhirBulan = ym.atEndOfMonth();

                double pemasukan   = hitungPemasukan(conn, awalBulan, akhirBulan);
                double pengeluaran = hitungPengeluaran(conn, awalBulan, akhirBulan);
                double keuntungan  = pemasukan - pengeluaran;

                totalPemasukanSemua   += pemasukan;
                totalPengeluaranSemua += pengeluaran;

                String namaBulan = ym.getMonth().getDisplayName(TextStyle.FULL, LOCALE_ID) + " " + ym.getYear();

                daftarLaporan.add(new KeuntunganRow(
                    namaBulan,
                    formatAngka(pemasukan),
                    formatAngka(pengeluaran),
                    formatAngka(keuntungan)
                ));
            }

            double totalKeuntunganSemua = totalPemasukanSemua - totalPengeluaranSemua;

            lblTotalPemasukan.setText("Total Pemasukan: Rp " + CURRENCY.format(totalPemasukanSemua));
            lblTotalPengeluaran.setText("Total Pengeluaran: Rp " + CURRENCY.format(totalPengeluaranSemua));
            lblTotalKeuntungan.setText("Keuntungan: Rp " + CURRENCY.format(totalKeuntunganSemua));
            lblTotalKeuntungan.setTextFill(totalKeuntunganSemua >= 0
                ? javafx.scene.paint.Color.web("#1b7d3c")
                : javafx.scene.paint.Color.web("#ba1a1a"));

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    private double hitungPemasukan(Connection conn, LocalDate dari, LocalDate sampai) throws SQLException {
        String sql = "SELECT COALESCE(SUM(jumlah_bayar),0) AS total FROM `Pembayaran` " +
                     "WHERE tgl_bayar BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dari.toString());
            stmt.setString(2, sampai.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0;
            }
        }
    }

    private double hitungPengeluaran(Connection conn, LocalDate dari, LocalDate sampai) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total),0) AS total FROM `Pembelian` " +
                     "WHERE DATE(tanggal) BETWEEN ? AND ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dari.toString());
            stmt.setString(2, sampai.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0;
            }
        }
    }

    /** Generate list YearMonth dari rentang tanggal dipilih, contoh: Jan 2026, Feb 2026, ... */
    private List<YearMonth> getRentangBulan(LocalDate dari, LocalDate sampai) {
        List<YearMonth> hasil = new ArrayList<>();
        YearMonth mulai  = YearMonth.from(dari);
        YearMonth akhir  = YearMonth.from(sampai);

        YearMonth current = mulai;
        while (!current.isAfter(akhir)) {
            hasil.add(current);
            current = current.plusMonths(1);
        }
        return hasil;
    }

    // ════════════════════════════════════════════════════════
    //  EXPORT PDF (JasperReports)
    // ════════════════════════════════════════════════════════
    @FXML
    private void btnexportKeuntungan() {
        if (daftarLaporan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Tidak ada data untuk dicetak. Tampilkan data dahulu.");
            return;
        }

        // TODO: integrasi JasperReports (LaporanKeuntungan.jrxml)
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
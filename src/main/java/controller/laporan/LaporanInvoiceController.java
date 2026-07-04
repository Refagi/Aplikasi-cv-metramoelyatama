package controller.laporan;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.InputStream;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import java.util.HashMap;
import java.util.Map;

import models.laporan.InvoiceRow;
import utils.DBConnection;

public class LaporanInvoiceController implements Initializable {

    @FXML private DatePicker       txtdariInvoice;
    @FXML private DatePicker       txtsampaiInvoice;
    @FXML private ComboBox<String> cmbstatusFilterInvoice;
    @FXML private Button           btntampilkanInvoice;
    @FXML private Button           btnexportInvoice;

    @FXML private Label lblTotalInvoice;
    @FXML private Label lblTotalTagihan;
    @FXML private Label lblTotalTerbayar;
    @FXML private Label lblTotalSisa;

    @FXML private TableView<InvoiceRow>           tblLaporanInvoice;
    @FXML private TableColumn<InvoiceRow, String> clmnoInvoice;
    @FXML private TableColumn<InvoiceRow, String> clmclientInvoice;
    @FXML private TableColumn<InvoiceRow, String> clmtglInvoice;
    @FXML private TableColumn<InvoiceRow, String> clmtotalInvoice;
    @FXML private TableColumn<InvoiceRow, String> clmdibayarInvoice;
    @FXML private TableColumn<InvoiceRow, String> clmsisaInvoice;
    @FXML private TableColumn<InvoiceRow, String> clmstatusInvoice;

    private ObservableList<InvoiceRow> daftarLaporan = FXCollections.observableArrayList();
    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableColumns();

        txtdariInvoice.setValue(LocalDate.now().withDayOfMonth(1));
        txtsampaiInvoice.setValue(LocalDate.now());

        tampilkanData();
    }

    private void setupComboBox() {
        cmbstatusFilterInvoice.setItems(FXCollections.observableArrayList(
            "Semua Status", "Belum Bayar", "Cicilan", "Lunas"
        ));
        cmbstatusFilterInvoice.setValue("Semua Status");
    }

    private void setupTableColumns() {
        clmnoInvoice.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        clmclientInvoice.setCellValueFactory(new PropertyValueFactory<>("namaClient"));
        clmtglInvoice.setCellValueFactory(new PropertyValueFactory<>("tglInvoice"));
        clmstatusInvoice.setCellValueFactory(new PropertyValueFactory<>("status"));

        clmtotalInvoice.setCellValueFactory(new PropertyValueFactory<>("totalTagihan"));
        clmtotalInvoice.setCellFactory(col -> currencyCell());

        clmdibayarInvoice.setCellValueFactory(new PropertyValueFactory<>("sudahDibayar"));
        clmdibayarInvoice.setCellFactory(col -> currencyCell());

        clmsisaInvoice.setCellValueFactory(new PropertyValueFactory<>("sisaTagihan"));
        clmsisaInvoice.setCellFactory(col -> currencyCell());

        clmstatusInvoice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "Lunas" -> setStyle("-fx-text-fill: #1b7d3c; -fx-font-weight: bold;");
                    case "Cicilan" -> setStyle("-fx-text-fill: #c08a00; -fx-font-weight: bold;");
                    case "Belum Bayar" -> setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        tblLaporanInvoice.setItems(daftarLaporan);
    }

    private TableCell<InvoiceRow, String> currencyCell() {
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

    @FXML
    private void btntampilkanInvoice() {
        tampilkanData();
    }

    private void tampilkanData() {
        daftarLaporan.clear();

        if (txtdariInvoice.getValue() == null || txtsampaiInvoice.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih rentang tanggal terlebih dahulu.");
            return;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT i.id, c.nama AS namaClient, i.tgl_invoice, i.total_bayar, i.status_bayar, " +
            "COALESCE(SUM(p.jumlah_bayar),0) AS totalDibayar " +
            "FROM `Invoice` i " +
            "LEFT JOIN `Orders` o ON o.id = i.orderId " +
            "LEFT JOIN `Client` c ON c.id = o.clientId " +
            "LEFT JOIN `Pembayaran` p ON p.invoiceId = i.id " +
            "WHERE i.tgl_invoice BETWEEN ? AND ? "
        );

        String statusFilter = cmbstatusFilterInvoice.getValue();
        boolean adaFilterStatus = statusFilter != null && !statusFilter.equals("Semua Status");
        if (adaFilterStatus) {
            sql.append("AND i.status_bayar = ? ");
        }

        sql.append("GROUP BY i.id ORDER BY i.tgl_invoice ASC");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, txtdariInvoice.getValue().toString());
            stmt.setString(2, txtsampaiInvoice.getValue().toString());
            if (adaFilterStatus) {
                stmt.setString(3, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int no = 1;
                double totalSemuaTagihan = 0;
                double totalSemuaDibayar = 0;

                while (rs.next()) {
                    double totalTagihan = rs.getDouble("total_bayar");
                    double totalDibayar = rs.getDouble("totalDibayar");
                    double sisa         = totalTagihan - totalDibayar;

                    totalSemuaTagihan += totalTagihan;
                    totalSemuaDibayar += totalDibayar;

                    daftarLaporan.add(new InvoiceRow(
                        String.valueOf(no++),
                        rs.getString("namaClient"),
                        rs.getString("tgl_invoice"),
                        formatAngka(totalTagihan),
                        formatAngka(totalDibayar),
                        formatAngka(sisa),
                        rs.getString("status_bayar")
                    ));
                }

                lblTotalInvoice.setText("Total Invoice: " + (no - 1));
                lblTotalTagihan.setText("Total Tagihan: Rp " + CURRENCY.format(totalSemuaTagihan));
                lblTotalTerbayar.setText("Sudah Dibayar: Rp " + CURRENCY.format(totalSemuaDibayar));
                lblTotalSisa.setText("Sisa Tagihan: Rp " + CURRENCY.format(totalSemuaTagihan - totalSemuaDibayar));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    @FXML
    private void btnexportInvoice() {
        if (daftarLaporan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Tidak ada data untuk dicetak. Tampilkan data dahulu.");
            return;
        }
        
        String reportPath = "/reports/LaporanInvoice.jasper";

        try (InputStream reportStream = getClass().getResourceAsStream(reportPath)) {
            if (reportStream == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "File template laporan tidak ditemukan di path: " + reportPath);
                return;
            }

            Map<String, Object> params = new HashMap<>();
            
            params.put("tgl_dari", txtdariInvoice.getValue() != null ? txtdariInvoice.getValue().toString() : "");
            params.put("tgl_sampai", txtsampaiInvoice.getValue() != null ? txtsampaiInvoice.getValue().toString() : "");
            
            String statusFilter = cmbstatusFilterInvoice.getValue();
            params.put("status_filter", statusFilter != null ? statusFilter : "Semua Status");

            Connection conn = DBConnection.getInstance().getConnection();

            JasperPrint print = JasperFillManager.fillReport(reportStream, params, conn);

            JasperViewer.viewReport(print, false);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal export laporan invoice: " + e.getMessage());
        }
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
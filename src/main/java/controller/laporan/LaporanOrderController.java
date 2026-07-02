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

import models.laporan.OrderRow;
import utils.DBConnection;

public class LaporanOrderController implements Initializable {

    @FXML private DatePicker       txtdariOrder;
    @FXML private DatePicker       txtsampaiOrder;
    @FXML private ComboBox<String> cmbstatusFilterOrder;
    @FXML private Button           btntampilkanOrder;
    @FXML private Button           btnexportOrder;

    @FXML private Label lblTotalOrder;
    @FXML private Label lblTotalNilaiOrder;

    @FXML private TableView<OrderRow>           tblLaporanOrder;
    @FXML private TableColumn<OrderRow, String> clmnoOrder;
    @FXML private TableColumn<OrderRow, String> clmidOrder;
    @FXML private TableColumn<OrderRow, String> clmclientOrder;
    @FXML private TableColumn<OrderRow, String> clmkaryawanOrder;
    @FXML private TableColumn<OrderRow, String> clmtglOrder;
    @FXML private TableColumn<OrderRow, String> clmstatusOrder;
    @FXML private TableColumn<OrderRow, String> clmtotalOrder;

    private ObservableList<OrderRow> daftarLaporan = FXCollections.observableArrayList();
    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableColumns();

        txtdariOrder.setValue(LocalDate.now().withDayOfMonth(1));
        txtsampaiOrder.setValue(LocalDate.now());

        tampilkanData();
    }

    private void setupComboBox() {
        cmbstatusFilterOrder.setItems(FXCollections.observableArrayList(
            "Semua Status", "Pending", "Proses", "Selesai", "Batal"
        ));
        cmbstatusFilterOrder.setValue("Semua Status");
    }

    private void setupTableColumns() {
        clmnoOrder.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        clmidOrder.setCellValueFactory(new PropertyValueFactory<>("id"));
        clmclientOrder.setCellValueFactory(new PropertyValueFactory<>("namaClient"));
        clmkaryawanOrder.setCellValueFactory(new PropertyValueFactory<>("namaKaryawan"));
        clmtglOrder.setCellValueFactory(new PropertyValueFactory<>("tglOrder"));
        clmstatusOrder.setCellValueFactory(new PropertyValueFactory<>("status"));

        clmtotalOrder.setCellValueFactory(new PropertyValueFactory<>("totalNilai"));
        clmtotalOrder.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                try { setText("Rp " + CURRENCY.format(Double.parseDouble(item))); }
                catch (NumberFormatException e) { setText(item); }
            }
        });

        tblLaporanOrder.setItems(daftarLaporan);
    }

    @FXML
    private void btntampilkanOrder() {
        tampilkanData();
    }

    private void tampilkanData() {
        daftarLaporan.clear();

        if (txtdariOrder.getValue() == null || txtsampaiOrder.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih rentang tanggal terlebih dahulu.");
            return;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT o.id, c.nama AS namaClient, k.nama AS namaKaryawan, " +
            "o.tgl_order, o.status_order, " +
            "COALESCE(SUM(d.subtotal),0) AS totalNilai " +
            "FROM `Orders` o " +
            "LEFT JOIN `Client` c ON c.id = o.clientId " +
            "LEFT JOIN `Karyawan` k ON k.id = o.karyawanId " +
            "LEFT JOIN `Detail_order` d ON d.orderId = o.id " +
            "WHERE o.tgl_order BETWEEN ? AND ? "
        );

        String statusFilter = cmbstatusFilterOrder.getValue();
        boolean adaFilterStatus = statusFilter != null && !statusFilter.equals("Semua Status");
        if (adaFilterStatus) {
            sql.append("AND o.status_order = ? ");
        }

        sql.append("GROUP BY o.id ORDER BY o.tgl_order ASC");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, txtdariOrder.getValue().toString());
            stmt.setString(2, txtsampaiOrder.getValue().toString());
            if (adaFilterStatus) {
                stmt.setString(3, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int no = 1;
                double totalSemua = 0;

                while (rs.next()) {
                    double nilai = rs.getDouble("totalNilai");
                    totalSemua += nilai;

                    daftarLaporan.add(new OrderRow(
                        String.valueOf(no++),
                        rs.getString("id"),
                        rs.getString("namaClient"),
                        rs.getString("namaKaryawan"),
                        rs.getString("tgl_order"),
                        rs.getString("status_order"),
                        formatAngka(nilai)
                    ));
                }

                lblTotalOrder.setText("Total Order: " + (no - 1));
                lblTotalNilaiOrder.setText("Total Nilai: Rp " + CURRENCY.format(totalSemua));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════
    //  EXPORT PDF (JasperReports)
    // ════════════════════════════════════════════════════════
    @FXML
    private void btnexportOrder() {
        if (daftarLaporan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Tidak ada data untuk dicetak. Tampilkan data dahulu.");
            return;
        }

        // TODO: panggil JasperReports di sini, contoh kerangka:
        //
        // try {
        //     Map<String, Object> params = new HashMap<>();
        //     params.put("tglMulai", txtdariOrder.getValue().toString());
        //     params.put("tglSelesai", txtsampaiOrder.getValue().toString());
        //
        //     JasperReport report = JasperCompileManager.compileReport(
        //         getClass().getResourceAsStream("/reports/LaporanOrder.jrxml"));
        //
        //     JasperPrint print = JasperFillManager.fillReport(
        //         report, params, new JRBeanCollectionDataSource(daftarLaporan));
        //
        //     JasperExportManager.exportReportToPdfFile(print, "LaporanOrder.pdf");
        //
        //     showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Laporan berhasil diexport.");
        // } catch (Exception e) {
        //     showAlert(Alert.AlertType.ERROR, "Error", "Gagal export: " + e.getMessage());
        // }

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
package controller.laporan;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import models.laporan.PengerjaanRow;
import utils.DBConnection;

public class LaporanPengerjaanController implements Initializable {

    @FXML private DatePicker       txtdariPengerjaan;
    @FXML private DatePicker       txtsampaiPengerjaan;
    @FXML private ComboBox<String> cmbstatusFilterPengerjaan;
    @FXML private Button           btntampilkanPengerjaan;
    @FXML private Button           btnexportPengerjaan;

    @FXML private Label lblTotalPengerjaan;
    @FXML private Label lblSelesaiPengerjaan;
    @FXML private Label lblProgressPengerjaan;
    @FXML private Label lblBelumPengerjaan;

    @FXML private TableView<PengerjaanRow>           tblLaporanPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmnoPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmclientPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmkaryawanPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmmulaiPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmselesaiPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmdurasiPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmstatusPengerjaan;
    @FXML private TableColumn<PengerjaanRow, String> clmcatatanPengerjaan;

    private ObservableList<PengerjaanRow> daftarLaporan = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBox();
        setupTableColumns();

        txtdariPengerjaan.setValue(LocalDate.now().withDayOfMonth(1));
        txtsampaiPengerjaan.setValue(LocalDate.now());

        tampilkanData();
    }

    private void setupComboBox() {
        cmbstatusFilterPengerjaan.setItems(FXCollections.observableArrayList(
            "Semua Status", "Belum Mulai", "On Progress", "Selesai"
        ));
        cmbstatusFilterPengerjaan.setValue("Semua Status");
    }

    private void setupTableColumns() {
        clmnoPengerjaan.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        clmclientPengerjaan.setCellValueFactory(new PropertyValueFactory<>("namaClient"));
        clmkaryawanPengerjaan.setCellValueFactory(new PropertyValueFactory<>("namaKaryawan"));
        clmmulaiPengerjaan.setCellValueFactory(new PropertyValueFactory<>("tglMulai"));
        clmselesaiPengerjaan.setCellValueFactory(new PropertyValueFactory<>("tglSelesai"));
        clmdurasiPengerjaan.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        clmstatusPengerjaan.setCellValueFactory(new PropertyValueFactory<>("status"));
        clmcatatanPengerjaan.setCellValueFactory(new PropertyValueFactory<>("catatan"));

        tblLaporanPengerjaan.setItems(daftarLaporan);
    }

    @FXML
    private void btntampilkanPengerjaan() {
        tampilkanData();
    }

    private void tampilkanData() {
        daftarLaporan.clear();

        if (txtdariPengerjaan.getValue() == null || txtsampaiPengerjaan.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih rentang tanggal terlebih dahulu.");
            return;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT c.nama AS namaClient, k.nama AS namaKaryawan, " +
            "p.tgl_mulai, p.tgl_selesai, p.status, p.catatan " +
            "FROM `Pengerjaan` p " +
            "LEFT JOIN `Orders` o ON o.id = p.orderId " +
            "LEFT JOIN `Client` c ON c.id = o.clientId " +
            "LEFT JOIN `Karyawan` k ON k.id = p.karyawanId " +
            "WHERE p.tgl_mulai BETWEEN ? AND ? "
        );

        String statusFilter = cmbstatusFilterPengerjaan.getValue();
        boolean adaFilterStatus = statusFilter != null && !statusFilter.equals("Semua Status");
        if (adaFilterStatus) {
            sql.append("AND p.status = ? ");
        }

        sql.append("ORDER BY p.tgl_mulai ASC");

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, txtdariPengerjaan.getValue().toString());
            stmt.setString(2, txtsampaiPengerjaan.getValue().toString());
            if (adaFilterStatus) {
                stmt.setString(3, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int no = 1;
                int countSelesai = 0, countProgress = 0, countBelum = 0;

                while (rs.next()) {
                    String tglMulai   = rs.getString("tgl_mulai");
                    String tglSelesai = rs.getString("tgl_selesai");
                    String status     = rs.getString("status");

                    String durasi = hitungDurasi(tglMulai, tglSelesai);

                    switch (status) {
                        case "Selesai" -> countSelesai++;
                        case "On Progress" -> countProgress++;
                        case "Belum Mulai" -> countBelum++;
                    }

                    daftarLaporan.add(new PengerjaanRow(
                        String.valueOf(no++),
                        rs.getString("namaClient"),
                        rs.getString("namaKaryawan"),
                        tglMulai,
                        tglSelesai,
                        durasi,
                        status,
                        rs.getString("catatan")
                    ));
                }

                lblTotalPengerjaan.setText("Total Pengerjaan: " + (no - 1));
                lblSelesaiPengerjaan.setText("Selesai: " + countSelesai);
                lblProgressPengerjaan.setText("On Progress: " + countProgress);
                lblBelumPengerjaan.setText("Belum Mulai: " + countBelum);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    private String hitungDurasi(String tglMulai, String tglSelesai) {
        if (tglMulai == null || tglMulai.isEmpty()) return "-";
        if (tglSelesai == null || tglSelesai.isEmpty()) return "Berjalan";

        try {
            LocalDate mulai   = LocalDate.parse(tglMulai);
            LocalDate selesai = LocalDate.parse(tglSelesai);
            long hari = ChronoUnit.DAYS.between(mulai, selesai);
            return hari + " hari";
        } catch (Exception e) {
            return "-";
        }
    }

    // ════════════════════════════════════════════════════════
    //  EXPORT PDF (JasperReports)
    // ════════════════════════════════════════════════════════
    @FXML
    private void btnexportPengerjaan() {
        if (daftarLaporan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Tidak ada data untuk dicetak. Tampilkan data dahulu.");
            return;
        }

        // TODO: integrasi JasperReports (LaporanPengerjaan.jrxml)
        showAlert(Alert.AlertType.INFORMATION, "Info", "Export PDF akan diintegrasikan dengan JasperReports.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
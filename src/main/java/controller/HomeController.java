package controller;

import java.net.URL;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import utils.DBConnection;

public class HomeController implements Initializable {

    @FXML private Label dataPenjualan;
    @FXML private Label dataStokBarang;
    @FXML private Label dataKeuntungan;

    @FXML private LineChart<String, Number> keuntunganChart;
    @FXML private BarChart<String, Number>  ordersChart;

    private static final NumberFormat CURRENCY = NumberFormat.getInstance(new Locale("id", "ID"));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCardPemasukan();
        loadCardStokBarang();
        loadCardKeuntungan();
        loadChartKeuntungan();
        loadChartOrders();
    }

    private void loadCardPemasukan() {
        LocalDate awal   = LocalDate.now().withDayOfMonth(1);
        LocalDate akhir  = LocalDate.now();

        String sql = "SELECT COALESCE(SUM(jumlah_bayar),0) AS total FROM `Pembayaran` " +
                     "WHERE tgl_bayar BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, awal.toString());
            stmt.setString(2, akhir.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total");
                    dataPenjualan.setText("Rp " + CURRENCY.format(total));
                }
            }
        } catch (SQLException e) {
            dataPenjualan.setText("Rp -");
        }
    }

    private void loadCardStokBarang() {
        String sql = "SELECT COALESCE(SUM(stok),0) AS total FROM `Barang`";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                long total = rs.getLong("total");
                dataStokBarang.setText(total + " Units");
            }
        } catch (SQLException e) {
            dataStokBarang.setText("- Units");
        }
    }


    private void loadCardKeuntungan() {
        LocalDate awal  = LocalDate.now().withDayOfMonth(1);
        LocalDate akhir = LocalDate.now();

        String sqlPemasukan   = "SELECT COALESCE(SUM(jumlah_bayar),0) AS total FROM `Pembayaran` " +
                                "WHERE tgl_bayar BETWEEN ? AND ?";
        String sqlPengeluaran = "SELECT COALESCE(SUM(total),0) AS total FROM `Pembelian` " +
                                "WHERE DATE(tanggal) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            double pemasukan = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlPemasukan)) {
                stmt.setString(1, awal.toString());
                stmt.setString(2, akhir.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) pemasukan = rs.getDouble("total");
                }
            }

            double pengeluaran = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlPengeluaran)) {
                stmt.setString(1, awal.toString());
                stmt.setString(2, akhir.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) pengeluaran = rs.getDouble("total");
                }
            }

            double keuntungan = pemasukan - pengeluaran;
            dataKeuntungan.setText("Rp " + CURRENCY.format(keuntungan));

            if (keuntungan < 0) {
                dataKeuntungan.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight: bold;");
            } else {
                dataKeuntungan.setStyle("-fx-text-fill: #1b7d3c; -fx-font-weight: bold;");
            }

        } catch (SQLException e) {
            dataKeuntungan.setText("Rp -");
        }
    }

    private void loadChartKeuntungan() {
        keuntunganChart.getData().clear();
        keuntunganChart.setAnimated(false);

        XYChart.Series<String, Number> seriesPemasukan   = new XYChart.Series<>();
        XYChart.Series<String, Number> seriesPengeluaran = new XYChart.Series<>();
        XYChart.Series<String, Number> seriesKeuntungan  = new XYChart.Series<>();

        seriesPemasukan.setName("Pemasukan");
        seriesPengeluaran.setName("Pengeluaran");
        seriesKeuntungan.setName("Keuntungan");

        String sqlPemasukan = "SELECT DATE_FORMAT(tgl_bayar, '%b %Y') AS bulan, " +
                              "COALESCE(SUM(jumlah_bayar),0) AS total " +
                              "FROM `Pembayaran` " +
                              "WHERE tgl_bayar >= DATE_SUB(CURDATE(), INTERVAL 11 MONTH) " +
                              "GROUP BY DATE_FORMAT(tgl_bayar, '%Y-%m') " +
                              "ORDER BY MIN(tgl_bayar) ASC";

        String sqlPengeluaran = "SELECT DATE_FORMAT(tanggal, '%b %Y') AS bulan, " +
                                "COALESCE(SUM(total),0) AS total " +
                                "FROM `Pembelian` " +
                                "WHERE DATE(tanggal) >= DATE_SUB(CURDATE(), INTERVAL 11 MONTH) " +
                                "GROUP BY DATE_FORMAT(tanggal, '%Y-%m') " +
                                "ORDER BY MIN(tanggal) ASC";

        try (Connection conn = DBConnection.getInstance().getConnection()) {

            // Ambil pemasukan per bulan
            java.util.Map<String, Double> mapPemasukan = new java.util.LinkedHashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement(sqlPemasukan);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapPemasukan.put(rs.getString("bulan"), rs.getDouble("total"));
                }
            }

            // Ambil pengeluaran per bulan
            java.util.Map<String, Double> mapPengeluaran = new java.util.LinkedHashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement(sqlPengeluaran);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mapPengeluaran.put(rs.getString("bulan"), rs.getDouble("total"));
                }
            }

            // Gabungkan semua bulan yang ada di kedua map
            java.util.Set<String> semuaBulan = new java.util.LinkedHashSet<>();
            semuaBulan.addAll(mapPemasukan.keySet());
            semuaBulan.addAll(mapPengeluaran.keySet());

            for (String bulan : semuaBulan) {
                double masuk  = mapPemasukan.getOrDefault(bulan, 0.0);
                double keluar = mapPengeluaran.getOrDefault(bulan, 0.0);
                double untung = masuk - keluar;

                seriesPemasukan.getData().add(new XYChart.Data<>(bulan, masuk));
                seriesPengeluaran.getData().add(new XYChart.Data<>(bulan, keluar));
                seriesKeuntungan.getData().add(new XYChart.Data<>(bulan, untung));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        keuntunganChart.getData().addAll(seriesPemasukan, seriesPengeluaran, seriesKeuntungan);
    }

    private void loadChartOrders() {
        ordersChart.getData().clear();
        ordersChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Jumlah Order");

        String sql = "SELECT status_order, COUNT(*) AS jumlah FROM `Orders` " +
                     "WHERE tgl_order BETWEEN ? AND ? " +
                     "GROUP BY status_order";

        LocalDate awal  = LocalDate.now().withDayOfMonth(1);
        LocalDate akhir = LocalDate.now();

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, awal.toString());
            stmt.setString(2, akhir.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    series.getData().add(new XYChart.Data<>(
                        rs.getString("status_order"),
                        rs.getInt("jumlah")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ordersChart.getData().add(series);
    }
}
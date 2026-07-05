package controller;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import models.Barang;
import models.Supplier;
import utils.DBConnection;

public class BarangController implements Initializable {

    @FXML private TextField              txtkodeBarang;
    @FXML private TextField              txtnamaBarang;
    @FXML private TextArea               txtdescBarang;
    @FXML private ComboBox<Supplier>     cmbsuppBarang;
    @FXML private TextField              txthbBarang;
    @FXML private TextField              txthjBarang;
    @FXML private ComboBox<String>       cmbstokBarang;
    @FXML private ComboBox<String>       cmbsatuanBarang;

    @FXML private Button btnsaveBarang;
    @FXML private Button btnupdateBarang;
    @FXML private Button btndeleteBarang;
    @FXML private Button btnresetBarang;

    @FXML private TableView<Barang>           tblBarang;
    @FXML private TableColumn<Barang, String> clmkdBarang;
    @FXML private TableColumn<Barang, String> clmnamaBarang;
    @FXML private TableColumn<Barang, String> clmdescBarang;
    @FXML private TableColumn<Barang, String> clmhbBarang;
    @FXML private TableColumn<Barang, String> clmhjBarang;
    @FXML private TableColumn<Barang, String> clmtstokBarang;
    @FXML private TableColumn<Barang, String> clmsatuanBarang;

    @FXML private TextField txtcariBarang;

    private final ObservableList<Barang>    daftarBarang   = FXCollections.observableArrayList();
    private final ObservableList<Supplier>  daftarSupplier = FXCollections.observableArrayList();
    private FilteredList<Barang>            filterBarang;
    private String                          selectedKode   = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupSatuan();
        setupSupplierComboBox();
        loadSupplier();
        setupTableColumns();
        setupTableClick();
        setupSearch();
        loadData();

        btnupdateBarang.setDisable(true);
        btndeleteBarang.setDisable(true);
    }

    public void onOpen() {
        loadSupplier();
        loadData();
    }


    private void setupSatuan() {
        cmbsatuanBarang.setItems(FXCollections.observableArrayList(
            "Pax", "Pcs", "Box", "Kg", "Liter", "Unit", "Sak", "m3", "m2", "Paket", "Set"
        ));

        cmbstokBarang.setItems(FXCollections.observableArrayList(
            "0","5","10","25","50","100","150","200","250","500"
        ));
        cmbstokBarang.setEditable(true);
    }


    private void setupSupplierComboBox() {
        cmbsuppBarang.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier s) {
                return s != null ? s.getNama() : "";
            }
            @Override
            public Supplier fromString(String string) {
                return null;
            }
        });
    }

    private void loadSupplier() {
        daftarSupplier.clear();
        String sql = "SELECT id, nama, email, alamat, no_telp FROM `Supplier` ORDER BY nama ASC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarSupplier.add(new Supplier(
                    rs.getString("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("alamat"),
                    rs.getString("no_telp")
                ));
            }
            cmbsuppBarang.setItems(daftarSupplier);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data supplier: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        clmkdBarang.setCellValueFactory(new PropertyValueFactory<>("kodeBarang"));
        clmnamaBarang.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        clmdescBarang.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        clmhbBarang.setCellValueFactory(new PropertyValueFactory<>("hargaBeli"));
        clmhjBarang.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        clmtstokBarang.setCellValueFactory(new PropertyValueFactory<>("stok"));
        clmsatuanBarang.setCellValueFactory(new PropertyValueFactory<>("satuan"));
    }

    private void setupTableClick() {
        tblBarang.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) isiDataForm(newVal);
            });
    }

    private void setupSearch() {
        filterBarang = new FilteredList<>(daftarBarang, p -> true);

        txtcariBarang.textProperty().addListener((obs, oldVal, newVal) -> {
            filterBarang.setPredicate(b -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String kw = newVal.toLowerCase();
                return b.getKodeBarang().toLowerCase().contains(kw)
                    || b.getNamaBarang().toLowerCase().contains(kw)
                    || b.getSatuan().toLowerCase().contains(kw)
                    || b.getNamaSupplier().toLowerCase().contains(kw);
            });
        });

        tblBarang.setItems(filterBarang);
    }


    private void loadData() {
        daftarBarang.clear();

        String sql = """
            SELECT b.kode_barang, b.nama_barang, b.deskripsi,
                   b.harga_beli, b.harga_jual, b.stok, b.satuan,
                   b.supplierId, COALESCE(s.nama, '-') AS nama_supplier
            FROM `Barang` b
            LEFT JOIN `Supplier` s ON b.supplierId = s.id
            ORDER BY b.created_at DESC
            """;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                daftarBarang.add(new Barang(
                    rs.getString("kode_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("deskripsi"),
                    rs.getString("harga_beli"),
                    rs.getString("harga_jual"),
                    rs.getString("stok"),
                    rs.getString("satuan"),
                    rs.getString("supplierId"),
                    rs.getString("nama_supplier")
                ));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data barang: " + e.getMessage());
        }
    }


    private void isiDataForm(Barang b) {
        selectedKode = b.getKodeBarang();

        txtkodeBarang.setText(b.getKodeBarang());
        txtkodeBarang.setDisable(true);

        txtnamaBarang.setText(b.getNamaBarang());
        txtdescBarang.setText(b.getDeskripsi());
        txthbBarang.setText(b.getHargaBeli());
        txthjBarang.setText(b.getHargaJual());
        cmbstokBarang.setValue(b.getStok());
        cmbsatuanBarang.setValue(b.getSatuan());

        daftarSupplier.stream()
            .filter(s -> s.getId().equals(b.getSupplierId()))
            .findFirst()
            .ifPresent(s -> cmbsuppBarang.setValue(s));

        btnsaveBarang.setDisable(true);
        btnupdateBarang.setDisable(false);
        btndeleteBarang.setDisable(false);
    }


    @FXML
    public void btnsaveBarang() {
        if (!validateForm()) return;

        String sql = """
            INSERT INTO `Barang`
                (id, kode_barang, nama_barang, deskripsi, harga_beli, harga_jual, stok, satuan, supplierId)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, java.util.UUID.randomUUID().toString());
            stmt.setString(2, txtkodeBarang.getText().trim().toUpperCase());
            stmt.setString(3, txtnamaBarang.getText().trim());
            stmt.setString(4, getNullable(txtdescBarang.getText()));
            stmt.setDouble(5, parseDouble(txthbBarang.getText()));
            stmt.setDouble(6, parseDouble(txthjBarang.getText()));
            stmt.setInt(7,    parseInt(cmbstokBarang.getValue()));
            stmt.setString(8, cmbsatuanBarang.getValue());
            stmt.setString(9, cmbsuppBarang.getValue() != null
                ? cmbsuppBarang.getValue().getId() : null);

            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data barang berhasil disimpan.");
            resetForm();
            loadData();

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplikat", "Kode barang sudah terdaftar di sistem.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    @FXML
    public void btnupdateBarang() {
        if (selectedKode == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data barang yang ingin diubah.");
            return;
        }
        if (!validateForm()) return;

        String sql = """
            UPDATE `Barang`
            SET nama_barang=?, deskripsi=?, harga_beli=?, harga_jual=?, stok=?, satuan=?, supplierId=?
            WHERE kode_barang=?
            """;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtnamaBarang.getText().trim());
            stmt.setString(2, getNullable(txtdescBarang.getText()));
            stmt.setDouble(3, parseDouble(txthbBarang.getText()));
            stmt.setDouble(4, parseDouble(txthjBarang.getText()));
            stmt.setInt(5,    parseInt(cmbstokBarang.getValue()));
            stmt.setString(6, cmbsatuanBarang.getValue());
            stmt.setString(7, cmbsuppBarang.getValue() != null
                ? cmbsuppBarang.getValue().getId() : null);
            stmt.setString(8, selectedKode);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data barang berhasil diperbarui.");
                resetForm();
                loadData();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal mengubah: " + e.getMessage());
        }
    }

    @FXML
    public void btndeleteBarang() {
        if (selectedKode == null) {
            showAlert(Alert.AlertType.WARNING, "Perhatian", "Pilih data barang yang ingin dihapus.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
            "Konfirmasi Hapus",
            "Hapus barang [" + selectedKode + "]?\nData detail pembelian & penjualan terkait juga akan terhapus."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "DELETE FROM `Barang` WHERE kode_barang = ?")) {

                stmt.setString(1, selectedKode);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data barang berhasil dihapus.");
                    resetForm();
                    loadData();
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus: " + e.getMessage());
            }
        }
    }

    @FXML
    public void btnresetBarang() {
        resetForm();
    }


    private void resetForm() {
        selectedKode = null;

        txtkodeBarang.clear();
        txtkodeBarang.setDisable(false);
        txtnamaBarang.clear();
        txtdescBarang.clear();
        txthbBarang.clear();
        txthjBarang.clear();
        cmbstokBarang.setValue(null);
        cmbsatuanBarang.setValue(null);
        cmbsuppBarang.setValue(null);

        tblBarang.getSelectionModel().clearSelection();

        btnsaveBarang.setDisable(false);
        btnupdateBarang.setDisable(true);
        btndeleteBarang.setDisable(true);
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (txtkodeBarang.getText().trim().isEmpty())
            errors.append("• Kode barang tidak boleh kosong.\n");

        if (txtnamaBarang.getText().trim().isEmpty())
            errors.append("• Nama barang tidak boleh kosong.\n");

        if (cmbsuppBarang.getValue() == null)
            errors.append("• Supplier harus dipilih.\n");

        if (cmbsatuanBarang.getValue() == null)
            errors.append("• Satuan harus dipilih.\n");

        try {
            double hb = parseDouble(txthbBarang.getText());
            if (hb < 0) errors.append("• Harga beli tidak boleh negatif.\n");
        } catch (NumberFormatException e) {
            errors.append("• Harga beli harus berupa angka.\n");
        }

        try {
            double hj = parseDouble(txthjBarang.getText());
            if (hj < 0) errors.append("• Harga jual tidak boleh negatif.\n");
        } catch (NumberFormatException e) {
            errors.append("• Harga jual harus berupa angka.\n");
        }

        try {
            int stok = parseInt(cmbstokBarang.getValue());
            if (stok < 0) errors.append("• Stok tidak boleh negatif.\n");
        } catch (NumberFormatException e) {
            errors.append("• Stok harus berupa angka bulat.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", errors.toString());
            return false;
        }
        return true;
    }

    private double parseDouble(String val) {
        if (val == null || val.trim().isEmpty()) return 0;
        return Double.parseDouble(val.trim().replace(",", "."));
    }

    private int parseInt(String val) {
        if (val == null || val.trim().isEmpty()) return 0;
        return Integer.parseInt(val.trim());
    }

    private String getNullable(String val) {
        return (val == null || val.trim().isEmpty()) ? null : val.trim();
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

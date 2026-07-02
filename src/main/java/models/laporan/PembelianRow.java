package models.laporan;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PembelianRow {
    private final StringProperty nomor;
    private final StringProperty id;
    private final StringProperty noFaktur;
    private final StringProperty tanggal;
    private final StringProperty namaSupplier;
    private final StringProperty namaKaryawan;
    private final StringProperty total;

    public PembelianRow(String nomor, String id, String noFaktur, String tanggal, String namaSupplier, String namaKaryawan, String total) {
        this.nomor = new SimpleStringProperty(nomor);
        this.id = new SimpleStringProperty(id);
        this.noFaktur = new SimpleStringProperty(noFaktur != null ? noFaktur : "");
        this.tanggal = new SimpleStringProperty(tanggal != null ? tanggal : "");
        this.namaSupplier = new SimpleStringProperty(namaSupplier != null ? namaSupplier : "");
        this.namaKaryawan = new SimpleStringProperty(namaKaryawan != null ? namaKaryawan : "");
        this.total = new SimpleStringProperty(total != null ? total : "0");
    }

    public String getNomor()        { return nomor.get(); }
    public String getId()           { return id.get(); }
    public String getNoFaktur()     { return noFaktur.get(); }
    public String getTanggal()      { return tanggal.get(); }
    public String getNamaSupplier() { return namaSupplier.get(); }
    public String getNamaKaryawan() { return namaKaryawan.get(); }
    public String getTotal()        { return total.get(); }

    public StringProperty nomorProperty()        { return nomor; }
    public StringProperty idProperty()           { return id; }
    public StringProperty noFakturProperty()     { return noFaktur; }
    public StringProperty tanggalProperty()      { return tanggal; }
    public StringProperty namaSupplierProperty() { return namaSupplier; }
    public StringProperty namaKaryawanProperty() { return namaKaryawan; }
    public StringProperty totalProperty()        { return total; }
}
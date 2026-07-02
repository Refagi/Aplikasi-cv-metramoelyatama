package models.laporan;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PengerjaanRow {
    private final StringProperty nomor;
    private final StringProperty namaClient;
    private final StringProperty namaKaryawan;
    private final StringProperty tglMulai;
    private final StringProperty tglSelesai;
    private final StringProperty durasi;
    private final StringProperty status;
    private final StringProperty catatan;

    public PengerjaanRow(String nomor, String namaClient, String namaKaryawan, String tglMulai, String tglSelesai, String durasi, String status, String catatan) {
        this.nomor        = new SimpleStringProperty(nomor);
        this.namaClient    = new SimpleStringProperty(namaClient != null ? namaClient : "");
        this.namaKaryawan  = new SimpleStringProperty(namaKaryawan != null ? namaKaryawan : "");
        this.tglMulai       = new SimpleStringProperty(tglMulai != null ? tglMulai : "");
        this.tglSelesai     = new SimpleStringProperty(tglSelesai != null ? tglSelesai : "-");
        this.durasi          = new SimpleStringProperty(durasi != null ? durasi : "-");
        this.status          = new SimpleStringProperty(status != null ? status : "");
        this.catatan         = new SimpleStringProperty(catatan != null ? catatan : "");
    }

    public String getNomor()        { return nomor.get(); }
    public String getNamaClient()   { return namaClient.get(); }
    public String getNamaKaryawan() { return namaKaryawan.get(); }
    public String getTglMulai()     { return tglMulai.get(); }
    public String getTglSelesai()   { return tglSelesai.get(); }
    public String getDurasi()       { return durasi.get(); }
    public String getStatus()       { return status.get(); }
    public String getCatatan()      { return catatan.get(); }

    public StringProperty nomorProperty()        { return nomor; }
    public StringProperty namaClientProperty()   { return namaClient; }
    public StringProperty namaKaryawanProperty() { return namaKaryawan; }
    public StringProperty tglMulaiProperty()     { return tglMulai; }
    public StringProperty tglSelesaiProperty()   { return tglSelesai; }
    public StringProperty durasiProperty()       { return durasi; }
    public StringProperty statusProperty()       { return status; }
    public StringProperty catatanProperty()      { return catatan; }
}
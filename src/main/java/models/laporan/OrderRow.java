package models.laporan;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrderRow {
    private final StringProperty nomor;
    private final StringProperty id;
    private final StringProperty namaClient;
    private final StringProperty namaKaryawan;
    private final StringProperty tglOrder;
    private final StringProperty status;
    private final StringProperty totalNilai;

    public OrderRow(String nomor, String id, String namaClient, String namaKaryawan,
                           String tglOrder, String status, String totalNilai) {
        this.nomor        = new SimpleStringProperty(nomor);
        this.id            = new SimpleStringProperty(id);
        this.namaClient    = new SimpleStringProperty(namaClient != null ? namaClient : "");
        this.namaKaryawan  = new SimpleStringProperty(namaKaryawan != null ? namaKaryawan : "");
        this.tglOrder       = new SimpleStringProperty(tglOrder != null ? tglOrder : "");
        this.status         = new SimpleStringProperty(status != null ? status : "");
        this.totalNilai     = new SimpleStringProperty(totalNilai != null ? totalNilai : "0");
    }

    public String getNomor()         { return nomor.get(); }
    public String getId()            { return id.get(); }
    public String getNamaClient()    { return namaClient.get(); }
    public String getNamaKaryawan()  { return namaKaryawan.get(); }
    public String getTglOrder()      { return tglOrder.get(); }
    public String getStatus()        { return status.get(); }
    public String getTotalNilai()    { return totalNilai.get(); }

    public StringProperty nomorProperty()        { return nomor; }
    public StringProperty idProperty()           { return id; }
    public StringProperty namaClientProperty()   { return namaClient; }
    public StringProperty namaKaryawanProperty() { return namaKaryawan; }
    public StringProperty tglOrderProperty()     { return tglOrder; }
    public StringProperty statusProperty()       { return status; }
    public StringProperty totalNilaiProperty()   { return totalNilai; }
}
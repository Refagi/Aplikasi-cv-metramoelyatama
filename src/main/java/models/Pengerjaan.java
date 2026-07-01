
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pengerjaan {
    private final StringProperty id;
    private final StringProperty orderId;
    private final StringProperty karyawanId;
    private final StringProperty tglMulai;
    private final StringProperty tglSelesai;
    private final StringProperty catatan;
    private final StringProperty status;

    public Pengerjaan(String id, String orderId, String karyawanId, String tglMulai,
                      String tglSelesai, String catatan, String status) {
        this.id         = new SimpleStringProperty(id);
        this.orderId    = new SimpleStringProperty(orderId != null ? orderId : "");
        this.karyawanId = new SimpleStringProperty(karyawanId != null ? karyawanId : "");
        this.tglMulai   = new SimpleStringProperty(tglMulai != null ? tglMulai : "");
        this.tglSelesai = new SimpleStringProperty(tglSelesai != null ? tglSelesai : "");
        this.catatan    = new SimpleStringProperty(catatan != null ? catatan : "");
        this.status     = new SimpleStringProperty(status != null ? status : "");
    }

    public String getId()                  { return id.get(); }
    public StringProperty idProperty()     { return id; }

    public String getOrderId()             { return orderId.get(); }
    public StringProperty orderIdProperty() { return orderId; }

    public String getKaryawanId()              { return karyawanId.get(); }
    public StringProperty karyawanIdProperty() { return karyawanId; }

    public String getTglMulai()                { return tglMulai.get(); }
    public StringProperty tglMulaiProperty()   { return tglMulai; }

    public String getTglSelesai()                { return tglSelesai.get(); }
    public StringProperty tglSelesaiProperty()   { return tglSelesai; }

    public String getCatatan()                { return catatan.get(); }
    public StringProperty catatanProperty()   { return catatan; }

    public String getStatus()                { return status.get(); }
    public StringProperty statusProperty()   { return status; }
}
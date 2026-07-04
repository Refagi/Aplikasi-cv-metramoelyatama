package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pembelian {
    private final StringProperty id;
    private final StringProperty noFaktur;
    private final StringProperty tanggal;
    private final StringProperty supplierId;
    private final StringProperty karyawanId;
    private final StringProperty total;

    public Pembelian(String id, String noFaktur, String tanggal,
                     String supplierId, String karyawanId, String total) {
        this.id         = new SimpleStringProperty(id);
        this.noFaktur   = new SimpleStringProperty(noFaktur != null ? noFaktur : "");
        this.tanggal    = new SimpleStringProperty(tanggal != null ? tanggal : "");
        this.supplierId = new SimpleStringProperty(supplierId != null ? supplierId : "");
        this.karyawanId = new SimpleStringProperty(karyawanId != null ? karyawanId : "");
        this.total      = new SimpleStringProperty(total != null ? total : "0");
    }

    public String getId()                  { return id.get(); }
    public StringProperty idProperty()     { return id; }

    public String getNoFaktur()                { return noFaktur.get(); }
    public StringProperty noFakturProperty()   { return noFaktur; }

    public String getTanggal()                { return tanggal.get(); }
    public StringProperty tanggalProperty()   { return tanggal; }

    public String getSupplierId()                { return supplierId.get(); }
    public StringProperty supplierIdProperty()   { return supplierId; }

    public String getKaryawanId()                { return karyawanId.get(); }
    public StringProperty karyawanIdProperty()   { return karyawanId; }

    public String getTotal()                { return total.get(); }
    public StringProperty totalProperty()   { return total; }
}
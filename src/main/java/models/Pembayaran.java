package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pembayaran {
    private final StringProperty id;
    private final StringProperty invoiceId;
    private final StringProperty tglBayar;
    private final StringProperty jumlahBayar;
    private final StringProperty metodeBayar;
    private final StringProperty catatan;

    public Pembayaran(String id, String invoiceId, String tglBayar,
                      String jumlahBayar, String metodeBayar, String catatan) {
        this.id          = new SimpleStringProperty(id);
        this.invoiceId   = new SimpleStringProperty(invoiceId != null ? invoiceId : "");
        this.tglBayar     = new SimpleStringProperty(tglBayar != null ? tglBayar : "");
        this.jumlahBayar  = new SimpleStringProperty(jumlahBayar != null ? jumlahBayar : "0");
        this.metodeBayar  = new SimpleStringProperty(metodeBayar != null ? metodeBayar : "");
        this.catatan      = new SimpleStringProperty(catatan != null ? catatan : "");
    }

    public String getId()                  { return id.get(); }
    public StringProperty idProperty()     { return id; }

    public String getInvoiceId()              { return invoiceId.get(); }
    public StringProperty invoiceIdProperty() { return invoiceId; }

    public String getTglBayar()                { return tglBayar.get(); }
    public StringProperty tglBayarProperty()   { return tglBayar; }

    public String getJumlahBayar()                { return jumlahBayar.get(); }
    public StringProperty jumlahBayarProperty()   { return jumlahBayar; }

    public String getMetodeBayar()                { return metodeBayar.get(); }
    public StringProperty metodeBayarProperty()   { return metodeBayar; }

    public String getCatatan()                { return catatan.get(); }
    public StringProperty catatanProperty()   { return catatan; }
}
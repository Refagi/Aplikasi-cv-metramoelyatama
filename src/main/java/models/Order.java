package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Order {
    private final StringProperty id;
    private final StringProperty clientId;
    private final StringProperty karyawanId;
    private final StringProperty tglOrder;
    private final StringProperty batasWaktu;
    private final StringProperty statusOrder;
    private final StringProperty keterangan;

    public Order(String id, String clientId, String karyawanId, String tglOrder,
                String batasWaktu, String statusOrder, String keterangan) {
        this.id          = new SimpleStringProperty(id);
        this.clientId    = new SimpleStringProperty(clientId != null ? clientId : "");
        this.karyawanId  = new SimpleStringProperty(karyawanId != null ? karyawanId : "");
        this.tglOrder    = new SimpleStringProperty(tglOrder != null ? tglOrder : "");
        this.batasWaktu  = new SimpleStringProperty(batasWaktu != null ? batasWaktu : "");
        this.statusOrder = new SimpleStringProperty(statusOrder != null ? statusOrder : "");
        this.keterangan  = new SimpleStringProperty(keterangan != null ? keterangan : "");
    }

    public String getId()                  { return id.get(); }
    public StringProperty idProperty()     { return id; }

    public String getClientId()            { return clientId.get(); }
    public StringProperty clientIdProperty() { return clientId; }

    public String getKaryawanId()              { return karyawanId.get(); }
    public StringProperty karyawanIdProperty() { return karyawanId; }

    public String getTglOrder()                { return tglOrder.get(); }
    public StringProperty tglOrderProperty()   { return tglOrder; }

    public String getBatasWaktu()                { return batasWaktu.get(); }
    public StringProperty batasWaktuProperty()   { return batasWaktu; }

    public String getStatusOrder()                { return statusOrder.get(); }
    public StringProperty statusOrderProperty()   { return statusOrder; }

    public String getKeterangan()                { return keterangan.get(); }
    public StringProperty keteranganProperty()   { return keterangan; }
}
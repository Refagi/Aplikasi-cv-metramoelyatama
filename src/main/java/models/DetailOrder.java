package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetailOrder {
    private final StringProperty id;
    private final StringProperty orderId;
    private final StringProperty layananId;
    private final StringProperty jumlah;
    private final StringProperty tarif;
    private final StringProperty subtotal;

    public DetailOrder(String id, String orderId, String layananId,
                       String jumlah, String tarif, String subtotal) {
        this.id        = new SimpleStringProperty(id);
        this.orderId   = new SimpleStringProperty(orderId != null ? orderId : "");
        this.layananId = new SimpleStringProperty(layananId != null ? layananId : "");
        this.jumlah    = new SimpleStringProperty(jumlah != null ? jumlah : "0");
        this.tarif     = new SimpleStringProperty(tarif != null ? tarif : "0");
        this.subtotal  = new SimpleStringProperty(subtotal != null ? subtotal : "0");
    }

    public String getId()                  { return id.get(); }
    public StringProperty idProperty()     { return id; }

    public String getOrderId()             { return orderId.get(); }
    public StringProperty orderIdProperty() { return orderId; }

    public String getLayananId()              { return layananId.get(); }
    public StringProperty layananIdProperty() { return layananId; }

    public String getJumlah()              { return jumlah.get(); }
    public StringProperty jumlahProperty() { return jumlah; }

    public String getTarif()              { return tarif.get(); }
    public StringProperty tarifProperty() { return tarif; }

    public String getSubtotal()              { return subtotal.get(); }
    public StringProperty subtotalProperty() { return subtotal; }
}
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Invoice {
    private final StringProperty id;
    private final StringProperty orderId;
    private final StringProperty tglInvoice;
    private final StringProperty totalBayar;
    private final StringProperty statusBayar;

    public Invoice(String id, String orderId, String tglInvoice,
                   String totalBayar, String statusBayar) {
        this.id          = new SimpleStringProperty(id);
        this.orderId     = new SimpleStringProperty(orderId != null ? orderId : "");
        this.tglInvoice  = new SimpleStringProperty(tglInvoice != null ? tglInvoice : "");
        this.totalBayar  = new SimpleStringProperty(totalBayar != null ? totalBayar : "0");
        this.statusBayar = new SimpleStringProperty(statusBayar != null ? statusBayar : "");
    }

    public String getId()                  { return id.get(); }
    public StringProperty idProperty()     { return id; }

    public String getOrderId()             { return orderId.get(); }
    public StringProperty orderIdProperty() { return orderId; }

    public String getTglInvoice()                { return tglInvoice.get(); }
    public StringProperty tglInvoiceProperty()   { return tglInvoice; }

    public String getTotalBayar()                { return totalBayar.get(); }
    public StringProperty totalBayarProperty()   { return totalBayar; }

    public String getStatusBayar()                { return statusBayar.get(); }
    public StringProperty statusBayarProperty()   { return statusBayar; }
}
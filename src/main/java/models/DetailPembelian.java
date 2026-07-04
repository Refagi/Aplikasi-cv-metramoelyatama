package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetailPembelian {
    private final StringProperty id;
    private final StringProperty pembelianId;
    private final StringProperty barangId;
    private final StringProperty qty;
    private final StringProperty harga;
    private final StringProperty subtotal;

    public DetailPembelian(String id, String pembelianId, String barangId,
                           String qty, String harga, String subtotal) {
        this.id          = new SimpleStringProperty(id);
        this.pembelianId = new SimpleStringProperty(pembelianId != null ? pembelianId : "");
        this.barangId    = new SimpleStringProperty(barangId != null ? barangId : "");
        this.qty         = new SimpleStringProperty(qty != null ? qty : "0");
        this.harga       = new SimpleStringProperty(harga != null ? harga : "0");
        this.subtotal    = new SimpleStringProperty(subtotal != null ? subtotal : "0");
    }

    public String getId()                  { return id.get(); }
    public StringProperty idProperty()     { return id; }

    public String getPembelianId()                { return pembelianId.get(); }
    public StringProperty pembelianIdProperty()   { return pembelianId; }

    public String getBarangId()                { return barangId.get(); }
    public StringProperty barangIdProperty()   { return barangId; }

    public String getQty()                { return qty.get(); }
    public StringProperty qtyProperty()   { return qty; }

    public String getHarga()                { return harga.get(); }
    public StringProperty hargaProperty()   { return harga; }

    public String getSubtotal()                { return subtotal.get(); }
    public StringProperty subtotalProperty()   { return subtotal; }
}
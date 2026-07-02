package models.laporan;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetailPembelianRow {
    private final StringProperty namaBarang;
    private final StringProperty qty;
    private final StringProperty harga;
    private final StringProperty subtotal;

    public DetailPembelianRow(String namaBarang, String qty, String harga, String subtotal) {
        this.namaBarang = new SimpleStringProperty(namaBarang != null ? namaBarang : "");
        this.qty = new SimpleStringProperty(qty != null ? qty : "0");
        this.harga = new SimpleStringProperty(harga != null ? harga : "0");
        this.subtotal = new SimpleStringProperty(subtotal != null ? subtotal : "0");
    }

    public String getNamaBarang() { return namaBarang.get(); }
    public String getQty()        { return qty.get(); }
    public String getHarga()      { return harga.get(); }
    public String getSubtotal()   { return subtotal.get(); }

    public StringProperty namaBarangProperty() { return namaBarang; }
    public StringProperty qtyProperty()        { return qty; }
    public StringProperty hargaProperty()      { return harga; }
    public StringProperty subtotalProperty()   { return subtotal; }
}
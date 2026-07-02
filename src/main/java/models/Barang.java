package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Barang {

    private final StringProperty kodeBarang;
    private final StringProperty namaBarang;
    private final StringProperty deskripsi;
    private final StringProperty hargaBeli;
    private final StringProperty hargaJual;
    private final StringProperty stok;
    private final StringProperty satuan;
    private final StringProperty supplierId;
    private final StringProperty namaSupplier;

    public Barang(String kodeBarang, String namaBarang, String deskripsi,
                  String hargaBeli, String hargaJual, String stok,
                  String satuan, String supplierId, String namaSupplier) {
        this.kodeBarang   = new SimpleStringProperty(kodeBarang   != null ? kodeBarang   : "");
        this.namaBarang   = new SimpleStringProperty(namaBarang   != null ? namaBarang   : "");
        this.deskripsi    = new SimpleStringProperty(deskripsi    != null ? deskripsi    : "");
        this.hargaBeli    = new SimpleStringProperty(hargaBeli    != null ? hargaBeli    : "0");
        this.hargaJual    = new SimpleStringProperty(hargaJual    != null ? hargaJual    : "0");
        this.stok         = new SimpleStringProperty(stok         != null ? stok         : "0");
        this.satuan       = new SimpleStringProperty(satuan       != null ? satuan       : "");
        this.supplierId   = new SimpleStringProperty(supplierId   != null ? supplierId   : "");
        this.namaSupplier = new SimpleStringProperty(namaSupplier != null ? namaSupplier : "");
    }

    // kodeBarang
    public String getKodeBarang()                  { return kodeBarang.get(); }
    public void setKodeBarang(String v)            { kodeBarang.set(v); }
    public StringProperty kodeBarangProperty()     { return kodeBarang; }

    // namaBarang
    public String getNamaBarang()                  { return namaBarang.get(); }
    public void setNamaBarang(String v)            { namaBarang.set(v); }
    public StringProperty namaBarangProperty()     { return namaBarang; }

    // deskripsi
    public String getDeskripsi()                   { return deskripsi.get(); }
    public void setDeskripsi(String v)             { deskripsi.set(v); }
    public StringProperty deskripsiProperty()      { return deskripsi; }

    // hargaBeli
    public String getHargaBeli()                   { return hargaBeli.get(); }
    public void setHargaBeli(String v)             { hargaBeli.set(v); }
    public StringProperty hargaBeliProperty()      { return hargaBeli; }

    // hargaJual
    public String getHargaJual()                   { return hargaJual.get(); }
    public void setHargaJual(String v)             { hargaJual.set(v); }
    public StringProperty hargaJualProperty()      { return hargaJual; }

    // stok
    public String getStok()                        { return stok.get(); }
    public void setStok(String v)                  { stok.set(v); }
    public StringProperty stokProperty()           { return stok; }

    // satuan
    public String getSatuan()                      { return satuan.get(); }
    public void setSatuan(String v)                { satuan.set(v); }
    public StringProperty satuanProperty()         { return satuan; }

    // supplierId
    public String getSupplierId()                  { return supplierId.get(); }
    public void setSupplierId(String v)            { supplierId.set(v); }
    public StringProperty supplierIdProperty()     { return supplierId; }

    // namaSupplier (display only)
    public String getNamaSupplier()                { return namaSupplier.get(); }
    public void setNamaSupplier(String v)          { namaSupplier.set(v); }
    public StringProperty namaSupplierProperty()   { return namaSupplier; }
}
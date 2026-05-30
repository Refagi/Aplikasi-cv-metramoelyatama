/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class JenisLayanan {
    private final StringProperty id;
    private final StringProperty nama;
    private final StringProperty kategori;
    private final StringProperty tarif;
    private final StringProperty satuan;
    private final StringProperty deskripsi;

    public JenisLayanan(String id, String nama, String kategori,
                        String tarif, String satuan, String deskripsi) {
        this.id        = new SimpleStringProperty(id);
        this.nama      = new SimpleStringProperty(nama != null ? nama : "");
        this.deskripsi = new SimpleStringProperty(deskripsi != null ? deskripsi : "");
        this.kategori  = new SimpleStringProperty(kategori != null ? kategori : "");
        this.satuan    = new SimpleStringProperty(satuan != null ? satuan : "");
        this.tarif     = new SimpleStringProperty(tarif != null ? tarif : "0");
    }

    // id
    public String getId()                   { return id.get(); }
    public void setId(String v)             { id.set(v); }
    public StringProperty idProperty()      { return id; }

    // nama
    public String getNama()                 { return nama.get(); }
    public void setNama(String v)           { nama.set(v); }
    public StringProperty namaProperty()    { return nama; }
    
    // deskripsi
    public String getDeskripsi()                { return deskripsi.get(); }
    public void setDeskripsi(String v)          { deskripsi.set(v); }
    public StringProperty deskripsiProperty()   { return deskripsi; }
    
    // kategori
    public String getKategori()                 { return kategori.get(); }
    public void setKategori(String v)           { kategori.set(v); }
    public StringProperty kategoriProperty()    { return kategori; }
    
    // satuan
    public String getSatuan()               { return satuan.get(); }
    public void setSatuan(String v)         { satuan.set(v); }
    public StringProperty satuanProperty()  { return satuan; }

    // tarif
    public String getTarif()                { return tarif.get(); }
    public void setTarif(String v)          { tarif.set(v); }
    public StringProperty tarifProperty()   { return tarif; }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Karyawan {
    private final StringProperty id;
    private final StringProperty userId;
    private final StringProperty nama;
    private final StringProperty jabatan;
    private final StringProperty noTelp;
    private final StringProperty tglMasuk;

    public Karyawan(String id, String userId, String nama, String jabatan, String noTelp, String tglMasuk) {
        this.id       = new SimpleStringProperty(id);
        this.userId   = new SimpleStringProperty(userId != null ? userId : "");
        this.nama     = new SimpleStringProperty(nama != null ? nama : "");
        this.jabatan  = new SimpleStringProperty(jabatan != null ? jabatan : "");
        this.noTelp   = new SimpleStringProperty(noTelp != null ? noTelp : "");
        this.tglMasuk = new SimpleStringProperty(tglMasuk != null ? tglMasuk : "");
    }

    // id
    public String getId()               { return id.get(); }
    public void setId(String v)         { id.set(v); }
    public StringProperty idProperty()  { return id; }

    // userId
    public String getUserId()               { return userId.get(); }
    public void setUserId(String v)         { userId.set(v); }
    public StringProperty userIdProperty()  { return userId; }

    // nama
    public String getNama()                 { return nama.get(); }
    public void setNama(String v)           { nama.set(v); }
    public StringProperty namaProperty()    { return nama; }

    // jabatan
    public String getJabatan()              { return jabatan.get(); }
    public void setJabatan(String v)        { jabatan.set(v); }
    public StringProperty jabatanProperty() { return jabatan; }

    // noTelp
    public String getNoTelp()               { return noTelp.get(); }
    public void setNoTelp(String v)         { noTelp.set(v); }
    public StringProperty noTelpProperty()  { return noTelp; }

    // tglMasuk
    public String getTglMasuk()                 { return tglMasuk.get(); }
    public void setTglMasuk(String v)           { tglMasuk.set(v); }
    public StringProperty tglMasukProperty()    { return tglMasuk; }

}

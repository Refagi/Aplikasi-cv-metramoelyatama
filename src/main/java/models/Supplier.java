/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Supplier {
    private final StringProperty id;
    private final StringProperty nama;
    private final StringProperty email;
    private final StringProperty noTelp;
    private final StringProperty alamat;

    public Supplier(String id, String nama, String email, String alamat, String noTelp) {
        this.id     = new SimpleStringProperty(id);
        this.nama   = new SimpleStringProperty(nama != null ? nama : "");
        this.email  = new SimpleStringProperty(email != null ? email : "");
        this.alamat = new SimpleStringProperty(alamat != null ? alamat : "");
        this.noTelp = new SimpleStringProperty(noTelp != null ? noTelp : "");
    }

    // id
    public String getId()               { return id.get(); }
    public void setId(String v)         { id.set(v); }
    public StringProperty idProperty()  { return id; }

    // nama
    public String getNama()                 { return nama.get(); }
    public void setNama(String v)           { nama.set(v); }
    public StringProperty namaProperty()    { return nama; }

    // email
    public String getEmail()                { return email.get(); }
    public void setEmail(String v)          { email.set(v); }
    public StringProperty emailProperty()   { return email; }

    // alamat
    public String getAlamat()               { return alamat.get(); }
    public void setAlamat(String v)         { alamat.set(v); }
    public StringProperty alamatProperty()  { return alamat; }
    
    // noTelp
    public String getNoTelp()               { return noTelp.get(); }
    public void setNoTelp(String v)         { noTelp.set(v); }
    public StringProperty noTelpProperty()  { return noTelp; }
}
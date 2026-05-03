/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {

    private final StringProperty id;
    private final StringProperty nama;
    private final StringProperty jenisClient;
    private final StringProperty npwp;
    private final StringProperty alamat;
    private final StringProperty noTelp;
    private final StringProperty email;
    private final StringProperty tglDaftar;

    public Client(String id, String nama, String jenisClient, String npwp, String alamat, String noTelp, String email, String tglDaftar) {
        this.id         = new SimpleStringProperty(id);
        this.nama       = new SimpleStringProperty(nama);
        this.jenisClient= new SimpleStringProperty(jenisClient);
        this.npwp       = new SimpleStringProperty(npwp != null ? npwp : "");
        this.alamat     = new SimpleStringProperty(alamat != null ? alamat : "");
        this.noTelp     = new SimpleStringProperty(noTelp != null ? noTelp : "");
        this.email      = new SimpleStringProperty(email != null ? email : "");
        this.tglDaftar  = new SimpleStringProperty(tglDaftar != null ? tglDaftar : "");
    }

    //id 
    public String getId()                  { return id.get(); }
    public void setId(String v)            { id.set(v); }
    public StringProperty idProperty()     { return id; }

    //nama
    public String getNama()                { return nama.get(); }
    public void setNama(String v)          { nama.set(v); }
    public StringProperty namaProperty()   { return nama; }

    //jenisClient
    public String getJenisClient()              { return jenisClient.get(); }
    public void setJenisClient(String v)        { jenisClient.set(v); }
    public StringProperty jenisClientProperty() { return jenisClient; }

    // npwp
    public String getNpwp()                { return npwp.get(); }
    public void setNpwp(String v)          { npwp.set(v); }
    public StringProperty npwpProperty()   { return npwp; }

    // alamat
    public String getAlamat()              { return alamat.get(); }
    public void setAlamat(String v)        { alamat.set(v); }
    public StringProperty alamatProperty() { return alamat; }

    //noTelp
    public String getNoTelp()              { return noTelp.get(); }
    public void setNoTelp(String v)        { noTelp.set(v); }
    public StringProperty noTelpProperty() { return noTelp; }

    // email
    public String getEmail()               { return email.get(); }
    public void setEmail(String v)         { email.set(v); }
    public StringProperty emailProperty()  { return email; }

    //tglDaftar
    public String getTglDaftar()               { return tglDaftar.get(); }
    public void setTglDaftar(String v)         { tglDaftar.set(v); }
    public StringProperty tglDaftarProperty()  { return tglDaftar; }
}
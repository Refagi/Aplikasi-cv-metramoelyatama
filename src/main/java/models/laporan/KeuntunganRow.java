package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KeuntunganRow {
    private final StringProperty bulan;
    private final StringProperty pemasukan;
    private final StringProperty pengeluaran;
    private final StringProperty keuntungan;

    public KeuntunganRow(String bulan, String pemasukan, String pengeluaran, String keuntungan) {
        this.bulan = new SimpleStringProperty(bulan != null ? bulan : "");
        this.pemasukan = new SimpleStringProperty(pemasukan != null ? pemasukan : "0");
        this.pengeluaran = new SimpleStringProperty(pengeluaran != null ? pengeluaran : "0");
        this.keuntungan = new SimpleStringProperty(keuntungan != null ? keuntungan : "0");
    }

    public String getBulan()       { return bulan.get(); }
    public String getPemasukan()   { return pemasukan.get(); }
    public String getPengeluaran() { return pengeluaran.get(); }
    public String getKeuntungan()  { return keuntungan.get(); }

    public StringProperty bulanProperty()       { return bulan; }
    public StringProperty pemasukanProperty()   { return pemasukan; }
    public StringProperty pengeluaranProperty() { return pengeluaran; }
    public StringProperty keuntunganProperty()  { return keuntungan; }
}
package models.laporan;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InvoiceRow {
    private final StringProperty nomor;
    private final StringProperty namaClient;
    private final StringProperty tglInvoice;
    private final StringProperty totalTagihan;
    private final StringProperty sudahDibayar;
    private final StringProperty sisaTagihan;
    private final StringProperty status;

    public InvoiceRow(String nomor, String namaClient, String tglInvoice, String totalTagihan, String sudahDibayar, String sisaTagihan, String status) {
        this.nomor = new SimpleStringProperty(nomor);
        this.namaClient = new SimpleStringProperty(namaClient != null ? namaClient : "");
        this.tglInvoice = new SimpleStringProperty(tglInvoice != null ? tglInvoice : "");
        this.totalTagihan = new SimpleStringProperty(totalTagihan != null ? totalTagihan : "0");
        this.sudahDibayar = new SimpleStringProperty(sudahDibayar != null ? sudahDibayar : "0");
        this.sisaTagihan = new SimpleStringProperty(sisaTagihan != null ? sisaTagihan : "0");
        this.status = new SimpleStringProperty(status != null ? status : "");
    }

    public String getNomor()         { return nomor.get(); }
    public String getNamaClient()    { return namaClient.get(); }
    public String getTglInvoice()    { return tglInvoice.get(); }
    public String getTotalTagihan()  { return totalTagihan.get(); }
    public String getSudahDibayar()  { return sudahDibayar.get(); }
    public String getSisaTagihan()   { return sisaTagihan.get(); }
    public String getStatus()        { return status.get(); }

    public StringProperty nomorProperty()         { return nomor; }
    public StringProperty namaClientProperty()    { return namaClient; }
    public StringProperty tglInvoiceProperty()    { return tglInvoice; }
    public StringProperty totalTagihanProperty()  { return totalTagihan; }
    public StringProperty sudahDibayarProperty()  { return sudahDibayar; }
    public StringProperty sisaTagihanProperty()   { return sisaTagihan; }
    public StringProperty statusProperty()        { return status; }
}
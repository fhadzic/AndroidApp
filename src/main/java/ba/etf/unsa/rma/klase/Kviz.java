package ba.etf.unsa.rma.klase;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@SuppressLint("ParcelCreator")
public class Kviz implements Comparable<Kviz>, Parcelable {

    private String naziv;
    private ArrayList<Pitanje> pitanja;
    private Kategorija kategorija;
    private String idFireBase;
    private int tip;

    public Kviz() {
        this.tip = 0;
        this.kategorija = new Kategorija();
        this.idFireBase = null;
    }

    public Kviz(String naziv, int tip) {
        this.naziv = naziv;
        this.tip = tip;
        this.kategorija = new Kategorija();
        this.idFireBase = null;
    }

    public Kviz(String naziv) {
        Pitanje dodajPitanje = new Pitanje("Dodaj Pitanje");
        dodajPitanje.setTip(1);
        this.pitanja = new ArrayList<Pitanje>();
        pitanja.add(dodajPitanje);
        this.naziv = naziv;
        this.tip = 0;
        this.kategorija = new Kategorija();
        this.idFireBase = null;
    }

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija, int tip) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
        this.tip = tip;
        this.hashCode();
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public String getIdFireBase() {
        return idFireBase;
    }

    public void setIdFireBase(String idFireBase) {
        this.idFireBase = idFireBase;
    }

    public void dodajPitanje(Pitanje pitanje) {
        pitanje.setTip(0);
        pitanja.add(pitanje);
        Collections.sort(pitanja);
    }

    @Override
    public int hashCode() {
        idFireBase = String.valueOf(Objects.hash(naziv.toLowerCase()));
        return Integer.parseInt(idFireBase);
    }

    @Override
    public int compareTo(Kviz k) {
        Integer c1 = getTip();
        Integer c2 = k.getTip();

        Integer poredak = c1-c2;

        if(poredak != 0){
            return poredak;
        }

        return naziv.compareTo(k.naziv);
    }

    protected Kviz(Parcel in) {
        naziv = in.readString();
        pitanja = in.createTypedArrayList(Pitanje.CREATOR);
        kategorija = in.readParcelable(Kategorija.class.getClassLoader());
        idFireBase = in.readString();
        tip = in.readInt();
    }


    public static final Creator<Kviz> CREATOR = new Creator<Kviz>() {
        @Override
        public Kviz createFromParcel(Parcel in) {
            return new Kviz(in);
        }

        @Override
        public Kviz[] newArray(int size) {
            return new Kviz[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naziv);
        dest.writeTypedList(pitanja);
        dest.writeParcelable(kategorija, flags);
        dest.writeString(idFireBase);
        dest.writeInt(tip);
    }
}

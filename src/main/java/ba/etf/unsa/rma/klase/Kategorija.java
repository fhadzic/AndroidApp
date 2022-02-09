package ba.etf.unsa.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Kategorija implements Comparable<Kategorija>, Parcelable {

    String naziv;
    String id;
    String idFireBase;
    int tip;

    public Kategorija() {
        this.tip = 0;
    }

    public Kategorija(String naziv, int tip) {
        this.naziv = naziv;
        this.tip = tip;
        this.id = null;
    }

    public Kategorija(String naziv, String id, int tip) {
        this.naziv = naziv;
        this.id = id;
        this.tip = tip;
    }

    protected Kategorija(Parcel in) {
        naziv = in.readString();
        id = in.readString();
        tip = in.readInt();
        idFireBase = in.readString();
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public String getIdFireBase() {
        return idFireBase;
    }

    public void setIdFireBase(String idFireBase) {
        this.idFireBase = idFireBase;
    }


    @Override
    public int hashCode() {
        idFireBase = String.valueOf(Objects.hash(naziv.toLowerCase()));
        return Integer.parseInt(idFireBase);
    }


    public static final Creator<Kategorija> CREATOR = new Creator<Kategorija>() {
        @Override
        public Kategorija createFromParcel(Parcel in) {
            return new Kategorija(in);
        }

        @Override
        public Kategorija[] newArray(int size) {
            return new Kategorija[size];
        }
    };


    @Override
    public int compareTo(Kategorija k) {
        Integer c1 = getTip();
        Integer c2 = k.getTip();

        Integer poredak = c1 - c2;

        if (poredak != 0) {
            return poredak;
        }

        return naziv.compareTo(k.naziv);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naziv);
        dest.writeString(id);
        dest.writeInt(tip);
        dest.writeString(idFireBase);
    }
}

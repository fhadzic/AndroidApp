package ba.etf.unsa.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Pitanje implements Parcelable, Comparable<Pitanje> {

    String naziv;
    String textPitanja;
    ArrayList<String> odgovori;
    String tacan;
    String idFireBase;
    int tip;


    public Pitanje(String naziv, ArrayList<String> odgovori, String tacan, int tip) {
        this.naziv = naziv;
        this.textPitanja = naziv;
        this.odgovori = odgovori;
        this.tacan = tacan;
        this.tip = tip;
        this.hashCode();
    }

    public Pitanje(String naziv) {
        this.naziv = naziv;
        this.textPitanja = naziv;
        this.tip = 0;
        this.tacan = "";
        this.idFireBase = null;
    }

    public Pitanje(String naziv, int tip) {
        this.naziv = naziv;
        this.textPitanja = naziv;
        this.tip = tip;
        this.tacan = "";
        this.idFireBase = null;
    }

    public Pitanje(){
        this.tip = 0;
        this.tacan = "";
        this.idFireBase = null;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getTextPitanja() {
        return textPitanja;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public String getTacan() {
        return tacan;
    }

    public int getTip() {
        return tip;
    }

    public int getIndexTacnog(){
        if(odgovori.size() == 0){
            return -1;
        }
        int i;
        for(i=0; i < odgovori.size(); i++){
            if(tacan.equals(odgovori.get(i))){
                break;
            }
        }
        return i;
    }

    public String getIdFireBase() {
        return idFireBase;
    }

    public void setIdFireBase(String idFireBase) {
        this.idFireBase = idFireBase;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public void setTacan(String tacan) {
        this.tacan = tacan;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public void setTextPitanja(String textPitanja) {
        this.textPitanja = textPitanja;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
        this.textPitanja = naziv;
    }

    ArrayList<String> dajRandomOdgovore() {
        Collections.shuffle(odgovori);
        return odgovori;
    }

    @Override
    public int hashCode() {
        idFireBase = String.valueOf(Objects.hash(naziv.toLowerCase()));
        return Integer.parseInt(idFireBase);
    }


    @Override
    public int compareTo(Pitanje p) {

        Integer c1 = getTip();
        Integer c2 = p.getTip();

        Integer poredak = c1-c2;

        if(poredak != 0){
            return poredak;
        }

        return naziv.compareTo(p.naziv);
    }


    protected Pitanje(Parcel in) {
        naziv = in.readString();
        textPitanja = in.readString();
        odgovori = in.createStringArrayList();
        tacan = in.readString();
        tip = in.readInt();
        idFireBase = in.readString();
    }

    public static final Creator<Pitanje> CREATOR = new Creator<Pitanje>() {
        @Override
        public Pitanje createFromParcel(Parcel in) {
            return new Pitanje(in);
        }

        @Override
        public Pitanje[] newArray(int size) {
            return new Pitanje[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(naziv);
        dest.writeString(textPitanja);
        dest.writeStringList(odgovori);
        dest.writeString(tacan);
        dest.writeInt(tip);
        dest.writeString(idFireBase);
    }


    @Override
    public int describeContents() {
        return 0;
    }

}

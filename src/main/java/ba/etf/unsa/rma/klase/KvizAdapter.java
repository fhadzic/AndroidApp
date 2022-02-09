package ba.etf.unsa.rma.klase;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.aktivnosti.KvizoviAkt;

public class KvizAdapter extends ArrayAdapter<Kviz> {

    private Context context;
    private ArrayList<Kviz> filtriraniKvizovi;
    ArrayList<Kviz> allKvizsSQLite = new ArrayList<>();

    public KvizAdapter(Context context, int resource) {
        super(context, resource);
        this.filtriraniKvizovi = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public int getItemViewType(int position) {
        if (filtriraniKvizovi == null || filtriraniKvizovi.isEmpty()) {
            return -1;
        }
        return filtriraniKvizovi.get(position).getTip();
    }

    @Override
    public Kviz getItem(int position) {
        return filtriraniKvizovi.get(position);
    }

    @Override
    public int getCount() {
        return filtriraniKvizovi.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        int tip = getItemViewType(position);

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            if (tip == 0) {
                view = vi.inflate(R.layout.icon_dialog, null);
            } else {
                view = vi.inflate(R.layout.icon_dodaj, null);
            }
        }

        Kviz kviz = getItem(position);
        if (kviz != null) {
            if (kviz.getKategorija() != null && kviz.getKategorija().getId() != null) {
                com.maltaisn.icondialog.IconView ikona = view.findViewById(R.id.icon_dialog);
                if (ikona != null) {
                    ikona.setIcon(Integer.valueOf(kviz.getKategorija().getId()));
                }

            }
            TextView itemName = view.findViewById(R.id.Itemname);
            itemName.setText(kviz.getNaziv());
        }
        return view;
    }

    public ArrayList<Kviz> getFiltriraniKvizovi() {
        return filtriraniKvizovi;
    }

    public void dodajKviz(Kviz kviz) {
        if (kviz != null) {
            filtriraniKvizovi.add(kviz);
            Collections.sort(filtriraniKvizovi);
            notifyDataSetChanged();
        }
    }

    public void dodajKvizIObrisiOstale() {
        filtriraniKvizovi.clear();

        Kviz dodajKviz = new Kviz("Dodaj Kviz");
        dodajKviz.setTip(1);

        filtriraniKvizovi.add(dodajKviz);
        notifyDataSetChanged();

    }


    public void addKviz(Kviz kviz, String bivsiNaziv) {
        int i;
        for (i = 0; i < filtriraniKvizovi.size() - 1; i++) {
            if (filtriraniKvizovi.get(i).getNaziv().equals(bivsiNaziv)) {
                filtriraniKvizovi.remove(filtriraniKvizovi.get(i));
                break;
            }
        }
        filtriraniKvizovi.add(i, kviz);
        notifyDataSetChanged();
    }

    public void filtrirajListu(Kategorija odabranaKategorija) {
        ArrayList<Kviz> sviKvizovi = new ArrayList<>();
        sviKvizovi.addAll(filtriraniKvizovi);
        filtriraniKvizovi.clear();
        if (odabranaKategorija.getNaziv().equals("Svi")) {
            filtriraniKvizovi.addAll(sviKvizovi);
            Collections.sort(filtriraniKvizovi);
            notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < sviKvizovi.size(); i++) {
            Kviz kviz = sviKvizovi.get(i);
            if (odabranaKategorija.getNaziv().equals(kviz.getKategorija().getNaziv()) || kviz.getNaziv().equals("Dodaj Kviz")) {
                filtriraniKvizovi.add(sviKvizovi.get(i));
            }
        }
        Collections.sort(filtriraniKvizovi);
        notifyDataSetChanged();
    }


    public void addAllKvizoveFromTheFireBase(ArrayList<Kviz> kvizovi) {
        filtriraniKvizovi.clear();
        filtriraniKvizovi.addAll(kvizovi);
        Kviz dodajKviz = new Kviz("Dodaj Kviz");
        dodajKviz.setTip(1);
        filtriraniKvizovi.add(filtriraniKvizovi.size(), dodajKviz);
        Collections.sort(filtriraniKvizovi);
        notifyDataSetChanged();
    }

    public void filterSQLite(Kategorija odabranaKategorija){
        if(allKvizsSQLite.size() == 0){
            allKvizsSQLite.add(new Kviz("Dodaj Kviz", 1));
        }
        filtriraniKvizovi.clear();
        if (odabranaKategorija.getNaziv().equals("Svi")) {
            filtriraniKvizovi.addAll(allKvizsSQLite);
            Collections.sort(filtriraniKvizovi);
            notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < allKvizsSQLite.size(); i++) {
            Kviz kviz = allKvizsSQLite.get(i);
            if (odabranaKategorija.getNaziv().equals(kviz.getKategorija().getNaziv()) || kviz.getNaziv().equals("Dodaj Kviz")) {
                filtriraniKvizovi.add(kviz);
            }
        }
        Collections.sort(filtriraniKvizovi);
        notifyDataSetChanged();
    }

    public ArrayList<Kviz> getAllKvizsSQLite() {
        return allKvizsSQLite;
    }

    public void setAllKvizsSQLite(ArrayList<Kviz> allKvizsSQLite) {
        this.allKvizsSQLite = allKvizsSQLite;
    }
}
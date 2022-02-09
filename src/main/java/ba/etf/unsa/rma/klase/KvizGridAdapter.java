package ba.etf.unsa.rma.klase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;
import java.util.Collections;

import ba.etf.unsa.rma.R;

public class KvizGridAdapter extends ArrayAdapter<Kviz> {

    private Context context;
    private ArrayList<Kviz> sviKvizovi;
    private ArrayList<Kviz> filtriraniKvizovi;

    public KvizGridAdapter(Context context, int resource) {
        super(context, resource);
        this.sviKvizovi = new ArrayList<>();
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
    public Kviz getItem(int position) { return filtriraniKvizovi.get(position); }

    @Override
    public int getCount() { return filtriraniKvizovi.size(); }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        int tip = getItemViewType(position);

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.element_grid, null);
        }

        Kviz kviz = getItem(position);
        if (kviz != null) {
            if (kviz.getKategorija().getId() != null) {
                IconView icon = view.findViewById(R.id.icon);
                if (icon != null) {
                    icon.setIcon(Integer.valueOf(kviz.getKategorija().getId()));
                }
            }
            if (kviz.getNaziv() != null) {
                TextView naziv = view.findViewById(R.id.naziv);
                naziv.setText(kviz.getNaziv());
            }
            if (kviz.getPitanja() != null) {
                TextView brojPitanja = view.findViewById(R.id.brojPitanja);
                brojPitanja.setText(String.valueOf(kviz.getPitanja().size()));
            }
        }
        return view;
    }

    public ArrayList<Kviz> getSviKvizovi() {
        return sviKvizovi;
    }

    public ArrayList<Kviz> getFiltriraniKvizovi() {
        return filtriraniKvizovi;
    }

    public void dodajKviz(Kviz kviz) {
        if (kviz != null) {
            filtriraniKvizovi.add(kviz);
            sviKvizovi.add(kviz);
            notifyDataSetChanged();
        }
    }

    public void addKviz(Kviz kviz) {
        int i;
        for (i = 0; i < sviKvizovi.size() - 1; i++) {
            if (sviKvizovi.get(i).getNaziv().equals(kviz.getNaziv())) {
                sviKvizovi.remove(sviKvizovi.get(i));
                break;
            }
        }
        sviKvizovi.add(i, kviz);
    }


    public void filtrirajListu(Kategorija odabranaKategorija) {

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
}

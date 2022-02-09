package ba.etf.unsa.rma.klase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ba.etf.unsa.rma.R;

public class KategorijaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Kategorija> listaKategorija;

    public KategorijaAdapter(Context context, ArrayList<Kategorija> listaKategorija) {
        this.context = context;
        this.listaKategorija = listaKategorija;
    }


    @Override
    public int getCount() {
        return listaKategorija.size();
    }

    @Override
    public Object getItem(int position) {
        return listaKategorija.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.just_text, parent, false);
        }

        Kategorija kategorija = (Kategorija) getItem(position);

        TextView imeKategorije = view.findViewById(R.id.text);

        imeKategorije.setText(kategorija.getNaziv());

        return view;
    }

    public void formatirajListu(ArrayList<Kategorija> novaLista){
        listaKategorija = novaLista;
        Collections.sort(listaKategorija);
        notifyDataSetChanged();
    }


    public Kategorija get(int position) {
        return listaKategorija.get(position);
    }
}

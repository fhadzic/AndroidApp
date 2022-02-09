package ba.etf.unsa.rma.klase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import ba.etf.unsa.rma.R;

public class PitanjeMoguceAdapter extends ArrayAdapter<Pitanje> {


    private Context context;
    private ArrayList<Pitanje> mogucaPitanja;

    public PitanjeMoguceAdapter(@NonNull Context context, int resource, ArrayList<Pitanje> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mogucaPitanja = objects;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mogucaPitanja.get(position).getTip();
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
                view = vi.inflate(R.layout.icon, null);
            } else {
                view = vi.inflate(R.layout.icon_dodaj, null);
            }
        }
        Pitanje pitanje = (Pitanje) getItem(position);
        TextView textView = view.findViewById(R.id.Itemname);
        textView.setText(pitanje.getNaziv());
        return view;
    }

    public void dodajMogucePitanje(Pitanje p) {
        p.setTip(1);
        mogucaPitanja.add(p);
        notifyDataSetChanged();
    }

    public void dodajListuMogucih(ArrayList<Pitanje> pitanja) {

        for(int i=0; i<pitanja.size(); i++){
            pitanja.get(i).setTip(1);
            mogucaPitanja.add(pitanja.get(i));
        }

        notifyDataSetChanged();
    }


    public void obrisiPitanje(Pitanje p) {
        mogucaPitanja.remove(p);
        notifyDataSetChanged();
    }

    public ArrayList<Pitanje> getMogucaPitanja() {
        return mogucaPitanja;
    }

}

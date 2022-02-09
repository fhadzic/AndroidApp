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

public class PitanjeDodanoAdapter extends ArrayAdapter<Pitanje> {


    private Context context;
    private ArrayList<Pitanje> dodanaPitanja;

    public PitanjeDodanoAdapter(@NonNull Context context, int resource, ArrayList<Pitanje> objects) {
        super(context, resource, objects);
        this.context = context;
        this.dodanaPitanja = objects;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return dodanaPitanja.get(position).getTip();
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

    public void obrisiPitanje(Pitanje p) {
        dodanaPitanja.remove(p);
        notifyDataSetChanged();
    }

    public void postaviPitanje(Pitanje p) {
        p.setTip(0);
        dodanaPitanja.add(p);
        Collections.sort(dodanaPitanja);
        notifyDataSetChanged();
    }

    public ArrayList<Pitanje> getDodanaPitanja() {
        return dodanaPitanja;
    }


}

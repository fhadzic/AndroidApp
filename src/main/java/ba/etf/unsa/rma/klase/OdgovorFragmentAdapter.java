package ba.etf.unsa.rma.klase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ba.etf.unsa.rma.R;

public class OdgovorFragmentAdapter extends ArrayAdapter {

    private Context context;
    private List<String> odgovori;
    private String tacan;
    private String pokusaj;

    public OdgovorFragmentAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.odgovori = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.icon, null);
        }

        String odgovor = (String) getItem(position);

        if(view != null && odgovor != null) {
            TextView text = view.findViewById(R.id.Itemname);
            text.setText(odgovor);

            if(odgovor.equals(tacan)){
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.zelena));
            }else if(odgovor.equals(pokusaj) && (!odgovor.equals(tacan))){
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.crvena));
            }else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        return view;
    }


    public void setPokusaj(String pokusaj, String tacan){
        this.pokusaj = pokusaj;
        this.tacan = tacan;
        notifyDataSetChanged();
    }

}

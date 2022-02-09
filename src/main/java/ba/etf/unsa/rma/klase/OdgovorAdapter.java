package ba.etf.unsa.rma.klase;

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

import java.util.ArrayList;
import java.util.List;

import ba.etf.unsa.rma.R;

public class OdgovorAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> odgovori = null;
    private String tacan;

    public OdgovorAdapter(@NonNull Context context, int resource, ArrayList<String> objects, String tacan) {
        super(context, resource, objects);
        this.context = context;
        this.odgovori = objects;
        this.tacan = tacan;
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

        String odgovor = getItem(position);

        if(view != null && odgovor != null) {
            TextView text = view.findViewById(R.id.Itemname);
            text.setText(odgovor);

            if(odgovor.equals(tacan)){
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.zelena));
            }else{
                view.setBackgroundColor(Color.TRANSPARENT);
            }

        }

        return view;
    }

    public void setTacan(String tacan){ this.tacan = tacan; }

}

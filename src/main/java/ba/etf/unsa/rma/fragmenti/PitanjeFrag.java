package ba.etf.unsa.rma.fragmenti;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.klase.OdgovorClickListener;
import ba.etf.unsa.rma.klase.OdgovorFragmentAdapter;
import ba.etf.unsa.rma.klase.Pitanje;

public class PitanjeFrag extends Fragment {

    private OdgovorClickListener odgovorClickListener;
    private TextView textPitanja;
    private ListView lvodgovoriPitanja;
    private Pitanje pitanje;

    public static PitanjeFrag newInstance(Pitanje p, OdgovorClickListener odgovorClickListener) {
        PitanjeFrag pitanjeFrag = new PitanjeFrag();
        pitanjeFrag.setOdgovorClickListener(odgovorClickListener);
        Bundle bdl = new Bundle();
        bdl.putParcelable("pitanje", p);
        pitanjeFrag.setArguments(bdl);
        return pitanjeFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bdl = getArguments();

        if (bdl != null && bdl.containsKey("pitanje")) {
            pitanje = bdl.getParcelable("pitanje");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pitanje, container, false);
        textPitanja = view.findViewById(R.id.tekstPitanja);
        lvodgovoriPitanja = view.findViewById(R.id.lvodgovoriPitanja);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textPitanja.setText(pitanje.getNaziv());

        if (pitanje.getOdgovori() != null) {
            final OdgovorFragmentAdapter odgovorAdapter = new OdgovorFragmentAdapter(getContext(), R.layout.icon, pitanje.getOdgovori());
            lvodgovoriPitanja.setAdapter(odgovorAdapter);
            lvodgovoriPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String pokusaj = pitanje.getOdgovori().get(position);
                    odgovorAdapter.setPokusaj(pokusaj, pitanje.getTacan());
                    lvodgovoriPitanja.setEnabled(false);

                    if (odgovorClickListener != null) {
                        odgovorClickListener.clicked(pitanje.getTacan().equals(pokusaj));
                    }
                }
            });
        }
    }

    public void setOdgovorClickListener(OdgovorClickListener odgovorClickListener) {
        this.odgovorClickListener = odgovorClickListener;
    }
}

package ba.etf.unsa.rma.fragmenti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.aktivnosti.DodajKvizAkt;
import ba.etf.unsa.rma.aktivnosti.IgrajKvizAkt;
import ba.etf.unsa.rma.klase.Kategorija;
import ba.etf.unsa.rma.klase.KategorijaAdapter;
import ba.etf.unsa.rma.klase.Kviz;
import ba.etf.unsa.rma.klase.KvizGridAdapter;

public class DetailFrag extends Fragment {

    private GridView gvKvizovi;
    private ArrayList<Kategorija> listaKategorija;
    private ArrayList<Kviz> listaKvizova;
    private KvizGridAdapter kvizGridAdapter;

    public static DetailFrag newInstance(ArrayList<Kategorija> listaKategorija, ArrayList<Kategorija> listaKvizova) {
        DetailFrag listaFrag = new DetailFrag();
        Bundle bdl = new Bundle();
        bdl.putParcelableArrayList("kategorije", listaKategorija);
        bdl.putParcelableArrayList("kvizovi", listaKvizova);
        listaFrag.setArguments(bdl);
        return listaFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bdl = getArguments();
        if (bdl != null && bdl.containsKey("kategorije") && bdl.containsKey("kvizovi")) {
            listaKategorija = bdl.getParcelableArrayList("kategorije");
            listaKvizova = bdl.getParcelableArrayList("kvizovi");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        gvKvizovi = view.findViewById(R.id.gvKvizovi);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        kvizGridAdapter = new KvizGridAdapter(getActivity(), R.layout.element_grid);
        gvKvizovi.setAdapter(kvizGridAdapter);
        gvKvizovi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Kviz otvoreniKviz = kvizGridAdapter.getFiltriraniKvizovi().get(position);
                otvoriKviz(otvoreniKviz);
                return true;
            }
        });
        gvKvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Kviz otvoreniKviz = kvizGridAdapter.getFiltriraniKvizovi().get(position);
                if (otvoreniKviz.getTip() == 0) {
                    otvoriIgrajKviz(otvoreniKviz);
                }
            }
        });
    }

    private void otvoriKviz(Kviz kviz) {
        Intent kvizIntent = new Intent(getActivity(), DodajKvizAkt.class);
        Bundle b = new Bundle();
        b.putString("naziv", kviz.getNaziv());
        b.putInt("tip", kviz.getTip());
        b.putParcelableArrayList("listaPitanja", kviz.getPitanja());
        b.putParcelable("kategorija", kviz.getKategorija());
        b.putParcelableArrayList("listaKvizova", kvizGridAdapter.getSviKvizovi());
        b.putParcelableArrayList("listaKategorija", listaKategorija);
        kvizIntent.putExtras(b);
        startActivityForResult(kvizIntent, 100);
    }

    private void otvoriIgrajKviz(Kviz kviz) {
        Intent igrajIntent = new Intent(getActivity(), IgrajKvizAkt.class);
        Bundle bdl = new Bundle();
        bdl.putString("naziv", kviz.getNaziv());
        bdl.putParcelableArrayList("ListaPitanja", kviz.getPitanja());
        igrajIntent.putExtras(bdl);
        startActivityForResult(igrajIntent, 500);
    }
}

package ba.etf.unsa.rma.fragmenti;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.klase.Kategorija;
import ba.etf.unsa.rma.klase.KategorijaAdapter;

public class ListaFrag extends Fragment {

    private ListView lvKategorije;
    private ArrayList<Kategorija> listaKategorija;
    private KategorijaAdapter kategorijaAdapter;

    public static ListaFrag newInstance(ArrayList<Kategorija> listaKategorija) {
        ListaFrag listaFrag = new ListaFrag();
        Bundle bdl = new Bundle();
        bdl.putParcelableArrayList("lista", listaKategorija);
        listaFrag.setArguments(bdl);
        return listaFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bdl = getArguments();
        if (bdl != null && bdl.containsKey("lista")) {
            listaKategorija = bdl.getParcelableArrayList("lista");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kategorije, container, false);
        lvKategorije = view.findViewById(R.id.lvKategorije);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        kategorijaAdapter = new KategorijaAdapter(getActivity(), listaKategorija);
        lvKategorije.setAdapter(kategorijaAdapter);
    }
}

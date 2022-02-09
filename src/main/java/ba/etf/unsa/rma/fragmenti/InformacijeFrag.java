package ba.etf.unsa.rma.fragmenti;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.aktivnosti.AlarmReceiverActivity;
import ba.etf.unsa.rma.aktivnosti.IgrajKvizAkt;
import ba.etf.unsa.rma.aktivnosti.KvizoviAkt;
import ba.etf.unsa.rma.klase.OdgovorClickListener;

import static android.content.Context.ALARM_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;

public class InformacijeFrag extends Fragment {

    private String nazivKviza;
    private int brojTacnihPitanja;
    private int brojPreostalihPitanja;
    private double procentTacnihOdg;
    private boolean bioPokusaj;
    private OdgovorClickListener odgovorClickListener;
    private TextView infNazivKviza;
    private TextView infBrojTacnihPitanja;
    private TextView infBrojPreostalihPitanja;
    private TextView infProcentTacni;
    private Button btnKraj;


    public static InformacijeFrag newInstance(String nazivKviza, int brojTacnihPitanja, int brojPreostalihPitanja, double procentTacnihOdg, boolean bioPokusaj, OdgovorClickListener odgovorClickListener) {
        InformacijeFrag infoFrag = new InformacijeFrag();
        infoFrag.setOdgovorClickListener(odgovorClickListener);
        Bundle bdl = new Bundle();
        bdl.putString("nazivKviza", nazivKviza);
        bdl.putBoolean("bioPokusaj", bioPokusaj);
        bdl.putInt("brojTacnihPitanja", brojTacnihPitanja);
        bdl.putInt("brojPreostalihPitanja", brojPreostalihPitanja);
        bdl.putDouble("ProcentTacnihOdg", procentTacnihOdg);
        bdl.putBoolean("bioPokusaj", bioPokusaj);
        infoFrag.setArguments(bdl);
        return infoFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bdl = getArguments();

        if (bdl != null && bdl.containsKey("nazivKviza") && bdl.containsKey("brojTacnihPitanja") && bdl.containsKey("brojPreostalihPitanja") && bdl.containsKey("ProcentTacnihOdg") && bdl.containsKey("bioPokusaj")) {
            nazivKviza = bdl.getString("nazivKviza");
            brojTacnihPitanja = bdl.getInt("brojTacnihPitanja");
            brojPreostalihPitanja = bdl.getInt("brojPreostalihPitanja");
            procentTacnihOdg = bdl.getDouble("ProcentTacnihOdg");
            bioPokusaj = bdl.getBoolean("bioPokusaj");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacije, container, false);

        infNazivKviza = view.findViewById(R.id.infNazivKviza);
        infBrojTacnihPitanja = view.findViewById(R.id.infBrojTacnihPitanja);
        infBrojPreostalihPitanja = view.findViewById(R.id.infBrojPreostalihPitanja);
        infProcentTacni = view.findViewById(R.id.infProcentTacni);
        btnKraj = view.findViewById(R.id.btnKraj);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        infNazivKviza.setText(nazivKviza);
        infBrojTacnihPitanja.setText(String.valueOf(brojTacnihPitanja));
        infBrojPreostalihPitanja.setText(String.valueOf(brojPreostalihPitanja));
        infProcentTacni.setText(String.format(Locale.getDefault(), "%.3f", procentTacnihOdg));

        postaviBoje();

        btnKraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    void postaviBoje(){

        if(bioPokusaj) {
            if (procentTacnihOdg < 0.5) {
                infProcentTacni.setBackgroundColor(getResources().getColor(R.color.crvena));
            } else if (procentTacnihOdg < 0.8) {
                infProcentTacni.setBackgroundColor(getResources().getColor(R.color.zuta));
            } else {
                infProcentTacni.setBackgroundColor(getResources().getColor(R.color.zelena));
            }

            if (brojTacnihPitanja == 0) {
                infBrojTacnihPitanja.setTextColor(getResources().getColor(R.color.crvena));
            } else {
                infBrojTacnihPitanja.setTextColor(getResources().getColor(R.color.zelena));
            }

        }

    }

    public void setOdgovorClickListener(OdgovorClickListener odgovorClickListener) {
        this.odgovorClickListener = odgovorClickListener;
    }

}

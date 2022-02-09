package ba.etf.unsa.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.klase.OdgovorAdapter;
import ba.etf.unsa.rma.klase.Pitanje;

public class DodajPitanjeAkt extends AppCompatActivity {

    private ArrayList<Pitanje> dodanaPitanja;
    private ArrayList<Pitanje> mogucaPitanja;
    private Pitanje novoPitanje = new Pitanje();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        final EditText etNaziv = findViewById(R.id.etNaziv);
        final ListView listView = findViewById(R.id.lvOdgovori);
        final EditText etOdgovor = findViewById(R.id.etOdgovor);
        Button dugmeDodaj = findViewById(R.id.btnDodajOdgovor);
        final Button dugmeDodajTacan = findViewById(R.id.btnDodajTacan);
        Button dugmeSpasiPitanje = findViewById(R.id.btnDodajPitanje);
        final ArrayList<String> odgovori = new ArrayList<String>();
        final OdgovorAdapter odgovorAdater;

        final Bundle bdl = getIntent().getExtras();

        if(bdl != null && bdl.containsKey("dodanaPitanja") && bdl.containsKey("mogucaPitanja")){
            dodanaPitanja = bdl.getParcelableArrayList("dodanaPitanja");
            mogucaPitanja = bdl.getParcelableArrayList("mogucaPitanja");
        }


        odgovorAdater = new OdgovorAdapter(this, R.layout.icon, odgovori, novoPitanje.getTacan());

        listView.setAdapter(odgovorAdater);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (odgovori.get(position).equals(novoPitanje.getTacan())) {
                    novoPitanje.setTacan("");
                    odgovorAdater.setTacan("");
                    dugmeDodajTacan.setBackground(getResources().getDrawable(R.color.button));
                }
                odgovori.remove(position);
                odgovorAdater.notifyDataSetChanged();
            }
        });

        dugmeDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dodajOdgovor = etOdgovor.getText().toString();
                boolean validacijaOdgovora = !dodajOdgovor.equals("");
                if (validacijaOdgovora) {
                    if( nePostojiOdgovor(odgovori, dodajOdgovor) ) {
                        odgovori.add(dodajOdgovor);
                        odgovorAdater.notifyDataSetChanged();
                        etOdgovor.setText("");
                    }else{
                        Toast.makeText(getApplicationContext(), "Odgovor vec postoji!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    etOdgovor.setBackgroundColor(Color.RED);
                }
            }
        });

        dugmeDodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dodajOdgovor = etOdgovor.getText().toString();
                boolean validacijaOdgovora = !dodajOdgovor.equals("");
                boolean postojiTacan = !novoPitanje.getTacan().equals("");

                if (validacijaOdgovora && !postojiTacan) {
                    if( nePostojiOdgovor(odgovori, dodajOdgovor) ) {
                        odgovori.add(odgovori.size(), dodajOdgovor);
                        novoPitanje.setTacan(dodajOdgovor);
                        odgovorAdater.setTacan(dodajOdgovor);
                        odgovorAdater.notifyDataSetChanged();
                        etOdgovor.setText("");
                    }else{
                        Toast.makeText(getApplicationContext(), "Odgovor vec postoji!", Toast.LENGTH_LONG).show();
                    }
                } else if (!validacijaOdgovora && !postojiTacan) {
                    etOdgovor.setBackgroundColor(Color.RED);
                } else if (validacijaOdgovora) {
                    dugmeDodajTacan.setBackgroundColor(Color.RED);
                } else {
                    etOdgovor.setBackgroundColor(Color.RED);
                    dugmeDodajTacan.setBackgroundColor(Color.RED);
                }
            }
        });

        dugmeSpasiPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validacijaNaziva = !etNaziv.getText().toString().equals("");
                if (!validacijaNaziva) {
                    etNaziv.setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Dodajte naziv pitanja!", Toast.LENGTH_LONG).show();
                }else {
                    int i;
                    for(i=0; i<dodanaPitanja.size(); i++){
                        if(etNaziv.getText().toString().equals(dodanaPitanja.get(i).getNaziv())){
                            break;
                        }
                    }
                    if(i<dodanaPitanja.size()){
                        etNaziv.setBackgroundColor(Color.RED);
                        etNaziv.setText("");
                        validacijaNaziva = false;
                        Toast.makeText(getApplicationContext(), "Pitanje postoji u kvizu!", Toast.LENGTH_LONG).show();
                    }
                    for(i=0; i<mogucaPitanja.size(); i++){
                        if(etNaziv.getText().toString().equals(mogucaPitanja.get(i).getNaziv())){
                            break;
                        }
                    }
                    if(i<mogucaPitanja.size()){
                        etNaziv.setBackgroundColor(Color.RED);
                        etNaziv.setText("");
                        validacijaNaziva = false;
                        Toast.makeText(getApplicationContext(), "Pitanje postoji u mogucim pitanjima!", Toast.LENGTH_LONG).show();
                    }
                }

                boolean postojiTacan = !novoPitanje.getTacan().equals("");

                if (!postojiTacan) {
                    listView.setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Dodajte tačan odgovor!", Toast.LENGTH_LONG).show();
                }

                if (postojiTacan && odgovori.size() < 2) {
                    listView.setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Imate samo tačan odgovor u odgovorima!", Toast.LENGTH_LONG).show();
                }


                if (validacijaNaziva && odgovori.size() > 1 && postojiTacan) {
                    novoPitanje.setNaziv(etNaziv.getText().toString());
                    novoPitanje.setTextPitanja(etNaziv.getText().toString());
                    novoPitanje.setOdgovori(odgovori);
                    novoPitanje.hashCode();
                    spremiPitanje();
                }

            }
        });

    }

    private void spremiPitanje() {
        Intent intent = new Intent();
        Bundle bdl = new Bundle();
        bdl.putParcelable("novoPitanje", novoPitanje);
        intent.putExtras(bdl);
        setResult(200, intent);
        finish();
    }

    private boolean nePostojiOdgovor(ArrayList<String> odgovori, String dodajOdgovor){
        int i;
        for (i=0; i<odgovori.size(); i++){
            if(dodajOdgovor.equals(odgovori.get(i))){
                break;
            }
        }
        if( i == odgovori.size() ) {
            return true;
        }

        return false;
    }

}

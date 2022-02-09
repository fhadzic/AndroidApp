package ba.etf.unsa.rma.aktivnosti;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.klase.DatabaseHelperKategorije;
import ba.etf.unsa.rma.klase.DatabaseHelperKvizovi;
import ba.etf.unsa.rma.klase.DatabaseHelperPitanja;
import ba.etf.unsa.rma.klase.Kategorija;
import ba.etf.unsa.rma.klase.KategorijaAdapter;
import ba.etf.unsa.rma.klase.Kviz;
import ba.etf.unsa.rma.klase.Pitanje;
import ba.etf.unsa.rma.klase.PitanjeDodanoAdapter;
import ba.etf.unsa.rma.klase.PitanjeMoguceAdapter;

public class DodajKvizAkt extends AppCompatActivity {

    private Spinner spKategorije;
    private EditText etNaziv;
    private ListView lvDodanaPitanja;
    private ListView lvMogucaPitanja;
    private Button btnDodajKviz;
    private ArrayList<Kategorija> listaKategorija;
    private KategorijaAdapter kategorijaAdapter;
    private ArrayList<Pitanje> dodanaPitanja;
    private PitanjeDodanoAdapter dodanaAdapter;
    private ArrayList<Pitanje> mogucaPitanja = new ArrayList<Pitanje>();
    private PitanjeMoguceAdapter mogucaAdapter;
    private Kategorija otvorenaKategorija;
    private Kategorija dodajKategoriju = new Kategorija("Dodaj Kategoriju", 1);
    private Pitanje pitanje;
    private String bivsiNaziv = null;
    private Kategorija kategorija;
    private int tip;
    private String idKviza = null;
    private String stariIdKviza = null;

    private DatabaseHelperKategorije myDbK;
    private DatabaseHelperPitanja myDbP;
    private DatabaseHelperKvizovi myDbKviz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);

        myDbK = new DatabaseHelperKategorije(this);
        myDbP = new DatabaseHelperPitanja(this);
        myDbKviz = new DatabaseHelperKvizovi(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("naziv")
                && bundle.containsKey("tip")
                && bundle.containsKey("listaPitanja")
                && bundle.containsKey("kategorija")
                && bundle.containsKey("listaKategorija")
                && bundle.containsKey("id")) {

            bivsiNaziv = bundle.getString("naziv");
            tip = bundle.getInt("tip");
            dodanaPitanja = bundle.getParcelableArrayList("listaPitanja");
            kategorija = bundle.getParcelable("kategorija");
            idKviza = bundle.getString("id");
            stariIdKviza = idKviza;
            listaKategorija = bundle.getParcelableArrayList("listaKategorija");
            listaKategorija.add(listaKategorija.size(), dodajKategoriju);
        }

        spKategorije = findViewById(R.id.spKategorije);
        etNaziv = findViewById(R.id.etNaziv);
        lvDodanaPitanja = findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = findViewById(R.id.lvMogucaPitanja);
        btnDodajKviz = findViewById(R.id.btnDodajKviz);
        Button btnUveziKviz = findViewById(R.id.btnImportKviz);


        if (kategorija == null) {
            kategorija = new Kategorija();
        }


        kategorijaAdapter = new KategorijaAdapter(this, listaKategorija);
        spKategorije.setAdapter(kategorijaAdapter);

        dodanaAdapter = new PitanjeDodanoAdapter(this, R.layout.icon, dodanaPitanja);
        lvDodanaPitanja.setAdapter(dodanaAdapter);

        mogucaAdapter = new PitanjeMoguceAdapter(this, R.layout.icon, mogucaPitanja);
        lvMogucaPitanja.setAdapter(mogucaAdapter);


        new DobaviMogucaPitanja().execute("Pitanja");


        // Ispisi podatke o kvizu koji se edituje
        if (tip == 0) {
            etNaziv.setText(bivsiNaziv);
            int index;

            for (index = 0; index < listaKategorija.size(); index++) {
                if (kategorija.getNaziv().equals(listaKategorija.get(index).getNaziv())) {
                    break;
                }
            }
            if (index != listaKategorija.size()) {
                spKategorije.setSelection(index);
            }
        }


        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                otvorenaKategorija = kategorijaAdapter.get(position);

                if (otvorenaKategorija.getTip() == 1) {
                    otvoriKategoriju();
                } else {
                    kategorija.setNaziv(otvorenaKategorija.getNaziv());
                    kategorija.setId(otvorenaKategorija.getId());
                    kategorija.setTip(otvorenaKategorija.getTip());
                    kategorija.hashCode();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        lvDodanaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dodanaPitanja.get(position).getTip() == 0) {
                    mogucaAdapter.dodajMogucePitanje(dodanaPitanja.get(position));
                    dodanaAdapter.obrisiPitanje(dodanaPitanja.get(position));
                } else {
                    otvoriPitanje();
                }
            }
        });



        lvMogucaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dodanaAdapter.postaviPitanje(mogucaPitanja.get(position));
                mogucaAdapter.obrisiPitanje(mogucaPitanja.get(position));
            }
        });


// Validacija Kviza
        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nazivValidan = !etNaziv.getText().toString().equals("");
                if (!nazivValidan) {
                    etNaziv.setBackgroundColor(Color.RED);
                }

                boolean kategorijaValidna = (kategorija != null && kategorija.getTip() != 2);
                if (!kategorijaValidna) {
                    spKategorije.setBackgroundColor(Color.RED);
                }

                if (nazivValidan && kategorijaValidna) {
                    spremiKviz(etNaziv.getText().toString());
                }
            }
        });


        btnUveziKviz.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(DodajKvizAkt.this, "Dugi klik!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }

    private void spremiKviz(String naziv) {

        Kviz kviz = new Kviz(naziv, dodanaPitanja, kategorija, 0);
        kviz.hashCode();
        idKviza = kviz.getIdFireBase();

        new ProvjeriDaLiPostojiKvizIUpisiGaUFirebase().execute("Kvizovi");

        ProvjeriDaLiPostojiKvizIUpisiGaUSQLite();

        Intent mainIntent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("naziv", etNaziv.getText().toString());
        bundle.putString("bivsiNaziv", bivsiNaziv);
        bundle.putInt("tip", 0);
        bundle.putParcelable("kategorija", kategorija);


        Collections.sort(listaKategorija);
        listaKategorija.remove(dodajKategoriju);
        bundle.putParcelableArrayList("listaKategorija", listaKategorija);
        bundle.putParcelableArrayList("listaPitanja", dodanaPitanja);
        mainIntent.putExtras(bundle);

        setResult(100, mainIntent);
        finish();

    }


    public void ProvjeriDaLiPostojiKvizIUpisiGaUSQLite(){
        if(tip == 0){
            DeleteSQLiteDataKviz(stariIdKviza);
            AddSQLiteDataKviz(idKviza);
        }else{
            AddSQLiteDataKviz(idKviza);
        }
    }


    public void DeleteSQLiteDataKviz(String id) {

        Integer deletedRows = myDbKviz.deleteData(id);

        if (!(deletedRows > 0)){
            Toast.makeText(getApplicationContext(), "Data not Deleted", Toast.LENGTH_SHORT).show();
        }

    }

    public void AddSQLiteDataKviz(String id) {
        kategorija.hashCode();
        String idPitanja = "";
        int i = 0;
        while ( i < dodanaPitanja.size()-1) {
            dodanaPitanja.get(i).hashCode();
            if (i != dodanaPitanja.size() - 1) {
                idPitanja += dodanaPitanja.get(i).getIdFireBase() + "Ł";
            } else {
                idPitanja += dodanaPitanja.get(i).getIdFireBase();
            }
            i++;
        }

        boolean isInserted = myDbKviz.insertData(id, etNaziv.getText().toString(), kategorija.getIdFireBase(), idPitanja);
        if (isInserted != true){
            Toast.makeText(getApplicationContext(), "Kviz not inserted", Toast.LENGTH_SHORT).show();
        }
    }


    private void otvoriPitanje() {
        Intent pitanjeIntent = new Intent(getApplicationContext(), DodajPitanjeAkt.class);
        Bundle bdl = new Bundle();
        bdl.putParcelableArrayList("dodanaPitanja", dodanaPitanja);
        bdl.putParcelableArrayList("mogucaPitanja", mogucaAdapter.getMogucaPitanja());
        pitanjeIntent.putExtras(bdl);
        startActivityForResult(pitanjeIntent, 200);
    }

    private void otvoriKategoriju() {
        Intent kategorijaIntent = new Intent(getApplicationContext(), DodajKategorijuAkt.class);
        Bundle bdl = new Bundle();
        bdl.putParcelableArrayList("listaKategorija", listaKategorija);
        kategorijaIntent.putExtras(bdl);
        startActivityForResult(kategorijaIntent, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) { //Pitanje
            if (data != null && data.getExtras() != null && data.getExtras().containsKey("novoPitanje")) {
                pitanje = (Pitanje) data.getParcelableExtra("novoPitanje");
                String odgovori = "";
                int j = 0;
                for (String o : pitanje.getOdgovori()) {// odgovore stavljam u string kako bih mogao dodat u bazu
                    if (j++ != pitanje.getOdgovori().size() - 1)
                        odgovori += o + "Ł";
                    else odgovori += o;
                }


                new DodajObrisiBaza().execute("Pitanja", String.valueOf(pitanje.hashCode()), pitanje.getNaziv(), odgovori,
                        String.valueOf(pitanje.getIndexTacnog()));

                insertInSQLiteDataBasePitanje(odgovori);
                dodanaAdapter.postaviPitanje(pitanje);
            }
        }

        if (requestCode == 300) { // Kategorija
            if (data != null && data.getExtras() != null && data.getExtras().containsKey("naziv") && data.getExtras().containsKey("ikona")) {
                kategorija.setNaziv(data.getStringExtra("naziv"));
                kategorija.setId(data.getStringExtra("ikona"));
                kategorija.setTip(0);
                kategorija.hashCode();

                new DodajObrisiBaza().execute("Kategorije", String.valueOf(kategorija.getIdFireBase())); // dodajem u Firebase
                insertInSQLiteDataBaseKategorija();

                listaKategorija.add(kategorija);
                Collections.sort(listaKategorija);
                kategorijaAdapter.formatirajListu(listaKategorija);

                //Pozicija u spinneru nakon dodavanja nove kategorije u listu
                int position;
                for (position = 0; position < listaKategorija.size(); position++) {
                    if (kategorija.getNaziv().equals(listaKategorija.get(position).getNaziv())) {
                        break;
                    }
                }
                spKategorije.setSelection(position);

            }
        }

    }

    private void insertInSQLiteDataBaseKategorija() {
        boolean isInserted = myDbK.insertData(kategorija.getIdFireBase(), kategorija.getNaziv(), kategorija.getId());
        if (isInserted == false) {
            Toast.makeText(getApplicationContext(), "Kategorija not inserted", Toast.LENGTH_SHORT).show();
        }

    }



    private void insertInSQLiteDataBasePitanje(String odgovori) {
        boolean isInserted = myDbP.insertData(pitanje.getIdFireBase(), pitanje.getNaziv(), String.valueOf(pitanje.getIndexTacnog()), odgovori);
        if (isInserted == false) {
            Toast.makeText(getApplicationContext(), "Pitanje not inserted", Toast.LENGTH_SHORT).show();
        }

    }




    private class ProvjeriDaLiPostojiKvizIUpisiGaUFirebase extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... urls) {
            Integer povratni = 0;
            try {
                String TOKEN = dajToken();

                String url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents/" + urls[0] + "/" + idKviza + "?access_token=" + TOKEN;
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                return -1;
            }
            return povratni;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            if (aVoid == -1) {
                if (tip == 0) {
                    new DodajObrisiBaza().execute("Kvizovi", stariIdKviza, "obrisi"); // brisem postojeci iz Firebase
                    new DodajObrisiBaza().execute("Kvizovi", idKviza); // dodajem novi kviz u Firebasu

                } else {
                    new DodajObrisiBaza().execute("Kvizovi", idKviza); // dodajem u Firebase
                }

            } else {
                if (bivsiNaziv.equals(etNaziv.getText().toString()) && tip == 0) {
                    new DodajObrisiBaza().execute("Kvizovi", stariIdKviza, "obrisi"); // brisem postojeci iz Firebase
                    new DodajObrisiBaza().execute("Kvizovi", idKviza); // dodajem novi kviz u Firebase

                } else {
                    okiniDijalog("Kviz vec postoji!");
                }
            }
        }

    }



    private class DodajObrisiBaza extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... urls) {//prvi param kolekcija drugi sta ubacujemo
            String token = dajToken();
            String url1;
            url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents/" + urls[0] + "?documentId=" + urls[1] + "&access_token=" + token;
            if (tip == 0 && urls[0].equals("Kvizovi"))
                url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + token;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                if (urls.length > 2 && tip == 0 && urls[0].equals("Kvizovi")) {
                    urlConnection.setRequestMethod("DELETE");
                } else if (tip == 0 && urls[0].equals("Kvizovi")) {
                    urlConnection.setRequestMethod("PATCH");
                } else {
                    urlConnection.setRequestMethod("POST");
                }
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                String dokument = "";
                if (urls.length > 2 && tip == 0 && urls[0].equals("Kvizovi")) {
                    // brisanje ne zahtjeva tijelo
                } else if (urls[0].equalsIgnoreCase("Kategorije")) {
                    dokument = "{ \"fields\": { \"idIkonice\": { \"integerValue\": \"" + kategorija.getId() + "\"}, \"naziv\": { \"stringValue\": \"" + kategorija.getNaziv() + "\"}}}";
                } else if (urls[0].equalsIgnoreCase("Kvizovi")) {
                    int i = 0;
                    dokument = "{ \"fields\": { \"pitanja\": { \"arrayValue\": { \"values\": [";
                    for (Pitanje p : dodanaPitanja) {
                        if (i < dodanaPitanja.size() - 2)
                            dokument += "{ \"stringValue\": \"" + p.getIdFireBase() + "\"}, ";
                        else if (i == dodanaPitanja.size() - 2)
                            dokument += "{ \"stringValue\": \"" + p.getIdFireBase() + "\"} ";
                        i++;
                    }
                    dokument += "]}}, \"naziv\": { \"stringValue\": \"" + etNaziv.getText().toString() + "\"}, \"idKategorije\": { \"stringValue\": \"" + kategorija.getIdFireBase() + "\"}}}";
                } else if (urls[0].equalsIgnoreCase("Pitanja")) {
                    String[] odgovori = urls[3].split("Ł");
                    int i = 0;
                    dokument = "{ \"fields\": { \"naziv\": { \"stringValue\": \"" + urls[2] + "\"}, \"odgovori\": { \"arrayValue\": { \"values\": [";
                    for (String p : odgovori) {
                        if (i++ != odgovori.length - 1)
                            dokument += "{ \"stringValue\": \"" + p + "\"}, ";
                        else dokument += "{ \"stringValue\": \"" + p + "\"} ";
                    }
                    dokument += "]}}, \"indexTacnog\": { \"integerValue\": \"" + urls[4] + "\"}}}";
                }

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = dokument.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = urlConnection.getResponseCode();
                InputStream odgovor = urlConnection.getInputStream();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("ODGOVOR", response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }


    private class DobaviMogucaPitanja extends AsyncTask<String, Integer, ArrayList<Pitanje>> {
        protected ArrayList<Pitanje> doInBackground(String... urls) {//prvi param kolekcija drugi id dokumenta
            String token = dajToken();
            String url1;
            ArrayList<Pitanje> listaMogucih = new ArrayList<>();
            if (urls.length == 1)
                url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents/" + urls[0] + "?access_token=" + token;
            else
                url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + token;
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = convertStreamToString(in);
                JSONObject jo = null;
                jo = new JSONObject(rezultat);
                JSONArray items = new JSONArray();
                if (urls[0].equalsIgnoreCase("Pitanja")) {
                    items = jo.getJSONArray("documents");
                    ArrayList<Pitanje> pitanja = ucitajSvaPitanjaIzBaze(items);
                    for (Pitanje p : pitanja) {
                        boolean postoji = false;
                        for (int i = 0; i < dodanaPitanja.size() - 1; i++) {
                            if (dodanaPitanja.get(i).getNaziv().equals(p.getNaziv())
                                    && dodanaPitanja.get(i).getTacan().equals(p.getTacan())
                                    && dodanaPitanja.get(i).getIdFireBase().equals(p.getIdFireBase())
                                    && dodanaPitanja.get(i).getOdgovori().equals(p.getOdgovori())) {
                                postoji = true;
                            }
                        }
                        if (!postoji) {
                            listaMogucih.add(p);
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return listaMogucih;
        }


        @Override
        protected void onPostExecute(ArrayList<Pitanje> lista) {
            for (int i = 0; i < lista.size(); i++) {
                lista.get(i).setTip(1);
            }
            mogucaPitanja.addAll(lista);
            mogucaAdapter.notifyDataSetChanged();
        }
    }


    public static ArrayList<Pitanje> ucitajSvaPitanjaIzBaze(JSONArray items) {
        ArrayList<Pitanje> pitanjaIzBaze = new ArrayList<>();
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject name = null;
                name = items.getJSONObject(i);
                JSONObject kviz = name.getJSONObject("fields");
                String naziv = kviz.getJSONObject("naziv").getString("stringValue");
                int indexTacnog = Integer.parseInt(kviz.getJSONObject("indexTacnog").getString("integerValue"));
                ArrayList<String> odgovori = new ArrayList<String>();
                JSONArray jArray = kviz.getJSONObject("odgovori").getJSONObject("arrayValue").getJSONArray("values");
                for (int j = 0; j < jArray.length(); j++) {
                    odgovori.add(jArray.getJSONObject(j).getString("stringValue"));
                }
                Pitanje pitanje = new Pitanje(naziv, odgovori, odgovori.get(indexTacnog), 0);
                pitanje.hashCode();
                pitanjaIzBaze.add(pitanje);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pitanjaIzBaze;
    }


    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }


    public String dajToken() {
        InputStream is = getResources().openRawResource(R.raw.secret);
        GoogleCredential credentials = null;
        try {
            credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
            credentials.refreshToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credentials.getAccessToken();
    }


    public void okiniDijalog(String poruka) {
        AlertDialog alertDialog = new AlertDialog.Builder(DodajKvizAkt.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(poruka);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}


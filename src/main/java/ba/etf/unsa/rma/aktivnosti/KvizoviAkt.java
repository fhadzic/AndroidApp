package ba.etf.unsa.rma.aktivnosti;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.klase.DatabaseHelperKategorije;
import ba.etf.unsa.rma.klase.DatabaseHelperKvizovi;
import ba.etf.unsa.rma.klase.DatabaseHelperPitanja;
import ba.etf.unsa.rma.klase.IDobaviKvizove;
import ba.etf.unsa.rma.klase.Kategorija;
import ba.etf.unsa.rma.klase.KategorijaAdapter;
import ba.etf.unsa.rma.klase.Kviz;
import ba.etf.unsa.rma.klase.KvizAdapter;
import ba.etf.unsa.rma.klase.Pitanje;

/**
 * @author fhadzic1@etf.unsa.ba
 */

public class KvizoviAkt extends AppCompatActivity implements IDobaviKvizove {

    public ArrayList<Kategorija> listaKategorija = new ArrayList<Kategorija>();

    public KvizAdapter adapterKviz;
    public KategorijaAdapter adapterKategorija;
    public Kategorija svi = new Kategorija("Svi", 2);

    private Spinner spinner;
    private ListView listView;

    private Kategorija odabranaKategorija = new Kategorija();
    private ArrayList<Pitanje> listaSvihPitanja = new ArrayList<>();

    private Kviz otvoreniKviz;
    private DatabaseHelperKategorije myDbK;
    private DatabaseHelperPitanja myDbP;
    private DatabaseHelperKvizovi myDbKviz;
    private Boolean appOtvorenaPrviPut = true;

    @Override
    protected void onResume() {
        super.onResume();

        myDbK = new DatabaseHelperKategorije(this);
        myDbP = new DatabaseHelperPitanja(this);
        myDbKviz = new DatabaseHelperKvizovi(this);

        if (!isNetworkAvailable() && appOtvorenaPrviPut) {
            appOtvorenaPrviPut = false;

            ucitajKategorijeIzSQLite();
            ucitajPitanjaIzSQLite();
            ucitajKvizoveIzSQLite();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Listu kvizova sam cuvao u KvizAdapter

        spinner = findViewById(R.id.spPostojeceKategorije);
        listView = findViewById(R.id.lvKvizovi);

        adapterKategorija = new KategorijaAdapter(this, listaKategorija);
        spinner.setAdapter(adapterKategorija);

        adapterKviz = new KvizAdapter(this, R.layout.icon);
        listView.setAdapter(adapterKviz);


        listaKategorija.add(svi);
        adapterKategorija.notifyDataSetChanged();
        adapterKviz.dodajKvizIObrisiOstale();

        if (isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Firebase Database!", Toast.LENGTH_SHORT).show();
            DobaviIzBaze dobaviIzBaze = new DobaviIzBaze();
            dobaviIzBaze.delegat = this;
            dobaviIzBaze.execute("Kategorije");
/*
            deleteDatabase("kategorije.db");
            deleteDatabase("pitanja.db");
            deleteDatabase("kvizovi.db");
*/
        } else {
            Toast.makeText(getApplicationContext(), "SQLite Database!", Toast.LENGTH_SHORT).show();
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                odabranaKategorija = adapterKategorija.get(position);
                if (odabranaKategorija.getNaziv().equals("Svi")) {
                    if(isNetworkAvailable()) {
                        dobaviSveKvizoveizBaze();
                    }else{
                        adapterKviz.filterSQLite(odabranaKategorija);
                    }
                } else {
                    if(isNetworkAvailable()) {
                        new DobaviSveKvizovePoKategoriji().execute();
                    }else{
                        adapterKviz.filterSQLite(odabranaKategorija);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

// Otvaranje aktivnosti za Dugi KLIK NA KVIZ
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Kviz otvoreniKviz = adapterKviz.getFiltriraniKvizovi().get(position);
                if(isNetworkAvailable()) {
                    otvoriKviz(otvoreniKviz);
                }
                return true;
            }
        });


// Otvaranje aktivnosti za Kratki KLIK NA KVIZ
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                otvoreniKviz = adapterKviz.getFiltriraniKvizovi().get(position);
                if (otvoreniKviz.getTip() == 0) {
                    if (ActivityCompat.checkSelfPermission(KvizoviAkt.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(KvizoviAkt.this, new String[]{Manifest.permission.READ_CALENDAR}, 122);
                    } else {
                        otvoriIgrajKviz(otvoreniKviz);
                    }
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (data != null && data.getExtras() != null
                    && data.getExtras().containsKey("naziv")
                    && data.getExtras().containsKey("bivsiNaziv")
                    && data.getExtras().containsKey("listaKategorija")
                    && data.getExtras().containsKey("listaPitanja")
                    && data.getExtras().containsKey("tip")
                    && data.getExtras().containsKey("kategorija")) {

                String bivsiNaziv = data.getStringExtra("bivsiNaziv");
                Kviz kviz = new Kviz();
                kviz.setNaziv(data.getStringExtra("naziv"));
                kviz.setTip(data.getIntExtra("tip", 0));
                kviz.setPitanja(data.<Pitanje>getParcelableArrayListExtra("listaPitanja"));
                kviz.setKategorija(data.<Kategorija>getParcelableExtra("kategorija"));
                kviz.hashCode();

                listaKategorija = data.getParcelableArrayListExtra("listaKategorija");
                adapterKategorija.formatirajListu(listaKategorija);
                adapterKviz.addKviz(kviz, bivsiNaziv);
                adapterKviz.filtrirajListu(svi);

                odabranaKategorija = svi;
                spinner.setSelection(listaKategorija.size()-1);

            }
        }
    }



    private void otvoriKviz(Kviz kviz) {
        Intent kvizIntent = new Intent(this, DodajKvizAkt.class);
        Bundle b = new Bundle();
        b.putString("naziv", kviz.getNaziv());
        b.putInt("tip", kviz.getTip());
        b.putParcelableArrayList("listaPitanja", kviz.getPitanja());
        b.putParcelable("kategorija", kviz.getKategorija());
        b.putString("id", kviz.getIdFireBase());

        //   b.putParcelableArrayList("listaKvizova", adapterKviz.getSviKvizovi());
        b.putParcelableArrayList("listaKategorija", listaKategorija);
        kvizIntent.putExtras(b);
        startActivityForResult(kvizIntent, 100);
    }

    private void otvoriIgrajKviz(Kviz kviz) {
        long x =(long) (kviz.getPitanja().size() - 1) * 30000;
        long y = provjeriEvent(x);
        if (y > 0) {
            float preostaleMinute = (float) y / 1000 / 60;
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("AlertDialog");
            alertDialog.setMessage(String.format(Locale.getDefault(), "Imate događaj u kalendaru za %3f minuta!", preostaleMinute));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        } else {
            Intent igrajIntent = new Intent(this, IgrajKvizAkt.class);
            Bundle bdl = new Bundle();
            bdl.putString("naziv", kviz.getNaziv());
            bdl.putParcelableArrayList("ListaPitanja", kviz.getPitanja());
            igrajIntent.putExtras(bdl);
            startActivityForResult(igrajIntent, 500);
        }
    }



    public void ucitajKvizoveIzSQLite(){
        Cursor res = myDbKviz.getAllData();
        if(res.getCount() == 0){
            return;
        }

        ArrayList<Kviz> sviKvizovi = new ArrayList<>();

        while(res.moveToNext()){
            Kviz k = new Kviz();
            k.setIdFireBase( res.getString(0) );
            k.setNaziv( res.getString(1) );
            String idKategorije = res.getString(2);
            int i=0;
            for(Kategorija kat : listaKategorija){
                if(kat.getIdFireBase().equals(idKategorije)){
                    break;
                }
                i++;
            }
            k.setKategorija(listaKategorija.get(i));

            String[] idPitanja = res.getString(3).split("Ł");

            ArrayList<Pitanje> pitanjaAL = new ArrayList<>();
            for(String o : idPitanja){
                for(i=0; i < listaSvihPitanja.size(); i++){
                    if(o.equals(listaSvihPitanja.get(i).getIdFireBase())){
                        pitanjaAL.add(listaSvihPitanja.get(i));
                    }
                }
            }
            pitanjaAL.add(new Pitanje("Dodaj Pitanje", 1));
            Collections.sort(pitanjaAL);
            k.setPitanja(pitanjaAL);
            sviKvizovi.add(k);
        }
        sviKvizovi.add(new Kviz("Dodaj Kviz", 1));
        adapterKviz.setAllKvizsSQLite(sviKvizovi);
        adapterKviz.notifyDataSetChanged();
        spinner.setSelection(listaKategorija.size()-1);
    }

    public  void ucitajPitanjaIzSQLite(){
        Cursor res = myDbP.getAllData();
        if(res.getCount() == 0){
            return;
        }

        listaSvihPitanja.clear();
        while(res.moveToNext()){
            Pitanje p = new Pitanje();
            p.setIdFireBase( res.getString(0) );
            p.setNaziv( res.getString(1) );
            String indexTacnog =  res.getString(2) ;
            String[] odgovori =  res.getString(3).split("Ł");

            ArrayList<String> odgAL = new ArrayList<>();
            int i=0;
            for(String o : odgovori){
                if(Integer.valueOf(indexTacnog) == i){
                    p.setTacan(o);
                }
                if( i++ != odgovori.length - 1 ){
                    odgAL.add(o);
                }else{
                    odgAL.add(o);
                }
            }

            p.setOdgovori(odgAL);
            p.setTip(0);
            listaSvihPitanja.add(p);
        }

    }

    public void ucitajKategorijeIzSQLite() {

        Cursor res = myDbK.getAllData();

        listaKategorija.clear();
        dodajSviKategorijuUSpinner();

        if (res.getCount() == 0) {
            return;
        }


        while (res.moveToNext()) {
            Kategorija k = new Kategorija();
            k.setIdFireBase( res.getString(0) );
            k.setNaziv( res.getString(1) );
            k.setId( res.getString(2) );
            listaKategorija.add(k);
        }

        Collections.sort(listaKategorija);
        adapterKategorija.formatirajListu(listaKategorija);
        odabranaKategorija = svi;
        spinner.setSelection(listaKategorija.size() - 1);

    }



    public long provjeriEvent(long x) {
        Date date = Calendar.getInstance().getTime();
        long dtStartKviza = date.getTime();

        String[] projection = {
                "_id",
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
        };
        String selection = "(" + CalendarContract.Events.DTSTART + " >= " + dtStartKviza + " )";

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                long dtStart = cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTSTART));
                if ((dtStart - dtStartKviza < x)) {
                    return (dtStart - dtStartKviza);
                }
            }
        }
        return -1;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 122) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                otvoriIgrajKviz(otvoreniKviz);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 911);
            }
        }
    }



    public class DobaviIzBaze extends AsyncTask<String, Integer, ArrayList<?>> {

        public IDobaviKvizove delegat = null;

        protected ArrayList<?> doInBackground(String... urls) {           //prvi param kolekcija drugi id dokumenta

            InputStream is = getResources().openRawResource(R.raw.secret);
            GoogleCredential credentials = null;
            try {
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String TOKEN = credentials.getAccessToken();
            String url1;
            ArrayList<?> lista = new ArrayList<>();
            if (urls.length == 1) {
                url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents/" + urls[0] + "?access_token=" + TOKEN;
            } else {
                url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents/" + urls[0] + "/" + urls[1] + "?access_token=" + TOKEN;
            }
            URL url;
            try {
                url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = DodajKvizAkt.convertStreamToString(in);
                JSONObject jo = null;
                jo = new JSONObject(rezultat);
                JSONArray items = new JSONArray();
                if (urls[0].equalsIgnoreCase("Kvizovi")) {
                    items = jo.getJSONArray("documents");
                    lista = ucitajKvizoveOdabraneKategorije(items, 1);
                } else if (urls.length == 3 && urls[2].equalsIgnoreCase("Kategorija")) {
                    odabranaKategorija = ucitajKategoriju(jo);
                } else if (urls[0].equalsIgnoreCase("Kategorije")) {
                    items = jo.getJSONArray("documents");
                    lista = ucitajSveKategorijeIzBaze(items);

                } else if (urls[0].equalsIgnoreCase("Pitanja")) {
                    items = jo.getJSONArray("documents");
                    listaSvihPitanja = DodajKvizAkt.ucitajSvaPitanjaIzBaze(items);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return lista;
        }

        @Override
        protected void onPostExecute(ArrayList<?> lista) {
            if (lista.size() != 0)
                delegat.processFinish(lista);
        }
    }

    @Override
    public void processFinish(ArrayList<?> output) {
        if (output.get(0).getClass() == Kategorija.class) {

            ArrayList<Kategorija> kategorijas = (ArrayList<Kategorija>) output;
            listaKategorija.clear();
            dodajSviKategorijuUSpinner();
            listaKategorija.addAll(kategorijas);
            Collections.sort(listaKategorija);
            adapterKategorija.formatirajListu(listaKategorija);
            odabranaKategorija = svi;
            spinner.setSelection(listaKategorija.size() - 1);

        } else {
            ArrayList<Kviz> kvizovi = (ArrayList<Kviz>) output;
            adapterKviz.addAllKvizoveFromTheFireBase(kvizovi);

            if (adapterKviz != null) adapterKviz.notifyDataSetChanged();
        }
    }

    private void dodajSviKategorijuUSpinner() {
        svi = new Kategorija();
        svi.setNaziv("Svi");
        svi.setTip(2);
        svi.hashCode();
        listaKategorija.add(svi);
    }


    private ArrayList<Kviz> ucitajKvizoveOdabraneKategorije(JSONArray items, int odakleJePozvano) {
        ArrayList<Kviz> kvizoviIzBaze = new ArrayList<>();
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject name = items.getJSONObject(i);
                JSONObject dokument = new JSONObject();
                JSONObject kviz = new JSONObject();
                if (odakleJePozvano == 2) {
                    dokument = name.getJSONObject("document");
                    kviz = dokument.getJSONObject("fields");
                } else {
                    kviz = name.getJSONObject("fields");
                }

                String naziv = kviz.getJSONObject("naziv").getString("stringValue");
                String idKategorije = kviz.getJSONObject("idKategorije").getString("stringValue");
                ArrayList<String> pitanjaIdevi = new ArrayList<String>();
                JSONArray jArray = new JSONArray();
                try {
                    jArray = kviz.getJSONObject("pitanja").getJSONObject("arrayValue").getJSONArray("values");
                } catch (JSONException e) {

                }
                for (int j = 0; j < jArray.length(); j++) {
                    pitanjaIdevi.add(jArray.getJSONObject(j).getString("stringValue"));
                }
                if (i == 0)
                    new DobaviIzBaze().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Pitanja").get();
                for (Kategorija k : listaKategorija) {
                    if (k.getIdFireBase().equals(idKategorije)) {
                        odabranaKategorija = k;
                        break;
                    }
                }
                ArrayList<Pitanje> pitanjaIzKviza = dajOdgovarajucaPitanja(pitanjaIdevi);
                pitanjaIzKviza.add(new Pitanje("Dodaj Pitanje", 1));
                Collections.sort(pitanjaIzKviza);
                kvizoviIzBaze.add(new Kviz(naziv, pitanjaIzKviza, odabranaKategorija, 0));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return kvizoviIzBaze;
    }

    private void dobaviSveKvizoveizBaze() {
        DobaviIzBaze dobaviIzBaze1 = new DobaviIzBaze();
        dobaviIzBaze1.delegat = this;
        adapterKviz.dodajKvizIObrisiOstale();

        dobaviIzBaze1.execute("Kvizovi");
    }


    private class DobaviSveKvizovePoKategoriji extends AsyncTask<String, Integer, ArrayList<Kviz>> {

        protected ArrayList<Kviz> doInBackground(String... urls) {
            InputStream is = getResources().openRawResource(R.raw.secret);
            GoogleCredential credentials = null;
            try {
                credentials = GoogleCredential.fromStream(is).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));
                credentials.refreshToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String TOKEN = credentials.getAccessToken();
            String query = "{\n" +
                    "    \"structuredQuery\": {\n" +
                    "        \"where\" : {\n" +
                    "            \"fieldFilter\" : { \n" +
                    "                \"field\": {\"fieldPath\": \"idKategorije\"}, \n" +
                    "                \"op\":\"EQUAL\", \n" +
                    "                \"value\": {\"stringValue\": \"" + odabranaKategorija.getIdFireBase() + "\"}\n" +
                    "            }\n" +
                    "        },\n" +
                    "        \"select\": { \"fields\": [ {\"fieldPath\": \"idKategorije\"}, {\"fieldPath\": \"naziv\"}, {\"fieldPath\": \"pitanja\"}] },\n" +
                    "        \"from\": [{\"collectionId\": \"Kvizovi\"}],\n" +
                    "       \"limit\": 1000 \n" +
                    "    }\n" +
                    "}";
            String url1 = "https://firestore.googleapis.com/v1/projects/spirala3-f5787/databases/(default)/documents:runQuery?access_token=" + TOKEN;
            ArrayList<Kviz> kvizovi = new ArrayList<>();
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = query.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = urlConnection.getResponseCode();
                InputStream in = urlConnection.getInputStream();
                String rezultat = DodajKvizAkt.convertStreamToString(in);
                rezultat = "{ \"documents\": " + rezultat + "}";
                JSONObject jo = null;
                jo = new JSONObject(rezultat);
                JSONArray items = new JSONArray();
                items = jo.getJSONArray("documents");
                kvizovi = ucitajKvizoveOdabraneKategorije(items, 2);

                try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.e("ODGOVOR", response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return kvizovi;
        }

        @Override
        protected void onPostExecute(ArrayList<Kviz> kvizs) {
            adapterKviz.addAllKvizoveFromTheFireBase(kvizs);
        }
    }


    private ArrayList<Pitanje> dajOdgovarajucaPitanja(ArrayList<String> pitanjaIdevi) {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        for (Pitanje p : listaSvihPitanja) {
            if (pitanjaIdevi.contains(p.getIdFireBase()))
                pitanja.add(p);
        }
        return pitanja;
    }

    public static ArrayList<Kategorija> ucitajSveKategorijeIzBaze(JSONArray items) {
        ArrayList<Kategorija> kategorijeIzBaze = new ArrayList<>();
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject name = null;
                name = items.getJSONObject(i);
                kategorijeIzBaze.add(ucitajKategoriju(name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return kategorijeIzBaze;
    }

    public static Kategorija ucitajKategoriju(JSONObject name) {
        JSONObject kviz = null;
        try {
            kviz = name.getJSONObject("fields");
            String naziv = kviz.getJSONObject("naziv").getString("stringValue");
            String idIkonice = kviz.getJSONObject("idIkonice").getString("integerValue");
            Kategorija kategorija = new Kategorija(naziv, idIkonice, 0);
            kategorija.hashCode();
            return kategorija;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

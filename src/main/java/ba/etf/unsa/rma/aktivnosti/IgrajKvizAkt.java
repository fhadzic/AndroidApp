package ba.etf.unsa.rma.aktivnosti;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.fragmenti.InformacijeFrag;
import ba.etf.unsa.rma.fragmenti.PitanjeFrag;
import ba.etf.unsa.rma.klase.OdgovorClickListener;
import ba.etf.unsa.rma.klase.Pitanje;


public class IgrajKvizAkt extends AppCompatActivity implements OdgovorClickListener {

    private String naziv;
    private ArrayList<Pitanje> listaPitanja = new ArrayList<>();
    private int brojTacnihPitanja = 0;
    private int brojPreostalihPitanja;
    private int ukupanBrojPitanja;
    private double procentTacnihOdg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        Bundle bdl = getIntent().getExtras();

        if (bdl != null && bdl.containsKey("naziv") && bdl.containsKey("ListaPitanja")) {
            naziv = bdl.getString("naziv");
            listaPitanja.addAll(bdl.<Pitanje>getParcelableArrayList("ListaPitanja"));
            listaPitanja.remove(listaPitanja.size() - 1);
            brojTacnihPitanja = 0;
            if (listaPitanja.size() != 0) {
                brojPreostalihPitanja = listaPitanja.size() - 1;
            }


            ukupanBrojPitanja = listaPitanja.size();

            if (ukupanBrojPitanja > 0) {
                pripremiAlarm( ukupanBrojPitanja * 30);
            }

            if (ukupanBrojPitanja != 0) {
                procentTacnihOdg = ((double) brojTacnihPitanja / (double) (ukupanBrojPitanja - brojPreostalihPitanja));
            } else {
                procentTacnihOdg = 0;
            }
        }


        ucitajPitanje();

        ucitajInformacije(false);

    }

    public void pripremiAlarm(int x) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = (calendar.get(Calendar.SECOND) + x)%60;

        int ostatakMin = (calendar.get(Calendar.SECOND) + x) / 60;

        if(sec > 0){
            sec = 0;
            ostatakMin++;
        }

        min = (ostatakMin + min) % 60;

        int ostatakHour = (ostatakMin + min)/60;

        hour = (ostatakHour+hour);

        if(hour>23){
            day++;
            hour = 0;
        }


        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, min);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Igranje kviza, Alarm!");
        startActivity(intent);

    }


    private void ucitajInformacije(boolean bioPokusaj) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.informacijePlace, InformacijeFrag.newInstance(naziv, brojTacnihPitanja, brojPreostalihPitanja, procentTacnihOdg, bioPokusaj, this))
                .commit();
    }

    private void ucitajPitanje() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pitanjePlace, PitanjeFrag.newInstance(dajSlucajnoPitanje(), this))
                .commit();
    }

    private Pitanje dajSlucajnoPitanje() {
        Pitanje postaviPitanje = new Pitanje();

        if (listaPitanja.size() == 0) {
            postaviPitanje.setNaziv("Kviz je završen!");
            postaviPitanje.setTip(1);
            postaviPitanje.setOdgovori(null);
            postaviPitanje.setTacan(null);
            return postaviPitanje;
        }

        Collections.shuffle(listaPitanja);

        postaviPitanje = listaPitanja.get(0);

        listaPitanja.remove(0);

        return postaviPitanje;
    }


    @Override
    public void clicked(final boolean tacan) {
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tacan) {
                    brojTacnihPitanja++;
                }

                procentTacnihOdg = ((double) brojTacnihPitanja / (ukupanBrojPitanja - brojPreostalihPitanja));

                if (brojPreostalihPitanja != 0) {
                    brojPreostalihPitanja--;
                }

                ucitajPitanje();
                ucitajInformacije(true);
            }
        }, 2000);
    }
}

//Alarm na dr nacin

//    AlarmManager alarmManager;
//    PendingIntent pendingIntent;
//    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

/*
    @SuppressLint("DefaultLocale")
    public void pripremiAlarm(double x) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, (int) x * 1000);

        //Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(getApplicationContext(), AlarmReceiverActivity.class);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, String.format("Alarm zvoni za %d sec!", (int) x), Toast.LENGTH_SHORT).show();
    }

    public void zavrsenKviz() {
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);

        Toast.makeText(getApplicationContext(), "Alarm ugasen!", Toast.LENGTH_LONG).show();
    }
*/
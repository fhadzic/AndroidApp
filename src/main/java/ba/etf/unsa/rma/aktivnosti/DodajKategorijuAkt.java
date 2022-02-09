package ba.etf.unsa.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ba.etf.unsa.rma.R;
import ba.etf.unsa.rma.klase.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private EditText etNaziv;
    private EditText etIkona;
    private Button btnDodajIkonu;
    private Button btnDodajKategoriju;
    private ArrayList<Kategorija> listaKategorija;
    private Icon[] selectedIcons;
    IconDialog iconDialog = new IconDialog();
    private String naziv;
    private String idIkona;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);


        Bundle bdl = getIntent().getExtras();
        if (bdl != null && bdl.containsKey("listaKategorija")) {
            listaKategorija = bdl.getParcelableArrayList("listaKategorija");
        }

        etNaziv = findViewById(R.id.etNaziv);
        etIkona = findViewById(R.id.etIkona);
        btnDodajIkonu = findViewById(R.id.btnDodajIkonu);
        btnDodajKategoriju = findViewById(R.id.btnDodajKategoriju);

        btnDodajIkonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");

            }
        });

        btnDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean validacijaNaziva = !etNaziv.getText().toString().equals("");
                boolean postojiIcon = !etIkona.getText().toString().equals("");

                if (!validacijaNaziva && !postojiIcon) {
                    etNaziv.setBackgroundColor(Color.RED);
                    etIkona.setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Unesite podatke!", Toast.LENGTH_LONG).show();
                } else if (!validacijaNaziva && postojiIcon) {
                    etNaziv.setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Dodajte naziv kategorije!", Toast.LENGTH_LONG).show();
                } else if (validacijaNaziva && !postojiIcon) {
                    etIkona.setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "Dodajte ikonu!", Toast.LENGTH_LONG).show();
                } else {

                    int i;
                    for (i = 0; i < listaKategorija.size() - 1; i++) {
                        if (etNaziv.getText().toString().equals(listaKategorija.get(i).getNaziv())) {
                            break;
                        }
                    }

                    if (i < listaKategorija.size() - 1) {
                        etNaziv.setBackgroundColor(Color.RED);
                        etNaziv.setText("");
                        validacijaNaziva = false;
                        okiniDijalog("Postoji kategorija sa istim nazivom, promijenite naziv!");
                    }

                }

                if (validacijaNaziva && postojiIcon) {
                    spremiPodatke();
                }


            }
        });

    }


    private void spremiPodatke() {
        Intent kvizIntent = new Intent();
        Bundle bdl = new Bundle();
        bdl.putString("naziv", etNaziv.getText().toString());
        bdl.putString("ikona", etIkona.getText().toString());
        kvizIntent.putExtras(bdl);
        setResult(300, kvizIntent);
        finish();
    }


    public void okiniDijalog(String poruka) {
        AlertDialog alertDialog = new AlertDialog.Builder(DodajKategorijuAkt.this).create();
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



    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        Icon icon = selectedIcons[0];
        etIkona.setText(String.valueOf(icon.getId()));
    }

}

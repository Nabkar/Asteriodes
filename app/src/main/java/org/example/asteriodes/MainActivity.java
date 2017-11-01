package org.example.asteriodes;

import android.content.Intent;
import android.content.SharedPreferences;
/*import android.preference.EditTextPreference;
import android.preference.Preference;*/
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button bAcercaDe;
    private Button bSalir;
    public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAcercaDe = (Button) findViewById(R.id.button03);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarAcercaDe(null);
            }
        });

        bSalir = (Button) findViewById(R.id.button04);
        bSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarPuntuaciones(null);
            }
        });
        /*bSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        /*final EditTextPreference fragmentos = (EditTextPreference) findPreference("fragmentos");
        fragmentos.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object
                    newValue) {
                int valor=0;
                try {
                    valor = Integer.parseInt((String)newValue);
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "Ha de ser un número",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (valor>=0 && valor<=9) {
                    fragmentos.setSummary(
                            "En cuantos trozos se divide un asteroide ("+valor+")");
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Máximo de fragmentos 9",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });*/
    }

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }


    public void lanzarPreferencias(View view){
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);
    }

    public void lanzarPuntuaciones(View view){
        Intent i = new Intent(this, Puntuaciones.class);
        startActivity(i);
    }

    public void mostrarPreferencias(View view){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = "música: " + pref.getBoolean("musica",true)
                + ", gráficos: " + pref.getString("graficos","3")
                + ", fragmentos: " + pref.getString("fragmentos", "0")
                + ", multijugador: " + pref.getBoolean("multijugador",true)
                + ", jugadores: " + pref.getString("jugadores","2")
                + ", conexion: " + pref.getString("conexion", "0");
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }
        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

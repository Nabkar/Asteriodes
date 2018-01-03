package org.example.asteroides;

import android.content.Intent;
import android.content.SharedPreferences;
/*import android.preference.EditTextPreference;
import android.preference.Preference;*/
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

public class MainActivity extends AppCompatActivity {
    private Button bAcercaDe;
    private Button bSalir;
    private Button bJugar;
    private Button bConfiguracion;
    public static AlmacenPuntuaciones almacen;
    private TextView titulo;
    static final int ACTIV_JUEGO = 0;
    static final int ACTIV_PREFERENCIAS = 1;

    //MediaPlayer mp;
    private SharedPreferences pref;
    private boolean sonidos;

    public static RequestQueue colaPeticiones;
    public static ImageLoader lectorImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, ServicioMusica.class));

        titulo = (TextView) findViewById(R.id.tvTitulo);
        final Animation giro = AnimationUtils.loadAnimation(this,R.anim.giro_con_zoom);
        titulo.startAnimation(giro);

        bJugar = (Button) findViewById(R.id.button01);
        //Animation aparicion = AnimationUtils.loadAnimation(this,R.anim.aparecer);
       // bJugar.startAnimation(aparicion);

        bJugar = (Button) findViewById(R.id.button02);
        //Animation desplazamiento = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_derecha);
        //bJugar.startAnimation(desplazamiento);

        bAcercaDe = (Button) findViewById(R.id.button03);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(giro);
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

        pref = PreferenceManager.getDefaultSharedPreferences(this);
       // mp = MediaPlayer.create(this, R.raw.audio);
        if (pref.getBoolean("sonidos", true)==true) {
            sonidos = true;
            //mp.start();
        } else {
            sonidos = false;
        }

        //Tipo de ALMACÉN DE PUNTUACIONES
        int tipoPuntuacion = Integer.parseInt(pref.getString("puntuaciones", "1"));
        if (tipoPuntuacion == 1) {
            almacen = new AlmacenPuntuacionesPreferencias(this);
        } else if (tipoPuntuacion == 2) {
            almacen = new AlmacenPuntuacionesFicheroInterno(this);
        } else if(tipoPuntuacion == 3) {
            almacen = new AlmacenPuntuacionesFicheroExterno(this);
        } else if(tipoPuntuacion == 4) {
            almacen = new AlmacenPuntuacionesFicheroExtApl(this);
        } else if(tipoPuntuacion == 5) {
            almacen = new AlmacenPuntuacionesRecursoRaw(this);
        } else if(tipoPuntuacion == 6) {
            almacen = new AlmacenPuntuacionesRecursoAssets(this);
        } else if(tipoPuntuacion == 7) {
            almacen = new AlmacenPuntuacionesXML_SAX(this);
        } else if(tipoPuntuacion == 8) {
            almacen = new AlmacenPuntuacionesXML_DOM(this);
        } else if(tipoPuntuacion == 9) {
            almacen = new AlmacenPuntuacionesGson(this);
        } else if(tipoPuntuacion == 10) {
            almacen = new AlmacenPuntuacionesJSon();
        } else if(tipoPuntuacion == 11) {
            almacen = new AlmacenPuntuacionesSQLite(this);
        } else if(tipoPuntuacion == 12) {
            almacen = new AlmacenPuntuacionesSQLiteRel(this);
        } else if(tipoPuntuacion == 13) {
            almacen = new AlmacenPuntuacionesProvider(this);
        } else if(tipoPuntuacion == 14) {
            almacen = new AlmacenPuntuacionesSocket();
        } else if(tipoPuntuacion == 15) {
            almacen = new AlmacenPuntuacionesSW_PHP();
        } else if(tipoPuntuacion == 16) {
            almacen = new AlmacenPuntuacionesSW_PHP_Hosting();
        } else {
            almacen = new AlmacenPuntuacionesArray();
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
                permitNetwork().build());


        //Toast.makeText(this,"onCreate",Toast.LENGTH_SHORT).show();
    }

    /*@Override
    protected void onSaveInstanceState(Bundle estadoGuardado){
        super.onSaveInstanceState(estadoGuardado);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            estadoGuardado.putInt("posicion", pos);
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado){
        super.onRestoreInstanceState(estadoGuardado);
        if (estadoGuardado != null && mp != null) {
            int pos = estadoGuardado.getInt("posicion");
            if (pref.getBoolean("sonidos", true)==true) {
                if (pos == 0) {
                    mp.start();
                } else {
                    mp.seekTo(pos);
                }
            } else {
                mp.stop();
            }
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        /*if (pref.getBoolean("sonidos", true)==true) {
            mp.start();
        }*/
        //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() {
        //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        //mp.pause();
        super.onPause();
    }
    @Override
    protected void onStop() {
        //Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        super.onStop();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        /*if (pref.getBoolean("sonidos", true)==true) {
            mp.start();
        } else {
            mp.stop();
        }*/
        //Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        //Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        stopService(new Intent(MainActivity.this,
                ServicioMusica.class));
        super.onDestroy();
    }

    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        stopService(new Intent(MainActivity.this,
                ServicioMusica.class));
        startActivity(i);
    }


    public void lanzarPreferencias(View view){
        Intent i = new Intent(this, PreferenciasActivity.class);
        stopService(new Intent(MainActivity.this,
                ServicioMusica.class));
        startActivityForResult(i,ACTIV_PREFERENCIAS);
    }

    public void lanzarPuntuaciones(View view){
        Intent i = new Intent(this, Puntuaciones.class);
        stopService(new Intent(MainActivity.this,
                ServicioMusica.class));
        startActivity(i);
    }

    public void lanzarJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        stopService(new Intent(MainActivity.this,
                ServicioMusica.class));
        startActivityForResult(i, ACTIV_JUEGO);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== ACTIV_JUEGO && resultCode==RESULT_OK && data!=null) {
            int puntuacion = data.getExtras().getInt("puntuacion");
            String nombre = "Yo";
            // Mejor leer nombre desde un AlertDialog.Builder o preferencias
            almacen.guardarPuntuacion(puntuacion, nombre,
                    System.currentTimeMillis());
            lanzarPuntuaciones(null);
        }
        if (requestCode== ACTIV_PREFERENCIAS && resultCode==RESULT_OK) {
            int tipoPuntuacion = Integer.parseInt(pref.getString("puntuaciones", "1"));
            if (tipoPuntuacion == 1) {
                almacen = new AlmacenPuntuacionesPreferencias(this);
            } else if (tipoPuntuacion == 2) {
                almacen = new AlmacenPuntuacionesFicheroInterno(this);
            } else if(tipoPuntuacion == 3) {
                almacen = new AlmacenPuntuacionesFicheroExterno(this);
            } else if(tipoPuntuacion == 4) {
                almacen = new AlmacenPuntuacionesFicheroExtApl(this);
            } else if(tipoPuntuacion == 5) {
                almacen = new AlmacenPuntuacionesRecursoRaw(this);
            } else if(tipoPuntuacion == 6) {
                almacen = new AlmacenPuntuacionesRecursoAssets(this);
            } else if(tipoPuntuacion == 7) {
                almacen = new AlmacenPuntuacionesXML_SAX(this);
            } else if(tipoPuntuacion == 8) {
                almacen = new AlmacenPuntuacionesXML_DOM(this);
            } else if(tipoPuntuacion == 9) {
                almacen = new AlmacenPuntuacionesGson(this);
            } else if(tipoPuntuacion == 10) {
                almacen = new AlmacenPuntuacionesJSon();
            } else if(tipoPuntuacion == 11) {
                almacen = new AlmacenPuntuacionesSQLite(this);
            } else if(tipoPuntuacion == 12) {
                almacen = new AlmacenPuntuacionesSQLiteRel(this);
            } else if(tipoPuntuacion == 13) {
                almacen = new AlmacenPuntuacionesProvider(this);
            } else if(tipoPuntuacion == 14) {
                almacen = new AlmacenPuntuacionesSocket();
            } else if(tipoPuntuacion == 15) {
                almacen = new AlmacenPuntuacionesSW_PHP();
            } else if(tipoPuntuacion == 16) {
                almacen = new AlmacenPuntuacionesSW_PHP_Hosting();
            } else {
                almacen = new AlmacenPuntuacionesArray();
            }
        }
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

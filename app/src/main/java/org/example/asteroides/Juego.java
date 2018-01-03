package org.example.asteroides;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Julio on 05/11/2017.
 */

public class Juego extends Activity {
    private VistaJuego vistaJuego;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
        vistaJuego.setPadre(this);
    }

    @Override
    protected void onPause() {
        vistaJuego.getThread().pausar();
        vistaJuego.desactivaSensores();
        vistaJuego.getSoundPool().autoPause();
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        vistaJuego.getThread().reanudar();
        vistaJuego.activaSensores();
        vistaJuego.getSoundPool().autoResume();
    }
    @Override
    protected void onDestroy() {
        vistaJuego.getThread().detener();
        vistaJuego.soundPool.autoPause();
        super.onDestroy();
    }
}

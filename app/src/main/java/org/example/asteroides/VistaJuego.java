package org.example.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VistaJuego extends View implements SensorEventListener {
    // //// ASTEROIDES //////
    private List<Grafico> asteroides; // Lista con los Asteroides
    private int numAsteroides = 3; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide
    private Drawable drawableAsteroide[] = new Drawable[3];

    // //// NAVE //////
    private Grafico nave; // Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20;
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    // //// MISIL //////
    private Grafico misil;
    private static int PASO_VELOCIDAD_MISIL = 12;
    private boolean misilActivo = false;
    private int tiempoMisil;
    //private Vector<Grafico> misiles;
    //private Vector<Integer> tiempoMisiles;

    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;

    private float mX=0, mY=0;
    private boolean  disparo = false;

    private boolean hayValorInicial = false;
    private float valorInicial;

    private SensorManager mSensorManager;
    private Sensor accSensor;

    // //// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;
    private boolean sonidos;

    // //// PUNTUACIONES //////
    private int puntuacion = 0;
    private Activity padre;


    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableNave, drawableMisil;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(!pref.getString("fragmentos","3").equals("3")){
            numFragmentos = Integer.parseInt(pref.getString("fragmentos","3"));
        }

        //Si modo de gráficos vectoriales
        if (pref.getString("graficos", "1").equals("0")) {
            //Asteroides
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);

            for (int i=0; i<numAsteroides; i++) {
                ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(
                        pathAsteroide, 1, 1));
                dAsteroide.getPaint().setColor(Color.WHITE);
                dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
                dAsteroide.setIntrinsicWidth(50 - i * 14);
                dAsteroide.setIntrinsicHeight(50 - i * 14);
                drawableAsteroide[i] = dAsteroide;
            }
            setBackgroundColor(Color.BLACK);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            //Misiles
            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;
        } else {
            drawableAsteroide[0] = context.getResources().getDrawable(R.drawable.asteroide1);
            ContextCompat.getDrawable(context, R.drawable.asteroide1);
            drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.asteroide2);
            ContextCompat.getDrawable(context, R.drawable.asteroide2);
            drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.asteroide3);
            ContextCompat.getDrawable(context, R.drawable.asteroide3);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);

            drawableMisil = context.getResources().getDrawable(R.drawable.misil1);
            ContextCompat.getDrawable(context, R.drawable.misil1);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        asteroides = new ArrayList<Grafico>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafico asteroide = new Grafico(this, drawableAsteroide[0]);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }

        //Nave
        drawableNave = context.getResources().getDrawable(R.drawable.nave);
        ContextCompat.getDrawable(context, R.drawable.nave);
        nave = new Grafico(this, drawableNave);

        //Misil
        misil = new Grafico(this,drawableMisil);

        //Sensores
        /*SensorManager */mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        /*if (pref.getBoolean("orientacion", true)==true) {
            List<Sensor> orientationSensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
            if (!orientationSensors.isEmpty()) {
                Sensor oriSensor = orientationSensors.get(0);
                mSensorManager.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }*/

        if (pref.getBoolean("acelerometro", true)==true) {
            List<Sensor> acceleratorsSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (!acceleratorsSensors.isEmpty()) {
                accSensor = acceleratorsSensors.get(0);
                //mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }

        //Multimedia
        soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);
        if (pref.getBoolean("sonidos", true)==true) {
            sonidos = true;
        }
    }

    // Métodos necesarios para la implementación de la interfaz SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_ORIENTATION:
                float valor = event.values[1];
                if (!hayValorInicial) {
                    valorInicial = valor;
                    hayValorInicial = true;
                }
                giroNave = (int) (valor - valorInicial/3);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                float /*x0 = 0,*/ y0 = 0;
                float /*x1,*/ y1;
                if (!hayValorInicial) {
                    //x0=event.values[0];
                    y0=event.values[1];
                    hayValorInicial = true;
                    //x1=x0;
                    y1=y0;
                } else {
                    //x1=event.values[0];
                    y1=event.values[1];
                }
                giroNave = (int) (y0-y1);//((x1-x0) + (y1-y0));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        // Una vez que conocemos nuestro ancho y alto.
        nave.setCenX(ancho/2);
        nave.setCenY(alto/2);

        for (Grafico asteroide: asteroides) {
            do {
                asteroide.setCenX((int) (Math.random()*ancho));
                asteroide.setCenY((int) (Math.random()*alto));
            } while(asteroide.distancia(nave) < (ancho+alto)/5);
        }

        ultimoProceso = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (asteroides) {
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }

        nave.dibujaGrafico(canvas);

        if (misilActivo) {
            misil.dibujaGrafico(canvas);
        }
    }

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                activaMisil();
                break;
            default:
                // Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
            break;
        }
        return procesada;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +0;
                break;
            default:
                // Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy < 6 && dx > 6) {
                    giroNave = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx < 6 && dy > 6) {
                    aceleracionNave = Math.round((mY - y) / 25);
                    disparo = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo) {
                    activaMisil();
                }
                break;
        }
        mX = x;
        mY = y;
        return true;
    }

    protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return; // Salir si el período de proceso no se ha cumplido.
        }
        // Para una ejecución en tiempo real calculamos el factor de movimiento
        double factorMov = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        // Actualizamos velocidad y dirección de la nave a partir de
        // giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * factorMov));
        double nIncX = nave.getIncX() + aceleracionNave *
                Math.cos(Math.toRadians(nave.getAngulo())) * factorMov;
        double nIncY = nave.getIncY() + aceleracionNave *
                Math.sin(Math.toRadians(nave.getAngulo())) * factorMov;
        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX,nIncY) <= MAX_VELOCIDAD_NAVE){
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        nave.incrementaPos(factorMov); // Actualizamos posición

        for (Grafico asteroide : asteroides) {
            asteroide.incrementaPos(factorMov);
        }

        for (int i = 0; i < asteroides.size(); i++) {
            if (nave.verificaColision(asteroides.get(i))) {
                salir();
            }
        }

        // Actualizamos posición de misil
        if (misilActivo) {
            misil.incrementaPos(factorMov);
            tiempoMisil-=factorMov;
            if (tiempoMisil < 0) {
                misilActivo = false;
            } else {
                for (int i = 0; i < asteroides.size(); i++) {
                    if (misil.verificaColision(asteroides.get(i))) {
                        destruyeAsteroide(i);
                        break;
                    }
                }
            }
        }
    }

    private void destruyeAsteroide(int i) {
        int tam;
        if(asteroides.get(i).getDrawable()!=drawableAsteroide[2]){
            if(asteroides.get(i).getDrawable()==drawableAsteroide[1]){
                tam=2;
            } else {
                tam=1;
            }
            for(int n=0;n<numFragmentos;n++){
                Grafico asteroide = new Grafico(this,drawableAsteroide[tam]);
                asteroide.setCenX(asteroides.get(i).getCenX());
                asteroide.setCenY(asteroides.get(i).getCenY());
                asteroide.setIncX(Math.random()*7-2-tam);
                asteroide.setIncY(Math.random()*7-2-tam);
                asteroide.setAngulo((int)(Math.random()*360));
                asteroide.setRotacion((int)(Math.random()*8-4));
                asteroides.add(asteroide);
            }
        }
        synchronized (asteroides) {
            asteroides.remove(i);
            misilActivo = false;
        }
        this.postInvalidate();
        if(sonidos) {
            soundPool.play(idExplosion, 1, 1, 0, 0, 1);
        }

        puntuacion += 1000;

        if (asteroides.isEmpty()) {
            salir();
        }
    }

    private void activaMisil() {
        misil.setCenX(nave.getCenX());
        misil.setCenY(nave.getCenY());
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) *
                PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) *
                PASO_VELOCIDAD_MISIL);
        tiempoMisil = (int) Math.min(this.getWidth() / Math.abs( misil.
                getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
        misilActivo = true;
        if (sonidos) {
            soundPool.play(idDisparo, 1, 1, 1, 0, 1);
        }
    }

    public void activaSensores(){
        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void desactivaSensores() {
        mSensorManager.unregisterListener(this);
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public boolean isSonidos() {
        return sonidos;
    }

    public void setPadre(Activity padre) {
        this.padre = padre;
    }

    private void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }

    class ThreadJuego extends Thread {
        private boolean pausa,corriendo;
        public synchronized void pausar() {
            pausa = true;
        }
        public synchronized void reanudar() {
            pausa = false;
            notify();
        }
        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }
        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    public ThreadJuego getThread() {
        return thread;
    }
}


package org.example.asteroides;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julio on 01/11/2017.
 */

public class AlmacenPuntuacionesArray implements AlmacenPuntuaciones{
    private List<String> puntuaciones;
    public AlmacenPuntuacionesArray() {
        puntuaciones = new ArrayList<String>();
        puntuaciones.add("123000 Pepito Domingez");
        puntuaciones.add("111000 Pedro Martinez");
        puntuaciones.add("011000 Paco Pérez");
    }
    @Override public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        puntuaciones.add(0, puntos + " " + nombre);
    }
    @Override public List<String> listaPuntuaciones(int cantidad) {
        return puntuaciones;
    }
}
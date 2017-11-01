package org.example.asteriodes;

import java.util.Vector;

/**
 * Created by Julio on 01/11/2017.
 */

public interface AlmacenPuntuaciones {
    public void guardarPuntuacion(int puntos,String nombre,long fecha);
    public Vector<String> listaPuntuaciones(int cantidad);
}

package org.example.asteroides;

import java.util.List;

/**
 * Created by Julio on 01/11/2017.
 */

public interface AlmacenPuntuaciones {
    public void guardarPuntuacion(int puntos,String nombre,long fecha);
    public List<String> listaPuntuaciones(int cantidad);
}

package org.example.asteroides;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JulioM on 19/12/2017.
 */

public class AlmacenPuntuacionesGson implements AlmacenPuntuaciones {
	private String string; //Almacena puntuaciones en formato JSON
	private Context context;
	private Gson gson = new Gson();
	private Type type = new TypeToken<List<Clase>>() {
	}.getType();

	public AlmacenPuntuacionesGson(Context context) {
		guardarPuntuacion(45000, "Mi nombre", System.currentTimeMillis());
		guardarPuntuacion(31000, "Otro nombre", System.currentTimeMillis());
		this.context = context;
	}

	@Override
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		//string = leerString();
		Clase objeto;
		if (string == null) {
			objeto = new Clase();
		} else {
			objeto = gson.fromJson(string, type);
		}
		objeto.puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
		string = gson.toJson(objeto, type);
		//guardarString(string)
	}

	@Override
	public List<String> listaPuntuaciones(int cantidad) {
		//string = leerString();
		Clase objeto;
		if (string == null) {
			objeto = new Clase();
		} else {
			objeto = gson.fromJson(string, type);
		}
		List<String> salida = new ArrayList<>();
		for (Puntuacion puntuacion : objeto.puntuaciones) {
			salida.add(puntuacion.getPuntos()+" "+puntuacion.getNombre());
		}
		return salida;
	}

	public class Clase {
		private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
		private boolean guardado;
	}
}

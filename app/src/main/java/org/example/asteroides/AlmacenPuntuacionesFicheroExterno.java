package org.example.asteroides;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JulioM on 13/12/2017.
 */

public class AlmacenPuntuacionesFicheroExterno implements AlmacenPuntuaciones {
	private static String FICHERO = Environment.getExternalStorageDirectory() + "/puntuaciones.txt";
	private Context context;
	public AlmacenPuntuacionesFicheroExterno(Context context) {
		this.context = context;
	}
	public void guardarPuntuacion(int puntos, String nombre, long fecha){
		try {
			String stadoSD = Environment.getExternalStorageState();
			if (stadoSD.equals(Environment.MEDIA_MOUNTED)) {
				// Podemos leer y escribir
				FileOutputStream f = new FileOutputStream(FICHERO, true);
				String texto = puntos + " " + nombre + "\n";
				f.write(texto.getBytes());
				f.close();
			} else {
				// No podemos leer y ni escribir
				Toast.makeText(this.context, "No tienes permisos de escritura o no hay memoria externa", Toast.LENGTH_SHORT).show();
			}
			
		} catch (Exception e) {
			Log.e("Asteroides", e.getMessage(), e);
		}
	}
	public List<String> listaPuntuaciones(int cantidad) {
		List<String> result = new ArrayList<String>();
		try {
			String stadoSD = Environment.getExternalStorageState();
			if (stadoSD.equals(Environment.MEDIA_MOUNTED) || (stadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
				// Podemos leer y escribir
				FileInputStream f = new FileInputStream(FICHERO);
				BufferedReader entrada = new BufferedReader(
						new InputStreamReader(f));
				int n = 0;
				String linea;
				do {
					linea = entrada.readLine();
					if (linea != null) {
						result.add(linea);
						n++;
					}
				} while (n < cantidad && linea != null);
				f.close();
			} else {
				// No podemos leer y ni escribir
				Toast.makeText(this.context, "No existe memoria externa en el dispositivo", Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			Log.e("Asteroides", e.getMessage(), e);
		}
		return result;
	}
}

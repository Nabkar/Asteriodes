package org.example.asteroides;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JulioM on 22/12/2017.
 */

public class AlmacenPuntuacionesSW_PHP implements AlmacenPuntuaciones {
	public List<String> listaPuntuaciones(int cantidad) {
		List<String> result = new ArrayList<String>();
		try {
			URL url = new URL("http://158.42.146.127/puntuaciones/lista.php?max=20");
			HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
			if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new
						InputStreamReader(conexion.getInputStream()));
				String linea = reader.readLine();
				while (!linea.equals("")) {
					result.add(linea);
					linea = reader.readLine();
				}
				reader.close();
			} else {
				Log.e("Asteroides", conexion.getResponseMessage());
			}
		} catch (Exception e) {
			Log.e("Asteroides", e.getMessage(), e);
		} finally {
			//if (conexion != null) conexion.disconnect();
			return result;
		}
	}
	public void guardarPuntuacion(int puntos, String nombre, long fecha) {
		try {
			URL url = new URL("http://158.42.146.127/puntuaciones/nueva.php?"
					+ "puntos="+ puntos
					+ "&nombre="+ URLEncoder.encode(nombre, "UTF-8")
					+ "&fecha=" + fecha);
			HttpURLConnection conexion = (HttpURLConnection) url
					.openConnection();
			if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new
						InputStreamReader(conexion.getInputStream()));
				String linea = reader.readLine();
				if (!linea.equals("OK")) {
					Log.e("Asteroides","Error en servicio Web nueva");
				}
			} else {
				Log.e("Asteroides", conexion.getResponseMessage());
			}
		} catch (Exception e) {
			Log.e("Asteroides", e.getMessage(), e);
		} finally {
			//if (conexion!=null) conexion.disconnect();
		}
	}
}
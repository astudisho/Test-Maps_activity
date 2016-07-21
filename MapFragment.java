package com.example.astudillo.test_maps_activity;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by astudillo on 10/07/16.
 */
public class MapFragment extends FragmentActivity implements OnMapReadyCallback
{
	private final LatLng LAT_LONG_GDL = new LatLng(20.6537181076049805,-103.3910357952);

	final int PERMISO_GPS = 0;
	private SupportMapFragment mapa;
	private Context contexto;
	private LatLng miPosicion;
	private GoogleMap googleMapa;

	public MapFragment(Context context)
	{
		//mapa = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
		this.contexto = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
	{
		super.onCreate(savedInstanceState, persistentState);

		LocationManager manejadorLocalizacion = (LocationManager) getSystemService(contexto.LOCATION_SERVICE);

		Toast.makeText(contexto,"Algo",Toast.LENGTH_SHORT);
		Log.d("Mapa","OnCreate");
	}

	@Override
	public void onMapReady(GoogleMap mapa)
	{
		inicializaMapa(mapa);
	}

	private void inicializaMapa(GoogleMap map)
	{
		googleMapa = map;
		miPosicion = new LatLng(
			MapActivity.localizacion.getLatitude(),
			MapActivity.localizacion.getLongitude()
			);

		try
		{

			map.moveCamera(CameraUpdateFactory.newLatLng(LAT_LONG_GDL));
			map.addMarker(new MarkerOptions()
						.position(LAT_LONG_GDL));

			map.setMyLocationEnabled(true);
		}
		catch (SecurityException se)
		{
			se.printStackTrace();
		}

		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setCompassEnabled(true);

		map.moveCamera(CameraUpdateFactory.newLatLng(miPosicion));
		map.moveCamera(CameraUpdateFactory.zoomTo(12));

		//Toast.makeText(contexto,"Mapa creado", Toast.LENGTH_LONG).show();

		Log.d("Mapa","Mapa creado");

		String obtenURL = getDirectionsUrl();
		ReadTask donwload = new ReadTask();
		donwload.execute(obtenURL);
	}

	private String getDirectionsUrl()
	{
		String waypoints = "origin=" + miPosicion.latitude + "," + miPosicion.longitude +
			"&" + "destination=" + LAT_LONG_GDL.latitude + "," + LAT_LONG_GDL.longitude ; //Astudillo calando la url

		String sensor = "sensor=false";
		String units = "units=metric";
		String mode = "mode=driving";
		String params = waypoints + "&" + units + "&" + mode + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"+ output + "?" + params;

		Log.d("Mapa: ", url);

		return url;
	}

	private class ReadTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... url)
		{
			String data = "";

			try
			{
				HttpConexion http = new HttpConexion();
				data = http.readUrl(url[0]);
			}
			catch (Exception e)
			{
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			new ParserTask().execute(result);
		}
	}

	private class ParserTask extends
		AsyncTask<String, Integer, List<List<HashMap<String, String> >> >
	{

		@Override
		protected List<List<HashMap<String, String > > > doInBackground(String... jsonData)
		{

			JSONObject jObject;
			List<List<HashMap<String, String > > > routes = null;

			try
			{
				jObject = new JSONObject(jsonData[0]);
				JSONParser parser = new JSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Log.d("Mapa", routes.toString());
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes)
		{

			ArrayList<LatLng> points = new ArrayList<>();
			PolylineOptions polyLineOptions = new PolylineOptions();

			Log.d("Mapa",routes.toString());

			for (int i = 0; i < routes.size(); i++) {
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(10);
				polyLineOptions.color(Color.BLUE);
			}

			googleMapa.addPolyline(polyLineOptions);

			((MapActivity) contexto).actualizarDistancia("Distancia: " + JSONParser.distancia);
			((MapActivity) contexto).actualizarTiempo("Tiempo: " + JSONParser.tiempo);
		}
	}
}

package com.example.astudillo.test_maps_activity;


import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser
{

	public static String distancia;
	public static String tiempo;

	public List<List<HashMap<String,String>>> parse(JSONObject jObject)
	{

		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;

		try {

			jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			for(int i=0;i<jRoutes.length();i++){
				jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
				List path = new ArrayList<HashMap<String, String>>();

				Log.d("Rutas", jLegs.toString());

				/** Traversing all legs */
				for(int j=0;j<jLegs.length();j++)
				{
					Log.d("Rutas", ( (JSONObject)jLegs.get(j)).get("distance").toString() );
					Log.d("Rutas", ( (JSONObject)jLegs.get(j)).get("duration").toString() );

					distancia = jLegs.getJSONObject(0).getJSONObject("distance").getString("text");
					tiempo = jLegs.getJSONObject(0).getJSONObject("duration").getString("text");

					Log.d("Rutas", distancia);

					jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

					/** Traversing all steps */
					for(int k=0;k<jSteps.length();k++){
						String polyline = "";
						polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);

						/** Traversing all points */
						for(int l=0;l<list.size();l++){
							HashMap<String, String> hashMap = new HashMap<String, String>();
							hashMap.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
							hashMap.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
							path.add(hashMap);
						}
					}
					routes.add(path);
				}
			}

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return routes;
	}

	private List<LatLng> decodePoly(String encoded)
	{

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
				(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}
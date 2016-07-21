package com.example.astudillo.test_maps_activity;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConexion
{
    public String readUrl(String mapsDirectionsUrl) throws IOException
    {
        String data = "";
        InputStream inStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(mapsDirectionsUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            StringBuffer sbr = new StringBuffer();
            String line = "";

            while ((line = br.readLine())!= null)
            {
                sbr.append(line);
            }

            data = sbr.toString();
            br.close();

        }
        catch (Exception e)
        {
			Log.d("Error", "Excepcion en HTTPConexion");
	        Log.d("Error", "URL: " + mapsDirectionsUrl);
	        e.printStackTrace();
        }

        return data;
    }
}
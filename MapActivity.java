package com.example.astudillo.test_maps_activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity
{
	final private int PERMISO_GPS = 0;
	private SupportMapFragment mapFragment;
	private TextView txvDistancia, txvTiempo;
	public static Context contexto = null;
	public static Location localizacion;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		contexto = MapActivity.this;

		setContentView(R.layout.activity_map);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		txvDistancia = (TextView) findViewById(R.id.txvDistancia);
		txvTiempo = (TextView) findViewById(R.id.txvTiempo);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show();
			}
		});

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
			PackageManager.PERMISSION_GRANTED
			&&
			ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED)
		{
			Log.d("Mapa","Entra a if");
			ActivityCompat.requestPermissions(
				MapActivity.this,
				new String[]
					{
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION
					},
				PERMISO_GPS
			);

		}
		else
		{
			//inicializaMapa(mapa);
			mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
			//mapFragment.getMapAsync(new com.example.astudillo.test_maps_activity.MapFragment(this));
			mapFragment.getMapAsync(new MapFragment(this));

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);

			LocationManager manejadorLocalizacion = (LocationManager) getSystemService(MapActivity.contexto.LOCATION_SERVICE);
			String provider = manejadorLocalizacion.getBestProvider(criteria,true);

			manejadorLocalizacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			localizacion = manejadorLocalizacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			//localizacion = manejadorLocalizacion.getLastKnownLocation(provider);
		}
		//mapFragment.getMapAsync(new com.example.astudillo.test_maps_activity.MapFragment());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		switch (requestCode)
		{
			case PERMISO_GPS:
				if(grantResults.length > 0 &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED)
					mapFragment.getMapAsync(new com.example.astudillo.test_maps_activity.MapFragment(this));
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	public void actualizarDistancia(String distancia)
	{
		txvDistancia.setText(distancia);
	}

	public void actualizarTiempo(String tiempo)
	{
		txvTiempo.setText(tiempo);
	}

}
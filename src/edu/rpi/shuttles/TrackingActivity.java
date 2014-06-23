package edu.rpi.shuttles;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import edu.rpi.shuttles.R;
import edu.rpi.shuttles.data.Coordinate;
import edu.rpi.shuttles.data.RPIShuttleDataProvider;
import edu.rpi.shuttles.data.Route;
import edu.rpi.shuttles.data.Stop;
import edu.rpi.shuttles.data.Vehicle;
import edu.rpi.shuttles.util.CompatibilityHelper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;

/**
 * The main shuttle tracking activity.
 * 
 */
public class TrackingActivity extends SherlockFragmentActivity {
	private final CompatibilityHelper compat_helper = new CompatibilityHelper();
	private RPIShuttleDataProvider shuttles_service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_tracking);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		
		if (findViewById(R.id.activity_tracking) != null) {
			if (savedInstanceState != null) {
				return;
			}
			
			
			if (compat_helper.hasGooglePlayServices(getApplicationContext())) {
				TrackingGoogleMapFragment mapFragment = new TrackingGoogleMapFragment();
				getSupportActionBar().setBackgroundDrawable(null);
				getSupportFragmentManager().beginTransaction()
					.add(R.id.activity_tracking, mapFragment).commit();
			} else if (compat_helper.hasAmazonMaps()) {
				Intent intent = new Intent(this, TrackingAmazonMapActivity.class);
				startActivity(intent);
			} else {
				Bundle mapFragmentArgs = new Bundle();
				mapFragmentArgs.putString("loadUrl", "http://shuttles.rpi.edu/?mobile=1&client_id=wtg");
				mapFragmentArgs.putBoolean("sendDeviceId", true);
				
				TrackingWebviewFragment mapFragment = new TrackingWebviewFragment();
				mapFragment.setArguments(mapFragmentArgs);
				
				getSupportActionBar().setBackgroundDrawable(null);
				getSupportFragmentManager().beginTransaction()
					.add(R.id.activity_tracking, mapFragment).commit();
			}
			/*
			MenuFragment leftDrawer = new MenuFragment();
			getSupportFragmentManager().beginTransaction()
			.add(R.id.left_drawer, leftDrawer).commit();
			*/
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		checkMapsCompatibility();
		if (mAmazonMapsAvailable) {
			return true;
		} else {
			MenuInflater inflater = getSupportMenuInflater();
		    inflater.inflate(R.menu.tracking_activity_actions, menu);
		    return super.onCreateOptionsMenu(menu);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		checkMapsCompatibility();
		switch (item.getItemId()) {
		case R.id.action_refresh:
    		AsyncTask<Void, Void, SparseArray<Stop>> stopsTask = shuttles_service.new PopulateStopsTask(this);
    		AsyncTask<Void, Void, SparseArray<Route>> routesTask = shuttles_service.new PopulateRoutesTask(this);

    		stopsTask.execute();
    		routesTask.execute();
    		return true;
		}
		TrackingFragment mapFragment =  (TrackingFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking);
		return mapFragment.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		if (compat_helper.hasGooglePlayServices(getApplicationContext())) 
		{
			shuttles_service = new RPIShuttleDataProvider();
			final GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
	                .findFragmentById(R.id.activity_tracking)).getMap();
			
			LatLng center = new LatLng(42.7302712352, -73.6765441399);
			
			map.setMyLocationEnabled(true);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 17));
			
			AsyncTask<Void, Void, SparseArray<Stop>> stopsTask = shuttles_service.new PopulateStopsTask(this);
			AsyncTask<Void, Void, SparseArray<Route>> routesTask = shuttles_service.new PopulateRoutesTask(this);
			stopsTask.execute();
			routesTask.execute();
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (compat_helper.hasGooglePlayServices(getApplicationContext())) {
						((TrackingGoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking))
							.moveCamera(new Coordinate(42.7302712352, -73.6765441399), (float) 15.25);
					} else {
						((TrackingFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking))
							.moveCamera(new Coordinate(42.7302712352, -73.6765441399), (float) 15.25);
					}
				}
			}, 1000);			
		}
	}
	public void updateRoutes(SparseArray<Route> routes) {
		if (compat_helper.hasGooglePlayServices(getApplicationContext())) {
			((TrackingGoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking))
				.renderRoutes(routes);
		} else {
			((TrackingFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking))
				.renderRoutes(routes);
		}
	}
	
	public void updateStops(SparseArray<Stop> stops) {
		if (compat_helper.hasGooglePlayServices(getApplicationContext())) {
			((TrackingGoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking))
				.renderStops(stops);
		} else {
			((TrackingFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking))
				.renderStops(stops);
		}
	}
	
	public void updateVehicles(SparseArray<Vehicle> vehicles) {
		if (compat_helper.hasGooglePlayServices(getApplicationContext())) {
			((TrackingGoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracking))
			.renderVehicles(vehicles);
		}
	}
}
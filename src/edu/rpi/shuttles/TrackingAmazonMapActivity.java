package edu.rpi.shuttles;

import com.amazon.geo.maps.GeoPoint;
import com.amazon.geo.maps.MapActivity;
import com.amazon.geo.maps.MapController;
import com.amazon.geo.maps.MapView;

import edu.rpi.shuttles.data.Coordinate;
import edu.rpi.shuttles.data.RPIShuttleDataProvider;
import edu.rpi.shuttles.data.Route;
import edu.rpi.shuttles.data.Stop;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TrackingAmazonMapActivity extends MapActivity {
	private MapView mMapView;
	private RPIShuttleDataProvider shuttles_service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking_amazon_map);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		shuttles_service = new RPIShuttleDataProvider();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracking_activity_actions, menu);
		
		// Extract a reference to the map view
        mMapView = (MapView) findViewById(R.id.mapview);
        
        // Turn on the zoom control widget
        mMapView.setBuiltInZoomControls(true);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		Log.d("STrakAmzMap", "Menu button pressed.");
		switch (item.getItemId()) {
    		case R.id.action_reset:
    			Log.d("STrakAmzMap", "Reset position requested.");
    			moveCamera(new Coordinate(42.7302712352, -73.6765441399), (float) 15.25);
    			return true;
    		case R.id.action_refresh:
    			mMapView.getOverlays().clear();
    			AsyncTask<Void, Void, SparseArray<Stop>> stopsTask = shuttles_service.new PopulateStopsTask(this);
    			AsyncTask<Void, Void, SparseArray<Route>> routesTask = shuttles_service.new PopulateRoutesTask(this);

    			stopsTask.execute();
    			routesTask.execute();
    			return true;
    		case R.id.action_map_toggle:
    			return true;
    		default:
    			return true;
		}
	}
	public void updateRoutes(SparseArray<Route> routes) {
		
	}
	
	public void updateStops(SparseArray<Stop> stops) {
		
	}

	/*
	 * Move the map to a given point.
	 */
	public void moveCamera(Coordinate center, float zoomLevel) {
		MapController mapController = mMapView.getController();
		mapController.animateTo(new GeoPoint((int) (center.latitude * 1E6), (int) (center.longitude * 1E6)));
		mapController.setZoom((int) zoomLevel);
	}
	// Override required by interface
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}

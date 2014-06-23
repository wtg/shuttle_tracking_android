package edu.rpi.shuttles;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.rpi.shuttles.data.Coordinate;
import edu.rpi.shuttles.data.DataProvider;
import edu.rpi.shuttles.data.Route;
import edu.rpi.shuttles.data.Stop;
import edu.rpi.shuttles.data.Vehicle;

public class TrackingGoogleMapFragment extends SupportMapFragment implements TrackingFragment {
	/**
	 * 
	 */
	private DataProvider shuttles_service;
	private ArrayList<Marker> vehicle_markers;
	private ArrayList<Polyline> route_paths;
	
	public TrackingGoogleMapFragment() {
		super.newInstance();
		vehicle_markers = new ArrayList<Marker>();
		route_paths = new ArrayList<Polyline>();
	}
	public class PopulateStopsTask extends AsyncTask<Void, Void, SparseArray<Stop>> {
		public Activity activity;
		
		public PopulateStopsTask(Activity act) {
			this.activity = act;
		}
		
		@Override
		protected SparseArray<Stop> doInBackground(Void... arg0) {
			try {
				mStops = shuttles_service.fetchStops();
			} catch (Exception e) {
				Log.w("STrak", e);
			}
			return mStops;
		}
		protected void onProgressUpdate(Integer... progress) {
			this.activity.setProgressBarIndeterminateVisibility(Boolean.TRUE);
		}
		protected void onPostExecute(SparseArray<Stop> result) {
			this.activity.setProgressBarIndeterminateVisibility(Boolean.FALSE);
			if (activity instanceof TrackingActivity) {
				((TrackingActivity) activity).updateStops(result);
			} else if (activity instanceof TrackingAmazonMapActivity) {
				((TrackingAmazonMapActivity) activity).updateStops(result);
			} else {
				// Do nothing.
			}
		}
	}
	
	public class PopulateRoutesTask extends AsyncTask<Void, Void, SparseArray<Route>> {
		public Activity activity;
		
		public PopulateRoutesTask(Activity act) {
			this.activity = act;
		}
		
		@Override
		protected SparseArray<Route> doInBackground(Void... arg0) {
			try {
				mRoutes = fetchRoutes();
			} catch (Exception e) {
				Log.w("STrak", e);
			}
			return mRoutes;
		}
		
		protected void onPostExecute(SparseArray<Route> result) {
			this.activity.setProgressBarIndeterminateVisibility(Boolean.FALSE);
			if (activity instanceof TrackingActivity) {
				((TrackingActivity) activity).updateRoutes(result);
			} else if (activity instanceof TrackingAmazonMapActivity) {
				((TrackingAmazonMapActivity) activity).updateRoutes(result);
			} else {
				// Do nothing.
			}
		}
	}
	
	public class PopulateVehiclesTask extends AsyncTask<Void, Void, SparseArray<Vehicle>> {
		public Activity activity;
		
		public PopulateVehiclesTask(Activity act) {
			this.activity = act;
		}
		
		@Override
		protected SparseArray<Vehicle> doInBackground(Void... arg0) {
			try {
				mVehicles = fetchVehicles();
			} catch (Exception e) {
				Log.w("STrak", e);
			}
			return mVehicles;
		}
		
		protected void onPostExecute(SparseArray<Vehicle> result) {
			this.activity.setProgressBarIndeterminateVisibility(Boolean.FALSE);
			if (activity instanceof TrackingActivity) {
				((TrackingActivity) activity).updateVehicles(result);
			} else {
				// Do nothing.
			}
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.action_reset:
	    		moveCamera(new Coordinate(42.7302712352, -73.6765441399), (float) 15.25);
	    		return true;
	    	case R.id.action_refresh:
	    		super.getMap().clear();
	    		AsyncTask<Void, Void, SparseArray<Stop>> stopsTask = shuttles_service.new PopulateStopsTask((TrackingActivity) getActivity());
	    		AsyncTask<Void, Void, SparseArray<Route>> routesTask = shuttles_service.new PopulateRoutesTask((TrackingActivity) getActivity());

	    		stopsTask.execute();
	    		routesTask.execute();
	    		return true;
	    	case R.id.action_map_toggle:
	    		renderOfflineMap();
	    		return true;
	    	default:
	    		return true;
	    }
	}
	/**
	 * Render routes
	 */
	@SuppressWarnings("unused")
	public void renderRoutes(SparseArray<Route> routes) {
		for (int i = 0; i < route_paths.size(); i++) {
			route_paths.get(i).remove();
		}
		route_paths.clear();
		int key = 0;
		for (int j = 0; j < routes.size(); j++) {
			key = routes.keyAt(j);
			Route route = routes.get(key);
			PolylineOptions routePolyline = new PolylineOptions();
			for (int k = 0; k < route.map_polyline.size(); k++) {
				routePolyline.add(new LatLng(route.map_polyline.get(k).latitude, route.map_polyline.get(k).longitude));
			}
			routePolyline.color(Color.parseColor(route.map_color) - 0x80000000);
			Polyline path = super.getMap().addPolyline(routePolyline);
			route_paths.add(path);
			Log.d("RPIDataProvider", String.format("Rendering route %S (%S) with %s coordinates...", Integer.toString(route.id), route.name, Integer.valueOf(route.map_polyline.size()).toString()));
		}
	}
	/**
	 * Render stops
	 */
	public void renderStops(SparseArray<Stop> stops) {
		int key = 0;
		for (int i = 0; i < stops.size(); i++) {
			key = stops.keyAt(i);
			Stop stop = stops.get(key);
			LatLng stop_location = new LatLng(stop.latitude, stop.longitude);
			super.getMap().addMarker(new MarkerOptions()
					.title(stop.name)
					.snippet(stop.extraAttributes.get("description"))
					.position(stop_location));
		}
	}
	
	public void renderVehicles(SparseArray<Vehicle> vehicles) {
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle vehicle = vehicles.get(vehicles.keyAt(i));
			super.getMap().addMarker(new MarkerOptions()
			.position(new LatLng(vehicle.latitude, vehicle.longitude))
			.title(vehicle.name)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle))
			.flat(true)
			);
		}
	}
	
	public void clearVehicles() {
		for (int i = 0; i < vehicle_markers.size(); i++) {
			vehicle_markers.get(i).remove();
		}
	}
	
	/**
	 * Render an OpenStreetMap overlay of shuttle service area.
	 * <p>
	 * Render an OpenStreetMap overlay of the service area.
	 * This is useful when the device does not have Internet connectivity.
	 * </p>
	 */
	public void renderOfflineMap() {
		GroundOverlayOptions osmMap = new GroundOverlayOptions()
		.image(BitmapDescriptorFactory.fromResource(R.drawable.osm_map))
		.positionFromBounds(new LatLngBounds(new LatLng(42.7217, -73.6886), new LatLng(42.7392, -73.6624)));
		super.getMap().addGroundOverlay(osmMap);
	}
	/**
	 * Move the map to a given point.
	 */
	public void moveCamera(Coordinate center, float zoomLevel) {
		LatLng centerLatLng = new LatLng(center.latitude, center.longitude);
		final CameraPosition angledPosition = new CameraPosition(centerLatLng, zoomLevel, 70, 45);
		super.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(angledPosition));
	}
}

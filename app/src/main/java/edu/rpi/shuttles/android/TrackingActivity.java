package edu.rpi.shuttles.android;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.rpi.shuttles.android.models.TrackingApi;
import edu.rpi.shuttles.android.models.Vehicle;
import edu.rpi.shuttles.android.models.Route;
import edu.rpi.shuttles.android.models.Route.Coord;

public class TrackingActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final String TAG = TrackingActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // Check for vehicle updates periodically and update position on map
        periodicMapUpdates();
    }

    private void updateShuttlePositions(ArrayList<Vehicle> vehicles) {
        mMap.clear();
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(v.lat, v.lng))
                    .title(v.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle))
                    .anchor((float) 0.5, (float) 0.5)
                    .rotation(v.heading - 90));
        }
    }

    private void drawRoutes() {
        String[] eastCoords = getResources().getStringArray(R.array.east_campus_route);
        String[] westCoords = getResources().getStringArray(R.array.west_campus_route);

        Route eastCampus = new Route("East Campus", eastCoords);
        ArrayList<Coord> eastRouteCoords = eastCampus.getRouteCoords();

        Route westCampus = new Route("West Campus", westCoords);
        ArrayList<Coord> westRouteCoords = westCampus.getRouteCoords();

        double lat, lng;
        ArrayList<LatLng> eastLatLng = new ArrayList<LatLng>();
        for (int i = 0; i < eastRouteCoords.size(); i++) {
            lat = eastRouteCoords.get(i).getLat();
            lng = eastRouteCoords.get(i).getLng();
            eastLatLng.add(new LatLng(lat, lng));
        }

        ArrayList<LatLng> westLatLng = new ArrayList<LatLng>();
        for (int i = 0; i < westRouteCoords.size(); i++) {
            lat = westRouteCoords.get(i).getLat();
            lng = westRouteCoords.get(i).getLng();
            westLatLng.add(new LatLng(lat, lng));
        }

        PolylineOptions eastRouteOpts = new PolylineOptions().width(3).color(Color.BLUE).geodesic(true);
        PolylineOptions westRouteOpts = new PolylineOptions().width(3).color(Color.RED).geodesic(true);

        eastRouteOpts.addAll(eastLatLng);
        westRouteOpts.addAll(westLatLng);

        mMap.addPolyline(eastRouteOpts);
        mMap.addPolyline(westRouteOpts);
    }

    private void placeStops() {
        String[] stopCoords = getResources().getStringArray(R.array.stops);

        double lat, lng;
        for (int i = 0; i < stopCoords.length; i++) {
            List<String> parseLatLng = Arrays.asList(stopCoords[i].split(","));
            lat = Double.parseDouble(parseLatLng.get(1));
            lng = Double.parseDouble(parseLatLng.get(0));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_marker)));
        }
    }

    private void periodicMapUpdates() {
        final Handler updatesHandler = new Handler();
        Timer updatesTimer = new Timer();

        TimerTask updatesTask = new TimerTask() {
            @Override
            public void run() {
                updatesHandler.post(new Runnable() {
                    public void run() {
                        new MapUpdates().execute();
                    }
                });
            }
        };

        updatesTimer.schedule(updatesTask, 0, 7000);
    }

    private class MapUpdates extends AsyncTask<Void, Void, ArrayList<Vehicle>> {

        private final String TAG = MapUpdates.class.getName();

        protected ArrayList<Vehicle> doInBackground(Void...params) {
            TrackingApi api = new TrackingApi();
            String strUpdates = api.getUpdates();

            try {
                ArrayList<Vehicle> vehicles =  new ArrayList<Vehicle>();
                JSONArray updates = new JSONArray(strUpdates);

                for (int i = 0; i < updates.length(); i++) {
                    JSONObject update = updates.getJSONObject(i).getJSONObject("vehicle");
                    Vehicle aVehicle = new Vehicle(update);
                    vehicles.add(aVehicle);
                }

                return vehicles;
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Vehicle> vehicles) {
            updateShuttlePositions(vehicles);
            drawRoutes();
            placeStops();
        }
    }
}

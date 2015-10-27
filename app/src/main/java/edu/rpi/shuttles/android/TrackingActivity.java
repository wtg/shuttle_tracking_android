package edu.rpi.shuttles.android;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.rpi.shuttles.android.models.TrackingApi;
import edu.rpi.shuttles.android.models.Vehicle;
import edu.rpi.shuttles.android.models.Route;

public class TrackingActivity extends FragmentActivity {

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
        periodicVehicleUpdates();
        // Draw routes on map
        drawRoutes();
        // Place stops on routes
        placeStops();
    }

    private void updateShuttlePositions(ArrayList<Vehicle> vehicles) {
        mMap.clear();
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            mMap.addMarker(new MarkerOptions()
                .position(new LatLng(v.lat, v.lng))
                .title(v.name));
        }
    }

    private void drawRoutes() {

    }

    private void placeStops() {

    }

    private void periodicVehicleUpdates() {
        final Handler updatesHandler = new Handler();
        Timer updatesTimer = new Timer();

        TimerTask updatesTask = new TimerTask() {
            @Override
            public void run() {
                updatesHandler.post(new Runnable() {
                    public void run() {
                        new VehicleUpdates().execute();
                    }
                });
            }
        };

        updatesTimer.schedule(updatesTask, 0, 7000);
    }

    private class VehicleUpdates extends AsyncTask<Void, Void, ArrayList<Vehicle>> {

        private final String TAG = VehicleUpdates.class.getName();

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
        }
    }
}

package edu.rpi.shuttles.android.models;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.rpi.shuttles.android.R;

public class TrackingApi {

    private final String TAG = TrackingApi.class.getName();

    private static String updatesUrl = "http://shuttles.rpi.edu/vehicles/current.js";
    private static String routesUrl = "http://shuttles.rpi.edu/routes.js";
    private static String stopsUrl = "http://shuttles.rpi.edu/stops.js";

    public String getUpdates() {
        // Get vehicle tracking updates
        try {
            String updatesData = getData(this.updatesUrl);
            return updatesData;
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
        return "";
    }

    public String getRoutes() {
        // Get tracking routes
        try {
            String routesData = getData(this.routesUrl);
            return routesData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getStops() {
        // Get route stops
        try {
            String stopsData = getData(this.stopsUrl);
            return stopsData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getData(String urlString) throws Exception {
        // Connect to our API
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String line = "";
        StringBuffer data = new StringBuffer("");

        // Read and return API response data
        InputStream instr = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(instr));

        while ((line = rd.readLine()) != null) {
            data.append(line);
        }

        connection.disconnect();
        return data.toString();
    }
}

package edu.rpi.shuttles.android.models;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.rpi.shuttles.android.R;

public class TrackingApi {

    private String updatesUrl;
    private String routesUrl;
    private String stopsUrl;

    public TrackingApi(Context context) {
        updatesUrl = context.getString(R.string.updates_url);
        routesUrl = context.getString(R.string.routes_url);
        stopsUrl = context.getString(R.string.stops_url);
    }

    public ArrayList<Vehicle> getVehicleUpdates() {
        JSONObject updatesJson = getUpdates();
        return null;
    }

    private JSONObject getUpdates() {
        // Get vehicle tracking updates
        try {
            String updatesData = getData(this.updatesUrl);
            return new JSONObject(updatesData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getRoutesUrl() {
        // Get tracking routes
        try {
            String routesData = getData(this.routesUrl);
            return new JSONObject(routesData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getStopsUrl() {
        // Get route stops
        try {
            String stopsData = getData(this.stopsUrl);
            return new JSONObject(stopsData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

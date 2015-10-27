package edu.rpi.shuttles.android.models;

import org.json.JSONObject;

public class Vehicle {

    public String name;
    public int heading;
    public double lat;
    public double lng;

    public Vehicle(JSONObject data) throws Exception {
        this.name = data.getString("name");
        JSONObject latestPos = data.getJSONObject("latest_position");
        this.heading = latestPos.getInt("heading");
        this.lat = latestPos.getDouble("latitude");
        this.lng = latestPos.getDouble("longitude");
    }
}

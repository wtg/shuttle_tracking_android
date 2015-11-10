package edu.rpi.shuttles.android.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Route {

    private String name;
    private ArrayList<Coord> routeCoords = new ArrayList<Coord>();

    public Route(String name, String[] coords) {
        this.name = name;
        for (int i = 0; i < coords.length; i++) {
            Coord aCoord = new Coord(coords[i]);
            this.routeCoords.add(aCoord);
        }
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Coord> getRouteCoords() {
        return this.routeCoords;
    }

    public class Coord {

        private double lat;
        private double lng;

        public Coord(String latLng) {
            List<String> parseLatLng = Arrays.asList(latLng.split(","));
            this.lat = Double.parseDouble(parseLatLng.get(1));
            this.lng = Double.parseDouble(parseLatLng.get(0));
        }

        public double getLat() {
            return this.lat;
        }

        public double getLng() {
            return this.lng;
        }
    }
}

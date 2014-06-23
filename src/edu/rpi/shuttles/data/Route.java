package edu.rpi.shuttles.data;

import java.util.ArrayList;

public class Route {
	public Integer id;
	public String name;
	public String description;
	public Boolean enabled;
	public String map_color;
	public ArrayList<Coordinate> map_polyline;
	public ArrayList<Integer> stop_ids;
	
	public Route() {
		map_polyline = new ArrayList<Coordinate>();
		stop_ids = new ArrayList<Integer>();
	}
}

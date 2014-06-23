package edu.rpi.shuttles.data;

import java.util.HashMap;

public class Stop {
	public Integer id;
	public String name;
	public String short_name;
	public Boolean enabled;
	public double latitude;
	public double longitude;
	public HashMap<String, String> extraAttributes;
	
	public Stop () {
		extraAttributes = new HashMap<String, String>();
	}
}

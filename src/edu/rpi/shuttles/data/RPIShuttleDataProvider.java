/*
 * Copyright 2013 Shuttle Tracking Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.rpi.shuttles.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.stream.JsonReader;

/*
 * ShuttleDataProvider for shuttle service provided by Rensselaer Polytechnic Institute.
 * Shuttle data documentation: https://github.com/wtg/shuttle-data
 * Route polylines are pulled from /displays/netlink.js
 * Route details are pulled from /routes.js
 * Stop details are pulled from /stops.js
 * Current vehicle locations are pulled from /vehicles/current.js
 * Vehicle details are pulled from /vehicles.xml
 */
public class RPIShuttleDataProvider extends DataProvider {
	private String mStopsUrl;
	private String mRoutesUrl;
	private String mRoutePathsUrl;
	private String mVehiclesUrl;
	private String mVehicleLocationsUrl;
	
	private String mUserAgent;
	
	public RPIShuttleDataProvider() {
		mUserAgent = String.format("ShuttleTracker/%s (Linux, Android %s; %s)", "0.0.1", android.os.Build.VERSION.RELEASE, android.os.Build.PRODUCT);
		mStopsUrl = "http://shuttles.rpi.edu/stops.js";
		mRoutesUrl = "http://shuttles.rpi.edu/routes.js";
		mRoutePathsUrl = "http://shuttles.rpi.edu/displays/netlink.js";
		mVehiclesUrl = "http://shuttles.rpi.edu/vehicles.xml";
		mVehicleLocationsUrl = "http://shuttles.rpi.edu/vehicles/current.js";
	}
	
	public SparseArray<Stop> fetchStops() throws IOException {
		// Download stop data.
		URL stopsURL = new URL(mStopsUrl);
		URLConnection stopsConnection = stopsURL.openConnection();
		stopsConnection.setRequestProperty("User-Agent", mUserAgent);
		InputStream in = new BufferedInputStream(stopsConnection.getInputStream());
		JsonReader reader = new JsonReader(new InputStreamReader(in));
		mStops.clear();
		mRouteStopsMap.clear();
		
		reader.beginArray();
		while (reader.hasNext()) {
			Stop stop = readStop(reader);
			mStops.put(stop.id, stop);
		}
		reader.endArray();
		
		reader.close();
		return mStops;
	}
	
	private Stop readStop(JsonReader reader) throws IOException {
		Stop stop = new Stop();
		ArrayList<Integer> routes = new ArrayList<Integer>();
		reader.beginObject();
		reader.nextName();
		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals("id")) {
				stop.id = reader.nextInt();
			} else if (key.equals("name")) {
				stop.name = reader.nextString();
			} else if (key.equals("short_name")) {
				stop.short_name = reader.nextString();
			} else if (key.equals("enabled")) {
				stop.enabled = reader.nextBoolean();
			} else if (key.equals("latitude")) {
				stop.latitude = reader.nextDouble();
			} else if (key.equals("longitude")) {
				stop.longitude = reader.nextDouble();
			} else if (key.equals("routes")) {
				reader.beginArray();
				while (reader.hasNext()) {
					reader.beginObject();
					while (reader.hasNext()) {
						if (reader.nextName().equals("id")) {
							routes.add(reader.nextInt());
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				}
				reader.endArray();
			} else {
				stop.extraAttributes.put(key, reader.nextString());
			}
			
		}
		reader.endObject();
		reader.endObject();
		Log.d("RPIDataProvider", String.format("Pulling stop %S (%S)...", Integer.toString(stop.id), stop.name));
		for (int i = 0; i < routes.size(); i++) {
			ArrayList<Integer> route = mRouteStopsMap.get(routes.get(i), new ArrayList<Integer>());
			route.add(stop.id);
			mRouteStopsMap.put(routes.get(i), route);
		}
		return stop;
	}
	
	public SparseArray<Route> fetchRoutes() throws IOException {
		URL routesURL = new URL(mRoutesUrl);
		URLConnection routesConnection = routesURL.openConnection();
		routesConnection.setRequestProperty("User-Agent", mUserAgent);
		InputStream in = new BufferedInputStream(routesConnection.getInputStream());
		
		URL routePathsURL = new URL(mRoutePathsUrl);
		URLConnection routePathsConnection = routePathsURL.openConnection();
		routePathsConnection.setRequestProperty("User-Agent", mUserAgent);
		InputStream pathInputStream = new BufferedInputStream(routePathsConnection.getInputStream());
		
		mRoutes.clear();
		JsonReader routesReader = new JsonReader(new InputStreamReader(in));
		routesReader.beginArray();
		while (routesReader.hasNext()) {
			Route route = readRoute(routesReader);
			mRoutes.put(route.id, route);
		}
		routesReader.endArray();
		
		JsonReader routePathReader = new JsonReader(new InputStreamReader(pathInputStream));
		routePathReader.beginObject();
		while (routePathReader.hasNext()) {
			Log.d("RouteDataProvider", "Entering L1.");
			String key = routePathReader.nextName();
			Log.d("RouteDataProvider", String.format("L1: %s", key));
			if (key.equals("routes")) {
				Log.d("RouteDataProvider", "Found 'routes' tag.");
				routePathReader.beginArray();
				while (routePathReader.hasNext()) {
					populateRoutePath(routePathReader);
				}
				routePathReader.endArray();
			} else {
				Log.d("RouteDataProvider", "Found other tag.");
				routePathReader.skipValue();
			}
		}		
		routePathReader.endObject();

		
		return mRoutes;
	}
	
	private Route readRoute(JsonReader reader) throws IOException {
		Route route = new Route();
		reader.beginObject();
		reader.nextName();
		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals("id")) {
				route.id = reader.nextInt();
			} else if (key.equals("name")) {
				route.name = reader.nextString();
			} else if (key.equals("description")) {
				route.description = reader.nextString();
			} else if (key.equals("enabled")) {
				route.enabled = reader.nextBoolean();
			} else if (key.equals("color")) {
				route.map_color = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		reader.endObject();
		Log.d("RouteDataProvider", String.format("Pulling route %S (%S)...", Integer.toString(route.id), route.name));
		Log.d("RouteDataProvider", String.format("Route %S (%S) has %S stops.", Integer.toString(route.id), route.name, Integer.toString(mRouteStopsMap.get(route.id).size())));
		return route;
	}
	
	private void populateRoutePath(JsonReader reader) throws IOException {
		ArrayList<Coordinate> path = new ArrayList<Coordinate>();
		Integer path_id = 0;
		reader.beginObject();
		while (reader.hasNext()) {
			// Process attributes of a route.
			// We have the array of coordinates making up the path of the route on a map.
			String key = reader.nextName();
			if (key.equals("coords")) {
				Double latitude = 0.0;
				Double longitude = 0.0;
				reader.beginArray();
				// Read coordinate attributes and add to path.
				while (reader.hasNext()) {
					reader.beginObject();
					// Read coordinate attributes.
					while (reader.hasNext()) {
						String coordinate_key = reader.nextName();
						if (coordinate_key.equals("latitude")) {
							latitude = reader.nextDouble();
						} else if (coordinate_key.equals("longitude")) {
							longitude = reader.nextDouble();
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
					// Add coordinate to path.
					path.add(new Coordinate(latitude, longitude));
					Log.d("CreatePoint", String.format("Inserting point %S, %S", latitude.toString(), longitude.toString()));
				}
				reader.endArray();
			} else if (key.equals("id")) {
				path_id = reader.nextInt();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		Route route = mRoutes.get(path_id);
		route.map_polyline = path;
		mRoutes.put(path_id, route);
		Log.d("RouteDataProvider", String.format("Pulling route %S (%S) with %s coordinates...", Integer.toString(route.id), route.name, Integer.valueOf(route.map_polyline.size()).toString()));
	}
	
	public SparseArray<Vehicle> fetchVehicles() throws IOException {
		// Download stop data.
		URL stopsURL = new URL(mVehiclesUrl);
		URLConnection stopsConnection = stopsURL.openConnection();
		stopsConnection.setRequestProperty("User-Agent", mUserAgent);
		mVehicles.clear();
		
		mVehicles = updateVehicleLocations();
		
		return mVehicles;
	}
	
	public SparseArray<Vehicle> updateVehicleLocations() throws IOException {
		// Download vehicle location data.
		URL vehicleLocationURL = new URL(mVehicleLocationsUrl);
		URLConnection vehicleLocationConnection = vehicleLocationURL.openConnection();
		vehicleLocationConnection.setRequestProperty("User-Agent", mUserAgent);
		InputStream in = new BufferedInputStream(vehicleLocationConnection.getInputStream());
		JsonReader reader = new JsonReader(new InputStreamReader(in));
		SparseArray<Vehicle> updated_vehicles = new SparseArray<Vehicle>();
		
		reader.beginArray();
		while (reader.hasNext()) {
			Vehicle shuttle = readVehicleLocation(reader);
			updated_vehicles.put(shuttle.id, shuttle);
			mVehicles.put(shuttle.id, shuttle);
		}
		reader.endArray();
		
		reader.close();
		return updated_vehicles;
	}
	
	@SuppressLint("SimpleDateFormat")
	private Vehicle readVehicleLocation(JsonReader reader) throws IOException {
		Vehicle shuttle = new Vehicle();
		reader.beginObject();
		reader.nextName();
		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals("id")) {
				shuttle.id = reader.nextInt();
			} else if (key.equals("name")) {
				shuttle.name = reader.nextString();
			} else if (key.equals("latest_position")) {
				reader.beginObject();
				while (reader.hasNext()) {
					key = reader.nextName();
					if (key.equals("heading")) {
						shuttle.heading = reader.nextInt();
					} else if (key.equals("latitude")) {
						shuttle.latitude = reader.nextDouble();
					} else if (key.equals("longitude")) {
						shuttle.longitude = reader.nextDouble();
					} else if (key.equals("speed")) {
						shuttle.speed = reader.nextInt();
					} else if (key.equals("timestamp")) {
						SimpleDateFormat iso_format = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");
						try {
							shuttle.timestamp = iso_format.parse(reader.nextString().replace("T", " "));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else if (key.equals("public_status_message")) {
						shuttle.description = reader.nextString();
					} else if (key.equals("cardinal_point")) {
						shuttle.cardinalPoint = reader.nextString();
					}
				}
				reader.endArray();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		reader.endObject();
		Log.d("RPIDataProvider", String.format("Updated Shuttle %S (%S) location...", Integer.toString(shuttle.id), shuttle.name));
		return shuttle;
	}
}

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

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

/*
 * Abstract class representing a remote data source of routes, stops, and vehicle locations.
 */
public abstract class DataProvider {
	protected static SparseArray<Route> mRoutes;
	protected static SparseArray<Stop> mStops;
	protected static SparseArray<Vehicle> mVehicles;
	protected static SparseArray<ArrayList<Integer>> mRouteStopsMap;
	
	protected static Integer mUpdateInterval;
	protected static Integer mVehicleActivityTimeout;
	
	protected static Boolean mSupportsWebview;
	protected static String mWebviewUrl;
	
	public DataProvider() {
		setUpdateInterval(4000);
		setVehicleActivityTimeout(300);
		mSupportsWebview = false;
		mWebviewUrl = "";
		mStops = new SparseArray<Stop>();
		mRoutes = new SparseArray<Route>();
		mRouteStopsMap = new SparseArray<ArrayList<Integer>>();
	}
	
	/**
	 * @return the mUpdateInterval
	 */
	public Integer getUpdateInterval() {
		return mUpdateInterval;
	}

	/**
	 * @param updateInterval time between vehicle data updates, in milliseconds
	 */
	public void setUpdateInterval(Integer updateInterval) {
		DataProvider.mUpdateInterval = updateInterval;
	}

	/**
	 * @return the mVehicleActivityTimeout
	 */
	public Integer getVehicleActivityTimeout() {
		return mVehicleActivityTimeout;
	}

	/**
	 * @param vehicleActivityTimeout time to wait before marking a vehicle offline, in seconds
	 */
	public void setVehicleActivityTimeout(Integer vehicleActivityTimeout) {
		DataProvider.mVehicleActivityTimeout = vehicleActivityTimeout;
	}
	
	/**
	 * @return Whether or not this data provider provides a web-based vehicle location application implementing the TrackingWebView API.
	 */
	public Boolean isWebviewSupported() {
		return mSupportsWebview;
	}
	
	/**
	 * @return URL of data provider's TrackingWebView API application, if supported.
	 */
	public String getWebviewUrl() {
		return mWebviewUrl;
	}

	/**
	 * @return the Routes
	 */
	public SparseArray<Route> getRoutes() {
		return mRoutes;
	}
	
	public List<Route> getRoutesList() {
		List<Route> result = new ArrayList<Route>();
		int key = 0;
		for (int i = 0; i < mRoutes.size(); i++) {
			key = mRoutes.keyAt(i);
			result.add(mRoutes.get(key));
		}
		return result;
	}

	/**
	 * @return the Stops
	 */
	public SparseArray<Stop> getStops() {
		return mStops;
	}

	/**
	 * @return the Vehicles
	 */
	public SparseArray<Vehicle> getVehicles() {
		return mVehicles;
	}

	/**
	 * @param Vehicles the Vehicles to set
	 */
	public void updateVehicles(SparseArray<Vehicle> mVehicles) {
		DataProvider.mVehicles = mVehicles;
	}
}

/*
 * Copyright 2014 Shuttle Tracking Authors
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
package edu.rpi.shuttles.util;

import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class CompatibilityHelper {
	private final String AMAZON_DEVICE_MANUFACTURER = "Amazon";

	public CompatibilityHelper() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Check for Google Play Services
	 * 
	 * @param context current application context
	 * @return boolean indicating if device supports Google Play Services
	 */
	public boolean hasGooglePlayServices(Context context) {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method checks the value of android.os.Build.MANUFACTURER
	 * to see if it's an Amazon device.  Amazon devices do not have
	 * Google Play Services.
	 * 
	 * @return boolean indicating if device is Amazon-manufactured.
	 */
	public boolean isAmazonDevice() {
		if (android.os.Build.MANUFACTURER == AMAZON_DEVICE_MANUFACTURER) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method checks for the presence of the presence of Amazon Maps API on
	 * all Kindle Fire tablets (except first-generation) and the Fire Phone.
	 * 
	 * @return boolean indicating presence of Amazon Maps API
	 */
	public boolean hasAmazonMaps() {
		/* Check for Amazon Maps API.
		 * Code adapted from: https://developer.amazon.com/public/apis/engage/maps/doc/03-migrating-an-app-from-google-maps
		 */
		try {
			Class.forName("com.amazon.geo.maps.MapView");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

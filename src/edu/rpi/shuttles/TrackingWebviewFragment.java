/*
 * Copyright (C) 2010 The Android Open Source Project
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
package edu.rpi.shuttles;

import java.util.HashMap;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class TrackingWebviewFragment extends SherlockFragment {
	private WebView mWebView;
	private boolean mIsWebViewAvailable;
	private boolean mSendDeviceId;
	private String mTargetUrl;
	
	public TrackingWebviewFragment() {
		// Initialize Amazon WebView, if available.
    }
	
    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @SuppressLint("NewApi")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = new WebView(getActivity());
        Context mContext = this.getActivity();
        
        Bundle args = getArguments();
        mSendDeviceId = args.getBoolean("sendDeviceId", false);
        if (android.os.Build.VERSION.SDK_INT >= 12) {
        	mTargetUrl = args.getString("loadUrl", "http://shuttles.rpi.edu/?mobile=1&client_id=wtg");
        } else {
        	mTargetUrl = "http://shuttles.rpi.edu/?mobile=1&client_id=wtg";
        }
        
        
		// Initialize WebView.
		WebSettings mWebSettings = mWebView.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		// Enable HTML5 AppCache for future use.
		mWebSettings.setDomStorageEnabled(true);
		mWebSettings.setAppCachePath(mContext.getCacheDir() + "/app_cache");
		mWebSettings.setAllowFileAccess(true);
		mWebSettings.setAppCacheEnabled(true);
		
		// Identify Android device and version in User-Agent
		String userAgentString = String.format("ShuttleTracker/%s (Linux, Android %s; %s)", "0.0.1", android.os.Build.VERSION.RELEASE, android.os.Build.PRODUCT);
		mWebSettings.setUserAgentString(userAgentString);
		
		// Send device info in HTTP request headers.
		Map<String, String> deviceInfoHeaders = new HashMap<String, String>();
		deviceInfoHeaders.put("X-Device-Manufacturer", android.os.Build.MANUFACTURER);
		deviceInfoHeaders.put("X-Device-Model", android.os.Build.MODEL);
		deviceInfoHeaders.put("X-Device-Build", android.os.Build.FINGERPRINT);
		if (mSendDeviceId) {
			String deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
			deviceInfoHeaders.put("X-Device-Id", deviceId);
		}
		
		// Enable zooming
		//mWebSettings.setBuiltInZoomControls(true);
		//mWebSettings.setSupportZoom(true);
		mWebView.loadUrl(mTargetUrl, deviceInfoHeaders);
        mIsWebViewAvailable = true;
        return mWebView;
    }
    
    /**
     * Called to reset the map zoom level and position.
     */
    public void resetZoom() {
    	if (mIsWebViewAvailable) {
    		mWebView.loadUrl("javascript:resetZoom();");
    	}
    }
    /**
     * Called to refresh the current page.
     */
    public void reload() {
    	mWebView.reload();
    }
    /**
     * Called to zoom in the map.
     */
    public void zoomIn() {
    	if (mIsWebViewAvailable) {
    		mWebView.loadUrl("javascript:zoomIn()");
    	}
    }
    /**
     * Called to zoom out the map.
     */
    public void zoomOut() {
    	if (mIsWebViewAvailable) {
    		mWebView.loadUrl("javascript:zoomOut();");
    	}
    }
    /**
     * Called to force a refresh of vehicle locations.
     */
    public void refresh() {
    	if (mIsWebViewAvailable) {
    		mWebView.loadUrl("javascript:update();");
    	}
    }
    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }
}

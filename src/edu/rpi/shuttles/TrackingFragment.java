package edu.rpi.shuttles;

import android.util.SparseArray;

import com.actionbarsherlock.view.MenuItem;

import edu.rpi.shuttles.data.Coordinate;
import edu.rpi.shuttles.data.Route;
import edu.rpi.shuttles.data.Stop;

public interface TrackingFragment {
	/**
	 * Render stops on a map.
	 * @param stops
	 */
	public void renderStops(SparseArray<Stop> stops);
	/**
	 * Render routes on a map.
	 * @param routes
	 */
	public void renderRoutes(SparseArray<Route> routes);
	/**
	 * Renders a map without making network requests.
	 */
	public void renderOfflineMap();
	/**
	 * Move the map to a given point.
	 */
	public void moveCamera(Coordinate center, float zoomLevel);
	/**
	 * Menu items selection handler.
	 * <p>
	 * This function is called when a option item is selected.
	 * See TrackingGoogleMapFragment.java for an example implementation.
	 * </p>
	 * @param item
	 * @return
	 */
	public boolean onOptionsItemSelected(MenuItem item);
}

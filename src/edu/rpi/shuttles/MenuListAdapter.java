package edu.rpi.shuttles;

import edu.rpi.shuttles.data.RPIShuttleDataProvider;
import edu.rpi.shuttles.data.Vehicle;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {
	private RPIShuttleDataProvider shuttles_service;
	private LayoutInflater inflater;
	private Context context;
	private String title;
	
	public MenuListAdapter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup parent) {
		TextView itemTitle;
		TextView itemDescription;
		TextView itemLastUpdated;
		
		Vehicle item = shuttles_service.getVehicles().valueAt(arg0);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.fragment_item_list_shuttle, parent, false);
		
		itemTitle = (TextView) itemView.findViewById(R.id.title);
		itemDescription = (TextView) itemView.findViewById(R.id.description);
		itemLastUpdated = (TextView) itemView.findViewById(R.id.last_updated);
		
		itemTitle.setText(item.name);
		itemDescription.setText("Route");
		
		Integer delta = (int) ((System.currentTimeMillis() - item.timestamp.getTime()) / 1000);
		String last_updated = new String();
		if (delta > 7200) {
			last_updated = String.format("%i hours", (delta / 3600));
		} else if (delta > 60) {
			last_updated = String.format("%i minutes", (delta / 60));
		} else {
			last_updated = String.format("%i seconds", delta);
		}
		itemLastUpdated.setText(String.format(parent.getResources().getString(R.string.shuttle_status), last_updated));
		
		return null;
	}

}

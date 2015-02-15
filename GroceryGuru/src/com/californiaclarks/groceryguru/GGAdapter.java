package com.californiaclarks.groceryguru;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GGAdapter extends BaseAdapter {

	//member variables
	private ArrayList<String> data;
	private Context context;
	//Map<String, Integer> icons = new HashMap<String, Integer>();

	//Adapter to hable custom gglist layout
	public GGAdapter(Context context, ArrayList<String> data) {
		super();
		this.data = data;
		this.context = context;

		// icons.put("eggs", R.drawable.eggs);
	}

	public void add(String string) {
		data.add(string);
	}

	public void clear() {
		data.clear();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public String getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//create the one-line view of each item with name and basket icon
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = LayoutInflater.from(context).inflate(R.layout.itemline,
				parent, false);

		TextView text = (TextView) rowView.findViewById(R.id.text);
		ImageView icon = (ImageView) rowView.findViewById(R.id.icon);

		text.setText(data.get(position));
		int iIcon = R.drawable.ic_launcher;
		//if (icons.containsKey(data.get(position))) {
		//	iIcon = icons.get(data.get(position));
		//}
		icon.setImageResource(iIcon);

		return rowView;
	}

}
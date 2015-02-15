package com.californiaclarks.groceryguru;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.californiaclarks.groceryguru.library.DatabaseHandler;
import com.californiaclarks.groceryguru.library.UserFunctions;

public class Frige2 extends ListFragment {

	//constructor
	public Frige2() {
	}

	//called when items are updates
	public void setItems(String[][] items) {
		this.items = items;

		//also updates adapter
		if (adapter != null) {
			adapter.clear();
			for (String item : items[DatabaseHandler.LOC_ITEM]) {
				adapter.add(item);
			}
			adapter.notifyDataSetChanged();
		}

	}

	//member variables
	String[][] items;
	UserFunctions userFunctions;
	GGAdapter adapter = null;
	Context context;

	//Toast item age on click
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Date now = new Date(System.currentTimeMillis());
		Date created_at = null;
		try {
			created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
					.parse(items[DatabaseHandler.LOC_CREATED_AT][position]);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Toast.makeText(
				getActivity(),
				"You have had "
						+ items[DatabaseHandler.LOC_ITEM][position]
						+ " for "
						+ String.valueOf((created_at.getTime() - now.getTime())
								/ -86400000) + " days", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		//set layout to custom list
		View vFrag = inflater.inflate(R.layout.gglist, container, false);

		//use and set custom list adapter
		adapter = new GGAdapter(inflater.getContext(),
				new ArrayList<String>());
		setListAdapter(adapter);

		//add items to adapter and refresh adapter
		for (String item : items[DatabaseHandler.LOC_ITEM]) {
			adapter.add(item);
		}
		adapter.notifyDataSetChanged();

		return vFrag;
	}

}
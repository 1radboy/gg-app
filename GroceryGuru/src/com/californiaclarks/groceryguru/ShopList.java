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

public class ShopList extends ListFragment {

	private static final int MILIS_PER_DAY = 86400000;

	// called when items are updated
	public void setItems(String[][] items) {
		this.items = items;
		// update adapter (UI) too
		if (adapter != null) {
			adapter.clear();
			for (int i = 0; i < items[DatabaseHandler.LOC_ITEM].length; i++) {
				// add only if running low
				Date now = new Date(System.currentTimeMillis());
				Date created_at = null;
				try {
					created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
							Locale.US)
							.parse(items[DatabaseHandler.LOC_CREATED_AT][i]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				int age = (int) ((now.getTime() - created_at.getTime()) / MILIS_PER_DAY);
				if (Integer.parseInt(items[DatabaseHandler.LOC_AVGLEN][i])
						- age < 3) {
					adapter.add(items[DatabaseHandler.LOC_ITEM][i]);
				}
			}
			adapter.notifyDataSetChanged();
		}

	}

	// member variables
	String[][] items;
	UserFunctions userFunctions;
	GGAdapter adapter = null;
	Context context;

	// Toast item age on click
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String key = adapter.getItem(position);
		for (int i = 0; i < items[DatabaseHandler.LOC_ITEM].length; i++) {
			if (items[DatabaseHandler.LOC_ITEM][i].equals(key)) {
				position = i;
				break;
			}
		}
		Date now = new Date(System.currentTimeMillis());
		Date created_at = null;
		try {
			created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
					.parse(items[DatabaseHandler.LOC_CREATED_AT][position]);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		int age = (int) ((now.getTime() - created_at.getTime()) / MILIS_PER_DAY);

		Toast.makeText(
				getActivity(),
				items[DatabaseHandler.LOC_ITEM][position]
						+ " expires in "
						+ String.valueOf(Integer
								.parseInt(items[DatabaseHandler.LOC_AVGLEN][position])
								- age) + " days", Toast.LENGTH_SHORT).show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		adapter = new GGAdapter(inflater.getContext(), new ArrayList<String>());
		setListAdapter(adapter);

		// only show items running low and refresh adapter
		for (int i = 0; i < items[DatabaseHandler.LOC_ITEM].length; i++) {
			Date now = new Date(System.currentTimeMillis());
			Date created_at = now;
			try {
				created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.US)
						.parse(items[DatabaseHandler.LOC_CREATED_AT][i]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int age = (int) ((now.getTime() - created_at.getTime()) / MILIS_PER_DAY);
			if (Integer.parseInt(items[DatabaseHandler.LOC_AVGLEN][i]) - age < 3) {
				adapter.add(items[DatabaseHandler.LOC_ITEM][i]);
			}
		}
		adapter.notifyDataSetChanged();

		return inflater.inflate(R.layout.gglist, container, false);
	}

}

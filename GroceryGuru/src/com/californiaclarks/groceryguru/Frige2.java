package com.californiaclarks.groceryguru;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.californiaclarks.groceryguru.library.DatabaseHandler;
import com.californiaclarks.groceryguru.library.UserFunctions;

public class Frige2 extends ListFragment {

	private static final int MILIS_PER_DAY = 86400000;
	private String currentItem;

	// constructor
	public Frige2() {
	}

	// called when items are updates
	public void setItems(String[][] items) {
		this.items = items;

		// also updates adapter
		if (adapter != null) {
			adapter.clear();
			for (String item : items[DatabaseHandler.LOC_ITEM]) {
				adapter.add(item);
			}
			adapter.notifyDataSetChanged();
		}

	}

	// member variables
	String[][] items;
	UserFunctions userFunctions = new UserFunctions();
	GGAdapter adapter = null;
	Context context;

	Button delete;

	// Toast item age on click
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

		int age = (int) ((now.getTime() - created_at.getTime()) / MILIS_PER_DAY);

		Toast.makeText(
				getActivity(),
				items[DatabaseHandler.LOC_ITEM][position]
						+ " expires in "
						+ String.valueOf(Integer
								.parseInt(items[DatabaseHandler.LOC_AVGLEN][position])
								- age) + " days", Toast.LENGTH_SHORT).show();
		delete.setText("Remove " + items[DatabaseHandler.LOC_ITEM][position]
				+ " from fridge");
		delete.setTextColor(Color.BLACK);
		delete.setClickable(true);
		currentItem = items[DatabaseHandler.LOC_ITEM][position];
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// set layout to custom list
		// use and set custom list adapter
		adapter = new GGAdapter(inflater.getContext(), new ArrayList<String>());
		setListAdapter(adapter);

		// add items to adapter and refresh adapter
		boolean empty = true;
		for (String item : items[DatabaseHandler.LOC_ITEM]) {
			adapter.add(item);
			empty = false;
		}
		adapter.notifyDataSetChanged();
		final View vFrag = inflater.inflate(R.layout.gglistfridge, container,
				false);
		delete = (Button) vFrag.findViewById(R.id.delete);
		delete.setClickable(false);
		delete.setTextColor(Color.GRAY);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = userFunctions.getUserData(getActivity()
						.getApplicationContext())[DatabaseHandler.LOC_EMAIL][0];
				userFunctions.delFromFrige(currentItem, email);
				// refresh local DBs
				((GroceryGuru) getActivity()).refresh();
				delete.setClickable(false);
				delete.setTextColor(Color.GRAY);
				delete.setText("Remove");
			}
		});
		if (empty) {
			vFrag.findViewById(R.id.appleFridge).setVisibility(View.VISIBLE);
			vFrag.findViewById(R.id.emptyTextFridge)
					.setVisibility(View.VISIBLE);
			vFrag.findViewById(R.id.delete).setVisibility(View.GONE);
			vFrag.findViewById(R.id.listLayoutFridge).setVisibility(View.GONE);
		}
		return vFrag;
	}

}

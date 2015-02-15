package com.californiaclarks.groceryguru;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.californiaclarks.groceryguru.library.DatabaseHandler;
import com.californiaclarks.groceryguru.library.UserFunctions;

public class Frige2 extends ListFragment {

	private static final int MILIS_PER_DAY = 86400000;
	private String clickedItem;

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

	Button delete, changeExp;

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
				+ " from Fridge");
		delete.setClickable(true);
		clickedItem = items[DatabaseHandler.LOC_ITEM][position];
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
		for (String item : items[DatabaseHandler.LOC_ITEM]) {
			adapter.add(item);
		}
		View vFrag = inflater.inflate(R.layout.gglistfridge, container, false);
		adapter.notifyDataSetChanged();

		delete = (Button) vFrag.findViewById(R.id.delete);
		delete.setClickable(false);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = userFunctions.getUserData(getActivity()
						.getApplicationContext())[DatabaseHandler.LOC_EMAIL][0];
				userFunctions.delFromFrige(clickedItem, email);
				// refresh local DBs
				((GroceryGuru) getActivity()).refresh();
				delete.setClickable(false);
				delete.setTextColor(Color.GRAY);
				delete.setText("Remove");
			}
		});
		changeExp = (Button) vFrag.findViewById(R.id.changeExp);
		changeExp.setClickable(false);
		changeExp.setTextColor(Color.GRAY);
		changeExp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertExp = new AlertDialog.Builder(
						getActivity());
				alertExp.setTitle("Change Expiration Date");
				alertExp.setMessage("Enter the number of days from now the item will expire.");
				final EditText inputExp = new EditText(getActivity());
				inputExp.setInputType(InputType.TYPE_CLASS_NUMBER);
				inputExp.setHint("Leave blank for default");
				alertExp.setView(inputExp);
				alertExp.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								userFunctions = new UserFunctions();
								if (inputExp.getText().toString().equals("")) {
									userFunctions
											.addToFrige(
													clickedItem,
													userFunctions
															.getUserData(getActivity()
																	.getApplicationContext())[DatabaseHandler.LOC_EMAIL][0]);
								} else {

									int expDate = Integer.parseInt(inputExp
											.getText().toString());
									// add item to online GroceryGuru account

									userFunctions.addToFrigeExpire(
											clickedItem,
											expDate,
											userFunctions
													.getUserData(getActivity()
															.getApplicationContext())[DatabaseHandler.LOC_EMAIL][0]);
								}// refresh local DBs
								((GroceryGuru) getActivity()).refresh();
							}
						});
				alertExp.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						});
				alertExp.show();
			}
		});

		return vFrag;
	}

}

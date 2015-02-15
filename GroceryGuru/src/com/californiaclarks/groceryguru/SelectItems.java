package com.californiaclarks.groceryguru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.californiaclarks.groceryguru.library.DatabaseHandler;
import com.californiaclarks.groceryguru.library.UserFunctions;

public class SelectItems extends Activity implements OnClickListener {
	
	//member variables
	Button bSubmit;
	ListView list;
	ArrayAdapter<String> adapter;
	UserFunctions userFunctions = new UserFunctions();

	//create selectable view
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectitems);

		list = (ListView) findViewById(R.id.list);
		bSubmit = (Button) findViewById(R.id.bSubmit);

		Bundle b = getIntent().getExtras();
		String[] basket = b.getStringArray("basket");
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, basket);
		//can select tiems
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setAdapter(adapter);

		bSubmit.setOnClickListener(this);
	}

	//add checked items to frige
	public void onClick(View v) {
		SparseBooleanArray checked = list.getCheckedItemPositions();
		//add item to frige GroceryGuru account online
		for (int i = 0; i < checked.size(); i++) {
			int pos = checked.keyAt(i);
			userFunctions
					.addToFrige(
							adapter.getItem(pos),
							userFunctions.getUserData(getApplicationContext())[DatabaseHandler.LOC_EMAIL][0]);
		}
		Toast.makeText(getApplicationContext(), "Added items to your frige!", Toast.LENGTH_SHORT).show();
		//will cause refresh on local frige and DBs
		setResult(RESULT_OK, new Intent());
		finish();
	}
}
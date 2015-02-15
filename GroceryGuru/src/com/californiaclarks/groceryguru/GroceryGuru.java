package com.californiaclarks.groceryguru;

import java.util.Locale;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.californiaclarks.groceryguru.library.DatabaseHandler;
import com.californiaclarks.groceryguru.library.UserFunctions;

public class GroceryGuru extends FragmentActivity {

	//member variables
	GGPagerAdapter paMain;
	ViewPager vpMain;
	UserFunctions userFunctions;

	//Fragments
	Frige2 f = new Frige2();
	ShopList s = new ShopList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userFunctions = new UserFunctions();

		if (userFunctions.isLoggedIn(getApplicationContext())) {
			setContentView(R.layout.groceryguru);
			// Create the adapter that will return a fragment .
			paMain = new GGPagerAdapter(getSupportFragmentManager());
			// Set up the ViewPager with the sections adapter.
			vpMain = (ViewPager) findViewById(R.id.vpMain);
			vpMain.setAdapter(paMain);
		} else {
			Intent login = new Intent(getApplicationContext(), Login.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuitem) {

		switch (menuitem.getItemId()) {
		case R.id.logout:
			//logout user and close main activity
			userFunctions = new UserFunctions();
			userFunctions.logoutUser(getApplicationContext());
			Intent login = new Intent(getApplicationContext(), Login.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			finish();
			break;
		case R.id.refresh:
			//refresh frige
			refresh();
			break;
		}

		return false;
	}

	public void refresh() {
		
		//refresh data in database from online
		userFunctions = new UserFunctions();
		JSONObject json = userFunctions
				.refreshFrige(userFunctions
						.getUserData(getApplicationContext())[DatabaseHandler.LOC_EMAIL][0]);

		//pull new data from the DB
		try {
			DatabaseHandler db = new DatabaseHandler(
					getApplicationContext());

			db.reset(DatabaseHandler.TABLE_FRIGE);
			JSONObject frige = json.getJSONObject("items");
			int j = 0;
			while (j < frige.length()) {
				String item = frige.names().getString(j);
				db.addItem(item, frige.getJSONArray(item).getString(0),
						frige.getJSONArray(item).getString(1));
				j++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//refresh items in frige fragment and shoping list fragment
		f.setItems(userFunctions.getFrige(getApplicationContext()));
		s.setItems(userFunctions.getFrige(getApplicationContext()));
	}
	
	//PageAdapter
	public class GGPagerAdapter extends FragmentPagerAdapter {

		//constructor
		public GGPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		//return each fragment
		@Override
		public Fragment getItem(int pos) {
			if (pos == 0)
				return new Scanner();
			else if (pos == 1) {
				f.setItems(userFunctions.getFrige(getApplicationContext()));
				return f;
			} else if (pos == 2) {
				s.setItems(userFunctions.getFrige(getApplicationContext()));
				return s;
			}
			return null;
		}

		//return fragment count
		@Override
		public int getCount() {
			return 3;
		}

		//return fragment titles
		@Override
		public CharSequence getPageTitle(int pos) {
			Locale l = Locale.getDefault();
			switch (pos) {
			case 0:
				return getString(R.string.camera_title).toUpperCase(l);
			case 1:
				return getString(R.string.frige_title).toUpperCase(l);
			case 2:
				return getString(R.string.shoplist_title).toUpperCase(l);
			}
			return null;
		}
	}

}

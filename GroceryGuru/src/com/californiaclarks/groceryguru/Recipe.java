package com.californiaclarks.groceryguru;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Recipe extends Fragment {

	TextView tvName, tvContent;
	String name;
	String content;

	// called when items are updates
	public void setRecipe(JSONObject json) {
		try {
			if (json.getInt("success") == 1) {
				name = json.getJSONObject("recipe").getString("name");
				content = json.getJSONObject("recipe").getString("content");
			}
			else {
				name = "";
				content = json.getString("error_msg");
			}
			tvName.setText(name.toUpperCase(Locale.US));
			tvContent.setText(content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View vFrag = inflater.inflate(R.layout.recipe, container, false);
		tvName = (TextView) vFrag.findViewById(R.id.tvName);
		tvContent = (TextView) vFrag.findViewById(R.id.tvContent);
		((GroceryGuru) getActivity()).refreshRecipe();

		return vFrag;
	}

}

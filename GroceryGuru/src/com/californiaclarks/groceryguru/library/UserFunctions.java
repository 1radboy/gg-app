package com.californiaclarks.groceryguru.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {

	//member variables
	JSONParser jsonParser;
	String url = "http://californiaclarks.com/sites/gg/";
	String email;

	//constructor
	public UserFunctions(Context c) {
		jsonParser = new JSONParser();
		this.email = getUserData(c)[DatabaseHandler.LOC_EMAIL][0];
	}
	public UserFunctions() {
		jsonParser = new JSONParser();
	}
	
	public void setContext(Context c) {
		this.email = getUserData(c)[DatabaseHandler.LOC_EMAIL][0];
	}

	//login and return server response
	public JSONObject loginUser(String email, String password) {
		this.email = email;
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "login"));
		tags.add(new BasicNameValuePair("email", email));
		tags.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}

	//register and return server response
	public JSONObject registerUser(String name, String email, String password) {
		this.email = email;
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "register"));
		tags.add(new BasicNameValuePair("name", name));
		tags.add(new BasicNameValuePair("email", email));
		tags.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}

	//read DB to see if user is logged in
	public boolean isLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount(DatabaseHandler.TABLE_LOGIN);
		if (count > 0) {
			return true;
		}
		return false;
	}

	//logout user (delete themfrom DB)
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.reset(DatabaseHandler.TABLE_LOGIN);
		db.reset(DatabaseHandler.TABLE_FRIGE);
		return true;
	}
	
	public String[][] getUserData(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		return db.getUserData();
	}

	// (above: login table)
	// =====================================================//
	// (below: items in frige)

	//return list of items in frige
	public String[][] getFrige(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		return db.getItems();
	}

	//get frige from online and return server response
	public JSONObject refreshFrige() {
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "refreshFrige"));
		tags.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}
	
	//add a single item to the frige online and return server response
	public JSONObject addToFrige(String item) {
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "addToFrige"));
		tags.add(new BasicNameValuePair("item", item));
		tags.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}
	
	//manually add a single item to the frige online and return server response
	public JSONObject addToFrigeExpire(String item, int userProvidedExpire) {
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "addToFrige"));
		tags.add(new BasicNameValuePair("item", item));
		tags.add(new BasicNameValuePair("email", email));
		tags.add(new BasicNameValuePair("expire", String.valueOf(userProvidedExpire)));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}
	
	//delete a single item from the frige online and return server response
	public JSONObject delFromFrige(String item) {
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "delFromFrige"));
		tags.add(new BasicNameValuePair("item", item));
		tags.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}
	
	// send text to server and return what server finds in text
	public JSONObject parseForItems(String text) {
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "parseForItems"));
		tags.add(new BasicNameValuePair("text", text));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}
	
	// request a recipie from the server
	public JSONObject requestRecipe() {
		List<NameValuePair> tags = new ArrayList<NameValuePair>();
		tags.add(new BasicNameValuePair("tag", "requestRecipe"));
		tags.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(url, tags);
		return json;
	}

}
package com.californiaclarks.groceryguru;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.californiaclarks.groceryguru.library.DatabaseHandler;
import com.californiaclarks.groceryguru.library.UserFunctions;

public class Login extends Activity {
	
	//member varibles
	Button btnLogin, btnLoginToRegister;
	EditText etEmail, etPassword;
	TextView tvError;

	
	//keys
	private static String KEY_SUCCESS = "success";
	//private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	static final String KEY_ITEM = "item";
	static final String KEY_AVGLEN = "avglen";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		etEmail = (EditText) findViewById(R.id.etLoginEmail);
		etPassword = (EditText) findViewById(R.id.etLoginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLoginToRegister = (Button) findViewById(R.id.btnLoginToRegister);
		tvError = (TextView) findViewById(R.id.tvLoginError);

		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				
				//get login info
				String email = etEmail.getText().toString();
				String password = etPassword.getText().toString();
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.loginUser(email, password);

				//read server response
				try {
					//login user
					if (!json.isNull(KEY_SUCCESS)) {
						tvError.setText("");
						String tag = json.getString(KEY_SUCCESS);
						if (Integer.parseInt(tag) == 1) {
							DatabaseHandler db = new DatabaseHandler(
									getApplicationContext());
							JSONObject user = json.getJSONObject("user");
							JSONObject frige = json.getJSONObject("items");

							userFunction.logoutUser(getApplicationContext());
							db.addUser(user.getString(KEY_NAME),
									user.getString(KEY_EMAIL),
									user.getString(KEY_CREATED_AT));

							//update frige
							int j = 0;
							while (j < frige.length()) {
								String item = frige.names().getString(j);
								db.addItem(item, frige.getJSONArray(item)
										.getString(0), frige.getJSONArray(item)
										.getString(1));
								j++;
							}

							//start main activity
							Intent i = new Intent(getApplicationContext(),
									GroceryGuru.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							finish();
						} else {
							tvError.setText(json.getString(KEY_ERROR_MSG));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		//link to register page
		btnLoginToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), Register.class);
				startActivity(i);
				finish();
			}
		});
	}
}
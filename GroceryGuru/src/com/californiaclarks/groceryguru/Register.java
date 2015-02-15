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

public class Register extends Activity {

	// member variables
	Button btnRegister, btnRegisterToLogin;
	EditText etName, etEmail, etPassword;
	TextView tvError;

	// keys
	private static String KEY_SUCCESS = "success";
	// private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		etName = (EditText) findViewById(R.id.etRegisterName);
		etEmail = (EditText) findViewById(R.id.etRegisterEmail);
		etPassword = (EditText) findViewById(R.id.etRegisterPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegisterToLogin = (Button) findViewById(R.id.btnRegisterToLogin);
		tvError = (TextView) findViewById(R.id.tvRegisterError);

		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// get registration info
				String name = etName.getText().toString();
				String email = etEmail.getText().toString();
				String password = etPassword.getText().toString();
				UserFunctions userFunction = new UserFunctions();
				JSONObject json = userFunction.registerUser(name, email,
						password);

				// read server response
				try {
					// register user using UserFunctions
					String success = null;
					try {
						success = json.getString(KEY_SUCCESS);
					} catch (Exception e) {
					}
					if (success != null) {
						if (Integer.parseInt(json.getString(KEY_SUCCESS)) == 1) {
							tvError.setText("");
							DatabaseHandler db = new DatabaseHandler(
									getApplicationContext());
							JSONObject user = json.getJSONObject("user");

							// update frige
							userFunction.logoutUser(getApplicationContext());
							db.addUser(user.getString(KEY_NAME),
									user.getString(KEY_EMAIL),
									user.getString(KEY_CREATED_AT));

							// start main activity
							Intent i = new Intent(getApplicationContext(),
									GroceryGuru.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							finish();
						} else {
							// show the error
							tvError.setText(json.getString(KEY_ERROR_MSG));
						}
					} else {
						// show the error
						tvError.setText("ERROR. Are you connected to the internet?");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		// link to login page
		btnRegisterToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), Login.class);
				startActivity(i);
				finish();
			}
		});
	}
}
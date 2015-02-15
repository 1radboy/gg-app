package com.californiaclarks.groceryguru.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	//Keys used by other classes
	static final int DATABASE_VERSION = 1;
	static final String DATABASE_NAME = "gg";
	static final String TABLE_LOGIN = "login";
	public static final String TABLE_FRIGE = "frige";
	static final String KEY_ID = "id";
	static final String KEY_NAME = "name";
	static final String KEY_EMAIL = "email";
	static final String KEY_CREATED_AT = "created_at";
	static final String KEY_ITEM = "item";
	static final String KEY_AVGLEN = "avglen";
	public static final int LOC_NAME = 0;
	public static final int LOC_EMAIL = 1;
	public static final int LOC_ITEM = 0;
	public static final int LOC_AVGLEN = 1;
	public static final int LOC_CREATED_AT = 2;

	//constructor
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//Query to create DBs
	public void onCreate(SQLiteDatabase db) {
		String create_login_query = "CREATE TABLE " + TABLE_LOGIN + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " TEXT" + ")";
		String create_frige_query = "CREATE TABLE " + TABLE_FRIGE + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ITEM + " TEXT,"
				+ KEY_AVGLEN + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";
		db.execSQL(create_login_query);
		db.execSQL(create_frige_query);
	}

	//Query to delete and then create DBs
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIGE);
		onCreate(db);
	}

	//Add user (on login) to DB
	public void addUser(String name, String email, String created_at) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_EMAIL, email);
		values.put(KEY_CREATED_AT, created_at);

		db.insert(TABLE_LOGIN, null, values);
		db.close();
	}

	//Return loggedin user data
	public String[][] getUserData() {
		String items_query = "SELECT  * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(items_query, null);
		String[][] items = new String[3][c.getCount()];
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			items[LOC_NAME][c.getPosition()] = c.getString(LOC_NAME+1); //+1 b/c Primary Key in row 0
			items[LOC_EMAIL][c.getPosition()] = c.getString(LOC_EMAIL+1);
			items[LOC_CREATED_AT][c.getPosition()] = c.getString(LOC_CREATED_AT+1);
		}
		c.close();
		db.close();
		return items;
	}

	//Add item to frige DB
	public void addItem(String item, String avglen, String created_at) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ITEM, item);
		values.put(KEY_AVGLEN, avglen);
		values.put(KEY_CREATED_AT, created_at);

		db.insert(TABLE_FRIGE, null, values);
		db.close();
	}

	//Get list of items in frige DB
	public String[][] getItems() {
		String items_query = "SELECT  * FROM " + TABLE_FRIGE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(items_query, null);
		String[][] items = new String[3][c.getCount()];
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			items[LOC_ITEM][c.getPosition()] = c.getString(LOC_ITEM+1); //+1 b/c Primary Key in row 0
			items[LOC_AVGLEN][c.getPosition()] = c.getString(LOC_AVGLEN+1);
			items[LOC_CREATED_AT][c.getPosition()] = c.getString(LOC_CREATED_AT+1);
		}
		c.close();
		db.close();
		return items;
	}

	//Get row count of specified table
	public int getRowCount(String table) {
		String count_query = "SELECT  * FROM " + table;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(count_query, null);
		int rows = cursor.getCount();
		db.close();
		cursor.close();
		return rows;
	}

	//Reset DBs
	public void reset(String table) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(table, null, null);
		db.close();
	}

}
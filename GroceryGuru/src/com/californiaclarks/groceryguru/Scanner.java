package com.californiaclarks.groceryguru;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Type;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.californiaclarks.groceryguru.library.DatabaseHandler;
import com.californiaclarks.groceryguru.library.UserFunctions;
import com.googlecode.tesseract.android.TessBaseAPI;

public class Scanner extends Fragment implements OnClickListener {

	// keys
	public static final String BASE_DIR = Environment
			.getExternalStorageDirectory().toString() + "/gg_ocr/";
	public static final String lang = "eng";
	public static final int REQUEST_SELECT = 200;
	public static final int REQUEST_CAMERA = 100;

	String value;

	// member variables
	UserFunctions userFunctions = new UserFunctions();
	Button bTakePic, bAddByHand;
	ProgressDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// make tessdata folder on sdcard
		File dir = new File(BASE_DIR + "tessdata/");
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e("SCANNER", "Could not create directory on sdcard: ");
			}
		}
		// copy tessdata to sdcard from assets
		if (!(new File(BASE_DIR + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {
				AssetManager assetManager = getActivity().getAssets();
				InputStream in = assetManager.open("tessdata/" + lang
						+ ".traineddata");
				OutputStream out = new FileOutputStream(BASE_DIR + "tessdata/"
						+ lang + ".traineddata");
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (IOException e) {
				Log.e("SCANNER", "Could not copy tessdata to sdcard: " + lang
						+ ".traineddata " + e.toString());
			}
		}

		View vFrag = inflater.inflate(R.layout.scanner, container, false);
		bTakePic = (Button) vFrag.findViewById(R.id.bTakePic);
		bTakePic.setOnClickListener(this);
		bAddByHand = (Button) vFrag.findViewById(R.id.bAddByHand);
		bAddByHand.setOnClickListener(this);

		return vFrag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bTakePic:
			// start camera activity
			File file = new File(BASE_DIR + "recipt.png");
			Uri outputFileUri = Uri.fromFile(file);

			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

			startActivityForResult(intent, REQUEST_CAMERA);
			break;
		case R.id.bAddByHand:
			// show popup for new item

			final AlertDialog.Builder alertExp = new AlertDialog.Builder(
					getActivity());
			alertExp.setTitle("Add An Item Manually");
			alertExp.setMessage("Enter the number of days from now the item will expire.");
			final EditText inputExp = new EditText(getActivity());
			inputExp.setInputType(InputType.TYPE_CLASS_NUMBER);
			inputExp.setHint("Leave blank for default");
			alertExp.setView(inputExp);
			alertExp.setPositiveButton("Add",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							userFunctions = new UserFunctions();
							if (inputExp.getText().toString().equals("")) {
								userFunctions.addToFrige(
										value,
										userFunctions.getUserData(getActivity()
												.getApplicationContext())[DatabaseHandler.LOC_EMAIL][0]);
							} else {

								int expDate = Integer.parseInt(inputExp
										.getText().toString());
								// add item to online GroceryGuru account

								userFunctions
										.addToFrigeExpire(
												value,
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

			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Add An Item Manually");
			alert.setMessage("Enter the name of the item to add manually below.");
			final EditText input = new EditText(getActivity());
			alert.setView(input);
			alert.setPositiveButton("Next",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							value = input.getText().toString();
							alertExp.show();
						}
					});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					});
			alert.show();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// check that the user did not just exit
		if (resultCode != -1)
			return;

		switch (requestCode) {
		case REQUEST_SELECT:
			// refresh after selection of new items
			((GroceryGuru) getActivity()).refresh();
			break;
		case REQUEST_CAMERA:
			// show scanning progress bar and start tesseract
			dialog = ProgressDialog.show(getActivity(), "Please Wait...",
					"We are scanning your recipt...", true);
			new ScanRecipt().execute(BASE_DIR + "recipt.png");
			break;
		}

	}

	// AsyncTask for tesseract
	private class ScanRecipt extends AsyncTask<String, Integer, String[]> {

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(String[] result) {
			// close loading popup
			dialog.dismiss();
			// show selection screen if items present
			if (!result[0].equals("none")) {
				Intent i = new Intent(getActivity().getApplicationContext(),
						SelectItems.class);
				Bundle b = new Bundle();
				b.putStringArray("basket", result);
				i.putExtras(b);
				startActivityForResult(i, REQUEST_SELECT);
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"No items found, please try again!", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		protected String[] doInBackground(String... arg0) {
			// roatae image according to image metadata
			Bitmap bitmap = BitmapFactory.decodeFile(BASE_DIR + "recipt.png");
			try {
				ExifInterface exif = new ExifInterface(BASE_DIR + "recipt.png");
				int orient = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				int rotate = 0;
				switch (orient) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				}
				if (rotate != 0) {
					int w = bitmap.getWidth();
					int h = bitmap.getHeight();
					Matrix mtx = new Matrix();
					mtx.preRotate(rotate);
					bitmap = Bitmap
							.createBitmap(bitmap, 0, 0, w, h, mtx, false);
				}
				bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			} catch (IOException e) {
				Log.e("SCANNER", "Could not correct rotation: " + e.toString());
			}

			// start and run tesseract
			TessBaseAPI baseApi = new TessBaseAPI();
			baseApi.setDebug(true);
			baseApi.init(BASE_DIR, lang);
			baseApi.setImage(bitmap);
			baseApi.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
			String text = baseApi.getUTF8Text();
			baseApi.end();

			Log.v("SCANNER", "Raw text: " + text);

			// prepare text for server scan
			if (lang.equalsIgnoreCase("eng"))
				text = text.replaceAll("[^a-zA-Z0-9]+", " ");
			text = text.trim();
			Locale l = Locale.getDefault();
			text = text.toLowerCase(l);

			// send text to server to parse
			JSONObject json = userFunctions.parseForItems(text);
			JSONArray jBasket = null;
			try {
				jBasket = json.getJSONArray("items");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// make String[] from JSONArray
			String[] sBasket = new String[jBasket.length()];
			for (int i = 0; i < jBasket.length(); i++) {
				try {
					sBasket[i] = jBasket.getString(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return sBasket;
		}
	}

}

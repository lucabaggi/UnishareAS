package it.android.unishare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.widget.ImageView;

public class Utilities {
	
	static JSONObject parseJSON(String text) {
		try {
			return new JSONObject(text);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static JSONArray getJSONFromURL(String url) {
		
		try {
			
			String json = IOUtils.toString(new URL(url/*+"?key=aa23b7da8ce9706361e010f335953c924f682444"*/));
			MyApplication.log(url + " - " + json);
			return new JSONArray(json);
	        
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static ArrayList<Entity> queryDatabase(Context context, String url) {
		System.out.println(url);
		if(!checkNetworkState(context)) {
			return null;
		}
		ArrayList<Entity> result = null;
		result = Entity.jsonArrayToEntityList(getJSONFromURL(url));
		return result;
	}

	
	public static boolean checkNetworkState(Context context) {
	    ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo infos[] = conMgr.getAllNetworkInfo();
	    for (NetworkInfo info : infos) {
	        if (info.getState() == State.CONNECTED)
	            return true;
	    }
	    return false;
	}

	public static File saveImage(Bitmap image, String path, Context context) {
		ContextWrapper cw = new ContextWrapper(context);
		// path to /data/data/yourapp/app_data/Images
		File directory = cw.getDir("Images", Context.MODE_PRIVATE);
		// Create imageDir
		File mypath = new File(directory, path);

		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(mypath);

			// Use the compress method on the BitMap object to write image to the OutputStream
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mypath;
	}

	public static void loadImage(ImageView image, String path, Context context) {
		try {
			ContextWrapper cw = new ContextWrapper(context);
			File directory = cw.getDir("Images", Context.MODE_PRIVATE);
			File f=new File(directory.getPath(), path);
			Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
			image.setImageBitmap(b);
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
	}
	
}

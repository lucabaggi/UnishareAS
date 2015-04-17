package it.android.unishare;

import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

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
	
	
}

package it.android.unishare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Entity implements Parcelable{
	
	public Entity() {
		elements = new HashMap<String,String>();
	}
	
	private Map<String,String> elements;
	
	public void addElement(String key, String value) {
		elements.put(key, value);
	}
	
	public String get(String name) {
		return elements.get(name);
	}

	public int getInt(String name) {
		return Integer.parseInt(elements.get(name));
	}
	
	public String getFirst() {
		return elements.get(elements.keySet().toArray()[0]);
	}
	
	static Entity jsonObjectToEntity(JSONObject jsonObject) {
		Entity result = new Entity();
		try {
			JSONArray names = jsonObject.names();
			JSONArray values = jsonObject.toJSONArray(names);
			for(int i = 0 ; i < values.length(); i++){
				result.addElement(names.getString(i), jsonObject.getString(names.getString(i)));
			}
		}
	    catch(Exception e) {
	    	return null;
	    }
		return result;
	}
	
	static ArrayList<Entity> jsonArrayToEntityList(JSONArray jsonArray) {
		if (jsonArray == null) return null;
		
		ArrayList<Entity> result = new ArrayList<Entity>();
		for(int i=0; i<jsonArray.length(); i++) {
			try {
				result.add(jsonObjectToEntity((JSONObject) jsonArray.get(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
		
	}
	
	static ArrayList<String> entityListToStringList(ArrayList<Entity> list) {
		ArrayList<String> result = new ArrayList<String>();
		for(Entity element : list) {
			result.add(element.getFirst());
		}
		return result;
	}
	
	@Override
	public String toString() {
		String tmp = "[";
		int i = 0;
		for(String attr : elements.values()) {
			if(i!=0) tmp += ",";
			tmp += attr;
			i++;
		}
		tmp += "]";
		return tmp;
		
	}
	
	public static String entityArraylistToString(ArrayList<Entity> list) {
		String tmp = "";
		for(Entity element : list) {
			tmp += element.toString();
		}
		return tmp;
	}

	public static ArrayList<String> entityListToStringList(ArrayList<Entity> list, String key) {
		ArrayList<String> array = new ArrayList<String>();
		for(Entity e : list) {
			array.add(e.get(key));
		}
		return array;
	}
	
	public ArrayList<String> values() {
		return new ArrayList<String>(elements.values());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeMap(elements);
		
	}

	public Entity(Parcel source){
		this.elements = source.readHashMap(ClassLoader.getSystemClassLoader());
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Entity createFromParcel(Parcel in) {
			return new Entity(in);
		}

		public Entity[] newArray(int size) {
			return new Entity[size];
		}
	};
}

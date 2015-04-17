package it.android.unishare;


import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class MyApplication extends android.app.Application {
	
	private static MyApplication instance = null;
	
	private int userID;
	private int campusID;
	private int universityID;
	private int facultyID;
	private int specializationID;
	private String nickname;
	
	//JSON
	private JSONObject jsonDatabase;
	
	//UI variables
	private Toast actualToast;
	
	//Counters
	private int notificationCount = 0;
	
	//Local database
	private SQLiteDatabase localDatabase;
	
	//Actual context
	private Activity currentActivity;
	private Context currentContext;
	
	protected MyApplication() {
		// Exists only to defeat instantiation.
	}
	
	public void initializeDatabase() {
		if(localDatabase == null) {
			userID = 1;
			DatabaseHelper dbHelper = new DatabaseHelper(currentActivity);
			localDatabase = dbHelper.getWritableDatabase();
		}
	}
	
	//Performs query on database
	public Cursor queryDatabase(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return localDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	//Inserts new row
	public long insertIntoDatabase (String table, ContentValues values) {
		return localDatabase.insert(table,null,values);
	}
	
	public static MyApplication getInstance(Activity activity) {
	   if(instance == null) {
		   instance = new MyApplication();
	   }
	   instance.setActivity(activity);
	   instance.initializeDatabase();
	   return instance;
	}
	
	public static MyApplication getInstance(Context context) {
	   if(instance == null) {
		   instance = new MyApplication();
	   }
	   instance.setContext(context);
	   return instance;
	}
	
	
	//Loads initial data
	public boolean loadInitialData(Context context) {
		String json = loadJSONDatabase(context);
		if(json==null) return false;
		jsonDatabase = Utilities.parseJSON(json);
		try {
			nickname = jsonDatabase.getString("nickname");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//Loads Json Database
	private String loadJSONDatabase(Context context) {
	    String json = null;
	    try {
	        InputStream is = context.getResources().openRawResource(R.raw.database);

	        int size = is.available();

	        byte[] buffer = new byte[size];

	        is.read(buffer);

	        is.close();

	        json = new String(buffer, "UTF-8");


	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	    return json;

	}
	
	//Starts a new activity after ms milliseconds
	public void newDelayedActivity(int ms, final Class<?> newActivity){
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(currentContext, newActivity);
                currentContext.startActivity(mainIntent);
                ((Activity) currentContext).finish();
            }
        }, ms);
	}
	
	//Starts a new activity
	public void newActivity(Class<?> newActivity) {
		Intent intent = new Intent(currentContext, newActivity);
		currentContext.startActivity(intent);		
	}
	
	/*
	//Insert the first fragment of an activity
	public void firstFragment(Fragment firstFragment){
		FragmentTransaction transaction = currentActivity.getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, firstFragment);
        transaction.commit();
	}
	
	//Inserts a new fragment
	public void newFragment(Fragment newFragment) {
    	FragmentManager manager = currentActivity.getFragmentManager();
    	FragmentTransaction transaction = manager.beginTransaction();
    	transaction.replace(R.id.container, newFragment);
    	transaction.addToBackStack(null);
    	transaction.commit();
	}
	*/
	
	//Create request to database
	public void databaseCall(String url, String tag, ProgressDialog dialog) {
		new AsynchRequest().setParameters(currentActivity,url,tag,dialog).execute();
	}
	
	//Creates alert box with a message
	public void alertMessage(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
		builder.setMessage(message);
	    builder.setTitle(title);
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	            // User clicked OK button
	        }
	    });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	public static void alertMessage(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
	    builder.setTitle(title);
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	            // User clicked OK button
	        }
	    });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	//Creates dialog box with a question
	public void alertDecision(String title, String message, DialogInterface.OnClickListener actionTrue, DialogInterface.OnClickListener actionFalse) {
		AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
		builder.setMessage(message);
	    builder.setTitle(title);
	    builder.setPositiveButton("Si", actionTrue);
	    builder.setNegativeButton("No", actionFalse);
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	/*
	public static void alertDecision(Context context, String title, String message, DialogInterface.OnClickListener actionTrue, DialogInterface.OnClickListener actionFalse) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
	    builder.setTitle(title);
	    builder.setPositiveButton("Si", actionTrue);
	    builder.setNegativeButton("No", actionFalse);
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	*/
	
	public void toastMessage(Context context, String message) {
		if(actualToast != null) actualToast.cancel();
		actualToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		actualToast.show();
	}
	
	public static void log(String message) {
		System.out.println(message);
	}
	
	public void sendNotification(Context context, String title, String message, Class<?> destinationActivity) {
		//Sets notification ID
    	int mNotificationId = notificationCount++;
    	
    	//Builds notification
    	NotificationCompat.Builder mBuilder =
    		    new NotificationCompat.Builder(context)
    		    .setSmallIcon(R.drawable.ic_launcher)
    		    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
    		    .setContentTitle(title)
    		    .setContentText(message);
    	
    	//Sets target activity
    	Intent resultIntent = new Intent(context, MainActivity.class);
    	PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    context,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_UPDATE_CURRENT
		);
    	mBuilder.setContentIntent(resultPendingIntent);
    	
    	//Gets notifications manager and sends notification
    	NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	mNotifyMgr.notify(mNotificationId, mBuilder.build());
    	
    	//Notifica con testo visibile - da implementare
    	//nm.notifyWithText(myApp.NOTIFICATION_GUID,"Prova",NotificationManager);
	}

	///////////////////////////////////////////////////////////////////////
	//Getters & Setters
	///////////////////////////////////////////////////////////////////////
	
	public int getUserID() {
		return userID;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public int getUniversityID() {
		return universityID;
	}
	
	public int getCampusID() {
		return campusID;
	}
	
	public int getFacultyID() {
		return facultyID;
	}
	
	public int getSpecializationID() {
		return specializationID;
	}
	
	public void setActivity(Activity activity) {
		currentActivity = activity;
		currentContext = activity;
	}
	
	public Activity getActivity() {
    	return currentActivity;
    }
	
	public void setContext(Context context) {
		currentContext = context;
	}
	
	public Context getContext() {
    	return currentContext;
    }
	
}

package it.android.unishare;


import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class MyApplication extends android.app.Application {

	private static MyApplication instance = null;

	private static final int ACTUAL_COURSES = 0;
    private static final int PASSED_COURSES = 1;
	private static final int SELL_BOOKS = 2;
	private static final int REQ_BOOKS = 3;
    private static final int BOOKS = 4;
    private static final int COURSES = 5;
	private static final int FILES = 6;
	private static final int UNIVERSI = 7;

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
			DatabaseHelper dbHelper = new DatabaseHelper(currentActivity);
			localDatabase = dbHelper.getWritableDatabase();
		}
	}

	//Performs query on database
	public Cursor queryDatabase(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return localDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	public void customQuery(String query) {
		localDatabase.execSQL(query);
	}

    public int deleteFromTable(String table, String where, String[] whereArgs){
        return localDatabase.delete(table, where, whereArgs);
    }

	public long numOfRows(String table){
		return DatabaseUtils.queryNumEntries(localDatabase, table);
	}

	//Inserts new row
	public long insertIntoDatabase (String table, ContentValues values) {
		return localDatabase.insert(table, null, values);
	}

	public long insertIntoDatabaseCatchingExceptions(String table, ContentValues values){
		return localDatabase.insertOrThrow(table, null, values);
	}

    public void deleteTable(String tableName) {
        localDatabase.delete(tableName, null, null);
    }

    public void regenerateDb() {
        if(localDatabase != null)
            DatabaseHelper.regenerateDatabase(localDatabase);
    }

	/*
	public int getUserId(){
		String[] projection = {DatabaseContract.UserInfoTable.COLUMN_USER_ID};
		Cursor cursor = queryDatabase(DatabaseContract.UserInfoTable.TABLE_NAME, projection,null,
				null,null,null,null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}
	*/

	public boolean fetchUserData() {
		String[] projection = {DatabaseContract.UserInfoTable.COLUMN_USER_ID, DatabaseContract.UserInfoTable.COLUMN_UNIVERSITY_ID, DatabaseContract.UserInfoTable.COLUMN_CAMPUS_ID, DatabaseContract.UserInfoTable.COLUMN_SPECIALIZATION_ID};
		Cursor cursor = queryDatabase(DatabaseContract.UserInfoTable.TABLE_NAME, projection,null,
				null,null,null,null);
		if(cursor.getCount()==0) return false;
		cursor.moveToFirst();
		if(cursor.getInt(0) > 0) {
			userID = cursor.getInt(0);
			universityID = cursor.getInt(1);
			campusID = cursor.getInt(2);
			specializationID = cursor.getInt(3);
			return true;
		}
		return false;
	}

	public boolean hasUserCompletedWelcome() {
		return getCampusId()>0 && getSpecializationId()>0;
	}

	public boolean existsInCurrentCourses(int id){
		String selection = DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?";
        String[] selectionArgs = {((Integer)id).toString()};
        Cursor cursor = queryDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME, null, selection,
                selectionArgs,null, null, null);
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
	}

    public boolean existsInPastCourses(int id){
        String selection = DatabaseContract.PassedExams.COLUMN_COURSE_ID + " = ?";
        String[] selectionArgs = {((Integer)id).toString()};
        Cursor cursor = queryDatabase(DatabaseContract.PassedExams.TABLE_NAME, null, selection,
                selectionArgs,null, null, null);
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
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

	/*
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
	*/

	//Starts a new activity after ms milliseconds
	public void newDelayedActivity(int ms, final Class<?> newActivity){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				final Intent mainIntent = new Intent(currentContext, newActivity);
				mainIntent.putExtra("showActivity", 0);
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

    //Starts a new activity passing a parameter
    public void newActivityWithParameter(Class<?> newActivity, String paramName, Parcelable param){
        Intent intent = new Intent(currentContext, newActivity);
        intent.putExtra(paramName, param);
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
	public void databaseCall(String url, String tag, com.gc.materialdesign.widgets.ProgressDialog dialog) {
		new AsynchRequest().setParameters(currentActivity,url,tag,dialog).execute();
	}


	public void alertMessage(String title, String message) {
		android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(currentContext);
		builder.setMessage(message);
	    builder.setTitle(title);
	    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
			}
		});
	    android.support.v7.app.AlertDialog dialog = builder.create();
	    dialog.show();
	}

	//Creates dialog box with a question
	public void alertDecision(String title, String message, EditText input, CheckBox checkBox, DialogInterface.OnClickListener actionTrue, DialogInterface.OnClickListener actionFalse) {
		android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(currentContext);
		builder.setMessage(message);
	    builder.setTitle(title);
		if(input != null) {
            LinearLayout ll = new LinearLayout(this.getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(input);
			ll.addView(checkBox);
            builder.setView(ll);
		}
	    builder.setPositiveButton("Ok", actionTrue);
	    builder.setNegativeButton("Cancel", actionFalse);
        android.support.v7.app.AlertDialog dialog = builder.create();
	    dialog.show();
	}

	public void toastMessage(Context context, String message) {
		if(actualToast != null) actualToast.cancel();
		actualToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		actualToast.show();
	}

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

	public void logoutUser() {
		deleteTable(DatabaseContract.UserInfoTable.TABLE_NAME);
		deleteTable(DatabaseContract.MyCoursesTable.TABLE_NAME);
		deleteTable(DatabaseContract.PassedExams.TABLE_NAME);
		FacebookSdk.sdkInitialize(currentContext);
		LoginManager.getInstance().logOut();
		newActivity(SplashActivity.class);
	}

    public void launchNewActivityFromDrawer(Activity activity, int position){
        switch (position){
			case(ACTUAL_COURSES):
				MyApplication.getInstance(activity).newActivity(MyCoursesActivity.class);
				break;
            case(PASSED_COURSES):
                MyApplication.getInstance(activity).newActivity(PassedCoursesActivity.class);
                break;
			case(SELL_BOOKS):
				if(Utilities.checkNetworkState(getActivity()))
					MyApplication.getInstance(activity).newActivity(MyBooksActivity.class);
				else{
					String title = "Error";
					String message = "Verifica la tua connessione a Internet e riprova";
					alertMessage(title, message);
				}
                break;
			case(REQ_BOOKS):
				if(Utilities.checkNetworkState(getActivity()))
					MyApplication.getInstance(activity).newActivity(RequestedBooksActivity.class);
				else{
					String title = "Error";
					String message = "Verifica la tua connessione a Internet e riprova";
					alertMessage(title, message);
				}
                break;
            case(BOOKS):
                MyApplication.getInstance(activity).newActivity(BooksActivity.class);
                break;
            case(COURSES):
                MyApplication.getInstance(activity).newActivity(CoursesActivity.class);
                break;
			case(FILES):
				MyApplication.getInstance(activity).newActivity(FilesActivity.class);
				break;
			case(UNIVERSI):
				if(Utilities.checkNetworkState(getActivity()))
					MyApplication.getInstance(activity).newActivity(UniversiActivity.class);
				else{
					String title = "Error";
					String message = "Verifica la tua connessione a Internet e riprova";
					alertMessage(title, message);
				}
				break;
            default:
                Log.i("MyApplication", "Errore");
                break;
        }


    }


	///////////////////////////////////////////////////////////////////////
	//Getters & Setters
	///////////////////////////////////////////////////////////////////////
	
	public int getUserId() {
		return userID;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public int getUniversityId() {
		return universityID;
	}
	
	public int getCampusId() {
		return campusID;
	}
	
	public int getFacultyId() {
		return facultyID;
	}
	
	public int getSpecializationId() {
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

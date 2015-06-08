package it.android.unishare;

import java.util.ArrayList;

import it.android.unishare.R;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.Profile;


public class MainActivity extends SmartActivity {
	
	private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String facebookId;

    private static final String USER_INFO = "unishareUserInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setName();
        super.setImage();
        facebookId = super.getFacebookId();

        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Unishare");
            drawerToggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
            drawerLayout.setDrawerListener(drawerToggle);
        }
        application = MyApplication.getInstance(this);
        //addUserToDb(facebookId);

        getFragmentManager().beginTransaction().add(R.id.container, new MainFragment(), MainFragment.TAG).commit();
        //Starts background service
        //Intent service = new Intent(this.getApplicationContext(), BackgroundService.class);
        //this.getApplicationContext().startService(service);
        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	application.setActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
            drawerLayout.closeDrawers();
            return;
        }
    	String title = "Exit";
    	String message ="Sei sicuro di voler uscire?";
        DialogInterface.OnClickListener actionTrue = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				
			}
		};
		DialogInterface.OnClickListener actionFalse = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		};
		
		application.alertDecision(title, message, null, actionTrue, actionFalse);
    }

    /*
    @Override
    public void handleResult(ArrayList<Entity> result, String tag){
        Log.i("MainActivity","handling results");
        if(tag == USER_INFO){
            Entity userEntity = result.get(0);
            UserInfo user = new UserInfo(userEntity);
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.UserInfoTable.COLUMN_USER_ID, user.getUserId());
            values.put(DatabaseContract.UserInfoTable.COLUMN_NICKNAME, user.getNickname());
            values.put(DatabaseContract.UserInfoTable.COLUMN_UNIVERSITY_ID, user.getUniversityId());
            values.put(DatabaseContract.UserInfoTable.COLUMN_UNIVERSITY, user.getUniversity());
            values.put(DatabaseContract.UserInfoTable.COLUMN_CAMPUS_ID, user.getCampusId());
            values.put(DatabaseContract.UserInfoTable.COLUMN_CAMPUS, user.getCampus());
            values.put(DatabaseContract.UserInfoTable.COLUMN_SPECIALIZATION_ID, user.getSpecializationId());
            values.put(DatabaseContract.UserInfoTable.COLUMN_SPECIALIZATION, user.getSpecialization());
            values.put(DatabaseContract.UserInfoTable.COLUMN_LAST_ACCESS, user.getLastAccess());

            Log.i("MainActivity", "values ha grandezza" + values.size());
            application.insertIntoDatabase(DatabaseContract.UserInfoTable.TABLE_NAME, values);
            String[] projection = {DatabaseContract.UserInfoTable.COLUMN_NICKNAME, DatabaseContract.UserInfoTable.COLUMN_USER_ID};
            Cursor cursor = MyApplication.getInstance(this).queryDatabase(DatabaseContract.UserInfoTable.TABLE_NAME,
                    projection, null, null, null, null, null);
            cursor.moveToFirst();
            String name = cursor.getString(0);
            int userId = cursor.getInt(1);
            Log.i("MainActivity", "=======================================================");
            Log.i("MainActivity", name + ", " + userId);
            Log.i("MainActivity", "=======================================================");
        }
    		
    }
    */

    @Override
    public void launchNewActivity(int position){
        application.launchNewActivityFromDrawer(this, position);
        drawerLayout.closeDrawers();
    }

    public MyApplication getMyApplication(){
        return this.application;
    }


    public void logout(){
        Intent intent = new Intent(this, FacebookActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    /*
    private void addUserToDb(String id){
        Log.i("MainActivity", "adding user with fb id = " + id);
        application.regenerateDb();
        getUser(id);
    }

    private void getUser(String id){
        application.databaseCall("log_user.php?id=" + id, "unishareUserInfo", null);
    }
    */
    

}

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

    private static final String MAIN_FRAGMENT_INSTANCE = "main_fragment_key";

    private MainFragment mainFragment;
	
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

        if(savedInstanceState != null){
            Log.i("MainActivity", "Existing fragment");
            mainFragment = (MainFragment)getFragmentManager()
                    .getFragment(savedInstanceState, MAIN_FRAGMENT_INSTANCE);
        }
        else{
            Log.i("MainActivity", "Creating new fragment");
            mainFragment = new MainFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mainFragment, MainFragment.TAG).commit();
        }

        //Starts background service
        //Intent service = new Intent(this.getApplicationContext(), BackgroundService.class);
        //this.getApplicationContext().startService(service);
        
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, MAIN_FRAGMENT_INSTANCE, mainFragment);
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
    	String message ="Premi OK per uscire";
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
		
		application.alertDecision(title, message, null, null, actionTrue, actionFalse);
    }


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

}

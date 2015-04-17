package it.android.unishare;

import java.util.ArrayList;

import it.android.unishare.R;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends SmartActivity {
	
	private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        application = MyApplication.getInstance(this);
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
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed(){
    	String title = "Exit";
    	String message ="Sei sicuro di voler uscire?";
    	DialogInterface.OnClickListener actionTrue = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.finish();
				
			}
		};
		DialogInterface.OnClickListener actionFalse = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		};
		
		application.alertDecision(title, message, actionTrue, actionFalse);
    }
    
    @Override
    public void handleResult(ArrayList<Entity> result, String tag){
    	if(tag == "userName"){
    		//MainFragment mainFragment = (MainFragment) fragment;
    		//mainFragment.displayResults(result,tag);
    	}
    		
    }
    
    

}

package it.android.unishare;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class SmartActivity extends ActionBarActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null)
        	setSupportActionBar(toolbar);
        
        //if(!Utilities.checkNetworkState(this)) Application.toastMessage(this, "Nessuna connessione internet");
	}
	
	void handleResult(ArrayList<Entity> result, String tag) {
		
	}

}

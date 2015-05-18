package it.android.unishare;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.facebook.Profile;

public class SmartActivity extends ActionBarActivity {

    public static Profile profile;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //if(!Utilities.checkNetworkState(this)) Application.toastMessage(this, "Nessuna connessione internet");
	}
	
	void handleResult(ArrayList<Entity> result, String tag) {
		
	}

    public void launchNewActivity(int position){

    }

    protected void setName(){
        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText(profile.getFirstName().toString());
    }

}

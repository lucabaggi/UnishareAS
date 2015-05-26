package it.android.unishare;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

public class SmartActivity extends ActionBarActivity {

    public static Profile profile;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //if(!Utilities.checkNetworkState(this)) Application.toastMessage(this, "Nessuna connessione internet");
	}
	
	void handleResult(ArrayList<Entity> result, String tag) {
		
	}

    public void handleError(String tag){

    }

    public void launchNewActivity(int position){

    }

    protected void setName(){
        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText(profile.getFirstName().toString());
    }

    protected void setImage(){
        CircularImageView image = (CircularImageView) findViewById(R.id.circular_image);
        String imageUlr = profile.getProfilePictureUri(500,500).toString();
        Log.i("SmartActivity", imageUlr);
        new DownloadImageTask(image).execute(imageUlr);
    }

}

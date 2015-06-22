package it.android.unishare;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;

public class SplashActivity extends Activity {
	
	private MyApplication application;

	private static final String SENDER_ID = "752443788766";
	private static final int TIME_SHOW_MILLIS = 3000;

    private GoogleCloudMessaging gcm;
    private Context context;
    private String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		application = MyApplication.getInstance(this);
		getFragmentManager().beginTransaction().add(R.id.splash_fragment_container, new SplashFragment()).commit();
        application.initializeDatabase();

        context = this;
        gcm = GoogleCloudMessaging.getInstance(this);
        registerInBackground();

        if(application.numOfRows(DatabaseContract.UserInfoTable.TABLE_NAME) > 0)
            if(Utilities.checkNetworkState(this)){
                int userId = application.getUserId();
                new SyncUserCoursesTask(application).execute(userId);
            }
            else {
                application.toastMessage(this,
                        "Nessuna connessione di rete. Sincronizzazione corsi fallita");
                launchFacebookActivity();
            }
        else
            launchFacebookActivity();
	}

    protected void launchFacebookActivity(){
        application.newDelayedActivity(TIME_SHOW_MILLIS, FacebookActivity.class);
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
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

    public MyApplication getMyApplication(){
        return this.application;
    }

    private void registerInBackground(){
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);

                }
                catch (IOException ex)
                {
                    return null;
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String regid)
            {
                if (regid!=null) {
                    Log.i("SplashActivity", "Registration_id = " + regid);
                }
                else
                    Log.i("SplashActivity", "Errore: registrazione su GCM non riuscita!");
            }
        }.execute();
    }
}

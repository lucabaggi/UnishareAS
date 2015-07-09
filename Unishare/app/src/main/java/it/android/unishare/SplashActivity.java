package it.android.unishare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class SplashActivity extends Activity {
	
	private MyApplication application;
    private SplashFragment splashFragment;

	private static final int TIME_SHOW_MILLIS = 2000;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		application = MyApplication.getInstance(this);
        splashFragment = new SplashFragment();
		getFragmentManager().beginTransaction().
                add(R.id.splash_fragment_container, splashFragment).commit();
	}

    public void initialize()
    {
        application.initializeDatabase();

        boolean status = application.fetchUserData();
        Class nextClass;

        if(status) {
            if(Utilities.checkNetworkState(this)){
                new SyncUserCoursesTask(application).execute(application.getUserId());
            }
            else {
                application.toastMessage(this, "Nessuna connessione di rete. Sincronizzazione corsi fallita");
            }
            if(application.hasUserCompletedWelcome()){
                nextClass = MainActivity.class;
            }
            else {
                nextClass = WelcomeActivity.class;
            }
        }
        else {
            nextClass = FacebookActivity.class;
        }
        Log.e("Class loaded:", nextClass.toString());
        application.newDelayedActivity(TIME_SHOW_MILLIS, nextClass);
    }


    public MyApplication getMyApplication(){
        return this.application;
    }


}

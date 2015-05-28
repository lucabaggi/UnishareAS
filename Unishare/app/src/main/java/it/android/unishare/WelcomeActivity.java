package it.android.unishare;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;



public class WelcomeActivity extends SmartActivity {

    private static final String WELCOME1_FRAGMENT_INSTANCE = "welcome1_fragment_key";

	public static final String TAG = "WelcomeActivity";

    private static final String ERROR = "error";

	private MyApplication application;
	private Welcome1Fragment welcome1Fragment;


    private Toolbar toolbar;

    private static final String UNIVERSITY_SELECTION_TAG = "universitySelection";

	ArrayList<Entity> adapterValues = new ArrayList<Entity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Benvenuto su Unishare");
        }
        application = MyApplication.getInstance(this);

        if(savedInstanceState != null){
            /*
        	searchFragment = (SearchFragment)getFragmentManager().getFragment(savedInstanceState, BOOKS_SEARCH_FRAGMENT_INSTANCE);
        	Log.i(TAG, "Existing fragment");
        	FragmentTransaction transaction = getFragmentManager().beginTransaction();
        	transaction.add(R.id.books_fragment_container, searchFragment, SearchFragment.TAG);
        	*/
        }
        else{
        	welcome1Fragment = new Welcome1Fragment();
        	Log.i(TAG, "Fragment not found. Creating new fragment");
        	getFragmentManager().beginTransaction()
        	.add(R.id.welcome_container, welcome1Fragment, Welcome1Fragment.TAG).commit();
        }

        getUniversities(null);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	application.setActivity(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	ArrayList<Entity> values = new ArrayList<Entity>();

        getFragmentManager().putFragment(outState, WELCOME1_FRAGMENT_INSTANCE, welcome1Fragment);
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

    public void initializeFragmentUI(String text, com.gc.materialdesign.widgets.ProgressDialog dialog){

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void handleError(String tag){
        if(tag == ERROR){
            String title = "Nessun risultato";
            String message = "Controlla la tua connessione o modifica la tua ricerca";
            getMyApplication().alertMessage(title, message);
        }

    }

	@Override
	public void handleResult(ArrayList<Entity> result, String tag) {
		if(tag == UNIVERSITY_SELECTION_TAG) {
            welcome1Fragment.displayUniversities(null);
        }
	}

    @Override
    public void onBackPressed(){

        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void launchNewActivity(int position) {
        application.launchNewActivityFromDrawer(this, position);
    }

    //@Override
    public MyApplication getMyApplication(){
        return this.application;
    }

	/////////////////////////////////////////////////
	//Calls to database
	/////////////////////////////////////////////////

    private void getUniversities(com.gc.materialdesign.widgets.ProgressDialog dialog) {
        application.databaseCall("universities.php", UNIVERSITY_SELECTION_TAG, dialog);
    }


}

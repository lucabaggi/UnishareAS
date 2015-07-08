package it.android.unishare;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.gc.materialdesign.widgets.ProgressDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class FilesActivity extends AdapterActivity {

    public final static String TAG = "FilesActivity";

    private static final String ADAPTER_VALUES = "key_adapter";
    private static final String FILES_SEARCH_FRAGMENT_INSTANCE = "fragment_key";

    private static final String FILE_SEARCH_TAG = "fileSearch";
    private static final String ERROR = "error";


    private MyApplication application;

    private FilesSearchFragment filesSearchFragment;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private FilesAdapter filesAdapter;

    ArrayList<Entity> adapterValues = new ArrayList<Entity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setName();
        super.setImage();
        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Unishare");
            drawerToggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
            drawerLayout.setDrawerListener(drawerToggle);
        }
        application = MyApplication.getInstance(this);
        filesAdapter = new FilesAdapter(this, new ArrayList<Entity>());

        if(savedInstanceState != null){
            filesSearchFragment = (FilesSearchFragment)getFragmentManager()
                    .getFragment(savedInstanceState, FILES_SEARCH_FRAGMENT_INSTANCE);
            Log.i(TAG, "Existing fragment");
            adapterValues = savedInstanceState.getParcelableArrayList(ADAPTER_VALUES);
            this.filesAdapter.addAll(adapterValues);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.files_fragment_container, filesSearchFragment, FilesSearchFragment.TAG);
        }
        else{
            filesSearchFragment = new FilesSearchFragment();
            Log.i(TAG, "Fragment not found. Creating new fragment");
            getFragmentManager().beginTransaction()
                    .add(R.id.files_fragment_container, filesSearchFragment, FilesSearchFragment.TAG).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        ArrayList<Entity> values = new ArrayList<Entity>();
        /**
         * Storing nel Bundle dei valori presenti nell'adapter, in questo modo possono essere ripristinati in seguito
         * ad un cambio di configurazione, come il cambio di orientamento del dispositivo
         */
        if(filesAdapter != null){
            for(int i = 0; i < filesAdapter.getCount(); i++)
                values.add(filesAdapter.getItem(i));
            outState.putParcelableArrayList(ADAPTER_VALUES, values);
        }
        /**
         * Storing del BooksSearchFragment per poterne ripristinare lo stato in seguito ad un cambio di configurazione.
         * I valori presenti nell'adapter vanno salvati a parte poich� non vengono conservati
         */
        getFragmentManager().putFragment(outState, FILES_SEARCH_FRAGMENT_INSTANCE, filesSearchFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_files, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
            return;
        }
        if(getFragmentManager().getBackStackEntryCount() == 0){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void launchNewActivity(int position){
        application.launchNewActivityFromDrawer(this, position);
        drawerLayout.closeDrawers();
    }

    @Override
    ArrayAdapter<Entity> getAdapter() {
        return this.filesAdapter;
    }

    @Override
    void initializeFragmentUI(String text, ProgressDialog dialog) {
        if(text != null && text != "") {
            searchFiles(text, dialog);
        }
    }

    @Override
    MyApplication getMyApplication() {
        return this.application;
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
        if (tag == FILE_SEARCH_TAG) {
            filesAdapter.addAll(result);
            filesSearchFragment = (FilesSearchFragment) getFragmentManager()
                    .findFragmentByTag(FilesSearchFragment.TAG);
            filesSearchFragment.displayResults(tag);
        }
    }


    //Calls to Database

    private void searchFiles(String text, ProgressDialog dialog){
        try {
            application.databaseCall("files.php?q=" + URLEncoder.encode(text.trim(), "UTF-8"),
                    FILE_SEARCH_TAG, dialog);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

package it.android.unishare;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class RequestedBooksActivity extends SmartActivity {

    public static final String TAG = "RequestedBooksActivity";

    private static final String REQUESTED_BOOKS_FRAGMENT_INSTANCE = "requested_books_fragment_key";
    private static final String ADAPTER_VALUES = "key_adapter";

    private static final String REQUESTED_BOOKS_TAG = "requestedBooks";
    private static final String REFRESH_REQUESTS_TAG = "refreshRequests";
    private static final String ERROR = "error";

    private RequestedBooksFragment requestedBooksFragment;

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    private RequestedBooksAdapter requestedBooksAdapter;
    private ArrayList<Entity> adapterValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requested_books_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setName();
        super.setImage();

        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Unishare");
            drawerToggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name){

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    MyApplication.hideKeyboard(RequestedBooksActivity.this);
                }
            };;
            drawerLayout.setDrawerListener(drawerToggle);
        }
        application = MyApplication.getInstance(this);
        requestedBooksAdapter = new RequestedBooksAdapter(this, new ArrayList<Entity>());
        if(savedInstanceState != null){
            requestedBooksFragment = (RequestedBooksFragment)getFragmentManager()
                    .getFragment(savedInstanceState, REQUESTED_BOOKS_FRAGMENT_INSTANCE);
            Log.i(TAG, "Existing fragment");
            adapterValues = savedInstanceState.getParcelableArrayList(ADAPTER_VALUES);
            this.requestedBooksAdapter.addAll(adapterValues);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.requested_books_fragment_container,
                    requestedBooksFragment, RequestedBooksFragment.TAG);
        }
        else{
            Log.i(TAG, "Fragment not exisisting, creating");
            int userId = application.getUserId();
            getRequestedBooks(userId);
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.mydata) {
            application.newActivity(MyDataActivity.class);
            return true;
        }
        else if(id == R.id.logout) {
            application.logoutUser();
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
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        ArrayList<Entity> values = new ArrayList<Entity>();
        /**
         * Storing nel Bundle dei valori presenti nell'adapter, in questo modo possono essere ripristinati in seguito
         * ad un cambio di configurazione, come il cambio di orientamento del dispositivo
         */
        if(requestedBooksAdapter != null){
            for(int i = 0; i < requestedBooksAdapter.getCount(); i++)
                values.add(requestedBooksAdapter.getItem(i));
            outState.putParcelableArrayList(ADAPTER_VALUES, values);
        }
        /**
         * Storing del RequestedBooksFragment per poterne ripristinare lo stato in seguito ad un cambio di configurazione.
         * I valori presenti nell'adapter vanno salvati a parte poich� non vengono conservati
         */
        getFragmentManager().putFragment(outState, REQUESTED_BOOKS_FRAGMENT_INSTANCE, requestedBooksFragment);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void launchNewActivity(int position){
        application.launchNewActivityFromDrawer(this, position);
        drawerLayout.closeDrawers();
    }

    public MyApplication getMyApplication(){
        return this.application;
    }

    public RequestedBooksAdapter getRequestedBooksAdapter(){
        return this.requestedBooksAdapter;
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
        if(tag == REQUESTED_BOOKS_TAG){
            if(result.isEmpty()){
                String message = "Nessun libro richiesto";
                application.alertMessage("", message);
                finish();
                return;
            }
            requestedBooksAdapter.addAll(result);
            requestedBooksFragment = new RequestedBooksFragment();
            getFragmentManager().beginTransaction().add(R.id.requested_books_fragment_container,
                    requestedBooksFragment, RequestedBooksFragment.TAG).commit();
        }
        if(tag == REFRESH_REQUESTS_TAG){
            if(result.isEmpty()){
                String message = "Nessun libro richiesto";
                application.alertMessage("", message);
                finish();
                return;
            }
            requestedBooksAdapter.clear();
            requestedBooksAdapter.addAll(result);
            requestedBooksAdapter.notifyDataSetChanged();
            RequestedBooksFragment f = (RequestedBooksFragment)getFragmentManager()
                    .findFragmentByTag(RequestedBooksFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
        }
    }

    public void refreshRequests(){
        int userId = application.getUserId();
        refreshRequests(userId);
    }



    //Database calls

    private void getRequestedBooks(int userId) {
        ProgressDialog dialog = new ProgressDialog(this, "Searching");
        getMyApplication().databaseCall("books_requested.php?u=" + userId, REQUESTED_BOOKS_TAG, dialog);
    }

    private void refreshRequests(int userId){
        getMyApplication().databaseCall("books_requested.php?u=" + userId, REFRESH_REQUESTS_TAG, null);
    }

}

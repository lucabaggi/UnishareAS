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
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;

import java.util.ArrayList;


public class UniversiActivity extends SmartActivity {

    private static final String ADAPTER_VALUES = "adapterValuesKey";
    private static final String UNIVERSI_FRAGMENT_INSTANCE = "FragmentKey";

    public static final String TAG = "UniversiActivity";

    private static final String UNIVERSI_TAG = "universi";
    private static final String REFRESH_TAG = "refresh";
    private static final String ERROR = "error";

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    private UniversiFragment universiFragment;

    private UniversiAdapter adapter;

    private ArrayList<Entity> adapterValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universi_activity);

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
                    MyApplication.hideKeyboard(UniversiActivity.this);
                }
            };;
            drawerLayout.setDrawerListener(drawerToggle);
        }
        application = MyApplication.getInstance(this);
        adapter = new UniversiAdapter(this, new ArrayList<Entity>());

        if(savedInstanceState != null){
            universiFragment = (UniversiFragment) getFragmentManager()
                    .getFragment(savedInstanceState, UNIVERSI_FRAGMENT_INSTANCE);
            Log.i(TAG, "Existing fragment");
            adapterValues = savedInstanceState.getParcelableArrayList(ADAPTER_VALUES);
            this.adapter.addAll(adapterValues);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.universi_fragment_container, universiFragment, UniversiFragment.TAG);
        }
        else{
            String title = "Searching";
            ProgressDialog dialog = new ProgressDialog(this, title);
            getUniversiNews(dialog);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Entity> values = new ArrayList<Entity>();
        /**
         * Storing nel Bundle dei valori presenti nell'adapter, in questo modo possono essere ripristinati in seguito
         * ad un cambio di configurazione, come il cambio di orientamento del dispositivo
         */
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++)
                values.add(adapter.getItem(i));
            outState.putParcelableArrayList(ADAPTER_VALUES, values);
        }
        /**
         * Storing del CoursesSearchFragment per poterne ripristinare lo stato in seguito ad un cambio di configurazione.
         * I valori presenti nell'adapter vanno salvati a parte poich� non vengono conservati
         */
        getFragmentManager().putFragment(outState, UNIVERSI_FRAGMENT_INSTANCE, universiFragment);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
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

    public MyApplication getMyApplication(){
        return this.application;
    }

    public UniversiAdapter getAdapter(){
        return this.adapter;
    }

    @Override
    public void handleError(String tag){
        if(tag == ERROR){
            String title = "Nessun risultato";
            String message = "Controlla la tua connessione o modifica la tua ricerca";
            getMyApplication().alertMessage(title, message);
            universiFragment = new UniversiFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.universi_fragment_container, universiFragment, UniversiFragment.TAG).commit();
        }

    }


    public void handleResult(ArrayList<Entity> result, String tag) {
        if(tag == UNIVERSI_TAG){
            adapter.addAll(result);
            universiFragment = new UniversiFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.universi_fragment_container, universiFragment, UniversiFragment.TAG).commit();

        }
        if(tag == REFRESH_TAG){
            adapter.clear();
            adapter.addAll(result);
            adapter.notifyDataSetChanged();
            UniversiFragment f = (UniversiFragment) getFragmentManager()
                    .findFragmentByTag(UniversiFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
        }
    }

    public void refresh(){
        if(Utilities.checkNetworkState(this))
            refreshUniversiNews();
        else{
            String title = "Errore";
            String message = "Controlla la tua connessione a Internet e riprova";
            UniversiFragment f = (UniversiFragment)getFragmentManager()
                    .findFragmentByTag(UniversiFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
            application.alertMessage(title, message);
        }
    }


    //Database Calls

    private void getUniversiNews(ProgressDialog dialog){
        application.databaseCall("universi.php", UNIVERSI_TAG, dialog);
    }

    private void refreshUniversiNews(){
        application.databaseCall("universi.php", REFRESH_TAG, null);
    }

}

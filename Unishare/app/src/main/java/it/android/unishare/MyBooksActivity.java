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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MyBooksActivity extends SmartActivity {

    public static final String TAG = "MyBooksActivity";

    private static final String ADAPTER_VALUES = "adapter_values_key";
    private static final String REQUESTS_VALUES = "requests_values_key";
    private static final String MY_BOOKS_FRAGMENT_INSTANCE = "my_books_fragment_key";
    private static final String REQUESTS_FRAGMENT_INSTANCE = "requests_fragment_key";
    private static final String SELL_FRAGMENT_INSTANCE = "sell_fragment_key";

    private static final String BOOKS_TAG = "soldBooks";
    private static final String SELL_BOOK_TAG = "bookSelling";
    private static final String GET_REQUESTS_TAG = "booksRequests";
    private static final String REFRESH_BOOKS_TAG = "refreshBooks";

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    private MyBooksAdapter myBooksAdapter;
    private RequestsAdapter requestsAdapter;

    private MyBooksFragment myBooksFragment;
    private SellBookFragment sellBookFragment;
    private RequestsFragment requestsFragment;

    ArrayList<Entity> adapterValues = new ArrayList<Entity>();
    ArrayList<Entity> requestsAdapterValues = new ArrayList<Entity>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_books_activity);

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
                    MyApplication.hideKeyboard(MyBooksActivity.this);
                }
            };;
            drawerLayout.setDrawerListener(drawerToggle);
        }
        application = MyApplication.getInstance(this);
        myBooksAdapter = new MyBooksAdapter(this, new ArrayList<Entity>());

        if(savedInstanceState != null) {
            myBooksFragment = (MyBooksFragment) getFragmentManager()
                    .getFragment(savedInstanceState, MY_BOOKS_FRAGMENT_INSTANCE);
            Log.i(TAG, "Existing fragment");
            adapterValues = savedInstanceState.getParcelableArrayList(ADAPTER_VALUES);
            this.myBooksAdapter.addAll(adapterValues);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.my_books_fragment_container, myBooksFragment, MyBooksFragment.TAG);

            if (getFragmentManager().getFragment(savedInstanceState, REQUESTS_FRAGMENT_INSTANCE) != null) {
                requestsAdapterValues = savedInstanceState.getParcelableArrayList(REQUESTS_VALUES);
                requestsAdapter = new RequestsAdapter(this, new ArrayList<Entity>());
                requestsAdapter.addAll(requestsAdapterValues);

                requestsFragment = (RequestsFragment) getFragmentManager()
                        .getFragment(savedInstanceState, REQUESTS_FRAGMENT_INSTANCE);
            }
            if(getFragmentManager().getFragment(savedInstanceState, SELL_FRAGMENT_INSTANCE) != null){
                sellBookFragment = (SellBookFragment) getFragmentManager()
                        .getFragment(savedInstanceState, SELL_FRAGMENT_INSTANCE);
            }
        }
        else{
            Log.i(TAG, "Fragment not exists, creating");
            int userId = application.getUserId();
            getSoldBooks(userId);
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
        if (myBooksAdapter != null) {
            for (int i = 0; i < myBooksAdapter.getCount(); i++)
                values.add(myBooksAdapter.getItem(i));
            outState.putParcelableArrayList(ADAPTER_VALUES, values);
        }
        /**
         * Storing del CoursesSearchFragment per poterne ripristinare lo stato in seguito ad un cambio di configurazione.
         * I valori presenti nell'adapter vanno salvati a parte poich� non vengono conservati
         */
        getFragmentManager().putFragment(outState, MY_BOOKS_FRAGMENT_INSTANCE, myBooksFragment);
        if (this.requestsFragment != null)
            if(this.requestsFragment.isVisible()){
                ArrayList<Entity> requests = new ArrayList<Entity>();
                if (requestsAdapter != null) {
                    for (int i = 0; i < requestsAdapter.getCount(); i++)
                        requests.add(requestsAdapter.getItem(i));
                    outState.putParcelableArrayList(REQUESTS_VALUES, requests);
                }
                getFragmentManager().putFragment(outState, REQUESTS_FRAGMENT_INSTANCE, requestsFragment);
            }
        if(this.sellBookFragment != null)
            if(this.sellBookFragment.isVisible())
                getFragmentManager().putFragment(outState, SELL_FRAGMENT_INSTANCE, sellBookFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_books, menu);
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

    public MyBooksAdapter getMyBooksAdapter(){ return this.myBooksAdapter; }

    public RequestsAdapter getRequestAdapter(){ return this.requestsAdapter; }

    @Override
    public void handleResult(ArrayList<Entity> result, String tag) {
        if(tag == BOOKS_TAG){
            Log.i(TAG, "Handling results, launching MyBooksFragment");
            myBooksAdapter.addAll(result);
            myBooksFragment = new MyBooksFragment();
            getFragmentManager().beginTransaction().add(R.id.my_books_fragment_container,
                    myBooksFragment, MyBooksFragment.TAG).commit();
        }
        if(tag == SELL_BOOK_TAG) {
            if (!result.isEmpty()) {
                if (result.get(0).getFirst().equals("ERROR")) {
                    Log.i(TAG, "Error, libro non inserito");
                    getFragmentManager().beginTransaction().remove(sellBookFragment).commit();
                    getFragmentManager().popBackStack();
                    String title = "Errore";
                    String message = "Libro non inserito";
                    application.alertMessage(title, message);
                    return;
                }
                Log.i(TAG, "Libro inserito correttamente");
                getFragmentManager().beginTransaction().remove(sellBookFragment).commit();
                getFragmentManager().popBackStack();
                String title = "";
                String message = "Libro messo in vendita. Grazie per il contributo!";
                application.alertMessage(title, message);
                refreshBooks();
            }
        }
        if(tag == GET_REQUESTS_TAG){
            if(result.isEmpty()){
                Log.i(TAG, "No requests for the book");
                String message = "Nessuna richiesta per il libro selezionato";
                application.alertMessage("", message);
                return;
            }
            requestsAdapter = new RequestsAdapter(this, new ArrayList<Entity>());
            requestsAdapter.addAll(result);
            requestsFragment = new RequestsFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.my_books_fragment_container, requestsFragment, RequestsFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if(tag == REFRESH_BOOKS_TAG){
            myBooksAdapter.clear();
            myBooksAdapter.addAll(result);
            myBooksAdapter.notifyDataSetChanged();
            MyBooksFragment f = (MyBooksFragment)getFragmentManager()
                    .findFragmentByTag(MyBooksFragment.TAG);
            if(f.getSwipeRefreshLayout().isRefreshing())
                f.getSwipeRefreshLayout().setRefreshing(false);
        }
    }

    public void launchSellFragment() {
        sellBookFragment = new SellBookFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.my_books_fragment_container, sellBookFragment, SellBookFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void sellBook(String bookTitle, String author, float price, com.gc.materialdesign.widgets.ProgressDialog dialog){
        Log.i(TAG, "Selling book");
        int userId = application.getUserId();
        insertSellingBook(bookTitle, author, price, userId, dialog);
    }

    public void getRequests(String bookId, ProgressDialog dialog){
        getRequestsForBook(bookId, dialog);
    }

    public void refreshBooks(){
        int userId = application.getUserId();
        refreshSoldBooks(userId);
    }

    public void removeBook(String bookId){

    }

    public void bookSold(String bookId){

    }

    //Database calls

    private void getSoldBooks(int userId) {
        ProgressDialog dialog = new ProgressDialog(this, "Searching");
        getMyApplication().databaseCall("books_sold.php?u=" + userId, BOOKS_TAG, dialog);
    }

    private void insertSellingBook(String bookTitle, String author, float price, int userId, com.gc.materialdesign.widgets.ProgressDialog dialog) {
        try {
            application.databaseCall("books_insert.php?u=" + userId + "&t=" + URLEncoder.encode(bookTitle, "UTF-8")
                    + "&a=" + URLEncoder.encode(author, "UTF-8") + "&p=" + price, SELL_BOOK_TAG, dialog);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getRequestsForBook(String bookId, ProgressDialog dialog){
        application.databaseCall("books_requests.php?id=" + bookId, GET_REQUESTS_TAG, dialog);
    }

    private void refreshSoldBooks(int userId){
        getMyApplication().databaseCall("books_sold.php?u=" + userId, REFRESH_BOOKS_TAG, null);
    }
}

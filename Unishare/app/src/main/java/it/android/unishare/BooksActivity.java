package it.android.unishare;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import it.android.unishare.SearchFragment.OnBookSelectedListener;
import it.android.unishare.R;
import it.android.unishare.DatabaseContract.UserInfoTable;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class BooksActivity extends AdapterActivity implements OnBookSelectedListener {
	
	public static final String TAG = "BooksActivity";

	private static final String BOOKS_SEARCH_FRAGMENT_INSTANCE = "books_search_fragment_key";
	private static final String ADAPTER_VALUES = "key_adapter";

	private static final String BOOKS_SEARCH_TAG = "bookSearch";
	private static final String BOOK_DETAILS_TAG = "bookDetail";
    private static final String REQUEST_BOOK_TAG = "bookRequest";
    private static final String ERROR = "error";

	private MyApplication application;
	private SearchFragment searchFragment;
    private BooksAdapter adapter;
	private Entity book;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

	ArrayList<Entity> adapterValues = new ArrayList<Entity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_activity);
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
                    MyApplication.hideKeyboard(BooksActivity.this);
                }
            };
            drawerLayout.setDrawerListener(drawerToggle);
        }
        application = MyApplication.getInstance(this);
        adapter = new BooksAdapter(this, new ArrayList<Entity>());
        /**
         * Se il Bundle non � null significa che l'Entity � stata ricreata in seguito ad un cambio di configurazione, quindi
         * devo ripristinare il BooksSearchFragment con i valori presenti nell'adapter prima del cambio di configurazione
         */
        if(savedInstanceState != null){
        	searchFragment = (SearchFragment)getFragmentManager().getFragment(savedInstanceState, BOOKS_SEARCH_FRAGMENT_INSTANCE);
        	Log.i(TAG, "Existing fragment");
			adapterValues = savedInstanceState.getParcelableArrayList(ADAPTER_VALUES);
			this.adapter.addAll(adapterValues);
        	FragmentTransaction transaction = getFragmentManager().beginTransaction();
        	transaction.add(R.id.books_fragment_container, searchFragment, SearchFragment.TAG);
        }
        else{
        	searchFragment = new SearchFragment();
        	Log.i(TAG, "Fragment not found. Creating new fragment");
        	getFragmentManager().beginTransaction()
        	.add(R.id.books_fragment_container, searchFragment, SearchFragment.TAG).commit();
        }
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
    	/**
    	 * Storing nel Bundle dei valori presenti nell'adapter, in questo modo possono essere ripristinati in seguito
    	 * ad un cambio di configurazione, come il cambio di orientamento del dispositivo
    	 */
    	if(adapter != null){
    		for(int i = 0; i < adapter.getCount(); i++)
        		values.add(adapter.getItem(i));
        	outState.putParcelableArrayList(ADAPTER_VALUES, values);
    	}
    	/**
    	 * Storing del BooksSearchFragment per poterne ripristinare lo stato in seguito ad un cambio di configurazione.
    	 * I valori presenti nell'adapter vanno salvati a parte poich� non vengono conservati
    	 */
        getFragmentManager().putFragment(outState, BOOKS_SEARCH_FRAGMENT_INSTANCE, searchFragment);
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

    public void initializeFragmentUI(String text, com.gc.materialdesign.widgets.ProgressDialog dialog){
    	if(text != null && text != "") {
    		searchBooks(0, text, dialog);
    	}
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
    public void handleError(String tag){
        if(tag == ERROR){
            String title = "Nessun risultato";
            String message = "Controlla la tua connessione o modifica la tua ricerca";
            getMyApplication().alertMessage(title, message);
        }

    }

	@Override
	public void handleResult(ArrayList<Entity> result, String tag) {
		if(tag == BOOKS_SEARCH_TAG) {
			adapter.addAll(result);
			searchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(SearchFragment.TAG);
			searchFragment.displayResults(tag);
		}
		if(tag == BOOK_DETAILS_TAG){
			this.book = result.get(0);
			Log.i(TAG, "BookId = " + book.get("id"));
			BooksDetailsFragment booksDetailsFragment = new BooksDetailsFragment(book);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.books_fragment_container, booksDetailsFragment, BooksDetailsFragment.TAG);
			transaction.addToBackStack(null);
			transaction.commit();
		}
        if(tag == REQUEST_BOOK_TAG) {
            if (!result.isEmpty()) {
                if (result.get(0).getFirst().equals("ERROR")) {
                    String title = "Error";
                    String message = "Richiesta libro non eseguita";
                    application.alertMessage(title, message);
                    return;
                }
                String title = "";
                String message = "Libro richiesto con successo. Una notifica è appena stata inviata al venditore";
                application.alertMessage(title, message);
            }
        }
	}


	@Override
	public void onBookSelected(String bookId, com.gc.materialdesign.widgets.ProgressDialog dialog) {
		int id = Integer.parseInt(bookId);
		getBook(id, dialog);
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
	public BooksAdapter getAdapter(){
		return this.adapter;
	}

    @Override
    public MyApplication getMyApplication(){
        return this.application;
    }


    public void requestBook(int bookId) {
        int userId = application.getUserId();
        callRequestBook(userId, bookId);
    }


    /////////////////////////////////////////////////
	//Calls to database
	/////////////////////////////////////////////////

	private void searchBooks(int campusId, String text, com.gc.materialdesign.widgets.ProgressDialog dialog) {
		try {
			application.databaseCall("books_search.php?q=" + URLEncoder.encode(text.trim(), "UTF-8") + "&s=" + campusId, BOOKS_SEARCH_TAG, dialog);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void getBook(int bookId, com.gc.materialdesign.widgets.ProgressDialog dialog) {
		application.databaseCall("books_detail.php?id=" + bookId, BOOK_DETAILS_TAG, dialog);
	}


    private void callRequestBook(int userId, int bookId) {
        application.databaseCall("books_request.php?id=" + bookId + "&u=" + userId, REQUEST_BOOK_TAG, null);
    }



}

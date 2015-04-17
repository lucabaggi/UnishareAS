package it.android.unishare;

import it.android.unishare.SearchFragment.OnCourseSelectedListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class CoursesActivity extends AdapterActivity implements OnCourseSelectedListener{
	
	public static final String TAG = "CoursesActivity";
	
	/**
	 * Keys necessarie per ripristinare lo stato dei Fragment associati all'activity dopo un cambio di configurazione
	 * (es. cambio orientamento dispositivo)
	 */
	private static final String COURSES_SEARCH_FRAGMENT_INSTANCE = "courses_search_fragment_key";
	private static final String INSERT_OPINION_FRAGMENT_INSTANCE = "insert_opinion_fragment_key";
	private static final String COURSE_NAME = "course_name_key";
	private static final String ADAPTER_VALUES = "key_adapter";
	private static final String OPINION_ADAPTER_VALUES = "key_opinion_adapter";
	
	/**
	 * Tag necessari per distinguere le chiamate al db esterno e le relative risposte
	 */
	private static final String COURSE_SEARCH_TAG = "courseSearch";
	private static final String OPINION_TAG = "opinionSearch";
	private static final String INSERT_OPINION_TAG = "insertOpinion";
	private static final String REFRESH_OPINIONS_ADAPTER = "refreshOpinionsAdapter";
	
	private MyApplication application;
	private SearchFragment searchFragment;
	private OpinionsFragment opinionsFragment;
	private InsertOpinionFragment insertOpinionFragment;
	private CoursesAdapter coursesAdapter;
	private OpinionsAdapter opinionsAdapter;
	
	private String courseName;
	private int courseId;
	
	ArrayList<Entity> adapterValues = new ArrayList<Entity>();
	ArrayList<Entity> opinionAdapterValues = new ArrayList<Entity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courses);
		application = MyApplication.getInstance(this);
		coursesAdapter = new CoursesAdapter(this, new ArrayList<Entity>());
        /**
         * Se il Bundle non � null significa che l'Entity � stata ricreata in seguito ad un cambio di configurazione, quindi
         * devo ripristinare il BooksSearchFragment con i valori presenti nell'adapter prima del cambio di configurazione
         */
        if(savedInstanceState != null){
        	searchFragment = (SearchFragment)getFragmentManager().getFragment(savedInstanceState, COURSES_SEARCH_FRAGMENT_INSTANCE);
        	Log.i(TAG, "Existing fragment");
			adapterValues = savedInstanceState.getParcelableArrayList(ADAPTER_VALUES);
			this.coursesAdapter.addAll(adapterValues);
        	FragmentTransaction transaction = getFragmentManager().beginTransaction();
        	transaction.add(R.id.courses_fragment_container, searchFragment, SearchFragment.TAG);
        	
        	if(savedInstanceState.getString(COURSE_NAME) != null){
        		this.courseName = savedInstanceState.getString(COURSE_NAME);
        		opinionAdapterValues = savedInstanceState.getParcelableArrayList(OPINION_ADAPTER_VALUES);
        		opinionsAdapter = new OpinionsAdapter(this, new ArrayList<Entity>());
        		opinionsAdapter.addAll(opinionAdapterValues);
        	}
        	if(getFragmentManager().getFragment(savedInstanceState, INSERT_OPINION_FRAGMENT_INSTANCE) != null)
        		insertOpinionFragment = (InsertOpinionFragment)getFragmentManager().getFragment(savedInstanceState, INSERT_OPINION_FRAGMENT_INSTANCE);
        }
        else{
        	searchFragment = new SearchFragment();
        	Log.i(TAG, "Fragment not found. Creating new fragment");
        	getFragmentManager().beginTransaction()
        	.add(R.id.courses_fragment_container, searchFragment, SearchFragment.TAG).commit();
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
    	if(coursesAdapter != null){
    		for(int i = 0; i < coursesAdapter.getCount(); i++)
        		values.add(coursesAdapter.getItem(i));
        	outState.putParcelableArrayList(ADAPTER_VALUES, values);
    	} 	
    	/**
    	 * Storing del CoursesSearchFragment per poterne ripristinare lo stato in seguito ad un cambio di configurazione.
    	 * I valori presenti nell'adapter vanno salvati a parte poich� non vengono conservati
    	 */
        getFragmentManager().putFragment(outState, COURSES_SEARCH_FRAGMENT_INSTANCE, searchFragment);
        if(this.courseName != null){
        	outState.putString(COURSE_NAME, this.courseName);
        	ArrayList<Entity> opinions = new ArrayList<Entity>();
        	if(opinionsAdapter != null){
        		for(int i = 0; i < opinionsAdapter.getCount(); i++)
        			opinions.add(opinionsAdapter.getItem(i));
        		outState.putParcelableArrayList(OPINION_ADAPTER_VALUES, opinions);
        	}
        			
        }
        if(this.insertOpinionFragment != null)
        	if(this.insertOpinionFragment.isVisible())
        		getFragmentManager().putFragment(outState, INSERT_OPINION_FRAGMENT_INSTANCE, insertOpinionFragment);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.courses, menu);
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
	
	public void initializeFragmentUI(String text, ProgressDialog dialog){
    	if(text != null && text != "") {
    		searchCourses(0, text, dialog);
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
	public void handleResult(ArrayList<Entity> result, String tag) {
		if(tag == COURSE_SEARCH_TAG) {
			coursesAdapter.addAll(result);
			searchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(SearchFragment.TAG);			
			searchFragment.displayResults(tag);
		}
		if(tag == OPINION_TAG){
			opinionsAdapter = new OpinionsAdapter(this, new ArrayList<Entity>());
			opinionsAdapter.addAll(result);
			createOpinionFragment();
		}
		if(tag == INSERT_OPINION_TAG){
			if(!result.isEmpty()){
				Log.i(TAG,"Opinione inserita correttamente");
				getFragmentManager().beginTransaction().remove(insertOpinionFragment).commit();
				getFragmentManager().popBackStack();
				String title = "";
				String message = "Opinione inserita. Grazie per il tuo contributo!";
				application.alertMessage(title, message);
				ProgressDialog dialog = new ProgressDialog(this);
				String dialogTitle = "Searching";
				String dialogMessage = "Please wait...";
				dialog.setTitle(dialogTitle);
				dialog.setMessage(dialogMessage);
				refreshOpinions(courseId, dialog);
			}
		}
		if(tag == REFRESH_OPINIONS_ADAPTER){
			Log.i(TAG, "Refreshing dell'adapter");
			opinionsAdapter.clear();
			opinionsAdapter.addAll(result);
			opinionsAdapter.notifyDataSetChanged();
		}
	}
	
	private void createOpinionFragment(){
		opinionsFragment = new OpinionsFragment(courseName);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.courses_fragment_container, opinionsFragment, OpinionsFragment.TAG);
		transaction.addToBackStack(null);
		transaction.commit();	
	}
	
	@Override
	public CoursesAdapter getAdapter(){
		return this.coursesAdapter;
	}
	
	@Override
	public void onCourseSelected(String courseId, String courseName, ProgressDialog dialog) {
		this.courseName = courseName;
		this.courseId = Integer.parseInt(courseId);
		getOpinion(this.courseId, dialog);
		
	}
	
	public OpinionsAdapter getOpinionsAdapter(){
		return this.opinionsAdapter;
	}
	
	public String getCourseName(){
		return this.courseName;
	}
	
	public void createInsertOpinionFragment() {
		insertOpinionFragment = new InsertOpinionFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.courses_fragment_container, insertOpinionFragment, InsertOpinionFragment.TAG);
		transaction.addToBackStack(null);
		transaction.commit();	
	}
	
	public void insertOpinion(String opinion, float rating, ProgressDialog dialog){
		Log.i(TAG, "Calling db for inserting opinion");
		Log.i(TAG, "Commento: " + opinion + "\nvoto: " + rating +" per il corso " + courseId);
		insertOpinion(courseId, rating, opinion, 2418, dialog);
	}
	
	/////////////////////////////////
	// 	   Calls to database       //
	/////////////////////////////////
	
	private void searchCourses(int campusId, String text, ProgressDialog dialog) {
		try {
			application.databaseCall("courses_search.php?q=" + URLEncoder.encode(text, "UTF-8") + "&s=" + campusId, COURSE_SEARCH_TAG, dialog);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}
	
	private void getOpinion(int courseId, ProgressDialog dialog){
		application.databaseCall("opinions.php?id=" + courseId, OPINION_TAG, dialog);
	}
	
	private void refreshOpinions(int courseId, ProgressDialog dialog){
		application.databaseCall("opinions.php?id=" + courseId, REFRESH_OPINIONS_ADAPTER, dialog);
	}

	private void insertOpinion(int courseId, float rating, String text, int cdsId, ProgressDialog dialog){
		try {
			application.databaseCall("opinions_insert.php?id=" + courseId + "&v=" + rating + "&c=" + URLEncoder.encode(text, "UTF-8") + "&u=" + cdsId, INSERT_OPINION_TAG, dialog);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}

package it.android.unishare;

import it.android.unishare.SearchFragment.OnCourseSelectedListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

import com.gc.materialdesign.widgets.ProgressDialog;

public class CoursesActivity extends CourseSupportActivity implements OnCourseSelectedListener{
	
	public static final String TAG = "CoursesActivity";
	
	/**
	 * Keys necessarie per ripristinare lo stato dei Fragment associati all'activity dopo un cambio di configurazione
	 * (es. cambio orientamento dispositivo)
	 */
	private static final String COURSES_SEARCH_FRAGMENT_INSTANCE = "courses_search_fragment_key";
	private static final String INSERT_OPINION_FRAGMENT_INSTANCE = "insert_opinion_fragment_key";
    private static final String ADD_COURSE_FRAGMENT_INSTANCE = "add_course_fragment_key";
	private static final String COURSE_NAME = "course_name_key";
	private static final String ADAPTER_VALUES = "key_adapter";
	private static final String OPINION_ADAPTER_VALUES = "key_opinion_adapter";


    //private MyApplication application;
	private SearchFragment searchFragment;
	private OpinionsFragment opinionsFragment;
	private InsertOpinionFragment insertOpinionFragment;
	private AddCourseFragment addCourseFragment;
	private CoursesAdapter coursesAdapter;
	private OpinionsAdapter opinionsAdapter;
	
	private String courseName;
	private int courseId;

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
	
	ArrayList<Entity> adapterValues = new ArrayList<Entity>();
	ArrayList<Entity> opinionAdapterValues = new ArrayList<Entity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courses);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        super.setName();
        super.setImage();
        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Unishare");
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
            drawerLayout.setDrawerListener(drawerToggle);
        }
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
        		insertOpinionFragment = (InsertOpinionFragment)getFragmentManager()
						.getFragment(savedInstanceState, INSERT_OPINION_FRAGMENT_INSTANCE);
            if(getFragmentManager().getFragment(savedInstanceState, ADD_COURSE_FRAGMENT_INSTANCE) != null)
                addCourseFragment = (AddCourseFragment)getFragmentManager()
                        .getFragment(savedInstanceState, ADD_COURSE_FRAGMENT_INSTANCE);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
        		getFragmentManager()
                        .putFragment(outState, INSERT_OPINION_FRAGMENT_INSTANCE, insertOpinionFragment);
        if(this.addCourseFragment != null)
            if(this.addCourseFragment.isVisible())
                getFragmentManager()
                        .putFragment(outState, ADD_COURSE_FRAGMENT_INSTANCE, addCourseFragment);
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
		if (id == R.id.mydata) {
			application.newActivity(FilesActivity.class);
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
	
	public void initializeFragmentUI(String text, com.gc.materialdesign.widgets.ProgressDialog dialog){
    	if(text != null && text != "") {
    		searchCourses(0, text, dialog);
    	}
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
	public void handleError(String tag){
		if(tag == ERROR){
			String title = "Nessun risultato";
			String message = "Controlla la tua connessione o modifica la tua ricerca";
			getMyApplication().alertMessage(title, message);
		}
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
				if(result.get(0).getFirst().equals("ERROR")){
					Log.i(CoursesActivity.TAG,"Error, opinione già inserita dall'utente");
					getFragmentManager().beginTransaction().remove(insertOpinionFragment).commit();
					getFragmentManager().popBackStack();
					String title = "Errore";
					String message = "Opinione non inserita. Verifica di non aver già recensito questo corso";
					application.alertMessage(title, message);
					return;
				}
				Log.i(TAG,"Opinione inserita correttamente");
				getFragmentManager().beginTransaction().remove(insertOpinionFragment).commit();
				getFragmentManager().popBackStack();
				String title = "";
				String message = "Opinione inserita. Grazie per il tuo contributo!";
				application.alertMessage(title, message);
				refreshOpinions(courseId);
			}
		}
		if(tag == REFRESH_OPINIONS_ADAPTER){
			Log.i(TAG, "Refreshing dell'adapter");
			opinionsAdapter.clear();
			opinionsAdapter.addAll(result);
			opinionsAdapter.notifyDataSetChanged();
		}
        if(tag == INSERT_COURSE_TAG) {
            if (!result.isEmpty()) {
                if (result.get(0).getFirst().equals("ERROR")) {
                    Log.i(CoursesActivity.TAG, "Errore inserimento corso");
                    getFragmentManager().beginTransaction().remove(addCourseFragment).commit();
                    getFragmentManager().popBackStack();
                    String title = "Errore";
                    String message = "Corso non inserito";
                    application.alertMessage(title, message);
                    return;
                }
                getFragmentManager().beginTransaction().remove(addCourseFragment).commit();
                getFragmentManager().popBackStack();
                String title = "";
                String message = "Corso inserito correttamente. Grazie per il tuo contributo!";
                application.alertMessage(title, message);
            }
        }
        if(tag == ADD_TO_ACTUAL_TAG){
            if (!result.isEmpty()) {
                if(!result.get(0).getFirst().equals("ERROR"))
                    Log.i(TAG, "corso aggiunto al db esterno");
            }
        }
        if(tag == ADD_TO_PAST_TAG){
            if (!result.isEmpty()) {
                if(!result.get(0).getFirst().equals("ERROR"))
                    Log.i(TAG, "corso aggiunto al db esterno");
            }
        }
	}

    @Override
    public MyApplication getMyApplication(){
        return this.application;
    }


    @Override
    public void launchNewActivity(int position){
        application.launchNewActivityFromDrawer(this, position);
        drawerLayout.closeDrawers();
    }

	
	@Override
	public CoursesAdapter getAdapter(){
		return this.coursesAdapter;
	}
	
	@Override
	public void onCourseSelected(String courseId, String courseName, com.gc.materialdesign.widgets.ProgressDialog dialog) {
		this.courseName = courseName;
		this.courseId = Integer.parseInt(courseId);
        getOpinion(this.courseId, dialog);
		
	}

    @Override
	public OpinionsAdapter getOpinionsAdapter(){
		return this.opinionsAdapter;
	}

    @Override
	public String getCourseName(){
		return this.courseName;
	}

    @Override
	public void createInsertOpinionFragment() {
		insertOpinionFragment = new InsertOpinionFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.courses_fragment_container, insertOpinionFragment, InsertOpinionFragment.TAG);
		transaction.addToBackStack(null);
		transaction.commit();	
	}

    @Override
	public void insertOpinion(String opinion, float rating, com.gc.materialdesign.widgets.ProgressDialog dialog){
		Log.i(TAG, "Calling db for inserting opinion");
		Log.i(TAG, "Commento: " + opinion + "\nvoto: " + rating + " per il corso " + courseId);
		int userId = application.getUserId();
		insertOpinion(courseId, rating, opinion, userId, dialog);
	}

    public void launchAddCourseFragment() {
        addCourseFragment = new AddCourseFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.courses_fragment_container, addCourseFragment, AddCourseFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createOpinionFragment(){
        opinionsFragment = new OpinionsFragment(courseName);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.courses_fragment_container, opinionsFragment, OpinionsFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void insertNewCourse(String courseName, String prof, String language, float cfu, String radioValue, ProgressDialog dialog) {
        int userId = application.getUserId();
        int index = Faculty.getInstance().getMap().get(radioValue);
        Log.i(CoursesActivity.TAG, radioValue + " , indice: " + index);
        insertCourse(userId, courseName, prof, language, cfu, index, dialog);
    }

	public void addToActualExams(int courseId){
		int userId = application.getUserId();
		addToActualExams(userId, courseId);
	}

    public void addToPassedExams(int courseId, int grade, int lode){
        int userId = application.getUserId();
        addToPassedExams(userId, courseId, grade, lode);
    }



    static class Faculty{

        private static Faculty instance = null;
        private static Map<String, Integer> map;

        protected Faculty(){
            map = new HashMap<>();
            map.put("Magistrale", 0);
            map.put("Triennale", 1);
            map.put("Entrambi", 2);
        }

        public static Faculty getInstance(){
            if(instance == null)
                instance = new Faculty();
            return instance;
        }

        public static Map<String,Integer> getMap(){
            return map;
        }
    }
}

package it.android.unishare;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gc.materialdesign.widgets.ProgressDialog;

import java.util.ArrayList;



public class WelcomeActivity extends SmartActivity {

    private static final String WELCOME1_FRAGMENT_INSTANCE = "welcome1_fragment_key";

	public static final String TAG = "WelcomeActivity";

    private static final String ERROR = "error";

	private MyApplication application;
	private Welcome1Fragment welcome1Fragment;

    private ArrayList<Entity> universities, campuses, specializations, courses;
    private String universityName, universityImage;
    private int universityId;
    private String campusName,campusImage;
    private int campusId;
    private String specializationName;
    private int specializationId;

    private Toolbar toolbar;

    private static final String UNIVERSITY_SELECTION_TAG = "universitySelection";
    private static final String CAMPUS_SELECTION_TAG = "campusSelection";
    private static final String SPECIALIZATION_SELECTION_TAG = "specializationSelection";
    private static final String COURSES_SELECTION_TAG = "coursesSelection";

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

        }

        getUniversities();
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
            universities = result;
            welcome1Fragment = new Welcome1Fragment();
            Bundle args = new Bundle();
            args.putStringArrayList("universities",Entity.entityListToStringList(result,"nome"));
            welcome1Fragment.setArguments(args);
            getFragmentManager().beginTransaction().add(R.id.welcome_container, welcome1Fragment, Welcome1Fragment.TAG).commit();
        }
        else if(tag == CAMPUS_SELECTION_TAG) {
            campuses = result;
            Fragment welcome2 = new Welcome2Fragment();
            Bundle args = new Bundle();
            args.putString("universityName", universityName);
            args.putString("universityImage",universityImage);
            args.putStringArrayList("campuses", Entity.entityListToStringList(result,"nome"));
            welcome2.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.welcome_container, welcome2, Welcome2Fragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if(tag == SPECIALIZATION_SELECTION_TAG) {
            specializations = result;
            Fragment welcome3 = new Welcome3Fragment();
            Bundle args = new Bundle();
            args.putString("campusImage",campusImage);
            args.putStringArrayList("specializations", Entity.entityListToStringList(result,"nome"));
            welcome3.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.welcome_container, welcome3, Welcome2Fragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if(tag == COURSES_SELECTION_TAG) {
            courses = result;
            Fragment welcome4 = new Welcome4Fragment();
            Bundle args = new Bundle();
            args.putStringArrayList("courses", Entity.entityListToStringList(result,"nome"));
            args.putStringArrayList("professors", Entity.entityListToStringList(result,"professore"));
            welcome4.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.welcome_container, welcome4, Welcome4Fragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
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

    public void goToCampusSelection(String universityName) {
        for(Entity e : universities) {
            if(e.get("nome").equals(universityName)) {
                this.universityName = e.get("nome");
                universityImage = e.get("immagine");
                universityId = e.getInt("id");
                getCampuses();
                break;
            }
        }
    }

    public void goToSpecializationSelection(String campusName) {
        for(Entity e : campuses) {
            if(e.get("nome").equals(campusName)) {
                this.campusName = campusName;
                campusImage = e.get("immagine");
                campusId = e.getInt("id");
                getSpecializations();
                break;
            }
        }
    }

    public void goToCourseSelection(String specializationName) {
        for(Entity e : specializations) {
            if(e.get("nome").equals(specializationName)) {
                this.specializationName = e.get("nome");
                specializationId = e.getInt("id");
                getCourses();
                break;
            }
        }
    }

    public void goToDashboard() {
        application.customQuery("UPDATE user_info SET university_id="+universityId+",university=\""+universityName+"\",campus_id="+campusId+",campus=\""+campusName+"\",specialization_id="+specializationId+",specialization=\""+specializationName+"\"");
        application.fetchUserData();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

	/////////////////////////////////////////////////
	//Calls to database
	/////////////////////////////////////////////////

    private void getUniversities() {
        ProgressDialog dialog = new ProgressDialog(this, "Caricamento...");
        application.databaseCall("universities.php", UNIVERSITY_SELECTION_TAG, dialog);
    }

    private void getCampuses() {
        ProgressDialog dialog = new ProgressDialog(this, "Caricamento...");
        application.databaseCall("campuses.php?e="+universityId, CAMPUS_SELECTION_TAG, dialog);
    }

    private void getSpecializations() {
        ProgressDialog dialog = new ProgressDialog(this, "Caricamento...");
        application.databaseCall("specializations.php?e="+campusId, SPECIALIZATION_SELECTION_TAG, dialog);
    }

    private void getCourses() {
        ProgressDialog dialog = new ProgressDialog(this, "Caricamento...");
        application.databaseCall("courses.php?s="+campusId, COURSES_SELECTION_TAG, dialog);
    }

    public void addToCourses(String text) {
        for(Entity e : courses) {
            if((e.get("nome")+" ("+e.get("professore")+")").equals(text)) {
                //Add course to actual courses locally & on server
                int courseId = e.getInt("id");
                String courseName = e.get("nome");
                String professor = e.get("professore");
                if(!inPassedExams(courseId)){
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.MyCoursesTable.COLUMN_NAME, courseName);
                    values.put(DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID, courseId);
                    values.put(DatabaseContract.MyCoursesTable.COLUMN_PROFESSOR, professor);
                    try{
                        application.insertIntoDatabaseCatchingExceptions(DatabaseContract.MyCoursesTable.TABLE_NAME, values);
                    }
                    catch (SQLiteConstraintException exc){
                        Log.i(TAG, "Corso gia' presente nel db");
                    }
                }
                application.databaseCall("courses_current.php?u=" + application.getUserId() + "&id=" + courseId + "&m=0", "addCourse", null);
                break;
            }
        }
    }

    private boolean inPassedExams(int courseId){
        String[] selectionArgs = {((Integer) courseId).toString()};
        Cursor cursor = application.queryDatabase(DatabaseContract.PassedExams.TABLE_NAME, null,
                DatabaseContract.PassedExams.COLUMN_COURSE_ID + " = ?", selectionArgs, null, null, null);
        if(cursor.getCount() > 0)
            return true;
        return false;
    }

}

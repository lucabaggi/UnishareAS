package it.android.unishare;

import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.gc.materialdesign.widgets.ProgressDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ProfileActivity extends SmartActivity implements MyCoursesFragment.OnCourseSelectedListener {

    public static final String TAG = "ProfileActivity";

    private static final String COURSE_TAG = "coursesDetail";
    private static final String OPINION_TAG = "courseOpinions";
    private static final String INSERT_OPINION_TAG = "insertOpinion";
    private static final String REFRESH_OPINIONS_ADAPTER = "refreshOpinion";

    private MyCoursesFragment myCoursesFragment;
    private OpinionsFragment opinionsFragment;
    private InsertOpinionFragment insertOpinionFragment;

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ArrayList<Entity> courses;
    private int numOfCourses;
    private String courseName;
    private int courseId;
    private CoursesAdapter coursesAdapter;
    private OpinionsAdapter opinionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

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
        coursesAdapter = new CoursesAdapter(this, new ArrayList<Entity>());

        getFragmentManager().beginTransaction().add(R.id.profile_fragment_container, new ProfileFragment(), ProfileFragment.TAG).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        application.setActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    @Override
    public void handleResult(ArrayList<Entity> result, String tag){
        if(tag == COURSE_TAG){
            Entity course = result.get(0);
            courses.add(course);
            if(courses.size() == numOfCourses){
                coursesAdapter.addAll(courses);
                Log.i("ProfileActivity", "adapter riempito con dimensione " + coursesAdapter.getCount());
                myCoursesFragment = new MyCoursesFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.profile_fragment_container, myCoursesFragment, MyCoursesFragment.TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
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

    }

    private void createOpinionFragment() {
        opinionsFragment = new OpinionsFragment(courseName);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_fragment_container, opinionsFragment, OpinionsFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void myCourses() {
        courses = new ArrayList<>();
        coursesAdapter.clear();
        String[] projection = {DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID};
        Cursor cursor = application.queryDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME, projection,
                null, null, null, null, null);
        numOfCourses = cursor.getCount();
        Log.i("ProfileActivity", "Trovati " + numOfCourses + " corsi nel db locale");
        while(cursor.moveToNext()){
            int courseId = cursor.getInt(0);
            getCourse(courseId);
        }
    }

    public CoursesAdapter getCoursesAdapter(){
        return this.coursesAdapter;
    }

    public OpinionsAdapter getOpinionsAdapter(){
        return this.opinionsAdapter;
    }

    public String getCourseName(){
        return this.courseName;
    }

    @Override
    public void onCourseSelected(String courseId, String courseName, ProgressDialog dialog) {
        this.courseName = courseName;
        this.courseId = Integer.parseInt(courseId);
        getOpinion(this.courseId, dialog);
    }

    public void createInsertOpinionFragment() {
        insertOpinionFragment = new InsertOpinionFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_fragment_container, insertOpinionFragment, InsertOpinionFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void insertOpinion(String opinion, float rating, ProgressDialog dialog) {
        Log.i(TAG, "Calling db for inserting opinion");
        Log.i(TAG, "Commento: " + opinion + "\nvoto: " + rating + " per il corso " + courseId);
        int userId = application.getUserId();
        insertOpinion(courseId, rating, opinion, userId, dialog);
    }


    //Calls to database

    private void getCourse(int courseId) {
        application.databaseCall("courses_detail.php?id=" + courseId, COURSE_TAG, null);
    }

    private void getOpinion(int courseId, com.gc.materialdesign.widgets.ProgressDialog dialog){
        application.databaseCall("opinions.php?id=" + courseId, OPINION_TAG, dialog);
    }

    private void insertOpinion(int courseId, float rating, String text, int cdsId, com.gc.materialdesign.widgets.ProgressDialog dialog){
        try {
            application.databaseCall("opinions_insert.php?id=" + courseId + "&v=" + rating + "&c=" + URLEncoder.encode(text, "UTF-8") + "&u=" + cdsId, INSERT_OPINION_TAG, dialog);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void refreshOpinions(int courseId){
        application.databaseCall("opinions.php?id=" + courseId, REFRESH_OPINIONS_ADAPTER, null);
    }

}

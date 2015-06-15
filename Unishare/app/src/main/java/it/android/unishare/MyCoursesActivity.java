package it.android.unishare;

import android.app.FragmentTransaction;
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

import com.gc.materialdesign.widgets.ProgressDialog;

import java.util.ArrayList;
import java.util.Iterator;


public class MyCoursesActivity extends CourseSupportActivity implements MyCoursesFragment.OnCourseSelectedListener, PassedExamsFragment.OnCourseSelectedListener {

    public static final String TAG = "MyCoursesActivity";

    private OpinionsFragment opinionsFragment;
    private InsertOpinionFragment insertOpinionFragment;

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String courseName;
    private int courseId;
    private CoursesAdapter coursesAdapter;
    private PassedCoursesAdapter passedCoursesAdapter;
    private OpinionsAdapter opinionsAdapter;

    private ArrayList<Entity> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_courses_activity);

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

        courses = new ArrayList<>();
        if(getIntent().getExtras().containsKey(ProfileActivity.ACTUAL_COURSES_TAG)){
            courses = getIntent().getParcelableArrayListExtra(ProfileActivity.ACTUAL_COURSES_TAG);
            coursesAdapter = new CoursesAdapter(this, new ArrayList<Entity>());
            coursesAdapter.addAll(courses);

            getFragmentManager().beginTransaction().add(R.id.my_courses_fragment_container,
                    new MyCoursesFragment(), MyCoursesFragment.TAG).commit();
        }
        else if(getIntent().getExtras().containsKey(ProfileActivity.PASSED_EXAMS_TAG)){
            courses = getIntent().getParcelableArrayListExtra(ProfileActivity.PASSED_EXAMS_TAG);
            passedCoursesAdapter = new PassedCoursesAdapter(this, new ArrayList<Entity>());
            passedCoursesAdapter.addAll(courses);

            getFragmentManager().beginTransaction().add(R.id.my_courses_fragment_container,
                    new PassedExamsFragment(), PassedExamsFragment.TAG).commit();
        }


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

    @Override
    public void handleResult(ArrayList<Entity> result, String tag){
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
        if(tag == DELETE_FROM_ACTUAL_TAG){
            Log.i(TAG, "Corso attuale eliminato dal db esterno");
        }
        if(tag == DELETE_FROM_PAST_TAG){
            Log.i(TAG, "Corso passato eliminato dal db esterno");
        }

    }

    private void createOpinionFragment() {
        opinionsFragment = new OpinionsFragment(courseName);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.my_courses_fragment_container, opinionsFragment, OpinionsFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void refreshActualCourses(String courseId){
        int id = Integer.parseInt(courseId);
        int userId = application.getUserId();
        deleteFromActualExams(userId, id);
        Iterator<Entity> it = courses.iterator();
        while(it.hasNext()){
            Entity e = it.next();
            if(e.get("id").equals(courseId))
                it.remove();
        }
        coursesAdapter.clear();
        coursesAdapter.addAll(courses);
        coursesAdapter.notifyDataSetChanged();
    }

    public void refreshPastCourses(String courseId){
        int id = Integer.parseInt(courseId);
        int userId = application.getUserId();
        deleteFromPastExams(userId, id);
        Iterator<Entity> it = courses.iterator();
        while(it.hasNext()){
            Entity e = it.next();
            if(e.get("id").equals(courseId))
                it.remove();
        }
        passedCoursesAdapter.clear();
        passedCoursesAdapter.addAll(courses);
        passedCoursesAdapter.notifyDataSetChanged();
    }

    public CoursesAdapter getAdapter(){
        return this.coursesAdapter;
    }

    public PassedCoursesAdapter getPassedCoursesAdapter(){ return this.passedCoursesAdapter; }

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
        transaction.replace(R.id.my_courses_fragment_container, insertOpinionFragment, InsertOpinionFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void insertOpinion(String opinion, float rating, ProgressDialog dialog) {
        Log.i(TAG, "Calling db for inserting opinion");
        Log.i(TAG, "Commento: " + opinion + "\nvoto: " + rating + " per il corso " + courseId);
        int userId = application.getUserId();
        insertOpinion(courseId, rating, opinion, userId, dialog);
    }


}

package it.android.unishare;

import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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


public class ProfileActivity extends SmartActivity {

    public static final String TAG = "ProfileActivity";
    public static final String ACTUAL_COURSES_TAG = "actualCourses";
    public static final String PASSED_EXAMS_TAG = "passedExams";

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ArrayList<Entity> courses;
    private ArrayList<Entity> passedExams;
    private int numOfCourses;
    private int numOfPassedExams;


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


    public void myCourses() {
        courses = new ArrayList<>();
        String[] projection = {DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID,
                DatabaseContract.MyCoursesTable.COLUMN_NAME,
                DatabaseContract.MyCoursesTable.COLUMN_PROFESSOR};
        Cursor cursor = application.queryDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME, projection,
                null, null, null, null, null);
        numOfCourses = cursor.getCount();
        Log.i("ProfileActivity", "Trovati " + numOfCourses + " corsi nel db locale");
        if(numOfCourses == 0){
            String title = "";
            String message = "Nessun corso presente, vai nella sezione Corsi e aggiungi i corsi che stai frequentando";
            application.alertMessage(title, message);
            return;
        }
        while(cursor.moveToNext()){
            Integer courseId = cursor.getInt(0);
            String name = cursor.getString(1);
            String professor = cursor.getString(2);
            Entity course = new Entity();
            course.addElement("id", courseId.toString());
            course.addElement("nome", name);
            course.addElement("professore", professor);
            courses.add(course);
        }
        for(Entity e : courses)
            Log.i(TAG, "{" + e.get("id") + " ," + e.get("nome") + " ," + e.get("professore") + "}\n");
        Intent intent = new Intent(this, MyCoursesActivity.class);
        intent.putExtra(ACTUAL_COURSES_TAG, courses);
        startActivity(intent);
    }

    public void passedExams(){
        passedExams = new ArrayList<>();
        String[] projection = {DatabaseContract.PassedExams.COLUMN_COURSE_ID,
                DatabaseContract.PassedExams.COLUMN_NAME,
                DatabaseContract.PassedExams.COLUMN_PROFESSOR,
                DatabaseContract.PassedExams.COLUMN_GRADE,
                DatabaseContract.PassedExams.COLUMN_LAUDE};
        Cursor cursor = application.queryDatabase(DatabaseContract.PassedExams.TABLE_NAME, projection,
                null, null, null, null, null);
        numOfPassedExams = cursor.getCount();
        Log.i("ProfileActivity", "Trovati " + numOfPassedExams + " esami superati nel db locale");
        if(numOfPassedExams == 0){
            String title = "";
            String message = "Nessun corso presente, vai nella sezione Corsi e aggiungi i corsi che hai superato";
            application.alertMessage(title, message);
            return;
        }
        while(cursor.moveToNext()){
            Integer courseId = cursor.getInt(0);
            String name = cursor.getString(1);
            String professor = cursor.getString(2);
            Integer grade = cursor.getInt(3);
            Integer lode = cursor.getInt(4);
            Entity course = new Entity();
            course.addElement("id", courseId.toString());
            course.addElement("nome", name);
            course.addElement("professore", professor);
            course.addElement("valutazione", grade.toString());
            course.addElement("lode", lode.toString());
            passedExams.add(course);
        }
        for(Entity e : passedExams)
            Log.i(TAG, "{" + e.get("id") + " ," + e.get("nome") + " ," + e.get("professore") + ", " + e.get("valutazione") + "}\n");
        Intent intent = new Intent(this, MyCoursesActivity.class);
        intent.putExtra(PASSED_EXAMS_TAG, passedExams);
        startActivity(intent);
    }

}

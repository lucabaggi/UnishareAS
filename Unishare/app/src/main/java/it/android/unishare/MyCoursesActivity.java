package it.android.unishare;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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

    private static final String MY_COURSES_FRAGMENT_INSTANCE = "my_courses_fragment_key";
    private static final String INSERT_OPINION_FRAGMENT_INSTANCE = "insert_opinion_fragment_key";
    private static final String ADAPTER_VALUES = "key_adapter";
    private static final String OPINION_ADAPTER_VALUES = "key_opinion_adapter";
    private static final String COURSE_NAME = "course_name_key";


    private OpinionsFragment opinionsFragment;
    private InsertOpinionFragment insertOpinionFragment;
    private MyCoursesFragment myCoursesFragment;

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String courseName;
    private int courseId;
    private CoursesAdapter coursesAdapter;
    private OpinionsAdapter opinionsAdapter;
    private int numOfCourses;

    private ArrayList<Entity> courses;

    ArrayList<Entity> adapterValues = new ArrayList<Entity>();
    ArrayList<Entity> opinionAdapterValues = new ArrayList<Entity>();

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
        coursesAdapter = new CoursesAdapter(this, new ArrayList<Entity>());

        if(savedInstanceState != null){
            myCoursesFragment = (MyCoursesFragment)getFragmentManager()
                    .getFragment(savedInstanceState, MY_COURSES_FRAGMENT_INSTANCE);
            Log.i(TAG, "Existing fragment");
            adapterValues = savedInstanceState.getParcelableArrayList(ADAPTER_VALUES);
            this.coursesAdapter.addAll(adapterValues);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.my_courses_fragment_container, myCoursesFragment, MyCoursesFragment.TAG);

            if(savedInstanceState.getString(COURSE_NAME) != null){
                this.courseName = savedInstanceState.getString(COURSE_NAME);
                opinionAdapterValues = savedInstanceState.getParcelableArrayList(OPINION_ADAPTER_VALUES);
                opinionsAdapter = new OpinionsAdapter(this, new ArrayList<Entity>());
                opinionsAdapter.addAll(opinionAdapterValues);
            }
            if(getFragmentManager().getFragment(savedInstanceState, INSERT_OPINION_FRAGMENT_INSTANCE) != null)
                insertOpinionFragment = (InsertOpinionFragment)getFragmentManager()
                        .getFragment(savedInstanceState, INSERT_OPINION_FRAGMENT_INSTANCE);
        }
        else{
            Log.i(TAG, "Fragment not exists, creating");
            myCourses();
        }

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
        getFragmentManager().putFragment(outState, MY_COURSES_FRAGMENT_INSTANCE, myCoursesFragment);
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
        if(tag == ACTUAL_COURSES_TAG){
            coursesAdapter.clear();
            coursesAdapter.addAll(result);
            coursesAdapter.notifyDataSetChanged();
            MyCoursesFragment f = (MyCoursesFragment)getFragmentManager().findFragmentByTag(MyCoursesFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
            updateLocalDb(result);
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

    public void refreshActualCourses() {
        int userId = application.getUserId();
        if(Utilities.checkNetworkState(this))
        getActualCourses(userId);
        else{
            String title = "Errore";
            String message = "Controlla la tua connessione a Internet e riprova";
            MyCoursesFragment f = (MyCoursesFragment)getFragmentManager().findFragmentByTag(MyCoursesFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
            application.alertMessage(title, message);
        }
    }


    public CoursesAdapter getAdapter(){
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

    public void myCourses(){
        courses = new ArrayList<>();
        String[] projection = {DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID,
                DatabaseContract.MyCoursesTable.COLUMN_NAME,
                DatabaseContract.MyCoursesTable.COLUMN_PROFESSOR};
        String orderBy = DatabaseContract.MyCoursesTable.COLUMN_NAME + " ASC";
        Cursor cursor = application.queryDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME, projection,
                null, null, null, null, orderBy);
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

        coursesAdapter = new CoursesAdapter(this, new ArrayList<Entity>());
        coursesAdapter.addAll(courses);

        myCoursesFragment = new MyCoursesFragment();
        getFragmentManager().beginTransaction().add(R.id.my_courses_fragment_container,
                myCoursesFragment, MyCoursesFragment.TAG).commit();
    }

    private void updateLocalDb(ArrayList<Entity> result){
        final ArrayList<Entity> courses = result;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for(Entity course : courses){
                    int id = Integer.parseInt(course.get("id"));
                    if(!application.existsInCurrentCourses(id)) {
                        String courseName = course.get("nome");
                        String professor = course.get("professore");
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContract.MyCoursesTable.COLUMN_NAME, courseName);
                        values.put(DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID, id);
                        values.put(DatabaseContract.MyCoursesTable.COLUMN_PROFESSOR, professor);
                        application.insertIntoDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME, values);
                    }
                }
            }
        });

    }



}

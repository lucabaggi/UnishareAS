package it.android.unishare;

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
import android.view.View;

import com.gc.materialdesign.widgets.ProgressDialog;

import java.util.ArrayList;
import java.util.Iterator;


public class MyCoursesActivity extends CourseSupportActivity implements MyCoursesFragment.OnCourseSelectedListener, PassedExamsFragment.OnCourseSelectedListener, CourseBooksFragment.OnBookSelectedListener {

    public static final String TAG = "MyCoursesActivity";

    private static final String REQUEST_BOOK_TAG = "request_book";
    private static final String BOOK_DETAILS_TAG = "book_details";
    private static final String ASSOCIATED_BOOKS_TAG = "associated_books";
    private static final String REFRESH_BOOKS_TAG = "refresh_books";

    private static final String MY_COURSES_FRAGMENT_INSTANCE = "my_courses_fragment_key";
    private static final String INSERT_OPINION_FRAGMENT_INSTANCE = "insert_opinion_fragment_key";
    private static final String FILES_FRAGMENT_INSTANCE = "files_fragment_key";
    private static final String ADAPTER_VALUES = "key_adapter";
    private static final String OPINION_ADAPTER_VALUES = "key_opinion_adapter";
    private static final String FILE_ADAPTER_VALUES = "key_file_adapter";
    private static final String BOOKS_ADAPTER_VALUES = "key_books_adapter";
    private static final String COURSE_NAME = "course_name_key";
    private static final String COURSE = "course_key";
    private static final String COURSE_ID_BOOKS = "course_id_books_key";
    private static final String COURSE_ID_FILES = "course_id_files_key";
    private static final String BOOKS_FRAGMENT_INSTANCE = "books_fragment_key";


    private OpinionsFragment opinionsFragment;
    private InsertOpinionFragment insertOpinionFragment;
    private MyCoursesFragment myCoursesFragment;
    private CourseFilesFragment courseFilesFragment;
    private CourseBooksFragment courseBooksFragment;

    private MyApplication application;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String courseName;
    private int courseId;
    private CoursesAdapter coursesAdapter;
    private OpinionsAdapter opinionsAdapter;
    private FilesAdapter filesAdapter;
    private BooksAdapter booksAdapter;
    private int numOfCourses;
    private Entity course;
    private Entity book;
    private int courseIdForBooks;
    private int courseIdForFiles;

    private ArrayList<Entity> courses;

    ArrayList<Entity> adapterValues = new ArrayList<Entity>();
    ArrayList<Entity> opinionAdapterValues = new ArrayList<Entity>();
    ArrayList<Entity> fileAdapterValues = new ArrayList<Entity>();
    ArrayList<Entity> booksAdapterValues = new ArrayList<Entity>();


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
            drawerToggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name){

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    MyApplication.hideKeyboard(MyCoursesActivity.this);
                }
            };;
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
                this.course = savedInstanceState.getParcelable(COURSE);
                opinionAdapterValues = savedInstanceState.getParcelableArrayList(OPINION_ADAPTER_VALUES);
                opinionsAdapter = new OpinionsAdapter(this, new ArrayList<Entity>());
                opinionsAdapter.addAll(opinionAdapterValues);
            }
            if(getFragmentManager().getFragment(savedInstanceState, INSERT_OPINION_FRAGMENT_INSTANCE) != null)
                insertOpinionFragment = (InsertOpinionFragment)getFragmentManager()
                        .getFragment(savedInstanceState, INSERT_OPINION_FRAGMENT_INSTANCE);
            if(getFragmentManager().getFragment(savedInstanceState, FILES_FRAGMENT_INSTANCE) != null){
                this.courseIdForFiles = savedInstanceState.getInt(COURSE_ID_FILES);
                fileAdapterValues = savedInstanceState.getParcelableArrayList(FILE_ADAPTER_VALUES);
                filesAdapter = new FilesAdapter(this, new ArrayList<Entity>());
                filesAdapter.addAll(fileAdapterValues);
                courseFilesFragment = (CourseFilesFragment) getFragmentManager()
                        .getFragment(savedInstanceState, FILES_FRAGMENT_INSTANCE);
            }
            if(savedInstanceState.getParcelableArrayList(BOOKS_ADAPTER_VALUES) != null){
                this.courseIdForBooks = savedInstanceState.getInt(COURSE_ID_BOOKS);
                booksAdapterValues = savedInstanceState.getParcelableArrayList(BOOKS_ADAPTER_VALUES);
                booksAdapter = new BooksAdapter(this, new ArrayList<Entity>());
                Log.i(TAG, "Adapter values size: " + booksAdapterValues.size());
                booksAdapter.addAll(booksAdapterValues);
            }
            if(getFragmentManager().getFragment(savedInstanceState, BOOKS_FRAGMENT_INSTANCE) != null){
                courseBooksFragment = (CourseBooksFragment) getFragmentManager()
                        .getFragment(savedInstanceState, BOOKS_FRAGMENT_INSTANCE);
            }

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
            outState.putParcelable(COURSE, course);
            ArrayList<Entity> opinions = new ArrayList<Entity>();
            if(opinionsAdapter != null){
                for(int i = 0; i < opinionsAdapter.getCount(); i++)
                    opinions.add(opinionsAdapter.getItem(i));
                outState.putParcelableArrayList(OPINION_ADAPTER_VALUES, opinions);
            }
        }

        if(filesAdapter != null){
            outState.putInt(COURSE_ID_FILES, courseIdForFiles);
            ArrayList<Entity> files = new ArrayList<>();
            for(int i = 0; i < filesAdapter.getCount(); i++)
                files.add(filesAdapter.getItem(i));
            outState.putParcelableArrayList(FILE_ADAPTER_VALUES, files);
        }

        if(booksAdapter != null){
            outState.putInt(COURSE_ID_BOOKS, courseIdForBooks);
            ArrayList<Entity> books = new ArrayList<>();
            for(int i = 0; i < booksAdapter.getCount(); i++)
                books.add(booksAdapter.getItem(i));
            Log.i(TAG, "books size: " + books.size());
            outState.putParcelableArrayList(BOOKS_ADAPTER_VALUES, books);
        }

        if(this.courseFilesFragment != null)
            if(this.courseFilesFragment.isVisible())
                getFragmentManager().putFragment(outState, FILES_FRAGMENT_INSTANCE, courseFilesFragment);

        if(this.courseBooksFragment != null)
            if(this.courseBooksFragment.isVisible())
                getFragmentManager().putFragment(outState, BOOKS_FRAGMENT_INSTANCE, courseBooksFragment);

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

    @Override
    public void handleError(String tag){
        if(tag == ERROR){
            String title = "Nessun risultato";
            String message = "Controlla la tua connessione o modifica la tua ricerca";
            getMyApplication().alertMessage(title, message);
        }

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
        if(tag == COURSE_FILES_TAG){
            if(result.isEmpty()){
                String title = "";
                String message = "Nessun file disponibile per questo corso";
                application.alertMessage(title, message);
                return;
            }
            filesAdapter = new FilesAdapter(this, new ArrayList<Entity>());
            filesAdapter.addAll(result);
            courseFilesFragment = new CourseFilesFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.my_courses_fragment_container, courseFilesFragment, CourseFilesFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if(tag == ASSOCIATED_BOOKS_TAG){
            booksAdapter = new BooksAdapter(this, new ArrayList<Entity>());
            if(result.isEmpty()){
                String title = "";
                String message = "Nessun libro associato al corso";
                application.alertMessage(title, message);
                return;
            }
            booksAdapter.addAll(result);
            courseBooksFragment = new CourseBooksFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.my_courses_fragment_container, courseBooksFragment, CourseBooksFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if(tag == REQUEST_BOOK_TAG){
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
        if(tag == BOOK_DETAILS_TAG){
            this.book = result.get(0);
            Log.i(TAG, "BookId = " + book.get("id"));
            BooksDetailsFragment booksDetailsFragment = new BooksDetailsFragment(book);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.my_courses_fragment_container, booksDetailsFragment, BooksDetailsFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if(tag == REFRESH_BOOKS_TAG){
            booksAdapter.clear();
            booksAdapter.addAll(result);
            booksAdapter.notifyDataSetChanged();
            CourseBooksFragment f = (CourseBooksFragment) getFragmentManager()
                    .findFragmentByTag(CourseBooksFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
        }
        if(tag == REFRESH_FILES_TAG){
            filesAdapter.clear();
            filesAdapter.addAll(result);
            filesAdapter.notifyDataSetChanged();
            CourseFilesFragment f = (CourseFilesFragment) getFragmentManager()
                    .findFragmentByTag(CourseFilesFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
        }
    }

    private void createOpinionFragment() {
        opinionsFragment = new OpinionsFragment(courseName, course);
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

    public FilesAdapter getFilesAdapter(){
        return this.filesAdapter;
    }

    public BooksAdapter getBooksAdapter(){
        return this.booksAdapter;
    }

    public String getCourseName(){
        return this.courseName;
    }

    @Override
    public void onCourseSelected(String courseId, String courseName, Entity course, ProgressDialog dialog) {
        this.courseName = courseName;
        this.courseId = Integer.parseInt(courseId);
        this.course = course;
        getOpinion(this.courseId, dialog);
    }

    @Override
    public void onBookSelected(String bookId, ProgressDialog dialog) {
        int id = Integer.parseInt(bookId);
        getBook(id, dialog);
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

    public void addToPassedExams(int courseId, int grade, int lode){
        int userId = application.getUserId();
        addToPassedExams(userId, courseId, grade, lode);
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
        }
        else{
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
        }
        coursesAdapter = new CoursesAdapter(this, new ArrayList<Entity>());
        coursesAdapter.addAll(courses);

        myCoursesFragment = new MyCoursesFragment();
        getFragmentManager().beginTransaction().add(R.id.my_courses_fragment_container,
                myCoursesFragment, MyCoursesFragment.TAG).commit();
    }

    public void getFiles(int courseId, ProgressDialog dialog){
        this.courseIdForFiles = courseId;
        getCourseFiles(courseId, dialog);
    }

    public void getAssociatedBooks(int courseId, ProgressDialog dialog){
        this.courseIdForBooks = courseId;
        getBooks(courseId, dialog);
    }

    public Entity getSelectedCourse(){
        return this.course;
    }

    private void updateLocalDb(ArrayList<Entity> result){
        final ArrayList<Entity> courses = result;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for (Entity course : courses) {
                    int id = Integer.parseInt(course.get("id"));
                    if (!application.existsInCurrentCourses(id)) {
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

    public void requestBook(int bookId) {
        int userId = application.getUserId();
        callRequestBook(userId, bookId);
    }

    public void refreshBooks(){
        if(Utilities.checkNetworkState(this))
            refreshBooks(courseIdForBooks);
        else{
            String title = "Errore";
            String message = "Controlla la tua connessione a Internet e riprova";
            CourseBooksFragment f = (CourseBooksFragment)getFragmentManager()
                    .findFragmentByTag(CourseBooksFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
            application.alertMessage(title, message);
        }
    }

    public void refreshFiles(){
        if(Utilities.checkNetworkState(this))
            refreshFiles(courseIdForFiles);
        else{
            String title = "Errore";
            String message = "Controlla la tua connessione a Internet e riprova";
            CourseFilesFragment f = (CourseFilesFragment)getFragmentManager()
                    .findFragmentByTag(CourseFilesFragment.TAG);
            f.getSwipeRefreshLayout().setRefreshing(false);
            application.alertMessage(title, message);
        }
    }

    //Calls to DB

    private void getBooks(int courseId, ProgressDialog dialog){
        application.databaseCall("books.php?c=" + courseId, ASSOCIATED_BOOKS_TAG, dialog);
    }

    private void callRequestBook(int userId, int bookId) {
        application.databaseCall("books_request.php?id=" + bookId + "&u=" + userId, REQUEST_BOOK_TAG, null);
    }

    private void getBook(int bookId, com.gc.materialdesign.widgets.ProgressDialog dialog) {
        application.databaseCall("books_detail.php?id=" + bookId, BOOK_DETAILS_TAG, dialog);
    }

    private void refreshBooks(int courseId){
        application.databaseCall("books.php?c=" + courseId, REFRESH_BOOKS_TAG, null);
    }
}

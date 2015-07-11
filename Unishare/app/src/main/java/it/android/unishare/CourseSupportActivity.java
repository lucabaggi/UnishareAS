package it.android.unishare;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.gc.materialdesign.widgets.ProgressDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class CourseSupportActivity extends AdapterActivity {

    public static final String COURSE_TAG = "coursesDetail";
    public static final String OPINION_TAG = "courseOpinions";
    public static final String INSERT_OPINION_TAG = "insertOpinion";
    public static final String REFRESH_OPINIONS_ADAPTER = "refreshOpinion";
    public static final String INSERT_COURSE_TAG = "insertCourse";
    public static final String ERROR = "error";
    public static final String COURSE_SEARCH_TAG = "courseSearch";
    public static final String ADD_TO_ACTUAL_TAG = "addToActual";
    public static final String ADD_TO_PAST_TAG = "addToPast";
    public static final String DELETE_FROM_ACTUAL_TAG = "deleteFromActual";
    public static final String DELETE_FROM_PAST_TAG = "deleteFromPast";
    public static final String ACTUAL_COURSES_TAG = "actualCourses";
    public static final String PAST_COURSES_TAG = "passedExams";
    public static final String COURSE_FILES_TAG = "courseFiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    ArrayAdapter<Entity> getAdapter() {
        return null;
    }

    @Override
    void initializeFragmentUI(String string, ProgressDialog dialog) {

    }

    public OpinionsAdapter getOpinionsAdapter(){
        return null;
    }

    public String getCourseName(){
        return null;
    }

    public Entity getSelectedCourse(){
        return null;
    }

    public void createInsertOpinionFragment(){}

    public void insertOpinion(String opinion, float rating, ProgressDialog dialog) {}


    //Database calls

    public void searchCourses(int campusId, String text, com.gc.materialdesign.widgets.ProgressDialog dialog) {
        try {
            getMyApplication().databaseCall("courses_search.php?q=" + URLEncoder.encode(text.trim(), "UTF-8") + "&s=" + campusId, COURSE_SEARCH_TAG, dialog);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getCourse(int courseId) {
        getMyApplication().databaseCall("courses_detail.php?id=" + courseId, COURSE_TAG, null);
    }

    public void getOpinion(int courseId, com.gc.materialdesign.widgets.ProgressDialog dialog){
        getMyApplication().databaseCall("opinions.php?id=" + courseId, OPINION_TAG, dialog);
    }

    public void insertOpinion(int courseId, float rating, String text, int cdsId, com.gc.materialdesign.widgets.ProgressDialog dialog){
        try {
            getMyApplication().databaseCall("opinions_insert.php?id=" + courseId + "&v=" + rating + "&c=" + URLEncoder.encode(text, "UTF-8") + "&u=" + cdsId, INSERT_OPINION_TAG, dialog);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void refreshOpinions(int courseId){
        getMyApplication().databaseCall("opinions.php?id=" + courseId, REFRESH_OPINIONS_ADAPTER, null);
    }

    public void insertCourse(int userId, String courseName, String prof, String language, float cfu, int index, ProgressDialog dialog) {
        try {
            getMyApplication().databaseCall("courses_insert.php?u=" + userId + "&n=" + URLEncoder.encode(courseName, "UTF-8") +
                    "&p=" + URLEncoder.encode(prof, "UTF-8") + "&t=" + index + "&c=" + cfu + "&l=" + URLEncoder.encode(language, "UTF-8"), INSERT_COURSE_TAG, dialog);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void addToActualExams(int userId, int courseId){
        getMyApplication().databaseCall("courses_current.php?u=" + userId + "&id=" + courseId + "&m=0", ADD_TO_ACTUAL_TAG, null);
    }

    public void addToPassedExams(int userId, int courseId, int grade, int lode){
        getMyApplication().databaseCall("courses_past.php?u=" + userId + "&id=" + courseId + "&m=0" + "&v=" + grade + "&l=" + lode, ADD_TO_PAST_TAG, null);
    }

    public void deleteFromActualExams(int userId, int courseId){
        getMyApplication().databaseCall("courses_current.php?u=" + userId + "&id=" + courseId + "&m=1", DELETE_FROM_ACTUAL_TAG, null);
    }

    public void deleteFromPastExams(int userId, int courseId){
        getMyApplication().databaseCall("courses_past.php?u=" + userId + "&id=" + courseId + "&m=1", DELETE_FROM_PAST_TAG, null);
    }

    public void getActualCourses(int userId){
        getMyApplication().databaseCall("courses_current.php?u=" + userId, ACTUAL_COURSES_TAG, null);
    }

    public void getPastCourses(int userId){
        getMyApplication().databaseCall("courses_past.php?u=" + userId, PAST_COURSES_TAG, null);

    }

    public void getCourseFiles(int courseId, ProgressDialog dialog){
        getMyApplication().databaseCall("files.php?c=" + courseId, COURSE_FILES_TAG, dialog);
    }
}

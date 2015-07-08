package it.android.unishare;

import android.content.ContentValues;
import android.os.AsyncTask;


import java.util.ArrayList;

/**
 * Created by luca on 21/06/15.
 */
public class SyncUserCoursesTask extends AsyncTask<Integer, Void, Void> {

    private MyApplication application;

    public SyncUserCoursesTask(MyApplication application){
        this.application = application;
    }

    @Override
    protected Void doInBackground(Integer... params) {

        int userId = params[0];
        application.deleteTable(DatabaseContract.MyCoursesTable.TABLE_NAME);
        application.deleteTable(DatabaseContract.PassedExams.TABLE_NAME);
        ArrayList<Entity> actualCourses = Utilities.queryDatabase(application.getActivity(),
                "http://www.unishare.it/api/courses_current.php?u=" + userId);
        ArrayList<Entity> pastCourses = Utilities.queryDatabase(application.getActivity(),
                "http://www.unishare.it/api/courses_past.php?u=" + userId);
        fillMyCoursesTable(actualCourses);
        fillPassedCoursesTable(pastCourses);
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if(application.getActivity() instanceof SplashActivity)
        {
            //((SplashActivity)application.getActivity()).launchNextActivity();
        }

    }

    private void fillMyCoursesTable(ArrayList<Entity> courses){
        for(Entity course : courses){
            String courseName = course.get("nome");
            String professor = course.get("professore");
            int courseId = Integer.parseInt(course.get("id"));
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.MyCoursesTable.COLUMN_NAME, courseName);
            values.put(DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID, courseId);
            values.put(DatabaseContract.MyCoursesTable.COLUMN_PROFESSOR, professor);
            application.insertIntoDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME,
                    values);
        }

    }

    private void fillPassedCoursesTable(ArrayList<Entity> courses){
        for(Entity course : courses){
            String courseName = course.get("nome");
            String professor = course.get("professore");
            int courseId = Integer.parseInt(course.get("id"));
            int grade = Integer.parseInt(course.get("valutazione"));
            int lode = Integer.parseInt(course.get("lode"));
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.PassedExams.COLUMN_NAME, courseName);
            values.put(DatabaseContract.PassedExams.COLUMN_COURSE_ID, courseId);
            values.put(DatabaseContract.PassedExams.COLUMN_PROFESSOR, professor);
            values.put(DatabaseContract.PassedExams.COLUMN_GRADE, grade);
            values.put(DatabaseContract.PassedExams.COLUMN_LAUDE, lode);
            application.insertIntoDatabase(DatabaseContract.PassedExams.TABLE_NAME,
                    values);
        }
    }

}


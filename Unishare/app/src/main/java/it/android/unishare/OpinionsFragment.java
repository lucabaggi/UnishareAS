package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;

public class OpinionsFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "OpinionsFragment";
	
	private String courseName;
    private Entity course;

	private View view;
	private ListView listview;
	
	private CourseSupportActivity activity;
	private OpinionsAdapter opinionsAdapter;

    private int counter;
	
	public OpinionsFragment(){
		
	}
	
	public OpinionsFragment (String courseName, Entity course){
		this.counter = 0;
        this.courseName = courseName;
        this.course = course;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null)
			view = inflater.inflate(R.layout.opinions_fragment, container, false);
        initializeUI(view);
        return view;
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof CoursesActivity){
            this.activity = (CoursesActivity) activity;
            Log.i(TAG, "fragment launched by CoursesActivity");
        }
        else if(activity instanceof MyCoursesActivity){
            this.activity = (MyCoursesActivity) activity;
            Log.i(TAG, "fragment launched by MyCoursesActivity");
        }
        else{
            this.activity = (PassedCoursesActivity) activity;
            Log.i(TAG, "fragment launched by PassedCoursesActivity");
        }
    }
    

	@Override
	public void initializeUI(View view) {
        opinionsAdapter = activity.getOpinionsAdapter();
        Log.i(TAG, "opinionsFragment per il corso " + courseName);
        TextView courseNameTextView = (TextView) view.findViewById(R.id.courseName);
        if (this.courseName == null) {
            this.courseName = activity.getCourseName();
            this.course = activity.getSelectedCourse();
        }
        courseNameTextView.setText(courseName);
        Log.i(TAG, "TextView value = " + courseNameTextView.getText().toString());

        ButtonFloat btn = (ButtonFloat) view.findViewById(R.id.buttonFloat);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.createInsertOpinionFragment();
            }
        });

        ButtonFloat btn2 = (ButtonFloat) view.findViewById(R.id.buttonFloat2);
        if(activity instanceof MyCoursesActivity || activity instanceof PassedCoursesActivity)
            btn2.setVisibility(View.INVISIBLE);
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Aggiungi ai tuoi corsi", "Aggiungi ai corsi superati"};
                final int courseId = Integer.parseInt(course.get("id"));
                final String professor = course.get("professore");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(course.get("nome"))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        Log.i("ContextMenu", "Item addFav was chosen");
                                        if(!inPassedExams(courseId)){
                                            ContentValues values = new ContentValues();
                                            values.put(DatabaseContract.MyCoursesTable.COLUMN_NAME, courseName);
                                            values.put(DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID, courseId);
                                            values.put(DatabaseContract.MyCoursesTable.COLUMN_PROFESSOR, professor);
                                            try{
                                                activity.getMyApplication().insertIntoDatabaseCatchingExceptions(DatabaseContract.MyCoursesTable.TABLE_NAME, values);
                                                activity.getMyApplication().toastMessage(activity, courseName + " è stato aggiunto ai Preferiti");
                                                ((CoursesActivity)activity).addToActualExams(courseId);
                                            }
                                            catch (SQLiteConstraintException e){
                                                Log.i(TAG, "Corso già presente nel db");
                                                activity.getMyApplication().alertMessage("", "Corso già presente fra i Preferiti");
                                            }
                                        }
                                        else
                                            activity.getMyApplication().toastMessage(activity, "Corso già presente tra gli esami sostenuti");
                                        break;
                                    case 1:
                                        Log.i("ContextMenu", "Item addPassed was chosen");
                                        final EditText input = new EditText(getActivity());
                                        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                        final CheckBox checkbox = new CheckBox(getActivity());
                                        checkbox.setText("Lode");
                                        String title = "";
                                        String message ="Con quale voto hai superato il corso?";
                                        DialogInterface.OnClickListener actionTrue = new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String gradeString = input.getText().toString().trim();
                                                boolean isChecked = checkbox.isChecked();
                                                if(isValid(gradeString, isChecked, courseId)){
                                                    int grade = Integer.parseInt(input.getText().toString());
                                                    int lode = 0;
                                                    ContentValues values = new ContentValues();
                                                    values.put(DatabaseContract.PassedExams.COLUMN_NAME, courseName);
                                                    values.put(DatabaseContract.PassedExams.COLUMN_COURSE_ID, courseId);
                                                    values.put(DatabaseContract.PassedExams.COLUMN_PROFESSOR, professor);
                                                    values.put(DatabaseContract.PassedExams.COLUMN_GRADE, grade);
                                                    if(isChecked){
                                                        values.put(DatabaseContract.PassedExams.COLUMN_LAUDE, 1);
                                                        lode = 1;
                                                    }
                                                    else
                                                        values.put(DatabaseContract.PassedExams.COLUMN_LAUDE, 0);
                                                    try{
                                                        activity.getMyApplication().insertIntoDatabaseCatchingExceptions(DatabaseContract.PassedExams.TABLE_NAME, values);
                                                        activity.getMyApplication().toastMessage(activity, courseName + " è stato aggiunto ai Corsi superati");
                                                        ((CoursesActivity)activity).addToPassedExams(courseId, grade, lode);
                                                    }
                                                    catch (SQLiteConstraintException e){
                                                        Log.i(TAG, "Corso già presente nel db");
                                                        activity.getMyApplication().alertMessage("", "Corso già presente fra gli esami superati");
                                                    }
                                                }
                                                else
                                                    activity.getMyApplication().toastMessage(activity, "Verifica che i dati inseriti siano corretti");
                                            }
                                        };
                                        DialogInterface.OnClickListener actionFalse = new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();

                                            }
                                        };
                                        activity.getMyApplication().alertDecision(title, message, input, checkbox, actionTrue, actionFalse);
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        listview = (ListView) view.findViewById(R.id.opinionsListView);
        listview.setAdapter(opinionsAdapter);
        if (opinionsAdapter.getCount() == 0 && counter == 0) {
            counter++;
            Log.i(TAG, "No opinions for this course");
            String title = "";
            String message = "Nessuna opinione presente per questo corso";
            activity.getMyApplication().alertMessage(title, message);
        }
    }

    private boolean inPassedExams(int courseId){
        String[] selectionArgs = {((Integer)courseId).toString()};
        Cursor cursor = activity.getMyApplication().queryDatabase(DatabaseContract.PassedExams.TABLE_NAME, null,
                DatabaseContract.PassedExams.COLUMN_COURSE_ID + " = ?", selectionArgs, null, null, null);
        if(cursor.getCount() > 0)
            return true;
        return false;
    }

    private boolean isValid(String gradeString, boolean isChecked, int courseId){
        if(isInteger(gradeString)){
            int grade = Integer.parseInt(gradeString);
            if(grade >= 18 && grade <= 30 && ((!isChecked) || (grade == 30))){
                if(coursePresentInCurrent(courseId))
                    deleteFromCurrent(courseId);
                return true;
            }
        }
        return false;
    }

    private boolean isInteger(String s){
        if(s == null || s.length()==0)
            return false;
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c <= '/' || c >= ':')
                return false;
        }
        return true;
    }

    private boolean coursePresentInCurrent(int courseId){
        String[] selectionArgs = {((Integer)courseId).toString()};
        Cursor cursor = activity.getMyApplication().queryDatabase(DatabaseContract.MyCoursesTable.TABLE_NAME, null,
                DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", selectionArgs, null, null, null);
        if(cursor.getCount() > 0)
            return true;
        return false;
    }

    private void deleteFromCurrent(int courseId){
        String whereArgs[] = {((Integer)courseId).toString()};
        activity.getMyApplication().deleteFromTable(DatabaseContract.MyCoursesTable.TABLE_NAME,
                DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", whereArgs);
    }
	
	

}

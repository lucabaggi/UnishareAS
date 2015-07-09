package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.ProgressDialog;

public class MyCoursesFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "MyCoursesFragment";

	private View view;
	private ListView listview;
    private TextView header;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    private OnCourseSelectedListener courseListener;

	private MyCoursesActivity activity;
	private CoursesAdapter coursesAdapter;

	public MyCoursesFragment(){

	}

	public interface OnCourseSelectedListener {
		public void onCourseSelected(String courseId, String courseName, com.gc.materialdesign.widgets.ProgressDialog dialog);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null)
			view = inflater.inflate(R.layout.my_courses_fragment, container, false);
        initializeUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            registerForContextMenu(listview);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        Entity course;
        course = activity.getAdapter().getItem(info.position);
        String courseName = course.get("nome");
        menu.setHeaderTitle(courseName);
        menu.add(Menu.NONE, R.id.delete_item, Menu.NONE, "Ho abbandonato questo corso");
        menu.add(Menu.NONE, R.id.passed_course, Menu.NONE, "Ho superato questo corso");
        menu.add(Menu.NONE, R.id.opinions, Menu.NONE, "Opinioni sul corso");
        menu.add(Menu.NONE, R.id.files, Menu.NONE, "Appunti disponibili");
        menu.add(Menu.NONE, R.id.associated_books, Menu.NONE, "Libri associati al corso");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //getting course info
        Entity course;
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        course = activity.getAdapter().getItem(info.position);
        final String courseId = course.get("id");
        final String courseName = course.get("nome");
        final String professor = course.get("professore");
        switch (item.getItemId()) {
            case R.id.delete_item:
                String whereArgs[] = {courseId};
                activity.getMyApplication().deleteFromTable(DatabaseContract.MyCoursesTable.TABLE_NAME,
                        DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", whereArgs);
                activity.refreshActualCourses(courseId);
                break;
            case R.id.passed_course:
                Log.i("ContextMenu", "Item addPassed was chosen");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                final CheckBox checkbox = new CheckBox(getActivity());
                checkbox.setText("Lode");
                String title = "";
                String message = "Con quale voto hai superato il corso?";
                DialogInterface.OnClickListener actionTrue = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gradeString = input.getText().toString().trim();
                        boolean isChecked = checkbox.isChecked();
                        if(isValid(gradeString, isChecked, Integer.parseInt(courseId))){
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
                                activity.addToPassedExams(Integer.parseInt(courseId), grade, lode);
                                activity.refreshActualCourses();
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
                return true;
            case R.id.opinions:
                if (!Utilities.checkNetworkState(activity)) {
                    String titleNet = "Errore";
                    String messageNet = "Verifica la tua connessione a Internet e riprova";
                    activity.getMyApplication().alertMessage(titleNet, messageNet);
                    break;
                }
                Log.i(TAG, "Clicked on course " + courseId);
                String titleSearch = "Searching";
                dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), titleSearch);
                MyCoursesFragment.this.courseListener.onCourseSelected(courseId, courseName, dialog);
                break;
            case R.id.files:
                if (!Utilities.checkNetworkState(activity)) {
                    String titleNet = "Errore";
                    String messageNet = "Verifica la tua connessione a Internet e riprova";
                    activity.getMyApplication().alertMessage(titleNet, messageNet);
                    break;
                }
                String titleSearching = "Searching";
                dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), titleSearching);
                activity.getFiles(Integer.parseInt(courseId), dialog);
                break;
            case R.id.associated_books:
                if (!Utilities.checkNetworkState(activity)) {
                    String titleNet = "Errore";
                    String messageNet = "Verifica la tua connessione a Internet e riprova";
                    activity.getMyApplication().alertMessage(titleNet, messageNet);
                    break;
                }
                String t = "Searching";
                dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), t);
                activity.getAssociatedBooks(Integer.parseInt(courseId), dialog);
        }
        return super.onContextItemSelected(item);

    }

    private boolean isValid(String gradeString, boolean isChecked, int courseId){
        if(isInteger(gradeString)){
            int grade = Integer.parseInt(gradeString);
            if(grade >= 18 && grade <= 30 && ((!isChecked) || (grade == 30))){
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

    private void deleteFromCurrent(int courseId){
        String whereArgs[] = {((Integer)courseId).toString()};
        activity.getMyApplication().deleteFromTable(DatabaseContract.MyCoursesTable.TABLE_NAME,
                DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", whereArgs);
    }


    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MyCoursesActivity) activity;
        try{
            this.courseListener = (OnCourseSelectedListener) activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnCourseSelectedListener");
        }
    }
    

	@Override
	public void initializeUI(View view) {
        listview = (ListView) view.findViewById(R.id.myCoursesListView);
        header = (TextView) view.findViewById(R.id.myCoursesTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.my_courses_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.Green, R.color.Orange, R.color.Blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activity.refreshActualCourses();
            }
        });
        header.setText("Corsi Attuali");
        coursesAdapter = activity.getAdapter();
        listview.setAdapter(coursesAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listview.showContextMenuForChild(view);
            }
        });
	}

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return this.swipeRefreshLayout;
    }
	
	

}

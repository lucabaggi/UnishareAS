package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private OnCourseSelectedListener courseListener;

	private MyCoursesActivity activity;
	private CoursesAdapter coursesAdapter;
    private PassedCoursesAdapter passedCoursesAdapter;

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
        if(activity.getAdapter() != null)
            course = activity.getAdapter().getItem(info.position);
        else
            course = activity.getPassedCoursesAdapter().getItem(info.position);
        String courseName = course.get("nome");
        menu.setHeaderTitle(courseName);
        menu.add(Menu.NONE, R.id.delete_item, Menu.NONE, "Elimina corso");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //getting course info
        Entity course;
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(activity.getAdapter() != null)
            course = activity.getAdapter().getItem(info.position);
        else
            course = activity.getPassedCoursesAdapter().getItem(info.position);
        final String courseId = course.get("id");

        switch (item.getItemId()) {
            case R.id.delete_item:
                String whereArgs[] = {courseId};
                if(activity.getAdapter() != null) {
                    activity.getMyApplication().deleteFromTable(DatabaseContract.MyCoursesTable.TABLE_NAME,
                        DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", whereArgs);
                    activity.refreshActualCourses(courseId);
                }
                else{
                    activity.getMyApplication().deleteFromTable(DatabaseContract.PassedExams.TABLE_NAME,
                            DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", whereArgs);
                    activity.refreshPastCourses(courseId);
                }

        }
        return super.onContextItemSelected(item);

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
        if(activity.getAdapter() != null){
            header.setText("Corsi Attuali");
            coursesAdapter = activity.getAdapter();
            listview.setAdapter(coursesAdapter);
        }
        else{
            header.setText("Corsi Superati");
            passedCoursesAdapter = activity.getPassedCoursesAdapter();
            listview.setAdapter(passedCoursesAdapter);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Utilities.checkNetworkState(activity)) {
                    String title = "Errore";
                    String message = "Verifica la tua connessione a Internet e riprova";
                    activity.getMyApplication().alertMessage(title, message);
                    return;
                }
                Entity course = (Entity) parent.getItemAtPosition(position);
                String courseId = course.get("id");
                String courseName = course.get("nome");
                Log.i(TAG, "Clicked on course " + courseId);
                String title = "Searching";
                dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
                MyCoursesFragment.this.courseListener.onCourseSelected(courseId, courseName, dialog);
            }
        });
	}
	
	

}

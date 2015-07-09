package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;

public class PassedExamsFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "PassedExamsFragment";

	private View view;
	private ListView listview;
    private TextView header;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    private OnCourseSelectedListener courseListener;

	private PassedCoursesActivity activity;
    private PassedCoursesAdapter passedCoursesAdapter;

	public PassedExamsFragment(){

	}

	public interface OnCourseSelectedListener {
		public void onCourseSelected(String courseId, String courseName, ProgressDialog dialog);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null)
			view = inflater.inflate(R.layout.passed_exams_fragment, container, false);
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
        menu.add(Menu.NONE, R.id.opinions, Menu.NONE, "Opinioni sul corso");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //getting course info
        Entity course;
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        course = activity.getPassedCoursesAdapter().getItem(info.position);
        final String courseId = course.get("id");
        final String courseName = course.get("nome");

        switch (item.getItemId()) {
            case R.id.delete_item:
                String whereArgs[] = {courseId};
                activity.getMyApplication().deleteFromTable(DatabaseContract.PassedExams.TABLE_NAME,
                        DatabaseContract.MyCoursesTable.COLUMN_COURSE_ID + " = ?", whereArgs);
                activity.refreshPastCourses(courseId);
                break;
            case R.id.opinions:
                if (!Utilities.checkNetworkState(activity)) {
                    String title = "Errore";
                    String message = "Verifica la tua connessione a Internet e riprova";
                    activity.getMyApplication().alertMessage(title, message);
                    break;
                }
                Log.i(TAG, "Clicked on course " + courseId);
                String title = "Searching";
                dialog = new ProgressDialog(getActivity(), title);
                PassedExamsFragment.this.courseListener.onCourseSelected(courseId, courseName, dialog);
                break;
        }
        return super.onContextItemSelected(item);

    }


    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (PassedCoursesActivity) activity;
        try{
            this.courseListener = (OnCourseSelectedListener) activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnCourseSelectedListener");
        }
    }


	@Override
	public void initializeUI(View view) {
        listview = (ListView) view.findViewById(R.id.passedExamsListView);
        header = (TextView) view.findViewById(R.id.passedExamsTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.passed_courses_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.Green, R.color.Orange, R.color.Blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activity.refreshPastCourses();
            }
        });
        passedCoursesAdapter = activity.getPassedCoursesAdapter();
        listview.setAdapter(passedCoursesAdapter);
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

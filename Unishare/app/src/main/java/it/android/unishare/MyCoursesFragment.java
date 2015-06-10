package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    private ProgressDialog dialog;

    private OnCourseSelectedListener courseListener;

	private ProfileActivity activity;
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (ProfileActivity) activity;
        try{
            this.courseListener = (OnCourseSelectedListener) activity;
        }
        catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnCourseSelectedListener");
        }
    }
    

	@Override
	public void initializeUI(View view) {
		coursesAdapter = activity.getCoursesAdapter();
		listview = (ListView) view.findViewById(R.id.myCoursesListView);
    	listview.setAdapter(coursesAdapter);
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

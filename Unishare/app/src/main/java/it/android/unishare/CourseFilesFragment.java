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

public class CourseFilesFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "CourseFIlesFragment";

    private MyCoursesActivity activity;

	private View view;
	private ListView listview;


    private FilesAdapter filesAdapter;

	public CourseFilesFragment(){

	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null)
			view = inflater.inflate(R.layout.course_files_fragment, container, false);
        initializeUI(view);
        return view;
    }


    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MyCoursesActivity) activity;
    }


	@Override
	public void initializeUI(View view) {
        listview = (ListView) view.findViewById(R.id.courseFilesListView);
        filesAdapter = activity.getFilesAdapter();
        listview.setAdapter(filesAdapter);
	}

	
	

}

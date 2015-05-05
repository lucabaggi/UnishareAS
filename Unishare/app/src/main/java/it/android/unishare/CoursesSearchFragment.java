package it.android.unishare;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CoursesSearchFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "CoursesSearchFragment";
	
	CoursesAdapter adapter;
	
	CoursesActivity activity;
	View view;
	
	EditText searchForm;
	ListView listview;
	com.gc.materialdesign.widgets.ProgressDialog dialog;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(view == null){
    		view = inflater.inflate(R.layout.courses_search_fragment, container, false);
            initializeUI(view);
    	}
    	return view;       
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (CoursesActivity) activity;
		if(activity instanceof CoursesActivity)
			Log.i(TAG, "Activity is CourseActivity");
		/*
		try {
            this.courseListener = (OnCourseSelectedListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCourseSelectedListener");
        }
        */
    }
	@Override
	public void initializeUI(View view) {
		adapter = activity.getAdapter();
    	listview = (ListView) view.findViewById(R.id.ListView1);
    	if(adapter.getCount() > 0){
    		listview.setAdapter(adapter);
    		
    		listview.setOnItemClickListener(new OnItemClickListener(){

    			@Override
    			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
    				Entity course = (Entity)parent.getItemAtPosition(position);
    				Log.i(TAG, "Clicked on " + course.get("id"));
    				//CoursesSearchFragment.this.courseListener.onCourseSelected(course);			
    			}
    				
    		});
    	}    		
    	searchForm = (EditText) view.findViewById(R.id.opinionText);
    	Button btn = (Button) view.findViewById(R.id.insertOpinionButton);
        btn.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View view) {
                String title = "Searching";
                dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
	        	activity.initializeFragmentUI(searchForm.getText().toString(), dialog);
	        }
        });	
	}
	
	public void displayResults(ArrayList<Entity> result, String tag) {
		clearList(adapter);
		adapter = activity.getAdapter();
		fillList(result);
		activity.getMyApplication().alertMessage("Ricerca di '" + searchForm.getText().toString() + "'", (result.size()) + " risultati trovati");
	}


	private void fillList(ArrayList<Entity> result) {
		adapter.addAll(result);	
		listview.setAdapter(adapter);
    		
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				Entity book = (Entity)parent.getItemAtPosition(position);
				Log.i(TAG, "Clicked on " + book.get("id"));
				//CoursesSearchFragment.this.courseListener.onCourseSelected(course);				
			}
				
		});
		
	}


	private void clearList(CoursesAdapter adapter) {
		adapter.clear();
		adapter.notifyDataSetChanged();		
	}

}

package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;

public class OpinionsFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "OpinionsFragment";
	
	private String courseName, professorName;
	private View view;
	private ListView listview;
	
	private CourseSupportActivity activity;
	private OpinionsAdapter opinionsAdapter;

    private int counter;
	
	public OpinionsFragment(){
		
	}
	
	public OpinionsFragment (String courseName/*, String professorName*/){
		this.counter = 0;
        this.courseName = courseName;
        this.professorName = professorName;
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
        }
        courseNameTextView.setText(courseName);
        Log.i(TAG, "TextView value = " + courseNameTextView.getText().toString());

        /* TO BE ADDED
        TextView professorNameTextView = (TextView) view.findViewById(R.id.professorName);
        professorNameTextView.setText(professorName);
        */

        ButtonFloat btn = (ButtonFloat) view.findViewById(R.id.buttonFloat);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.createInsertOpinionFragment();
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



}

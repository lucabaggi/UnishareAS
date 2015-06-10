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
	
	private String courseName;
	private View view;
	private ListView listview;
	
	private SmartActivity activity;
	private OpinionsAdapter opinionsAdapter;

    private int counter;
	
	public OpinionsFragment(){
		
	}
	
	public OpinionsFragment (String courseName){
		this.counter = 0;
        this.courseName = courseName;
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
        else{
            this.activity = (ProfileActivity) activity;
            Log.i(TAG, "fragment launched by ProfileActivity");
        }
    }
    

	@Override
	public void initializeUI(View view) {
        opinionsAdapter = activity instanceof CoursesActivity ? ((CoursesActivity) activity).getOpinionsAdapter()
                : ((ProfileActivity) activity).getOpinionsAdapter();
        Log.i(TAG, "opinionsFragment per il corso " + courseName);
        TextView courseNameTextView = (TextView) view.findViewById(R.id.courseName);
        courseNameTextView.setText(courseName);
        Log.i(TAG, "TextView value = " + courseNameTextView.getText().toString());
        if (this.courseName == null) {
            if(activity instanceof CoursesActivity)
                this.courseName = ((CoursesActivity)activity).getCourseName();
            else
                this.courseName = ((ProfileActivity)activity).getCourseName();
        }

        ButtonFloat btn = (ButtonFloat) view.findViewById(R.id.buttonFloat);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(activity instanceof CoursesActivity)
                    ((CoursesActivity)activity).createInsertOpinionFragment();
                else
                    ((ProfileActivity)activity).createInsertOpinionFragment();
            }
        });
        listview = (ListView) view.findViewById(R.id.opinionsListView);
        listview.setAdapter(opinionsAdapter);
        if (opinionsAdapter.getCount() == 0 && counter == 0) {
            counter++;
            Log.i(TAG, "No opinions for this course");
            String title = "";
            String message = "Nessuna opinione presente per questo corso";
            if (activity instanceof CoursesActivity)
                ((CoursesActivity) activity).getMyApplication().alertMessage(title, message);
            else
                ((ProfileActivity) activity).getMyApplication().alertMessage(title, message);
        }
    }
	
	

}

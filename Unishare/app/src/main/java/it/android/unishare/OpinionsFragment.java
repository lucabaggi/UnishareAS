package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Aggiungi ai tuoi corsi", "Aggiungi ai corsi superati"};

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(course.get("nome"))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        activity.getMyApplication().toastMessage(activity, "Miei corsi");
                                        break;
                                    case 1:
                                        activity.getMyApplication().toastMessage(activity, "corsi superati");
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
	
	

}

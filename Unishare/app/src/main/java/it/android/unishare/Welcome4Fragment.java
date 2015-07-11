package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;

public class Welcome4Fragment extends Fragment implements ViewInitiator {

	public static final String TAG = "WelcomeFragment4";

	private String campusImage;
	private ArrayList<String> courses, professors;
	
	private WelcomeActivity activity;
	private View view;

	private AutoCompleteTextView coursesSelector;
	private ListView courseListView;
	private ArrayList<String> coursesList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_welcome4, container, false);
		courses = getArguments().getStringArrayList("courses");
		professors = getArguments().getStringArrayList("professors");
        initializeUI(view);
        return view;
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (WelcomeActivity) activity;
    }
    
    @Override
	public void initializeUI(View view) {
		//ImageView campusImageView = (ImageView) view.findViewById(R.id.imageView);
		//Utilities.loadImage(campusImageView,"campus.jpg",activity.getApplicationContext());

		ButtonRectangle saveButton = (ButtonRectangle) view.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				activity.goToDashboard();
			}
		});

		ImageView universityLogoImageView = (ImageView) view.findViewById(R.id.universityLogoImage);
		Utilities.loadImage(universityLogoImageView, "universityLogo.jpg", activity.getApplicationContext());

		courseListView = (ListView) view.findViewById(R.id.listCourses);
		ArrayAdapter<String> coursesAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,coursesList);
		courseListView.setAdapter(coursesAdapter);

		coursesSelector = (AutoCompleteTextView) view.findViewById(R.id.autoSelectCourses);
		coursesSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				coursesList.add(coursesSelector.getText().toString());
				ArrayAdapter<String> coursesAdapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,coursesList);
				courseListView.setAdapter(coursesAdapter);
				activity.addToCourses(coursesSelector.getText().toString());
				coursesSelector.setText("");
				MyApplication.hideKeyboard(activity);
			}
		});
		String[] crss = new String[courses.size()];
		int i = 0;
		for(String c : courses) {
			crss[i] = c + " (" + professors.get(i) + ")";
			i++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, crss);
		coursesSelector.setAdapter(adapter);
	}

}

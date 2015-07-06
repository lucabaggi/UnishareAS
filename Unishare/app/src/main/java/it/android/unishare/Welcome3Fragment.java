package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Welcome3Fragment extends Fragment implements ViewInitiator {

	public static final String TAG = "WelcomeFragment3";

	private String campusImage;
	private ArrayList<String> specializations
			;
	
	private WelcomeActivity activity;
	private View view;

	AutoCompleteTextView specializationSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_welcome3, container, false);
		campusImage = getArguments().getString("campusImage");
		specializations = getArguments().getStringArrayList("specializations");
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
		ImageView campusImageView = (ImageView) view.findViewById(R.id.imageView);
		campusImageView.setTag(campusImage);
		new DownloadImagesTask("campus.jpg",activity.getApplicationContext()).execute(campusImageView);

		specializationSelector = (AutoCompleteTextView) view.findViewById(R.id.autoSelectSpecialization);
		specializationSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.goToCourseSelection(specializationSelector.getText().toString());
			}
		});
		String[] specs = new String[specializations.size()];
		int i = 0;
		for(String u : specializations) {
			specs[i] = u;
			i++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, specs);
		specializationSelector.setAdapter(adapter);

	}

}

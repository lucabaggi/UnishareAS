package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Welcome1Fragment extends Fragment implements ViewInitiator {

	public static final String TAG = "WelcomeFragment1";

	
	private WelcomeActivity activity;
	private View view;

	AutoCompleteTextView universitySelector;

	private static final String[] COUNTRIES = new String[] {
			"Belgium", "France", "Italy", "Germany", "Spain"
	};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_welcome1, container, false);
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


		universitySelector = (AutoCompleteTextView) view.findViewById(R.id.autoSelectUniversity);


	}

	public void displayUniversities(ArrayList<Entity> result) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
		universitySelector.setAdapter(adapter);
	}
    
}

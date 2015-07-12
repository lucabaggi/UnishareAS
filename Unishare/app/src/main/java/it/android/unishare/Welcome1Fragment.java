package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Welcome1Fragment extends Fragment implements ViewInitiator {

	public static final String TAG = "WelcomeFragment1";

	
	private WelcomeActivity activity;
	private View view;

	private ArrayList<String> universities;

	AutoCompleteTextView universitySelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_welcome1, container, false);
		universities = getArguments().getStringArrayList("universities");
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
		universitySelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.goToCampusSelection(universitySelector.getText().toString());
			}
		});
		String[] univs = new String[universities.size()];
		int i = 0;
		for(String u : universities) {
			univs[i] = u;
			i++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, univs);
		universitySelector.setAdapter(adapter);

		TextView errorLink = (TextView) view.findViewById(R.id.errorLink);
		errorLink.setText(Html.fromHtml("<a href=\"http://www.unishare.it/benvenuto/errore/universita\">"+ errorLink.getText() +"</a>"));
		errorLink.setMovementMethod(LinkMovementMethod.getInstance());
	}

    
}

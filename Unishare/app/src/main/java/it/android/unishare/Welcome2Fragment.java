package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Welcome2Fragment extends Fragment implements ViewInitiator {

	public static final String TAG = "WelcomeFragment2";

	private String universityName, universityImage, universityLogoImage;
	private ArrayList<String> campuses;
	
	private WelcomeActivity activity;
	private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_welcome2, container, false);
		universityImage = getArguments().getString("universityImage");
		universityLogoImage = getArguments().getString("universityLogoImage");
		universityName = getArguments().getString("universityName");
		campuses = getArguments().getStringArrayList("campuses");
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
		ImageView universityImageView = (ImageView) view.findViewById(R.id.universityImage);
		universityImageView.setTag(universityImage);
		new DownloadImagesTask().execute(universityImageView);

		ImageView universityLogoImageView = (ImageView) view.findViewById(R.id.universityLogoImage);
		universityLogoImageView.setTag(universityLogoImage);
		new DownloadImagesTask("universityLogo.jpg",activity.getApplicationContext()).execute(universityLogoImageView);

		TextView universityTextView = (TextView) view.findViewById(R.id.universityName);
		universityTextView.setText(universityName);

		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.campusSelector);
		for(String name : campuses) {
			final RadioButton button = new RadioButton(activity);
			button.setText(name);
			button.setTextColor(Color.BLACK);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					activity.goToSpecializationSelection(button.getText().toString());
				}
			});
			radioGroup.addView(button);
		}
	}

}

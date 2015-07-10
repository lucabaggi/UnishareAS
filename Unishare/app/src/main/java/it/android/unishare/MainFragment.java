package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainFragment extends Fragment implements ViewInitiator {
	
	public final static String TAG = "it.android.unishare.MainFragment";
	
	private MainActivity activity;
	private View view;

	private Integer [] viewFlipperArray = {
			R.layout.viewflipper_opinions,
			R.layout.viewflipper_books
	};
	private ArrayList<Integer> viewFlipperElements;

	public MainFragment() {
		viewFlipperElements = new ArrayList<Integer>(Arrays.asList(viewFlipperArray));
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initializeUI(view);
        return view;
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
    }
    
    @Override
	public void initializeUI(View view) {

		ImageView campusImageView = (ImageView) view.findViewById(R.id.campusImageView);
		Utilities.loadImage(campusImageView, "campus.jpg", activity.getApplicationContext());

		TextView dashNews = (TextView) view.findViewById(R.id.dashNews);
		//if(activity.getNews()!=null )dashNews.setText(activity.getNews());

		LayoutInflater factory = LayoutInflater.from(activity);

		ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);
		Collections.shuffle(viewFlipperElements);
		for(Integer index : viewFlipperElements) {
			viewFlipper.addView(factory.inflate(index, null));
		}

		viewFlipper.setInAnimation(activity, R.anim.slide_in_from_left);
		viewFlipper.setOutAnimation(activity, R.anim.slide_out_to_right);
		viewFlipper.setFlipInterval(6000);
		viewFlipper.startFlipping();

	}
    
}

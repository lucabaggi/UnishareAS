package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainFragment extends Fragment implements ViewInitiator {
	
	public final static String TAG = "it.android.unishare.MainFragment";
	
	private MainActivity activity;
	private View view;


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
		if(activity.getNews()!=null )dashNews.setText(activity.getNews());

	}
    
}

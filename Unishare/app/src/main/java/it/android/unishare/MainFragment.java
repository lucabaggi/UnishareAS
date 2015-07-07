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
		Utilities.loadImage(campusImageView,"campus.jpg",activity.getApplicationContext());

        Button btn = (Button) view.findViewById(R.id.logout_button);
        btn.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View view) {
                activity.logout();
	        }
        });

		Button btn4 = (Button) view.findViewById(R.id.button4);
		btn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.getInstance(activity).newActivity(WelcomeActivity.class);
			}
		});
        
	}

	public void displayResults(ArrayList<Entity> result, String tag) {
		Entity user = result.get(0);
		TextView textView = (TextView) view.findViewById(R.id.textView1);
		textView.setText(user.get("nickname"));
	}
    
}

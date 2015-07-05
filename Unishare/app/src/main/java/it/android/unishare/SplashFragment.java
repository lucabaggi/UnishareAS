package it.android.unishare;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SplashFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "SplashFragment";

	private View view;
    private SplashActivity activity;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(view == null){
    		view = inflater.inflate(R.layout.splash_fragment, container, false);
            initializeUI(view);
    	}
    	return view;       
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        activity.initialize();
    }


    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        this.activity = (SplashActivity) activity;
    }


	@Override
	public void initializeUI(View view) {

	}


}

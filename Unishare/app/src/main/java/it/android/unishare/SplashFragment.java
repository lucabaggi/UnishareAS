package it.android.unishare;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment implements ViewInitiator {
	
	private View view;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(view == null){
    		view = inflater.inflate(R.layout.splash_fragment, container, false);
    	}
    	return view;       
    }

    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
    }


	@Override
	public void initializeUI(View view) {
		/**
		 * It does nothing in this case because we have not associated listener/events
		 */
	}
    
}

package it.android.unishare;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Welcome2Fragment extends Fragment implements ViewInitiator {

	public static final String TAG = "WelcomeFragment2";

	private Entity university;
	
	private WelcomeActivity activity;
	private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_welcome2, container, false);
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
		ImageView universityImage = (ImageView) view.findViewById(R.id.universityImage);
		universityImage.setTag("http://www.unishare.it/images/Polimi/Leonardo/HeaderLeonardo-340.jpg");
		new DownloadImagesTask().execute(universityImage);
	}
	/*
	public void displayUniversities(ArrayList<Entity> result) {
		String[] universityList = new String[result.size()];
		int i =0;
		for(Entity e : result) {
			universityList[i] = e.get("nome");
			i++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, universityList);
		universitySelector.setAdapter(adapter);
	}
    */
}

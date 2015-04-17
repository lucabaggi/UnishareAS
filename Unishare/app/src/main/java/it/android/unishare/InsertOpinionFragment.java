package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class InsertOpinionFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "InsertOpinionFragment";
	
	private View view;
	
	private TextView commentTextView;
	private RatingBar ratingBar;
	
	private ProgressDialog dialog;
	
	private CoursesActivity activity;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null)
			view = inflater.inflate(R.layout.insert_opinion_fragment, container, false);
        initializeUI(view);
        return view;
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (CoursesActivity) activity;
    }
    

	@Override
	public void initializeUI(View view) {
		commentTextView = (TextView) view.findViewById(R.id.opinionText);		
		ratingBar = (RatingBar) view.findViewById(R.id.ratingInsertedBar);	
		dialog = new ProgressDialog(activity);
		dialog.setTitle("Inserting opinion");
		dialog.setMessage("Please wait...");
		Button btn = (Button) view.findViewById(R.id.insertOpinionConfirmButton);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Clicked button");
				String opinion = commentTextView.getText().toString();
				float rating = ratingBar.getRating();
				Log.i(TAG, "Opinione: " + opinion + ", voto: " + rating);
				if(opinion != null && rating > 0)
					activity.insertOpinion(opinion, rating, dialog);			
			}
		});
	}
	
	

}

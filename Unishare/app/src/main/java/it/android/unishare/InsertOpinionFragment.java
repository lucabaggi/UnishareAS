package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class InsertOpinionFragment extends Fragment implements ViewInitiator {
	
	public static final String TAG = "InsertOpinionFragment";
	
	private View view;
	
	private EditText commentText;
	private RatingBar ratingBar;
	
	private com.gc.materialdesign.widgets.ProgressDialog dialog;
	
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
		commentText = (EditText) view.findViewById(R.id.opinionText);
		ratingBar = (RatingBar) view.findViewById(R.id.ratingInsertedBar);
        String title = "Inserting";
        dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
		Button btn = (Button) view.findViewById(R.id.insertOpinionConfirmButton);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Clicked button");
				String opinion = commentText.getText().toString().trim();
				float rating = ratingBar.getRating();
				Log.i(TAG, "Opinione: " + opinion + ", voto: " + rating);
				if(opinion != null && !opinion.equals("") && rating > 0) {
                    activity.getMyApplication().hideKeyboard(activity);
					if(!Utilities.checkNetworkState(activity)){
						String title = "Errore";
						String message = "Verifica la tua connessione a Internet e riprova";
						activity.getMyApplication().alertMessage(title, message);
						return;
					}
                    activity.insertOpinion(opinion, rating, dialog);
                }

			}
		});
	}
	
	

}

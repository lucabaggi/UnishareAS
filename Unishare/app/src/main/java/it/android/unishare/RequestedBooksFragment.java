package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.ProgressDialog;

public class RequestedBooksFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "RequestedBooksFragment";

	private View view;
	private ListView listview;
	private ProgressDialog dialog;

	private RequestedBooksActivity activity;
	private RequestedBooksAdapter requestedBooksAdapter;

	public RequestedBooksFragment(){

	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "RequestedBooksFragment launched");
		if(view == null)
			view = inflater.inflate(R.layout.requested_books_fragment, container, false);
        initializeUI(view);
        return view;
    }



    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (RequestedBooksActivity) activity;
    }


	@Override
	public void initializeUI(View view) {
		listview = (ListView) view.findViewById(R.id.requestedBooksListView);
		requestedBooksAdapter = activity.getRequestedBooksAdapter();
		listview.setAdapter(requestedBooksAdapter);

	}
}

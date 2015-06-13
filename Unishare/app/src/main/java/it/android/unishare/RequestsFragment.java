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

public class RequestsFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "RequestsFragment";

	private View view;
	private ListView listview;

	private MyBooksActivity activity;
	private RequestsAdapter requestsAdapter;

	public RequestsFragment(){

	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "RequestFragment launched");
		if(view == null)
			view = inflater.inflate(R.layout.requests_fragment, container, false);
        initializeUI(view);
        return view;
    }



    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MyBooksActivity) activity;
    }


	@Override
	public void initializeUI(View view) {
		listview = (ListView) view.findViewById(R.id.requestsListView);
		requestsAdapter = activity.getRequestAdapter();
		listview.setAdapter(requestsAdapter);
	}
}

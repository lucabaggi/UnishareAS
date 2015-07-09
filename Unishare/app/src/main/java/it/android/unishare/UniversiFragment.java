package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.widgets.ProgressDialog;

public class UniversiFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "UniversiFragment";

	private View view;
	private ListView listview;
    private ProgressDialog dialog;

	private UniversiActivity activity;
    private UniversiAdapter adapter;

	public UniversiFragment(){

	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null)
			view = inflater.inflate(R.layout.universi_fragment, container, false);
        initializeUI(view);
        return view;
    }


    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (UniversiActivity) activity;
    }


	@Override
	public void initializeUI(View view) {
        listview = (ListView) view.findViewById(R.id.universiListView);
        adapter = activity.getAdapter();
        listview.setAdapter(adapter);
	}
	

}

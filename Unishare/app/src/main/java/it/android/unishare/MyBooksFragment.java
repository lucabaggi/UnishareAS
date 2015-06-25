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

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.ProgressDialog;

public class MyBooksFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "MyBooksFragment";

	private View view;
	private ListView listview;
	private ProgressDialog dialog;
	private SwipeRefreshLayout swipeRefreshLayout;

	private MyBooksActivity activity;
	private MyBooksAdapter myBooksAdapter;

	public MyBooksFragment(){

	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "MyBooksFragment launched");
		if(view == null)
			view = inflater.inflate(R.layout.my_books_fragment, container, false);
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
		listview = (ListView) view.findViewById(R.id.myBooksListView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.my_books_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.Green, R.color.Orange, R.color.Blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activity.refreshBooks();
            }
        });
		myBooksAdapter = activity.getMyBooksAdapter();
		listview.setAdapter(myBooksAdapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!Utilities.checkNetworkState(activity)) {
					String title = "Errore";
					String message = "Verifica la tua connessione a Internet e riprova";
					activity.getMyApplication().alertMessage(title, message);
					return;
				}
				Entity book = (Entity) parent.getItemAtPosition(position);
				String bookId = book.get("id");
				Log.i(TAG, "Clicked on book " + bookId);
				String title = "Searching";
				dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), title);
				activity.getRequests(bookId, dialog);
			}

		});
		ButtonFloat sellBookButton = (ButtonFloat) view.findViewById(R.id.sellBookButtonFloat);
		sellBookButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.launchSellFragment();
			}
		});

	}

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return this.swipeRefreshLayout;
    }
}

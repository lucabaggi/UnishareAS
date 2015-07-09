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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(listview);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		Entity book;
		book = activity.getMyBooksAdapter().getItem(info.position);
		String bookTitle = book.get("titolo");
		menu.setHeaderTitle(bookTitle);
		menu.add(Menu.NONE, R.id.remove_book, Menu.NONE, "Rimuovi il libro");
		menu.add(Menu.NONE, R.id.book_sold, Menu.NONE, "Il libro è stato venduto");
		menu.add(Menu.NONE, R.id.requests, Menu.NONE, "Vedi richieste");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		//getting book info
		Entity book;
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		book = activity.getMyBooksAdapter().getItem(info.position);
		final String bookId = book.get("id");
		//final String courseName = course.get("nome");
		String title = "Errore";
		String message = "Verifica la tua connessione a Internet e riprova";

		switch (item.getItemId()) {
			case R.id.remove_book:
				if (!Utilities.checkNetworkState(activity)) {
					activity.getMyApplication().alertMessage(title, message);
					break;
				}
				activity.removeBook(bookId);
				break;
			case R.id.book_sold:
				if (!Utilities.checkNetworkState(activity)) {
					activity.getMyApplication().alertMessage(title, message);
					break;
				}
				activity.bookSold(bookId);
				break;
			case R.id.requests:
				if (!Utilities.checkNetworkState(activity)) {

					activity.getMyApplication().alertMessage(title, message);
					break;
				}
				Log.i(TAG, "Clicked on book " + bookId);
				String searching = "Searching";
				dialog = new com.gc.materialdesign.widgets.ProgressDialog(getActivity(), searching);
				activity.getRequests(bookId, dialog);
		}
		return super.onContextItemSelected(item);

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
				listview.showContextMenuForChild(view);
				/*
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
				*/
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

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

import java.util.ArrayList;
import java.util.Arrays;

public class DrawerFragment extends Fragment implements ViewInitiator{

    public static final String TAG = "DrawerFragment";

    SmartActivity activity;
    View view;
    ListView listView;
    String drawerItem[] = new String[]{"Vai ai Libri", "Vai ai Corsi"};
    DrawerAdapter drawerAdapter;


    public DrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.drawer_fragment, container, false);
            initializeUI(view);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (SmartActivity)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void initializeUI(View view) {
        listView = (ListView) view.findViewById(R.id.drawerListView);
        ArrayList drawerItemList = new ArrayList();
        drawerItemList.addAll(Arrays.asList(drawerItem));
        drawerAdapter = new DrawerAdapter(activity, drawerItemList);
        listView.setAdapter(drawerAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
                String item = (String)parent.getItemAtPosition(position);
                Log.i(TAG, "Clicked on " + item);
                activity.launchNewActivity(position);
            }

        });

    }
}

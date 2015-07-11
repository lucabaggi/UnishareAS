package it.android.unishare;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;

public class MyDataFragment extends Fragment implements ViewInitiator {

	public static final String TAG = "MyDataFragment";

	private View view;
    private TextView header;
    private ProgressDialog dialog;
	private MyDataActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MyDataActivity) activity;
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null)
			view = inflater.inflate(R.layout.my_data_fragment, container, false);
        initializeUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


	@Override
	public void initializeUI(View view) {

        TextView textView = (TextView) view.findViewById(R.id.userInfo);
        textView.setText(activity.getUserInfo());
        /*
        ImageView universityLogoImageView = (ImageView) view.findViewById(R.id.universityLogoImage);
        Utilities.loadImage(universityLogoImageView, "universityLogo.jpg", activity.getApplicationContext());
        */
        ButtonRectangle resetButton = (ButtonRectangle) view.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.resetAccount();
            }
        });
	}
	

}

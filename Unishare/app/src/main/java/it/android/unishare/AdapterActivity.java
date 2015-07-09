package it.android.unishare;

import android.app.ProgressDialog;
import android.widget.ArrayAdapter;

public abstract class AdapterActivity extends SmartActivity {
	
	abstract ArrayAdapter<Entity> getAdapter();

	abstract void initializeFragmentUI(String string, com.gc.materialdesign.widgets.ProgressDialog dialog);

    //abstract MyApplication getMyApplication();

}

package it.android.unishare;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class AsynchRequest extends AsyncTask<Void,ArrayList<Entity>,ArrayList<Entity>> {
	
	private String url;
	private String tag;
	private SmartActivity activity;
	private com.gc.materialdesign.widgets.ProgressDialog dialog;
	private float startedAt;
	
	public AsynchRequest setParameters(Activity activity, String url, String tag, com.gc.materialdesign.widgets.ProgressDialog dialog) {
		this.url = url;
		this.activity = (SmartActivity) activity;
		this.tag = tag;
		this.dialog = dialog;
		return this;
	}
	
	/** The system calls this to perform work in a worker thread and
	* delivers it the parameters given to AsyncTask.execute() */
	protected ArrayList<Entity> doInBackground(Void... params) {
		MyApplication.log("Begin: " + tag);
		startedAt = System.currentTimeMillis();
		return Utilities.queryDatabase(activity, "http://www.unishare.it/api/" + url);
	}
	
	protected void onPreExecute() {
        super.onPreExecute();
		if(dialog != null)
        	dialog.show();
	}
   
   /** The system calls this to perform work in the UI thread and delivers
    * the result from doInBackground() */
	protected void onPostExecute(ArrayList<Entity> result) {
		float finishedIn = System.currentTimeMillis() - startedAt;
		MyApplication.log("Finished in " + finishedIn + "ms : " + tag);
		if(result == null) {
			String tag = "error";
            if(dialog != null)
			    dialog.dismiss();
			activity.handleError(tag);
			return;
		}
        if(dialog != null)
		    dialog.dismiss();
		Log.i(activity.toString(), "calling handle result");
		activity.handleResult(result, tag);
	}

}

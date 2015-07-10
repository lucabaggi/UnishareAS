package it.android.unishare;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

public class IndependentRequest extends AsyncTask<Void,ArrayList<Entity>,ArrayList<Entity>> {
	
	private String url;
	private String tag;
	private MyApplication application;
	private com.gc.materialdesign.widgets.ProgressDialog dialog;
	private float startedAt;
	
	public IndependentRequest setParameters(MyApplication application, String url, String tag) {
		this.url = url;
		this.application = application;
		this.tag = tag;
		return this;
	}
	
	/** The system calls this to perform work in a worker thread and
	* delivers it the parameters given to AsyncTask.execute() */
	protected ArrayList<Entity> doInBackground(Void... params) {
		MyApplication.log("Begin: " + tag);
		startedAt = System.currentTimeMillis();
		return Utilities.queryDatabase(application.getContext(), "http://www.unishare.it/api/" + url);
	}

   
   /** The system calls this to perform work in the UI thread and delivers
    * the result from doInBackground() */
	protected void onPostExecute(ArrayList<Entity> result) {
		float finishedIn = System.currentTimeMillis() - startedAt;
		MyApplication.log("Finished in " + finishedIn + "ms : " + tag);
		application.handleRequests(result,tag);
	}

}

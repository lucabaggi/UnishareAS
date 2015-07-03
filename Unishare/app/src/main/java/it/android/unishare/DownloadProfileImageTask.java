package it.android.unishare;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.pkmmte.view.CircularImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by luca on 19/05/15.
 */
public class DownloadProfileImageTask extends AsyncTask<String, Void, Bitmap> {

    private FacebookActivity activity;

    public DownloadProfileImageTask(FacebookActivity activity){
        this.activity = activity;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        activity.saveToInternalStorage(result);
    }
}

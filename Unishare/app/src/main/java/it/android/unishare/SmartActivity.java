package it.android.unishare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import it.android.unishare.DatabaseContract.UserInfoTable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

public class SmartActivity extends ActionBarActivity {

    //public static Profile profile;
    protected MyApplication application;

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = MyApplication.getInstance(this);
        if(!(application.getUserId()>0)) application.fetchUserData();
        //if(!Utilities.checkNetworkState(this)) Application.toastMessage(this, "Nessuna connessione internet");
	}
	
	void handleResult(ArrayList<Entity> result, String tag) {

	}

    public void handleError(String tag){

    }

    public void launchNewActivity(int position){

    }

    protected void setName(){
        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText(application.getNickname());
    }

    protected void setImage(){
        String argsValues[] = {UserInfoTable.COLUMN_PROFILE_IMAGE_PATH};
        Cursor cursor = application.queryDatabase(UserInfoTable.TABLE_NAME, argsValues,
                null, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String path = cursor.getString(0);
            loadImageFromLocal(path);
        }
    }

    private void loadImageFromLocal(String path)
    {

        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircularImageView image = (CircularImageView) findViewById(R.id.circular_image);
            image.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public MyApplication getMyApplication() {
        return application;
    }
}

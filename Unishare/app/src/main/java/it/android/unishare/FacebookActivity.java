package it.android.unishare;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;


public class FacebookActivity extends SmartActivity {

    private static final String USER_INFO = "unishareUserInfo";
    private static final String REG_ID_TAG = "addingRegistrationId";

    private MyApplication application;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private Profile profile;

    private boolean atStart;
    private Entity userEntity;

    private Context context;
    private GoogleCloudMessaging gcm;
    private String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = MyApplication.getInstance(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

        setContentView(R.layout.activity_facebook);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        atStart = false;

        Intent intent = getIntent();
        int show = intent.getIntExtra("showActivity", 1);
        if(show == 0){
            Log.i("FacebookActivity", "Intent ha valore true");
            atStart = true;
        }


        profile = Profile.getCurrentProfile();
        if(profile != null) {
            Log.e("FBStatus: ", "Already logged as " + profile.getName());
            if(atStart){
                Log.e("FacebookActivity", "atStart = " + atStart);
                switchActivity();
                return;
            }

        } else {
            Log.e("FBStatus: ", "Not yet logged");
        }

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("FBStatus: ", "Success");
            }

            @Override
            public void onCancel() {
                Log.i("FBStatus: ", "Canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i("FBStatus: ", "Error");
            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile != null) {
                    Profile.setCurrentProfile(currentProfile);
                    profile = currentProfile;

                    //Adding corresponding Unishare user to db
                    getUser(profile.getId());
                } else {
                    application.logoutUser();
                    Log.i("FBStatus: ", "Now logged out");
                }
            }
        };
    }

    private void switchActivity() {
        application.newActivity(MainActivity.class);
        atStart = false;
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_facebook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mydata) {
            application.newActivity(MyDataActivity.class);
            return true;
        }
        else if(id == R.id.logout) {
            application.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("it.android.unishare", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onBackPressed(){

        String title = "Exit";
        String message ="Premi OK per uscire";
        DialogInterface.OnClickListener actionTrue = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        };
        DialogInterface.OnClickListener actionFalse = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        };

        application.alertDecision(title, message, null, null, actionTrue, actionFalse);
    }

    @Override
    public void handleResult(ArrayList<Entity> result, String tag) {
        Log.i("FacebookActivity", "handling results");
        if (tag == USER_INFO) {
            userEntity = result.get(0);
            String imageUrl = profile.getProfilePictureUri(500,500).toString();
            Log.i("FacebookActivity", "URL profile image: " + imageUrl);
            new DownloadProfileImageTask(this).execute(imageUrl);
            insertUserIntoLocalDatabase();
        }
    }

    private void getUser(String id){
        application.databaseCall("log_user.php?id=" + id, "unishareUserInfo", null);
    }

    protected void insertUserIntoLocalDatabase() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("Images", Context.MODE_PRIVATE);

        UserInfo user = new UserInfo(userEntity);
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserInfoTable.COLUMN_USER_ID, user.getUserId());
        values.put(DatabaseContract.UserInfoTable.COLUMN_NICKNAME, user.getNickname());
        values.put(DatabaseContract.UserInfoTable.COLUMN_UNIVERSITY_ID, user.getUniversityId());
        values.put(DatabaseContract.UserInfoTable.COLUMN_UNIVERSITY, user.getUniversity());
        values.put(DatabaseContract.UserInfoTable.COLUMN_CAMPUS_ID, user.getCampusId());
        values.put(DatabaseContract.UserInfoTable.COLUMN_CAMPUS, user.getCampus());
        values.put(DatabaseContract.UserInfoTable.COLUMN_SPECIALIZATION_ID, user.getSpecializationId());
        values.put(DatabaseContract.UserInfoTable.COLUMN_SPECIALIZATION, user.getSpecialization());
        values.put(DatabaseContract.UserInfoTable.COLUMN_LAST_ACCESS, user.getLastAccess());
        values.put(DatabaseContract.UserInfoTable.COLUMN_PROFILE_IMAGE_PATH, directory.getAbsolutePath());
        Log.i("FacebookActivity", "values ha grandezza" + values.size());
        application.insertIntoDatabase(DatabaseContract.UserInfoTable.TABLE_NAME, values);

        application.fetchUserData();

        context = FacebookActivity.this;
        gcm = GoogleCloudMessaging.getInstance(context);
        registerInBackground();

        new SyncUserCoursesTask(application).execute(user.getUserId());

        Intent intent;
        if(application.hasUserCompletedWelcome()) {
            intent = new Intent(FacebookActivity.this, MainActivity.class);
        }
        else {
            intent = new Intent(FacebookActivity.this, WelcomeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        FacebookActivity.this.finish();
        Log.i("FBStatus: ", "Now logged as " + profile.getName());
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Properties.GCM_SENDER_ID);

                } catch (IOException ex) {
                    return null;
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String regid) {
                if (regid != null) {
                    Log.i("FacebookActivity", "Registration_id = " + regid);
                    Properties.GCM_REGISTRATION_ID = regid;
                    int userid = application.getUserId();
                    addRegId(userid, Properties.GCM_REGISTRATION_ID);
                } else
                    Log.i("FacebookActivity", "Errore: registrazione su GCM non riuscita!");
            }
        }.execute();
    }

    private void addRegId(int userId, String regId){
        application.databaseCall("android_gcm_set.php?u=" + userId + "&id=" + regId, REG_ID_TAG, null);
    }

}

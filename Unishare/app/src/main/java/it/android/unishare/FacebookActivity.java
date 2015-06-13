package it.android.unishare;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;


public class FacebookActivity extends SmartActivity {

    private static final String USER_INFO = "unishareUserInfo";

    private MyApplication application;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private Profile profile;

    private Button returnButton;
    private boolean atStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = MyApplication.getInstance(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_facebook);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setVisibility(View.INVISIBLE);
        atStart = false;

        Intent intent = getIntent();
        int show = intent.getIntExtra("showActivity", 1);
        if(show == 0){
            Log.i("FacebookActivity", "Intent ha valore true");
            atStart = true;
        }


        //Profile.fetchProfileForCurrentAccessToken();
        profile = Profile.getCurrentProfile();
        if(profile != null) {
            Log.i("FBStatus: ", "Already logged as " + profile.getName());
            if(atStart){
                Log.i("FacebookActivity", "atStart = " + atStart);
                switchActivity();
                return;
            }

            returnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartActivity.profile = profile;
                    application.newActivity(MainActivity.class);
                    FacebookActivity.this.finish();
                }
            });
            returnButton.setVisibility(View.VISIBLE);
        } else {
            Log.i("FBStatus: ", "Not yet logged");
        }

        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
                    SmartActivity.profile = profile;

                    //Adding corresponding Unishare user to db
                    getUser(profile.getId());
                } else {
                    returnButton.setVisibility(View.INVISIBLE);
                    application.deleteTable(DatabaseContract.UserInfoTable.TABLE_NAME);
                    application.deleteTable(DatabaseContract.MyCoursesTable.TABLE_NAME);
                    application.deleteTable(DatabaseContract.PassedExams.TABLE_NAME);
                    Log.i("FBStatus: ", "Now logged out");
                }
            }
        };
        //profileTracker.startTracking();
    }

    private void switchActivity() {
        SmartActivity.profile = profile;
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
        if (id == R.id.action_settings) {
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
        Log.i("MainActivity", "handling results");
        if (tag == USER_INFO) {
            Entity userEntity = result.get(0);
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

            Log.i("MainActivity", "values ha grandezza" + values.size());
            application.insertIntoDatabase(DatabaseContract.UserInfoTable.TABLE_NAME, values);
            Intent intent = new Intent(FacebookActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            FacebookActivity.this.finish();
            Log.i("FBStatus: ", "Now logged as " + profile.getName());
        }
    }

    private void getUser(String id){
        application.databaseCall("log_user.php?id=" + id, "unishareUserInfo", null);
    }

}

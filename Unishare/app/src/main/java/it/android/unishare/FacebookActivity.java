package it.android.unishare;

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
import java.util.Arrays;


public class FacebookActivity extends ActionBarActivity {

    private MyApplication application;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private Profile profile;

    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = MyApplication.getInstance(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_facebook);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        //Profile.fetchProfileForCurrentAccessToken();
        profile = Profile.getCurrentProfile();
        if(profile != null) {
            Log.i("FBStatus: ", "Already logged as " + profile.getName());
            application.alertMessage("Bentornato, " + profile.getFirstName(), "Bella zio, sei già connesso B)");
            returnButton = new Button(this);
            returnButton.setText("Torna ad Unishare");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.login_button);
            returnButton.setLayoutParams(params);
            returnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SmartActivity.profile = profile;
                    application.newActivityWithParameter(MainActivity.class, "profile", profile);
                    finish();
                }
            });
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.facebook_layout);
            layout.addView(returnButton);
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
                    Intent intent = new Intent(FacebookActivity.this, MainActivity.class);
                    intent.putExtra("profile", profile);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    FacebookActivity.this.finish();
                    Log.i("FBStatus: ", "Now logged as " + currentProfile.getName());
                } else {
                    if(returnButton != null){
                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.facebook_layout);
                        layout.removeView(returnButton);
                    }
                    Log.i("FBStatus: ", "Now logged out");
                }
            }
        };
        //profileTracker.startTracking();
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
        String message ="Sei sicuro di voler uscire?";
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

        application.alertDecision(title, message, actionTrue, actionFalse);
    }


}

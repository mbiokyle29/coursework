package app.android.homeBase;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.view.animation.Animation;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.ParseUser;

/**
 * This activity is the default launcher activity for the app
 * If there is a cached user session it will simple hand off the instance to the feed activity
 * Otherwise it presents the nessecary login features and has a button leading to signup
 *
 * TODO handle a logged in user who has none or multiple houses
 */
public class LoginActivity extends HomeBaseActivity{
    private Animation animTranslate;
    private ApplicationManager mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myIntent = getIntent();
        myClassName = "LoginActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        mApplication = ApplicationManager.getInstance();
        setContentView(R.layout.activity_login);


        // activity here
        if(mApplication.parse.userLoggedIn())
        {
            Log.d("Logged in User", mApplication.parse.getCurrentUser().getUsername());
            if(mApplication.parse.getCurrentUser().has("house")) {
                mApplication.upsertHouseData();
                Intent startFeed = new Intent(LoginActivity.this, NewsFeedActivity.class);
                startFeed.putExtra("caller", myClassName);
                startActivity(startFeed);
            }
            else {
                Intent startNewhouse = new Intent(LoginActivity.this, JoinOrCreateHouseActivity.class);
                startNewhouse.putExtra("caller", myClassName);
                startActivity(startNewhouse);
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * on click method for the login button
     * Gets the entered information and calls the loginUser method in ParseBase class
     * @param v
     */
    public void loginOnClick(View v)
    {
        BootstrapEditText usernameText= (BootstrapEditText) findViewById(R.id.login_username_etext);
        BootstrapEditText passwordText = (BootstrapEditText) findViewById(R.id.login_password_etext);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if(!username.isEmpty() && !password.isEmpty())
        {
            mApplication.parse.loginUser(username, password, v.getContext(), this);
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Please enter your username and password", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoginSuccess()
    {
        if(ParseUser.getCurrentUser().has("house"))
        {
            mApplication.upsertHouseData();
            Intent startFeed = new Intent(LoginActivity.this, NewsFeedActivity.class);
            startActivity(startFeed);
        } else {
            Intent startNewHouse = new Intent(LoginActivity.this, JoinOrCreateHouseActivity.class);
            startActivity(startNewHouse);
        }
        finish();
    }

    @Override
    public void onLoginError(String e)
    {
        Toast.makeText(LoginActivity.this, e, Toast.LENGTH_LONG).show();
    }

    /**
     * Simple onclick method for the signup button
     * Just starts the signUp activity
     * @param view
     */
    public void onSignUpClick(View view)
    {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(LoginActivity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("SERVICE", service.service.getClassName());
            if (GPSservice.class.getName().equals(service.service.getClassName())) {
                Log.d("Found Service"," Don't start new one");
                return true;
            }
        }
        Log.d("Did Not Find Service"," Start new one");
        return false;
    }
}

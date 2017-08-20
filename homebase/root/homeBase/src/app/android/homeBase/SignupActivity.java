package app.android.homeBase;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This activity class handles the signup "flow" for the application
 * It is started (usually) from the login activity. It's main purpose is to validate the user input
 * entered into the edittext boxes. It does this using the Listeners made within onCreate.
 */
public class SignupActivity extends HomeBaseActivity
{
    private ProgressBar progressSpinner;
    private List<BootstrapEditText> editTexts;
    private ApplicationManager mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "SignupActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_signup);
        progressSpinner = (ProgressBar) findViewById(R.id.signup_progressbar);
        final EmailValidator emailVal = new EmailValidator();

        //Get references to the three edit texts
        final BootstrapEditText usernameEditText = (BootstrapEditText) findViewById(R.id.signup_username_etext);
        final BootstrapEditText emailEditText = (BootstrapEditText) findViewById(R.id.signup_email_etext);
        final BootstrapEditText passwordEditText = (BootstrapEditText) findViewById(R.id.signup_password_etext);
        final BootstrapEditText passwordVerEditText = (BootstrapEditText) findViewById(R.id.signup_passwordV_etext);

        // Also store them in a list
        editTexts= new LinkedList<BootstrapEditText>();
        editTexts.add(usernameEditText);
        editTexts.add(emailEditText);
        editTexts.add(passwordEditText);
        editTexts.add(passwordVerEditText);

        // Ref all four checkboxes make sure they are checked
        final List<CheckBox> boxes = new LinkedList<CheckBox>();
        boxes.add((CheckBox) findViewById(R.id.username_checkbox));
        boxes.add((CheckBox) findViewById(R.id.email_checkbox));
        boxes.add((CheckBox) findViewById(R.id.password_checkbox));
        boxes.add((CheckBox) findViewById(R.id.passwordVer_checkbox));

        usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(usernameEditText.getText())) {
                    String username = usernameEditText.getText().toString();
                    if (!username.isEmpty()) {
                        mApplication.parse.checkUserName(username, SignupActivity.this);
                    }
                }
                return false;
            }
        });

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // When the edittext loses focus
                if (!hasFocus && !TextUtils.isEmpty(usernameEditText.getText())) {
                    String username = usernameEditText.getText().toString();
                    // If a username was submitted, verify that username isnt already in use on parse
                    if (!username.isEmpty()) {
                        mApplication.parse.checkUserName(username, SignupActivity.this);
                    }
                }
            }
        });

        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(emailEditText.getText())) {
                    String email = emailEditText.getText().toString();
                    if (!email.isEmpty() && emailVal.validate(email)) {
                        CheckBox emailCheckBox = (CheckBox) findViewById(R.id.email_checkbox);
                        emailCheckBox.setChecked(true);
                    } else {
                        Toast.makeText(view.getContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // When the edittext loses focus
                if (!hasFocus && !TextUtils.isEmpty(emailEditText.getText())) {
                    String email = emailEditText.getText().toString();
                    if (!email.isEmpty() && emailVal.validate(email)) {
                        CheckBox emailCheckBox = (CheckBox) findViewById(R.id.email_checkbox);
                        emailCheckBox.setChecked(true);
                    } else {
                        Toast.makeText(view.getContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });


        // Same idea for password, but now jsut check for a reasonable size password
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(passwordEditText.getText()))
                {
                    CheckBox passWordCheckBox = (CheckBox) findViewById(R.id.password_checkbox);
                    String password = passwordEditText.getText().toString();
                    // If the password is valid the checkbox gets checked
                    if (password.length() < 5) {
                        Toast.makeText(view.getContext(), "Please enter a password of at least 6 characters", Toast.LENGTH_LONG).show();
                        passWordCheckBox.setChecked(false);
                    } else {
                        passWordCheckBox.setChecked(true);
                    }
                }
                return false;
            }
        });

        // Same idea for password, but now jsut check for a reasonable size password
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && !TextUtils.isEmpty(passwordEditText.getText())) {
                    CheckBox passWordCheckBox = (CheckBox) findViewById(R.id.password_checkbox);
                    String password = passwordEditText.getText().toString();
                    // If the password is valid the checkbox gets checked
                    if (password.length() < 5) {
                        Toast.makeText(view.getContext(), "Please enter a password of at least 6 characters", Toast.LENGTH_LONG).show();
                        passWordCheckBox.setChecked(false);
                    } else {
                        passWordCheckBox.setChecked(true);
                    }
                }
            }
        });

        // Again, just make sure it matches the first password box
        passwordVerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(passwordVerEditText.getText())) {
                    String passwordVer = passwordVerEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    CheckBox passWordVerCheckBox = (CheckBox) findViewById(R.id.passwordVer_checkbox);
                    CheckBox passWordCheckBox = (CheckBox) findViewById(R.id.password_checkbox);
                    if (password.equals(passwordVer) && passWordCheckBox.isChecked()) {
                        passWordVerCheckBox.setChecked(true);
                    } else {
                        Toast.makeText(view.getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                        passWordVerCheckBox.setChecked(false);
                    }
                }
                return false;
            }
        });

        // Again, just make sure it matches the first password box
        passwordVerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus && !TextUtils.isEmpty(passwordVerEditText.getText())) {
                    String passwordVer = passwordVerEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    CheckBox passWordVerCheckBox = (CheckBox) findViewById(R.id.passwordVer_checkbox);
                    CheckBox passWordCheckBox = (CheckBox) findViewById(R.id.password_checkbox);
                    if (password.equals(passwordVer) && passWordCheckBox.isChecked()) {
                        passWordVerCheckBox.setChecked(true);
                    } else {
                        Toast.makeText(view.getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                        passWordVerCheckBox.setChecked(false);
                    }
                }
            }
        });

        //Set up listener for the signUp Botton
        BootstrapButton signupButton = (BootstrapButton) findViewById(R.id.signup_signup_button);
        signupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                for(CheckBox box : boxes)
                {
                    if(!box.isChecked())
                    {
                        Toast.makeText(SignupActivity.this, "Please correctly fill out all fields", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                // Fire up a nifty spinner to indicate the user is being signed up
                progressSpinner.setVisibility(View.VISIBLE);
                progressSpinner.setIndeterminate(true);

                // addUser will handle the intent firing upon succsessful signup
                mApplication.parse.addUser(usernameEditText.getText().toString(), passwordEditText.getText().toString(), emailEditText.getText().toString(), SignupActivity.this);
            }
        });
    }

    @Override
    public void onSignupSuccess(ParseUser user)
    {
        GPSservice gps = new GPSservice(SignupActivity.this);
        Intent newHouse = new Intent(SignupActivity.this, JoinOrCreateHouseActivity.class);
        progressSpinner.setVisibility(View.INVISIBLE);
        progressSpinner.setIndeterminate(false);
        startActivity(newHouse);
        finish();
    }

    @Override
    public void onSignupError(ParseException e)
    {
        ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.signup_progressbar);
        progressSpinner.setIndeterminate(false);
        progressSpinner.setVisibility(View.INVISIBLE);
        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckUserSuccess()
    {
        CheckBox usernameCheckBox = (CheckBox) findViewById(R.id.username_checkbox);
        usernameCheckBox.setChecked(true);
    }

    @Override
    public void onCheckUserFailure()
    {
        CheckBox usernameCheckBox = (CheckBox) findViewById(R.id.username_checkbox);
        usernameCheckBox.setChecked(false);
        Toast.makeText(SignupActivity.this, "This username is taken!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckUserError(ParseException e)
    {
        CheckBox usernameCheckBox = (CheckBox) findViewById(R.id.username_checkbox);
        usernameCheckBox.setChecked(false);
        Toast.makeText(SignupActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signup, menu);
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
}

class EmailValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean validate(final String hex)
    {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }
}
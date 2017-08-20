package app.android.homeBase;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import java.util.ArrayList;
import android.widget.Button;
import android.view.ViewGroup.LayoutParams;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.FontAwesomeText;


public class GPSActivity extends HomeBaseActivity {
    private LinearLayout globalLayout;
    private boolean expand = true;
    private int menuHeight;
    private ApplicationManager mApplication;

    ArrayList<HomeBaseUser> stats = new ArrayList<HomeBaseUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "GPSActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_gps);


        //if no user print message
        if(mApplication.users.size() < 2){

            LinearLayout layout = (LinearLayout) findViewById(R.id.GPSuser_container);

            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.alert_container, null, false);

            BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_container);
            BootstrapButton myHeader = (BootstrapButton) buttonCont.findViewById(R.id.alertContainer_header);

            myButton.setClickable(false);

            buttonCont.removeView(myButton);
            layout.addView(myButton);

            myButton.setText("New users can join with the admin username");
            myHeader.setText("No Users Found");
            myHeader.setBootstrapType("danger");
        }

        mApplication.parse.getUsersOfHouse(GPSActivity.this);
    }

    @Override
    public void onGetHomeUsersSuccess(String user, Boolean home) {
        HomeBaseUser userStatus = new HomeBaseUser(user, home);
        Log.d("Found User", userStatus.username + " " + userStatus.isHome);
        stats.add(userStatus);

        if (mApplication.parse.getCurrentUser().getUsername().equals(user)){return;}

        LinearLayout layout = (LinearLayout) findViewById(R.id.GPSuser_container);

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout buttonCont = (LinearLayout) inflater.inflate(R.layout.gps_user_template, null, false);

        BootstrapButton myButton = (BootstrapButton) buttonCont.findViewById(R.id.GPStracker_template_button);
        BootstrapButton myHeader = (BootstrapButton) buttonCont.findViewById(R.id.GPStracker_template_header);
        FontAwesomeText myIcon = (FontAwesomeText) buttonCont.findViewById(R.id.GPStracker_template_icon);

        buttonCont.removeView(myButton);
        layout.addView(myButton);
        String header = user;
        //String isHome = String.valueOf(home);
       // myButton.setText(isHome);
        myHeader.setText(user);
        myHeader.setBootstrapType("default");
        if (home) {
            myButton.setText("Home");
            myIcon.startFlashing(this, true, FontAwesomeText.AnimationSpeed.MEDIUM);
            myIcon.setTextColor(getResources().getColor(R.color.bbutton_success));
            myHeader.setBootstrapType("success");
        } else {
            myButton.setText("Not home");
            myIcon.setTextColor(getResources().getColor(R.color.bbutton_danger));
            myHeader.setBootstrapType("danger");
        }

    }

    @Override
    public void onGetHomeUsersFailure()
    {
        Log.d("Found User","FAILURE");
    }

    @Override
    public void onGetAlertListSuccess(ArrayList<HomeBaseAlert> alerts)
    {

    }

    @Override
    public void onReturnUsersSuccess() {
        Log.d("Found House", "yes, yes i did");
    }

    @Override
    public void onReturnUsersFailure() {
        Log.d("Found all users","FAILURE");

    }

}


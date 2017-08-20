package app.android.homeBase;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import com.beardedhen.androidbootstrap.BootstrapButton;

public class ChoreInfoActivity extends HomeBaseActivity {
    private String title;
    private String info;
    private String creator;
    private String alertID;

    private ApplicationManager mApplication;
    private List <String> responsibleUsers;
    private List <String> completedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "ChoreInfoActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_chore_info);
        responsibleUsers = new ArrayList<String>();
        completedUsers = new ArrayList<String>();

        Bundle extras = getIntent().getExtras();
        title = "";
        info = "";
        creator = "";
        if (extras != null) {
            title = extras.getString("title");
            info = extras.getString("info");
            creator = extras.getString("creator");
            alertID = extras.getString("alertID");
        }

        BootstrapButton headerBar = (BootstrapButton) this.findViewById(R.id.chore_info_header_button);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Resources resources = this.getResources();

        int navBarHeight = 0;

        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        int statusBarHeight = 0;

        resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        int height = (int)((size.y - headerBar.getLayoutParams().height * 2) - navBarHeight - statusBarHeight);

        BootstrapButton infoContainer = (BootstrapButton) this.findViewById(R.id.chore_info_body_button);
        ViewGroup.LayoutParams rlp = infoContainer.getLayoutParams();
        rlp.height = height;
        infoContainer.setLayoutParams(rlp);
        BootstrapButton body = (BootstrapButton) this.findViewById(R.id.chore_info_body_button);

        BootstrapButton creatorField = (BootstrapButton) this.findViewById(R.id.chore_info_creator_field);
        mApplication.parse.getAlertResponsibleUsers(creator, title, this);
        mApplication.parse.getAlertCompletedUsers(creator, title, this);

        creatorField.setText(creator);
        headerBar.setText(title);
        body.setText(info);
    }

    void DecideViewOptions(boolean responsible) {
        BootstrapButton btn = (BootstrapButton) this.findViewById(R.id.choreInfo_confirm_button);
        if (creator.equals(mApplication.parse.getCurrentUser().getUsername())) {
            btn.setText("Close Issue");
        } else if (responsible) {
            btn.setText("Mark Completed");
        } else {
            btn.setText("");
            btn.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }

    public void onChoreInfoConfirmClick(View view)
    {
        // if creator
        if (creator.equals(mApplication.parse.getCurrentUser().getUsername())) {
            // delete bill
            // myObject.deleteInBackground();
            mApplication.parse.getAlert(alertID, ChoreInfoActivity.this);

        } else {
            String currUser = mApplication.parse.getCurrentUser().getUsername();
            boolean removed = responsibleUsers.remove(currUser);
            if (removed) {
                completedUsers.add(currUser);
            }
        }

        mApplication.parse.updateAlertResponsibleUsers(creator, title, responsibleUsers, completedUsers, "Chore", this);
    }

    @Override
    public void onUpdateAlertSuccess(HomeBaseAlert alert)
    {
        finish();
    }


    public void onChoreInfoCancelClick(View view)
    {
        onBackPressed();
    }

    @Override
    public void onGetAlertResponsibleUsersSuccess(List<String> responsibleUsers) {
        String currUser = mApplication.parse.getCurrentUser().getUsername();
        boolean currentUserIsResponsible = false;

        for (int i = 0; i < responsibleUsers.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout template = (LinearLayout) inflater.inflate(R.layout.user_select_template, null, false);
            RelativeLayout btnContainer = (RelativeLayout) template.findViewById(R.id.userSelection_template_container);
            template.removeView(btnContainer);
            BootstrapButton userBtn = (BootstrapButton)btnContainer.findViewById(R.id.userSelection_button);
            userBtn.setText(responsibleUsers.get(i));
            LinearLayout responsibleContainer = (LinearLayout) this.findViewById(R.id.chore_info_responsible_container);
            responsibleContainer.addView(btnContainer);
            this.responsibleUsers.add(responsibleUsers.get(i));
            if (responsibleUsers.get(i).equals(currUser)) currentUserIsResponsible = true;
        }

        DecideViewOptions(currentUserIsResponsible);
    }

    @Override
    public void onGetAlertResponsibleUsersFailure(String e) {

    }

    @Override
    public void onGetAlertCompletedUsersSuccess(List<String> completedUsers) {
        for (int i = 0; i < completedUsers.size(); i++) {
            this.completedUsers.add(completedUsers.get(i));
        }
    }

    @Override
    public void onGetAlertCompletedUsersFailure(String e) {

    }

    @Override
    public void onGetAlertSuccess(HomeBaseAlert alert){
        mApplication.parse.deleteAlert(alert, this);
    }

    @Override
    public void onDeleteAlertFailure(String error){
        Log.d("Delete Alert", "Failed: " + error);
    }

    @Override
    public void onDeleteAlertSuccess()
    {
        finish();
    }
}
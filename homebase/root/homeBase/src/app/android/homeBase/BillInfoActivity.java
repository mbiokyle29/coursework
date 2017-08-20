package app.android.homeBase;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.util.Log;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.parse.ParseObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BillInfoActivity extends HomeBaseActivity {
    private String title;
    private String info;
    private String creator;
    private String amount;
    private String alertID;

    private ApplicationManager mApplication;
    private List<String> responsibleUsers;
    private List <String> completedUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "BillInfoActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_bill_info);
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
            amount = extras.getString("amount");
            alertID = extras.getString("alertID");
        }

        BootstrapButton headerBar = (BootstrapButton) this.findViewById(R.id.bill_info_header_button);

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

        int height = (int)((size.y - headerBar.getLayoutParams().height * 3) - navBarHeight - statusBarHeight);

        BootstrapButton infoContainer = (BootstrapButton) this.findViewById(R.id.bill_info_body_button);
        ViewGroup.LayoutParams rlp = infoContainer.getLayoutParams();
        rlp.height = height;
        infoContainer.setLayoutParams(rlp);
        BootstrapButton body = (BootstrapButton) this.findViewById(R.id.bill_info_body_button);

        BootstrapButton creatorField = (BootstrapButton) this.findViewById(R.id.billInfo_creator_field);
        BootstrapButton amountField = (BootstrapButton) this.findViewById(R.id.billInfo_amount_field);
        mApplication.parse.getAlertResponsibleUsers(creator, title, this);
        mApplication.parse.getAlertCompletedUsers(creator, title, this);

        amountField.setText("$" + amount);
        creatorField.setText(creator);
        headerBar.setText(title);
        body.setText(info);
    }

    void DecideViewOptions(boolean responsible) {
        BootstrapButton btn = (BootstrapButton) this.findViewById(R.id.billInfo_confirm_button);
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

    public void onBillInfoConfirmClick(View view)
    {
        // if creator
        if (creator.equals(mApplication.parse.getCurrentUser().getUsername())) {
            // delete bill
           // myObject.deleteInBackground();
            mApplication.parse.getAlert(alertID, BillInfoActivity.this);

        } else {  // not creator so just remove the one user from bill
            String currUser = mApplication.parse.getCurrentUser().getUsername();
            boolean removed = responsibleUsers.remove(currUser);
            if (removed) {
                completedUsers.add(currUser);
            }
        }
        mApplication.parse.updateAlertResponsibleUsers(creator, title, responsibleUsers, completedUsers, "Bill", this);
    }

    @Override
    public void onUpdateAlertSuccess(HomeBaseAlert alert)
    {
        finish();
    }

    public void onBillInfoCancelClick(View view)
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
            userBtn.setClickable(false);
            LinearLayout responsibleContainer = (LinearLayout) this.findViewById(R.id.billInfo_responsible_container);
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
        Log.d("Delete Alert","Failed: " + error);
    }

    @Override
    public void onDeleteAlertSuccess()
    {
        finish();
    }
}
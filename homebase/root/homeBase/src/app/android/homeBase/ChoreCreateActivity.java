package app.android.homeBase;


import android.content.res.Resources;
import android.os.Bundle;
import android.view.ViewGroup;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ChoreCreateActivity extends HomeBaseActivity {
    private BootstrapEditText headerBar;
    private BootstrapEditText infoContainer;
    private BootstrapButton ownerField;
    private ApplicationManager mApplication;

    private ArrayList<String> userNames;
    private HashMap<String, BootstrapButton> responsibleUsers;
    private HashMap<BootstrapButton, Boolean> selectedResponsibleUsers;

    private final String k_alertType = "Chore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "ChoreCreateActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_chore_create);

        headerBar = (BootstrapEditText) this.findViewById(R.id.chore_create_header_field);

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

        int height = ((size.y - headerBar.getLayoutParams().height * 2) - navBarHeight - statusBarHeight);

        infoContainer = (BootstrapEditText) this.findViewById(R.id.chore_create_body_field);
        ViewGroup.LayoutParams rlp = infoContainer.getLayoutParams();
        rlp.height = height;
        infoContainer.setLayoutParams(rlp);

        //set up "responsible for" options
        userNames = mApplication.getHomeUsers();
        responsibleUsers = new HashMap<String, BootstrapButton>();
        selectedResponsibleUsers = new HashMap<BootstrapButton, Boolean>();


        for (int i = 0; i < userNames.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout template = (LinearLayout) inflater.inflate(R.layout.user_select_template, null, false);
            RelativeLayout btnContainer = (RelativeLayout) template.findViewById(R.id.userSelection_template_container);
            template.removeView(btnContainer);
            BootstrapButton userBtn = (BootstrapButton)btnContainer.findViewById(R.id.userSelection_button);
            userBtn.setText(userNames.get(i));
            responsibleUsers.put(userNames.get(i), userBtn);
            selectedResponsibleUsers.put(userBtn, false);
            LinearLayout responsibleContainer = (LinearLayout) this.findViewById(R.id.chore_create_responsible_container);
            responsibleContainer.addView(btnContainer);
        }

        ownerField = (BootstrapButton) this.findViewById(R.id.chore_create_creator_field);
        ownerField.setText(mApplication.parse.getCurrentUser().getUsername());
    }

    public void onUserSelected(View view)
    {
        BootstrapButton button = (BootstrapButton)view;
        if (!selectedResponsibleUsers.get(button)) {
            button.setBootstrapType("info");
            selectedResponsibleUsers.put(button, true);
        } else {
            button.setBootstrapType("default");
            selectedResponsibleUsers.put(button, false);
        }
    }

    public void onChoreCreateSubmitClick(View view)
    {
        String title = headerBar.getText().toString();
        String type = k_alertType;
        String desc = infoContainer.getText().toString();
        String creator = mApplication.parse.getCurrentUser().getUsername();
        List<String> responsibleUsers = new LinkedList<String>();

        for(String user : userNames) {
            BootstrapButton userButton = this.responsibleUsers.get(user);
            if (selectedResponsibleUsers.get(userButton)) {
                responsibleUsers.add(user);
            }
        }

        mApplication.parse.createAlert(title,type, desc, responsibleUsers, creator, ChoreCreateActivity.this);
    }

    public void onChoreCreateCancelClick(View view)
    {
        onBackPressed();
    }

    @Override
    public void onCreateAlertSuccess(HomeBaseAlert alert)
    {
        finish();
    }

    @Override
    public void onCreateAlertFailure(String e)
    {
        Toast.makeText(ChoreCreateActivity.this, e, Toast.LENGTH_LONG).show();
    }
}
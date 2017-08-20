package app.android.homeBase;

import android.content.Intent;
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
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class SupplyCreateActivity extends HomeBaseActivity {

    private ApplicationManager mApplication;

    private BootstrapEditText headerBar;
    private BootstrapEditText infoContainer;
    private BootstrapButton creatorField;

    private ArrayList<String> userNames;
    private HashMap<String, BootstrapButton> responsibleUsers;
    private HashMap<BootstrapButton, Boolean> selectedResponsibleUsers;

    private final String k_alertType = "Supply";

    private HomeBaseAlert createdAlert;
    private HashMap<String, ParseUser> usersObjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "SupplyCreateActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_supply_create);

        headerBar = (BootstrapEditText) this.findViewById(R.id.supplyCreate_header_field);

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

        int height = ((size.y - headerBar.getLayoutParams().height * 3) - navBarHeight - statusBarHeight);

        infoContainer = (BootstrapEditText) this.findViewById(R.id.supplyCreate_body_field);
        ViewGroup.LayoutParams rlp = infoContainer.getLayoutParams();
        rlp.height = height;
        infoContainer.setLayoutParams(rlp);

        creatorField = (BootstrapButton) this.findViewById(R.id.supplyCreate_creator_field);
        creatorField.setText(mApplication.parse.getCurrentUser().getUsername());

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

    public void onSupplyCreateSubmitClick(View view)
    {
        String title = headerBar.getText().toString();
        String type = k_alertType;
        String desc = infoContainer.getText().toString();

        // Make sure everything is filled out
        List<String> fields = new LinkedList<String>(Arrays.asList(title, type, desc));
        for(String field : fields)
        {
            if(field.isEmpty())
            {
                Toast.makeText(SupplyCreateActivity.this, "Please fill out all fields", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String creator = mApplication.parse.getCurrentUser().getUsername();
        List<String> placeHolder = new LinkedList<String>();
        mApplication.parse.createAlert(title, type, desc, placeHolder, creator, SupplyCreateActivity.this);
    }

    public void onSupplyCreateCancelClick(View view)
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
        Toast.makeText(SupplyCreateActivity.this, e, Toast.LENGTH_LONG).show();
    }
}
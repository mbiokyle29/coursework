package app.android.homeBase;

import app.android.homeBase.HomeBaseActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;


public class SupplyInfoActivity extends HomeBaseActivity {
    private String title;
    private String info;
    private String creator;
    private String id;

    private ApplicationManager mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ApplicationManager.getInstance();
        myIntent = getIntent();
        myClassName = "SupplyInfoActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_supply_info);
        Bundle extras = getIntent().getExtras();
        title = "";
        info = "";
        creator = "";
        id = "";
        if(extras != null) {
            title = extras.getString("title");
            info = extras.getString("info");
            creator = extras.getString("creator");
            id = extras.getString("id");
        }

        BootstrapButton headerBar = (BootstrapButton) this.findViewById(R.id.supply_info_header_button);

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

        BootstrapButton infoContainer = (BootstrapButton) this.findViewById(R.id.supply_info_body_button);
        ViewGroup.LayoutParams rlp = infoContainer.getLayoutParams();
        rlp.height = height;
        infoContainer.setLayoutParams(rlp);
        BootstrapButton body = (BootstrapButton) this.findViewById(R.id.supply_info_body_button);

        BootstrapButton creatorField = (BootstrapButton) this.findViewById(R.id.supply_info_creator_field);

        mApplication.parse.getAlertResponsibleUsers(creator, title, this);
        mApplication.parse.getAlertCompletedUsers(creator, title, this);

        creatorField.setText(creator);
        headerBar.setText(title);
        body.setText(info);
        BootstrapButton btn = (BootstrapButton) this.findViewById(R.id.supplyInfo_confirm_button);
        btn.setText("Mark Completed");
    }

    public void onSupplyInfoConfirmClick(View view) {
        mApplication.parse.getAlert(id, SupplyInfoActivity.this);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }

    @Override
    public void onGetAlertSuccess(HomeBaseAlert alert)
    {
        mApplication.parse.deleteAlert(alert, SupplyInfoActivity.this);
    }

    @Override
    public void onGetAlertFailure(String e)
    {
        Toast.makeText(SupplyInfoActivity.this, "Error: "+e, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteAlertSuccess()
    {
        Intent supplyInfo = new Intent(SupplyInfoActivity.this, SuppliesActivity.class);
        mApplication.forwardIntentQueue.pop();
        startActivity(supplyInfo);
        finish();
    }

    @Override
    public void onDeleteAlertFailure(String e)
    {
        Toast.makeText(SupplyInfoActivity.this, "Error: "+e, Toast.LENGTH_LONG).show();
    }


}

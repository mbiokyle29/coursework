package app.android.homeBase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.view.View;


public class JoinOrCreateHouseActivity extends HomeBaseActivity {
    private Animation animTranslate;
    private ApplicationManager mApplication;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myIntent = getIntent();
        myClassName = "JoinOrCreateHouseActivity";
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        mApplication = ApplicationManager.getInstance();
        setContentView(R.layout.activity_join_create);
    }


    public void onCreateHouseClick(View view){
        Intent intent = new Intent(JoinOrCreateHouseActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void onJoinClick(View view){
        EditText houseCodeET = (EditText) findViewById(R.id.newhouse_joinhouse_etext);
        try
        {
            String code = houseCodeET.getText().toString();
            if(code.length() != 0)
            {
                mApplication.parse.getHouse(code, JoinOrCreateHouseActivity.this);
            }
            else
            {
                Toast.makeText(JoinOrCreateHouseActivity.this, "Please enter the username of the house admin", Toast.LENGTH_LONG).show();
            }
        } catch (NullPointerException e)
        {
            Toast.makeText(JoinOrCreateHouseActivity.this, "Please enter the username of the house admin", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetHouseSuccess(HomeBaseHouse house)
    {
        house.getMembers().add(ParseUser.getCurrentUser().getObjectId());
        mApplication.parse.updateHouse(house, JoinOrCreateHouseActivity.this);
    }

    @Override
    public void onGetHouseFailure(String e)
    {
        Toast.makeText(JoinOrCreateHouseActivity.this, e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateHouseSuccess(final HomeBaseHouse house)
    {
        ParseUser.getCurrentUser().put("house", house.getId());
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    mApplication.upsertHouseData();
                    mApplication.subscribeToHouseChannel(house.getId());
                    mApplication.setHouse(house);
                    Intent startFeed = new Intent(JoinOrCreateHouseActivity.this, NewsFeedActivity.class);
                    startFeed.putExtra("caller", myClassName);
                    startActivity(startFeed);
                    finish();
                }
                else {
                    Toast.makeText(JoinOrCreateHouseActivity.this, "Error: Could not update user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onUpdateHouseFailure(String e)
    {
        Toast.makeText(JoinOrCreateHouseActivity.this, e, Toast.LENGTH_SHORT).show();
    }
}

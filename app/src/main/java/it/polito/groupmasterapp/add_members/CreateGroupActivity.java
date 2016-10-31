package it.polito.groupmasterapp.add_members;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import it.polito.groupmasterapp.Injection;
import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.util.ActivityUtils;

public class CreateGroupActivity extends AppCompatActivity {

    private CreateGroupPresenter mCreateGroupPresenter;

    public static final String DEVICE_NAME_EXTRA = "it.polito.groupmasterapp.DEVICE_NAME_EXTRA";
    public static final String GROUP_NAME_EXTRA = "it.polito.groupmasterapp.GROUP_NAME_EXTRA";
    public static final String GROUP_ID_EXTRA = "it.polito.groupmasterapp.GROUP_ID_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CreateGroupActivityFragment createGroupFragment = (CreateGroupActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (createGroupFragment == null) {
            //creazione del fragment
            createGroupFragment = CreateGroupActivityFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), createGroupFragment, R.id.contentFrame);
        }

        //creazione del presenter
        mCreateGroupPresenter = new CreateGroupPresenter(createGroupFragment, Injection.provideGroupRepository(getApplicationContext()));

        Intent intent = getIntent();
        if (intent != null) {
            String deviceName = intent.getStringExtra(DEVICE_NAME_EXTRA);
            String groupName = intent.getStringExtra(GROUP_NAME_EXTRA);
            String groupId = intent.getStringExtra(GROUP_ID_EXTRA);

            mCreateGroupPresenter.setGroup(new Group(groupName, groupId));
            mCreateGroupPresenter.setDeviceName(deviceName);

        } else if (savedInstanceState != null) {
            mCreateGroupPresenter.restoreSavedInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCreateGroupPresenter.start();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mCreateGroupPresenter.stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mCreateGroupPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}

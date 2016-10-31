package it.polito.groupslaveapp.group_action;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.polito.groupslaveapp.Injection;
import it.polito.groupslaveapp.R;
import it.polito.groupslaveapp.data.source.GroupSlaveService;
import it.polito.groupslaveapp.util.ActivityUtils;

public class GroupActionSlaveActivity extends AppCompatActivity {

    public static final String GROUP_ID_EXTRA = "it.polito.groupmasterapp.group_actions.GROUP_ID_EXTRA";
    private GroupActionSlavePresenter mPresenter;
    private GroupSlaveService mGroupService;
    private String mGroupId;
    private boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GroupSlaveService.GroupBinder binder = (GroupSlaveService.GroupBinder) service;
            mGroupService = binder.getService();
            mBound = true;
            mPresenter.setService(mGroupService);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_action_slave);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GroupActionSlaveActivityFragment groupActionsFragment = (GroupActionSlaveActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (groupActionsFragment == null) {
            //creazione del fragment
            groupActionsFragment = GroupActionSlaveActivityFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), groupActionsFragment, R.id.contentFrame);
        }

        Intent intent = getIntent();
        if (intent != null) mGroupId = intent.getStringExtra(GROUP_ID_EXTRA);
        if (savedInstanceState != null) {
            mGroupId = savedInstanceState.getString(GROUP_ID_EXTRA);
        }

        mPresenter = new GroupActionSlavePresenter(groupActionsFragment, Injection.provideGroupRepository(getApplicationContext()), mGroupId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(GROUP_ID_EXTRA, mGroupId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, GroupSlaveService.class);
        intent.putExtra(GroupSlaveService.GROUP_ID_ARG, mGroupId);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mPresenter.start();
    }

    @Override
    protected void onStop() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        mPresenter.stop();
        super.onStop();
    }

}

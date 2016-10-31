package it.polito.groupmasterapp.group_actions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.polito.groupmasterapp.Injection;
import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.data.source.GroupService;
import it.polito.groupmasterapp.util.ActivityUtils;

public class GroupActionsActivity extends AppCompatActivity {

    public static final String GROUP_ID_EXTRA = "it.polito.groupmasterapp.group_actions.GROUP_ID_EXTRA";
    private GroupActionsPresenter mPresenter;
    private GroupService mGroupService;
    private String mGroupId;
    private boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GroupService.GroupBinder binder = (GroupService.GroupBinder) service;
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
        setContentView(R.layout.activity_group_actions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GroupActionsActivityFragment groupActionsFragment = (GroupActionsActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (groupActionsFragment == null) {
            //creazione del fragment
            groupActionsFragment = GroupActionsActivityFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), groupActionsFragment, R.id.contentFrame);
        }
        Intent intent = getIntent();
        if (intent != null) mGroupId = intent.getStringExtra(GROUP_ID_EXTRA);
        if (savedInstanceState != null) {
            mGroupId = savedInstanceState.getString(GROUP_ID_EXTRA);
        }
        mPresenter = new GroupActionsPresenter(groupActionsFragment, Injection.provideGroupRepository(getApplicationContext()), mGroupId);

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
        Intent intent = new Intent(this, GroupService.class);
        intent.putExtra(GroupService.GROUP_ID_ARG, mGroupId);
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

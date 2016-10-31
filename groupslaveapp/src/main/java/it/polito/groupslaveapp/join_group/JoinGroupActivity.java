package it.polito.groupslaveapp.join_group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.polito.groupslaveapp.Injection;
import it.polito.groupslaveapp.R;
import it.polito.groupslaveapp.search_group.SearchGroupPresenter;
import it.polito.groupslaveapp.util.ActivityUtils;

public class JoinGroupActivity extends AppCompatActivity {

    public static final String DEVICE_NAME_EXTRA = "it.polito.groupmasterapp.DEVICE_NAME_EXTRA";
    private JoinGroupContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        JoinGroupActivityFragment fragment = (JoinGroupActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            //creazione del fragment
            fragment = JoinGroupActivityFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        mPresenter = new JoinGroupPresenter(Injection.provideGroupRepository(getApplicationContext()), fragment, getApplicationContext());

        if (savedInstanceState != null) {
            mPresenter.restoreSavedInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}

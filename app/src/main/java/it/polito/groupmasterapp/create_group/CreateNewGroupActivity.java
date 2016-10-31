package it.polito.groupmasterapp.create_group;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import it.polito.groupmasterapp.Injection;
import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.util.ActivityUtils;

public class CreateNewGroupActivity extends AppCompatActivity {

    private CreateNewGroupContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CreateNewGroupActivityFragment fragment = (CreateNewGroupActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            //creazione del fragment
            fragment = CreateNewGroupActivityFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        mPresenter = new CreateNewGroupPresenter(fragment, Injection.provideGroupRepository(getApplicationContext()), getApplicationContext());

        if (savedInstanceState != null) {
            // TODO: 13/10/16  
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO: 13/10/16
        super.onSaveInstanceState(outState);
    }

}

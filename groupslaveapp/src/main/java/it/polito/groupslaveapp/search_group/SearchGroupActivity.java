package it.polito.groupslaveapp.search_group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.polito.groupslaveapp.Injection;
import it.polito.groupslaveapp.R;
import it.polito.groupslaveapp.util.ActivityUtils;

public class SearchGroupActivity extends AppCompatActivity {

    private SearchGroupContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SearchGroupActivityFragment fragment = (SearchGroupActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            //creazione del fragment
            fragment = SearchGroupActivityFragment.getInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        mPresenter = new SearchGroupPresenter(Injection.provideGroupRepository(getApplicationContext()), fragment, getApplicationContext());

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

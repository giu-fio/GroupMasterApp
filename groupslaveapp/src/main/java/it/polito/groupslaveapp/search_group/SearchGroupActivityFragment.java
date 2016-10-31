package it.polito.groupslaveapp.search_group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.base.Strings;

import it.polito.groupslaveapp.R;
import it.polito.groupslaveapp.join_group.JoinGroupActivity;
import it.polito.groupslaveapp.util.AbstractTextWatcher;

import static it.polito.groupslaveapp.search_group.SearchGroupContract.Presenter;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchGroupActivityFragment extends Fragment implements SearchGroupContract.View {

    private Presenter mPresenter;
    //Views
    private EditText mDeviceNameEditText;
    private Button mSearchButton;
    private ProgressBar mProgressBar;

    public SearchGroupActivityFragment() {
    }

    public static SearchGroupActivityFragment getInstance() {
        return new SearchGroupActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_group, container, false);


        mDeviceNameEditText = (EditText) v.findViewById(R.id.deviceNameEditText);
        mSearchButton = (Button) v.findViewById(R.id.searchButton);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        mDeviceNameEditText.addTextChangedListener(new AbstractTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.deviceNameChanged(s.toString());
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.searchClick();
            }
        });
        mProgressBar.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void buttonEnabled(boolean enabled) {
        mSearchButton.setEnabled(enabled);
    }

    @Override
    public void initDeviceName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            mDeviceNameEditText.setText(name);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void navigateToJoinGroup(String deviceName) {
        Intent intent = new Intent(getActivity(), JoinGroupActivity.class);
        intent.putExtra(JoinGroupActivity.DEVICE_NAME_EXTRA, deviceName);
        startActivity(intent);
    }

    @Override
    public void showLoading(boolean loading) {
        int visibility = (loading) ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void showCreateErrorMessage() {
        Toast.makeText(getContext(), getString(R.string.search_error), Toast.LENGTH_LONG).show();
    }
}

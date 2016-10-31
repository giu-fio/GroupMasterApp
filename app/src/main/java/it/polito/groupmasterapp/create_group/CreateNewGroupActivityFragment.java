package it.polito.groupmasterapp.create_group;

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

import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.add_members.CreateGroupActivity;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.util.AbstractTextWatcher;

import static it.polito.groupmasterapp.create_group.CreateNewGroupContract.Presenter;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateNewGroupActivityFragment extends Fragment implements CreateNewGroupContract.View {

    private Presenter mPresenter;

    //Views
    private EditText mGroupNameEditText;
    private EditText mDeviceNameEditText;
    private Button mCreateButton;
    private ProgressBar mProgressBar;


    public CreateNewGroupActivityFragment() {
    }

    public static CreateNewGroupActivityFragment getInstance() {
        return new CreateNewGroupActivityFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_new_group, container, false);


        //find view
        mGroupNameEditText = (EditText) v.findViewById(R.id.groupNameEditText);
        mDeviceNameEditText = (EditText) v.findViewById(R.id.deviceNameEditText);
        mCreateButton = (Button) v.findViewById(R.id.creaButton);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        //init view listeners
        mGroupNameEditText.addTextChangedListener(new AbstractTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.groupNameChanged(s.toString());
            }
        });

        mDeviceNameEditText.addTextChangedListener(new AbstractTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.deviceNameChanged(s.toString());
            }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createClick();
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
        mCreateButton.setEnabled(enabled);
    }

    @Override
    public void initDeviceName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            mDeviceNameEditText.setText(name);
        }
    }

    @Override
    public void initGroupName(String groupName) {
        if (!Strings.isNullOrEmpty(groupName)) {
            mGroupNameEditText.setText(groupName);
        }
    }

    @Override
    public void setPresenter(CreateNewGroupContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void navigateToAddMembers(String deviceName, Group group) {
        Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
        intent.putExtra(CreateGroupActivity.DEVICE_NAME_EXTRA, deviceName);
        intent.putExtra(CreateGroupActivity.GROUP_NAME_EXTRA, group.getName());
        intent.putExtra(CreateGroupActivity.GROUP_ID_EXTRA, group.getId());
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showLoading(boolean loading) {
        int visibility = (loading) ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void showCreateErrorMessage() {
        Toast.makeText(getContext(), getString(R.string.create_error), Toast.LENGTH_LONG).show();
    }

}

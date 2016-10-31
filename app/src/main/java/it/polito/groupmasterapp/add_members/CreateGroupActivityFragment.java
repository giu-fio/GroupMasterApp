package it.polito.groupmasterapp.add_members;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.group_actions.GroupActionsActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateGroupActivityFragment extends Fragment implements CreateGroupContract.View, View.OnClickListener {

    private CreateGroupContract.Presenter mPresenter;

    private ProgressBar mProgressBar;
    private TextView mMessageTextView;
    private Button mCreateGroupButton;
    private Button mDiscoverButton;
    private RecyclerView mRecyclerView;
    private DevicesAdapter mAdapter;


    public CreateGroupActivityFragment() {
        // Requires empty public constructor
    }

    public static CreateGroupActivityFragment getInstance() {
        return new CreateGroupActivityFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        //find views
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageTextView = (TextView) view.findViewById(R.id.messageTextView);
        mCreateGroupButton = (Button) view.findViewById(R.id.creaButton);
        mDiscoverButton = (Button) view.findViewById(R.id.cercaButton);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        //init recycler view
        mAdapter = new DevicesAdapter(new LinkedList<Slave>(), new LinkedList<Device>(), new DevicesAdapter.OnItemSelected<Device>() {
            @Override
            public void onItemSelected(Device item, boolean selected) {
                if (selected) {
                    mPresenter.addDevice(item);
                } else {
                    mPresenter.removeDevice(item);
                }
            }
        }, new DevicesAdapter.OnItemSelected<Slave>() {
            @Override
            public void onItemSelected(Slave item, boolean selected) {
                if (selected) {
                    mPresenter.addSlave(item);
                } else {
                    mPresenter.addSlave(item);
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init views and callbacks
        mProgressBar.setVisibility(View.GONE);
        mMessageTextView.setText(String.format(getString(R.string.discovery_message), getString(R.string.discovery_button)));
        mDiscoverButton.setOnClickListener(this);
        mCreateGroupButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void onClick(View v) {
        if (v == mCreateGroupButton) {
            //Create button click handler
            mPresenter.startGroup();

        } else if (v == mDiscoverButton) {
            //Discovery button click handler
            mPresenter.discoverDevices();
            mPresenter.discoverSlaves();
        }
    }

    @Override
    public void setPresenter(CreateGroupContract.Presenter presenter) {
        checkNotNull(presenter);
        mPresenter = presenter;
    }

    @Override
    public void showMessage(boolean value) {
        int visibility = (value) ? View.VISIBLE : View.GONE;
        mMessageTextView.setVisibility(visibility);
    }

    @Override
    public void showLoadingSlaves(boolean loading) {
        int visibility = (loading) ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void showLoadingDevices(boolean loading) {
        int visibility = (loading) ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(visibility);
    }


    @Override
    public void showNoSlave() {
        Toast.makeText(getContext(), getString(R.string.no_slaves_message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNoDevices() {
        Toast.makeText(getContext(), getString(R.string.no_devices_message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDevices(List<Device> devices) {
        mAdapter.addDevices(devices);
    }

    @Override
    public void showSlaves(List<Slave> slaves) {
        mAdapter.addSlaves(slaves);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void removeAllSlaves() {
        mAdapter.clearSlaves();
    }

    @Override
    public void removeSlave(Slave slave) {
        mAdapter.removeSlave(slave);
    }

    @Override
    public void removeAllDevices() {
        mAdapter.clearDevices();
    }

    @Override
    public void removeDevice(Device device) {
        mAdapter.removeDevice(device);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getActivity(), getString(R.string.create_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void createButtonEnabled(boolean enabled) {
        mCreateGroupButton.setEnabled(enabled);
    }

    @Override
    public void navigateToGroupAction(String groupId) {
        Intent intent = new Intent(getActivity(), GroupActionsActivity.class);
        intent.putExtra(GroupActionsActivity.GROUP_ID_EXTRA, groupId);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void discoverButtonEnabled(boolean b) {
        mDiscoverButton.setEnabled(b);
    }
}

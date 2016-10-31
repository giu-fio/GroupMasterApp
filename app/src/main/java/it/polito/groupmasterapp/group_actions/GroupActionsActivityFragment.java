package it.polito.groupmasterapp.group_actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.groupmasterapp.MainActivity;
import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupActionsActivityFragment extends Fragment implements GroupActionsContract.View {

    private static final String TAG = GroupActionsActivityFragment.class.getSimpleName();

    private RecyclerView mLostDevicesRecyclerView;
    private DeviceAdapter mDeviceAdapter;
    private RecyclerView mGroupRecyclerView;
    private GroupAdapter mGroupAdapter;
    private ProgressBar mProgressBar;
    private GroupActionsContract.Presenter mPresenter;

    public GroupActionsActivityFragment() {
    }

    public static GroupActionsActivityFragment getInstance() {
        return new GroupActionsActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_actions, container, false);
        setHasOptionsMenu(true);

        mLostDevicesRecyclerView = (RecyclerView) v.findViewById(R.id.lostDevicesRecyclerView);
        mGroupRecyclerView = (RecyclerView) v.findViewById(R.id.groupsRecyclerView);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        mDeviceAdapter = new DeviceAdapter(new ArrayList<Device>());
        mLostDevicesRecyclerView.setAdapter(mDeviceAdapter);
        mLostDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mGroupAdapter = new GroupAdapter(getContext(), new LinkedList<SlaveListItem>());
        mGroupRecyclerView.setAdapter(mGroupAdapter);
        mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_group_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_close:
                mPresenter.stopClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showMessage(boolean value) {
        Toast.makeText(getActivity(), "Messaggio temporaneo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean loading) {
        int visibility = (loading) ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getContext(), getString(R.string.generic_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLostDevice(Device device) {
        Log.d(TAG, "showLostDevice: ");
        mDeviceAdapter.addDevice(device);
    }

    @Override
    public void showLostSlave(Slave slave) {
        // TODO: 23/10/16
    }

    @Override
    public void showDeviceFound(Device device) {
        Log.d(TAG, "showDeviceFound: ");
        mDeviceAdapter.removeDevice(device);
    }

    @Override
    public void showSlaveFound(Slave slave) {
// TODO: 23/10/16
    }

    @Override
    public void updateSlave(Slave slave, List<Device> devices) {
        mGroupAdapter.updateSubGroup(slave, devices);
    }

    @Override
    public void updateMaster(List<Device> devices) {
        // TODO: 24/10/16  
    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void setPresenter(GroupActionsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}

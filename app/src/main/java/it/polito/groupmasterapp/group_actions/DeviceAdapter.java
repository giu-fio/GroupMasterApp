package it.polito.groupmasterapp.group_actions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import java.util.List;

import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;

/**
 * Created by giuseppe on 12/10/16.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private static final int SLAVE_VIEW_TYPE = 0;
    private static final int DEVICE_VIEW_TYPE = 1;
    private List<Device> devices;


    public DeviceAdapter(List<Device> devices) {
        this.devices = devices;
    }

    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        v.findViewById(R.id.checkbox).setVisibility(View.GONE);
        return new DeviceViewHolder(v);

    }

    @Override
    public void onBindViewHolder(DeviceAdapter.DeviceViewHolder holder, int position) {


        Device device = devices.get(position);
        DeviceViewHolder deviceVh = (DeviceViewHolder) holder;
        deviceVh.uidTextView.setText(device.getUid());

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    public void setDevices(List<Device> deviceList) {
        Preconditions.checkNotNull(deviceList);
        int oldSize = devices.size();
        devices.addAll(deviceList);
        notifyItemRangeInserted(oldSize, deviceList.size());
    }

    public void addDevice(Device device) {
        Preconditions.checkNotNull(device);
        if (!devices.contains(device)) {
            devices.add(device);
            notifyItemInserted(devices.size() - 1);
        }
    }


    public void clearDevices() {
        int size = devices.size();
        devices.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeDevice(Device device) {
        int pos = devices.indexOf(device);
        if (pos >= 0) {
            devices.remove(pos);
        }
        notifyItemRemoved(pos);
    }


    class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView uidTextView;


        DeviceViewHolder(View itemView) {
            super(itemView);
            uidTextView = (TextView) itemView.findViewById(R.id.uid);

        }
    }

}

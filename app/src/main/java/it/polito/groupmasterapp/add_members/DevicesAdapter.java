package it.polito.groupmasterapp.add_members;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import java.util.List;

import it.polito.groupmasterapp.R;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;

/**
 * Created by giuseppe on 12/10/16.
 */

public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SLAVE_VIEW_TYPE = 0;
    private static final int DEVICE_VIEW_TYPE = 1;
    private List<Slave> slaves;
    private List<Device> devices;
    private OnItemSelected<Device> devicesListener;
    OnItemSelected<Slave> slaveListener;


    public DevicesAdapter(List<Slave> slaves, List<Device> devices, OnItemSelected<Device> devicesListener, OnItemSelected<Slave> slaveListener) {
        this.slaves = slaves;
        this.devices = devices;
        this.devicesListener = devicesListener;
        this.slaveListener = slaveListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == SLAVE_VIEW_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slave, parent, false);
            return new SlaveViewHolder(v);
        }

        if (viewType == DEVICE_VIEW_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
            return new DeviceViewHolder(v);
        }

        //mai raggiunto
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < slaves.size()) {
            //Slave
            if (!(holder instanceof SlaveViewHolder)) {
                throw new IllegalStateException("wrong view holder subclass");
            }
            Slave slave = slaves.get(position);
            SlaveViewHolder slaveVh = (SlaveViewHolder) holder;
            slaveVh.nameTextView.setText(slave.getName());
            slaveVh.idTextView.setText(slave.getId());
        } else {
            //Device
            if (!(holder instanceof DeviceViewHolder)) {
                throw new IllegalStateException("wrong view holder subclass");
            }
            Device device = devices.get(position - slaves.size());
            DeviceViewHolder deviceVh = (DeviceViewHolder) holder;
            deviceVh.uidTextView.setText(device.getUid());
        }
    }

    @Override
    public int getItemCount() {
        return slaves.size() + devices.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < slaves.size()) return SLAVE_VIEW_TYPE;
        return DEVICE_VIEW_TYPE;
    }

    public void addDevices(List<Device> deviceList) {
        Preconditions.checkNotNull(deviceList);
        int oldSize = devices.size();
        devices.addAll(deviceList);
        notifyItemRangeInserted(oldSize, deviceList.size());
    }

    public void addSlaves(List<Slave> slaveList) {
        Preconditions.checkNotNull(slaveList);
        int oldSize = slaves.size();
        slaves.addAll(slaveList);
        notifyItemRangeInserted(oldSize, slaveList.size());
    }

    public void clearSlaves() {
        int size = slaves.size();
        slaves.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeSlave(Slave slave) {
        int pos = slaves.indexOf(slave);
        if (pos >= 0) {
            slaves.remove(pos);
        }
        notifyItemRemoved(pos);
    }

    public void clearDevices() {
        int size = devices.size();
        devices.clear();
        notifyItemRangeRemoved(slaves.size(), size);
    }

    public void removeDevice(Device device) {
        int pos = devices.indexOf(device);
        if (pos >= 0) {
            devices.remove(pos);
        }
        notifyItemRemoved(pos + slaves.size());
    }


    class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView uidTextView;
        AppCompatCheckBox checkBox;

        DeviceViewHolder(View itemView) {
            super(itemView);
            uidTextView = (TextView) itemView.findViewById(R.id.uid);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getAdapterPosition() - slaves.size();
                    Device device = devices.get(pos);
                    devicesListener.onItemSelected(device, isChecked);
                }
            });
        }
    }

    class SlaveViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView idTextView;
        AppCompatCheckBox checkBox;

        SlaveViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            idTextView = (TextView) itemView.findViewById(R.id.id);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getAdapterPosition();
                    Slave slave = slaves.get(pos);
                    slaveListener.onItemSelected(slave, isChecked);
                }
            });
        }
    }

    interface OnItemSelected<T> {
        void onItemSelected(T item, boolean selected);
    }
}

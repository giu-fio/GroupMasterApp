package it.polito.groupslaveapp.group_action;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.List;

import it.polito.groupslaveapp.R;
import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Slave;


/**
 * Created by giuseppe on 19/10/16.
 */

public class GroupAdapter extends ExpandableRecyclerAdapter<GroupAdapter.SlaveViewHolder, GroupAdapter.DeviceViewHolder> {

    private LayoutInflater mInflator;
    private List<SlaveListItem> slaveListItems;
    private Context context;

    public GroupAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.slaveListItems = (List<SlaveListItem>) parentItemList;
        this.context = context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public SlaveViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View slaveView = mInflator.inflate(R.layout.item_group_slave, parentViewGroup, false);
        return new SlaveViewHolder(slaveView);
    }

    @Override
    public DeviceViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View deviceView = mInflator.inflate(R.layout.item_group_device, childViewGroup, false);
        return new DeviceViewHolder(deviceView);
    }

    @Override
    public void onBindParentViewHolder(SlaveViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        SlaveListItem slaveListItem = (SlaveListItem) parentListItem;
        Slave slave = slaveListItem.getSlave();
        parentViewHolder.bind(slave, slaveListItem.getDeviceList().size());
    }

    public void updateSubGroup(Slave slave, List<Device> deviceList) {
        for (SlaveListItem slaveListItem : slaveListItems) {
            if (slaveListItem.getSlave().equals(slave)) {
                slaveListItem.setDeviceList(deviceList);
                notifyDataSetChanged();
                return;
            }
        }
        //non è presente nella lista
        SlaveListItem slaveListItem = new SlaveListItem(slave, deviceList);
        slaveListItems.add(slaveListItem);
        notifyParentItemInserted(slaveListItems.size() - 1);
    }

    @Override
    public void onBindChildViewHolder(DeviceViewHolder childViewHolder, int position, Object childListItem) {
        Device device = (Device) childListItem;
        childViewHolder.bind(device);
    }

    public class SlaveViewHolder extends ParentViewHolder {

        private TextView nameTextView;
        private TextView countTextView;

        public SlaveViewHolder(View itemView) {
            super(itemView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.name);
            this.countTextView = (TextView) itemView.findViewById(R.id.count);
        }

        public void bind(Slave slave, int count) {
            String name = (slave.getName() == null) ? slave.getId() : slave.getName();
            this.nameTextView.setText(name);
            String devText = String.format(context.getString(R.string.n_device), count);
            this.countTextView.setText(devText);
        }


    }

    public class DeviceViewHolder extends ChildViewHolder {

        private TextView uidTextView;
        private TextView distanceTextView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            this.uidTextView = (TextView) itemView.findViewById(R.id.uid);
            this.distanceTextView = (TextView) itemView.findViewById(R.id.distance);
        }

        public void bind(Device device) {
            this.uidTextView.setText(device.getUid());
            String distText = String.format(context.getString(R.string.n_distance), device.getDistance());
            this.distanceTextView.setText(distText);
        }

    }
}

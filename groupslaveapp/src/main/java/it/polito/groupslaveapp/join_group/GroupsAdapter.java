package it.polito.groupslaveapp.join_group;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.polito.groupslaveapp.R;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.util.OnItemSelected;

/**
 * Created by giuseppe on 14/10/16.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private int selectedPos = -1;
    private List<Group> groups;
    private OnItemSelected<Group> listener;

    public GroupsAdapter(List<Group> groups, OnItemSelected<Group> listener) {
        this.groups = groups;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.nameTextView.setText(group.getName());
        holder.idTextView.setText(group.getId());
        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void addGroup(Group group) {
        groups.add(group);
        notifyItemInserted(groups.size() - 1);
    }

    public void addGroups(List<Group> groupList) {
        int oldSize = groups.size();
        int newGroupSize = groupList.size();
        groups.addAll(groupList);
        notifyItemRangeInserted(oldSize, newGroupSize);
    }

    public void removeGroup(Group group) {
        int pos = groups.indexOf(group);
        if (pos >= 0) {
            groups.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void removeAll() {
        int oldSize = groups.size();
        groups.clear();
        notifyItemRangeRemoved(0, oldSize);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView idTextView;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            idTextView = (TextView) itemView.findViewById(R.id.id);
            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int oldPos = selectedPos;
                        selectedPos = getAdapterPosition();
                        notifyItemChanged(selectedPos);
                        notifyItemChanged(oldPos);
                        Group group = groups.get(selectedPos);
                        listener.onItemSelected(group, selectedPos);
                    }

                });
            }
        }
    }


}

package it.polito.groupslaveapp.join_group;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.groupslaveapp.R;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.group_action.GroupActionSlaveActivity;
import it.polito.groupslaveapp.util.OnItemSelected;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.polito.groupslaveapp.join_group.JoinGroupContract.Presenter;

/**
 * A placeholder fragment containing a simple view.
 */
public class JoinGroupActivityFragment extends Fragment implements View.OnClickListener, JoinGroupContract.View {

    private Presenter mPresenter;

    private ProgressBar mProgressBar;
    private TextView mMessageTextView;
    private Button mJoinGroupButton;
    private Button mDiscoverButton;
    private RecyclerView mRecyclerView;
    private GroupsAdapter mAdapter;
    private AlertDialog mAlertDialog;

    public JoinGroupActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group, container, false);

        //find views
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageTextView = (TextView) view.findViewById(R.id.messageTextView);
        mJoinGroupButton = (Button) view.findViewById(R.id.joinButton);
        mDiscoverButton = (Button) view.findViewById(R.id.cercaButton);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        //init recycler view
        mAdapter = new GroupsAdapter(new ArrayList<Group>(), new OnItemSelected<Group>() {
            @Override
            public void onItemSelected(Group item, int position) {
                mPresenter.selectGroup(item);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init views and callbacks
        mProgressBar.setVisibility(View.GONE);
        mMessageTextView.setText(String.format(getString(R.string.discovery_message), getString(R.string.discovery_button)));
        mDiscoverButton.setOnClickListener(this);
        mJoinGroupButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void onClick(View v) {
        if (v == mJoinGroupButton) {
            //Create button click handler
            mPresenter.joinClick();

        } else if (v == mDiscoverButton) {
            //Discovery button click handler
            mPresenter.discoverGroups();
        }
    }

    @Override
    public void showMessage(boolean value) {
        int visibility = (value) ? View.VISIBLE : View.GONE;
        mMessageTextView.setVisibility(visibility);
    }

    @Override
    public void showLoadingGroups(boolean loading) {
        int visibility = (loading) ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void showNoGroups() {
        Toast.makeText(getContext(), getString(R.string.no_groups_message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showGroups(List<Group> groups) {
        mAdapter.addGroups(groups);
    }

    @Override
    public void removeAllGroups() {
        mAdapter.removeAll();
    }

    @Override
    public void removeGroup(Group group) {
        mAdapter.removeGroup(group);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getActivity(), getString(R.string.join_search_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void joinButtonEnabled(boolean enabled) {
        mJoinGroupButton.setEnabled(enabled);
    }

    @Override
    public void showDialog(boolean show) {
        if (show) {
            mAlertDialog = new AlertDialog.Builder(getContext())
                    .setTitle("Richiesta inviata")
                    .setMessage("In attesa del master ...")
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Toast.makeText(getActivity(), "Richiesta di partecipazione annullata", Toast.LENGTH_SHORT).show();
                            mPresenter.cancelJoin();
                        }
                    })
                    .show();
        } else {
            checkNotNull(mAlertDialog);
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    @Override
    public void navigateToGroupAction(String groupId) {
        Intent intent = new Intent(getActivity(), GroupActionSlaveActivity.class);
        intent.putExtra(GroupActionSlaveActivity.GROUP_ID_EXTRA, groupId);
        startActivity(intent);
    }


    public static JoinGroupActivityFragment getInstance() {
        return new JoinGroupActivityFragment();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        checkNotNull(presenter);
        mPresenter = presenter;
    }
}
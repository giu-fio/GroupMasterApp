package it.polito.groupslaveapp.data.source;

import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.data.Slave;
import it.polito.groupslaveapp.util.AbstractChildEventListener;
import it.polito.groupslaveapp.util.OnCompleteListener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by giuseppe on 14/10/16.
 */

public class FirebaseGroupDataSource implements GroupDataSource {

    private static final String TAG = FirebaseGroupDataSource.class.getSimpleName();
    private DatabaseReference mRef;
    private Query mQuery;
    private FirebaseAuth mAuth;
    private String mName;
    private String mUid;


    private ChildEventListener mChildEventListener;

    public FirebaseGroupDataSource() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public String saveDevice(String deviceName) {
        mUid = mAuth.getCurrentUser().getUid();
        mName = deviceName;
        //create user location
        final Map<String, Object> valMap = ImmutableMap.<String, Object>builder()
                .put("devices/" + mUid + "/name", deviceName)
                .put("devices/" + mUid + "/id", mUid)
                .build();

        mRef.updateChildren(valMap);
        return mUid;
    }

    @Override
    public void searchGroup(final DataSource.FindGroupCallback callback) {

        mChildEventListener = new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("TEST", "onChildAdded: " + dataSnapshot.exists());
                if (dataSnapshot.exists()) {
                    String groupId = dataSnapshot.getKey();
                    String groupName = dataSnapshot.child("name").getValue(String.class);
                    // String masterUid = dataSnapshot.child("master").getValue(String.class);
                    callback.onGroupFound(new Group(groupName, groupId));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String groupId = dataSnapshot.getKey();
                    String groupName = dataSnapshot.child("name").getValue(String.class);
                    // String masterUid = dataSnapshot.child("master").getValue(String.class);
                    callback.onGroupLost(new Group(groupName, groupId));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
                callback.onError(databaseError.toException());
            }
        };
        mQuery = mRef.child("group").orderByChild("discovering").equalTo(true);
        mQuery.addChildEventListener(mChildEventListener);
      /*  mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onDataChange: " + databaseError);
            }
        });*/
    }

    @Override
    public void stopSearchGroup() {
        mQuery.removeEventListener(mChildEventListener);
    }

    @Override
    public void joinGroup(final Group group, final OnCompleteListener<Group> listener) {
        checkNotNull(group);
        mRef.child("members_discovering").child(group.getId()).child(mUid).setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                mRef.child("group_members").child(group.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: Exist: " + dataSnapshot.exists() + " Data: " + dataSnapshot + " UID: " + mUid);
                        if (dataSnapshot.exists()) {
                            Log.d(TAG, "HAS CH: " + dataSnapshot.hasChild(mUid));
                            listener.onComplete(group, dataSnapshot.hasChild(mUid));
                            mRef.child("group_members").child(group.getId()).removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: ", databaseError.toException());
                        listener.onComplete(group, false);
                        mRef.child("group_members").child(group.getId()).removeEventListener(this);
                    }
                });
            }
        });
    }

    @Override
    public void cancelJoin(final Group selectedGroup, final OnCompleteListener<Group> listener) {

        checkNotNull(selectedGroup);
        mRef.child("members_discovering").child(selectedGroup.getId()).child(mUid).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                listener.onComplete(selectedGroup, databaseError != null);
            }
        });
    }

    @Override
    public void loadGroup(final String groupId, final OnCompleteListener<Group> onCompleteListener) {
        mRef.child("group").child(groupId).addListenerForSingleValueEvent(new AbstractValueEventListener(onCompleteListener) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Group group = new Group(dataSnapshot.child("name").getValue(String.class), groupId);
                    mRef.child("group_members").child(groupId).addListenerForSingleValueEvent(new AbstractValueEventListener(onCompleteListener) {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Map<String, String> valueMap = (Map<String, String>) dataSnapshot.getValue();
                            final int[] count = new int[1];
                            final int members = valueMap.size();
                            for (final String id : valueMap.keySet()) {
                                mRef.child("devices").child(id).addListenerForSingleValueEvent(new AbstractValueEventListener(onCompleteListener) {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Slave slave = new Slave(dataSnapshot.child("name").getValue(String.class), id);
                                        if (valueMap.get(id).equalsIgnoreCase("master")) {
                                            group.setMaster(slave);
                                        }
                                        group.addSlave(slave);
                                        mRef.child("group_devices").child(groupId).addListenerForSingleValueEvent(new AbstractValueEventListener(onCompleteListener) {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, Object> deviceValueMap = (Map<String, Object>) dataSnapshot.getValue();
                                                for (String uid : deviceValueMap.keySet()) {
                                                    Device device = new Device(uid);
                                                    device.setDistance(Double.MAX_VALUE);
                                                    group.addDevice(device);

                                                }
                                                if (count[0] == members - 1) {
                                                    onCompleteListener.onComplete(group, true);
                                                }
                                                count[0]++;

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                } else {
                    onCompleteListener.onComplete(null, false);
                }
            }
        });
    }

    @Override
    public void sendVisibleDevices(String groupId, List<Device> devices) {
        Map<String, Object> updateMap = new HashMap<>();
        for (Device device : devices) {
            updateMap.put(device.getUid(), device.getDistance());
        }
        updateMap.put("timestamp", ServerValue.TIMESTAMP);
        mRef.child("group_slave_messages").child(groupId).child(mAuth.getCurrentUser().getUid()).setValue(updateMap);
    }

    @Override
    public void start() {
        checkNotNull(mAuth.getCurrentUser());
    }

    @Override
    public void stop() {
        // TODO: 14/10/16
    }

    private abstract class AbstractValueEventListener implements ValueEventListener {

        private OnCompleteListener<Group> onCompleteListener;

        public AbstractValueEventListener(OnCompleteListener<Group> onCompleteListener) {
            this.onCompleteListener = onCompleteListener;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            onCompleteListener.onComplete(null, false);
        }
    }
}
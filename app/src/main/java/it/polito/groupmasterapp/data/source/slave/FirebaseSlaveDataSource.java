package it.polito.groupmasterapp.data.source.slave;

import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.util.AbstractChildEventListener;
import it.polito.groupmasterapp.util.MyFirebaseParser;
import it.polito.groupmasterapp.util.OnCompleteListener;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.polito.groupmasterapp.data.source.DevicesDataSource.FindSlavesCallback;
import static it.polito.groupmasterapp.data.source.DevicesDataSource.SlaveMessageCallback;

/**
 * Created by giuseppe on 13/10/16.
 */

public class FirebaseSlaveDataSource implements SlaveDataSource {

    private static final String TAG = FirebaseSlaveDataSource.class.getSimpleName();

    private DatabaseReference mRef;
    private Query mMessagesQuery;
    private FirebaseAuth mAuth;

    private FindSlavesCallback mCallback;

    private String mGroupName;
    private String mGroupUid;
    private String mMasterUid;


    private ChildEventListener mChildEventListener;
    private List<ChildEventListener> mSlaveListenerList;

    public FirebaseSlaveDataSource() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public String createGroup(String deviceName, String groupName) {

        mMasterUid = mAuth.getCurrentUser().getUid();
        mGroupName = groupName;

        //create user location
        final Map<String, Object> valMap = ImmutableMap.<String, Object>builder().
                put("devices/" + mMasterUid + "/name", deviceName).
                put("devices/" + mMasterUid + "/id", mMasterUid).
                build();


        mRef.updateChildren(valMap);

        //create group location
        DatabaseReference groupRef = mRef.child("group").push();
        mGroupUid = groupRef.getKey();

        final Map<String, Object> groupValMap = ImmutableMap.<String, Object>builder().
                put("master", mMasterUid).
                put("name", groupName).
                put("discovering", true).
                put("active", true).
                build();
        groupRef.setValue(groupValMap);

        //group members location
        mRef.child("members_discovering").child(mGroupUid).child(mMasterUid).setValue(true);
        return mGroupUid;
    }

    @Override
    public void saveGroup(final Group group, final OnCompleteListener<Group> onCompleteListener) {

        final Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("group/" + group.getId() + "/discovering", false);
        updateMap.put("members_discovering/" + group.getId(), null);
        updateMap.put("group_members/" + group.getId() + "/" + mAuth.getCurrentUser().getUid(), "master");

        for (Slave slave : group.getSlaves()) {
            updateMap.put("group_members/" + group.getId() + "/" + slave.getId(), "slave");
        }
        for (Device device : group.getDevices()) {
            updateMap.put("group_devices/" + group.getId() + "/" + device.getUid(), true);
        }

        mRef.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                onCompleteListener.onComplete(group, databaseError != null);
            }
        });
    }


    @Override
    public void startSlavesDiscovering(@NonNull final FindSlavesCallback callback) {


        mChildEventListener = new AbstractChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.getKey().equals(mMasterUid)) {
                    final String slaveUid = dataSnapshot.getKey();
                    mRef.child("members").child(slaveUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue(String.class);
                            callback.onSlaveFound(new Slave(name, slaveUid));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "startSlavesDiscovering", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getKey().equals(mMasterUid)) {
                    final String slaveUid = dataSnapshot.getKey();
                    mRef.child("members").child(slaveUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue(String.class);
                            callback.onSlaveLost(new Slave(name, slaveUid));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "startSlavesDiscovering", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "startSlavesDiscovering", databaseError.toException());
            }
        };
        mRef.child("members_discovering").child(mGroupUid).addChildEventListener(mChildEventListener);
    }

    @Override
    public void stopSlavesDiscovering() {
        mRef.child("members_discovering").child(mGroupUid).removeEventListener(mChildEventListener);
    }


    @Override
    public void updateGroup(final Group group, Map<Slave, List<Device>> groupCompositionMap, final OnCompleteListener<Group> callback) {
        checkNotNull(group);
        checkNotNull(groupCompositionMap);
        Map<String, Object> updateMap = new HashMap<>();

        for (Slave slave : groupCompositionMap.keySet()) {
            Map<String, Object> valueMap = new HashMap<>();
            String slaveId = slave.getId();
            Log.d(TAG, "updateGroup: " + groupCompositionMap);
            List<Device> devices = groupCompositionMap.get(slave);
            valueMap.put("devices_count", devices.size());
            //updateMap.put(group.getId() + "/" + slaveId + "/devices_count", );
            for (Device device : devices) {
                //updateMap.put(group.getId() + "/" + slaveId + "/" + device.getUid() + "/distance", device.getDistance());
                valueMap.put(device.getUid(), device.getDistance());
            }
            updateMap.put(group.getId() + "/" + slaveId, valueMap);
        }
        updateMap.put(group.getId() + "/timestamp", ServerValue.TIMESTAMP);

        mRef.child("group_composition").updateChildren(updateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                callback.onComplete(group, databaseError != null);
            }
        });
    }

    @Override
    public void closeGroup(Group group) {
        mRef.child("group").child(group.getId()).child("active").setValue(false);

        /*  Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("group/" + group.getId(), null);
        updateMap.put("group_composition/" + group.getId(), null);
        updateMap.put("group_devices/" + group.getId(), null);
        updateMap.put("group_members/" + group.getId(), null);
        updateMap.put("group_slave_messages/" + group.getId(), null);
        mRef.updateChildren(updateMap);*/

    }

    @Override
    public void notifyLostDevice(Group group, final Device device, String lastSlaveId, long lastTimestamp, final OnCompleteListener<Device> callback) {
        checkNotNull(device);
        checkNotNull(lastSlaveId);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("last_slave", lastSlaveId);
        valueMap.put("last_distance", device.getDistance());
        valueMap.put("last_timestamp", ServerValue.TIMESTAMP);
        mRef.child("group_lost_devices").child(group.getId()).child(device.getUid()).setValue(valueMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (callback != null) callback.onComplete(device, databaseError != null);
            }
        });
    }

    @Override
    public void notifyDeviceFound(Group group, final Device device, String slaveId, final OnCompleteListener<Device> callback) {
        checkNotNull(device);
        checkNotNull(slaveId);

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("slave", slaveId);
        valueMap.put("distance", device.getDistance());
        valueMap.put("timestamp", ServerValue.TIMESTAMP);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("group_lost_devices/" + group.getId() + "/" + device.getUid(), null);
        updateMap.put("group_devices_found/" + group.getId() + "/" + device.getUid(), valueMap);

       /* mRef.child("group_lost_device").child(group.getId()).child(device.getUid()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                callback.onComplete(null, databaseError != null);
            }
        });*/

        mRef.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (callback != null) callback.onComplete(device, databaseError != null);
            }
        });

    }


    @Override
    public void startSlaveMessagesListening(@NonNull final Group group, @NonNull final SlaveMessageCallback callback) {

        mRef.child("group_devices").child(group.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSlaveListenerList = new ArrayList<>((int) dataSnapshot.getChildrenCount());
                Map<String, Object> valueMap = (Map<String, Object>) dataSnapshot.getValue();
                for (String slaveId : valueMap.keySet()) {
                    final Slave slave = new Slave(null, slaveId);
                    mMessagesQuery = mRef.child("group_slave_messages").child(group.getId()).child(slaveId)
                            .orderByChild("timestamp").limitToLast(1);

                    mSlaveListenerList.add(mMessagesQuery.addChildEventListener(
                            new AbstractChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()) {
                                        message(dataSnapshot);
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()) {
                                        message(dataSnapshot);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    callback.onError(databaseError.toException());
                                }

                                private void message(DataSnapshot dataSnapshot) {
                                    List<Device> deviceList = new LinkedList<>();
                                    long timestamp = 0L;
                                    for (DataSnapshot deviceDS : dataSnapshot.getChildren()) {
                                        String deviceId = deviceDS.getKey();
                                        if (deviceId.equals("timestamp")) {
                                            timestamp = deviceDS.getValue(Long.class);
                                        } else {
                                            double distance = MyFirebaseParser.parseDouble(deviceDS.getValue());
                                            Device device = new Device(deviceId, null);
                                            device.setDistance(distance);
                                            deviceList.add(device);
                                        }
                                    }
                                    callback.onSlaveMessage(slave, deviceList, timestamp);
                                }
                            }));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });

    }

    @Override
    public void stopSlaveMessagesListening() {
        for (ChildEventListener listener : mSlaveListenerList) {
            mMessagesQuery.removeEventListener(listener);
        }
        mSlaveListenerList = null;
    }

    @Override
    public void startBackgroundSlaveListening() {

    }

    @Override
    public void stopBackgroundSlaveListening() {

    }


    @Override
    public void start() {
        checkNotNull(mAuth.getCurrentUser());
    }

    @Override
    public void stop() {
        // TODO: 13/10/16
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

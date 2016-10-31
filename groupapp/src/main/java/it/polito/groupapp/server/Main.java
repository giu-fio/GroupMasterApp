package it.polito.groupapp.server;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.groupapp.server.util.MySender;

import static it.polito.groupapp.server.MasterListeners.DevicesFoundListener;
import static it.polito.groupapp.server.MasterListeners.GroupCompositionListener;
import static it.polito.groupapp.server.MasterListeners.LostDevicesListener;
import static it.polito.groupapp.server.util.FirebaseUtil.getId;
import static it.polito.groupapp.server.util.FirebaseUtil.initFirebase;

/**
 * Created by giuseppe on 19/10/16.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {

        initFirebase();
        final String keyId = getId();
        if (keyId == null) {
            System.out.println("Key not found!");
            System.exit(-1);
        }

        System.out.println("Start listening...");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Map<String, List<Object>> groupListenersMap = new HashMap<>();


        ref.child("group").addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Boolean active = dataSnapshot.child("active").getValue(Boolean.class);
                        String groupId = dataSnapshot.getKey();
                        if (active != null && active) {
                            if (!groupListenersMap.containsKey(groupId)) {
                                System.out.println("GROUP " + groupId + " aggiunto");
                                SlaveListener slaveListener = new SlaveListener(groupId, keyId);
                                LostDevicesListener lostDeviceListener = new LostDevicesListener(groupId, keyId);
                                DevicesFoundListener devicesFoundListener = new DevicesFoundListener(groupId, keyId);
                                GroupCompositionListener groupCompositionListener = new GroupCompositionListener(groupId, keyId);
                                ref.child("group_slave_messages").child(groupId).addChildEventListener(slaveListener);
                                ref.child("group_lost_devices").child(groupId).addChildEventListener(lostDeviceListener);
                                ref.child("group_devices_found").child(groupId).addChildEventListener(devicesFoundListener);
                                ref.child("group_composition").child(groupId).addValueEventListener(groupCompositionListener);
                                List<Object> listeners = new LinkedList<>();
                                listeners.add(slaveListener);
                                listeners.add(lostDeviceListener);
                                listeners.add(devicesFoundListener);
                                listeners.add(groupCompositionListener);
                                groupListenersMap.put(groupId, listeners);
                            }
                        }

                    }


                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Boolean active = dataSnapshot.child("active").getValue(Boolean.class);
                        String groupId = dataSnapshot.getKey();
                        if (active != null && !active) {
                            removeListener(dataSnapshot);
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        removeListener(dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                    private void addListener(DataSnapshot dataSnapshot) {

                        Boolean active = dataSnapshot.child("active").getValue(Boolean.class);
                        String groupId = dataSnapshot.getKey();
                        if (active != null && active) {
                            if (!groupListenersMap.containsKey(groupId)) {
                                System.out.println("GROUP " + groupId + " aggiunto");
                                SlaveListener slaveListener = new SlaveListener(groupId, keyId);
                                LostDevicesListener lostDeviceListener = new LostDevicesListener(groupId, keyId);
                                DevicesFoundListener devicesFoundListener = new DevicesFoundListener(groupId, keyId);
                                GroupCompositionListener groupCompositionListener = new GroupCompositionListener(groupId, keyId);
                                ref.child("group_slave_messages").child(groupId).addChildEventListener(slaveListener);
                                ref.child("lost_devices").child(groupId).addChildEventListener(lostDeviceListener);
                                ref.child("devices_found").child(groupId).addChildEventListener(devicesFoundListener);
                                ref.child("group_composition").child(groupId).addValueEventListener(groupCompositionListener);
                                List<Object> listeners = new LinkedList<>();
                                listeners.add(slaveListener);
                                listeners.add(lostDeviceListener);
                                listeners.add(devicesFoundListener);
                                listeners.add(groupCompositionListener);
                                groupListenersMap.put(groupId, listeners);

                            }
                        } else {
                            Map<String, String> dataMap = new HashMap<>();
                            dataMap.put("type", "close");
                            dataMap.put("group", groupId);
                            sendToSlaves(ref, groupId, dataMap, keyId);
                            removeListener(dataSnapshot);
                        }
                    }

                    private void removeListener(DataSnapshot dataSnapshot) {
                        String groupId = dataSnapshot.getKey();
                        System.out.println("CLOSE GROUP " + groupId);
                        Map<String, String> messageMap = new HashMap<>();
                        messageMap.put("type", "close_group");
                        messageMap.put("group", groupId);
                        sendToSlaves(ref, groupId, messageMap, keyId);

                        if (groupListenersMap.containsKey(groupId)) {
                            List<Object> listeners = groupListenersMap.get(groupId);
                            if (listeners != null) {
                                Iterator<Object> it = listeners.iterator();
                                ref.child("group_slave_messages").child(groupId).removeEventListener((ChildEventListener) it.next());
                                ref.child("lost_devices").child(groupId).removeEventListener((ChildEventListener) it.next());
                                ref.child("devices_found").child(groupId).removeEventListener((ChildEventListener) it.next());
                                ref.child("group_composition").child(groupId).removeEventListener((ValueEventListener) it.next());
                            }
                            groupListenersMap.remove(groupId);
                        }

                    }
                }
        );


        Thread.currentThread().join(10000);
    }

    public static void sendToSlaves(DatabaseReference ref, String groupId, final Map<String, String> messageMap, final String keyId) {
        ref.child("group_members").child(groupId).orderByValue().equalTo("slave").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot masterDS : dataSnapshot.getChildren()) {
                    ref.child("devices").child(masterDS.getKey()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String token = dataSnapshot.getValue(String.class);
                                MySender.send(token, messageMap, keyId);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

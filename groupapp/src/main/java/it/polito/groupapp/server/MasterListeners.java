package it.polito.groupapp.server;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import it.polito.groupapp.server.util.MyParser;

import static it.polito.groupapp.server.Main.sendToSlaves;

/**
 * Created by giuseppe on 27/10/16.
 */
public class MasterListeners {

    public static class LostDevicesListener extends AbstractChildEventListener {
        private final String groupId;
        private final String keyId;
        private DatabaseReference ref;

        public LostDevicesListener(String groupId, String keyId) {

            this.groupId = groupId;
            this.keyId = keyId;
            ref = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            sendMessage(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            sendMessage(dataSnapshot);
        }

        private void sendMessage(DataSnapshot dataSnapshot) {
                Map<String, String> messageMap = new HashMap<>();
                Map<String, Object> valueMap = (Map<String, Object>) dataSnapshot.getValue();
                String devId = dataSnapshot.getKey();
                messageMap.put("type", "lost_device");
                messageMap.put("device", devId);
                for (String key : valueMap.keySet()) {
                    messageMap.put(key, valueMap.get(key).toString());
                }
                sendToSlaves(ref, groupId, messageMap, keyId);
        }


    }

    public static class DevicesFoundListener extends AbstractChildEventListener {
        private final String groupId;
        private final String keyId;
        private DatabaseReference ref;

        public DevicesFoundListener(String groupId, String keyId) {
            this.groupId = groupId;
            this.keyId = keyId;
            ref = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            sendMessage(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            sendMessage(dataSnapshot);
        }

        private void sendMessage(DataSnapshot dataSnapshot) {
            ////System.out.println("DEVICES FOUND " + dataSnapshot);

                Map<String, String> messageMap = new HashMap<>();
                Map<String, Object> valueMap = (Map<String, Object>) dataSnapshot.getValue();
                String devId = dataSnapshot.getKey();
                messageMap.put("type", "device_found");
                messageMap.put("device", devId);

                for (String key : valueMap.keySet()) {
                    messageMap.put(key, valueMap.get(key).toString());
                }
                sendToSlaves(ref, groupId, messageMap, keyId);

        }


    }

    public static class GroupCompositionListener implements ValueEventListener {
        private final String groupId;
        private final String keyId;
        private DatabaseReference ref;

        public GroupCompositionListener(String groupId, String keyId) {
            this.groupId = groupId;
            this.keyId = keyId;
            ref = FirebaseDatabase.getInstance().getReference();
        }


        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ////System.out.println("GROUP COMPOSITION " + dataSnapshot);


            if (!dataSnapshot.exists()) return;


            Map<String, Object> valueMap = new HashMap<>((Map<String, Object>) dataSnapshot.getValue());
            String timestamp = valueMap.remove("timestamp").toString();

            for (String slaveId : valueMap.keySet()) {
                Map<String, String> messageMap = new HashMap<>();
                messageMap.put("type", "group_update");
                messageMap.put("group", groupId);
                messageMap.put("timestamp", timestamp);
                messageMap.put("slave", slaveId);
                Map<String, Object> devMap = (Map<String, Object>) valueMap.get(slaveId);
                long count = (long) devMap.remove("devices_count");
                if (count > 0) {
                   // System.out.println("count " + count + "devMap " + devMap);
                    for (String deviceId : devMap.keySet()) {
                       // System.out.println("TYPE " + devMap.get(deviceId).getClass().getSimpleName());
                        Double distance = MyParser.parseDouble(devMap.get(deviceId));
                        messageMap.put(deviceId, distance.toString());
                    }
                }
                //System.out.println("SLAVE "+messageMap);
                sendToSlaves(ref, groupId, messageMap, keyId);
            }
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private static abstract class AbstractChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


}

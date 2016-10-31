package it.polito.groupapp.server;

import com.google.firebase.database.*;
import it.polito.groupapp.server.util.MySender;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppe on 27/10/16.
 */
public class SlaveListener implements ChildEventListener {
    private final String groupId;
    private final String keyId;
    private DatabaseReference ref;

    public SlaveListener(String key, String keyId) {
        this.groupId = key;
        this.keyId = keyId;
        ref = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        slaveMessage(dataSnapshot);
    }

    private void slaveMessage(DataSnapshot dataSnapshot) {

        Map<String, Object> valueMap = (Map<String, Object>) dataSnapshot.getValue();
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("type", "update");
        messageMap.put("slave_id", dataSnapshot.getKey());
        for (String id : valueMap.keySet()) {
            messageMap.put(id, valueMap.get(id).toString());
        }

        ref.child("group_members").child(groupId).orderByValue().equalTo("master").addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        slaveMessage(dataSnapshot);
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

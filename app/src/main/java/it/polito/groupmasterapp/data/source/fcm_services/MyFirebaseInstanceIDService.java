package it.polito.groupmasterapp.data.source.fcm_services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceIDSer";

    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth == null || auth.getCurrentUser() == null) {
            Log.d(TAG, "sendRegistrationToServer: null user");
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("devices");
        ref.child(auth.getCurrentUser().getUid()).child("token").setValue(refreshedToken);
    }
}

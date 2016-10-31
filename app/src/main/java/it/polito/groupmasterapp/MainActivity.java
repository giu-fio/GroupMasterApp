package it.polito.groupmasterapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.HttpURLConnection;
import java.net.URL;

import it.polito.groupmasterapp.create_group.CreateNewGroupActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    Class intentClass = CreateNewGroupActivity.class;
//    Class intentClass = CreateNewGroupActivity.class;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        new NetworkTestTask().execute(getApplicationContext());

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            HttpURLConnection urlc = null;
            try {
                urlc = (HttpURLConnection) (new URL("https://www.google.it").openConnection());
                urlc.setInstanceFollowRedirects(false);
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();

                Log.d(TAG, "hasActiveInternetConnection: " + urlc.getResponseCode());
                Log.d(TAG, "hasActiveInternetConnection: " + urlc.getHeaderField("Location"));

                return (urlc.getResponseCode() == 200);

            } catch (Exception e) {
                Log.e(TAG, "Error checking internet connection", e);
            } finally {
                if (urlc != null) urlc.disconnect();
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

    private class NetworkTestTask extends AsyncTask<Context, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Context... params) {
            return hasActiveInternetConnection(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) connected();
            else {
                Toast.makeText(MainActivity.this, "Nessuna connessione disponibile! ", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000L);
            }
        }
    }

    private void connected() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "TOKEN: ");
                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("devices");
                            ref.child(mAuth.getCurrentUser().getUid()).child("token").setValue(refreshedToken, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Log.d(TAG, "onComplete() called with: databaseError = [" + databaseError + "], databaseReference = [" + databaseReference + "]");
                                }
                            });
                            Intent intent = new Intent(MainActivity.this, intentClass);
                            startActivity(intent);
                            finish();
                        }

                    }
                });


        Intent intent = new Intent(this, intentClass);
        startActivity(intent);
        Log.d(TAG, "onCreate: ");

    }

}

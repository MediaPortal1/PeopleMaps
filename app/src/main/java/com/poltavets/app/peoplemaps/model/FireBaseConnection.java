package com.poltavets.app.peoplemaps.model;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FireBaseConnection {

    private Context context;

    private final String FIREBASE_URL="https://peoplemaps-1324.firebaseio.com/";
    private final String FIREBASE_USERS="https://peoplemaps-1324.firebaseio.com/users";
//  private final String FIREBASE_TOKEN="wuAS09b1mK9C8jSQJ4HjfJzqKtbDPSyqgdVL10QO";

    private String token;
    private DatabaseReference rootRef; //ROOT
    private DatabaseReference state; //Connection state
    private DatabaseReference online; //Online users
    private FirebaseUser user;
    private double latitude,longitude;


        /*************CONSTRUCTOR*************/
    public FireBaseConnection(Context context,String token,double latitude,double longitude){
        this.context = context;
        this.token=token;
        this.latitude=latitude;
        this.longitude=longitude;
        Firebase.setAndroidContext(context);
        createRef();
    }

    private void createRef() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        state=rootRef.child("/.info/connected");
        online=rootRef.child("users");
        state.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean status=dataSnapshot.getValue(Boolean.class);
                if(status){
                    Log.i("State","Connected");
                    DatabaseReference con = online.child(user.getUid());

                    Map<String,Object> map=new HashMap<String, Object>();
                    map.put("name",user.getDisplayName());
                    map.put("lat",Double.toString(latitude));
                    map.put("long",Double.toString(longitude));
                    map.put("status",true);
                    con.setValue(map);
                    con.child("status").onDisconnect().setValue(false);
                }else {
                    Log.i("State","not connected");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser();
            }
        });
        auth.signInWithCredential(credential);

    }

}

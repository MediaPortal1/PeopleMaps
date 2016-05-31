package com.poltavets.app.peoplemaps.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.firebase.client.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.poltavets.app.peoplemaps.R;
import com.poltavets.app.peoplemaps.presenter.MainActivityInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FireBaseConnection {

    private Context context; // APP CONTEXT
    private MainActivityInterface presenter; // PRESENTER | MainActivityPresenter

    private String token; // TOKEN OF GOOGLE USER
    private DatabaseReference rootRef; //ROOT CAT
    private DatabaseReference state; //Connection state CAT
    private DatabaseReference online; //Online users CAT
    private FirebaseUser user; // USER
    private double latitude,longitude; // Location DATA
    private ArrayList<Map<String,Object>> itemlist; // USERS ONLINE ARRAY
    private SimpleAdapter adapter; // USERS ONLINE LISTITEM ADAPTER


        /*************CONSTRUCTOR*************/
    public FireBaseConnection(final Context context, final MainActivityInterface presenter, String token, double latitude, double longitude){
        /*********SET************/
        this.context = context;
        this.token=token;
        this.latitude=latitude;
        this.longitude=longitude;
        this.presenter=presenter;

        Firebase.setAndroidContext(context);//SET CONTEXT APP

        createRef(); // CREATE CONNECTION TO FIREBASE
    }


    /*
    LOAD ONLINE USERS FROM FIREBASE
     */
    public void loadUsers(){
        if(user!=null) {
            DownloadOnlineUsers connection = new DownloadOnlineUsers();
            connection.execute();
        }
    }

    /*
     CREATE CONNECTION WITH FIREBASE AND AUTH
     */
    private void createRef() {

        /*
        INIT CATS
         */
        rootRef = FirebaseDatabase.getInstance().getReference();
        state=rootRef.child("/.info/connected");
        online=rootRef.child("users");

        //ON CONNECT | LOGIN BY USER ID/REGISTER NEW USER
        state.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean status=dataSnapshot.getValue(Boolean.class);
                if(status && user!=null){
                    Log.i("State","Connected");

                    PeopleMapsUser mapsuser=new PeopleMapsUser(user.getDisplayName(),latitude,longitude);

                    DatabaseReference con = online.child(user.getUid());
                    con.setValue(mapsuser);
                    con.child("status").onDisconnect().setValue(false); // WHEN CONNECTION IS DONE, SET STATUS=FALSE
                    loadUsers();
                }else {
                    Log.i("State","not connected");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //ON ITEMS CHANGED
        online.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {} //UPDATE USER LIST

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}//UPDATE USER LIST

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}//UPDATE USER LIST

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}//UPDATE USER LIST

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //AUTH USER
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user=firebaseAuth.getCurrentUser(); // SET AUTH USER
            }
        });
        //SIGN IN
        auth.signInWithCredential(credential);


    }
    private void getOnlineQueryUsers(){
        /*
        GET USERS ORDER BY NAME
         */
        Query result=online.orderByChild("name");

        result.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){

                PeopleMapsUser usersnapshot=child.getValue(PeopleMapsUser.class);//GET PEOPLEMAPSUSER FROM FIREBASE
                if(usersnapshot.isStatus()) { // IS USER ONLINE? (STATUS==TRUE)
                    Map<String, Object> item = new HashMap<String, Object>();
                    if (usersnapshot.getName() == user.getDisplayName())//THIS USER ARE YOU?
                        item.put("name", usersnapshot.getName() + " (you)"); //YES
                    else
                        item.put("name", usersnapshot.getName()); //NO
                    item.put("lat", usersnapshot.getLatitude());
                    item.put("long", usersnapshot.getLongitude());
                    itemlist.add(item); //ADD USER TO LIST
                    adapterUpdate();  //UPDATE USER LIST
                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //GET USER
        result.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapterUpdate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {adapterUpdate();} //UPDATE LIST
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {adapterUpdate();}//UPDATE LIST
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {adapterUpdate(); }//UPDATE LIST
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void adapterUpdate(){
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
            presenter.setListVisible();
        }
    }

    public int getUserCount(){
        return itemlist==null ? 0:itemlist.size(); //GET ITEMLIST.SIZE
    }

    public double getLatitude(int id) {
        return (Double)itemlist.get(id).get("lat"); //GET LATITUDE
    }

    public double getLongitude(int id) {
        return (Double)itemlist.get(id).get("long"); //GET LONGITUDE
    }

    public String getName(int id) {
        return (String)itemlist.get(id).get("name"); //GET NAME
    }

    /*****************DOWNLOAD ONLINE USERS***************/
    private class DownloadOnlineUsers extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            presenter.setOnProgress();
            itemlist=new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getOnlineQueryUsers();
            adapter=new SimpleAdapter(context,itemlist,
                    R.layout.listitem_postitions_main,
                    new String[]{"name"},new int[]{R.id.textView_medium_listitem_main});
            adapter.setViewBinder(new MyBinder());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            presenter.setAdapter(adapter);
        }
    }

    /*****************DOWNLOAD PROFILE IMAGE***************/
    // DONT USING
    private static class DownloadFile extends AsyncTask<String,Void,Bitmap>{
        private PeopleMapsUser user;

        public DownloadFile(PeopleMapsUser user) {
            this.user = user;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bmImg=null;
            URL myFileUrl =null;
            try {
                myFileUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                //int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
                // imageLoadedHandler.sendEmptyMessage(FAILED);
            } catch (IOException e) {
                // imageLoadedHandler.sendEmptyMessage(FAILED);
            }
            return bmImg;
        }
    }


    /*****************PEOPLE MAPS***************/
     /****************USER ID CLASS ***********/
    public static class PeopleMapsUser{

        private String name;
        private double latitude,longitude;
        private boolean status=true;

        public PeopleMapsUser(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public PeopleMapsUser() { }

        public String getName() {return name;}

        public void setName(String name) {this.name = name;}

        public double getLatitude() {return latitude;}

        public void setLatitude(double latitude) {this.latitude = latitude;}

        public double getLongitude() {return longitude;}

        public void setLongitude(double longitude) { this.longitude = longitude;}

        public boolean isStatus() {return status;}

        public void setStatus(boolean status) {this.status = status;}
     }

    /*****************MY BINDER***************/
    /****************FOR IMAGES FROM URL ***********/
    private class MyBinder implements SimpleAdapter.ViewBinder{
        @Override
        public boolean setViewValue(android.view.View view, Object data, String textRepresentation) {
            if((view instanceof ImageView) & (data instanceof Bitmap))
            {
                ImageView iv = (ImageView) view;
                Bitmap bm = (Bitmap) data;
                iv.setImageBitmap(bm);
                return true;
            }
            return false;
        }
    }
}

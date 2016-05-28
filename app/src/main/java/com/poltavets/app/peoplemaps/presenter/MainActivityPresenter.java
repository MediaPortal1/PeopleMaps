package com.poltavets.app.peoplemaps.presenter;

import android.content.Context;

/**
 * Created by Poltavets on 27.05.2016.
 */
public class MainActivityPresenter implements MainActivityInterface {

    private Context context;
    private String userName,userId;

    public MainActivityPresenter(Context context, String userName, String userId) {
        this.context = context;
        this.userName = userName;
        this.userId = userId;
    }
    private void checkGoogleId(String id,String name){
        //TODO:
        if(isUserRegister(id)){
            initUser(id,name);
        }
        else{
            registerUser(id,name);
        }
    }
    private boolean isUserRegister(String id){

        return true;//TODO:
    }
    private void registerUser(String id,String name){
        //TODO: REGiSTER
    }
    private void initUser(String id,String name){
        //TODO: INIT USER
    }
}

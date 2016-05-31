package com.poltavets.app.peoplemaps.presenter;


import android.content.Intent;
import android.widget.BaseAdapter;

public interface MainActivityInterface {
    void setAdapter(BaseAdapter adapter);
    void setListVisible();
    Intent getMapIntent(int position);
    void setOnProgress();
    void refreshUserList();
}

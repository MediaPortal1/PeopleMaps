package com.poltavets.app.peoplemaps.view;

import android.widget.BaseAdapter;


public interface MainView {
    void setAdapter(BaseAdapter adapter);
    void setListEnabled();
    void setListOnProgress();
}

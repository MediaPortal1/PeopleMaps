package com.poltavets.app.peoplemaps.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.poltavets.app.peoplemaps.R;
import com.poltavets.app.peoplemaps.presenter.MainActivityInterface;
import com.poltavets.app.peoplemaps.presenter.MainActivityPresenter;


public class MainActivity extends AppCompatActivity implements MainView{

    /*
    VIEW
     */
    private ListView listview; //MAIN LISTVIEW

    /*
    PRESENTER
     */
    private MainActivityInterface presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_main));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //

        /*
        INIT VIEW
         */
        listview=(ListView)findViewById(R.id.listView_main);
        //

        /*
        PRESENTER
         */
        Intent loginIntent=getIntent();
        presenter=new MainActivityPresenter(this,loginIntent.getStringExtra("name"),loginIntent.getStringExtra("id"));
        //

    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        listview.setAdapter(adapter);
    }
}

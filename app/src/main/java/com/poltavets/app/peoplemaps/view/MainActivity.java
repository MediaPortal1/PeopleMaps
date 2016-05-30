package com.poltavets.app.peoplemaps.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.poltavets.app.peoplemaps.R;
import com.poltavets.app.peoplemaps.presenter.MainActivityInterface;
import com.poltavets.app.peoplemaps.presenter.MainActivityPresenter;


public class MainActivity extends AppCompatActivity implements MainView{

    /*
    VIEW
     */
    private ListView listview; //MAIN LISTVIEW
    private TextView textView; //MAIN TEXTVIEW NAME

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
        textView=(TextView)findViewById(R.id.textView_name_mainactivity);
        //

        /*
        PRESENTER
         */
        Intent loginIntent=getIntent();
        textView.setText(loginIntent.getStringExtra("name"));
        presenter=new MainActivityPresenter(this,loginIntent.getStringExtra("name"),loginIntent.getStringExtra("id"),loginIntent.getStringExtra("token"));
        //


    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        listview.setAdapter(adapter);
    }

}

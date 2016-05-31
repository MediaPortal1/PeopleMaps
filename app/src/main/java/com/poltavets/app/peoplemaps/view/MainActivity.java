package com.poltavets.app.peoplemaps.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.poltavets.app.peoplemaps.R;
import com.poltavets.app.peoplemaps.presenter.MainActivityInterface;
import com.poltavets.app.peoplemaps.presenter.MainActivityPresenter;


public class MainActivity extends AppCompatActivity implements MainView{

    /*
    VIEW
     */
    private ListView listview; //MAIN LISTVIEW
    private TextView textView; //MAIN TEXTVIEW NAME
    private ProgressBar progressBar; //MAIN PROGRESSBAR

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
        progressBar=(ProgressBar) findViewById(R.id.progressBar_main);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(presenter.getMapIntent(position));
            }
        });
        //

        /*
        PRESENTER
         */
        Intent loginIntent=getIntent();
        if(loginIntent!=null) {
            textView.setText(loginIntent.getStringExtra("name"));
            presenter = new MainActivityPresenter(this, this, loginIntent.getStringExtra("token"));
        }
        //


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        listview.setAdapter(adapter);
    }

    @Override
    public void setListEnabled() {
        listview.setEnabled(true);
        listview.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setListOnProgress() {
        listview.setEnabled(false);
        listview.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }
}

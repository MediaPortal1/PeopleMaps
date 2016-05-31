package com.poltavets.app.peoplemaps.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
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
    private FloatingActionButton floatingActionButton;

    /*
    PRESENTER
     */
    private MainActivityInterface presenter;

    /*
    LAND ORIENTATION
     */
    private boolean orintationLand;
    private MapsFragment fragmentMap;

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
                if(!orintationLand)
                startActivity(presenter.getMapIntent(position));
                else {
                    Bundle bundle=presenter.getMapIntent(position).getExtras();
                    fragmentMap.addMarkerToMap(bundle.getString("name"),bundle.getDouble("lat"),bundle.getDouble("long"));
                }
            }
        });
        floatingActionButton= (FloatingActionButton) findViewById(R.id.floating_refresh);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.refreshUserList();
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

         /*
        ORIENTATION LAND OR PORTRAIT
         */
        fragmentMap = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        if(fragmentMap!=null && getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
            orintationLand=true;
        else
            orintationLand=false;
        //

        /*
        TABLETS
         */
        if(findViewById(R.id.container_main_xhdpi)!=null)
            orintationLand=true;
    }


    @Override
    public void setAdapter(BaseAdapter adapter) { listview.setAdapter(adapter); }

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

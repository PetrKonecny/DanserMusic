package cz.muni.danser;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.api.GeneralApi;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;
import cz.muni.danser.model.Playlist;

public class SongListActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener, View.OnClickListener {

    private GeneralApi service;
    private List<DanceSong> songs;
    private ExportFragment exportFragment;
    private SongListFragment listFragment;
    private SongDetailFragment detailFragment;
    private boolean pending;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton fab,fab1,fab2,fabClose;
    private Animation fab_open,fab_close;
    private boolean isFabOpen;
    private String title;

    public List<Listable> getSongs(int type) {
        return (List) songs;
    }

    @Override
    public boolean getPending() {
        return pending;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        service = new ApiImpl();
        songs = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent((NavigationView) findViewById(R.id.navigation));

        fab = (FloatingActionButton)findViewById(R.id.floating_button);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fabClose = (FloatingActionButton)findViewById(R.id.floating_button_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fabClose.setOnClickListener(this);

        exportFragment = (ExportFragment) getFragmentManager().findFragmentByTag("EXPORT_FRAGMENT");
        if(exportFragment == null){
            exportFragment = new ExportFragment();
            getFragmentManager().beginTransaction().add(exportFragment,"EXPORT_FRAGMENT").commit();
        }
        detailFragment = (SongDetailFragment) getFragmentManager().findFragmentById(R.id.detail_frag_duo_container);
        if(detailFragment == null && getResources().getBoolean(R.bool.dualPane)) {
            detailFragment = new SongDetailFragment();
            getFragmentManager().beginTransaction().add(R.id.detail_frag_duo_container,detailFragment).commit();
        }
        listFragment = (SongListFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag_container);
        if(listFragment == null){
            listFragment = new SongListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.list_frag_container,listFragment).commit();
        }
        if(savedInstanceState != null){
            songs.addAll((List) savedInstanceState.getParcelableArrayList("SONGS"));
        }
        if (getIntent().hasExtra("dance")) {
            Dance dance = (Dance) getIntent().getExtras().get("dance");
            if(savedInstanceState == null) {
                pending = true;
                getSupportActionBar().setTitle(Utils.getTranslatedMainText(dance));
                service.getSongs(dance.getDanceType(), new Consumer<List<DanceSong>>() {
                    @Override
                    public void accept(List<DanceSong> danceSongs) {
                        songs = danceSongs;
                        boolean dual = getResources().getBoolean(R.bool.dualPane);
                        pending = false;
                        setDetailViewVisibility();
                        listFragment.refreshList((List) danceSongs, dual);
                    }
                });
            }
        } else if (getIntent().hasExtra("playlistId")) {
            long playlistId = getIntent().getExtras().getLong("playlistId");
            Playlist playlist = SongUtils.getPlaylist(playlistId);
            if(playlist != null){
                getSupportActionBar().setTitle(playlist.playlistName);
                songs = playlist.songs();
            }else{
                Toast.makeText(this,"Playlist not fonud",Toast.LENGTH_SHORT).show();
            }
            setDetailViewVisibility();
        } else if (getIntent().hasExtra("songs") && getIntent().hasExtra("query")) {
            songs = getIntent().getParcelableArrayListExtra("songs");
            setDetailViewVisibility();
            getSupportActionBar().setTitle(String.format("%d songs for query '%s'", songs.size(), getIntent().getStringExtra("query")));
        }
        setDetailViewVisibility();
    }

    public void setDetailViewVisibility(){
        if(getResources().getBoolean(R.bool.dualPane)){
            if(songs.isEmpty()) {
                findViewById(R.id.detail_frag_duo_container).setVisibility(View.GONE);
            }else{
                findViewById(R.id.detail_frag_duo_container).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getIntent().hasExtra("playlistId")){
            getMenuInflater().inflate(R.menu.menu_list_playlist,menu);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.floating_button:
                animateFAB();
                break;
            case R.id.floating_button_close:
                animateFAB();
                break;
            case R.id.fab1:
                exportToYoutube(null);
                break;
            case R.id.fab2:
                exportToSpotify(null);
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){
            fabClose.hide();
            fab.show();
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {
            fabClose.show();
            fab.hide();
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }



    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        MainActivity.selectDrawerItem(menuItem,SongListActivity.this,mDrawer);
                        return true;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void deletePlaylist(MenuItem item) {
        SongUtils.deletePlaylist(getIntent().getExtras().getLong("playlistId"));
        Intent intent = new Intent(this,SongListActivity.class);
        intent.setAction(MainActivity.LIST_PLAYLIST_ACTION);
        Toast.makeText(this,"Playlist deleted",Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    public void deleteSongFromPlaylist() {


    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArrayList("SONGS", (ArrayList) songs);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onListItemClick(Listable item) {
        if(!getResources().getBoolean(R.bool.dualPane)) {
            Intent intent = new Intent(this, DetailFragmentWrapperActivity.class);
            intent.putExtra("danceSong", (DanceSong) item);
            startActivity(intent);
        } else {
            detailFragment.updateDanceSong((DanceSong) item);
        }
    }

    public void exportToSpotify(MenuItem item){
        Intent intent = new Intent(this, ExportActivity.class);
        intent.putParcelableArrayListExtra("songs",(ArrayList<? extends Parcelable>) songs);
        intent.putExtra("service","spotify");
        startActivity(intent);
    }

    public void exportToYoutube(MenuItem item){
        Intent intent = new Intent(this, ExportActivity.class);
        intent.putParcelableArrayListExtra("songs",(ArrayList<? extends Parcelable>) songs);
        intent.putExtra("service","youtube");
        startActivity(intent);
    }

}

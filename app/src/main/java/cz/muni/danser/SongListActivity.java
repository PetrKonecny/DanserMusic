package cz.muni.danser;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.api.GeneralApi;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;

public class SongListActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener {

    private GeneralApi service;
    private List<DanceSong> songs;
    private ExportFragment exportFragment;
    private SongListFragment listFragment;
    private SongDetailFragment detailFragment;


    public List<Listable> getSongs() {
        return (List) songs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        service = new ApiImpl();
        songs = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
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
        if(getIntent().hasExtra("dance")){
            Dance dance = (Dance) getIntent().getExtras().get("dance");
            listFragment.setPending(true);
            getSupportActionBar().setTitle(Utils.getTranslatedMainText(dance));
            service.getSongs(dance.getDanceType(), new Consumer<List<DanceSong>>(){
                @Override
                public void accept(List<DanceSong> danceSongs) {
                    songs = danceSongs;
                    boolean dual = getResources().getBoolean(R.bool.dualPane);
                    listFragment.setPending(false);
                    listFragment.refreshList((List)danceSongs, dual);
                }
            });
        } else if (getIntent().hasExtra("playlistId")) {
            long playlistId = getIntent().getExtras().getLong("playlistId");
            songs = service.getPlaylist(playlistId).songs();
        } else if(getIntent().hasExtra("songs")){
            songs = getIntent().getParcelableArrayListExtra("songs");
            getSupportActionBar().setTitle(String.format("%d songs for query '%s'",songs.size(),getIntent().getStringExtra("query")));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArrayList("SONGS", (ArrayList) songs);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlist, menu);
        return true;
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
        /*
        exportFragment.setSongs(songs);
        exportFragment.setPlaylistName("generated in danser");
        exportFragment.exportToSpotify();*/
    }

    public void exportToYoutube(MenuItem item){
        Intent intent = new Intent(this, ExportActivity.class);
        intent.putParcelableArrayListExtra("songs",(ArrayList<? extends Parcelable>) songs);
        intent.putExtra("service","youtube");
        startActivity(intent);
        /*
        exportFragment.setSongs(songs);
        exportFragment.setPlaylistName("generated in danser");
        exportFragment.exportToYoutube();*/
    }

}

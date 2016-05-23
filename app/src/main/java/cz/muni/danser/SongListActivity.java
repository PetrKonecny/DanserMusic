package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.api.GeneralApi;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;

public class SongListActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener {

    private GeneralApi service;
    private List<DanceSong> songs;
    private ExportFragment exportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        service = new ApiImpl();
        songs = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        final SongListFragment fragment = (SongListFragment) getFragmentManager().findFragmentById(R.id.list_frag);
        if(savedInstanceState != null){
            songs.addAll((List) savedInstanceState.getParcelableArrayList("SONGS"));
        }
        if(getIntent().hasExtra("danceId")){
            int danceId = getIntent().getExtras().getInt("danceId");
            service.getSongs(danceId, new Consumer<List<DanceSong>>(){
                @Override
                public void accept(List<DanceSong> danceSongs) {
                    songs = danceSongs;
                    fragment.refreshList(danceSongs);
                    if(getResources().getBoolean(R.bool.dualPane)){
                        onListFragmentInteraction((DanceSong) danceSongs.get(0));
                    }
                }
            });

        } else if (getIntent().hasExtra("playlistId")) {
            long playlistId = getIntent().getExtras().getLong("playlistId");
            Log.d("whatever",Integer.toString(service.getPlaylist(playlistId).songs().size()));
            fragment.refreshList(service.getPlaylist(playlistId).songs());
            songs = service.getPlaylist(playlistId).songs();
        }

        exportFragment = (ExportFragment) getFragmentManager().findFragmentByTag("EXPORT_FRAGMENT");
        if(exportFragment == null){
            exportFragment = new ExportFragment();
            getFragmentManager().beginTransaction().add(exportFragment,"EXPORT_FRAGMENT").commit();
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
    public void onListFragmentInteraction(Listable item) {
        if(!getResources().getBoolean(R.bool.dualPane)) {
            Intent intent = new Intent(this, DetailFragmentWrapperActivity.class);
            intent.putExtra("danceSong", (DanceSong) item);
            startActivity(intent);
        } else {
            SongDetailFragment fragment = (SongDetailFragment) getFragmentManager().findFragmentById(R.id.detail_frag_duo);
            fragment.updateDanceSong((DanceSong) item);
        }
    }

    public void exportToSpotify(MenuItem item){
        exportFragment.setSongs(songs);
        exportFragment.setPlaylistName("generated in danser");
        exportFragment.exportToSpotify();
    }

    public void exportToYoutube(MenuItem item){
        exportFragment.setSongs(songs);
        exportFragment.setPlaylistName("generated in danser");
        exportFragment.exportToYoutube();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("whatever" , Integer.toString(requestCode));
    }
}

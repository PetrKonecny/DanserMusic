package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.api.GeneralApi;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;

public class SongListActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener {

    private GeneralApi service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        service = new ApiImpl();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        final SongListFragment fragment = (SongListFragment) SongListActivity.this.getFragmentManager().findFragmentById(R.id.list_frag);
        if(getIntent().hasExtra("danceId")){
            int danceId = getIntent().getExtras().getInt("danceId");
            service.getSongs(danceId, new Consumer<List<DanceSong>>(){
                @Override
                public void accept(List<DanceSong> danceSongs) {
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
        }
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
}

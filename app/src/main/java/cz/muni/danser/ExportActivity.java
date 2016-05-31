package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.api.GeneralApi;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;

public class ExportActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener {

    private ExportFragment exportFragment;
    private SongListFragment listFragment;
    private List<DanceSong> validSongs;
    private List<DanceSong> invalidSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validSongs = new ArrayList<>();
        setContentView(R.layout.activity_export);
        GeneralApi api = new ApiImpl();
        exportFragment = (ExportFragment) getFragmentManager().findFragmentByTag("EXPORT_FRAGMENT");
        if(exportFragment == null){
            exportFragment = new ExportFragment();
            getFragmentManager().beginTransaction().add(exportFragment,"EXPORT_FRAGMENT").commit();
        }
        listFragment = (SongListFragment) getFragmentManager().findFragmentById(R.id.list_frag_container);
        if(listFragment == null){
            listFragment = new SongListFragment();
            getFragmentManager().beginTransaction().add(R.id.list_frag_container,listFragment).commit();
        }
        List<DanceSong> songs = getIntent().getParcelableArrayListExtra("songs");
        List<String> params = new ArrayList<>();
        params.add(getIntent().getStringExtra("service"));
        listFragment.setPending(true);
        api.getManyRecordings(songs, params, new Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>>() {
            @Override
            public void accept(LinkedHashMap<DanceSong, List<DanceRecording>> map) {
                for(Map.Entry<DanceSong, List<DanceRecording>> e : map.entrySet()) {
                    DanceSong song = e.getKey();
                    song.setRecordings(e.getValue());
                    if (!song.getRecordings().isEmpty()) {
                        validSongs.add(song);
                    }
                }
                listFragment.setPending(false);
                listFragment.refreshList( (List) validSongs);
                if(!validSongs.isEmpty()){
                    findViewById(R.id.export_button).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onListItemClick(Listable item) {
        Intent intent = new Intent(this, DetailFragmentWrapperActivity.class);
        intent.putExtra("danceSong", (DanceSong) item);
        startActivity(intent);
    }

    @Override
    public List<Listable> getSongs() {
        return (List) validSongs;
    }

    public void export(View view){
        exportFragment.setSongs(validSongs);
        exportFragment.setPlaylistName("generated in danser");
        Log.d("ExportActivity.export",getIntent().getStringExtra("service"));
        switch(getIntent().getStringExtra("service")){
            case "spotify" :
                exportFragment.exportToSpotify();
                break;
            case "youtube" :
                exportFragment.exportToYoutube();
                break;
            default:
        }
    }
}

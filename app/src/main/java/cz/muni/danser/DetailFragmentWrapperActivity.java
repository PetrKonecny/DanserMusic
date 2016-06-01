package cz.muni.danser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cz.muni.danser.model.DanceSong;

public class DetailFragmentWrapperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_fragment_wrapper);
        SongDetailFragment fragment = (SongDetailFragment) getFragmentManager().findFragmentById(R.id.detail_frag_solo);
        DanceSong song = (DanceSong) getIntent().getParcelableExtra("danceSong");
        getSupportActionBar().setTitle(song.getSongName());
        fragment.updateDanceSong(song);
    }
}

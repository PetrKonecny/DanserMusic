package cz.muni.danser;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cz.muni.danser.model.DanceSong;

public class GeneratePlaylistActivity extends AppCompatActivity {
    private Fragment generateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_playlist);
        getSupportActionBar().setTitle(R.string.generate_playlist);

        generateFragment = getFragmentManager().findFragmentById(R.id.generate_frag_container);
        if(generateFragment == null) {
            if(!getIntent().hasExtra("generateAdvanced")){
                generateFragment = new GeneratePlaylistSimpleFragment();
                getFragmentManager().beginTransaction().add(R.id.generate_frag_container,generateFragment).commit();
            } else {
                generateFragment = new GeneratePlaylistAdvancedFragment();
                getFragmentManager().beginTransaction().add(R.id.generate_frag_container,generateFragment).commit();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!getIntent().hasExtra("generateAdvanced")) {
            getMenuInflater().inflate(R.menu.menu_generate, menu);
            return true;
        }
        return false;
    }

    public void switchToAdvanced(MenuItem item){
        Intent intent = new Intent(this, GeneratePlaylistActivity.class);
        intent.putExtra("generateAdvanced", true);
        startActivity(intent);
    }
}

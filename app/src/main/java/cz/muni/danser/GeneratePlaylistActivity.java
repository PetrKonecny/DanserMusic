package cz.muni.danser;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Playlist;

public class GeneratePlaylistActivity extends AppCompatActivity {
    private Fragment generateFragment;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_playlist);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent((NavigationView) findViewById(R.id.navigation));

        getSupportActionBar().setTitle(R.string.create_playlist);

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


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        MainActivity.selectDrawerItem(menuItem,GeneratePlaylistActivity.this,mDrawer);
                        return true;
                    }
                });
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

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
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

    public void generateSimplePlaylist(View v){
        assert findViewById(R.id.generated_playlist_name) != null;
        final String playlistName = String.valueOf(((TextView)findViewById(R.id.generated_playlist_name)).getText());
        if(playlistName.equals("")){
            Toast.makeText(GeneratePlaylistActivity.this, R.string.playlist_choose_name, Toast.LENGTH_SHORT).show();
        } else if(SongUtils.getPlaylistByName(playlistName) != null){
            Toast.makeText(GeneratePlaylistActivity.this, R.string.playlist_already_exists, Toast.LENGTH_SHORT).show();
        } else {
            assert findViewById(R.id.generate_spinner) != null;
            final int preset = ((Spinner) findViewById(R.id.generate_spinner)).getSelectedItemPosition();
            (new ApiImpl()).generatePlaylistFromPreset(preset, new Consumer<List<DanceSong>>() {
                @Override
                public void accept(List<DanceSong> danceSongs) {
                    Playlist playlist = new Playlist();
                    playlist.playlistName = playlistName;
                    playlist.save();
                    Utils.activeAndroidSaveCollection(danceSongs);
                    SongUtils.saveSongsToPaylist(playlist.getId(), danceSongs);
                    Toast.makeText(GeneratePlaylistActivity.this, String.format(getString(R.string.playlist_was_created), playlistName), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

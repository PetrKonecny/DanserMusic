package cz.muni.danser;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.muni.danser.api.Api;
import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceCategory;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;
import cz.muni.danser.model.Playlist;

public class MainActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener, SearchSuggestionFragment.Callbacks {

    List<Listable> listables;
    SearchSuggestionFragment searchFragment;
    SongListFragment listFragment;
    ApiImpl service;

    final static String LIST_PLAYLIST_ACTION = "cz.muni.fi.danser.LIST_PLAYLIST_ACTION";
    final static String SAVE_LISTABLES = "LISTABLES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchFragment = (SearchSuggestionFragment) getFragmentManager().findFragmentByTag("SEARCH");
        if (searchFragment == null) {
            searchFragment = new SearchSuggestionFragment();
            getFragmentManager().beginTransaction().add(searchFragment, "SEARCH").commit();
        }
        listFragment = (SongListFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag_container);
        if (listFragment == null) {
            listFragment = new SongListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.list_frag_container,listFragment).commit();
        }
        Intent intent = this.getIntent();
        Api.setContext(this);
        service = new ApiImpl();
        service.setExceptionCallback(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(MainActivity.this, getString(R.string.not_online), Toast.LENGTH_LONG).show();
            }
        });
        listables = new ArrayList<>();

        if (savedInstanceState != null) {
            listables.addAll((Collection) savedInstanceState.getParcelableArrayList(SAVE_LISTABLES));
        }

        if (intent.hasExtra("danceCategory")) {
            listFragment.setPending(true);
            DanceCategory danceCategory = intent.getExtras().getParcelable("danceCategory");
            getSupportActionBar().setTitle(Utils.getTranslatedMainText(danceCategory));
            service.getDances(danceCategory.getDanceCategory(), new Consumer<List<Dance>>() {
                @Override
                public void accept(List<Dance> dances) {
                    MainActivity.this.listables.clear();
                    MainActivity.this.listables.addAll(dances);
                    listFragment.setPending(false);
                    listFragment.refreshList((List)dances);
                }
            });
        } else if (intent.getAction().equals(LIST_PLAYLIST_ACTION)) {
            listables.clear();
            listables.addAll(SongUtils.getAllPlaylists());
        } else {
            listFragment.setPending(true);
            service.getCategories(new Consumer<List<DanceCategory>>() {
                @Override
                public void accept(List<DanceCategory> danceCategories) {
                    MainActivity.this.listables.clear();
                    MainActivity.this.listables.addAll(danceCategories);
                    listFragment.setPending(false);
                    listFragment.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
                    listFragment.refreshList((List)danceCategories);
                }
            });
        }
    }

    @Override
    public List<Listable> getSongs(){
        return listables;
    }

    @Override
    public void onListItemClick(Listable item){
        Intent intent = getIntent();
        if (intent.hasExtra("danceCategory")) {
            intent = new Intent(MainActivity.this, SongListActivity.class);
            intent.putExtra("dance", (Dance) item);
            startActivity(intent);
        } else if (intent.getAction() == LIST_PLAYLIST_ACTION) {
            intent = new Intent(MainActivity.this, SongListActivity.class);
            intent.putExtra("playlistId", ((Playlist) item).getId());
            startActivity(intent);
        } else {
            intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("danceCategory", (DanceCategory) item);
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(SAVE_LISTABLES, (ArrayList) listables);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void showPlaylists(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(LIST_PLAYLIST_ACTION);
        startActivity(intent);
    }

    public void generatePlaylist(MenuItem item) {
        Intent intent = new Intent(this, GeneratePlaylistActivity.class);
        startActivity(intent);
    }

    @Override
    public void onQuerySubmit(List<DanceSong> songs, String query) {
        Intent intent = new Intent(this, SongListActivity.class);
        intent.putParcelableArrayListExtra("songs",(ArrayList<? extends Parcelable>) songs);
        intent.putExtra("query", query);
        startActivity(intent);
    }
}

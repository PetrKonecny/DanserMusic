package cz.muni.danser;

import android.content.Intent;
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
import cz.muni.danser.model.Listable;
import cz.muni.danser.model.Playlist;

public class MainActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener {

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
        listFragment = (SongListFragment) getFragmentManager().findFragmentById(R.id.list_frag_container);
        if (listFragment == null) {
            listFragment = new SongListFragment();
            getFragmentManager().beginTransaction().add(R.id.list_frag_container,listFragment).commit();
        }
        Intent intent = this.getIntent();
        Api.setContext(this);
        service = new ApiImpl();
        service.setExceptionCallback(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Toast.makeText(MainActivity.this, getString(R.string.not_online), Toast.LENGTH_LONG).show();
            }
        });
        listables = new ArrayList<>();

        if (savedInstanceState != null) {
            listables.addAll((Collection) savedInstanceState.getParcelableArrayList(SAVE_LISTABLES));
        }

        if (intent.hasExtra("danceCategory")) {
            DanceCategory danceCategory = intent.getExtras().getParcelable("danceCategory");
            service.getDances(danceCategory.getDanceCategory(), new Consumer<List<Dance>>() {
                @Override
                public void accept(List<Dance> dances) {
                    MainActivity.this.listables.clear();
                    MainActivity.this.listables.addAll(dances);
                    listFragment.refreshList((List)dances);
                }
            });
        } else if (intent.getAction() == LIST_PLAYLIST_ACTION) {
            listables.clear();
            listables.addAll((List) new Select().all().from(Playlist.class).execute());
        } else {
            service.getCategories(new Consumer<List<DanceCategory>>() {
                @Override
                public void accept(List<DanceCategory> danceCategories) {
                    MainActivity.this.listables.clear();
                    MainActivity.this.listables.addAll(danceCategories);
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
            intent.putExtra("danceId", ((Dance) item).getDanceType());
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
}

package cz.muni.danser;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import cz.muni.danser.api.Api;
import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceCategory;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    @Bind(R.id.my_recycler_view) RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SearchView mSearchView;
    List<DanceCategory> categories = new ArrayList<>();
    List<Dance> dances = new ArrayList<>();
    List<DanceSong> danceSongs = new ArrayList<>();
    List<DanceSong> suggestedDanceSongs = new ArrayList<>();
    ApiImpl service;

    final static String LIST_PLAYLIST_ACTION = "cz.muni.fi.danser.LIST_PLAYLIST_ACTION";
    final static String SAVE_CATEGORIES = "DANCE_CATEGORIES";
    final static String SAVE_DANCES = "DANCES";
    final static String SAVE_SONGS = "DANCE_SONGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        Intent intent = this.getIntent();
        Api.setContext(this);
        service = new ApiImpl();
        service.setExceptionCallback(new Consumer<Throwable>(){
            @Override
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        if(savedInstanceState != null) {
            categories.addAll((Collection) savedInstanceState.getParcelableArrayList(SAVE_CATEGORIES));
            dances.addAll((Collection) savedInstanceState.getParcelableArrayList(SAVE_DANCES));
            danceSongs.addAll((Collection) savedInstanceState.getParcelableArrayList(SAVE_SONGS));
        }

        ActionBar bar = getSupportActionBar();

        if(intent.hasExtra("dance")){
            mLayoutManager = new LinearLayoutManager(this);
            Dance dance = intent.getExtras().getParcelable("dance");
            service.getSongs(dance.getDanceType(), new Consumer<List<DanceSong>>(){
                @Override
                public void accept(List<DanceSong> danceSongs) {
                    MainActivity.this.danceSongs.clear();
                    MainActivity.this.danceSongs.addAll(danceSongs);
                    mAdapter.notifyDataSetChanged();
                }
            });
            if(bar != null){
                bar.setTitle(Utils.getTranslatedMainText(dance));
            }

            mAdapter = new ListAdapter(danceSongs, new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable danceSong){
                    Intent intent = new Intent(MainActivity.this,SongDetailActivity.class);
                    intent.putExtra("danceSong",(DanceSong) danceSong);
                    startActivity(intent);
                }
            },R.layout.list_item_view);
        }
        else if(intent.hasExtra("danceCategory")){
            mLayoutManager = new GridLayoutManager(this,2);
            DanceCategory danceCategory = intent.getExtras().getParcelable("danceCategory");
            service.getDances(danceCategory.getDanceCategory(), new Consumer<List<Dance>>(){
                @Override
                public void accept(List<Dance> dances) {
                    MainActivity.this.dances.clear();
                    MainActivity.this.dances.addAll(dances);
                    mAdapter.notifyDataSetChanged();
                }
            });
            if(bar != null){
                bar.setTitle(Utils.getTranslatedMainText(danceCategory));
            }
            mAdapter = new ListAdapter(dances, new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable dance){
                    Intent intent = new Intent(MainActivity.this,SongListActivity.class);
                    intent.putExtra("danceId",((Dance)dance).getDanceType());
                    startActivity(intent);
                }
            },R.layout.square_list_item_view);
        }
        else{
            service.getCategories(new Consumer<List<DanceCategory>>(){
                @Override
                public void accept(List<DanceCategory> danceCategories) {
                    MainActivity.this.categories.clear();
                    MainActivity.this.categories.addAll(danceCategories);
                    mAdapter.notifyDataSetChanged();
                }
            });
            mLayoutManager = new GridLayoutManager(this,2);
            mAdapter = new ListAdapter(categories, new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable danceCategory){
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("danceCategory",(DanceCategory)danceCategory);
                    startActivity(intent);
                }
            },R.layout.square_list_item_view);
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArrayList(SAVE_CATEGORIES, (ArrayList) categories);
        savedInstanceState.putParcelableArrayList(SAVE_DANCES, (ArrayList) dances);
        savedInstanceState.putParcelableArrayList(SAVE_SONGS, (ArrayList) danceSongs);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(this,
                R.layout.track_search_suggestion,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        mSearchView.setSuggestionsAdapter(suggestionAdapter);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        service.searchSongs(query, null, new Consumer<List<DanceSong>>() {
            @Override
            public void accept(List<DanceSong> danceSongs) {
                mAdapter = new ListAdapter(danceSongs, new ListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Listable danceSong) {
                        Intent intent = new Intent(MainActivity.this, SongDetailActivity.class);
                        intent.putExtra("danceSong", (DanceSong) danceSong);
                        startActivity(intent);
                    }
                }, R.layout.list_item_view);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        service.suggestSongs(newText, new Consumer<List<DanceSong>>() {
            @Override
            public void accept(List<DanceSong> danceSongs) {
                mSearchView.getSuggestionsAdapter().swapCursor(createCursor(danceSongs));
            }
        });
        return false;
    }

    public Cursor createCursor(List<DanceSong> danceSongs){
        suggestedDanceSongs.clear();
        suggestedDanceSongs.addAll(danceSongs);

        String[] columns = {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA
        };

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < danceSongs.size(); i++)
        {
            cursor.addRow(new Object[]{i, danceSongs.get(i).getMainText(), i});
        }
        return cursor;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        showSelectedSuggestedSong(position);
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        showSelectedSuggestedSong(position);
        return false;
    }

    private void showSelectedSuggestedSong(int position){
        Intent intent = new Intent(MainActivity.this,SongDetailActivity.class);
        intent.putExtra("danceSong", suggestedDanceSongs.get(position));
        startActivity(intent);
    }

    public void showPlaylists(MenuItem item){
        Intent intent = new Intent(this,PlaylistActivity.class);
        intent.setAction(MainActivity.LIST_PLAYLIST_ACTION);
        startActivity(intent);
    }

    public void generatePlaylist(MenuItem item){
        Intent intent = new Intent(this,GeneratePlaylistActivity.class);
        startActivity(intent);
    }
}

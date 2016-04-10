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
import cz.muni.danser.cz.muni.danser.api.Api;
import cz.muni.danser.cz.muni.danser.api.CategoryServiceImpl;
import cz.muni.danser.api.DanceServiceImpl;
import cz.muni.danser.api.TrackServiceImpl;
import cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.DanceCategory;
import cz.muni.danser.model.Listable;
import cz.muni.danser.model.Track;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, TrackServiceImpl.Callbacks, DanceServiceImpl.Callbacks, CategoryServiceImpl.Callbacks {

    @Bind(R.id.my_recycler_view) RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SearchView mSearchView;
    List<DanceCategory> categories = new ArrayList<>();
    List<Dance> dances = new ArrayList();
    List<Track> tracks = new ArrayList();
    List<Track> suggestedTracks = new ArrayList();
    TrackServiceImpl trackService;
    DanceServiceImpl danceService;
    CategoryServiceImpl categoryService;

    final static String LIST_PLAYLIST_ACTION = "cz.muni.fi.danser.LIST_PLAYLIST_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        Intent intent = this.getIntent();
        Api.setContext(this);
        trackService = new TrackServiceImpl(this);
        danceService = new DanceServiceImpl(this);
        categoryService = new CategoryServiceImpl(this);

        if(savedInstanceState != null) {
            dances.addAll((Collection) savedInstanceState.getParcelableArrayList("DANCES"));
            tracks.addAll((Collection) savedInstanceState.getParcelableArrayList("TRACKS"));
            categories.addAll((Collection) savedInstanceState.getParcelableArrayList("DANCE_CATEGORIES"));
        }

        ActionBar bar = getSupportActionBar();

        if(intent.hasExtra("dance")){
            mLayoutManager = new LinearLayoutManager(this);
            Dance dance = intent.getExtras().getParcelable("dance");
            danceService.getTracks(dance.getDanceType());
            if(bar != null){
                bar.setTitle(Utils.getTranslatedMainText(dance));
            }

            mAdapter = new ListAdapter(tracks, new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable track){
                    Intent intent = new Intent(MainActivity.this,TrackDetailActivity.class);
                    intent.putExtra("track",(Track) track);
                    startActivity(intent);
                }
            },R.layout.list_item_view);
        }
        else if(intent.hasExtra("danceCategory")){
            mLayoutManager = new GridLayoutManager(this,2);
            DanceCategory danceCategory = intent.getExtras().getParcelable("danceCategory");
            categoryService.getDances(danceCategory.getDanceCategory());
            if(bar != null){
                bar.setTitle(Utils.getTranslatedMainText(danceCategory));
            }
            mAdapter = new ListAdapter(dances, new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable dance){
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("dance",(Dance) dance);
                    startActivity(intent);
                }
            },R.layout.square_list_item_view);
        }
        else{
            categoryService.getCategories();
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
        savedInstanceState.putParcelableArrayList("DANCE_CATEGORIES", (ArrayList) categories);
        savedInstanceState.putParcelableArrayList("DANCES", (ArrayList) dances);
        savedInstanceState.putParcelableArrayList("TRACKS", (ArrayList) tracks);
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
        trackService.searchTracks(query, null);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        trackService.suggestTracks(newText);
        return false;
    }

    public Cursor createCursor(List<Track> tracks){
        suggestedTracks.clear();
        suggestedTracks.addAll(tracks);

        String[] columns = {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA
        };

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < tracks.size(); i++)
        {
            cursor.addRow(new Object[]{i, tracks.get(i).getTrackName(), i});
        }
        return cursor;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        showSelectedSuggestedTrack(position);
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        showSelectedSuggestedTrack(position);
        return false;
    }

    private void showSelectedSuggestedTrack(int position){
        Intent intent = new Intent(MainActivity.this,TrackDetailActivity.class);
        intent.putExtra("track", suggestedTracks.get(position));
        startActivity(intent);
    }

    public void showPlaylists(MenuItem item){
        Intent intent = new Intent(this,PlaylistActivity.class);
        intent.setAction(MainActivity.LIST_PLAYLIST_ACTION);
        startActivity(intent);
    }

    // Implemented service callbacks

    @Override
    public void searchTracksCallback(List<Track> tracks) {
        mAdapter = new ListAdapter(tracks, new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Listable track) {
                Intent intent = new Intent(MainActivity.this, TrackDetailActivity.class);
                intent.putExtra("track", (Track) track);
                startActivity(intent);
            }
        }, R.layout.list_item_view);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void suggestTracksCallback(List<Track> tracks) {
        mSearchView.getSuggestionsAdapter().swapCursor(createCursor(tracks));
    }

    @Override
    public void getAllCategoriesCallback(List<DanceCategory> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void exceptionCallback(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void getDancesForCategoryCallback(List<Dance> dances) {
        this.dances.clear();
        this.dances.addAll(dances);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void getTracksForDanceCallback(List<Track> tracks) {
        this.tracks.clear();
        this.tracks.addAll(tracks);
        mAdapter.notifyDataSetChanged();
    }

    //Not used service callbacks
    @Override
    public void getAllTracksCallback(List<Track> tracks) { }

    @Override
    public void getAllDancesCallback(List<Dance> dances) { }

    @Override
    public void getCategoryCallback(DanceCategory category) { }

    @Override
    public void getDanceCallback(Dance dance) {}

    @Override
    public void getTrackCallback(Track track) {}

}

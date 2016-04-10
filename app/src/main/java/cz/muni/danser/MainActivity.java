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
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    @Bind(R.id.my_recycler_view) RecyclerView mRecyclerView;
    static RecyclerView.Adapter mAdapter;
    protected RecyclerView.Adapter getmAdapter(){
        return mAdapter;
    }
    RecyclerView.LayoutManager mLayoutManager;
    SearchView mSearchView;
    List<Track> suggestedTracks = new ArrayList<>();
    final static String LIST_PLAYLIST_ACTION = "cz.muni.fi.danser.LIST_PLAYLIST_ACTION";
    static CachingTrackService api;
    static boolean firstActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        Intent intent = this.getIntent();

        Api.setContext(getApplicationContext());
        if(api == null){
            api = new CachingTrackService(savedInstanceState);
        }

        SimpleCallback callback = new SimpleCallback(){
            @Override
            public void callback() {
                getmAdapter().notifyDataSetChanged();
            }
        };
        StringCallback failure = new StringCallback() {
            @Override
            public void callback(String s) {
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
            }
        };
        api.failure(failure);

        ActionBar bar = getSupportActionBar();

        if(intent.hasExtra("dance")){
            mLayoutManager = new LinearLayoutManager(this);
            Dance dance = intent.getExtras().getParcelable("dance");
            if(bar != null){
                bar.setTitle(dance.getTranslatedMainText());
            }

            mAdapter = new ListAdapter(api.getTracks(dance.getDanceType(),callback), new ListAdapter.OnItemClickListener() {
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
            if(bar != null){
                bar.setTitle(danceCategory.getTranslatedMainText());
            }
            mAdapter = new ListAdapter(api.getDances(danceCategory.getDanceCategory(),callback), new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable dance){
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("dance",(Dance) dance);
                    startActivity(intent);
                }
            },R.layout.square_list_item_view);
        }
        else{
            mLayoutManager = new GridLayoutManager(this,2);
            mAdapter = new ListAdapter(api.getCategories(callback), new ListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Listable danceCategory){
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("danceCategory",(DanceCategory)danceCategory);
                    startActivity(intent);
                }
            },R.layout.square_list_item_view);

            if(firstActivity && !Utils.isNetworkAvailable()){
                firstActivity = false;
                Toast.makeText(MainActivity.this,getString(R.string.no_network),Toast.LENGTH_LONG).show();
            }
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        api.save(savedInstanceState);
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
        Call<List<Track>> call = Api.getTrackService().searchTracks(query, null);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                List<Track> tracks = response.body();
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
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Call<List<Track>> call = Api.getTrackService().searchTracks(newText,5);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                mSearchView.getSuggestionsAdapter().swapCursor(createCursor(response.body()));
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
        intent.putExtra("track",suggestedTracks.get(position));
        startActivity(intent);
    }

    public void showPlaylists(MenuItem item){
        Intent intent = new Intent(this,PlaylistActivity.class);
        intent.setAction(MainActivity.LIST_PLAYLIST_ACTION);
        startActivity(intent);
    }
}

package cz.muni.danser;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SearchView mSearchView;
    static Api api = new Api();
    static List<Track> tracks = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new TrackListAdapter(tracks, new TrackListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Track track){
                Intent intent = new Intent(MainActivity.this,TrackDetailActivity.class);
                intent.putExtra("track",track);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        loadTracks();
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
        //TODO implement actvitiy action search on MainActivity or in new activity
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //Call<List<Track>> call = api.getTrackService().searchTracks(newText,5);
        Call<List<Track>> call = api.getTrackService().getAllTracks();
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

    public void loadTracks(){
        Call<List<Track>> call = api.getTrackService().getAllTracks();
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                tracks.addAll(response.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public Cursor createCursor(List<Track> tracks){
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
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Intent intent = new Intent(MainActivity.this,TrackDetailActivity.class);
        intent.putExtra("track",tracks.get(position));
        startActivity(intent);
        return false;
    }
}

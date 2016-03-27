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
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SearchView mSearchView;
    final static Api api = new Api();
    final static List<DanceCategory> categories = new ArrayList<>();
    final static List<Dance> dances = new ArrayList<>();
    final static List<Track> tracks = new ArrayList();
    final static List<Track> suggestedTracks = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);

        Intent intent = this.getIntent();
        if(intent.hasExtra("dance")){
            mLayoutManager = new LinearLayoutManager(this);
            Dance dance = intent.getExtras().getParcelable("dance");
            mAdapter = new TrackListAdapter(tracks, new TrackListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Track track){
                    Intent intent = new Intent(MainActivity.this,TrackDetailActivity.class);
                    intent.putExtra("track",track);
                    startActivity(intent);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            loadTracks(dance);
        }
        else if(intent.hasExtra("danceCategory")){
            mLayoutManager = new GridLayoutManager(this,2);
            DanceCategory danceCategory = intent.getExtras().getParcelable("danceCategory");
            mAdapter = new DanceListAdapter(dances, new DanceListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Dance dance){
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("dance",dance);
                    startActivity(intent);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            loadDances(danceCategory);
        }
        else{
            mLayoutManager = new GridLayoutManager(this,2);
            mAdapter = new DanceCategoryListAdapter(categories, new DanceCategoryListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DanceCategory danceCategory){
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("danceCategory",danceCategory);
                    startActivity(intent);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            loadDanceCategories();
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
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
        Call<List<Track>> call = api.getTrackService().searchTracks(query, null);
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                List<Track> tracks = response.body();
                mAdapter = new TrackListAdapter(tracks, new TrackListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Track track){
                        Intent intent = new Intent(MainActivity.this,TrackDetailActivity.class);
                        intent.putExtra("track",track);
                        startActivity(intent);
                    }
                });
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
        Call<List<Track>> call = api.getTrackService().searchTracks(newText,5);
        //Call<List<Track>> call = api.getTrackService().getAllTracks();
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

    public void loadDanceCategories(){
        categories.clear();
        Call<List<DanceCategory>> call = api.getTrackService().getCategories();
        call.enqueue(new Callback<List<DanceCategory>>() {
            @Override
            public void onResponse(Call<List<DanceCategory>> call, Response<List<DanceCategory>> response) {
                categories.addAll(response.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<DanceCategory>> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadDances(DanceCategory danceCategory){
        dances.clear();
        Call<List<Dance>> call = api.getTrackService().getDances(danceCategory.getDanceCategory());
        call.enqueue(new Callback<List<Dance>>() {
            @Override
            public void onResponse(Call<List<Dance>> call, Response<List<Dance>> response) {
                dances.addAll(response.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Dance>> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadTracks(Dance dance){
        tracks.clear();
        Call<List<Track>> call = api.getTrackService().getTracks(dance.getDanceType());
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
}

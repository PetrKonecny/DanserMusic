package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.my_recycler_view) RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    List<DanceCategory> categories = new ArrayList<>();
    List<Dance> dances = new ArrayList<>();
    List<DanceSong> danceSongs = new ArrayList<>();
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
        SearchSuggestionFragment searchFragment = (SearchSuggestionFragment) getFragmentManager().findFragmentByTag("SEARCH");
        if(searchFragment == null){
            getFragmentManager().beginTransaction().add(new SearchSuggestionFragment(),"SEARCH").commit();
        }
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

        if(intent.hasExtra("danceCategory")){
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

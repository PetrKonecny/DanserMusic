package cz.muni.danser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
    private boolean pending;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;



    final static String LIST_PLAYLIST_ACTION = "cz.muni.fi.danser.LIST_PLAYLIST_ACTION";
    final static String SAVE_LISTABLES = "LISTABLES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent((NavigationView) findViewById(R.id.navigation));

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
            DanceCategory danceCategory = intent.getExtras().getParcelable("danceCategory");
            getSupportActionBar().setTitle(Utils.getTranslatedMainText(danceCategory));
            if(savedInstanceState == null) {
                pending = true;
                service.getDances(danceCategory.getDanceCategory(), new Consumer<List<Dance>>() {
                    @Override
                    public void accept(List<Dance> dances) {
                        MainActivity.this.listables.clear();
                        MainActivity.this.listables.addAll(dances);
                        pending = false;
                        listFragment.refreshList((List) dances);
                    }
                });
            }
        } else if (intent.getAction() != null && intent.getAction().equals(LIST_PLAYLIST_ACTION)) {
            getSupportActionBar().setTitle(R.string.playlists);
            if(savedInstanceState == null) {
                listables.clear();
                listables.addAll(SongUtils.getAllPlaylists());
            }
            FloatingActionButton button = (FloatingActionButton) findViewById(R.id.floating_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generatePlaylist(null);
                }
            });
            button.show();
        } else {
            getSupportActionBar().setTitle(R.string.browse);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                listFragment.setLayoutManager(new GridLayoutManager(MainActivity.this,4));
            }else {
                listFragment.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
            }
            if(savedInstanceState == null) {
                pending = true;
                service.getCategories(new Consumer<List<DanceCategory>>() {
                    @Override
                    public void accept(List<DanceCategory> danceCategories) {
                        MainActivity.this.listables.clear();
                        MainActivity.this.listables.addAll(danceCategories);
                        pending = false;
                        listFragment.refreshList((List) danceCategories);
                    }
                });
            }
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        MainActivity.selectDrawerItem(menuItem,MainActivity.this,mDrawer);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void selectDrawerItem(MenuItem menuItem, final Activity activity, DrawerLayout drawer) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.drawer_browse:
                intent = new Intent(activity,MainActivity.class);
                break;
            case R.id.drawer_paylists:
                intent = new Intent(activity,MainActivity.class);
                intent.setAction(LIST_PLAYLIST_ACTION);
                break;
            case R.id.drawer_generate:
                intent = new Intent(activity,GeneratePlaylistActivity.class);
                break;
            default:
                return;
        }
        drawer.closeDrawers();
        final Intent finalIntent = intent;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                activity.startActivity(finalIntent);
            }
        }, 200);
    }

    @Override
    public List<Listable> getSongs(int type){
        return listables;
    }

    @Override
    public boolean getPending() {
        return pending;
    }

    @Override
    public void onListItemClick(Listable item){
        Intent intent = getIntent();
        if (intent.hasExtra("danceCategory")) {
            intent = new Intent(MainActivity.this, SongListActivity.class);
            intent.putExtra("dance", (Dance) item);
            startActivity(intent);
        } else if (intent.getAction() != null && intent.getAction().equals(LIST_PLAYLIST_ACTION)) {
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

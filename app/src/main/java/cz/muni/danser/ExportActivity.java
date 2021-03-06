package cz.muni.danser;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.api.GeneralApi;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Listable;

public class ExportActivity extends AppCompatActivity implements SongListFragment.OnListFragmentInteractionListener {

    private ExportFragment exportFragment;
    private SongListFragment listFragment;
    private SongListFragment notAvailableListFragment;
    private List<DanceSong> validSongs;
    private List<DanceSong> invalidSongs;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private boolean pending;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private String title;

    public static int INVALID_SONGS_FRAGMENT = 1;
    public static int VALID_SONGS_FRAGMENT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validSongs = new ArrayList<>();
        invalidSongs = new ArrayList<>();
        setContentView(R.layout.activity_export);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent((NavigationView) findViewById(R.id.navigation));

        GeneralApi api = new ApiImpl();
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        adapter = (ViewPagerAdapter) viewPager.getAdapter();
        exportFragment = (ExportFragment) getFragmentManager().findFragmentByTag("EXPORT_FRAGMENT");
        if (exportFragment == null) {
            exportFragment = new ExportFragment();
            getFragmentManager().beginTransaction().add(exportFragment, "EXPORT_FRAGMENT").commit();
        }
        List<DanceSong> songs = getIntent().getParcelableArrayListExtra("songs");
        List<String> params = new ArrayList<>();
        String service = getIntent().getStringExtra("service");
        title = getIntent().getStringExtra("title");
        params.add(service);
        if(service.equals("youtube")){
            getSupportActionBar().setTitle(R.string.export_to_youtube);
        } else if(service.equals("spotify")) {
            getSupportActionBar().setTitle(R.string.export_to_spotify);
        }

        if (savedInstanceState != null) {
            validSongs.addAll((List) savedInstanceState.getParcelableArrayList("VALID_SONGS"));
            invalidSongs.addAll((List) savedInstanceState.getParcelableArrayList("INVALID_SONGS"));
            title = savedInstanceState.getString("TITLE");
            updateInterface();
        } else {
            pending = true;
            api.getManyRecordings(songs, params, new Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>>() {
                @Override
                public void accept(LinkedHashMap<DanceSong, List<DanceRecording>> map) {
                    for (Map.Entry<DanceSong, List<DanceRecording>> e : map.entrySet()) {
                        DanceSong song = e.getKey();
                        song.setRecordings(e.getValue());
                        if (song.getRecordings() != null && !song.getRecordings().isEmpty()) {
                            validSongs.add(song);
                        } else {
                            invalidSongs.add(song);
                        }
                    }
                    listFragment = (SongListFragment) adapter.getRegisteredFragment(0);
                    notAvailableListFragment = (SongListFragment) adapter.getRegisteredFragment(1);
                    notAvailableListFragment.setType(INVALID_SONGS_FRAGMENT);
                    pending = false;
                    listFragment.refreshList((List) validSongs);
                    notAvailableListFragment.refreshList((List) invalidSongs);
                    updateInterface();
                }
            });
        }
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        MainActivity.selectDrawerItem(menuItem,ExportActivity.this,mDrawer);
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

    public void updateInterface() {
        if (!validSongs.isEmpty()) {
            findViewById(R.id.export_button).setVisibility(View.VISIBLE);
        }
        tabLayout.getTabAt(0).setText(getString(R.string.available)+String.format(" (%d)", validSongs.size()));
        tabLayout.getTabAt(1).setText(getString(R.string.not_available)+String.format(" (%d)", invalidSongs.size()));
    }

    @Override
    public void onListItemClick(Listable item) {
        Intent intent = new Intent(this, DetailFragmentWrapperActivity.class);
        intent.putExtra("danceSong", (DanceSong) item);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("VALID_SONGS", (ArrayList) validSongs);
        savedInstanceState.putParcelableArrayList("INVALID_SONGS", (ArrayList) invalidSongs);
        savedInstanceState.putString("TITLE", title);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public List<Listable> getSongs(int type) {
        if(type == INVALID_SONGS_FRAGMENT){
            return (List) invalidSongs;
        }
        return (List) validSongs;
    }

    @Override
    public boolean getPending() {
        return pending;
    }

    public void export(View view) {
        exportFragment.setSongs(validSongs);
        exportFragment.setPlaylistName(title);
        switch (getIntent().getStringExtra("service")) {
            case "spotify":
                exportFragment.exportToSpotify();
                break;
            case "youtube":
                exportFragment.exportToYoutube();
                break;
            default:
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SongListFragment(), getString(R.string.available));
        adapter.addFragment(new SongListFragment(), getString(R.string.not_available));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SparseArray<Fragment> registeredFragments = new SparseArray<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

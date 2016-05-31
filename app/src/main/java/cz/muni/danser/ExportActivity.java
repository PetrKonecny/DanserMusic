package cz.muni.danser;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validSongs = new ArrayList<>();
        invalidSongs = new ArrayList<>();
        setContentView(R.layout.activity_export);
        GeneralApi api = new ApiImpl();
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        adapter = (ViewPagerAdapter) viewPager.getAdapter();
        exportFragment = (ExportFragment) getFragmentManager().findFragmentByTag("EXPORT_FRAGMENT");
        if(exportFragment == null){
            exportFragment = new ExportFragment();
            getFragmentManager().beginTransaction().add(exportFragment,"EXPORT_FRAGMENT").commit();
        }
        List<DanceSong> songs = getIntent().getParcelableArrayListExtra("songs");
        List<String> params = new ArrayList<>();
        String service = getIntent().getStringExtra("service");
        params.add(service);
        getSupportActionBar().setTitle(String.format("Export to %s",service));
        ((SongListFragment)adapter.mFragmentList.get(0)).setPending(true);
        ((SongListFragment)adapter.mFragmentList.get(1)).setPending(true);
        api.getManyRecordings(songs, params, new Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>>() {
            @Override
            public void accept(LinkedHashMap<DanceSong, List<DanceRecording>> map) {
                for(Map.Entry<DanceSong, List<DanceRecording>> e : map.entrySet()) {
                    DanceSong song = e.getKey();
                    song.setRecordings(e.getValue());
                    if (!song.getRecordings().isEmpty()) {
                        validSongs.add(song);
                    }else{
                        invalidSongs.add(song);
                    }
                }
                listFragment = (SongListFragment) adapter.getRegisteredFragment(0);
                tabLayout.getTabAt(0).setText(String.format("Available (%d)",validSongs.size()));
                tabLayout.getTabAt(1).setText(String.format("Not Available (%d)",invalidSongs.size()));
                notAvailableListFragment = (SongListFragment) adapter.getRegisteredFragment(1);
                listFragment.setPending(false);
                listFragment.refreshList( (List) validSongs);
                notAvailableListFragment.refreshList((List) invalidSongs);
                if(!validSongs.isEmpty()){
                    findViewById(R.id.export_button).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onListItemClick(Listable item) {
        Intent intent = new Intent(this, DetailFragmentWrapperActivity.class);
        intent.putExtra("danceSong", (DanceSong) item);
        startActivity(intent);
    }

    @Override
    public List<Listable> getSongs() {
        return (List) validSongs;
    }

    public void export(View view){
        exportFragment.setSongs(validSongs);
        exportFragment.setPlaylistName("generated in danser");
        Log.d("ExportActivity.export",getIntent().getStringExtra("service"));
        switch(getIntent().getStringExtra("service")){
            case "spotify" :
                exportFragment.exportToSpotify();
                break;
            case "youtube" :
                exportFragment.exportToYoutube();
                break;
            default:
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SongListFragment(), "Available");
        adapter.addFragment(new SongListFragment(), "Not Available");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


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

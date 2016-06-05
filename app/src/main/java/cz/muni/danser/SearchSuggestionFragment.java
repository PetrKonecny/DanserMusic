package cz.muni.danser;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.api.GeneralApi;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.DanceSong;

public class SearchSuggestionFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener{

    private SearchView mSearchView;
    private GeneralApi service;
    private List<DanceSong> suggestedDanceSongs;

    public SearchSuggestionFragment() {
        // Required empty public constructor
    }

    public interface Callbacks {
        void onQuerySubmit(List<DanceSong> songs, String query);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new ApiImpl();
        suggestedDanceSongs = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.track_search_suggestion,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        mSearchView.setSuggestionsAdapter(suggestionAdapter);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onQueryTextSubmit(final String query) {
        service.searchSongs(query, null, new Consumer<List<DanceSong>>() {
            @Override
            public void accept(List<DanceSong> danceSongs) {
                ((Callbacks)getActivity()).onQuerySubmit(danceSongs, query);
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.length() > 1) {
            service.suggestSongs(newText, new Consumer<List<DanceSong>>() {
                @Override
                public void accept(List<DanceSong> danceSongs) {
                    mSearchView.getSuggestionsAdapter().swapCursor(createCursor(danceSongs));
                }
            });
        }
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
        Intent intent = new Intent(SearchSuggestionFragment.this.getActivity(),DetailFragmentWrapperActivity.class);
        intent.putExtra("danceSong", suggestedDanceSongs.get(position));
        startActivity(intent);
    }

}

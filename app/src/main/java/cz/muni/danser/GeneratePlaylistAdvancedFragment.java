package cz.muni.danser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cz.muni.danser.api.ApiImpl;
import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.Playlist;

public class GeneratePlaylistAdvancedFragment extends Fragment {

    public GeneratePlaylistAdvancedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_playlist_advanced, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.playlists);
        List<String> options = new ArrayList<>();
        for(Playlist p : SongUtils.getAllPlaylists()){
            options.add(p.getMainText());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(GeneratePlaylistAdvancedFragment.this.getActivity(), android.R.layout.simple_spinner_item, options);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        new ApiImpl().getAllDances(new Consumer<List<Dance>>() {
            @Override
            public void accept(List<Dance> dances) {
                for(Dance dance : dances){
                    CheckBox checkbox = new CheckBox(getActivity());
                    checkbox.setText(Utils.getTranslatedMainText(dance));
                    ((GridLayout)getActivity().findViewById(R.id.include_dances)).addView(checkbox);
                }
            }
        });

        return view;
    }

}

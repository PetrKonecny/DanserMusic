package cz.muni.danser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.muni.danser.model.Playlist;

public class GeneratePlaylistSimpleFragment extends Fragment {

    public GeneratePlaylistSimpleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_playlist_simple, container, false);

        ((RadioGroup)view.findViewById(R.id.playlists_or_presets)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Spinner spinner = (Spinner) GeneratePlaylistSimpleFragment.this.getActivity().findViewById(R.id.generate_spinner);
                List<String> options = new ArrayList<>();
                if(checkedId == R.id.radio_playlists){
                    for(Playlist p : SongUtils.getAllPlaylists()){
                        options.add(p.getMainText());
                    }
                } else {
                    options = Arrays.asList(getResources().getStringArray(R.array.generate_presets));
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(GeneratePlaylistSimpleFragment.this.getActivity(), android.R.layout.simple_spinner_item, options);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
            }
        });

        return view;
    }

}

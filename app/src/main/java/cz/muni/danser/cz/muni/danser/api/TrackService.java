package cz.muni.danser.cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.cz.muni.danser.model.Dance;
import cz.muni.danser.cz.muni.danser.model.DanceCategory;
import cz.muni.danser.cz.muni.danser.model.Track;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Petr2 on 3/24/2016.
 */
public interface TrackService {

    void getAllTracks();

    void getTrack(String mbid);

    void searchTracks(String trackName, Integer limit);

    void suggestTracks(String trackName);
}

package cz.muni.danser.api;

import cz.muni.danser.model.DanceCategory;

/**
 * Created by Petr2 on 3/24/2016.
 */
public interface TrackService {

    void getAllTracks();

    void getTrack(String mbid);

    void searchTracks(String trackName, Integer limit);

    void suggestTracks(String trackName);
}

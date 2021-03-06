package cz.muni.danser.api;

import java.util.LinkedHashMap;
import java.util.List;

import cz.muni.danser.functional.Consumer;
import cz.muni.danser.model.Dance;
import cz.muni.danser.model.DanceCategory;
import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Playlist;

public interface GeneralApi {
    void getCategories(Consumer<List<DanceCategory>> callback);
    void getCategory(String category, Consumer<DanceCategory> callback);

    void getDances(String category, Consumer<List<Dance>> callback);
    void getAllDances(Consumer<List<Dance>> callback);
    void getDance(int danceType, Consumer<Dance> callback);

    void getSongs(int danceType, Consumer<List<DanceSong>> callback);
    void searchSongs(String trackName, Integer limit, Consumer<List<DanceSong>> callback);
    void suggestSongs(String trackName, Consumer<List<DanceSong>> callback);

    void getRecordings(DanceSong danceSong, Consumer<List<DanceRecording>> callback);
    void getManyRecordings(List<DanceSong> danceSongs, List<String> requiredFields, Consumer<LinkedHashMap<DanceSong, List<DanceRecording>>> callback);

    void generatePlaylistFromPreset(int preset, Consumer<List<DanceSong>> callback);
}

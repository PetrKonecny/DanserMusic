package cz.muni.danser.api;

public interface SongService {
    void searchSongs(String trackName, Integer limit);

    void suggestSongs(String trackName);
}

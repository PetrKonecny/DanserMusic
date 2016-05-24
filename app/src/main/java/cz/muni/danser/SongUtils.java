package cz.muni.danser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;

public class SongUtils {
    public static LinkedHashMap<DanceSong, String> getYoutubeIdsForSongs(LinkedHashMap<DanceSong, List<DanceRecording>> map){
        LinkedHashMap<DanceSong, String> ids = new LinkedHashMap<>();
        for(Map.Entry<DanceSong, List<DanceRecording>> e : map.entrySet()){
            for(DanceRecording recording : e.getValue()){
                if(recording.getYoutubeId() != null){
                    ids.put(e.getKey(), recording.getYoutubeId());
                    break;
                }
            }
        }
        return ids;
    }

    public static List<String> getSpotifyUrisForSongs(LinkedHashMap<DanceSong, List<DanceRecording>> map){
        List<String> uris = new ArrayList<>();
        for(Map.Entry<DanceSong, List<DanceRecording>> e : map.entrySet()){
            for(DanceRecording recording : e.getValue()){
                if(recording.getSpotifyId() != null){
                    uris.add("spotify:track:" + recording.getSpotifyId());
                    break;
                }
            }
        }
        return uris;
    }
}

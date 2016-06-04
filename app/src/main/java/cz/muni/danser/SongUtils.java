package cz.muni.danser;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.muni.danser.model.DanceRecording;
import cz.muni.danser.model.DanceSong;
import cz.muni.danser.model.Playlist;
import cz.muni.danser.model.SongPlaylist;

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

    public static List<Playlist> getAllPlaylists(){
        return (List) new Select().all().from(Playlist.class).execute();
    }

    public static void deleteSongFromPlaylist(long songId, long playlistId){
    }

    public static Playlist getPlaylist(long id) {
        return new Select().from(Playlist.class).where("Id = ?",id).executeSingle();
    }

    public static Playlist getPlaylistByName(String name) {
        return new Select().from(Playlist.class).where("playlistName = ?", name).executeSingle();
    }

    public static  void deletePlaylist(long id) {
        Playlist playlist = Playlist.load(Playlist.class,id);
        playlist.delete();
    }
}

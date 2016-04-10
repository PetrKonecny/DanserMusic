package cz.muni.danser.api;

import com.activeandroid.query.Select;

import java.util.List;

import cz.muni.danser.model.Playlist;

/**
 * Created by Petr2 on 4/10/2016.
 */
public class PlaylistServiceImpl implements PlaylistService {
    @Override
    public List<Playlist> getPlaylists() {
        return new Select().all().from(Playlist.class).execute();
    }

    @Override
    public Playlist getPlaylist(String id) {
        return new Select().from(Playlist.class).where("id =?",id).executeSingle();
    }

    @Override
    public Playlist getPlaylistByName(String name) {
        return new Select().from(Playlist.class).where("playlistName = ?", name).executeSingle();
    }
}

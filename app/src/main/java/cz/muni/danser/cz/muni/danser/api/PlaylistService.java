package cz.muni.danser.cz.muni.danser.api;

import java.util.List;

import cz.muni.danser.cz.muni.danser.model.Playlist;

/**
 * Created by Petr2 on 4/10/2016.
 */
public interface PlaylistService {

    List<Playlist> getPlaylists();

    Playlist getPlaylist(String id);

    Playlist getPlaylistByName(String name);

}

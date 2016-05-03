package cz.muni.danser.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by Petr2 on 3/27/2016.
 */
@Table(name = "SongPlaylists")
public class SongPlaylist extends Model {

    @Column(name = "DanceSong", onDelete= Column.ForeignKeyAction.CASCADE)
    public DanceSong danceSong;
    @Column(name = "Playlist", onDelete= Column.ForeignKeyAction.CASCADE)
    public Playlist playlist;

    public List<DanceSong> songs() {
        return getMany(DanceSong.class, "SongPlaylists");
    }
    public List<Playlist> playlists() {
        return getMany(Playlist.class, "SongPlaylists");
    }
}

package cz.muni.danser;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by Petr2 on 3/27/2016.
 */
@Table(name = "TrackPlaylist")
public class TrackPlaylist extends Model {

    @Column(name = "Track", onDelete= Column.ForeignKeyAction.CASCADE)
    public Track track;
    @Column(name = "Playlist", onDelete= Column.ForeignKeyAction.CASCADE)
    public Playlist playlist;

    public List<Track> tracks() {
        return getMany(Track.class, "TrackPlaylist");
    }
    public List<Playlist> playlists() {
        return getMany(Playlist.class, "TrackPlaylist");
    }
}

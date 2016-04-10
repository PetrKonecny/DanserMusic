package cz.muni.danser.cz.muni.danser.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Petr2 on 3/27/2016.
 */

@Table(name = "Playlists")
public class Playlist extends Model implements Listable {

    @Column(name = "PlaylistName")
    public String playlistName;

    public List<Track> tracks() {
        return new Select().from(Track.class)
                .innerJoin(TrackPlaylist.class)
                .on("TrackPlaylist.Track = Tracks.Id")
                .where("TrackPlaylist.Playlist = ?",this.getId())
                .execute();
    }

    @Override
    public String getMainText() {
        return playlistName;
    }
}

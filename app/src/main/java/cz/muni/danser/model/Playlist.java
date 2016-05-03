package cz.muni.danser.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Playlists")
public class Playlist extends Model implements Listable {

    @Column(name = "PlaylistName")
    public String playlistName;

    public List<DanceSong> songs() {
        return new Select().from(DanceSong.class)
                .innerJoin(SongPlaylist.class)
                .on("SongPlaylists.DanceSong = DanceSong.Id")
                .where("SongPlaylists.Playlist = ?",this.getId())
                .execute();
    }

    @Override
    public String getMainText() {
        return playlistName;
    }
}

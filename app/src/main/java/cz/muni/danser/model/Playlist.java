package cz.muni.danser.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Playlists")
public class Playlist extends Model implements Listable, Parcelable {

    @Column(name = "PlaylistName")
    public String playlistName;

    public Playlist(){

    }

    protected Playlist(Parcel in) {
        playlistName = in.readString();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public List<DanceSong> songs() {
        return new Select().from(DanceSong.class)
                .innerJoin(SongPlaylist.class)
                .on("SongPlaylists.DanceSong = Songs.Id")
                .where("SongPlaylists.Playlist = ?",this.getId())
                .execute();
    }

    @Override
    public String getMainText() {
        return playlistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playlistName);
    }
}

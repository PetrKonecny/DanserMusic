package cz.muni.danser.model;

/**
 * Created by Pavel on 3. 5. 2016.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.muni.danser.api.Api;

@Table(name = "DanceSong")
public class DanceSong extends Model implements Parcelable, Listable, Comparable<DanceSong> {
    @Expose
    @SerializedName("song_for_dance_id")
    @Column(name = "id", index = true, unique = true)
    private int songForDanceId;

    @Expose
    @SerializedName("song_id")
    @Column(name = "songId", index = true)
    private int songId;

    @Expose
    @SerializedName("song_name")
    @NonNull
    private String songName;

    @Expose
    @SerializedName("work_mbid")
    private String workMbid;

    @Expose
    private int dance;

    protected DanceSong(Parcel in) {
        songForDanceId = in.readInt();
        songId = in.readInt();
        songName = in.readString();
        workMbid = in.readString();
        dance = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(songForDanceId);
        dest.writeInt(songId);
        dest.writeString(songName);
        dest.writeString(workMbid);
        dest.writeInt(dance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DanceSong> CREATOR = new Creator<DanceSong>() {
        @Override
        public DanceSong createFromParcel(Parcel in) {
            return new DanceSong(in);
        }

        @Override
        public DanceSong[] newArray(int size) {
            return new DanceSong[size];
        }
    };

    @Override
    public String getMainText() {
        return getSongName();
    }

    @Override
    public int compareTo(@NonNull DanceSong another) {
        return getMainText().compareToIgnoreCase(another.getMainText());
    }

    public boolean getIsFavorite() {
        Playlist favorites = new Select().from(Playlist.class).where("PlaylistName = 'Favorite'").executeSingle();
        return favorites.songs().contains(this);
    }

    public boolean favoriteSong() {
        Playlist favorites = new Select().from(Playlist.class).where("PlaylistName = 'Favorite'").executeSingle();
        if (favorites == null) {
            favorites = new Playlist();
            favorites.playlistName = "Favorite";
            favorites.save();
        }

        if(!getIsFavorite()) {
            SongPlaylist songPlaylist = new SongPlaylist();
            songPlaylist.danceSong = this;
            songPlaylist.playlist = favorites;
            this.save();
            songPlaylist.save();
        }else{
            return false;
        }
        return true;
    }

    public int getSongForDanceId() {
        return songForDanceId;
    }

    public void setSongForDanceId(int songForDanceId) {
        this.songForDanceId = songForDanceId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @NonNull
    public String getSongName() {
        return songName;
    }

    public void setSongName(@NonNull String songName) {
        this.songName = songName;
    }

    public String getWorkMbid() {
        return workMbid;
    }

    public void setWorkMbid(String workMbid) {
        this.workMbid = workMbid;
    }

    public int getDance() {
        return dance;
    }

    public void setDance(int dance) {
        this.dance = dance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DanceSong danceSong = (DanceSong) o;

        return songForDanceId == danceSong.songForDanceId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + songForDanceId;
        return result;
    }

    @NonNull
    public List<DanceRecording> listRecordings(){
        try {
            return Api.getRetrofitApi().getRecordings(this.songForDanceId).execute().body();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}

package cz.muni.danser.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Table(name = "Songs")
public class DanceSong extends Model implements Parcelable, Listable {
    @Expose
    @SerializedName("song_for_dance_id")
    @Column(name = "songForDanceId")
    private int songForDanceId;

    @Expose
    @SerializedName("song_id")
    @Column(name = "songId", index = true)
    private int songId;

    @Expose
    @SerializedName("song_name")
    @Column(name = "songName")
    private String songName;

    @Expose
    @SerializedName("work_mbid")
    @Column(name = "workMbid")
    private String workMbid;

    @Expose
    @Column(name = "dance")
    private Dance dance;

    public DanceSong(){
        super();
    }

    protected DanceSong(Parcel in) {
        songForDanceId = in.readInt();
        songId = in.readInt();
        songName = in.readString();
        workMbid = in.readString();
        dance = in.readParcelable(Dance.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(songForDanceId);
        dest.writeInt(songId);
        dest.writeString(songName);
        dest.writeString(workMbid);
        dest.writeParcelable(dance, flags);
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
            if(this.dance.getIsUnique()) {
                this.dance.save();
            }else{
                this.dance = new Select().from(Dance.class).where("DanceType = ?",this.dance.getDanceType()).executeSingle();
            }
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

    public Dance getDance() {
        return dance;
    }

    public void setDance(Dance dance) {
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

    @Override
    public String toString() {
        return "DanceSong{" +
                "songForDanceId=" + songForDanceId +
                ", songId=" + songId +
                ", songName='" + songName + '\'' +
                ", workMbid='" + workMbid + '\'' +
                ", dance=" + dance +
                '}';
    }

    public List<DanceRecording> listDanceRecordings() {
        return getMany(DanceRecording.class, "danceSong");
    }
}

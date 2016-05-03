package cz.muni.danser.model;

/**
 * Created by Pavel on 3. 5. 2016.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

@Table(name = "DanceSong")
public class DanceSong extends Model implements Parcelable, Listable, Comparable<DanceSong> {
    @Expose
    @SerializedName("song_id")
    private int songId;

    @Expose
    @SerializedName("song_name")
    private String songName;

    @Expose
    @SerializedName("recording_mbids")
    private Set<String> recordingMbids;

    @Expose
    private int length;

    @Expose
    private float bpm;

    @Expose
    @SerializedName("work_mbid")
    private String workMbid;

    @Expose
    @SerializedName("dance_type")
    private int danceType;

    @Expose
    @SerializedName("artist_names")
    private String artistNames;

    @Expose
    @SerializedName("youtube_ids")
    private Set<String> youtubeIds;

    @Expose
    @SerializedName("spotify_ids")
    private Set<String> spotifyIds;

    @Expose
    @SerializedName("release_mbids")
    private Set<String> releaseMbids;

    @Expose
    private String releases;

    public String getReleaseYears() {
        return releaseYears;
    }

    public void setReleaseYears(String releaseYears) {
        this.releaseYears = releaseYears;
    }

    @Expose
    private String releaseYears;

    @Expose
    private String tempo;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Set<String> getRecordingMbids() {
        return recordingMbids;
    }

    public void setRecordingMbids(Set<String> recordingMbids) {
        this.recordingMbids = recordingMbids;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public float getBpm() {
        return bpm;
    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

    public String getWorkMbid() {
        return workMbid;
    }

    public void setWorkMbid(String workMbid) {
        this.workMbid = workMbid;
    }

    public int getDanceType() {
        return danceType;
    }

    public void setDanceType(int danceType) {
        this.danceType = danceType;
    }

    public String getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(String artistNames) {
        this.artistNames = artistNames;
    }

    public Set<String> getYoutubeIds() {
        return youtubeIds;
    }

    public void setYoutubeIds(Set<String> youtubeIds) {
        this.youtubeIds = youtubeIds;
    }

    public Set<String> getSpotifyIds() {
        return spotifyIds;
    }

    public void setSpotifyIds(Set<String> spotifyIds) {
        this.spotifyIds = spotifyIds;
    }

    public Set<String> getReleaseMbids() {
        return releaseMbids;
    }

    public void setReleaseMbids(Set<String> releaseMbids) {
        this.releaseMbids = releaseMbids;
    }

    public String getReleases() {
        return releases;
    }

    public void setReleases(String releases) {
        this.releases = releases;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public DanceSong() {
        super();
    }

    protected DanceSong(Parcel in) {
        songId = in.readInt();
        songName = in.readString();
        length = in.readInt();
        bpm = in.readFloat();
        workMbid = in.readString();
        danceType = in.readInt();
        artistNames = in.readString();
        releases = in.readString();
        releaseYears = in.readString();
        tempo = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(songId);
        dest.writeString(songName);
        dest.writeInt(length);
        dest.writeFloat(bpm);
        dest.writeString(workMbid);
        dest.writeInt(danceType);
        dest.writeString(artistNames);
        dest.writeString(releases);
        dest.writeString(releaseYears);
        dest.writeString(tempo);
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
}

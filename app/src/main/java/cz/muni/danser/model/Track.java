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

@Table(name = "Tracks")
public class Track extends Model implements Parcelable, Listable, Comparable<Track> {

    @Expose
    @Column(name = "Mdid", index = true, unique = true)
    private String mbid;
    @Expose
    @Column(name = "TrackName")
    @SerializedName("track_name")
    private String trackName;
    @Expose
    @Column(name = "DanceType")
    @SerializedName("dance_type")
    private int danceType;
    @Expose
    @Column(name = "Artist")
    @SerializedName("artist_name")
    private String artistName;
    @Expose
    @Column(name = "SpotifyId")
    @SerializedName("spotify_ids")
    private String spotifyId;
    @Expose
    @Column(name = "YoutubeId")
    @SerializedName("youtube_ids")
    private String youtubeId;

    public String getReleaseMbid() {
        return releaseMbid;
    }

    public void setReleaseMbid(String releaseMbid) {
        this.releaseMbid = releaseMbid;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Expose
    @Column(name = "ReleaseMbid")
    @SerializedName("release_mbid")
    private String releaseMbid;
    @Expose
    @Column(name = "ReleaseName")
    @SerializedName("release_name")
    private String releaseName;
    @Expose
    @Column(name = "ReleaseYear")
    @SerializedName("release_year")
    private int releaseYear;

    public Track() {
        super();
    }

    public boolean favoriteTrack() {

        Playlist favorites = new Select().from(Playlist.class).where("PlaylistName = 'Favorite'").executeSingle();
        if (favorites == null) {
            favorites = new Playlist();
            favorites.playlistName = "Favorite";
            favorites.save();
        }

        if(!getIsFavorite()) {
            TrackPlaylist trackPlaylist = new TrackPlaylist();
            trackPlaylist.track = this;
            trackPlaylist.playlist = favorites;
            this.save();
            trackPlaylist.save();
        }else{
            return false;
        }
        return true;
    }

    public boolean getIsFavorite() {
        Playlist favorites = new Select().from(Playlist.class).where("PlaylistName = 'Favorite'").executeSingle();
        return favorites.tracks().contains(this);
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public int getDanceType() {
        return danceType;
    }

    public void setDanceType(int danceType) {
        this.danceType = danceType;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMbid());
        dest.writeString(getTrackName());
        dest.writeInt(getDanceType());
        dest.writeString(getArtistName());
        dest.writeString(getSpotifyId());
        dest.writeString(getYoutubeId());
        dest.writeString(getReleaseMbid());
        dest.writeString(getReleaseName());
        dest.writeInt(getReleaseYear());
    }

    public Track (Parcel in){
        setMbid(in.readString());
        setTrackName(in.readString());
        setDanceType(in.readInt());
        setArtistName(in.readString());
        setSpotifyId(in.readString());
        setYoutubeId(in.readString());
        setReleaseMbid(in.readString());
        setReleaseName(in.readString());
        setReleaseYear(in.readInt());
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {

        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        return getMbid().equals(track.getMbid());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mbid.hashCode();
        return result;
    }

    @Override
    public String getMainText() {
        return getTrackName();
    }

    @Override
    public int compareTo(@NonNull Track another) {
        return getMainText().compareToIgnoreCase(another.getMainText());
    }
}
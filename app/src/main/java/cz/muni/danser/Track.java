package cz.muni.danser;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Table(name = "Tracks")
public class Track extends Model implements Parcelable {

    @Column(name = "Mdid", index = true, unique = true)
    private String mbid;
    @Column(name = "TrackName")
    @SerializedName("track_name")
    private String trackName;
    @Column(name = "DanceType")
    @SerializedName("dance_type")
    private int danceType;
    @Column(name = "Artist")
    @SerializedName("artist_mbid")
    private String artistMbid;
    @Column(name = "SpotifyId")
    @SerializedName("spotify_id")
    private String spotifyId;
    @Column(name = "YoutubeId")
    @SerializedName("youtube_id")
    private String youtubeId;

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
        System.out.println(this.getMbid());
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

    public String getArtistMbid() {
        return artistMbid;
    }

    public void setArtistMbid(String artistMbid) {
        this.artistMbid = artistMbid;
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
        dest.writeString(getArtistMbid());
        dest.writeString(getSpotifyId());
        dest.writeString(getYoutubeId());
    }

    public Track (Parcel in){
        setMbid(in.readString());
        setTrackName(in.readString());
        setDanceType(in.readInt());
        setArtistMbid(in.readString());
        setSpotifyId(in.readString());
        setYoutubeId(in.readString());
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
}
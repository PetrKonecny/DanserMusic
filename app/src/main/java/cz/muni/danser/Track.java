package cz.muni.danser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Track implements Parcelable {

    private String mbid;
    @SerializedName("track_name")
    private String trackName;
    @SerializedName("dance_type")
    private int danceType;
    @SerializedName("artist_mbid")
    private String artistMbid;
    @SerializedName("spotify_id")
    private String spotifyId;
    @SerializedName("youtube_id")
    private String youtubeId;

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
}
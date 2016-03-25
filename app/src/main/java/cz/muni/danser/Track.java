package cz.muni.danser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Track implements Parcelable {

    @SerializedName("mbid")
    @Expose
    private String mbid;
    @SerializedName("track_name")
    @Expose
    private String trackName;
    @SerializedName("dance_type")
    @Expose
    private String danceType;
    @SerializedName("artist_mbid")
    @Expose
    private String artistMbid;
    @SerializedName("spotify_id")
    @Expose
    private String spotifyId;
    @SerializedName("youtube_id")
    @Expose
    private String youtubeId;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("added")
    @Expose
    private String added;

    /**
     *
     * @return
     * The mbid
     */
    public String getMbid() {
        return mbid;
    }

    /**
     *
     * @param mbid
     * The mbid
     */
    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    /**
     *
     * @return
     * The trackName
     */
    public String getTrackName() {
        return trackName;
    }

    /**
     *
     * @param trackName
     * The track_name
     */
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    /**
     *
     * @return
     * The danceType
     */
    public String getDanceType() {
        return danceType;
    }

    /**
     *
     * @param danceType
     * The dance_type
     */
    public void setDanceType(String danceType) {
        this.danceType = danceType;
    }

    /**
     *
     * @return
     * The artistMbid
     */
    public String getArtistMbid() {
        return artistMbid;
    }

    /**
     *
     * @param artistMbid
     * The artist_mbid
     */
    public void setArtistMbid(String artistMbid) {
        this.artistMbid = artistMbid;
    }

    /**
     *
     * @return
     * The spotifyId
     */
    public String getSpotifyId() {
        return spotifyId;
    }

    /**
     *
     * @param spotifyId
     * The spotify_id
     */
    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    /**
     *
     * @return
     * The youtubeId
     */
    public String getYoutubeId() {
        return youtubeId;
    }

    /**
     *
     * @param youtubeId
     * The youtube_id
     */
    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    /**
     *
     * @return
     * The updated
     */
    public String getUpdated() {
        return updated;
    }

    /**
     *
     * @param updated
     * The updated
     */
    public void setUpdated(String updated) {
        this.updated = updated;
    }

    /**
     *
     * @return
     * The added
     */
    public String getAdded() {
        return added;
    }

    /**
     *
     * @param added
     * The added
     */
    public void setAdded(String added) {
        this.added = added;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMbid());
        dest.writeString(getTrackName());
        dest.writeString(getDanceType());
        dest.writeString(getArtistMbid());
        dest.writeString(getSpotifyId());
        dest.writeString(getYoutubeId());
        dest.writeString(getUpdated());
        dest.writeString(getAdded());
    }

    public Track (Parcel in){
        setMbid(in.readString());
        setTrackName(in.readString());
        setDanceType(in.readString());
        setArtistMbid(in.readString());
        setSpotifyId(in.readString());
        setYoutubeId(in.readString());
        setUpdated(in.readString());
        setAdded(in.readString());
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
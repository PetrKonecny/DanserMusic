package cz.muni.danser.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Table(name = "Recordings")
public class DanceRecording extends DanceSong {
    @Expose
    @Column(name = "Mbid", index = true, unique = true)
    @SerializedName("recording_mbid")
    @NonNull
    private String recordingMbid;

    @Expose
    @Column(name = "Name")
    @SerializedName("recording_name")
    private String recordingName;

    @Expose
    @Column(name = "ArtistName")
    @SerializedName("artist_name")
    private String artistName;

    @Expose
    @Column(name = "Length")
    private int length;

    @Expose
    @Column(name = "SpotifyId")
    @SerializedName("spotify_id")
    private String spotifyId;

    @Expose
    @Column(name = "YoutubeId")
    @SerializedName("youtube_id")
    private String youtubeId;

    @Expose
    private float bpm;

    @Expose
    @SerializedName("release_mbid")
    private String releaseMbid;
    @Expose
    @SerializedName("release_name")
    private String releaseName;
    @Expose
    @SerializedName("release_year")
    private int releaseYear;

    protected DanceRecording(Parcel in) {
        super(in);
        recordingMbid = in.readString();
        recordingName = in.readString();
        artistName = in.readString();
        length = in.readInt();
        spotifyId = in.readString();
        youtubeId = in.readString();
        bpm = in.readFloat();
        releaseMbid = in.readString();
        releaseName = in.readString();
        releaseYear = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(recordingMbid);
        dest.writeString(recordingName);
        dest.writeString(artistName);
        dest.writeInt(length);
        dest.writeString(spotifyId);
        dest.writeString(youtubeId);
        dest.writeFloat(bpm);
        dest.writeString(releaseMbid);
        dest.writeString(releaseName);
        dest.writeInt(releaseYear);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DanceRecording> CREATOR = new Creator<DanceRecording>() {
        @Override
        public DanceRecording createFromParcel(Parcel in) {
            return new DanceRecording(in);
        }

        @Override
        public DanceRecording[] newArray(int size) {
            return new DanceRecording[size];
        }
    };

    public String getRecordingMbid() {
        return recordingMbid;
    }

    public void setRecordingMbid(String recordingMbid) {
        this.recordingMbid = recordingMbid;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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

    public float getBpm() {
        return bpm;
    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

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

    @Override
    public String getMainText() {
        return getRecordingName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DanceRecording that = (DanceRecording) o;

        return recordingMbid.equals(that.recordingMbid);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + recordingMbid.hashCode();
        return result;
    }
}
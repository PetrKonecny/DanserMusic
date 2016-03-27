package cz.muni.danser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavel on 27. 3. 2016.
 */
public class Dance implements Parcelable {
    @SerializedName("dance_type")
    private int danceType;
    @SerializedName("dance_name")
    private String danceName;

    protected Dance(Parcel in) {
        danceType = in.readInt();
        danceName = in.readString();
    }

    public static final Creator<Dance> CREATOR = new Creator<Dance>() {
        @Override
        public Dance createFromParcel(Parcel in) {
            return new Dance(in);
        }

        @Override
        public Dance[] newArray(int size) {
            return new Dance[size];
        }
    };

    public int getDanceType() {
        return danceType;
    }

    public void setDanceType(int danceType) {
        this.danceType = danceType;
    }

    public String getDanceName() {
        return danceName;
    }

    public void setDanceName(String danceName) {
        this.danceName = danceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getDanceType());
        dest.writeString(getDanceName());
    }
}

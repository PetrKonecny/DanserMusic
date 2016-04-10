package cz.muni.danser.cz.muni.danser.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavel on 27. 3. 2016.
 */
public class Dance implements Parcelable, Listable, StringParsable, Comparable<Dance>, Translatable {
    @Expose
    @SerializedName("dance_type")
    private int danceType;
    @Expose
    @SerializedName("dance_name")
    private String danceName;

    @Expose
    @SerializedName("dance_category")
    private String danceCategory;

    public String getDanceCategory() {
        return danceCategory;
    }

    public void setDanceCategory(String danceCategory) {
        this.danceCategory = danceCategory;
    }

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

    @Override
    public String getMainText() {
        return getDanceName();
    }

    @Override
    public Map<String,String> getResourceMap() {
        Map<String,String> map = new HashMap<>();
        map.put("mainTitle","dance_type_" + String.valueOf(getDanceType()));
        return map;
    }

    @Override
    public int compareTo(@NonNull Dance another) {
        return getMainText().compareToIgnoreCase(another.getMainText());
    }
}

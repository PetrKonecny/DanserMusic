package cz.muni.danser.model;

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
public class DanceCategory implements Parcelable, StringParsable, Listable, Comparable<DanceCategory>, Translatable {
    public String getDanceCategory() {
        return danceCategory;
    }

    public void setDanceCategory(String danceCategory) {
        this.danceCategory = danceCategory;
    }

    @Expose
    @SerializedName("dance_category")
    private String danceCategory;

    protected DanceCategory(Parcel in) {
        danceCategory = in.readString();
    }

    public static final Creator<DanceCategory> CREATOR = new Creator<DanceCategory>() {
        @Override
        public DanceCategory createFromParcel(Parcel in) {
            return new DanceCategory(in);
        }

        @Override
        public DanceCategory[] newArray(int size) {
            return new DanceCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(danceCategory);
    }

    @Override
    public String getMainText() {
        return getDanceCategory();
    }

    @Override
    public Map<String,String> getResourceMap() {
        Map<String,String> map = new HashMap<>();
        map.put("mainTitle","dance_category_" + getDanceCategory());
        return map;
    }

    @Override
    public int compareTo(@NonNull DanceCategory another) {
        return getMainText().compareToIgnoreCase(another.getMainText());
    }
}

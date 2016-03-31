package cz.muni.danser;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavel on 27. 3. 2016.
 */
public class DanceCategory extends ListableTImpl implements Parcelable {
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
    public String getTranslationIdentifier() {
        return "dance_category_" + getDanceCategory();
    }
}

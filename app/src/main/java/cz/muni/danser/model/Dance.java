package cz.muni.danser.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

@Table(name = "Dances")
public class Dance extends Model implements Parcelable, Listable, StringParsable, Comparable<Dance>, Translatable {
    @Expose
    @SerializedName("dance_type")
    @Column(name = "DanceType", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int danceType;

    @Expose
    @SerializedName("dance_name")
    @Column(name = "DanceName")
    private String danceName;

    @Expose
    @SerializedName("dance_category")
    @Column(name = "DanceCategory")
    private String danceCategory;

    protected Dance(Parcel in) {
        danceType = in.readInt();
        danceName = in.readString();
        danceCategory = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(danceType);
        dest.writeString(danceName);
        dest.writeString(danceCategory);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getDanceCategory() {
        return danceCategory;
    }

    public void setDanceCategory(String danceCategory) {
        this.danceCategory = danceCategory;
    }

    public Dance(){
        super();
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Dance dance = (Dance) o;

        return danceType == dance.danceType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + danceType;
        return result;
    }

    @Override
    public String toString() {
        return "Dance{" +
                "danceType=" + danceType +
                ", danceName='" + danceName + '\'' +
                ", danceCategory='" + danceCategory + '\'' +
                '}';
    }
}

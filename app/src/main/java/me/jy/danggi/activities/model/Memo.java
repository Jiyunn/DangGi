package me.jy.danggi.activities.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by JY on 2018-01-12.
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Memo implements Parcelable{

    private String content;
    private Date writeDate;

    private Memo(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel ( Parcel in ) {
        this.content = in.readString();
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel ( Parcel out, int flags ) {
        out.writeString(content);
    }

    public static final Parcelable.Creator<Memo> CREATOR = new Parcelable.Creator<Memo> () {
        public Memo createFromParcel (Parcel in) {
            return new Memo(in);
        }
        @Override
        public Memo[] newArray ( int size ) {
            return new Memo[size];
        }
    };
}

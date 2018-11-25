package com.example.david.lists.data.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This provides a uniform way to pass information about an
 * {@link Group} or {@link Item} that is being edited.
 */
public final class EditingInfo implements Parcelable {
    private final String id;
    private final String title;

    public EditingInfo(Group group) {
        this.id = group.getId();
        this.title = group.getTitle();
    }

    public EditingInfo(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    // Parcelable implementation below

    private EditingInfo(Parcel in) {
        id = in.readString();
        title = in.readString();
    }

    public static final Creator<EditingInfo> CREATOR = new Creator<EditingInfo>() {
        @Override
        public EditingInfo createFromParcel(Parcel in) {
            return new EditingInfo(in);
        }

        @Override
        public EditingInfo[] newArray(int size) {
            return new EditingInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
    }
}

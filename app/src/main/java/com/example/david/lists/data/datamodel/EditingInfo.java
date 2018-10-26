package com.example.david.lists.data.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This provides a uniform way to pass information about an
 * {@link UserList} or {@link Item} that is being edited.
 */
public final class EditingInfo implements Parcelable {
    private final int id;
    private final String title;


    public EditingInfo(UserList userList) {
        this.id = userList.getId();
        this.title = userList.getTitle();
    }

    public EditingInfo(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    // Parcelable implementation below

    private EditingInfo(Parcel in) {
        id = in.readInt();
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
        dest.writeInt(id);
        dest.writeString(title);
    }
}

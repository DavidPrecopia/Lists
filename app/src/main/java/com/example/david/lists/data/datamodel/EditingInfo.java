package com.example.david.lists.data.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This provides a uniform way to pass information about an
 * {@link UserList} or {@link Item} that is being edited.
 */
public final class EditingInfo implements Parcelable {
    private final String id;
    private final String title;
    private final String userListId;


    public EditingInfo(UserList userList) {
        this.id = userList.getId();
        this.title = userList.getTitle();
        this.userListId = userList.getId();
    }

    public EditingInfo(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.userListId = item.getUserListId();
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUserListId() {
        return userListId;
    }


    // Parcelable implementation below

    private EditingInfo(Parcel in) {
        id = in.readString();
        title = in.readString();
        userListId = in.readString();
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
        dest.writeString(userListId);
    }
}

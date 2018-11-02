package com.example.david.lists.util;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import java.util.ArrayList;
import java.util.List;

public final class MyUtil {
    private MyUtil() {
    }

    public static List<String> getUserListsIds(List<UserList> userLists) {
        List<String> userListsIds = new ArrayList<>();
        for (UserList userList : userLists) {
            userListsIds.add(userList.getId());
        }
        return userListsIds;
    }

    public static List<String> getItemIds(List<Item> items) {
        List<String> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        return itemIds;
    }
}

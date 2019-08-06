package com.example.david.lists.view.userlistlist;

import com.example.david.lists.data.datamodel.UserList;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UserListDiffCallbackTest {

    private String oneId = "id_one";
    private String oneTitle = "title_one";
    private int onePosition = 1;
    private UserList one = new UserList(oneId, new UserList(oneTitle, onePosition));

    private String twoId = "id_two";
    private String twoTitle = "title_two";
    private int twoPosition = 2;
    private UserList two = new UserList(twoId, new UserList(twoTitle, twoPosition));

    private UserListDiffCallback diffCallback = new UserListDiffCallback();


    @Test
    public void areItemsTheSameTrue() {
        assertThat(
                diffCallback.areItemsTheSame(one, one),
                is(true)
        );
    }

    @Test
    public void areItemsTheSameFalse() {
        assertThat(
                diffCallback.areItemsTheSame(one, two),
                is(false)
        );
    }


    @Test
    public void areContentsTheSameTrue() {
        assertThat(
                diffCallback.areContentsTheSame(one, one),
                is(true)
        );
    }

    @Test
    public void areContentsTheSameFalse() {
        assertThat(
                diffCallback.areContentsTheSame(one, two),
                is(false)
        );
    }
}
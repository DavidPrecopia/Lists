package com.example.david.lists.view.itemlist;

import com.example.david.lists.data.datamodel.Item;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ItemDiffCallbackTest {

    private String userListId = "qwerty";

    private String oneId = "id_one";
    private String oneTitle = "title_one";
    private int onePosition = 0;
    private Item one = new Item(oneId, new Item(oneTitle, onePosition, userListId));

    private String twoId = "id_two";
    private String twoTitle = "title_two";
    private int twoPosition = 1;
    private Item two = new Item(twoId, new Item(twoTitle, twoPosition, userListId));


    private ItemDiffCallback diffCallback = new ItemDiffCallback();


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
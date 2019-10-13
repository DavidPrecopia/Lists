package com.example.david.lists.view.itemlist

import com.example.david.lists.data.datamodel.Item
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ItemDiffCallbackTest {

    private val userListId = "qwerty"

    private val one = Item("title_one", 0, userListId, "id_one")
    private val two = Item("title_two", 1, userListId, "id_two")

    private val diffCallback = ItemDiffCallback()


    @Test
    fun `areItemsTheSame - True`() {
        assertThat(diffCallback.areItemsTheSame(one, one)).isTrue()
    }

    @Test
    fun `areItemsTheSame - False`() {
        assertThat(diffCallback.areItemsTheSame(one, two)).isFalse()
    }


    @Test
    fun `areContentsTheSame - True`() {
        assertThat(diffCallback.areContentsTheSame(one, one)).isTrue()
    }

    @Test
    fun `areContentsTheSame - False`() {
        assertThat(diffCallback.areContentsTheSame(one, two)).isFalse()
    }
}
package com.example.david.lists.view.itemlist

import com.example.david.lists.data.datamodel.Item
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class ItemDiffCallbackTest {

    private val userListId = "qwerty"

    private val oneId = "id_one"
    private val oneTitle = "title_one"
    private val onePosition = 0
    private val one = Item(oneTitle, onePosition, userListId, oneId)

    private val twoId = "id_two"
    private val twoTitle = "title_two"
    private val twoPosition = 1
    private val two = Item(twoTitle, twoPosition, userListId, twoId)


    private val diffCallback = ItemDiffCallback()


    @Test
    fun areItemsTheSameTrue() {
        assertThat(
                diffCallback.areItemsTheSame(one, one),
                `is`(true)
        )
    }

    @Test
    fun areItemsTheSameFalse() {
        assertThat(
                diffCallback.areItemsTheSame(one, two),
                `is`(false)
        )
    }


    @Test
    fun areContentsTheSameTrue() {
        assertThat(
                diffCallback.areContentsTheSame(one, one),
                `is`(true)
        )
    }

    @Test
    fun areContentsTheSameFalse() {
        assertThat(
                diffCallback.areContentsTheSame(one, two),
                `is`(false)
        )
    }
}
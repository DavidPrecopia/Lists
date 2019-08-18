package com.example.david.lists.view.userlistlist

import com.example.david.lists.data.datamodel.UserList
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class UserListDiffCallbackTest {

    private val oneId = "id_one"
    private val oneTitle = "title_one"
    private val onePosition = 1
    private val one = UserList(oneTitle, onePosition, oneId)

    private val twoId = "id_two"
    private val twoTitle = "title_two"
    private val twoPosition = 2
    private val two = UserList(twoTitle, twoPosition, twoId)

    private val diffCallback = UserListDiffCallback()


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
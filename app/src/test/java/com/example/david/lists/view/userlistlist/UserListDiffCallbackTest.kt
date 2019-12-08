package com.example.david.lists.view.userlistlist

import com.example.domain.datamodel.UserList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserListDiffCallbackTest {

    private val one = UserList("title_one", 1, "id_one")
    private val two = UserList("title_two", 2, "id_two")

    private val diffCallback = UserListDiffCallback()


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
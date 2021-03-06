package com.precopia.androiddata.datamodel

import com.precopia.androiddata.remote.RemoteRepositoryConstants

/**
 * ATTENTION!
 * Field names need to match the constants in [RemoteRepositoryConstants].
 *
 * @param id Generated by Firebase.
 */
data class FirebaseUserList(val title: String,
                            val position: Int,
                            val id: String) {
    /**
     * Required by Firestore.
     */
    constructor() : this("", 0, "")
}
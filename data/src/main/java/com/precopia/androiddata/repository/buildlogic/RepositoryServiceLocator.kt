package com.precopia.androiddata.repository.buildlogic

import com.precopia.androiddata.remote.IRemoteRepositoryContract
import com.precopia.androiddata.remote.buildlogic.RemoteRepoModule
import com.precopia.androiddata.repository.Repository
import com.precopia.domain.repository.IRepositoryContract

class RepositoryServiceLocator(userRepo: IRepositoryContract.UserRepository) {

    private val remoteRepo: IRemoteRepositoryContract.Repository =
            RemoteRepoModule(userRepo.userSignedOutObservable()).remoteRepo()

    private val repo: IRepositoryContract.Repository =
            Repository(remoteRepo)


    fun repository() = repo
}
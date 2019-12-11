package com.example.androiddata.repository.buildlogic

import com.example.androiddata.remote.IRemoteRepositoryContract
import com.example.androiddata.remote.buildlogic.RemoteRepoModule
import com.example.androiddata.repository.Repository
import com.example.domain.repository.IRepositoryContract

class RepositoryServiceLocator(userRepo: IRepositoryContract.UserRepository) {

    private val remoteRepo: IRemoteRepositoryContract.Repository =
            RemoteRepoModule(userRepo.userSignedOutObservable()).remoteRepo()

    private val repo: IRepositoryContract.Repository =
            Repository(remoteRepo)


    fun repository() = repo
}
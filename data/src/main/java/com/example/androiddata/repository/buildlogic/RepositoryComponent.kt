package com.example.androiddata.repository.buildlogic

import com.example.androiddata.common.DataScope
import com.example.androiddata.remote.buildlogic.RemoteRepositoryModule
import com.example.domain.repository.IRepositoryContract
import dagger.Subcomponent

@DataScope
@Subcomponent(modules = [
    RepositoryModule::class,
    UserRepositoryModule::class,
    RemoteRepositoryModule::class
])
interface RepositoryComponent {
    fun repo(): IRepositoryContract.Repository

    fun userRepo(): IRepositoryContract.UserRepository
}
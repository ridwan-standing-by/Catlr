package com.ridwanstandingby.catlr.di

import com.ridwanstandingby.catlr.data.CatGraphicApiClient
import com.ridwanstandingby.catlr.data.CatGraphicPagingSource
import com.ridwanstandingby.catlr.ui.CatGraphicViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object KoinInjector {

    fun buildModule() =
        module {
            defineDomainComponents()
            defineUiComponents()
        }

    private fun Module.defineDomainComponents() {
        single { CatGraphicApiClient() }
        factory { CatGraphicPagingSource(get()) }
    }

    private fun Module.defineUiComponents() {
        viewModel { CatGraphicViewModel() }
    }
}
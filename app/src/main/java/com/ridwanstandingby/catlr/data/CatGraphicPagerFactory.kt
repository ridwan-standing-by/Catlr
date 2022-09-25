package com.ridwanstandingby.catlr.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import org.koin.core.component.KoinComponent

class CatGraphicPagerFactory(private val catGraphicApiClient: CatGraphicApiClient) : KoinComponent {

    fun build() = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = true,
            maxSize = 300
        )
    ) { CatGraphicPagingSource(catGraphicApiClient) }
}
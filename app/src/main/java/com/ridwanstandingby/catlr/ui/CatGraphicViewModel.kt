package com.ridwanstandingby.catlr.ui

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ridwanstandingby.catlr.data.CatGraphicPagingSource
import com.ridwanstandingby.catlr.domain.CatGraphic
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class CatGraphicViewModel : ViewModel(), KoinComponent {

    val pager: Pager<Int, CatGraphic> = Pager(
        PagingConfig(
            pageSize = 10,
            enablePlaceholders = true,
            maxSize = 300
        )
    ) { get<CatGraphicPagingSource>() }
}
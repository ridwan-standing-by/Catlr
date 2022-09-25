package com.ridwanstandingby.catlr.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ridwanstandingby.catlr.data.CatGraphicApiClient
import com.ridwanstandingby.catlr.data.CatGraphicPagingSource
import com.ridwanstandingby.catlr.domain.CatGraphic
import com.ridwanstandingby.catlr.domain.CatGraphicMode
import com.ridwanstandingby.catlr.domain.Category

class CatGraphicViewModel(
    private val catGraphicApiClient: CatGraphicApiClient,
    val settingsPanelExpanded: MutableState<Boolean> = mutableStateOf(false),
    val catGraphicMode: MutableState<CatGraphicMode> = mutableStateOf(CatGraphicMode.GIFS),
    val filteredCategories: SnapshotStateList<Category> = mutableStateListOf("hat")
) : ViewModel() {

    private lateinit var pagingSource: CatGraphicPagingSource
    val pager: Pager<Int, CatGraphic> = Pager(
        PagingConfig(
            pageSize = CatGraphicPagingSource.PAGE_SIZE,
            enablePlaceholders = true,
            maxSize = 300
        )
    ) {
        CatGraphicPagingSource(
            client = catGraphicApiClient,
            catGraphicMode = catGraphicMode.value,
            filteredCategories = filteredCategories.toList()
        ).also { pagingSource = it }
    }

    private var previousCatGraphicMode = catGraphicMode.value
    private var previousFilteredCategories = filteredCategories.toList()

    fun onSettingsOpened() {
        settingsPanelExpanded.value = true
        previousCatGraphicMode = catGraphicMode.value
        previousFilteredCategories = filteredCategories.toList()
    }

    fun onSettingsDismissed() {
        if (catGraphicMode.value != previousCatGraphicMode ||
            filteredCategories.toList() != previousFilteredCategories.toList()
        ) {
            pagingSource.invalidate()
        }
        settingsPanelExpanded.value = false
    }
}
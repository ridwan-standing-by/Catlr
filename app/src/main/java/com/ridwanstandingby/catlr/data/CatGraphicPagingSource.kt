package com.ridwanstandingby.catlr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ridwanstandingby.catlr.domain.CatGraphic
import com.ridwanstandingby.catlr.domain.CatGraphicMode
import com.ridwanstandingby.catlr.domain.Category
import com.ridwanstandingby.catlr.domain.filterCategories

class CatGraphicPagingSource(
    private val client: CatGraphicApiClient,
    private val catGraphicMode: CatGraphicMode,
    private val filteredCategories: List<Category>
) : PagingSource<Int, CatGraphic>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CatGraphic> =
        try {
            val nextPageNumber = params.key ?: 1
            val response = client
                .getCatGraphics(nextPageNumber, catGraphicMode)
                .filterCategories(filteredCategories)
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, CatGraphic>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    companion object {
        const val PAGE_SIZE = 10
    }
}
package com.ridwanstandingby.catlr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ridwanstandingby.catlr.domain.CatGraphic

/**
 * Example from https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingsource
 */
class CatGraphicPagingSource(
    private val client: CatGraphicApiClient
) : PagingSource<Int, CatGraphic>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CatGraphic> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = client.getCatGraphics(nextPageNumber)
            LoadResult.Page(
                data = response,
                prevKey = null, // Only paging forward.
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CatGraphic>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
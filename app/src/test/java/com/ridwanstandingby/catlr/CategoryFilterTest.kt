package com.ridwanstandingby.catlr

import com.google.common.truth.Truth.assertThat
import com.ridwanstandingby.catlr.domain.CatGraphic
import com.ridwanstandingby.catlr.domain.filterCategories
import org.junit.Test

class CategoryFilterTest {

    @Test
    fun filterCategories_removesGivenCategories() {
        val cat1 = CatGraphic(id = "1", "", listOf())
        val cat2 = CatGraphic(id = "2", "", listOf("hat"))
        val cat3 = CatGraphic(id = "3", "", listOf("lots of hats", "something else"))
        val cat4 = CatGraphic(id = "4", "", listOf("something else"))
        val cat5 = CatGraphic(id = "5", "", listOf("lots of hats"))
        val cat6 = CatGraphic(id = "6", "", listOf("funny"))
        val cat7 = CatGraphic(id = "7", "", listOf("very funny", "hats"))

        assertThat(
            listOf(cat1, cat2, cat3, cat4, cat5, cat6, cat7)
                .filterCategories(listOf("hat", "funny"))
        ).containsExactly(cat1, cat4)
    }
}
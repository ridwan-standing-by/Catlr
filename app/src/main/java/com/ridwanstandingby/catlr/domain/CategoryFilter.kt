package com.ridwanstandingby.catlr.domain

fun List<CatGraphic>.filterCategories(categoriesToRemove: List<Category>) =
    filter { catGraphic ->
        catGraphic.categories?.none { thisCatCategory ->
            categoriesToRemove.any { category ->
                category.isNotBlank() && thisCatCategory.contains(category)
            }
        } ?: true
    }
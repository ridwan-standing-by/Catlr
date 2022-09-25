package com.ridwanstandingby.catlr.domain

enum class CatGraphicMode(val images: Boolean, val gifs: Boolean) {
    IMAGES(images = true, gifs = false),
    GIFS(images = false, gifs = true),
    BOTH(images = true, gifs = true)
}
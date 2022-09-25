package com.ridwanstandingby.catlr.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ridwanstandingby.catlr.data.CatGraphicApiClient
import kotlinx.coroutines.launch

class CatGraphicViewModel(private val catGraphicApiClient: CatGraphicApiClient) : ViewModel() {

    fun start() {
        // TODO
        viewModelScope.launch {
            catGraphicApiClient.getCatGraphics().forEach {
                Log.d("CAT", it.toString())
            }
        }
    }
}
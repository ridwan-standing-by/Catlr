package com.ridwanstandingby.catlr.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.facebook.drawee.backends.pipeline.Fresco
import com.ridwanstandingby.catlr.ui.theme.CatlrTheme
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatGraphicActivity : ComponentActivity() {

    private val vm by viewModel<CatGraphicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatlrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val lazyPagingItems = vm.pager.flow.collectAsLazyPagingItems()

                    LazyColumn {
                        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
                            item {
                                Text(
                                    text = "Waiting for items to load from the backend",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        itemsIndexed(lazyPagingItems) { index, item ->
                            Text("Index=$index: $item", fontSize = 20.sp)
                            item ?: return@itemsIndexed
                            FrescoWebImage(
                                controllerBuilder = Fresco.newDraweeControllerBuilder()
                                    .setUri(item.url)
                                    .setAutoPlayAnimations(true),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            )
                        }

                        if (lazyPagingItems.loadState.append == LoadState.Loading) {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        if (lazyPagingItems.loadState.append is LoadState.Error) {
                            item {
                                Text(
                                    text = "Waiting for items to load from the backend",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.ridwanstandingby.catlr.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.facebook.drawee.backends.pipeline.Fresco
import com.ridwanstandingby.catlr.domain.CatGraphic
import com.ridwanstandingby.catlr.ui.theme.CatlrTheme
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatGraphicActivity : ComponentActivity() {

    private val vm by viewModel<CatGraphicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatlrTheme {
                CatGraphicUi(vm)
            }
        }
    }
}

@Composable
private fun CatGraphicUi(vm: CatGraphicViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            SettingsButton(vm::onSettingsOpened)
        },
        backgroundColor = MaterialTheme.colors.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CatGraphicList(vm.pager)
            AnimatedVisibility(visible = vm.settingsPanelExpanded.value) {
                SettingsDialog(vm::onSettingsDismissed)
            }
        }
    }
}

@Composable
fun SettingsDialog(onSettingsDismissed: () -> Unit) {
    Dialog(onDismissRequest = { onSettingsDismissed() }) {
        Card(Modifier) {
            Column(Modifier.padding(16.dp)) {

                Button(onClick = onSettingsDismissed, Modifier.align(Alignment.End)) {
                    Text("OK", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsButton(onSettingsOpened: () -> Unit) {
    FloatingActionButton(onClick = onSettingsOpened) {
        Icon(
            imageVector = Icons.Rounded.Settings,
            contentDescription = "Options"
        )
    }
}

@Composable
private fun CatGraphicList(pager: Pager<Int, CatGraphic>) {
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    LazyColumn {
        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Text(
                    text = "Waiting for items to load...",
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
                    text = "Waiting for items to load...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
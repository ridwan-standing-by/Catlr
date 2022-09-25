package com.ridwanstandingby.catlr.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.ridwanstandingby.catlr.domain.CatGraphicMode
import com.ridwanstandingby.catlr.domain.Category
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
                SettingsDialog(vm.catGraphicMode, vm.filteredCategories, vm::onSettingsDismissed)
            }
        }
    }
}

@Composable
fun SettingsDialog(
    catGraphicMode: MutableState<CatGraphicMode>,
    filteredCategories: SnapshotStateList<Category>,
    onSettingsDismissed: () -> Unit
) {
    Dialog(onDismissRequest = { onSettingsDismissed() }) {
        Card(Modifier) {
            Column(
                Modifier
                    .padding(16.dp)
                    .defaultMinSize(minWidth = 220.dp)
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Graphics to load:", style = MaterialTheme.typography.h5)

                CatGraphicMode.values().forEach {
                    CatGraphicModeOption(
                        selectedMode = catGraphicMode,
                        thisMode = it
                    )
                }

                Divider(Modifier.padding(top = 8.dp))

                Text(text = "Categories to filter out:", style = MaterialTheme.typography.h5)

                filteredCategories.forEachIndexed { index, _ ->
                    EditableCategory(
                        filteredCategories = filteredCategories,
                        thisCategoryIndex = index
                    )
                }

                IconButton(onClick = { filteredCategories.add("") }) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add category")
                }

                Divider(Modifier.padding(top = 8.dp))

                Button(
                    onClick = onSettingsDismissed,
                    Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Text("OK", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun CatGraphicModeOption(selectedMode: MutableState<CatGraphicMode>, thisMode: CatGraphicMode) {
    Row(
        Modifier
            .padding(top = 8.dp)
            .clickable { selectedMode.value = thisMode }) {
        RadioButton(
            selected = selectedMode.value == thisMode,
            onClick = null,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = when (thisMode) {
                CatGraphicMode.IMAGES -> "Images"
                CatGraphicMode.GIFS -> "Gifs"
                CatGraphicMode.BOTH -> "Both"
            }
        )
    }
}

@Composable
fun EditableCategory(filteredCategories: SnapshotStateList<Category>, thisCategoryIndex: Int) {
    Row(
        Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        TextField(value = filteredCategories[thisCategoryIndex], onValueChange = {
            filteredCategories[thisCategoryIndex] = it.take(20)
        }, Modifier.weight(0.8f))

        IconButton(
            onClick = { filteredCategories.removeAt(thisCategoryIndex) },
            modifier = Modifier.weight(0.2f)
        ) {
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Remove category")
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
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
import androidx.compose.material.ChipDefaults.chipColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.facebook.drawee.backends.pipeline.Fresco
import com.ridwanstandingby.catlr.R
import com.ridwanstandingby.catlr.domain.CatGraphic
import com.ridwanstandingby.catlr.domain.CatGraphicMode
import com.ridwanstandingby.catlr.domain.Category
import com.ridwanstandingby.catlr.ui.theme.CatlrTheme
import com.skydoves.landscapist.fresco.websupport.FrescoWebImage
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

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
                Text(
                    text = stringResource(R.string.graphics_to_load),
                    style = MaterialTheme.typography.h5
                )

                CatGraphicMode.values().forEach {
                    CatGraphicModeOption(
                        selectedMode = catGraphicMode,
                        thisMode = it
                    )
                }

                Divider(Modifier.padding(top = 8.dp))

                Text(
                    text = stringResource(R.string.categories_to_filter),
                    style = MaterialTheme.typography.h5
                )

                filteredCategories.forEachIndexed { index, _ ->
                    EditableCategory(
                        filteredCategories = filteredCategories,
                        thisCategoryIndex = index
                    )
                }

                IconButton(onClick = { filteredCategories.add("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_category)
                    )
                }

                Divider(Modifier.padding(top = 8.dp))

                Button(
                    onClick = onSettingsDismissed,
                    Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.ok),
                        Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
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
                CatGraphicMode.IMAGES -> stringResource(R.string.images)
                CatGraphicMode.GIFS -> stringResource(R.string.gifs)
                CatGraphicMode.BOTH -> stringResource(R.string.both)
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
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.remove_category)
            )
        }
    }
}

@Composable
fun SettingsButton(onSettingsOpened: () -> Unit) {
    FloatingActionButton(onClick = onSettingsOpened) {
        Icon(
            imageVector = Icons.Rounded.Settings,
            contentDescription = stringResource(R.string.options)
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
                    text = stringResource(R.string.waiting_for_items_to_load),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        items(lazyPagingItems) { item ->
            item ?: return@items
            CatGraphicCard(catGraphic = item)
        }

        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp)
                )
            }
        }

        if (lazyPagingItems.loadState.append is LoadState.Error) {
            item {
                Text(
                    text = stringResource(R.string.waiting_for_items_to_load),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CatGraphicCard(catGraphic: CatGraphic) {
    Card(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)) {
        Column(Modifier.padding(4.dp)) {
            FrescoWebImage(
                controllerBuilder = Fresco.newDraweeControllerBuilder()
                    .setUri(catGraphic.url)
                    .setAutoPlayAnimations(true),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Text(
                text = catGraphic.url,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (!catGraphic.categories.isNullOrEmpty()) {
                Row(Modifier.padding(top = 4.dp)) {
                    catGraphic.categories.forEach {
                        Chip(
                            onClick = { /*TODO*/ },
                            colors = chipColors(backgroundColor = hsvHashCodeColour(it))
                        ) {
                            Text(text = it)
                        }
                    }
                }
            }

        }
    }
}

private fun hsvHashCodeColour(string: String) =
    Color.hsl(hue = abs(string.hashCode() % 360f), saturation = 1f, lightness = 0.8f)
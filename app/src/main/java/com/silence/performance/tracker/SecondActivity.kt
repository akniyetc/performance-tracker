package com.silence.performance.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import com.silence.performance.tracker.ui.theme.PerformanceTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondActivity : ComponentActivity() {

  private val viewModel by viewModels<SecondViewModel>()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      PerformanceTrackerTheme {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        Scaffold(
          modifier = Modifier.fillMaxSize()
            .semantics {
              testTagsAsResourceId = true
            },
          topBar = {
            TopAppBar(
              title = { Text("Performance Demo") },
            )
          }
        ) { innerPadding ->
          when(state) {
            SecondViewState.Loading -> {
              Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                  modifier = Modifier.align(Alignment.Center)
                )
              }
            }
            is SecondViewState.Content -> {
              ListContent(
                modifier = Modifier.padding(innerPadding),
                content = state.content,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ListContent(
  modifier: Modifier = Modifier,
  content: List<SecondContentType>,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize().testTag("second_content"),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    items(
      count = content.size,
    ) { index ->
      when (val item = content[index]) {
        is SecondContentType.Photo -> PhotoItem(item.url)
        is SecondContentType.Gif -> GifItem(item.url)
      }
    }
  }
}

@Composable
fun PhotoItem(
  url: String,
) {
  val sizeResolver = rememberConstraintsSizeResolver()
  val painter = rememberAsyncImagePainter(
    model = ImageRequest.Builder(LocalPlatformContext.current)
      .data(url)
      .size(sizeResolver)
      .build(),
  )
  Image(
    modifier = Modifier.then(sizeResolver).fillMaxWidth().wrapContentHeight(),
    painter = painter,
    contentDescription = null,
    contentScale = ContentScale.Crop,
  )
}

@Composable
fun GifItem(
  url: String,
) {
  val sizeResolver = rememberConstraintsSizeResolver()
  val model = ImageRequest.Builder(LocalPlatformContext.current)
    .data(url)
    .size(sizeResolver)
    .build()
  AsyncImage(
    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
    model = model,
    contentDescription = null,
    contentScale = ContentScale.Crop,
  )
}

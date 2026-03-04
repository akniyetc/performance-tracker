package com.silence.performance.tracker

import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tracing.Trace
import androidx.tracing.trace
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.silence.performance.tracker.ui.theme.PerformanceTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<MainViewModel>()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      PerformanceTrackerTheme {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .semantics {
              testTagsAsResourceId = true
            },
          topBar = {
            TopAppBar(
              title = { Text("Performance Demo") },
            )
          },
          bottomBar = {
            val context = LocalContext.current
            BottomAppBar {
              Button(
                modifier = Modifier.testTag("second_activity_content"),
                onClick = {
                  val intent = Intent(this@MainActivity, SecondActivity::class.java)
                  context.startActivity(intent)
                },
              ) {
                Text(
                  text = "Second Activity"
                )
              }
            }
          }
        ) { innerPadding ->
          when (state) {
            ViewState.Loading -> {
              Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                  modifier = Modifier.align(Alignment.Center)
                )
              }
            }

            is ViewState.Content -> {
              Content(
                modifier = Modifier.padding(innerPadding),
                content = state.content,
              )
              LaunchedEffect(Unit) {
                this@MainActivity.reportFullyDrawn()
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun Content(
  modifier: Modifier = Modifier,
//  content: ImmutableList<ContentType>,
  content: List<ContentType>,
) {
//  val deviceImagesCount = remember {
//    mutableIntStateOf(0)
//  }
  val contentList = trace(
    label = "content shuffle in compose"
  ) {
    content.shuffled()
  }
  val context = LocalContext.current
//  EnsureImageReadPermission(
//    onGranted = {
//
//    }
//  )
  val count = trace(
    label = "device images count fetching"
  ) {
    val recentLimit = 200
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Images.Media._ID)

    val (selection, selectionArgs) = buildSelectionForImages()
    val sortOrderOlderApis = "date_added DESC LIMIT $recentLimit" // API < 26 fallback

    val cursor = if (Build.VERSION.SDK_INT >= 26) {
      val args = Bundle().apply {
        // recent first
        putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Images.Media.DATE_ADDED))
        putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
        putInt(ContentResolver.QUERY_ARG_LIMIT, recentLimit)
        if (selection != null) putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
        if (selectionArgs != null) putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
      }
      context.contentResolver.query(uri, projection, args, /* cancellationSignal = */ null)
    } else {
      context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrderOlderApis)
    }

    cursor.use { it?.count ?: 0 }
  }
//  deviceImagesCount.intValue = count


  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Adaptive(200.dp),
    modifier = modifier
      .fillMaxSize()
      .testTag("content")
      .then(Modifier.alpha(90f))
      .pointerInput(Unit) { detectTapGestures {} },
    verticalItemSpacing = 4.dp,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    item {
      Text(
        text = count.toString()
      )
    }
    items(
      items = contentList,
      /*key = { item ->
        when (item) {
          is ContentType.Photo -> item.url
          is ContentType.Gif -> item.url
        }
      },
      contentType = {
        when (it) {
          is ContentType.Photo -> "photo"
          is ContentType.Gif -> "gif"
        }
      }*/
    ) { item ->
      when (item) {
        is ContentType.Photo -> Photo(item.url)
        is ContentType.Gif -> Gif(item.url)
      }
    }
  }
}

private fun buildSelectionForImages(): Pair<String?, Array<String?>?> {
  val clauses = mutableListOf<String>()
  if (Build.VERSION.SDK_INT >= 29) clauses += MediaStore.Images.Media.IS_PENDING + "=0"
  if (Build.VERSION.SDK_INT >= 30) clauses += MediaStore.MediaColumns.IS_TRASHED + "=0"
  return if (clauses.isEmpty()) null to null else clauses.joinToString(" AND ") to emptyArray()
}

@Composable
private fun Photo(
  url: String,
) {
//  val sizeResolver = rememberConstraintsSizeResolver()
  val model = ImageRequest.Builder(LocalPlatformContext.current)
    .data(url)
    //    .size(sizeResolver)
    .crossfade(true)
    .build()
//  AsyncImage(
//    model = painter,
//    //modifier = Modifier.size(52.dp),
//    contentDescription = null,
//    contentScale = ContentScale.Crop,
//  )
  SubcomposeAsyncImage(
    modifier = Modifier.blur(12.dp),
    model = model,
    //modifier = Modifier.size(52.dp),
    contentDescription = null,
    contentScale = ContentScale.Crop,
  )
}

@Composable
private fun Gif(
  url: String,
) {
//  val sizeResolver = rememberConstraintsSizeResolver()
  val model = ImageRequest.Builder(LocalPlatformContext.current)
    .data(url)
//    .size(sizeResolver)
    .crossfade(true)
    .build()
//  AsyncImage(
//    modifier = Modifier.size(52.dp),
//    model = model,
//    contentDescription = null,
//    contentScale = ContentScale.Crop,
//  )
  SubcomposeAsyncImage(
//    modifier = Modifier.size(52.dp),
    model = model,
    contentDescription = null,
    contentScale = ContentScale.Crop,
  )
}

@Composable
fun EnsureImageReadPermission(
  onGranted: () -> Unit,
  onDenied: (() -> Unit)? = null,
  autoRequestOnFirstComposition: Boolean = true,
  appPackage: String = LocalContext.current.packageName
) {
  val context = LocalContext.current
  val activity = context as ComponentActivity

  val permission = if (Build.VERSION.SDK_INT >= 33)
    android.Manifest.permission.READ_MEDIA_IMAGES
  else
    android.Manifest.permission.READ_EXTERNAL_STORAGE

  var showRationale by remember { mutableStateOf(false) }
  var showGoToSettings by remember { mutableStateOf(false) }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    when {
      granted -> onGranted()
      // If user denied and "Don't ask again" is checked, shouldShow… returns false
      !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) ->
        showGoToSettings = true

      else -> {
        showRationale = true
        onDenied?.invoke()
      }
    }
  }

  fun request() {
    // Already granted?
    val granted =
      ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    if (granted) {
      onGranted(); return
    }
    // Should show rationale before asking?
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
      showRationale = true
    } else {
      launcher.launch(permission)
    }
  }

  // Auto ask when this Composable first appears (optional)
  LaunchedEffect(Unit) {
    if (autoRequestOnFirstComposition) request()
  }

  // Minimal UI you can wire to a button/menu
  TextButton(onClick = { request() }) { Text("Grant image permission") }

  if (showRationale) {
    AlertDialog(
      onDismissRequest = { showRationale = false },
      title = { Text("Allow access to photos") },
      text = { Text("We need permission to read images to count photos on your device.") },
      confirmButton = {
        TextButton(onClick = {
          showRationale = false
          launcher.launch(permission)
        }) { Text("Continue") }
      },
      dismissButton = {
        TextButton(onClick = { showRationale = false }) { Text("Cancel") }
      }
    )
  }

  if (showGoToSettings) {
    val settingsIntent = remember {
      Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", appPackage, null)
      }
    }
    AlertDialog(
      onDismissRequest = { showGoToSettings = false },
      title = { Text("Permission required") },
      text = { Text("You’ve denied photo access. Please enable it in Settings to proceed.") },
      confirmButton = {
        TextButton(onClick = {
          showGoToSettings = false
          context.startActivity(settingsIntent)
        }) { Text("Open Settings") }
      },
      dismissButton = {
        TextButton(onClick = { showGoToSettings = false }) { Text("Not now") }
      }
    )
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  PerformanceTrackerTheme {
    Content(Modifier, persistentListOf(ContentType.Photo("https://picsum.photos/200/300")))
  }
}
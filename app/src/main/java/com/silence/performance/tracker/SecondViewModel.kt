package com.silence.performance.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.tracing.trace
import androidx.tracing.traceAsync
import com.silence.performance.tracker.content.GifUseCase
import com.silence.performance.tracker.content.PhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(
  private val photosUseCase: PhotosUseCase,
  private val gifsUseCase: GifUseCase,
) : ViewModel() {
  private val performanceTracker = PerformanceTrackerProvider.getInstance()
  private val _state: MutableStateFlow<SecondViewState> = MutableStateFlow(SecondViewState.Loading)
  val state: StateFlow<SecondViewState> = _state.asStateFlow()

  init {
    trace(
      lazyLabel = { "SecondViewModel init" },
      block = {
        val traceListener = DemoListener {}
        performanceTracker.addListener(traceListener)
        performanceTracker.stopTrace(STARTUP_TIME)
        viewModelScope.launch(Dispatchers.IO) {
          traceAsync(
            methodName = "loadAllContent",
            cookie = 0,
            block = {
              loadContent()
            }
          )
        }
      }
    )
  }

  private suspend fun loadContent() {
    val photos = traceAsync(
      methodName = "loadPhotos",
      cookie = 0,
      block = {
        viewModelScope.async(Dispatchers.IO) { photosUseCase.photos().map { SecondContentType.Photo(it) } }
      }
    )
    val gifs = traceAsync(
      methodName = "loadGifs",
      cookie = 0,
      block = {
        viewModelScope.async(Dispatchers.IO) { gifsUseCase.gifs().map { SecondContentType.Gif(it) } }
      }
    )

    val content = traceAsync(
      methodName = "shuffleContent",
      cookie = 0,
      block = {
        (photos.await() + gifs.await()).shuffled()
      }
    )
    _state.value = SecondViewState.Content(content)
  }
}

sealed class SecondViewState {
  object Loading : SecondViewState()
  data class Content(val content: List<SecondContentType>) : SecondViewState()
}
sealed class SecondContentType {
  data class Photo(val url: String) : SecondContentType()
  data class Gif(val url: String) : SecondContentType()
}
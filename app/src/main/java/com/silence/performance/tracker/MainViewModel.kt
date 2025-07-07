package com.silence.performance.tracker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
  private val performanceTracker = PerformanceTrackerProvider.getInstance()
  private val _state: MutableStateFlow<String> = MutableStateFlow("measuring..")
  val state: StateFlow<String> = _state.asStateFlow()

  init {
    val traceListener = DemoListener {
      _state.value = it.toString()
    }
    performanceTracker.addListener(traceListener)
    performanceTracker.stopTrace(STARTUP_TIME)
  }
}
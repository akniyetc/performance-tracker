package com.silence.performance.tracker

import com.silence.performance.tracking.InMemoryPerformanceTracker
import com.silence.performance.tracking.PerformanceTracker

object PerformanceTrackerProvider {

  @Volatile
  private var INSTANCE: PerformanceTracker? = null

  @JvmStatic
  fun getInstance() =
    INSTANCE ?: synchronized(this) {
      INSTANCE ?: InMemoryPerformanceTracker().also { INSTANCE = it }
    }
}
package com.silence.performance.tracker

object PerformanceTrackerProvider {

  @Volatile
  private var INSTANCE: PerformanceTracker? = null

  @JvmStatic
  fun getInstance() =
    INSTANCE ?: synchronized(this) {
      INSTANCE ?: InMemoryPerformanceTracker().also { INSTANCE = it }
    }
}
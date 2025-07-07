package com.silence.performance.tracker

import android.os.SystemClock

class InMemoryTrace {

  private val startTime = SystemClock.elapsedRealtime()
  private val attributes = mutableMapOf<String, Any>()
  private val metrics = mutableMapOf<String, Any>()

  fun stop(): Long? {
    return SystemClock.elapsedRealtime() - startTime
  }

  fun addAttribute(key: String, value: Any) {
    attributes[key] = value
  }

  fun addMetric(key: String, value: Any) {
    metrics[key] = value
  }

  fun toPerformanceTrace(name: String): PerformanceTrace {
    return PerformanceTrace(
      name = name,
      durationMs = stop() ?: 0,
      attributes = attributes.toMap(),
      metrics = metrics.toMap()
    )
  }
}


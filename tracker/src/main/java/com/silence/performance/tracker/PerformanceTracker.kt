package com.silence.performance.tracker

interface PerformanceTracker {
  fun startTrace(name: String)
  fun stopTrace(name: String)

  fun addAttribute(traceName: String, key: String, value: Any)
  fun addMetric(traceName: String, metricName: String, value: Any)

  fun addListener(listener: TraceListener)
  fun removeListener(listener: TraceListener)
}

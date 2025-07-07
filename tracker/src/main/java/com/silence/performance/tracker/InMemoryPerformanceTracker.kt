package com.silence.performance.tracker

import java.util.concurrent.ConcurrentHashMap

class InMemoryPerformanceTracker : PerformanceTracker {

  private val traces = ConcurrentHashMap<String, InMemoryTrace>()
  private val listeners = mutableSetOf<TraceListener>()

  override fun startTrace(name: String) {
    traces[name] = InMemoryTrace().also {
      listeners.forEach { it.onStart(name) }
    }
  }

  override fun stopTrace(name: String) {
    val trace = traces.remove(name) ?: return
    val result = trace.toPerformanceTrace(name)
    listeners.forEach { it.onStop(result) }
  }

  override fun addListener(listener: TraceListener) {
    listeners.add(listener)
  }

  override fun removeListener(listener: TraceListener) {
    listeners.remove(listener)
  }

  override fun addAttribute(traceName: String, key: String, value: Any) {
    traces[traceName]?.addAttribute(key, value)
    listeners.forEach { it.onAttributeAddedToTrace(traceName, key, value) }
  }

  override fun addMetric(traceName: String, metricName: String, value: Any) {
    traces[traceName]?.addMetric(metricName, value)
    listeners.forEach { it.onMetricAddedToTrace(traceName, metricName, value) }
  }
}
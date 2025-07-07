package com.silence.performance.tracker.firebase

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.silence.performance.tracker.PerformanceTrace
import com.silence.performance.tracker.TraceListener

class FirebasePerformanceTracker(
  private val firebasePerformance: FirebasePerformance,
) : TraceListener {
  private val firebaseTraces = mutableMapOf<String, Trace>()

  override fun onStart(traceName: String) {
    val trace = firebasePerformance.newTrace(traceName)
    trace.start()
    firebaseTraces[traceName] = trace
  }

  override fun onAttributeAddedToTrace(traceName: String, attrName: String, attrValue: Any) {
    firebaseTraces[traceName]?.putAttribute(attrName, attrValue.toString())
  }

  override fun onMetricAddedToTrace(traceName: String, metricName: String, metricValue: Any) {
    val longValue = (metricValue as? Number)?.toLong() ?: return
    firebaseTraces[traceName]?.putMetric(metricName, longValue)
  }

  override fun onStop(trace: PerformanceTrace) {
    firebaseTraces.remove(trace.name)?.stop()
  }
}

package com.silence.performance.tracker

import com.silence.performance.tracking.PerformanceTrace
import com.silence.performance.tracking.TraceListener

class DemoListener(
  val onTraceStopped: (PerformanceTrace) -> Unit,
) : TraceListener {

  override fun onStart(traceName: String) = Unit

  override fun onStop(trace: PerformanceTrace) {
    onTraceStopped(trace)
  }

  override fun onAttributeAddedToTrace(
    traceName: String,
    attrName: String,
    attrValue: Any
  ) = Unit

  override fun onMetricAddedToTrace(
    traceName: String,
    metricName: String,
    metricValue: Any
  ) = Unit
}
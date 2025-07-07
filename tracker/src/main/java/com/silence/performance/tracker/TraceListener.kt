package com.silence.performance.tracker

interface TraceListener {
  fun onStart(traceName: String)
  fun onStop(trace: PerformanceTrace)
  fun onAttributeAddedToTrace(traceName: String, attrName: String, attrValue: Any)
  fun onMetricAddedToTrace(traceName: String, metricName: String, metricValue: Any)
}
package com.silence.performance.tracker.sentry

import com.silence.performance.tracker.PerformanceTrace
import com.silence.performance.tracker.TraceListener
import io.sentry.ITransaction
import io.sentry.Sentry
import io.sentry.SpanStatus

class SentryPerformanceTracker : TraceListener {
  private val sentryTraces = mutableMapOf<String, ITransaction>()

  override fun onStart(traceName: String) {
    val transaction = Sentry.startTransaction(traceName, "custom")
    sentryTraces[traceName] = transaction
  }

  override fun onAttributeAddedToTrace(traceName: String, attrName: String, attrValue: Any) {
    sentryTraces[traceName]?.setTag(attrName, attrValue.toString())
  }

  override fun onMetricAddedToTrace(traceName: String, metricName: String, metricValue: Any) {
    sentryTraces[traceName]?.setData(metricName, metricValue)
  }

  override fun onStop(trace: PerformanceTrace) {
    sentryTraces.remove(trace.name)?.finish(SpanStatus.OK)
  }
}

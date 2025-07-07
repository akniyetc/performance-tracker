package com.silence.performance.tracker

data class PerformanceTrace(
  val name: String,
  val durationMs: Long,
  val attributes: Map<String, Any> = emptyMap(),
  val metrics: Map<String, Any> = emptyMap()
)
package com.silence.performance.tracker

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.FirebaseApp

const val STARTUP_TIME = "startup_time"

class StartupInitializer: Initializer<Unit> {

  override fun create(context: Context) {
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }

    val performanceTracker = PerformanceTrackerProvider.getInstance().apply {
//      addListener(FirebasePerformanceTracker(FirebasePerformance.getInstance()))
//      addListener(SentryPerformanceTracker())
    }
    performanceTracker.startTrace(STARTUP_TIME)
  }

  override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}
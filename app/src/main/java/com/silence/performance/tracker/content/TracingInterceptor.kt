package com.silence.performance.tracker.content

import okhttp3.Interceptor
import okhttp3.Response

class TracingInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    androidx.tracing.trace(
      lazyLabel = { request.url.toString() }
    ) {
      return chain.proceed(request)
    }
  }
}
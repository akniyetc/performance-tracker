package com.silence.performance.tracker.content

import javax.inject.Inject

class GifUseCase @Inject constructor(
  private val gifApi: GifApi,
) {

  suspend fun gifs(): List<String> = gifApi.gifs().data.map { it.images.original.url }
}
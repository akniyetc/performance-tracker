package com.silence.performance.tracker.content

import javax.inject.Inject

class PhotosUseCase @Inject constructor(
  private val api: PhotosApi,
) {
  suspend fun photos(): List<String> = api.photos(25).photos.map { it.src.original }
}
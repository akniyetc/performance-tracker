package com.silence.performance.tracker.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PhotosApi {

  @Headers("Authorization: FXQco2AlLEr994pR5jLL2jr7hq8fDk2znwAzHtyMeCOyWLbbF3x2WYM5")
  @GET("v1/curated")
  suspend fun photos(@Query("per_page") page: Int): PexelsResponse
}

@Serializable
data class PexelsResponse(
  val page: Int,
  @SerialName("per_page")
  val perPage: Int,
  val photos: List<Photo>,
  @SerialName("next_page")
  val nextPage: String?
)

@Serializable
data class Photo(
  val id: Long,
  val width: Int,
  val height: Int,
  val url: String,
  val photographer: String,
  val src: Src,
  val liked: Boolean,
  val alt: String
)

@Serializable
data class Src(
  val original: String,
  val large2x: String,
  val large: String,
  val medium: String,
  val small: String,
  val portrait: String,
  val landscape: String,
  val tiny: String
)

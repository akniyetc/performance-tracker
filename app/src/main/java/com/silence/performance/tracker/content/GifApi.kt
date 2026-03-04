package com.silence.performance.tracker.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface GifApi {

  @GET("v1/gifs/trending?api_key=bSrhpv2RzrxVf3NPXYPgNexmmvzLlWec&limit=25&offset=0&rating=g&bundle=messaging_non_clips")
  suspend fun gifs(): GiphyResponse
}

@Serializable
data class GiphyResponse(
  val data: List<GifData>,
)

@Serializable
data class GifData(
  val type: String,
  val id: String,
  val url: String,
  @SerialName("bitly_gif_url")
  val bitlyGifUrl: String,
  @SerialName("bitly_url")
  val bitlyUrl: String,
  @SerialName("embed_url")
  val embedUrl: String,
  val source: String,
  val title: String,
  @SerialName("content_url")
  val contentUrl: String,
  val images: Images,
)

@Serializable
data class Images(
  val original: ImageVariant,
  @SerialName("fixed_height")
  val fixedHeight: ImageVariant,
  @SerialName("fixed_height_downsampled")
  val fixedHeightDownsampled: ImageVariant,
  @SerialName("fixed_height_small")
  val fixedHeightSmall: ImageVariant,
  @SerialName("fixed_width")
  val fixedWidth: ImageVariant,
  @SerialName("fixed_width_downsampled")
  val fixedWidthDownsampled: ImageVariant,
  @SerialName("fixed_width_small")
  val fixedWidthSmall: ImageVariant
)

@Serializable
data class ImageVariant(
  val height: String,
  val width: String,
  val size: String? = null,
  val url: String,
  val mp4: String? = null,
  val webp: String? = null,
  val frames: String? = null,
  val hash: String? = null
)




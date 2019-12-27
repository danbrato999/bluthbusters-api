package com.bluthbusters.api.modules

import com.bluthbusters.api.services.OmdbApiClient
import com.bluthbusters.api.services.OmdbApiClientImpl
import com.bluthbusters.api.services.YoutubeApiClient
import com.bluthbusters.api.services.YoutubeApiClientImpl
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.client.WebClient

class ApiClients : AbstractVerticle() {
  override fun start() {
    val omdbConfig = config().getJsonObject("omdb-api")
    val omdbApiClient = OmdbApiClientImpl(omdbConfig.getString("url"), omdbConfig.getString("api-key"),
      WebClient.create(vertx))
    val youtubeApiClient = YoutubeApiClientImpl(
      config().getJsonObject("youtube-api").getString("api-key"),
      WebClient.create(vertx)
    )

    bindService(OMDB_API_CLIENT_SERVICE, OmdbApiClient::class.java, omdbApiClient)
    bindService(YOUTUBE_API_CLIENT_SERVICE, YoutubeApiClient::class.java, youtubeApiClient)
  }
}

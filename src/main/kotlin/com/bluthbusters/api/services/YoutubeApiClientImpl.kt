package com.bluthbusters.api.services

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.apache.commons.text.StringEscapeUtils

class YoutubeApiClientImpl(apiKey: String, webClient: WebClient) : YoutubeApiClient {
  private val request = webClient.getAbs("https://www.googleapis.com/youtube/v3/search")
    .addQueryParam("key", apiKey)
    .addQueryParam("part", "snippet")
    .addQueryParam("type", "video")
    .addQueryParam("order", "relevance")
    .addQueryParam("maxResults", "5")

  override fun searchByTitle(title: String, handler: Handler<AsyncResult<JsonArray>>) {
    request.copy()
      .expect(ResponsePredicate.SC_SUCCESS)
      .addQueryParam("q", title)
      .send { ar ->
        handler.handle(ar.map(this::fromHttpResponse))
      }
  }

  private fun fromHttpResponse(response: HttpResponse<Buffer>) : JsonArray {
    val jsonResponse = response.bodyAsJsonObject()
    val items = jsonResponse
      .getJsonArray("items")
      .map { it as JsonObject }
      .map { item ->
        val snippet = item.getJsonObject("snippet")
        json {
          obj(
            "id" to item.getJsonObject("id").getString("videoId"),
            "title" to StringEscapeUtils.unescapeHtml4(snippet.getString("title")),
            "thumbnail" to snippet.getJsonObject("thumbnails").getJsonObject("default").getString("url")
          )
        }
      }

    return JsonArray(items)
  }
}

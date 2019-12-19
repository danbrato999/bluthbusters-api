package com.bluthbusters.api.services

import com.bluthbusters.api.models.MovieDataSearchForm
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class OmdbApiClientImpl(url: String, apiKey: String, webClient: WebClient) : OmdbApiClient {
  private val baseRequest = webClient.getAbs(url)
    .addQueryParam("apiKey", apiKey)
    .addQueryParam("type", "movie")

  override fun searchSingle(form: MovieDataSearchForm, handler: Handler<AsyncResult<JsonObject?>>) {
    val searchType = if (form.type == "byImdbId") "i" else "t"
    baseRequest.copy()
      .addQueryParam(searchType, form.value)
      .expect(ResponsePredicate.SC_SUCCESS)
      .send { ar ->
        handler.handle(ar.map { result ->
          val jsonResult = result.bodyAsJsonObject()
          if (jsonResult.containsKey("Error"))
            null
          else
            fromJsonResponse(jsonResult)
        })
      }
  }

  private fun fromJsonResponse(response: JsonObject) : JsonObject = json {
    obj(
      "imdbId" to response.getString("imdbID"),
      "title" to response.getString("Title"),
      "genre" to response.getString("Genre"),
      "year" to response.getString("Year").toInt(),
      "director" to response.getString("Director"),
      "runtime" to response.getString("Runtime"),
      "poster" to response.getString("Poster"),
      "description" to response.getString("Plot")
    )
  }
}

package com.bluthbusters.api.services

import io.vertx.codegen.annotations.ProxyGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray

@ProxyGen
interface YoutubeApiClient {
  fun searchByTitle(title: String, handler: Handler<AsyncResult<JsonArray>>)
}

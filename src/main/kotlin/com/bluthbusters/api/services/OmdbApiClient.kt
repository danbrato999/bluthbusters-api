package com.bluthbusters.api.services

import com.bluthbusters.api.models.MovieDataSearchForm
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject

@ProxyGen
interface OmdbApiClient {
  fun searchSingle(form: MovieDataSearchForm, handler: Handler<AsyncResult<JsonObject?>>)
}

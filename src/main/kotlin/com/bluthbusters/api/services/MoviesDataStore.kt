package com.bluthbusters.api.services

import com.bluthbusters.api.models.IdObject
import com.bluthbusters.api.models.MovieForm
import com.bluthbusters.api.models.PaginatedList
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject

@ProxyGen
interface MoviesDataStore {
  fun add(form: MovieForm, handler: Handler<AsyncResult<IdObject>>)
  fun list(status: String, limit: Long, page: Long, handler: Handler<AsyncResult<PaginatedList>>)
  fun find(id: String, handler: Handler<AsyncResult<JsonObject?>>)
}

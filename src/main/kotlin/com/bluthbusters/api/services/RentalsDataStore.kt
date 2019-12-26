package com.bluthbusters.api.services

import com.bluthbusters.api.models.RentalForm
import io.vertx.codegen.annotations.ProxyGen
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

@ProxyGen
interface RentalsDataStore {
  fun add(form: RentalForm, handler: Handler<AsyncResult<JsonObject?>>)
  fun returnMovie(customerId: String, movieId: String, handler: Handler<AsyncResult<Long>>)
  fun list(customerId: String, handler: Handler<AsyncResult<JsonArray>>)
  fun hasCopy(customerId: String, movieId: String, handler: Handler<AsyncResult<JsonObject?>>)
  fun pendingMoviesCount(customerId: String, handler: Handler<AsyncResult<Long>>)
}

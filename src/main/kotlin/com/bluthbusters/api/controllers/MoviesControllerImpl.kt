package com.bluthbusters.api.controllers

import com.bluthbusters.api.models.MovieForm
import com.bluthbusters.api.services.MoviesDataStore
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse

class MoviesControllerImpl(private val moviesDataStore: MoviesDataStore) : MoviesController {
  override fun addMovie(
    body: JsonObject,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    try {
      val form = MovieForm(body)
      moviesDataStore.add(form, resultHandler.ofId())
    } catch (e: Exception) {
      resultHandler.handle(Future.failedFuture(e))
    }
  }

  override fun listMovies(
    status: String?,
    limit: Long?,
    page: Long?,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    moviesDataStore.list(status ?: "all", limit ?: 50, page ?: 1, resultHandler.ofPaginatedList())
  }

  override fun findMovie(id: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>) {
    moviesDataStore.find(id, resultHandler.ofNullableJson())
  }
}

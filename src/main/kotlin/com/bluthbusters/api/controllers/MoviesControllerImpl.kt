package com.bluthbusters.api.controllers

import com.bluthbusters.api.controllers.Data.movies
import com.bluthbusters.api.models.PaginatedList
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse

class MoviesControllerImpl : MoviesController {
  override fun listMovies(
    status: String?,
    limit: Long?,
    page: Long?,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    val response = OperationResponse.completedWithJson(JsonObject.mapFrom(PaginatedList(2, movies)))
    resultHandler.handle(Future.succeededFuture(response))
  }

  override fun findMovie(id: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>) {
    val movie = movies.find { it.id == id }
    val result = if (movie == null) OperationResponse().setStatusCode(404) else OperationResponse.completedWithJson(
      JsonObject.mapFrom(movie))

    resultHandler.handle(Future.succeededFuture(result))
  }
}

package com.bluthbusters.api.controllers

import com.bluthbusters.api.controllers.Data.movieRentals
import com.bluthbusters.api.controllers.Data.movies
import com.bluthbusters.api.models.MovieRentalDetail
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse

class CustomersControllerImpl : CustomersController {
  override fun getCustomerHistory(context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>) {
    val history = movieRentals
      .map { movieRental ->
        val movie = movies.find { it.id == movieRental.movieId }!!
          .externalData
        MovieRentalDetail(movieRental, movie)
      }

    resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(JsonArray(history))))
  }

  override fun hasMovieCopy(
    movieId: String,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    val currentRent = movieRentals
      .filter { it.returnedAt == null }
      .find { it.movieId == movieId }

    val response = if (currentRent == null)
      OperationResponse().setStatusCode(204)
    else
      OperationResponse.completedWithJson(JsonObject.mapFrom(currentRent))

    resultHandler.handle(Future.succeededFuture(response))
  }
}

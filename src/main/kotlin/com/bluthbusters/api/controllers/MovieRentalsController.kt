package com.bluthbusters.api.controllers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse
import io.vertx.ext.web.api.generator.WebApiServiceGen

@WebApiServiceGen
interface MovieRentalsController {
  fun rentMovie(movieId: String, body: JsonObject, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun returnMovie(movieId: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun hasMovieCopy(movieId: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun getCustomerHistory(context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
}

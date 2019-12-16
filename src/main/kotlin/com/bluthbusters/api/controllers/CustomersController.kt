package com.bluthbusters.api.controllers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse
import io.vertx.ext.web.api.generator.WebApiServiceGen

@WebApiServiceGen
interface CustomersController {
  fun getCustomerHistory(context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun hasMovieCopy(movieId: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
}

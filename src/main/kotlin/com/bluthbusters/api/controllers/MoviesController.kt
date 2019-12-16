package com.bluthbusters.api.controllers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse
import io.vertx.ext.web.api.generator.WebApiServiceGen

@WebApiServiceGen
interface MoviesController {
  fun listMovies(status: String?, limit: Long?, page: Long?, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun findMovie(id: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
}

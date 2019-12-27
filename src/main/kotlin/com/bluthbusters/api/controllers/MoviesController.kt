package com.bluthbusters.api.controllers

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse
import io.vertx.ext.web.api.generator.WebApiServiceGen

@WebApiServiceGen
interface MoviesController {
  fun addMovie(body: JsonObject, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun listMovies(name: String?, status: String?, limit: Long?, page: Long?, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun findMovie(id: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun updateMovie(id: String, body: JsonObject, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun searchMovieExternalData(body: JsonObject, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
  fun searchMovieTrailer(title: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
}

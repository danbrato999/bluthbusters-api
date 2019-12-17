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
      moviesDataStore.add(form, Handler { ar ->
        resultHandler.handle(
          ar.map {
            OperationResponse
              .completedWithJson(it.toJson())
              .setStatusCode(201)
          }
        )
      })
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
  ) =
    moviesDataStore.list(status ?: "all", limit ?: 50, page ?: 1, Handler { ar ->
      resultHandler.handle(
        ar.map {
          OperationResponse.completedWithJson(it.toJson())
        }
      )
    })

  override fun findMovie(id: String, context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>) =
    moviesDataStore.find(id, Handler { ar ->
      resultHandler.handle(
        ar.map {
          if (it == null)
            OperationResponse().setStatusCode(404)
          else
            OperationResponse.completedWithJson(it)
        }
      )
    })

  override fun updateMovie(
    id: String,
    body: JsonObject,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    try {
      val form = MovieForm(body)
      moviesDataStore.update(id, form, Handler { ar ->
        resultHandler.handle(ar.map { update ->
          when (update) {
              null -> OperationResponse().setStatusCode(404)
              0L -> OperationResponse().setStatusCode(400)
              else -> OperationResponse().setStatusCode(200)
          }
        })
      })
    } catch (e: Exception) {
      resultHandler.handle(Future.failedFuture(e))
    }
  }
}

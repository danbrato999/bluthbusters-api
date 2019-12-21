package com.bluthbusters.api.controllers

import com.bluthbusters.api.models.RentalForm
import com.bluthbusters.api.services.RentalsDataStore
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse

class MovieRentalsControllerImpl(private val rentalsDataStore: RentalsDataStore) : MovieRentalsController {
  private val hardCodedCustomer = "customer00001";

  override fun rentMovie(
    movieId: String,
    body: JsonObject,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    try {
      val form = RentalForm(movieId, hardCodedCustomer, body.getString("rentUntil"))
      rentalsDataStore.add(form, Handler { ar ->
        resultHandler.handle(
          ar.map {
            if (it == null)
              OperationResponse().setStatusCode(400)
            else
              OperationResponse.completedWithJson(it).setStatusCode(201)
          }
        )
      })
    } catch (e: Exception) {
      resultHandler.handle(Future.failedFuture(e))
    }
  }

  override fun returnMovie(
    movieId: String,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    rentalsDataStore.returnMovie(hardCodedCustomer, movieId, Handler { ar ->
      resultHandler.handle(
        ar.map { docsUpdated ->
          OperationResponse()
            .setStatusCode(if (docsUpdated == 0L) 400 else 200)
        }
      )
    })
  }

  override fun hasMovieCopy(
    movieId: String,
    context: OperationRequest,
    resultHandler: Handler<AsyncResult<OperationResponse>>
  ) {
    rentalsDataStore.hasCopy(hardCodedCustomer, movieId, Handler { ar ->
      resultHandler.handle(
        ar.map { currentRent ->
          if (currentRent == null)
            OperationResponse().setStatusCode(204)
          else
            OperationResponse.completedWithJson(JsonObject.mapFrom(currentRent))
        }
      )
    })
  }

  override fun getCustomerHistory(context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>) {
    rentalsDataStore.list(hardCodedCustomer, Handler { ar ->
      resultHandler.handle(ar.map { OperationResponse.completedWithJson(it) })
    })
  }
}

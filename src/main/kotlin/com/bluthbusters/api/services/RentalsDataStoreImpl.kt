package com.bluthbusters.api.services

import com.bluthbusters.api.mappers.RentalsMapper
import com.bluthbusters.api.models.IdObject
import com.bluthbusters.api.models.RentalForm
import com.bluthbusters.api.services.Collections.moviesCollection
import com.bluthbusters.api.services.Collections.rentalsCollection
import io.vertx.core.AsyncResult
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.MongoClientUpdateResult
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.findOptionsOf

class RentalsDataStoreImpl(private val dbClient: MongoClient) : RentalsDataStore {
  override fun add(form: RentalForm, handler: Handler<AsyncResult<IdObject?>>) {
    canRentMovie(form.customerId, form.movieId)
      .compose<IdObject?> { canRent ->
        if (canRent)
          Future.future<String> {
            dbClient.insert(rentalsCollection, RentalsMapper.toNewRentalDocument(form), it)
          }.compose { rentalId ->
            increaseUnavailableInventory(form.movieId, 1)
              .map { IdObject(rentalId) }
          }
        else
          Future.succeededFuture(null)
      }.setHandler(handler)
  }

  override fun returnMovie(customerId: String, movieId: String, handler: Handler<AsyncResult<Long>>) {
    Future.future<MongoClientUpdateResult> {
      dbClient.updateCollection(rentalsCollection, find(customerId, movieId), returnUpdate(), it)
    }.compose { result ->
      if (result.docModified > 0)
        increaseUnavailableInventory(movieId, -1)
          .map { result.docModified }
      else
        Future.succeededFuture(0L)
    }.setHandler(handler)
  }

  override fun list(customerId: String, handler: Handler<AsyncResult<JsonArray>>) {
    dbClient.runCommand("aggregate", customerHistoryAggregate(customerId)) { ar ->
      handler.handle(ar.map { result ->
        if (result.containsKey("cursor") && result.getJsonObject("cursor").containsKey("firstBatch"))
          result.getJsonObject("cursor")
            .getJsonArray("firstBatch")
            .map { it as JsonObject }
            .map(RentalsMapper::fromDetailedMovieRentalDocument)
            .let { JsonArray(it) }
        else
          JsonArray()
      })
    }
  }

  override fun hasCopy(customerId: String, movieId: String, handler: Handler<AsyncResult<JsonObject?>>) {
    dbClient.findWithOptions(rentalsCollection, find(customerId, movieId), findOptionsOf(limit = 1)) { ar ->
      handler.handle(ar.map { it.firstOrNull()?.let(RentalsMapper::fromRentalDocument) })
    }
  }

  private fun canRentMovie(customerId: String, movieId: String) : Future<Boolean> {
    val movieQuery = json {
      obj(
        "_id" to movieId,
        "\$where" to "this.inventory.copies > this.inventory.rented"
      )
    }

    val movieAvailableFuture = Future.future<List<JsonObject>> {
      dbClient.find(moviesCollection, movieQuery, it)
    }.map { it.firstOrNull() }

    val customerWithCopyFuture = Future.future<JsonObject?> {
      hasCopy(customerId, movieId, it)
    }

    return CompositeFuture.join(movieAvailableFuture, customerWithCopyFuture)
      .map {
        movieAvailableFuture.result() != null && customerWithCopyFuture.result() == null
      }
  }

  private fun increaseUnavailableInventory(movieId: String, amount: Long) : Future<Long> {
    val query = JsonObject().put("_id", movieId)
    val update = json {
      obj(
        "\$inc" to obj(
          "inventory.rented" to amount
        )
      )
    }

    return Future.future<MongoClientUpdateResult> {
      dbClient.updateCollection(moviesCollection, query, update, it)
    }.map { it.docModified }
  }

  companion object {
    private fun find(customerId: String, movieId: String) : JsonObject = json {
      obj(
        "customerId" to customerId,
        "movieId" to movieId,
        "returnedAt" to null
      )
    }

    private fun returnUpdate() : JsonObject = json {
      obj(
        "\$set" to obj(
          "returnedAt" to RentalsMapper.currentDate()
        )
      )
    }

    private fun customerHistoryAggregate(customerId: String) : JsonObject = json {
      obj(
        "aggregate" to rentalsCollection,
        "pipeline" to array(
          obj(
            "\$match" to obj(
              "customerId" to customerId
            )
          ),
          obj(
            "\$lookup" to obj(
              "from" to moviesCollection,
              "localField" to "movieId",
              "foreignField" to "_id",
              "as" to "movie"
            )
          ),
          obj(
            "\$sort" to obj(
              "rentedAt" to -1
            )
          )
        ),
        "explain" to false
      )
    }
  }
}
